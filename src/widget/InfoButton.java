package widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.SwingUtilities;


/**
 * @author Harry Anuszewski
 */
public class InfoButton extends FadingButton {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4549296690888084589L;

	public InfoButton() {
        super();
        setSize(new Dimension(13, 13));
        setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 13));
    }

    protected Shape getShape() {
        return new Ellipse2D.Float(0, 0, getWidth() - 1, getHeight() - 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        Shape disc = getShape();

        // background
        if (getModel().isArmed()) {
            g2.setColor(new Color(1f, 1f, 1f, .2f));
            g2.fill(disc);
        }

        // symbol
        g2.setFont(getFont());
        g2.setColor(new Color(1f, 1f, 1f, alpha));
        int iw = SwingUtilities.computeStringWidth(g2.getFontMetrics(), "i");
        g2.drawString("i", (getWidth() / 2) - (iw / 2),
                      (getHeight() / 2) + g2.getFontMetrics().getHeight() / 2 -
                      4);
        g2.dispose();
    }
}
