package org.jdesktop.swt.animation.demos;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdesktop.core.animation.demos.DemoResources;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Evaluator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swt.animation.timing.evaluators.EvaluatorPoint;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

/**
 * This demonstration uses property setters to create a "to" animation using the
 * Timing Framework.
 * <p>
 * A "to" animation uses the getter on the property to set the starting point of
 * the animation. Further, it tests that the {@link EvaluatorPoint} class is
 * automatically chosen as the {@link Evaluator} for an SWT {@link Point} .
 * 
 * @author Tim Halloran
 */
public final class ClickAndGo extends Canvas {

  public static void main(String[] args) {
    final Display display = Display.getDefault();
    final Shell shell = new Shell(display);
    shell.setText("SWT Click and Go!");
    shell.setLayout(new FillLayout());

    setupGUI(shell);

    shell.setSize(800, 600);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

  public static class Ball {
    Point location;
    Rectangle rect;
    Color rectColor;
    Image image;
    Animator animator;

    public void setLocation(Point value) {
      location = value;
    }

    public Point getLocation() {
      return location;
    }

    public void setRect(Rectangle value) {
      rect = value;
    }

    public Rectangle getRect() {
      return rect;
    }

    public void setRectRGB(RGB value) {
      if (rectColor != null)
        rectColor.dispose();
      rectColor = new Color(image.getDevice(), value);
    }

    public RGB getRectRGB() {
      return rectColor.getRGB();
    }

    public Color getRecColor() {
      return rectColor;
    }
  }

  static final Ball f_ball = new Ball();
  static final Random f_die = new Random();
  static Canvas f_panel = null;

  public static void setupGUI(Shell shell) {
    f_ball.image = DemoImages.getImage(DemoResources.BLUE_SPHERE, shell.getDisplay());
    f_ball.setLocation(new Point(50, 90));
    final int rectSize = f_ball.image.getBounds().width;
    f_ball.setRect(new Rectangle(50, 90, rectSize, rectSize));
    f_ball.setRectRGB(new RGB(100, 100, 100));

    shell.setLayout(new FillLayout());

    f_panel = new ClickAndGo(shell, SWT.DOUBLE_BUFFERED);

  }

  public ClickAndGo(Composite parent, int style) {
    super(parent, style);
    addPaintListener(new PaintListener() {

      @Override
      public void paintControl(PaintEvent e) {
        final GC gc = e.gc;
        gc.setBackground(e.widget.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, getBounds().width, getBounds().height);

        gc.drawString("Click on the screen and the ball and the colored rectangle will move to that point in two seconds.", 5, 5);
        gc.drawString("The ball and the colored rectangle don't use the same interpolator, so they will move at different rates.",
            5, 20);
        gc.drawString("Feel free to change the destination at any time.", 5, 35);

        gc.setBackground(f_ball.getRecColor());
        gc.setForeground(e.widget.getDisplay().getSystemColor(SWT.COLOR_BLACK));
        gc.fillRectangle(f_ball.getRect());
        gc.drawRectangle(f_ball.getRect());
        gc.drawImage(f_ball.image, f_ball.location.x, f_ball.location.y);
      }
    });
    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(MouseEvent e) {
        if (f_ball.animator != null)
          f_ball.animator.stop();

        final TimingSource ts = new SWTTimingSource(e.widget.getDisplay());
        ts.addPostTickListener(new PostTickListener() {
          @Override
          public void timingSourcePostTick(TimingSource source, long nanoTime) {
            if (f_panel != null && !f_panel.isDisposed())
              f_panel.redraw();
          }
        });
        ts.init();

        f_ball.animator = new Animator.Builder(ts).setDuration(2, TimeUnit.SECONDS).setDisposeTimingSource(true).build();

        final Point clickPoint = new Point(e.x, e.y);
        f_ball.animator.addTarget(PropertySetter
            .getTargetTo(f_ball, "location", new AccelerationInterpolator(0.5, 0.5), clickPoint));

        final int rectSize = f_ball.image.getBounds().width;
        final Rectangle clickRect = new Rectangle(e.x, e.y, rectSize * (f_die.nextInt(4) + 1), rectSize * (f_die.nextInt(4) + 1));
        f_ball.animator.addTarget(PropertySetter.getTargetTo(f_ball, "rect", clickRect));

        final RGB rectRGB = new RGB(f_die.nextInt(255), f_die.nextInt(255), f_die.nextInt(255));
        f_ball.animator.addTarget(PropertySetter.getTargetTo(f_ball, "rectRGB", rectRGB));

        f_ball.animator.start();
      }
    });
  }
}
