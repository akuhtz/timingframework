package org.jdesktop.swing.animation.timing.sources;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jdesktop.core.animation.timing.TimingSource;

import com.surelogic.ThreadSafe;
import com.surelogic.Vouch;

/**
 * A timing source based upon a Swing {@link Timer} and the
 * {@link SwingUtilities#invokeLater(Runnable)} method. This implementation
 * ensures that calls to registered {@code TickListener} and
 * {@code PostTickListener} objects are always made within the thread context of
 * the Swing EDT.
 * <p>
 * A typical use, where {@code tl} is a {@code TickListener} object, would be
 * 
 * <pre>
 * TimingSource ts = new SwingTimerTimingSource(15, TimeUnit.MILLISECONDS);
 * ts.init(); // starts the timer
 * 
 * ts.addTickListener(tl); // tl gets tick notifications in the Swing EDT
 * 
 * ts.removeTickListener(tl); // tl stops getting notifications
 * 
 * ts.dispose(); // done using ts
 * </pre>
 * 
 * If you are not sure what period to set, use the
 * {@link #SwingTimerTimingSource()} constructor which uses a reasonable default
 * value of 15 milliseconds.
 * <p>
 * Tasks submitted to {@link #submit(Runnable)} and calls to registered
 * {@code TickListener} and {@code PostTickListener} objects from this timing
 * source are always made in the context of the Swing EDT.
 * 
 * @author Tim Halloran
 */
@ThreadSafe
public final class SwingTimerTimingSource extends TimingSource {

  @Vouch("ThreadSafe")
  private final Timer f_timer;

  /**
   * Constructs a new instance. The {@link #init()} must be called on the new
   * instance to start the timer. The {@link #dispose()} method should be called
   * to stop the timer.
   * <p>
   * The Swing timer requires a period of at least 1 millisecond. If the period
   * passed is smaller, it is set to 1 millisecond. It is also not recommended
   * to pass a period much larger than a day as this could lead to integer
   * overflow in the case that the value in milliseconds cannot be represented
   * as an <tt>int</tt> to be passed to the Swing {@link Timer}.
   * 
   * @param period
   *          the period of time between "tick" events.
   * @param unit
   *          the time unit of period parameter.
   */
  public SwingTimerTimingSource(long period, TimeUnit unit) {
    int periodMillis = (int) unit.toMillis(period);
    if (periodMillis < 1)
      periodMillis = 1;
    f_timer = new Timer(periodMillis, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        getPerTickTask().run();
      }
    });
  }

  /**
   * Constructs a new instance with a period of 15 milliseconds. The
   * {@link #init()} must be called on the new instance to start the timer. The
   * {@link #dispose()} method should be called to stop the timer.
   */
  public SwingTimerTimingSource() {
    this(15, TimeUnit.MILLISECONDS);
  }

  @Override
  public void init() {
    f_timer.start();
  }

  @Override
  public void dispose() {
    f_timer.stop();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(SwingTimerTimingSource.class.getSimpleName()).append('@').append(Integer.toHexString(hashCode()));
    b.append("(period=").append(f_timer.getDelay()).append(' ').append(TimeUnit.MILLISECONDS.toString());
    b.append(')');
    return b.toString();
  }
}
