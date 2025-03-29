package classes.awt;

import java.awt.Font;
import java.awt.FontMetrics;

public class BrowserFontMetrics extends FontMetrics {
    int[] widths = null;
    public BrowserFontMetrics(Font font) {
        super(font);
        init();
    }
    private native void init();

    public native int getAscent();
    public native int getDescent();
    
    public native int stringWidth(String str);
    
    public int[] getWidths() {
        if (widths != null) {
            return widths;
        }

        widths = new int[256];
        for (char ch = 0; ch < 256; ch++) {
            widths[ch] = stringWidth(new String(new char[]{ch}));
        }
        return widths;
    }
}