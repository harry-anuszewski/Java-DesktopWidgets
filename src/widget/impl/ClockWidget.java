package widget.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import widget.RoundedBorder;


/**
 * @author Harry Anuszewski
 */
public class ClockWidget extends PlainWidget {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2184970450693059003L;
	private static DateFormat dateFormat24 = new SimpleDateFormat("kk:mm");
    private static DateFormat dateFormat12 = new SimpleDateFormat("hh:mm");
    private static DateFormat df = dateFormat12;

    private ClockWidget() {
        super();
    }

    public Dimension getPreferredSize() {
        return new Dimension(220, 130);
    }

    @Override
    protected JComponent createFront() {
        JPanel panel = new GlassLookPanel(30);
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 70));
        panel.setOpaque(false);
        panel.setBackground(new Color(0, 0, 0));
        panel.setBorder(new RoundedBorder(20, 2f, Color.LIGHT_GRAY));
        panel.add(new ClockPanel());
        return panel;
    }

    @Override
    protected JComponent createBack() {
        JComponent back = super.createBack();
        back.setPreferredSize(new Dimension(200, 70));
        ButtonGroup bg = new ButtonGroup();
        JRadioButton rb12 = ComponentFactory.createRadioButton("12 hour", bg);
        JRadioButton rb24 = ComponentFactory.createRadioButton("24 hour", bg);
        rb12.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    df = dateFormat12;
                }
            });
        rb24.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    df = dateFormat24;
                }
            });
        JPanel buttonPanel = new JPanel(new FlowLayout(10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(rb12);
        buttonPanel.add(rb24);
        back.add(buttonPanel, BorderLayout.NORTH);
        return back;
    }

    protected Point getInfoButtonLocation() {
        Point p = super.getInfoButtonLocation();
        p.translate(-8, 2);
        return p;
    }

    protected Point getCloseButtonLocation() {
        return new Point(0, 18);
    }

    private class ClockPanel extends JComponent {
        /**
		 * 
		 */
		private static final long serialVersionUID = 790810631171979507L;
		private Font f = new Font("Arial", Font.BOLD, 65);
        private Font amf = new Font("Arial", Font.BOLD, 12);
        private DateFormat dateFormat12am = new SimpleDateFormat("a");
        private Timer t;

        private ClockPanel() {
            t = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        widgetComp.repaint();
                    }
                });
            t.setRepeats(true);
            t.start();
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(f);
            g2.setColor(Color.WHITE);
            g2.drawString(df.format(new Date()), 0, 51);

            if (df == dateFormat12) {
                g2.setFont(amf);
                g2.drawString(dateFormat12am.format(new Date()),
                              getWidth() - 18, 16);
            }
        }
    }

    public static void main(String[] args) {
        new ClockWidget();
    }
}
