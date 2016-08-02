package widget.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JRadioButton;


/**
 * @author Harry Anuszewski
 */
class ComponentFactory {
    protected static JRadioButton createRadioButton(String text,
                                                    ButtonGroup bg) {
        JRadioButton rb = new JRadioButton(text, true);
        rb.setOpaque(false);
        rb.setForeground(Color.WHITE);
        rb.setIcon(RadioButtonIcon.INSTANCE);
        rb.setFocusPainted(false);
        bg.add(rb);
        return rb;
    }

    private static class RadioButtonIcon implements Icon {
        private static RadioButtonIcon INSTANCE = new RadioButtonIcon();

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(x, y, getIconWidth() - 1, getIconHeight() - 1);

            JRadioButton rb = (JRadioButton)c;
            if (rb.getModel().isSelected())
                g2.fillOval(x, y, getIconWidth() - 1, getIconHeight() - 1);
        }

        public int getIconWidth() {
            return 11;
        }

        public int getIconHeight() {
            return 11;
        }
    }
}
