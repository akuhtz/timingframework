package org.jdesktop.android.animation.rendering;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public final class JPassiveRenderer implements JRenderer, SurfaceHolder.Callback {

  /*
   * Thread-confined to the SWT UI thread
   */
  final SurfaceView f_on;
  final JRendererTarget<SurfaceView, Canvas> f_target;
  final TimingSource f_ts;
  final PostTickListener f_postTick = new PostTickListener() {
    public void timingSourcePostTick(TimingSource source, long nanoTime) {
      long now = System.nanoTime();
      if (f_renderCount != 0) {
        f_totalRenderTime += now - f_lastRenderTimeNanos;
      }
      f_lastRenderTimeNanos = now;
      f_renderCount++;
      f_target.renderUpdate();

      final SurfaceHolder sh = f_on.getHolder();
      Canvas c = null;
      try {
        c = sh.lockCanvas();
        f_target.render(c, c.getWidth(), c.getHeight());
      } finally {
        if (c != null)
          sh.unlockCanvasAndPost(c);
      }
    }
  };

  /*
   * Statistics counters
   */
  long f_lastRenderTimeNanos;
  long f_totalRenderTime = 0;
  long f_renderCount = 0;

  public JPassiveRenderer(SurfaceView on, JRendererTarget<SurfaceView, Canvas> target, TimingSource timingSource) {
    if (on == null)
      throw new IllegalArgumentException(I18N.err(1, "on"));
    f_on = on;

    if (target == null)
      throw new IllegalArgumentException(I18N.err(1, "life"));
    f_target = target;

    if (timingSource == null)
      throw new IllegalArgumentException(I18N.err(1, "timingSource"));
    f_ts = timingSource;

    f_on.getHolder().addCallback(this);
  }

  public void invokeLater(Runnable task) {
    f_on.post(task);
  }

  public TimingSource getTimingSource() {
    return f_ts;
  }

  public long getFPS() {
    final long avgCycleTime = getAverageCycleTimeNanos();
    if (avgCycleTime != 0) {
      return SECONDS.toNanos(1) / avgCycleTime;
    } else
      return 0;
  }

  public long getAverageCycleTimeNanos() {
    if (f_renderCount != 0) {
      return (f_totalRenderTime) / f_renderCount;
    } else
      return 0;
  }

  public void shutdown() {
    f_on.getHolder().removeCallback(this);

    f_target.renderShutdown();
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    // Nothing to do
  }

  public void surfaceCreated(SurfaceHolder holder) {
    f_target.renderSetup(f_on);
    f_ts.addPostTickListener(f_postTick);
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    f_ts.removePostTickListener(f_postTick);
  }
}
