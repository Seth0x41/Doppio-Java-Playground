package classes.awt;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.Image;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import classes.awt.BrowserCanvas;

public class CanvasGraphics2D extends Graphics2D {
    private Paint paint = Color.BLACK;
    private Font font = null;

    private AffineTransform tf = new AffineTransform();
    private BrowserCanvas canvas;

    public Runnable postDrawSync;

    public CanvasGraphics2D(BrowserCanvas canvas) {
        this.canvas = canvas;
        init();
        setFont(new Font("sans-serif", 0, 14));
    }
    private native void init();

    private void sync() {
        if (postDrawSync != null) {
            postDrawSync.run();
        }
    }

    private native void beginPath();
    private native void moveTo(double x, double y);
    private native void lineTo(double x, double y);
    private native void quadTo(double a, double b, double c, double d);
    private native void cubicTo(double a, double b, double c, double d, double e, double f);
    private native void doStroke();
    private native void doFill();

    private native void syncTransform();

    private void traceShape(Shape s) {
        PathIterator iter = s.getPathIterator(null);
        double[] coords = new double[6];
        double startX = 0.0, startY = 0.0;
        while (!iter.isDone()) {
            int kind = iter.currentSegment(coords);
            switch(kind) {
                case PathIterator.SEG_MOVETO:
                    startX = coords[0];
                    startY = coords[1];
                    moveTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    lineTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    lineTo(startX, startY);
                    break;
                default:
                    throw new Error("unexpected path type " + kind);
            }
            iter.next();
        }
    }

    public Image getImage() {
        int width = this.canvas.width(), height = this.canvas.height();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        writeARGB(img);
        return img;
    }

    private native void writeARGB(BufferedImage img);

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addRenderingHints'");
    }

    @Override
    public void clip(Shape s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clip'");
    }

    @Override
    public void draw(Shape s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'draw'");
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawGlyphVector'");
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        AffineTransform origTf = new AffineTransform(tf);
        transform(xform);

        boolean res = drawImage(img, 0, 0, null);

        setTransform(origTf);
        return res;
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawImage'");
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawRenderableImage'");
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawRenderedImage'");
    }

    @Override
    public void drawString(String str, int x, int y) {
        drawString(str, (float) x, (float) y);
    }

    @Override
    public native void drawString(String str, float x, float y);

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawString'");
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawString'");
    }

    @Override
    public void fill(Shape s) {
        beginPath();
        traceShape(s);
        doFill();
    }

    @Override
    public Color getBackground() {
        // FIXME
        return Color.BLACK;
    }

    @Override
    public Composite getComposite() {
        // FIXME
        return null;
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDeviceConfiguration'");
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFontRenderContext'");
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        return null;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return new RenderingHints(null);
    }

    @Override
    public Stroke getStroke() {
        // FIXME
        return null;
    }

    @Override
    public AffineTransform getTransform() {
        return new AffineTransform(tf);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hit'");
    }

    @Override
    public void rotate(double theta) {
        tf.rotate(theta);
        syncTransform();
    }

    @Override
    public void rotate(double theta, double x, double y) {
        tf.rotate(theta, x, y);
        syncTransform();
    }

    @Override
    public void scale(double sx, double sy) {
        tf.scale(sx, sy);
        syncTransform();
    }

    @Override
    public void setBackground(Color color) {
        // FIXME
    }

    @Override
    public void setComposite(Composite comp) {
        // FIXME
    }

    @Override
    public void setPaint(Paint paint) {
        if (!(paint instanceof Color)) {
            throw new UnsupportedOperationException("setPaint only supports Color");
        }
        setColor((Color) paint);
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
    }

    @Override
    public void setStroke(Stroke s) {
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        tf.setTransform(Tx);
        syncTransform();
    }

    @Override
    public void shear(double shx, double shy) {
        tf.shear(shx, shy);
        syncTransform();
    }

    @Override
    public void transform(AffineTransform Tx) {
        tf.concatenate(Tx);
        syncTransform();
    }

    @Override
    public void translate(int x, int y) {
        translate((double) x, (double) y);
    }

    @Override
    public void translate(double tx, double ty) {
        tf.translate(tx, ty);
        syncTransform();
    }

    @Override
    public native void clearRect(int x, int y, int width, int height);

    @Override
    public void clipRect(int x, int y, int width, int height) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clipRect'");
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'copyArea'");
    }

    @Override
    public Graphics create() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public void dispose() {
        // kind of a hack but sync back bufferedimages here
        if (postDrawSync != null) {
            postDrawSync.run();
        }
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawArc'");
    }

    private static int expectWidth(Image img) {
        int width = img.getWidth(null);
        if (width < 0) {
            throw new UnsupportedOperationException("img dimensions were not ready yet");
        }
        return width;
    }
    private static int expectHeight(Image img) {
        int height = img.getHeight(null);
        if (height < 0) {
            throw new UnsupportedOperationException("img dimensions were not ready yet");
        }
        return height;
    }

    static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return drawImage(img, x, y, TRANSPARENT, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return drawImage(img, x, y, expectWidth(img), expectHeight(img), bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return drawImage(img, x, y, expectWidth(img), expectHeight(img), TRANSPARENT, observer);
    }

    @Override
    public native boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer);

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
            ImageObserver observer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawImage'");
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
            Color bgcolor, ImageObserver observer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawImage'");
    }

    @Override
    public native void drawLine(int x1, int y1, int x2, int y2);

    @Override
    public native void drawOval(int x, int y, int width, int height);

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawPolygon'");
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawPolyline'");
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawRoundRect'");
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fillArc'");
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fillOval'");
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fillPolygon'");
    }

    @Override
    public native void fillRect(int x, int y, int width, int height);

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fillRoundRect'");
    }

    @Override
    public Shape getClip() {
        // FIXME
        return null;
    }

    @Override
    public Rectangle getClipBounds() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getClipBounds'");
    }

    @Override
    public Color getColor() {
        return (Color) paint;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return new BrowserFontMetrics(f);
    }

    @Override
    public void setClip(Shape clip) {
        // FIXME
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    @Override
    public void setColor(Color c) {
        paint = c;
        setColorImpl(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }
    private native void setColorImpl(int r, int g, int b, int a);

    @Override
    public void setFont(Font font) {
        this.font = font;
        setFontImpl();
    }
    private native void setFontImpl();

    @Override
    public void setPaintMode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPaintMode'");
    }

    @Override
    public void setXORMode(Color c1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setXORMode'");
    }
    
}