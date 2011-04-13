package org.jdesktop.swt.animation.demos;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.Trigger;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.triggers.FocusTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;
import org.jdesktop.swt.animation.timing.triggers.TriggerUtility;

/**
 * Simple program that demonstrates the use of several different {@link Trigger}
 * implementations available in the Timing Framework.
 * <p>
 * This demo is discussed in Chapter 15 on pages 388&ndash;391 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * 
 * @author Chet Haase
 */
public class Triggers extends Composite {

  public static void main(String[] args) {
    final Display display = Display.getDefault();
    final Shell shell = new Shell(display);
    shell.setText("SWT Triggers");

    final TimingSource ts = new SWTTimingSource(display);
    AnimatorBuilder.setDefaultTimingSource(ts);
    ts.init();

    createAndShowGUI(shell);

    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    ts.dispose();
    display.dispose();
  }

  SpherePanel armed, over, action, focus, timing;
  static Button triggerButton;

  /** Creates a new instance of Triggers */
  public Triggers(Composite parent, int style) {
    super(parent, style);
    setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
    final RowLayout layout = new RowLayout(SWT.HORIZONTAL);
    layout.fill = true;
    layout.wrap = false;
    layout.spacing = 0;
    setLayout(layout);
    action = new SpherePanel(this, SWT.DOUBLE_BUFFERED, DemoResources.YELLOW_SPHERE, "B-Click", true);
    focus = new SpherePanel(this, SWT.DOUBLE_BUFFERED, DemoResources.BLUE_SPHERE, "Key-Foc", false);
    armed = new SpherePanel(this, SWT.DOUBLE_BUFFERED, DemoResources.RED_SPHERE, "M-Press", true);
    over = new SpherePanel(this, SWT.DOUBLE_BUFFERED, DemoResources.GREEN_SPHERE, "M-Enter", false);
    timing = new SpherePanel(this, SWT.DOUBLE_BUFFERED, DemoResources.GRAY_SPHERE, "1-Stop", true);

    /*
     * Add triggers for each sphere, depending on what we want to trigger them.
     */
    TriggerUtility.addEventTrigger(triggerButton, SWT.Selection, action.getAnimator());
    TriggerUtility.addFocusTrigger(triggerButton, focus.getAnimator(), FocusTriggerEvent.IN, true);
    TriggerUtility.addMouseTrigger(triggerButton, armed.getAnimator(), MouseTriggerEvent.PRESS);
    TriggerUtility.addMouseTrigger(triggerButton, over.getAnimator(), MouseTriggerEvent.ENTER, true);
    TriggerUtility.addTimingTrigger(action.getAnimator(), timing.getAnimator(), TimingTriggerEvent.STOP);
  }

  private static void createAndShowGUI(Shell shell) {
    shell.setLayout(new GridLayout());
    final Composite buttonPanel = new Composite(shell, SWT.NONE);
    buttonPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    buttonPanel.setLayout(new GridLayout());
    /*
     * Note that "Other Button" exists only to provide another component to move
     * focus from/to, in order to show how a focus trigger works.
     */
    final Button otherButton = new Button(buttonPanel, SWT.PUSH);
    otherButton.setText("Other Button");
    otherButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    triggerButton = new Button(buttonPanel, SWT.PUSH);
    triggerButton.setText("Trigger");
    triggerButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    final Composite c = new Triggers(shell, SWT.NONE);
    c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
  }

  /**
   * This class encapsulates both the rendering of a sphere, at a location that
   * may be animating, and the animation that drives the sphere movement.
   * 
   * @author Chet Haase
   * @author Tim Halloran
   */
  public static class SpherePanel extends Canvas {

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
    SpherePanel(Composite parent, int style, String resourceName, String label, boolean bounce) {
      super(parent, style);
      f_sphereImage = DemoResources.getImage(resourceName, parent.getDisplay());
      f_bouncer = new AnimatorBuilder().setDuration(2, TimeUnit.SECONDS).setInterpolator(ACCEL_5_5).build();
      if (bounce)
        f_bouncer.addTarget(PropertySetter.getTarget(this, "sphereY", 20, (PANEL_HEIGHT - f_sphereImage.getBounds().height), 20));
      else
        f_bouncer.addTarget(PropertySetter.getTarget(this, "sphereY", 20, (PANEL_HEIGHT - f_sphereImage.getBounds().height)));
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
}
