package classes.awt;

import classes.awt.CanvasGraphics2D;
import java.awt.Image;
import java.awt.event.ComponentListener;

public class BrowserCanvas {
    CanvasGraphics2D g;

    public BrowserCanvas() {
        init();
    }
    private native void init();
    public native void resize(int width, int height);
    public native void mount();
    public native int width();
    public native int height();

    public CanvasGraphics2D getGraphics() {
        return new CanvasGraphics2D(this);
    }
    public Image getImage() {
        if (g == null) {
            g = getGraphics();
        }
        return g.getImage();
    }

    public synchronized void addComponentListener(ComponentListener l) {}
    public synchronized void removeComponentListener(ComponentListener l) {}
    
}
