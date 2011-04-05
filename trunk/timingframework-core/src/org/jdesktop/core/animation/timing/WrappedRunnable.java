package org.jdesktop.core.animation.timing;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.core.animation.i18n.I18N;

/**
 * Wraps a task and provides logging if that task fails due to an unhandled
 * exception.
 * <p>
 * If you have a {@link Runnable} instance <tt>r</tt> you could wrap it with
 * code similar to the following snippet.
 * 
 * <pre>
 * ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
 * executor.submit(new WrappedRunnable(r));
 * </pre>
 * 
 * This would ensure that if an exception is thrown that it will be logged.
 * 
 * @author Tim Halloran
 * 
 */
public class WrappedRunnable implements Runnable {

  /**
   * Wraps the passed task and logs to {@link Logger#getAnonymousLogger()} if
   * that task fails due to an unhandled exception.
   * 
   * @param task
   *          a task.
   */
  public WrappedRunnable(Runnable task) {
    if (task == null)
      throw new IllegalArgumentException(I18N.err(1, " task"));
    f_task = task;
  }

  /**
   * The wrapped task,
   */
  private final Runnable f_task;

  @Override
  public void run() {
    try {
      f_task.run();
    } catch (Exception e) {
      Logger.getAnonymousLogger().log(Level.SEVERE, I18N.err(4, e.getClass().getSimpleName()), e);
    }
  }
}
