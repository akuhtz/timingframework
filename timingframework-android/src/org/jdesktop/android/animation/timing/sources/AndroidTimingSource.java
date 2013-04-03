package org.jdesktop.android.animation.timing.sources;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.TimingSource;

import android.app.Activity;
import android.os.Handler;

import com.surelogic.ThreadSafe;
import com.surelogic.Vouch;

/**
 * A timing source based upon the Android
 * {@link Activity#runOnUiThread(Runnable)} and
 * {@link Handler#postDelayed(Runnable, long)} methods. Drift correction is
 * performed by this implementation. This implementation ensures that calls to
 * registered {@code TickListener} and {@code PostTickListener} objects are
 * always made within the thread context of the Android UI thread for the passed
 * {@link Activity}.
 * <p>
 * A typical use, where {@code tl} is a {@code TickListener} object, would be
 * 
 * <pre>
 * Activity activity = ...; // Often "this"
 * TimingSource ts = new AndroidTimingSource(15, TimeUnit.MILLISECONDS, activity);
 * ts.init(); // starts the timer
 * 
 * ts.addTickListener(tl); // tl gets tick notifications in the Android UI thread
 * 
 * ts.removeTickListener(tl); // tl stops getting notifications
 * 
 * ts.dispose(); // done using ts
 * </pre>
 * 
 * If you are not sure what period to set, use the
 * {@link #AndroidTimingSource(Activity)} constructor which uses a reasonable
 * default value of 15 milliseconds.
 * <p>
 * Tasks submitted to {@link #submit(Runnable)} and calls to registered
 * {@code TickListener} and {@code PostTickListener} objects from this timing
 * source are always made in the context of the Android UI thread.
 * 
 * @author Tim Halloran
 */
@ThreadSafe
public final class AndroidTimingSource extends TimingSource {

  /**
   * Flags that no ideal time for the next callback to the periodic task
   * {@link #f_periodic} declared below has been setup. I.e., the task just
   * started running.
   */
  static private final long NONE = -1;

  final long f_periodNanos;
  final AtomicBoolean f_running = new AtomicBoolean(true);
  @Vouch("ThreadSafe")
  final Activity f_activity;

  /**
   * Handler for the periodic task {@link #f_periodic} declared below.
   * <p>
   * This state is thread confined to the Android UI thread.
   */
  @Vouch("ThreadSafe")
  Handler f_handler;

  @Vouch("ThreadSafe")
  final Runnable f_periodic = new Runnable() {

    /**
     * This represents a relative {@link System#nanoTime()} at which the next
     * callback in the future to this periodic task should occur.
     * <p>
     * This state is thread confined to the Android UI thread.
     */
    private long f_ideaNextTickNanoTime = NONE;

    public void run() {
      if (f_running.get()) {
        runPerTick();
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
   * Constructs a new instance. The {@link #init()} must be called on the new
   * instance to start the timer. The {@link #dispose()} method should be called
   * to stop the timer.
   * <p>
   * The Android timer requires a period of at least 1 millisecond. If the
   * period passed is smaller, it is set to 1 millisecond.
   * 
   * @param period
   *          the period of time between "tick" events.
   * @param unit
   *          the time unit of period parameter.
   * @param activity
   *          the {@link Activity} that manages the connection between Android
   *          timing and the underlying operating system.
   * 
   * @throws IllegalArgumentException
   *           if <tt>activity<tt> is {@code null}.
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

  /**
   * Constructs a new instance with a period of 15 milliseconds. The
   * {@link #init()} must be called on the new instance to start the timer. The
   * {@link #dispose()} method should be called to stop the timer.
   */
  public AndroidTimingSource(final Activity activity) {
    this(15, TimeUnit.MILLISECONDS, activity);
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
  public boolean isDisposed() {
    return !f_running.get();
  }
}
