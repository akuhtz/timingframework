package org.jdesktop.android.animation.timing.sources;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.TimingSource;

import android.app.Activity;
import android.os.Handler;

import com.surelogic.ThreadSafe;
import com.surelogic.Vouch;

@ThreadSafe
public final class AndroidTimingSource extends TimingSource {

  /**
   * Flags that no ideal time for the next callback to the periodic task
   * {@link #f_periodic} declared below has been setup. I.e., the task just
   * started running.
   */
  static private final long NONE = -1;

  private final long f_periodNanos;
  private final AtomicBoolean f_running = new AtomicBoolean(true);
  @Vouch("ThreadSafe")
  private final Activity f_activity;

  /**
   * Handler for the periodic task {@link #f_periodic} declared below.
   * <p>
   * This state is thread confined to the Android UI thread.
   */
  @Vouch("ThreadSafe")
  private Handler f_handler;

  @Vouch("ThreadSafe")
  private final Runnable f_periodic = new Runnable() {

    /**
     * This represents a relative {@link System#nanoTime()} at which the next
     * callback in the future to this periodic task should occur.
     * <p>
     * This state is thread confined to the Android UI thread.
     */
    private long f_ideaNextTickNanoTime = NONE;

    public void run() {
      if (f_running.get()) {
        getNotifyTickListenersTask().run();
        final long now = System.nanoTime();
        final long delayNanos;
        if (f_ideaNextTickNanoTime != NONE) {
          final long delayUntilNext = f_ideaNextTickNanoTime - now;
          if (delayUntilNext > 0)
            delayNanos = delayUntilNext;
          else
            delayNanos = 0;
          f_ideaNextTickNanoTime += f_periodNanos;
        } else {
          delayNanos = f_periodNanos;
          f_ideaNextTickNanoTime = now + f_periodNanos;
        }
        long delayMillis = TimeUnit.NANOSECONDS.toMillis(delayNanos);
        if (delayMillis > 0) {
          f_handler.postDelayed(f_periodic, delayMillis);
        } else {
          f_handler.post(f_periodic);
        }
      }
    }
  };

  /**
   * If the period passed is less than 1 millisecond it will be increased to 1
   * millisecond.
   * 
   * @param period
   * @param unit
   * @param activity
   */
  public AndroidTimingSource(long period, TimeUnit unit, final Activity activity) {
    final long one = TimeUnit.MILLISECONDS.toNanos(1);
    long periodNanos = unit.toNanos(period);
    if (periodNanos < one)
      periodNanos = one;
    f_periodNanos = periodNanos;
    if (activity == null)
      throw new IllegalArgumentException(I18N.err(1, "activity"));
    f_activity = activity;
  }

  @Override
  public void init() {
    f_activity.runOnUiThread(new Runnable() {
      public void run() {
        f_handler = new Handler();
        f_handler.post(f_periodic);
      }
    });
  }

  @Override
  public void dispose() {
    f_running.set(false);
  }

  @Override
  protected void runTaskInThreadContext(Runnable task) {
    f_activity.runOnUiThread(task);
  }
}
