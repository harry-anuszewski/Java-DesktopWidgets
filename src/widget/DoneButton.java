package widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.DefaultButtonModel;
import javax.swing.SwingUtilities;


/**
 * @author Harry Anuszewski
 */
public class DoneButton extends AbstractButton {
    /**
	 * 
	 */
	private static final long serialVersionUID = 116139752387113227L;
	private RoundedBorder b = new RoundedBorder(10);
    private ReversibleComponent reversibleComp;

    public DoneButton(ReversibleComponent reversibleComp) {
        this.reversibleComp = reversibleComp;
        setText("Done");
        setOpaque(false);
        setFont(new Font("Helvetica", Font.BOLD, 11));
        setForeground(new Color(240, 240, 240, 200));
        setPreferredSize(new Dimension(38, 16));
        setModel(new DefaultButtonModel());

        addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (b.getShape(DoneButton.this).contains(e.getPoint()))
                        DoneButton.this.reversibleComp.flip();
                }

                public void mouseEntered(MouseEvent e) {
                    getModel().setRollover(true);
                }

                public void mouseExited(MouseEvent e) {
                    getModel().setRollover(false);
                }
            });
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        Shape s = b.getShape(this);

        if (getModel().isRollover())
            g2.setColor(new Color(240, 240, 240, 80));
        else
            g2.setColor(new Color(240, 240, 240, 40));
        g2.fill(s);

        g2.setColor(new Color(240, 240, 240, 200));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(s);

        g2.setColor(new Color(255, 255, 255, 255));
        int x =
            (getWidth() / 2) - (SwingUtilities.computeStringWidth(g.getFontMetrics(),
                                                                  getText()) /
                                2);
        int y = (getHeight() / 2) + (g.getFontMetrics().getHeight() / 2) - 2;
        g2.drawString(getText(), x, y);
    }
}
