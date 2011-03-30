package org.jdesktop.swt.animation.timing.sources;

import org.eclipse.swt.widgets.Display;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.TimingSource.TickListenerNotificationContext;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;

/**
 * A tick listener notification context that ensures that the thread context of
 * notifications to a using {@link TimingSource} object's {@link TickListener}
 * objects is the SWT UI thread.
 * <p>
 * This class is intended to be used to make an instance of
 * {@link ScheduledExecutorTimingSource} perform tick notifications within the
 * SWT UI thread. A typical use of this class for this purpose, where {@code tl}
 * is a {@link TickListener} object, would be
 * 
 * <pre>
 * TimingSource ts = new ScheduledExecutorTimingSource(new SWTNotificationContext(), 15, TimeUnit.MILLISECONDS);
 * ts.init(); // starts the timer
 * 
 * ts.addListener(tl); // tl gets tick notifications in the SWT UI thread
 * 
 * ts.removeListener(tl); // tl stops getting notifications
 * 
 * ts.dispose(); // done using ts
 * </pre>
 * 
 * @author Tim Halloran
 */
public final class SWTNotificationContext implements TickListenerNotificationContext {

  @Override
  public void notifyTickListeners(final TimingSource source) {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        /*
         * Notify listeners within the thread context of the Swing EDT.
         */
        source.notifyTickListeners();
      }
    });
  }
}
