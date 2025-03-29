package classes.awt;

import java.awt.BufferCapabilities;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ImageCapabilities;
import java.awt.image.BufferStrategy;
import java.awt.Image;

public class CanvasBufferStrategy extends BufferStrategy {

    private BrowserCanvas browserCanvas;
    private Graphics2D target;

    public CanvasBufferStrategy(Graphics2D target) {
        this.target = target;
        browserCanvas = new BrowserCanvas();
    }

    public void resize(int width, int height) {
        browserCanvas.resize(width, height);
    }

    @Override
    public boolean contentsLost() {
        return false;
    }

    @Override
    public boolean contentsRestored() {
        return false;
    }

    @Override
    public BufferCapabilities getCapabilities() {
        ImageCapabilities caps = new ImageCapabilities(true);
        return new BufferCapabilities(caps, caps, null);
    }

    @Override
    public Graphics getDrawGraphics() {
        return browserCanvas.getGraphics();
    }

    @Override
    public void show() {
        Image img = browserCanvas.getImage();
        target.drawImage(img, 0, 0, null);
    }
    
}
