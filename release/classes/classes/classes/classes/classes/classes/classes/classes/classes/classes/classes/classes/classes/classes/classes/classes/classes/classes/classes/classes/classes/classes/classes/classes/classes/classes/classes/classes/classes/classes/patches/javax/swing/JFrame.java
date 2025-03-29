package javax.swing;


import java.awt.Image;
import java.awt.Component;
import java.awt.Canvas;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.event.WindowListener;
import java.awt.event.ComponentListener;
import java.awt.Dimension;
// import java.awt.Container;
// import java.awt.Frame;

// import javax.accessibility.Accessible;
// import javax.accessibility.AccessibleContext;
// import javax.swing.JLayeredPane;
// import javax.swing.JRootPane;
// import javax.swing.RootPaneContainer;
// import javax.swing.TransferHandler;
// import javax.swing.WindowConstants;

public class JFrame extends Component {
    public JFrame(String title) {
        this.title = title;
    }

    Canvas canvas;
    boolean visible = false;

    String title = "";

    public Component add(Component comp) {
        if (comp instanceof Canvas) {
            if (canvas != null) {
                throw new UnsupportedOperationException("only one canvas supported in a JFrame");
            }
            canvas = (Canvas) comp;
            if (visible) {
                canvas.setVisible(true);
                canvas.repaint();
            }

        }

        return comp;
    }

    
    public static final int EXIT_ON_CLOSE = 3;

    public void setDefaultCloseOperation(int op) {}
    public void pack() {}
    public void setLocationRelativeTo(Component comp) {}
    public Dimension getSize() {
        return new Dimension();
    }
    public void setSize(Dimension d) {}
    public void setLocation(int x, int y) {}
    public Point getLocation() {
        return new Point();
    }
    public void setResizable(boolean r) {}
    public void setVisible(boolean visible) {
        if (canvas == null) {
            return;
        }
        this.visible = visible;
        canvas.setVisible(visible);
        if (visible) {
            canvas.repaint();
        }
    }
    String getTitle() {
        return title;
    }
    void setTitle(String title) {
        this.title = title;
    }

    public void setIconImage(Image image) {
    }

    public synchronized void addWindowListener(WindowListener l) {}
    public synchronized void removeWindowListener(WindowListener l) {}

    public synchronized void addComponentListener(ComponentListener l) {}
    public synchronized void removeComponentListener(ComponentListener l) {}
}

