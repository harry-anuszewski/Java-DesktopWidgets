package widget;


import com.jhlabs.image.PinchFilter;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;


/**
 * @author Harry Anuszewski
 */
public class CloseTransition extends AnimatedTransition {
    private float alpha = 1.2f;
    private float alphaStep = 0.02f;
    private PinchFilter pinchFilter = new PinchFilter();

    public CloseTransition(JComponent startComp, JLayeredPane layerPane) {
        super(startComp, startComp, layerPane);
        pinchFilter.setCentreX(0f);
        pinchFilter.setCentreY(0f);
        pinchFilter.setRadius(Math.min(startComp.getWidth(),
                                       startComp.getHeight()));
    }

    protected void updateInterImage() {
    }

    protected boolean timerTick() {
        pinchFilter.setRadius(pinchFilter.getRadius() * 1.07f);
        pinchFilter.setAmount(pinchFilter.getAmount() * 1.05f);
        alphaStep *= 1.07;
        alpha = Math.max(0f, alpha - alphaStep);
        return alpha <= 0f;
    }

    protected void paintInterImage(Graphics2D g2, int width, int height) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   Math.min(1f, alpha)));
        g2.drawImage(startImage, pinchFilter, 0, 0);
    }
}
