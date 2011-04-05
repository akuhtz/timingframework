package org.jdesktop.swing.animation.timing.sources;

import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.TimingSource.TickListenerNotificationContext;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;

/**
 * A tick listener notification context that ensures that the thread context of
 * notifications to a using {@link TimingSource} object's {@link TickListener}
 * objects is the Swing EDT.
 * <p>
 * This class is intended to be used to make an instance of
 * {@link ScheduledExecutorTimingSource} perform tick notifications within the
 * Swing EDT. A typical use of this class for this purpose, where {@code tl} is
 * a {@link TickListener} object, would be
 * 
 * <pre>
 * TimingSource ts = new ScheduledExecutorTimingSource(new SwingNotificationContext(), 15, TimeUnit.MILLISECONDS);
 * ts.init(); // starts the timer
 * 
 * ts.addListener(tl); // tl gets tick notifications in the Swing EDT
 * 
 * ts.removeListener(tl); // tl stops getting notifications
 * 
 * ts.dispose(); // done using ts
 * </pre>
 * 
 * @author Tim Halloran
 */
public final class SwingNotificationContext implements TickListenerNotificationContext {

  @Override
  public void notifyTickListenersInContext(final TimingSource source) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        /*
         * Notify listeners within the thread context of the Swing EDT.
         */
        source.notifyTickListeners();
      }
    });
  }

  @Override
  public void runInContext(final Runnable task) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        /*
         * Run the task within the thread context of the Swing EDT.
         */
        task.run();
      }
    });
  }
}
