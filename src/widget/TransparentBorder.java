package widget;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;


/**
 * @author Harry Anuszewski
 */
public class TransparentBorder extends AbstractBorder {
    /**
	 * 
	 */
	private static final long serialVersionUID = -96678683360070357L;
	private Insets insets;

    public TransparentBorder(int top, int left, int bottom, int right) {
        insets = new Insets(top, left, bottom, right);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width,
                            int height) {
        Graphics2D g2 = (Graphics2D)g.create();

        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(x, y, width, height);
        g2.setComposite(AlphaComposite.SrcOver);
    }

    public Insets getBorderInsets(Component c) {
        return insets;
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        return insets;
    }

    public Rectangle getInteriorRectangle(Component c, int x, int y, int width,
                                          int height) {
        return getInteriorRectangle(c, this, x, y, width, height);
    }

    public static Rectangle getInteriorRectangle(Component c, Border b, int x,
                                                 int y, int width,
                                                 int height) {
        Insets insets;
        if (b != null)
            insets = b.getBorderInsets(c);
        else
            insets = new Insets(0, 0, 0, 0);
        return new Rectangle(x + insets.left, y + insets.top,
                             width - insets.right - insets.left,
                             height - insets.top - insets.bottom);
    }
}

