package org.jdesktop.android.animation.demos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.jdesktop.android.animation.rendering.JPassiveRenderer;
import org.jdesktop.android.animation.timing.sources.AndroidTimingSource;
import org.jdesktop.core.animation.demos.DemoResources;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public final class TooManyBalls extends Activity implements JRendererTarget<SurfaceView, Canvas> {
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.too_many_balls);

    final Button addButton = (Button) findViewById(R.id.button_add_ball);
    if (addButton == null)
      throw new IllegalArgumentException("R.id.button_add_ball does not reference a Button");
    addButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        addBall();
      }
    });

    final Button removeButton = (Button) findViewById(R.id.button_remove_ball);
    if (removeButton == null)
      throw new IllegalArgumentException("R.id.button_remove_ball does not reference a Button");
    removeButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        removeBall();
      }
    });

    try {
      f_ballImages = new Bitmap[DemoResources.SPHERES.length];
      int index = 0;
      for (String resourceName : DemoResources.SPHERES) {
        f_ballImages[index++] = BitmapFactory.decodeStream(DemoResources.getResource(resourceName).openStream());
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failure loading color ball images to animate", e);
    }
  }

  private SurfaceView f_ballView = null;
  private JRenderer f_renderer = null;

  @Override
  protected void onResume() {
    super.onResume();

    /**
     * Used for ball animations.
     */
    final TimingSource animationTimer = new AndroidTimingSource(15, TimeUnit.MILLISECONDS, this);
    Animator.setDefaultTimingSource(animationTimer);

    f_ballView = (SurfaceView) findViewById(R.id.surfaceViewBalls);
    if (f_ballView == null)
      throw new IllegalStateException("Can't find ball SurfaceView for the demo");

    f_renderer = new JPassiveRenderer(f_ballView, this, animationTimer);

    animationTimer.init();
  }

  @Override
  protected void onPause() {
    super.onPause();

    final TimingSource ts = Animator.getDefaultTimingSource();
    if (ts != null)
      ts.dispose();

    f_renderer.shutdown();
    f_renderer = null;

    f_ballView = null;
  }

  private static final Interpolator ACCEL_4_4 = new AccelerationInterpolator(0.4, 0.4);
  private static final Interpolator SPLINE_0_1_1_0 = new SplineInterpolator(0.00, 1.00, 1.00, 1.00);
  private static final Interpolator SPLINE_1_0_1_1 = new SplineInterpolator(1.00, 0.00, 1.00, 1.00);

  private final Random f_die = new Random();
  private Bitmap[] f_ballImages;

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

  long getFPS() {
    if (f_paintCount < 1)
      return 0;
    final long avgCycleTime = f_totalPaintTimeNanos / f_paintCount;
    if (avgCycleTime != 0) {
      return TimeUnit.SECONDS.toNanos(1) / avgCycleTime;
    } else
      return 0;
  }

  void addBall() {
    final Ball ball = new Ball();
    ball.imageIndex = f_die.nextInt(5);

    ball.x = f_die.nextInt(f_ballView.getWidth());
    ball.y = f_die.nextInt(f_ballView.getHeight());

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
    KeyFrames.Builder<Integer> builder = new KeyFrames.Builder<Integer>(ball.x);
    builder.addFrame(ball.x + radiusX, SPLINE_0_1_1_0);
    builder.addFrame(ball.x, SPLINE_1_0_1_1);
    builder.addFrame(ball.x - radiusX, SPLINE_0_1_1_0);
    builder.addFrame(ball.x, SPLINE_1_0_1_1);
    final KeyFrames<Integer> framesX = builder.build();

    builder = new KeyFrames.Builder<Integer>(ball.y);
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
    ball.animator = new Animator.Builder().setDuration(duration, TimeUnit.SECONDS).addTarget(circularMovement)
        .setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.LOOP).setInterpolator(i).build();
    ball.animator.start();

    f_balls.add(ball);
  }

  void removeBall() {
    if (f_balls.isEmpty())
      return;

    Ball ball = f_balls.remove(0);
    if (ball != null) {
      ball.animator.stop();
    }
  }

  public void renderSetup(SurfaceView d) {
    // TODO Auto-generated method stub

  }

  public void renderUpdate() {
    // TODO Auto-generated method stub

  }

  public void render(Canvas g, int width, int height) {

    g.drawColor(Color.WHITE);

    for (Ball ball : f_balls) {
      g.drawBitmap(f_ballImages[ball.imageIndex], ball.x, ball.y, null);
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

  public void renderShutdown() {
    // TODO Auto-generated method stub
  }
}
