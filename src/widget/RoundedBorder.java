package widget;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;


/**
 * @author Harry Anuszewski
 */
public class RoundedBorder extends AbstractBorder {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1987711553515418679L;
	private Insets insets;
    private Stroke stroke;
    private Color strokeColor;
    private int arc;
    private float strokeWidth;

    /**
     * Simple rounded border with no outline
     * @param arc
     */
    public RoundedBorder(int arc) {
        this.arc = arc;
        int i = (int)(arc / Math.PI) / 2;
        insets = new Insets(i + 20, i + 5, i + 5, i);
    }

    /**
     * Rounded border with an outline
     * @param arc
     * @param strokeWidth width of the outline
     * @param color color of the outline
     */
    public RoundedBorder(int arc, float strokeWidth, Color color) {
        this.arc = arc;
        int i = (int)((arc / Math.PI) + ((strokeWidth * 2) / (Math.PI)));
        insets = new Insets(i, i, i, i);
        this.stroke = new BasicStroke(strokeWidth);
        this.strokeColor = color;
        this.strokeWidth = strokeWidth;
    }

    public Shape getShape(Component c) {
        RoundRectangle2D.Float rect = null;
        if (stroke != null) {
            int i = (int)strokeWidth / 2;
            rect =
new RoundRectangle2D.Float(i, i, c.getWidth() - strokeWidth - 1,
                           c.getHeight() - strokeWidth - 1, arc, arc);
        } else {
            rect =
new RoundRectangle2D.Float(0, 0, c.getWidth() - 1, c.getHeight() - 1, arc,
                           arc);
        }

        return rect;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width,
                            int height) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(x, y, width, height);
        g2.setComposite(AlphaComposite.SrcOver);

        // In real code optimize by preserving the rect between calls
        if (stroke != null) {
            Shape s = getShape(c);
            g2.translate(x, y);
            g2.setColor(c.getBackground());
            g2.fill(s);

            g2.setColor(strokeColor);
            g2.setStroke(stroke);
            g2.draw(s);
        } else {
            Shape s = getShape(c);
            g2.translate(x, y);
            g2.setColor(c.getBackground());
            g2.fill(s);
        }
        g2.dispose();
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
