package widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;


/**
 * @author Harry Anuszewski
 */
public abstract class Widget extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7965271593482426566L;
	protected ReversibleComponent widgetComp;
    protected FadingButton infoButton;
    protected FadingButton closeButton;

    protected static int BORDER_GAP = 15;

    public Widget() {
        setLayout(new FlowLayout());
        Border b =
            new TransparentBorder((FlipTransition.EXTRA_SPACE / 2), BORDER_GAP,
                                  (FlipTransition.EXTRA_SPACE / 2),
                                  BORDER_GAP);
        ((JComponent)getContentPane()).setBorder(b);
        setTitle("Widget");
        createComp();
        add(widgetComp);

        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);

        setLocationRelativeTo(null);
        makeTransparent();
        MoveListener ml = new MoveListener();
        widgetComp.addMouseMotionListener(ml);
        widgetComp.addMouseListener(ml);

        Dimension d = getPreferredSize();
        setSize(d);
        doLayout();
        addCloseButton();
        addInfoButton();
        
        setVisible(true);
    }
    
    private void makeTransparent() {
        setUndecorated(true); // remove the window controls
        setResizable(false); // remove the resize controls

        // OSX transparency
        //setBackground(new Color(0f, 0f, 0f, 0f));
        setBackground(new Color(Color.TRANSLUCENT));

        // Non-reflection version for Java 6 SE u10:
        // AWTUtilities.setWindowOpaque(this, false);
        //
        // Reflection version (to compile on Java 1.5):
        try {
            Class clazz = Class.forName("com.sun.awt.AWTUtilities");
            Method method =
                clazz.getMethod("setWindowOpaque", new Class[] { Window.class,
                                                                 Boolean.TYPE });
            method.invoke(clazz, new Object[] { this, false });
        } catch (ClassNotFoundException e) {
            // Oh well, not Java 6 u10
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    public Dimension getPreferredSize() {
        return getContentPane().getPreferredSize();
    }

    protected ReversibleComponent getRevesiblecomponent() {
        return widgetComp;
    }

    /**
     * @return the component featured on the front of the widget
     */
    abstract protected JComponent createFront();

    /**
     * @return the component featured on the back of the widget
     */
    abstract protected JComponent createBack();

    private void createComp() {
        widgetComp = new ReversibleComponent();
        widgetComp.setFront(createFront());
        widgetComp.setBack(createBack());
    }

    protected void addCloseButton() {
        closeButton = new CloseButton();
        getLayeredPane().add(closeButton, JLayeredPane.DRAG_LAYER - 1);
        closeButton.setLocation(getCloseButtonLocation());
        addButtonListeners(closeButton);
        closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (widgetComp.getShowingComponent() == null)
                        return;
                    closeButton.removeActionListener(this);
                    closeButton.setVisible(false);
                    close();
                }
            });
    }

    protected void addInfoButton() {
        infoButton = new InfoButton();
        getLayeredPane().add(infoButton, JLayeredPane.DRAG_LAYER - 1);
        infoButton.setLocation(getInfoButtonLocation());
        addButtonListeners(infoButton);

        infoButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    widgetComp.flip();
                }
            });
    }

    private void addButtonListeners(final FadingButton button) {
        widgetComp.addMouseListener(new MouseInputAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setRollover(true);
                }

                public void mouseExited(MouseEvent e) {
                    Point p =
                        SwingUtilities.convertPoint((Component)e.getSource(),
                                                    e.getPoint(), widgetComp);
                    button.setRollover(widgetComp.contains(p));
                }
            });

        widgetComp.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ReversibleComponent.FLIP_PROPERTY))
                        button.setVisible(evt.getNewValue() ==
                                          ReversibleComponent.FLIP_STATE.FRONT);
                }
            });
    }

    /**
     * @return the location of the info button in LayeredPane coordinate space.
     */
    protected Point getInfoButtonLocation() {
        JComponent c = widgetComp.getFront();
        Point p = c.getLocation();
        p.translate(c.getPreferredSize().width - 14,
                    c.getPreferredSize().height - 14);
        p.translate(widgetComp.getParent().getInsets().right,
                    widgetComp.getParent().getInsets().top);
        p = SwingUtilities.convertPoint(c, p, getLayeredPane());
        return p;
    }

    /**
     * @return the location of the info button in LayeredPane coordinate space.
     */
    protected Point getCloseButtonLocation() {
        return new Point(7, 17);
    }

    private void close() {
        CloseTransition closeTrans =
            new CloseTransition(widgetComp.getShowingComponent(), widgetComp);
        closeTrans.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getNewValue() == CloseTransition.STATE.COMPLETE)
                        System.exit(0);
                }
            });
        closeTrans.doTransition();
    }

    static boolean isOSX() {
        String lcOSName = System.getProperty("os.name").toLowerCase();
        boolean MAC_OS_X = lcOSName.startsWith("mac os x");
        return MAC_OS_X;
    }

    private class MoveListener extends MouseInputAdapter {
        private Point location;

        public void mousePressed(MouseEvent me) {
            location = me.getPoint();
        }

        public void mouseDragged(MouseEvent me) {
            Point p = me.getPoint();
            SwingUtilities.convertPointToScreen(p,
                                                ((JComponent)me.getSource()));
            p.translate(-location.x, -location.y);
            p.translate(-widgetComp.getX(), -widgetComp.getY());
            setLocation(p);
        }
    }
}

