package org.jdesktop.swt.animation.timing.demos.ch15;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swt.animation.timing.demos.DemoResources;

/**
 * This class encapsulates both the rendering of a sphere, at a location that
 * may be animating, and the animation that drives the sphere movement.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class SpherePanel extends Canvas {

  private static final int PADDING = 5;
  private static final int PANEL_HEIGHT = 300;
  private static final Interpolator ACCEL_5_5 = new AccelerationInterpolator(.5, .5);

  private final Image f_sphereImage;
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
    redraw();
  }

  /**
   * Load the named image and create the animator that will bounce the image
   * down and back up in this panel.
   */
  SpherePanel(Composite parent, int style, String resourceName, String label) {
    super(parent, style);
    f_sphereImage = DemoResources.getImage(resourceName, parent.getDisplay());
    f_bouncer = new AnimatorBuilder().setDuration(2, TimeUnit.SECONDS).setInterpolator(ACCEL_5_5).build();
    f_bouncer.addTarget(PropertySetter.getTarget(this, "sphereY", 20, (PANEL_HEIGHT - f_sphereImage.getBounds().height), 20));
    f_label = label;
    addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        final GC gc = e.gc;
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, getBounds().width, getBounds().height);
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
        gc.drawString(f_label, f_sphereX, 5);
        gc.drawImage(f_sphereImage, f_sphereX, f_sphereY);
      }
    });
  }

  @Override
  public Point computeSize(int wHint, int hHint, boolean changed) {
    final Point result = new Point(f_sphereImage.getBounds().width + 2 * PADDING, PANEL_HEIGHT);
    return result;
  }

  Animator getAnimator() {
    return f_bouncer;
  }
}