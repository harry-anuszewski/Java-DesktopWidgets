package widget;


import com.jhlabs.image.PerspectiveFilter;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 * A class to animate the visibility of two components by visually flipping
 * them so it looks like one component is on the back (flip side) of the other.
 *
 * For the animation to look the best the components should be the same size and
 * centered vertically.
 *
 * The animcation takes place on a layered pane above the actual components.
 * Call flip(...) with the the two components to flip. The flip process will start
 * the animation on the layer pane, call setVisible(false) on the first component,
 * then before the animation finishes setVisible(true) is called on the second.
 *
 * @author Harry Anuszewski
 */
public class ComponentFlipper {
    private JComponent frontComponent;
    private JComponent backComponent;
    private JLayeredPane layeredPane;
    private RenderComponent renderComp;

    private Insets frontInsets;
    private Insets backInsets;

    private BufferedImage frontImage;
    private BufferedImage backImage;

    private static final int EXTRA_SPACE = 50;

    public void flip(JComponent frontComponent, JComponent backComponent,
                     JLayeredPane layeredPane) {
        this.frontComponent = frontComponent;
        this.backComponent = backComponent;
        this.layeredPane = layeredPane;
        renderComp = new RenderComponent();
        layeredPane.add(renderComp, JLayeredPane.POPUP_LAYER);

        doSizing();

        frontImage = grabImage(frontComponent, frontInsets);
        backImage = grabImage(backComponent, backInsets);
        // flip the back horizontal
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-backImage.getWidth(null), 0);
        AffineTransformOp op =
            new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        backImage = op.filter(backImage, null);

        renderComp.interImage =
                new BufferedImage(frontImage.getWidth(), frontImage.getHeight() +
                                  (EXTRA_SPACE * 2),
                                  BufferedImage.TYPE_INT_ARGB);
        renderComp.shadedImage =
                new BufferedImage(frontImage.getWidth(), frontImage.getHeight(),
                                  BufferedImage.TYPE_INT_ARGB);
        renderComp.updateImage();
        //renderComp.paintImmediately(0,0,renderComp.getWidth(), renderComp.getHeight());
        frontComponent.setVisible(false);

        Timer t = new Timer(2, renderComp);
        t.setCoalesce(true);
        t.start();
    }

    private void completeFlip() {
        renderComp.interImage = backImage;
        backComponent.setVisible(true);
        layeredPane.remove(renderComp);
    }

    private void doSizing() {
        int maxWidth =
            Math.max(frontComponent.getWidth(), backComponent.getWidth());
        int maxHeight =
            Math.max(frontComponent.getHeight(), backComponent.getHeight()) +
            EXTRA_SPACE;

        int fhd = (maxHeight - frontComponent.getHeight()) / 2;
        int fwd = (maxWidth - frontComponent.getWidth()) / 2;
        frontInsets = new Insets(fhd, fwd, fhd, fwd);

        int bhd = (maxHeight - backComponent.getHeight()) / 2;
        int bwd = (maxWidth - backComponent.getWidth()) / 2;
        backInsets = new Insets(bhd, bwd, bhd, bwd);

        Point p =
            (frontComponent.isShowing()) ? frontComponent.getLocationOnScreen() :
            backComponent.getLocationOnScreen();
        p.x = p.x - frontInsets.left;
        p.y = p.y - frontInsets.top;
        SwingUtilities.convertPointFromScreen(p, layeredPane);

        renderComp.setBounds(p.x, p.y, maxWidth, maxHeight);
        //renderComp.setPreferredSize(maxWidth, maxHeight);
    }

    private BufferedImage grabImage(JComponent comp, Insets insets) {
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

    /**
     * The class where the intermediate images are rendered to. It is added to
     * the layer pane, the animation is triggered and this component repaints
     * the animation frames. The component is then removed after animation stops
     * and the flipped side is displayed.
     */
    private class RenderComponent extends JComponent implements ActionListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 7601932070990973747L;
		private double step = Math.PI / 80;
        private double rads = 0;
        private int x;
        private int y;

        private BufferedImage interImage;
        private BufferedImage shadedImage;

        RenderComponent() {
            setOpaque(false);
        }

        public void actionPerformed(ActionEvent e) {
            double additional =
                ((Math.PI / 2) - Math.abs((Math.PI / 2) - rads)) / 10;
            rads += step + additional;

            if (rads >= Math.PI) {
                Timer t = (Timer)e.getSource();
                t.stop();
                completeFlip();
            } else {
                updateImage();
            }
        }

        private BufferedImage getVisibleImage() {
            if (rads < Math.PI / 2)
                return frontImage;
            else
                return backImage;
        }


        public void paintComponent(Graphics g) {
            int imageX = (getWidth() / 2) - (interImage.getWidth() / 2);
            int imageY = ((getHeight() / 2) - (interImage.getHeight() / 2));

            int x0 = (interImage.getWidth() / 2) - x;
            int y0 = EXTRA_SPACE - y;

            Graphics2D g2 = (Graphics2D)g;
            g.setClip(imageX + x0, imageY + y0,
                      interImage.getWidth() - (2 * x0) - 1,
                      (interImage.getHeight() - (2 * y0)));
            if (rads < Math.PI / 2)
                g2.drawImage(interImage, imageX + x0, imageY + y0,
                             imageX + x0 + interImage.getWidth(),
                             imageY + y0 + interImage.getHeight(), 0, 0,
                             interImage.getWidth(), interImage.getHeight(),
                             null);
            else
                g2.drawImage(interImage, imageX + interImage.getWidth() - x0,
                             imageY + y0, imageX - x0,
                             imageY + y0 + interImage.getHeight(), 0, 0,
                             interImage.getWidth(), interImage.getHeight(),
                             null);
        }

        private void updateImage() {
            // clear inter image
            Graphics2D g2inter = interImage.createGraphics();
            g2inter.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,
                                                            0.0f));
            Rectangle2D.Double rect =
                new Rectangle2D.Double(0, 0, interImage.getWidth(),
                                       interImage.getHeight());
            g2inter.fill(rect);
            g2inter.dispose();

            // darken the image progresively as it spins
            BufferedImage image = getVisibleImage();
            Graphics2D g2 = shadedImage.createGraphics();
            Composite composite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,
                                                       0.0f));
            rect =
new Rectangle2D.Double(0, 0, shadedImage.getWidth(), shadedImage.getHeight());
            g2.fill(rect);
            g2.setComposite(composite);
            g2.drawImage(image, 0, 0, null);

            double degree = Math.abs(((Math.PI / 2) - rads) / (Math.PI / 2));
            int alpha = (int)(64 - (64 * degree));

            GradientPaint gp =
                new GradientPaint(0, 0, new Color(0, 0, 0, 0), image.getWidth(),
                                  0, new Color(0, 0, 0, alpha));
            g2.setPaint(gp);
            g2.fillRect(0, EXTRA_SPACE / 2, image.getWidth(),
                        image.getHeight() - EXTRA_SPACE);
            g2.dispose();

            // now perspective adjust
            int ix = image.getWidth() / 2;

            x = (int)Math.abs(ix * (Math.cos(rads)));
            y = (int)(((EXTRA_SPACE / 2) * Math.sin(rads)));

            int x0 = ix - x;
            int y0 = -y;
            int x1 = ix + x;
            int y1 = y;
            int x2 = x1;
            int y2 = image.getHeight() - y;
            int x3 = x0;
            int y3 = image.getHeight() + y;

            PerspectiveFilter pf =
                new PerspectiveFilter(x0, y0, x1, y1, x2, y2, x3, y3);
            interImage = pf.filter(shadedImage, interImage);

            repaint();
        }
    }
}
