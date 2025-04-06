let fsReady = false;
let doppioReady = false;

function initFS() {
  BrowserFS.install(window);
  const mfs = new BrowserFS.FileSystem.MountableFileSystem();
  BrowserFS.initialize(mfs);

  mfs.mount("/tmp", new BrowserFS.FileSystem.InMemory());
  mfs.mount("/release", new BrowserFS.FileSystem.InMemory());

  const fs = BrowserFS.BFSRequire("fs");

  try {
    createDirectoryIfNotExists(fs, "/tmp");
    createDirectoryIfNotExists(fs, "/release/vendor");
    createDirectoryIfNotExists(fs, "/release/vendor/java_home");
    createDirectoryIfNotExists(fs, "/release/vendor/java_home/lib");
    createDirectoryIfNotExists(fs, "/release/vendor/natives");

    copyJavaHomeToBrowserFS(fs)
      .then(() => {
        fs.writeFileSync(
          "/tmp/Main.java",
          document.getElementById("code").value
        );
        console.log("Wrote Main.java to /tmp");

        const mainJavaContent = fs.readFileSync("/tmp/Main.java", "utf8");
        console.log("Main.java content:", mainJavaContent);

        fsReady = true;
        checkAllReady();
        document.getElementById("status").textContent =
          "File system initialized";
      })
      .catch((err) => {
        console.error("Error copying files:", err);
        document.getElementById("status").textContent =
          "Error initializing: " + err.message;
      });
  } catch (e) {
    console.error("Error setting up FS:", e);
    document.getElementById("status").textContent = "Error in FS: " + e.message;
  }
}

function createDirectoryIfNotExists(fs, path) {
  try {
    fs.mkdirSync(path);
    console.log(`Created directory: ${path}`);
  } catch (e) {
    if (e.code !== "EEXIST") throw e;
  }
}

const essentialFiles = ["lib/doppio.jar", "lib/rt.jar", "lib/tools.jar"];

const optionalFiles = ["lib/charsets.jar", "lib/jce.jar", "lib/currency.data"];

async function copyJavaHomeToBrowserFS(fs) {
  let totalSize = 0;
  let fileCount = 0;

  for (const file of essentialFiles) {
    const localFilePath = `release/vendor/java_home/${file}`;
    const virtualFilePath = `/release/vendor/java_home/${file}`;

    console.log(`Attempting to fetch ${localFilePath}`);
    try {
      const response = await fetch(localFilePath);
      if (!response.ok) {
        throw new Error(
          `Failed to fetch ${localFilePath}: ${response.status} ${response.statusText}`
        );
      }
      const buffer = await response.arrayBuffer();
      if (buffer.byteLength === 0) {
        throw new Error(`Fetched ${localFilePath} but the file is empty`);
      }

      const dirPath = virtualFilePath.substring(
        0,
        virtualFilePath.lastIndexOf("/")
      );
      createDirectoryIfNotExists(fs, dirPath);
      fs.writeFileSync(virtualFilePath, Buffer.from(buffer));

      const stats = fs.statSync(virtualFilePath);
      const fileSize = stats.size;
      totalSize += fileSize;
      fileCount++;

      console.log(`Copied ${localFilePath} to ${virtualFilePath}`);
      console.log(`File ${virtualFilePath} size: ${fileSize} bytes`);
    } catch (err) {
      console.error(`Error copying ${localFilePath}:`, err);
      throw err;
    }
  }

  // Return early for basic functionality
  document.getElementById("status").textContent =
    "Basic runtime loaded. Optional components will load when needed.";

  // Load optional files in the background
  setTimeout(() => {
    loadOptionalFiles(fs);
  }, 3000);

  return totalSize;
}

async function loadOptionalFiles(fs) {
  let totalSize = 0;
  let fileCount = 0;

  for (const file of optionalFiles) {
    const localFilePath = `release/vendor/java_home/${file}`;
    const virtualFilePath = `/release/vendor/java_home/${file}`;

    console.log(`Attempting to fetch ${localFilePath}`);
    try {
      const response = await fetch(localFilePath);
      if (!response.ok) {
        throw new Error(
          `Failed to fetch ${localFilePath}: ${response.status} ${response.statusText}`
        );
      }
      const buffer = await response.arrayBuffer();
      if (buffer.byteLength === 0) {
        throw new Error(`Fetched ${localFilePath} but the file is empty`);
      }

      const dirPath = virtualFilePath.substring(
        0,
        virtualFilePath.lastIndexOf("/")
      );
      createDirectoryIfNotExists(fs, dirPath);
      fs.writeFileSync(virtualFilePath, Buffer.from(buffer));

      const stats = fs.statSync(virtualFilePath);
      const fileSize = stats.size;
      totalSize += fileSize;
      fileCount++;

      console.log(`Copied ${localFilePath} to ${virtualFilePath}`);
      console.log(`File ${virtualFilePath} size: ${fileSize} bytes`);
    } catch (err) {
      console.error(`Error copying ${localFilePath}:`, err);
      console.log(`Continuing with remaining files...`);
    }
  }

  if (fileCount > 0) {
    const totalSizeMB = (totalSize / (1024 * 1024)).toFixed(2);
    console.log(`Additional ${fileCount} files loaded (${totalSizeMB} MB)`);
    document.getElementById("status").textContent = "Full runtime loaded.";
  } else {
    console.log("No additional files were loaded successfully");
  }
}

function loadDoppio() {
  if (typeof window.Doppio === "undefined") {
    console.error("Doppio not loaded. Check doppio.js");
    document.getElementById("status").textContent = "Error: Doppio not loaded";
    return;
  }
  console.log("Doppio loaded successfully");
  doppioReady = true;
  checkAllReady();
}

function checkAllReady() {
  if (fsReady && doppioReady) {
    document.getElementById("status").textContent = "Ready to run Java code";
    document.getElementById("runButton").disabled = false;
  }
}

function setupConsole() {
  const consoleDiv = document.getElementById("console");
  const process = BrowserFS.BFSRequire("process");
  process.initializeTTYs();

  process.stdout.on(
    "data",
    (data) => (consoleDiv.textContent += data.toString())
  );
  process.stderr.on(
    "data",
    (data) => (consoleDiv.textContent += "Error: " + data.toString())
  );
}

function moveClassFile(fs, from, to) {
  try {
    const classContent = fs.readFileSync(from);
    fs.writeFileSync(to, classContent);
    console.log(`Moved class file from ${from} to ${to}`);

    // Verify the file
    const stats = fs.statSync(to);
    console.log(`File ${to} size: ${stats.size} bytes`);
    return true;
  } catch (e) {
    console.error(`Error moving class file: ${e.message}`);
    return false;
  }
}

function runJavaCode() {
  const consoleDiv = document.getElementById("console");
  const code = document.getElementById("code").value;

  consoleDiv.textContent = "";
  document.getElementById("runButton").disabled = true;
  document.getElementById("status").textContent = "Compiling and running...";

  // Extract the class name from the code
  const classNameMatch = code.match(/public\s+class\s+(\w+)/);
  const className = classNameMatch ? classNameMatch[1] : "Main";
  const fileName = `${className}.java`;
  const filePath = `/tmp/${fileName}`;

  const fs = BrowserFS.BFSRequire("fs");
  try {
    // Write the code to the appropriate file name
    fs.writeFileSync(filePath, code);
    console.log(`Wrote ${fileName} to /tmp`);

    const javaFileContent = fs.readFileSync(filePath, "utf8");
    console.log(`${fileName} content before compilation:`, javaFileContent);

    // Check if tools.jar exists for compilation
    const toolsJarExists = fs.existsSync(
      "/release/vendor/java_home/lib/tools.jar"
    );
    console.log("tools.jar exists:", toolsJarExists);

    // Include all essential jars in the classpath
    const classpath = [
      "/release/vendor/java_home/lib/doppio.jar",
      "/release/vendor/java_home/lib/rt.jar",
      "/release/vendor/java_home/lib/tools.jar",
    ].join(":");

    console.log("Using classpath for compilation:", classpath);

    // Add a delay to make sure filesystem operations are complete
    setTimeout(() => {
      Doppio.VM.CLI(
        ["-classpath", classpath, "com.sun.tools.javac.Main", filePath],
        {
          doppioHomePath: "/release",
          javaHomePath: "/release/vendor/java_home",
          nativeClasspath: ["/release/vendor/natives"],
        },
        function (exitCode) {
          if (exitCode === 0) {
            console.log("Compilation successful");
            document.getElementById("status").textContent =
              "Compilation successful, running...";

            try {
              // Check if className.class exists and log its location
              const classFileExists = fs.existsSync(`/tmp/${className}.class`);
              console.log(
                `${className}.class exists in /tmp:`,
                classFileExists
              );

              if (classFileExists) {
                const classFileStats = fs.statSync(`/tmp/${className}.class`);
                console.log(
                  `${className}.class size:`,
                  classFileStats.size,
                  "bytes"
                );

                // Create copy of the class in root directory to increase visibility
                fs.writeFileSync(
                  `/${className}.class`,
                  fs.readFileSync(`/tmp/${className}.class`)
                );
                console.log(`Copied ${className}.class to root directory`);
              }
            } catch (e) {
              console.error(`Error checking ${className}.class:`, e);
            }

            // Add /tmp and root directory to classpath for maximum visibility
            Doppio.VM.CLI(
              ["-classpath", "/tmp:/", className],
              {
                doppioHomePath: "/release",
                javaHomePath: "/release/vendor/java_home",
                nativeClasspath: ["/release/vendor/natives"],
              },
              function (runExitCode) {
                document.getElementById("runButton").disabled = false;
                if (runExitCode === 0) {
                  document.getElementById("status").textContent =
                    "Execution completed successfully";
                } else {
                  document.getElementById("status").textContent =
                    "Execution failed with exit code: " + runExitCode;
                }
              }
            );
          } else {
            console.error("Compilation failed with exit code:", exitCode);
            document.getElementById("status").textContent =
              "Compilation failed with exit code: " + exitCode;
            document.getElementById("runButton").disabled = false;
          }
        }
      );
    }, 500); // Half-second delay to ensure filesystem operations complete
  } catch (e) {
    consoleDiv.textContent += "Error: " + e.message + "\n";
    document.getElementById("status").textContent =
      "Error running code: " + e.message;
    document.getElementById("runButton").disabled = false;
    console.error("Run error:", e);
  }
}

document.addEventListener("DOMContentLoaded", function () {
  document.getElementById("runButton").addEventListener("click", runJavaCode);
  document.getElementById("clearButton").addEventListener("click", function () {
    document.getElementById("console").textContent = "";
  });

  initFS();
  setupConsole();
  loadDoppio();
});
