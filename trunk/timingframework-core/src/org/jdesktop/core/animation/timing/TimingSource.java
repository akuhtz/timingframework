package org.jdesktop.core.animation.timing;

import java.util.concurrent.CopyOnWriteArraySet;

import com.surelogic.ThreadSafe;

/**
 * This class provides provides a base implementation for arbitrary timers that
 * may be used with the Timing Framework.
 * <p>
 * A timer needs to "tick" at some regular period of time. This period, if it is
 * variable for a particular implementation, should be specified to the
 * subclass's constructor.
 * <p>
 * A timer notifies a registered set of {@link TickListener}s that a tick of
 * time has elapsed. In addition, a timer notifies a set of
 * {@link PostTickListener}s after the registered set of {@link TickListener}s
 * has been notified. For example, a {@link PostTickListener} could be used to
 * call <tt>repaint()</tt> after a large number of animations (which register
 * themselves as {@link TickListener}s) have updated the program's state.
 * <p>
 * A timer should begin ticking after {@link #init()} is called and should be
 * stopped and disposed after {@link #dispose()} is called. The timer cannot be
 * restarted after {@link #dispose()} is called.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@ThreadSafe
public abstract class TimingSource {

  /**
   * This interface is implemented by any object wishing to receive "tick"
   * events from a {@link TimingSource} object. The {@link TickListener}
   * implementation is registered as a listener of the {@link TimingSource} via
   * the {@link TimingSource#addTickListener(TickListener)} method.
   * <p>
   * Animations register themselves as a {@link TickListener}.
   */
  public interface TickListener {

    /**
     * This method is called by the {@link TimingSource} object while the timer
     * is running. It indicates a tick of time has passed.
     * 
     * @param source
     *          the object that invoked this call.
     * @param nanoTime
     *          the current value of the most precise available system timer, in
     *          nanoseconds.
     */
    public void timingSourceTick(TimingSource source, long nanoTime);
  }

  /**
   * This interface is implemented by any object wishing to receive "tick"
   * events from a {@link TimingSource} object after all registered
   * {@link TickListener} objects have been notified. The
   * {@link PostTickListener} implementation is registered as a listener of the
   * {@link TimingSource} via the
   * {@link TimingSource#addPostTickListener(TickListener)} method.
   * <p>
   * For example, a {@link PostTickListener} could be used to call
   * <tt>repaint()</tt> after a large number of animations (which register
   * themselves as {@link TickListener}s) have updated the program's state.
   */
  public interface PostTickListener {

    /**
     * This method is called by the {@link TimingSource} object while the timer
     * is running. It indicates a tick of time has passed and that all
     * registered {@link TickListener} objects have been notified.
     * 
     * @param source
     *          the object that invoked this call.
     * @param nanoTime
     *          the current value of the most precise available system timer, in
     *          nanoseconds.
     */
    public void timingSourcePostTick(TimingSource source, long nanoTime);
  }

  /**
   * Starts up the timing source.
   */
  public abstract void init();

  /**
   * Stops the timing source and disposes of its resources.
   */
  public abstract void dispose();

  /**
   * Submits a task to be run in the thread context of this timing source.
   * <p>
   * Implementers should check the the correct thread context of this call
   * before queuing the task for later execution. If <tt>task.run()</tt> can be
   * directly invoked this is preferred.
   * 
   * @param task
   *          a task.
   */
  protected abstract void runTaskInThreadContext(Runnable task);

  /**
   * Listeners that will receive "tick" events.
   */
  private final CopyOnWriteArraySet<TickListener> f_tickListeners = new CopyOnWriteArraySet<TickListener>();

  /**
   * Adds a {@link TickListener} to the set of listeners that receive timing
   * events from this {@link TimingSource}. Has no effect if the listener has
   * already been added.
   * 
   * @param listener
   *          the listener to be added.
   */
  public final void addTickListener(TickListener listener) {
    if (listener == null)
      return;
    f_tickListeners.add(listener);
  }

  /**
   * Removes a {@link TickListener} from the set of listeners that receive
   * timing events from this {@link TimingSource}. Has no effect if the listener
   * is not in the set of listeners.
   * 
   * @param listener
   *          the listener to be removed.
   */
  public final void removeTickListener(TickListener listener) {
    f_tickListeners.remove(listener);
  }

  /**
   * Listeners that will receive "tick" events after all the registered
   * {@link TickListener}s have been notified.
   */
  private final CopyOnWriteArraySet<PostTickListener> f_postTickListeners = new CopyOnWriteArraySet<PostTickListener>();

  /**
   * Adds a {@link PostTickListener} to the set of listeners that receive timing
   * events from this {@link TimingSource}. Has no effect if the listener has
   * already been added.
   * 
   * @param listener
   *          the listener to be added.
   */
  public final void addPostTickListener(PostTickListener listener) {
    if (listener == null)
      return;
    f_postTickListeners.add(listener);
  }

  /**
   * Removes a {@link PostTickListener} from the set of listeners that receive
   * timing events from this {@link TimingSource}. Has no effect if the listener
   * is not in the set of listeners.
   * 
   * @param listener
   *          the listener to be removed.
   */
  public final void removePostTickListener(PostTickListener listener) {
    f_postTickListeners.remove(listener);
  }

  /**
   * A task to notify registered {@link TickListener} and
   * {@link PostTickListener} objects that a tick of time has elapsed.
   */
  private final Runnable f_notifyTickListenersTask = new Runnable() {
    public void run() {
      final long nanoTime = System.nanoTime();
      if (!f_tickListeners.isEmpty())
        for (TickListener listener : f_tickListeners) {
          listener.timingSourceTick(TimingSource.this, nanoTime);
        }
      if (!f_postTickListeners.isEmpty())
        for (PostTickListener listener : f_postTickListeners) {
          listener.timingSourcePostTick(TimingSource.this, nanoTime);
        }
    }
  };

  /**
   * Used by implementations to directly execute the registered listeners rather
   * than calling {@link #notifyTickListeners()}.
   * 
   * @return the tick listener notification task.
   */
  protected Runnable getNotifyTickListenersTask() {
    return f_notifyTickListenersTask;
  }

  /**
   * This method notifies this object's {@link TickListener}s and, subsequently,
   * {@link PostTickListener}s that a tick of time has elapsed.
   * <p>
   * Calls will be made in the thread context of this timing source.
   * 
   * @see TickListenerNotificationContext#notifyTickListeners(TimingSource)
   */
  public final void notifyTickListeners() {
    runTaskInThreadContext(f_notifyTickListenersTask);
  }

  /**
   * Runs the passed task through the object's
   * {@link TickListenerNotificationContext}. The task is wrapped, via
   * {@link WrappedRunnable}, to log if it fails due to an unhandled exception.
   * <p>
   * This method can be used to execute a snippet of code in the thread context
   * used to callback to {@link TimingSource.TickListener} and
   * {@link TimingSource.PostTickListener}.
   * 
   * @param task
   *          a task.
   * 
   * @see WrappedRunnable
   */
  public final void contextAwareRunTask(Runnable task) {
    if (task == null)
      return;
    final WrappedRunnable wrapped = new WrappedRunnable(task);
    runTaskInThreadContext(wrapped);
  }
}
