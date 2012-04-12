package org.jdesktop.swt.animation.timing.sources;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;
import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.TimingSource;

import com.surelogic.ThreadSafe;
import com.surelogic.Vouch;

/**
 * A timing source based upon the SWT {@link Display#timerExec(int, Runnable)}
 * and {@link Display#asyncExec(Runnable)} methods. Drift correction is
 * performed by this implementation. This implementation ensures that calls to
 * registered {@code TickListener} and {@code PostTickListener} objects are
 * always made within the thread context of the SWT UI thread.
 * <p>
 * A typical use, where {@code tl} is a {@code TickListener} object, would be
 * 
 * <pre>
 * Display display = Display.getDefault();
 * TimingSource ts = new SWTTimingSource(15, TimeUnit.MILLISECONDS, display);
 * ts.init(); // starts the timer
 * 
 * ts.addTickListener(tl); // tl gets tick notifications in the SWT UI thread
 * 
 * ts.removeTickListener(tl); // tl stops getting notifications
 * 
 * ts.dispose(); // done using ts
 * </pre>
 * 
 * If you are not sure what period to set, use the
 * {@link #SWTTimingSource(Display)} constructor which uses a reasonable default
 * value of 15 milliseconds.
 * <p>
 * Tasks submitted to {@link #submit(Runnable)} and calls to registered
 * {@code TickListener} and {@code PostTickListener} objects from this timing
 * source are always made in the context of the SWT UI thread.
 * 
 * @author Tim Halloran
 */
@ThreadSafe
public final class SWTTimingSource extends TimingSource {

  /**
   * Flags that no ideal time for the next callback to the periodic task
   * {@link #f_periodic} declared below has been setup. I.e., the task just
   * started running.
   */
  static private final long NONE = -1;

  private final long f_periodNanos;
  private final AtomicBoolean f_running = new AtomicBoolean(true);
  @Vouch("ThreadSafe")
  private final Display f_display;

  @Vouch("ThreadSafe")
  private final Runnable f_periodic = new Runnable() {

    /**
     * This represents a relative {@link System#nanoTime()} at which the next
     * callback in the future to this periodic task should occur.
     * <p>
     * This state is thread confined to the SWT UI thread.
     */
    private long f_ideaNextTickNanoTime = NONE;

    public void run() {
      if (f_running.get()) {
        getPerTickTask().run();
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
          f_display.timerExec((int) delayMillis, f_periodic);
        } else {
          f_display.asyncExec(f_periodic);
        }
      }
    }
  };

  /**
   * Constructs a new instance. The {@link #init()} must be called on the new
   * instance to start the timer. The {@link #dispose()} method should be called
   * to stop the timer.
   * <p>
   * The SWT timer requires a period of at least 1 millisecond. If the period
   * passed is smaller, it is set to 1 millisecond. It is also not recommended
   * to pass a period much larger than a day as this could lead to integer
   * overflow in the case that the value in milliseconds cannot be represented
   * as an <tt>int</tt> to be passed to the
   * {@link Display#timerExec(int, Runnable)} method.
   * 
   * @param period
   *          the period of time between "tick" events.
   * @param unit
   *          the time unit of period parameter.
   * @param display
   *          the display that manages the connection between SWT timing and the
   *          underlying operating system.
   * 
   * @throws IllegalArgumentException
   *           if <tt>display<tt> is {@code null}.
   */
  public SWTTimingSource(long period, TimeUnit unit, final Display display) {
    final long one = TimeUnit.MILLISECONDS.toNanos(1);
    long periodNanos = unit.toNanos(period);
    if (periodNanos < one)
      periodNanos = one;
    f_periodNanos = periodNanos;
    if (display == null)
      throw new IllegalArgumentException(I18N.err(1, "display"));
    f_display = display;
  }

  /**
   * Constructs a new instance with a period of 15 milliseconds. The
   * {@link #init()} must be called on the new instance to start the timer. The
   * {@link #dispose()} method should be called to stop the timer.
   */
  public SWTTimingSource(final Display display) {
    this(15, TimeUnit.MILLISECONDS, display);
  }

  @Override
  public void init() {
    f_display.asyncExec(f_periodic);
  }

  @Override
  public void dispose() {
    f_running.set(false);
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(SWTTimingSource.class.getSimpleName()).append('@').append(Integer.toHexString(hashCode()));
    b.append("(period=").append(TimeUnit.NANOSECONDS.toMillis(f_periodNanos)).append(' ').append(TimeUnit.MILLISECONDS.toString());
    b.append(')');
    return b.toString();
  }
}
