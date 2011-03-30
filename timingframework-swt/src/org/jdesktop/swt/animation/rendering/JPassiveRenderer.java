package org.jdesktop.swt.animation.rendering;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;

public class JPassiveRenderer implements JRenderer<Canvas> {

  /*
   * Some messages if things go wrong.
   */
  private static final String E_UIT_REQUIRED = "This code must be invoked within the SWT UI thread.";
  private static final String E_NON_NULL = "%s must be non-null.";

  /*
   * Thread-confined to the SWT UI thread
   */
  private final Canvas f_on;
  private final JRendererTarget<Display, GC> f_target;
  private final TimingSource f_ts;
  private boolean f_renderingStarted = false;
  private final PostTickListener f_postTick = new PostTickListener() {
    public void timingSourcePostTick(TimingSource source, long nanoTime) {
      long now = System.nanoTime();
      if (f_renderCount != 0) {
        f_totalRenderTime += now - f_lastRenderTimeNanos;
      }
      f_lastRenderTimeNanos = now;
      f_renderCount++;
      f_target.renderUpdate();
      f_on.redraw();
    }
  };

  /*
   * Statistics counters
   */
  private long f_lastRenderTimeNanos;
  private long f_totalRenderTime = 0;
  private long f_renderCount = 0;

  public JPassiveRenderer(Canvas on, JRendererTarget<Display, GC> target, TimingSource timingSource) {
    if (on == null)
      throw new IllegalArgumentException(String.format(E_NON_NULL, "on"));
    f_on = on;

    if (!on.getDisplay().getThread().equals(Thread.currentThread()))
      throw new IllegalStateException(E_UIT_REQUIRED);

    if (target == null)
      throw new IllegalArgumentException(String.format(E_NON_NULL, "life"));
    f_target = target;

    if (timingSource == null)
      throw new IllegalArgumentException(String.format(E_NON_NULL, "timingSource"));
    f_ts = timingSource;

    f_on.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        /*
         * Check if initialization is necessary.
         */
        if (f_on.isVisible() && !f_renderingStarted) {
          f_renderingStarted = true;
          f_target.renderSetup(f_on.getDisplay());
          f_ts.addPostTickListener(f_postTick);
        }
        final GC g = e.gc;
        final int width = f_on.getBounds().width;
        final int height = f_on.getBounds().height;
        f_target.render(g, width, height);
      }
    });
  }

  @Override
  public Canvas getOn() {
    return f_on;
  }

  @Override
  public void invokeLater(Runnable task) {
    f_on.getDisplay().asyncExec(task);
  }

  @Override
  public TimingSource getTimingSource() {
    return f_ts;
  }

  @Override
  public long getFPS() {
    final long avgCycleTime = getAverageCycleTimeNanos();
    if (avgCycleTime != 0) {
      return TimeUnit.SECONDS.toNanos(1) / avgCycleTime;
    } else
      return 0;
  }

  @Override
  public long getAverageCycleTimeNanos() {
    if (f_renderCount != 0) {
      return (f_totalRenderTime) / f_renderCount;
    } else
      return 0;
  }

  @Override
  public void shutdown() {
    f_ts.removePostTickListener(f_postTick);
    f_target.renderShutdown();
  }
}
