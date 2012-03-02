package org.jdesktop.android.animation.demos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.jdesktop.android.animation.timing.sources.AndroidTimingSource;
import org.jdesktop.core.animation.demos.DemoResources;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public final class TooManyBalls extends Activity implements JRendererTarget<Activity, Canvas> {
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(new BallView(this));
  }

  @Override
  protected void onResume() {
    super.onResume();

    /**
     * Used for ball animations.
     */
    final TimingSource animationTimer = new AndroidTimingSource(15, TimeUnit.MILLISECONDS, this);
    Animator.setDefaultTimingSource(animationTimer);

  }

  @Override
  protected void onPause() {
    super.onPause();

    final TimingSource ts = Animator.getDefaultTimingSource();
    if (ts != null)
      ts.dispose();
  }

  private static class BallView extends View {

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

    private long getFPS() {
      if (f_paintCount < 1)
        return 0;
      final long avgCycleTime = f_totalPaintTimeNanos / f_paintCount;
      if (avgCycleTime != 0) {
        return TimeUnit.SECONDS.toNanos(1) / avgCycleTime;
      } else
        return 0;
    }

    public BallView(Context context) {
      super(context);

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

    @Override
    protected void onDraw(final Canvas canvas) {
      canvas.drawColor(Color.BLACK);
      Paint circlePaint = new Paint();
      circlePaint.setColor(Color.RED);
      canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 3, circlePaint);
      // if (blueSphere != null) {
      // canvas.drawBitmap(blueSphere, 10, 10, null);
      // }
    }
  }

  @Override
  public void renderSetup(Activity d) {
    // TODO Auto-generated method stub

  }

  @Override
  public void renderUpdate() {
    // TODO Auto-generated method stub

  }

  @Override
  public void render(Canvas g, int width, int height) {
    // TODO Auto-generated method stub

  }

  @Override
  public void renderShutdown() {
    // TODO Auto-generated method stub

  }
}
