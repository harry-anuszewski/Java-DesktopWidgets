package widget.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import widget.DoneButton;
import widget.RoundedBorder;
import widget.Widget;


/**
 * @author Harry Anuszewski
 */
public class PlainWidget extends Widget {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5605717658024884741L;

	protected JComponent createFront() {
        JPanel panel = new GlassLookPanel(60);
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(160, 160));
        panel.setOpaque(false);
        Color color = new Color(20, 50, 110);
        panel.setBackground(color);
        panel.setBorder(new RoundedBorder(20, 2f,
                                          color.brighter().brighter()));

        return panel;
    }

    protected JComponent createBack() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(160, 160));
        panel.setOpaque(false);
        Color color = new Color(60, 60, 60);
        panel.setBackground(color);
        Border border = new RoundedBorder(20, 2f, color.brighter().brighter());
        panel.setBorder(border);

        JPanel donePanel = new JPanel(new BorderLayout());
        donePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        donePanel.setOpaque(false);
        donePanel.add(new DoneButton(getRevesiblecomponent()),
                      BorderLayout.EAST);
        panel.add(donePanel, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        new PlainWidget();
    }
}
