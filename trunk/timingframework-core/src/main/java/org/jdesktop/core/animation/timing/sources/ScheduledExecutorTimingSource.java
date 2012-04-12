package org.jdesktop.core.animation.timing.sources;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jdesktop.core.animation.timing.TimingSource;

import com.surelogic.ThreadSafe;
import com.surelogic.Vouch;

/**
 * A timing source using a {@link ScheduledExecutorService} as returned from
 * {@link Executors#newSingleThreadScheduledExecutor()}.
 * <p>
 * A typical use, where {@code tl} is a {@code TickListener} object, would be
 * 
 * <pre>
 * TimingSource ts = new ScheduledExecutorTimingSource(15, TimeUnit.MILLISECONDS);
 * ts.init(); // starts the timer
 * 
 * ts.addTickListener(tl); // tl gets tick notifications
 * 
 * ts.removeTickListener(tl); // tl stops getting notifications
 * 
 * ts.dispose(); // done using ts
 * </pre>
 * 
 * If you are not sure what period to set, use the
 * {@link #ScheduledExecutorTimingSource()} constructor which uses a reasonable
 * default value of 15 milliseconds.
 * <p>
 * Tasks submitted to {@link #submit(Runnable)} and calls to registered
 * {@code TickListener} and {@code PostTickListener} objects from this timing
 * source are always made in the context of a single thread. This thread is the
 * thread created by {@link Executors#newSingleThreadScheduledExecutor()}.
 * 
 * @author Tim Halloran
 */
@ThreadSafe
public final class ScheduledExecutorTimingSource extends TimingSource {

  @Vouch("ThreadSafe")
  private final ScheduledExecutorService f_executor;
  private final long f_period;
  private final TimeUnit f_periodTimeUnit;

  /**
   * Constructs a new instance. The {@link #init()} must be called on the new
   * instance to start the timer. The {@link #dispose()} method should be called
   * to stop the timer.
   * 
   * @param period
   *          the period of time between "tick" events.
   * @param unit
   *          the time unit of period parameter.
   */
  public ScheduledExecutorTimingSource(long period, TimeUnit unit) {
    f_period = period;
    f_periodTimeUnit = unit;
    f_executor = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * Constructs a new instance with a period of 15 milliseconds. The
   * {@link #init()} must be called on the new instance to start the timer. The
   * {@link #dispose()} method should be called to stop the timer.
   */
  public ScheduledExecutorTimingSource() {
    this(15, TimeUnit.MILLISECONDS);
  }

  @Override
  public void init() {
    f_executor.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        getPerTickTask().run();
      }
    }, 0, f_period, f_periodTimeUnit);
  }

  @Override
  public void dispose() {
    f_executor.shutdown();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(ScheduledExecutorTimingSource.class.getSimpleName()).append('@').append(Integer.toHexString(hashCode()));
    b.append("(period=").append(f_period).append(' ').append(f_periodTimeUnit.toString());
    b.append(')');
    return b.toString();
  }
}
