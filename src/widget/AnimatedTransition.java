package widget;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 * @author Harry Anuszewski
 */
public abstract class AnimatedTransition {

    private JComponent startComponent;
    private JComponent endComponent;
    private JLayeredPane layeredPane;
    private RenderComponent renderComp;

    private Insets startInsets;
    private Insets endInsets;

    protected BufferedImage startImage;
    protected BufferedImage endImage;
    protected BufferedImage interImage;

    private transient ArrayList propertyChangeListeners = new ArrayList(2);

    public String STATE_PROP = "state";

    public enum STATE {
        RUNNING,
        COMPLETE;
    }

    public AnimatedTransition(JComponent startComponent,
                              JComponent endComponent,
                              JLayeredPane layeredPane) {
        this.startComponent = startComponent;
        this.endComponent = endComponent;
        this.layeredPane = layeredPane;
        renderComp = new RenderComponent();
    }

    public void doTransition() {
        firePropertyChange(new PropertyChangeEvent(this, STATE_PROP, null,
                                                   STATE.RUNNING));
        layeredPane.add(renderComp, JLayeredPane.POPUP_LAYER);

        doSizing();

        startImage = createStartImage(startComponent, startInsets);
        endImage = createEndImage(endComponent, endInsets);
        interImage = createInterImage();

        updateInterImage();
        startComponent.setVisible(false);

        Timer t = new Timer(12, renderComp);
        t.setCoalesce(true);
        t.start();
    }

    protected BufferedImage createStartImage(JComponent startComp,
                                             Insets insets) {
        return grabImage(startComp, insets);
    }

    protected BufferedImage createEndImage(JComponent endComp, Insets insets) {
        return grabImage(endComp, insets);
    }

    protected BufferedImage createInterImage() {
        return new BufferedImage(startImage.getWidth(), startImage.getHeight(),
                                 BufferedImage.TYPE_INT_ARGB);
    }

    private void completeFlip() {
        interImage = endImage;
        endComponent.setVisible(true);
        layeredPane.remove(renderComp);
        firePropertyChange(new PropertyChangeEvent(this, STATE_PROP,
                                                   STATE.RUNNING,
                                                   STATE.COMPLETE));
    }

    private void doSizing() {
        int maxWidth =
            Math.max(startComponent.getWidth(), endComponent.getWidth());
        int maxHeight =
            Math.max(startComponent.getHeight(), endComponent.getHeight());

        int fhd = (maxHeight - startComponent.getHeight()) / 2;
        int fwd = (maxWidth - startComponent.getWidth()) / 2;
        startInsets = new Insets(fhd, fwd, fhd, fwd);

        int bhd = (maxHeight - endComponent.getHeight()) / 2;
        int bwd = (maxWidth - endComponent.getWidth()) / 2;
        endInsets = new Insets(bhd, bwd, bhd, bwd);

        Point p =
            (startComponent.isShowing()) ? startComponent.getLocationOnScreen() :
            endComponent.getLocationOnScreen();
        p.x = p.x - startInsets.left;
        p.y = p.y - startInsets.top;
        SwingUtilities.convertPointFromScreen(p, layeredPane);

        renderComp.setBounds(p.x, p.y, maxWidth, maxHeight);
        //renderComp.setPreferredSize(maxWidth, maxHeight);
    }

    protected BufferedImage grabImage(JComponent comp, Insets insets) {
        BufferedImage image =
            new BufferedImage(comp.getWidth(), comp.getHeight() + insets.top +
                              insets.bottom, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();
        Composite composite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        Rectangle2D.Double rect =
            new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight());
        g.fill(rect);
        g.setComposite(composite);

        g.translate(insets.left, insets.top);
        comp.paint(g);
        g.translate(insets.left, insets.top);
        g.dispose();
        return image;
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeListeners == null) {
            propertyChangeListeners = new ArrayList(2);
        }
        propertyChangeListeners.add(listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeListeners != null) {
            propertyChangeListeners.remove(listener);
        }
    }

    private void firePropertyChange(PropertyChangeEvent propertyChangeEvent) {
        java.util.List list;
        synchronized (this) {
            if (propertyChangeListeners == null) {
                return;
            }
            list = (java.util.List)propertyChangeListeners.clone();
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ((PropertyChangeListener)list.get(i)).propertyChange(propertyChangeEvent);
        }
    }


    /**
     * Update the interImage for the current frame
     */
    protected abstract void updateInterImage();

    /**
     * Render the image to the components graphics
     * @param g2
     * @param width
     * @param height
     */
    protected abstract void paintInterImage(Graphics2D g2, int width,
                                            int height);

    /**
     * The animation is moving forward another frame
     * @return true if the transition is complete
     */
    protected abstract boolean timerTick();

    /**
     * The class where the intermediate images are rendered to. It is added to
     * the layer pane, the animation is triggered and this component repaints
     * the animation frames. The component is then removed after animation stops
     * and the reversed side is displayed.
     */
    private class RenderComponent extends JComponent implements ActionListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1334742132714803972L;

		RenderComponent() {
            setOpaque(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (timerTick()) {
                Timer t = (Timer)e.getSource();
                t.stop();
                completeFlip();
            } else {
                updateInterImage();
                repaint();
            }
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            clearBackgroundForOSX(g2);
            paintInterImage(g2, getWidth(), getHeight());
        }

        private void clearBackgroundForOSX(Graphics2D g2) {
            Composite composite = g2.getComposite();
            g2.setColor(new Color(0, 100, 100));
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(composite);
        }
    }
}
