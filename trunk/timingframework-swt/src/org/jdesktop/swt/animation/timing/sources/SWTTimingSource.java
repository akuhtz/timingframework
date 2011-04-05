package org.jdesktop.swt.animation.timing.sources;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;
import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.TimingSource;

/**
 * An implementation of {@link TimingSource} for SWT. This implementation
 * ensures that calls to registered {@code TickListener} and
 * {@code PostTickListener} objects are always made within the thread context of
 * the SWT UI thread.
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
 * Calls to registered {@link TickListener} and {@link PostTickListener} objects
 * from this timing source are always made in the context of the SWT UI thread.
 * Further, any tasks submitted to {@link #submit(Runnable)} are run in the
 * thread context of the SWT UI thread as well.
 * 
 * @author Tim Halloran
 */
public final class SWTTimingSource extends TimingSource {

  private final int f_periodMillis;
  private final AtomicBoolean f_running = new AtomicBoolean(true);
  private final Display f_display;

  /**
   * Constructs a new instance. The {@link #init()} must be called on the new
   * instance to start the timer. The {@link #dispose()} method should be called
   * to stop the timer.
   * <p>
   * The SWT timer requires a period of at least 1 millisecond. If the period
   * passed is smaller it is rounded up to 1 millisecond.
   * 
   * @param period
   *          the period of time between "tick" events.
   * @param unit
   *          the time unit of period parameter.
   */
  private final Runnable f_periodic = new Runnable() {
    public void run() {
      if (f_running.get()) {
        getNotifyTickListenersTask().run();
        f_display.timerExec(f_periodMillis, f_periodic);
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
    int periodMillis = (int) unit.toMillis(period);
    if (periodMillis < 1)
      periodMillis = 1;
    f_periodMillis = periodMillis;
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
  protected void runTaskInThreadContext(final Runnable task) {
    if (task == null)
      return;
    if (Thread.currentThread().equals(f_display.getThread())) {
      task.run();
    } else {
      f_display.asyncExec(new Runnable() {
        @Override
        public void run() {
          task.run();
        }
      });
    }
  }
}
