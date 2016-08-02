package widget;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;


/**
 * @author Harry Anuszewski
 */
public class CloseButton extends FadingButton {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6280453532530850261L;
	private static int INSET = 2;

    public CloseButton() {
        super();
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setSize(new Dimension(25, 25));
    }

    protected Shape getShape() {
        return new Ellipse2D.Float(INSET, INSET,
                                   getWidth() - INSET - INSET - 1,
                                   getHeight() - INSET - INSET - 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        Shape disc = getShape();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   alpha));

        // background
        g2.setColor(getBackground());
        g2.fill(disc);

        // faint outline
        g2.setColor(new Color(0, 0, 0, 80));
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,
                                     BasicStroke.JOIN_ROUND));
        g2.draw(disc);

        // edge
        g2.setColor(getForeground());
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND,
                                     BasicStroke.JOIN_ROUND));
        g2.draw(disc);

        // cross
        int i = 9;
        g2.drawLine(i, i, getWidth() - i - 1, getHeight() - i - 1);
        g2.drawLine(getWidth() - i - 1, i, i, getHeight() - i - 1);
        g2.dispose();
    }
}
