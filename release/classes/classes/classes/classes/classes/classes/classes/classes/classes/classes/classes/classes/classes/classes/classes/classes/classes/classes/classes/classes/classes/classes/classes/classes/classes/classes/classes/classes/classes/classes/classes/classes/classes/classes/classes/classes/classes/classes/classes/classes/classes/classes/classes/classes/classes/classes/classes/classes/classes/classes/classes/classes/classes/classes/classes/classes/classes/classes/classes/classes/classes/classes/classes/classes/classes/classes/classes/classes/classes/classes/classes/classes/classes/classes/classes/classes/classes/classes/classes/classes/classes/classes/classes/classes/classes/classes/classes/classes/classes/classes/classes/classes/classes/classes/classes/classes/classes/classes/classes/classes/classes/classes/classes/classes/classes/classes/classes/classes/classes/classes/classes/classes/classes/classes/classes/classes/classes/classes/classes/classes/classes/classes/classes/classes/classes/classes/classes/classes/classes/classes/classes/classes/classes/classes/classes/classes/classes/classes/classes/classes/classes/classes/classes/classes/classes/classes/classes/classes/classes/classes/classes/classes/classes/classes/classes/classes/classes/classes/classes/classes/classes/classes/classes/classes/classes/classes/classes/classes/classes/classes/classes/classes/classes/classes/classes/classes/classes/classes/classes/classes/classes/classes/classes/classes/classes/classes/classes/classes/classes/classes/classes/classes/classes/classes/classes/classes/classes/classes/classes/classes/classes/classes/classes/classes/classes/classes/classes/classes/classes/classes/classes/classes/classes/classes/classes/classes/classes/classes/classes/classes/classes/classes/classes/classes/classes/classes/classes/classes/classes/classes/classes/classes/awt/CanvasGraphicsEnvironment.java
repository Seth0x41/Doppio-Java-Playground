package classes.awt;

import java.awt.*;
import java.awt.image.BufferedImage;

import classes.awt.BrowserCanvas;
import sun.awt.*;
import sun.java2d.*;

public class CanvasGraphicsEnvironment extends SunGraphicsEnvironment {
    public native GraphicsDevice[] getScreenDevices()
        throws HeadlessException;

    public native GraphicsDevice getDefaultScreenDevice()
        throws HeadlessException;

    public Graphics2D createGraphics(BufferedImage img) {
        // TODO this really shouldn't create two separate canvases if this is called twice. but whatever
        BrowserCanvas canvas = new BrowserCanvas();
        canvas.resize(img.getWidth(), img.getHeight());
        CanvasGraphics2D g = canvas.getGraphics();
        // make sure the canvas has this image
        g.drawImage(img, 0, 0, null);

        g.postDrawSync = new Runnable() {
            public void run() {
                BufferedImage result = (BufferedImage) g.getImage();
                img.setData(result.getRaster());
            }
        };
        return g;
    }

    public native Font[] getAllFonts();

    public native String[] getAvailableFontFamilyNames();

    protected int getNumScreens() { return 1; }
    protected native GraphicsDevice makeScreenDevice(int screennum);
    protected native FontConfiguration createFontConfiguration();

    public FontConfiguration createFontConfiguration(boolean preferLocaleFonts,
                                                     boolean preferPropFonts) {
      // ignore arguments for simplicity
      return createFontConfiguration();
    }

    public boolean isDisplayLocal() { return true; }
}
