package org.jdesktop.swt.animation.demos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.KeyFramesBuilder;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swt.animation.rendering.JRendererFactory;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

/**
 * This demonstration is a variant of the demonstration by Chet Haase at JavaOne
 * 2008. Chet discussed the problem in the original Timing Framework where, by
 * default, each animation used its own {@link javax.swing.Timer}. This did not
 * scale and, as balls were added to the demonstration, the multiple timers
 * caused noticeable performance problems.
 * <p>
 * This version of the Timing Framework allows setting a default timer on the
 * {@link AnimatorBuilder}, thus making it easy for client code to avoid this
 * problem. This design was inspired by the JavaOne 2008 talk.
 * <p>
 * This program is a copy of Chet's demo implemented in SWT.
 * 
 * @author Tim Halloran
 */
public class TooManyBalls implements JRendererTarget<Display, GC> {

  public static void main(String[] args) {
    new TooManyBalls();
  }

  private static final Interpolator ACCEL_4_4 = new AccelerationInterpolator(0.4, 0.4);
  private static final Interpolator SPLINE_0_1_1_0 = new SplineInterpolator(0.00, 1.00, 1.00, 1.00);
  private static final Interpolator SPLINE_1_0_1_1 = new SplineInterpolator(1.00, 0.00, 1.00, 1.00);

  private final Label f_infoLabel;
  private final Canvas f_panel;
  private final Random f_die = new Random();
  private Image[] f_ballImages;

  public class Ball {
    int x, y;
    int imageIndex;
    Animator animator;

    public void setX(int x) {
      this.x = x;
    }

    public void setY(int y) {
      this.y = y;
    }
  }

  private final List<Ball> f_balls = new ArrayList<Ball>();

  long f_paintCount = 0;
  long f_lastPaintNanos = 0;
  long f_totalPaintTimeNanos = 0;

  private long getFPS() {
    if (f_paintCount < 1)
      return 0;
    final long avgCycleTime = f_totalPaintTimeNanos / f_paintCount;
    if (avgCycleTime != 0) {
      return TimeUnit.SECONDS.toNanos(1) / avgCycleTime;
    } else
      return 0;
  }

  public TooManyBalls() {
    final Display display = Display.getDefault();
    final Shell shell = new Shell(display);
    shell.setText("SWT Too Many Balls!");

    /**
     * Used for ball animations.
     */
    final TimingSource animationTimer = new SWTTimingSource(display);
    AnimatorBuilder.setDefaultTimingSource(animationTimer);

    /**
     * Used to update the FPS display once a second.
     */
    final TimingSource infoTimer = new SWTTimingSource(1, TimeUnit.SECONDS, display);

    final GridLayout panelLayout = new GridLayout(2, false);
    shell.setLayout(panelLayout);
    GridData gridData;

    final Composite topPanel = new Composite(shell, SWT.NONE);
    gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
    topPanel.setLayoutData(gridData);
    final RowLayout topPanelLayout = new RowLayout();
    topPanel.setLayout(topPanelLayout);

    final Button addBall = new Button(topPanel, SWT.PUSH);
    addBall.setText("Add Ball");
    addBall.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        addBall();
        updateBallCount();
      }
    });
    final Button add10Balls = new Button(topPanel, SWT.PUSH);
    add10Balls.setText("Add 10 Balls");
    add10Balls.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        updateBallCount();
      }
    });

    final Button removeBall = new Button(topPanel, SWT.PUSH);
    removeBall.setText("Remove Ball");
    removeBall.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        removeBall();
        updateBallCount();
      }
    });
    final Button remove10Balls = new Button(topPanel, SWT.PUSH);
    remove10Balls.setText("Remove 10 Balls");
    remove10Balls.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        updateBallCount();
      }
    });

    f_infoLabel = new Label(shell, SWT.RIGHT);
    gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    f_infoLabel.setLayoutData(gridData);
    updateBallCount();

    f_panel = new Canvas(shell, SWT.DOUBLE_BUFFERED);
    gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
    f_panel.setLayoutData(gridData);
    f_panel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

    JRenderer<Canvas> renderer = JRendererFactory.getDefaultRenderer(f_panel, this, false);

    infoTimer.addTickListener(new TickListener() {
      @Override
      public void timingSourceTick(TimingSource source, long nanoTime) {
        updateBallCount();
      }
    });

    animationTimer.init();
    infoTimer.init();

    shell.setSize(800, 600);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    renderer.getTimingSource().dispose();
    renderer.shutdown();
    animationTimer.dispose();
    infoTimer.dispose();
    display.dispose();
  }

  private void updateBallCount() {
    f_infoLabel.setText("Balls: " + f_balls.size() + "    FPS: " + getFPS());
  }

  /*
   * Renderer thread methods and state
   */

  private void addBall() {
    final Ball ball = new Ball();
    ball.imageIndex = f_die.nextInt(5);
    Image ballImage = f_ballImages[ball.imageIndex];

    ball.x = f_die.nextInt(f_panel.getSize().x - ballImage.getBounds().width);
    ball.y = f_die.nextInt(f_panel.getSize().y - ballImage.getBounds().height);

    final int duration = 4 + f_die.nextInt(10);

    /*
     * Create a circular movement.
     */
    int radiusX = f_die.nextInt(400);
    if (f_die.nextBoolean())
      radiusX = -radiusX;
    int radiusY = f_die.nextInt(300);
    if (f_die.nextBoolean())
      radiusY = -radiusY;
    KeyFramesBuilder<Integer> builder = new KeyFramesBuilder<Integer>(ball.x);
    builder.addFrame(ball.x + radiusX, SPLINE_0_1_1_0);
    builder.addFrame(ball.x, SPLINE_1_0_1_1);
    builder.addFrame(ball.x - radiusX, SPLINE_0_1_1_0);
    builder.addFrame(ball.x, SPLINE_1_0_1_1);
    final KeyFrames<Integer> framesX = builder.build();

    builder = new KeyFramesBuilder<Integer>(ball.y);
    builder.addFrame(ball.y + radiusY, SPLINE_1_0_1_1);
    builder.addFrame(ball.y + (2 * radiusY), SPLINE_0_1_1_0);
    builder.addFrame(ball.y + radiusY, SPLINE_1_0_1_1);
    builder.addFrame(ball.y, SPLINE_0_1_1_0);
    final KeyFrames<Integer> framesY = builder.build();

    final TimingTarget circularMovement = new TimingTargetAdapter() {
      @Override
      public void timingEvent(Animator source, double fraction) {
        ball.x = framesX.getInterpolatedValueAt(fraction);
        ball.y = framesY.getInterpolatedValueAt(fraction);
      }
    };
    /*
     * Sometimes go at a constant rate, sometimes accelerate and decelerate.
     */
    final Interpolator i = f_die.nextBoolean() ? ACCEL_4_4 : null;
    ball.animator = new AnimatorBuilder().setDuration(duration, TimeUnit.SECONDS).addTarget(circularMovement)
        .setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.LOOP).setInterpolator(i).build();
    ball.animator.start();

    f_balls.add(ball);
  }

  private void removeBall() {
    if (f_balls.isEmpty())
      return;

    Ball ball = f_balls.remove(0);
    if (ball != null) {
      ball.animator.stop();
    }
  }

  @Override
  public void renderSetup(Display d) {
    f_ballImages = new Image[DemoResources.SPHERES.length];
    int index = 0;
    for (String resourceName : DemoResources.SPHERES) {
      f_ballImages[index++] = DemoResources.getImage(resourceName, d);
    }
  }

  @Override
  public void renderUpdate() {
    // Nothing to do
  }

  @Override
  public void render(GC g, int width, int height) {
    final Display display = f_panel.getDisplay();

    g.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
    g.fillRectangle(0, 0, width, height);

    for (Ball ball : f_balls) {
      g.drawImage(f_ballImages[ball.imageIndex], ball.x, ball.y);
    }

    // Statistics
    final long now = System.nanoTime();
    if (f_lastPaintNanos == 0) {
      f_lastPaintNanos = now;
    } else {
      f_paintCount++;
      f_totalPaintTimeNanos += now - f_lastPaintNanos;
      f_lastPaintNanos = now;
    }
  }

  @Override
  public void renderShutdown() {
    // Nothing to do
  }
}
