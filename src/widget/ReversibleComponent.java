package widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;


/**
 * @author Harry Anuszewski
 */
public class ReversibleComponent extends JLayeredPane {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8871841501114683639L;
	private JComponent frontComp = new JPanel(new BorderLayout(), false);
    private JComponent backComp = new JPanel(new BorderLayout(), false);

    public static String FLIP_PROPERTY = "flip-property";

    public static enum FLIP_STATE {
        FRONT,
        BACK,
        FLIPPING;
    }

    public ReversibleComponent() {
        add(frontComp);
        add(backComp);
        backComp.setVisible(false);
        setOpaque(false);
        frontComp.setOpaque(false);
        backComp.setOpaque(false);

        frontComp.addComponentListener(new ComponentAdapter() {
                public void componentShown(ComponentEvent e) {
                    firePropertyChange(FLIP_PROPERTY, FLIP_STATE.FLIPPING,
                                       FLIP_STATE.FRONT);
                }

                public void componentHidden(ComponentEvent e) {
                    firePropertyChange(FLIP_PROPERTY, FLIP_STATE.FRONT,
                                       FLIP_STATE.FLIPPING);
                }
            });

        backComp.addComponentListener(new ComponentAdapter() {
                public void componentShown(ComponentEvent e) {
                    firePropertyChange(FLIP_PROPERTY, FLIP_STATE.FLIPPING,
                                       FLIP_STATE.BACK);
                }

                public void componentHidden(ComponentEvent e) {
                    firePropertyChange(FLIP_PROPERTY, FLIP_STATE.BACK,
                                       FLIP_STATE.FLIPPING);
                }
            });
    }

    public void setFront(JComponent comp) {
        frontComp.removeAll();
        frontComp.add(comp);
    }

    public void setBack(JComponent comp) {
        backComp.removeAll();
        backComp.add(comp);
    }

    public JComponent getFront() {
        return frontComp;
    }

    public JComponent getBack() {
        return backComp;
    }

    public boolean isShowingFront() {
        return frontComp.isVisible();
    }

    public boolean isShowingBack() {
        return backComp.isVisible();
    }

    public JComponent getShowingComponent() {
        return (isShowingFront()) ? frontComp :
               (isShowingBack()) ? backComp : null;
    }

    public void flip() {
        FlipTransition flipper = null;
        ;

        if (frontComp.isVisible())
            flipper = new FlipTransition(frontComp, backComp, this);
        else
            flipper = new FlipTransition(backComp, frontComp, this);

        flipper.doTransition();
    }

    public Dimension getPreferredSize() {
        Dimension fd = frontComp.getPreferredSize();
        Dimension bd = backComp.getPreferredSize();
        Dimension d =
            new Dimension(Math.max(fd.width, bd.width), Math.max(fd.height,
                                                                 bd.height));
        return d;
    }

    @Override
    public void doLayout() {
        Rectangle r = getBounds();
        Dimension fd = frontComp.getPreferredSize();
        Dimension bd = backComp.getPreferredSize();
        Dimension d =
            new Dimension(Math.max(fd.width, bd.width), Math.max(fd.height,
                                                                 bd.height));

        frontComp.setBounds((r.width / 2) - (d.width / 2),
                            (r.height / 2) - (d.height / 2), d.width,
                            d.height);

        backComp.setBounds((r.width / 2) - (d.width / 2),
                           (r.height / 2) - (d.height / 2), d.width, d.height);

    }
}
