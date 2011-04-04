package org.jdesktop.swing.animation.timing.demos.ch15;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.triggers.FocusTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.TimingTrigger;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.Trigger;
import org.jdesktop.swing.animation.timing.demos.DemoResources;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.jdesktop.swing.animation.timing.triggers.ActionTrigger;
import org.jdesktop.swing.animation.timing.triggers.FocusTrigger;
import org.jdesktop.swing.animation.timing.triggers.MouseTrigger;

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
    action = new SpherePanel(DemoResources.YELLOW_SPHERE, "B-Click");
    focus = new SpherePanel(DemoResources.BLUE_SPHERE, "Key-Foc");
    armed = new SpherePanel(DemoResources.RED_SPHERE, "M-Press");
    over = new SpherePanel(DemoResources.GREEN_SPHERE, "M-Enter");
    timing = new SpherePanel(DemoResources.GRAY_SPHERE, "1-Stop");

    add(action);
    add(focus);
    add(armed);
    add(over);
    add(timing);

    /*
     * Add triggers for each sphere, depending on what we want to trigger them.
     */
    ActionTrigger.addTrigger(triggerButton, action.getAnimator());
    FocusTrigger.addTrigger(triggerButton, focus.getAnimator(), FocusTriggerEvent.IN);
    MouseTrigger.addTrigger(triggerButton, armed.getAnimator(), MouseTriggerEvent.PRESS);
    MouseTrigger.addTrigger(triggerButton, over.getAnimator(), MouseTriggerEvent.ENTER);
    TimingTrigger.addTrigger(action.getAnimator(), timing.getAnimator(), TimingTriggerEvent.STOP);
  }

  private static void createAndShowGUI() {
    JFrame f = new JFrame("Triggers");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLayout(new BorderLayout());
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BorderLayout());
    // Note: "Other Button" exists only to provide another component to
    // move focus from/to, in order to show how FocusTrigger works
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

    TimingSource ts = new SwingTimerTimingSource(10, TimeUnit.MILLISECONDS);
    AnimatorBuilder.setDefaultTimingSource(ts);
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

    private static final int PADDING = 5;
    private static final int PANEL_HEIGHT = 300;
    private static final Interpolator ACCEL_5_5 = new AccelerationInterpolator(.5, .5);

    private final BufferedImage f_sphereImage;
    private final int f_sphereX = PADDING;
    private final Animator f_bouncer;
    private final String f_label;

    private int f_sphereY = 20; // mutable

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
    SpherePanel(String resourceName, String label) {
      try {
        f_sphereImage = ImageIO.read(DemoResources.getResource(resourceName));
      } catch (Exception e) {
        throw new IllegalStateException("Problem loading image " + resourceName, e);
      }
      setPreferredSize(new Dimension(f_sphereImage.getWidth() + 2 * PADDING, PANEL_HEIGHT));
      f_bouncer = new AnimatorBuilder().setDuration(2, TimeUnit.SECONDS).setInterpolator(ACCEL_5_5).build();
      f_bouncer.addTarget(PropertySetter.getTarget(this, "sphereY", 20, (PANEL_HEIGHT - f_sphereImage.getHeight()), 20));
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
