package org.jdesktop.swt.animation.timing.sources;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
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
 * TimingSource ts = new SWTTimingSource(15, TimeUnit.MILLISECONDS);
 * ts.init(); // starts the timer
 * 
 * ts.addTickListener(tl); // tl gets tick notifications in the SWT UI thread
 * 
 * ts.removeTickListener(tl); // tl stops getting notifications
 * 
 * ts.dispose(); // done using ts
 * </pre>
 * 
 * @author Tim Halloran
 */
public final class SWTTimingSource extends TimingSource {

  private final int f_periodMillis;
  private final AtomicBoolean f_running = new AtomicBoolean(true);
  private final AtomicReference<Display> f_display = new AtomicReference<Display>();

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
  final Runnable f_periodic = new Runnable() {
    public void run() {
      if (f_running.get()) {
        contextAwareNotifyTickListeners();
        f_display.get().timerExec(f_periodMillis, f_periodic);
      }
    }
  };

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
   * @param display
   *          the display that manages the connection between SWT timing and the
   *          underlying operating system. May be {@code null}, in which case
   *          {@link Display#getDefault()} is invoked when {@link #init()} is
   *          called on this timer.
   */
  public SWTTimingSource(long period, TimeUnit unit, Display display) {
    super(null);
    int periodMillis = (int) unit.toMillis(period);
    if (periodMillis != unit.toMillis(period))
      periodMillis = 1;
    f_periodMillis = periodMillis;
    if (display != null)
      f_display.set(display);
  }

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
  public SWTTimingSource(long period, TimeUnit unit) {
    this(period, unit, null);
  }

  @Override
  public void init() {
    if (f_display.get() == null) {
      f_display.set(Display.getDefault());
    }
    f_display.get().asyncExec(f_periodic);
  }

  @Override
  public void dispose() {
    f_running.set(false);
  }
}
