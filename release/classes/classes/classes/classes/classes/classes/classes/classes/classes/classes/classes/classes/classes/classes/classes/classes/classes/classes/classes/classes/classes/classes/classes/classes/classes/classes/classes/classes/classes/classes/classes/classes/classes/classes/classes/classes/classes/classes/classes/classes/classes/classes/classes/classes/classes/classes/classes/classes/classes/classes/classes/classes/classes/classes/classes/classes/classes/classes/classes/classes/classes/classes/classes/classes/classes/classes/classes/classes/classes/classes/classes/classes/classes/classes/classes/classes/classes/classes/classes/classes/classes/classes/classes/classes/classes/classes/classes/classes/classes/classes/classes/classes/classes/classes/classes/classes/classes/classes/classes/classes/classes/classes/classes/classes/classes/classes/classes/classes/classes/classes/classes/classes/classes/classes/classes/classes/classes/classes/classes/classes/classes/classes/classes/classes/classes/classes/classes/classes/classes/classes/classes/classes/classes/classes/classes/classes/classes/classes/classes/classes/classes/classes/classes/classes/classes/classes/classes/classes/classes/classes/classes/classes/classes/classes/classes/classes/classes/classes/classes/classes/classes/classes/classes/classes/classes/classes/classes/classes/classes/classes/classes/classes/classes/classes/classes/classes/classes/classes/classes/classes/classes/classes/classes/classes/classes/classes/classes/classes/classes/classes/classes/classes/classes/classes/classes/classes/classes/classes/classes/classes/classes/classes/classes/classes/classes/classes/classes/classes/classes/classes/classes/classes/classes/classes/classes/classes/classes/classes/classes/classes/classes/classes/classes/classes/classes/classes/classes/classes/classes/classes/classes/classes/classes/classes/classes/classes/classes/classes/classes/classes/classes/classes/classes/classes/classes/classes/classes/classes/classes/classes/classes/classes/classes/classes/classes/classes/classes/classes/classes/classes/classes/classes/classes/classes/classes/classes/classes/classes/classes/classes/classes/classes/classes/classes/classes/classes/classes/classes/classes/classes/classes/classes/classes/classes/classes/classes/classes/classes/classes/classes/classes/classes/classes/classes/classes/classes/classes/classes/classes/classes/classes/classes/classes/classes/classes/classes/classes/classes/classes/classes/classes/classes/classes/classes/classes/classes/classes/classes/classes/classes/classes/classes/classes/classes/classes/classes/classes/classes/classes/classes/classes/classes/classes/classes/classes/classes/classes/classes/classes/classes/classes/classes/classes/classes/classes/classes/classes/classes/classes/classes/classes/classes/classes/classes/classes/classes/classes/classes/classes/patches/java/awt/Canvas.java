package java.awt;

import java.awt.image.BufferStrategy;

import classes.awt.BrowserCanvas;
import classes.awt.CanvasGraphics2D;
import classes.awt.CanvasBufferStrategy;


public class Canvas extends Component {

   private BrowserCanvas browserCanvas;
   public Canvas() {
      browserCanvas = new BrowserCanvas();
   }

   private CanvasBufferStrategy repaintStrat;
   private CanvasBufferStrategy bufferStrat;

   @Override
   public void repaint(long tm, int x, int y, int width, int height) {
      if (x != 0 || y != 0 || width != this.width || height != this.height) {
         throw new UnsupportedOperationException("only supports repainting the whole frame");
      }
      if (repaintStrat == null) {
         repaintStrat = newBufferStrategy();
      }
      Graphics g = repaintStrat.getDrawGraphics();
      update(g);
      g.dispose();
      repaintStrat.show();
   }

   public void requestFocus() {}
   public void setSize(int width, int height) {
      this.width = width;
      this.height = height;
      browserCanvas.resize(width, height);
      if (repaintStrat != null) repaintStrat.resize(width, height);
      if (bufferStrat != null) bufferStrat.resize(width, height);
   }

   public void paint(Graphics g) {
      g.clearRect(0, 0, this.width, this.height);
   }

   public void update(Graphics g) {
      g.clearRect(0, 0, this.width, this.height);
      this.paint(g);
   }


   private CanvasBufferStrategy newBufferStrategy() {
      CanvasGraphics2D g = browserCanvas.getGraphics();
      CanvasBufferStrategy strat = new CanvasBufferStrategy(g);
      strat.resize(width, height);
      return strat;
   }

   public void createBufferStrategy(int numBuffers) {
      bufferStrat = newBufferStrategy();
   }

   public BufferStrategy getBufferStrategy() {
      return bufferStrat;
   }

   public void setVisible(boolean visible) {
      if (!visible) {
         return;
      }
      browserCanvas.mount();
   }
}
