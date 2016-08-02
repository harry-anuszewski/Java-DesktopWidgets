package widget;


import com.jhlabs.image.PerspectiveFilter;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;


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
public class FlipTransition extends AnimatedTransition {

    private BufferedImage shadedImage;
    private double step = Math.PI / 80;
    private double rads = 0;
    private int x;
    private int y;

    static final int EXTRA_SPACE = 50;

    public FlipTransition(JComponent frontComp, JComponent backComp,
                          JLayeredPane layerPane) {
        super(frontComp, backComp, layerPane);
    }

    protected BufferedImage createEndImage(JComponent endComp, Insets insets) {
        BufferedImage endImage = grabImage(endComp, insets);

        // flip the back horizontal
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-endImage.getWidth(null), 0);
        AffineTransformOp op =
            new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        endImage = op.filter(endImage, null);

        return endImage;
    }

    protected BufferedImage createInterImage() {
        BufferedImage iimage =
            new BufferedImage(startImage.getWidth(), startImage.getHeight() +
                              EXTRA_SPACE + EXTRA_SPACE,
                              BufferedImage.TYPE_INT_ARGB);

        shadedImage =
                new BufferedImage(startImage.getWidth(), startImage.getHeight(),
                                  BufferedImage.TYPE_INT_ARGB);

        return iimage;
    }


    protected void updateInterImage() {
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
            new GradientPaint(0, 0, new Color(0, 0, 0, 0), image.getWidth(), 0,
                              new Color(0, 0, 0, alpha));
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
    }

    private BufferedImage getVisibleImage() {
        if (rads < Math.PI / 2)
            return startImage;
        else
            return endImage;
    }

    protected void paintInterImage(Graphics2D g2, int width, int height) {
        int imageX = (width / 2) - (interImage.getWidth() / 2);
        int imageY = (height / 2) - (interImage.getHeight() / 2);

        int x0 = (interImage.getWidth() / 2) - x;
        int y0 = EXTRA_SPACE - y;

        clearBackground(g2);

        g2.setClip(imageX + x0, imageY + y0,
                   interImage.getWidth() - (2 * x0) - 1,
                   (interImage.getHeight() - (2 * y0)));

        if (rads < Math.PI / 2)
            g2.drawImage(interImage, imageX + x0, imageY + y0,
                         imageX + x0 + interImage.getWidth(),
                         imageY + y0 + interImage.getHeight(), 0, 0,
                         interImage.getWidth(), interImage.getHeight(), null);
        else
            g2.drawImage(interImage, imageX + interImage.getWidth() - x0,
                         imageY + y0, imageX - x0,
                         imageY + y0 + interImage.getHeight(), 0, 0,
                         interImage.getWidth(), interImage.getHeight(), null);
    }

    private void clearBackground(Graphics2D g2) {
        Composite composite = g2.getComposite();
        g2.setColor(new Color(100, 0, 100));
        g2.setComposite(AlphaComposite.Clear);
        g2.setClip(0, -EXTRA_SPACE, startImage.getWidth(),
                   startImage.getHeight() + EXTRA_SPACE + EXTRA_SPACE);
        g2.fillRect(0, -EXTRA_SPACE, startImage.getWidth(),
                    startImage.getHeight() + EXTRA_SPACE + EXTRA_SPACE);
        g2.setComposite(composite);
    }

    protected boolean timerTick() {
        double additional =
            ((Math.PI / 2) - Math.abs((Math.PI / 2) - rads)) / 10;
        rads += step + additional;
        return rads >= Math.PI;
    }
}
