package org.jdesktop.swing.animation.demos;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.demos.DemoResources;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.Trigger;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.triggers.FocusTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.jdesktop.swing.animation.timing.triggers.TriggerUtility;

/**
 * Simple program that demonstrates the use of several different {@link Trigger}
 * implementations available in the Timing Framework.
 * <p>
 * This demo is discussed in Chapter 15 on pages 388&ndash;391 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * 
 * @author Chet Haase
 */
public class Triggers extends JComponent {

  SpherePanel armed, over, action, focus, timing;
  static JButton triggerButton;

  /** Creates a new instance of Triggers */
  public Triggers() {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    action = new SpherePanel(DemoResources.YELLOW_SPHERE, "B-Click", true);
    focus = new SpherePanel(DemoResources.BLUE_SPHERE, "Key-Foc", false);
    armed = new SpherePanel(DemoResources.RED_SPHERE, "M-Press", true);
    over = new SpherePanel(DemoResources.GREEN_SPHERE, "M-Enter", false);
    timing = new SpherePanel(DemoResources.GRAY_SPHERE, "1-Stop", true);

    add(action);
    add(focus);
    add(armed);
    add(over);
    add(timing);

    /*
     * Add triggers for each sphere, depending on what we want to trigger them.
     */
    TriggerUtility.addActionTrigger(triggerButton, action.getAnimator());
    TriggerUtility.addFocusTrigger(triggerButton, focus.getAnimator(), FocusTriggerEvent.IN, true);
    TriggerUtility.addMouseTrigger(triggerButton, armed.getAnimator(), MouseTriggerEvent.PRESS);
    TriggerUtility.addMouseTrigger(triggerButton, over.getAnimator(), MouseTriggerEvent.ENTER, true);
    TriggerUtility.addTimingTrigger(action.getAnimator(), timing.getAnimator(), TimingTriggerEvent.STOP);
  }

  static void createAndShowGUI() {
    JFrame f = new JFrame("Swing Triggers");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLayout(new BorderLayout());
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BorderLayout());
    /*
     * Note that "Other Button" exists only to provide another component to move
     * focus from/to, in order to show how a focus trigger works.
     */
    buttonPanel.add(new JButton("Other Button"), BorderLayout.NORTH);
    triggerButton = new JButton("Trigger");
    buttonPanel.add(triggerButton, BorderLayout.SOUTH);
    f.add(buttonPanel, BorderLayout.NORTH);
    f.add(new Triggers(), BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
  }

  public static void main(String args[]) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    TimingSource ts = new SwingTimerTimingSource();
    Animator.setDefaultTimingSource(ts);
    ts.init();

    Runnable doCreateAndShowGUI = new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    };
    SwingUtilities.invokeLater(doCreateAndShowGUI);
  }

  private static final long serialVersionUID = -907905936402755070L;

  /**
   * This class encapsulates both the rendering of a sphere, at a location that
   * may be animating, and the animation that drives the sphere movement.
   * 
   * @author Chet Haase
   */
  public static class SpherePanel extends JPanel {

    static final int PADDING = 5;
    static final int PANEL_HEIGHT = 300;
    static final Interpolator ACCEL_5_5 = new AccelerationInterpolator(.5, .5);

    final BufferedImage f_sphereImage;
    final int f_sphereX = PADDING;
    final Animator f_bouncer;
    final String f_label;

    int f_sphereY = 20; // mutable

    /**
     * The animation changes the location of the sphere over time through this
     * property setter. We force a repaint to display the sphere in its new
     * location.
     */
    public void setSphereY(int sphereY) {
      this.f_sphereY = sphereY;
      repaint();
    }

    /**
     * Load the named image and create the animator that will bounce the image
     * down and back up in this panel.
     */
    SpherePanel(String resourceName, String label, boolean bounce) {
      try {
        f_sphereImage = ImageIO.read(DemoResources.getResource(resourceName));
      } catch (Exception e) {
        throw new IllegalStateException("Problem loading image " + resourceName, e);
      }
      setPreferredSize(new Dimension(f_sphereImage.getWidth() + 2 * PADDING, PANEL_HEIGHT));
      f_bouncer = new Animator.Builder().setDuration(2, SECONDS).setInterpolator(ACCEL_5_5).build();
      if (bounce)
        f_bouncer.addTarget(PropertySetter.getTarget(this, "sphereY", 20, (PANEL_HEIGHT - f_sphereImage.getHeight()), 20));
      else
        f_bouncer.addTarget(PropertySetter.getTarget(this, "sphereY", 20, (PANEL_HEIGHT - f_sphereImage.getHeight())));
      f_label = label;
    }

    Animator getAnimator() {
      return f_bouncer;
    }

    @Override
    protected void paintComponent(Graphics g) {
      g.setColor(Color.white);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(Color.blue);
      g.drawString(f_label, f_sphereX, 15);
      g.drawImage(f_sphereImage, f_sphereX, f_sphereY, null);
    }

    private static final long serialVersionUID = 5222141023907163488L;
  }
}
