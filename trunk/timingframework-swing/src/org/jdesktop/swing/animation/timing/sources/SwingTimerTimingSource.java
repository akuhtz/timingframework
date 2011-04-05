package org.jdesktop.swing.animation.timing.sources;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jdesktop.core.animation.timing.TimingSource;

/**
 * An implementation of {@link TimingSource} using a Swing {@link Timer}. This
 * implementation ensures that calls to registered {@code TickListener} and
 * {@code PostTickListener}objects are always made within the thread context of
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
 * @author Tim Halloran
 */
public final class SwingTimerTimingSource extends TimingSource {

  private final Timer f_timer;

  /**
   * javax.swing.Timer
   * 
   * 
   * Constructs a new instance. The {@link #init()} must be called on the new
   * instance to start the timer. The {@link #dispose()} method should be called
   * to stop the timer.
   * <p>
   * The Swing timer requires a period of at least 1 millisecond. If the period
   * passed is smaller it is rounded up to 1 millisecond.
   * 
   * @param period
   *          the period of time between "tick" events.
   * @param unit
   *          the time unit of period parameter.
   */
  public SwingTimerTimingSource(long period, TimeUnit unit) {
    int periodMillis = (int) unit.toMillis(period);
    if (periodMillis != unit.toMillis(period))
      periodMillis = 1;
    f_timer = new Timer(periodMillis, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        getNotifyTickListenersTask().run();
      }
    });
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
  protected void runTaskInThreadContext(final Runnable task) {
    if (SwingUtilities.isEventDispatchThread()) {
      task.run();
    } else {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          task.run();
        }
      });
    }
  }
}
