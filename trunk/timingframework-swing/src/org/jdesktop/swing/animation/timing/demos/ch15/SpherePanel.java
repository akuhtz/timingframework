package org.jdesktop.swing.animation.timing.demos.ch15;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.demos.DemoResources;

/**
 * This class encapsulates both the rendering of a sphere, at a location that
 * may be animating, and the animation that drives the sphere movement.
 * 
 * @author Chet Haase
 */
@SuppressWarnings("serial")
public class SpherePanel extends JPanel {

  private static final int PADDING = 5;
  private static final int PANEL_HEIGHT = 300;

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
    final TimingTarget ps = PropertySetter.build(this, "sphereY", 20, (PANEL_HEIGHT - f_sphereImage.getHeight()), 20);
    f_bouncer = new AnimatorBuilder().addTarget(ps).setDuration(2, TimeUnit.SECONDS)
        .setInterpolator(new AccelerationInterpolator(.5, .5)).build();
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
}
