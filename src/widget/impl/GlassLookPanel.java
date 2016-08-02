package widget.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


/**
 * @author Harry Anuszewski
 */
public class GlassLookPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3491310206998153882L;
	private int glassHeight;

    public GlassLookPanel(int glassHeight) {
        this.glassHeight = glassHeight;
        setForeground(Color.WHITE);
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D)g.create();

        Color c = getForeground();
        g2.setComposite(AlphaComposite.SrcAtop); //ensures we don't paint on transparent area
        GradientPaint gp =
            new GradientPaint(0, 0, new Color(c.getRed(), c.getGreen(),
                                              c.getBlue(), 140), 0,
                              glassHeight,
                              new Color(c.getRed(), c.getGreen(), c.getBlue(),
                                        0));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), glassHeight);
        g2.dispose();
    }
}
