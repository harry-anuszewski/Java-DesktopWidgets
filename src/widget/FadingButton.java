package widget;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.DefaultButtonModel;
import javax.swing.Timer;


/**
 * @author Harry Anuszewski
 */
public abstract class FadingButton extends AbstractButton {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8046485745214872606L;
	protected float alpha = 0f;
    private Timer fadeTimer = new Timer(25, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (getModel().isRollover()) {
                    alpha += 0.06;
                    if (alpha >= 1f) {
                        alpha = 1f;
                        fadeTimer.stop();
                    }
                } else {
                    alpha -= 0.06;
                    if (alpha <= 0f) {
                        alpha = 0f;
                        fadeTimer.stop();
                    }
                }
                repaint();
            }
        });


    public FadingButton() {
        setOpaque(false);
        setModel(new DefaultButtonModel());

        addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (getShape().contains(e.getPoint()))
                        fireActionPerformed(new ActionEvent(FadingButton.this,
                                                            ActionEvent.ACTION_PERFORMED,
                                                            "click"));
                }

                public void mouseEntered(MouseEvent e) {
                    getModel().setArmed(true);
                    getModel().setRollover(true);
                }

                public void mouseExited(MouseEvent e) {
                    getModel().setArmed(false);
                }
            });
    }

    public void setRollover(boolean rollover) {
        getModel().setRollover(rollover);
        fadeTimer.start();
    }

    protected abstract Shape getShape();
}
