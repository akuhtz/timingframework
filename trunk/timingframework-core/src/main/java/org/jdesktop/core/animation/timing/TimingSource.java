package org.jdesktop.core.animation.timing;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import com.surelogic.ThreadSafe;
import com.surelogic.Vouch;

/**
 * This class provides provides a base implementation for arbitrary timers that
 * may be used with the Timing Framework.
 * <p>
 * A timing source needs to "tick" at some regular period of time. This period,
 * if it is variable for a particular implementation, should be specified to the
 * subclass's constructor.
 * <p>
 * A timer notifies a registered set of {@link TickListener}s that a tick of
 * time has elapsed. In addition, a timer notifies a set of
 * {@link PostTickListener}s after the registered set of {@link TickListener}s
 * has been notified. For example, a {@link PostTickListener} could be used to
 * call <tt>repaint()</tt> after a large number of animations (which register
 * themselves as {@link TickListener}s) have updated the program's state. One
 * time tasks can be queued to be run at the next tick of time (before any other
 * listeners are called) using {@link #submit(Runnable)}.
 * <p>
 * A timer should begin ticking after {@link #init()} is called and should be
 * stopped and disposed after {@link #dispose()} is called. The timer cannot be
 * restarted after {@link #dispose()} is called.
 * <p>
 * A timing source implementation should document the thread context in which it
 * makes calls to registered listeners.
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
   * {@link TimingSource#addPostTickListener(PostTickListener)} method.
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
   * Holds "one shot" tasks to be run on the next tick.
   */
  private final ConcurrentLinkedQueue<Runnable> f_oneShotQueue = new ConcurrentLinkedQueue<Runnable>();

  /**
   * Runs the passed task in the thread context of this timing source. The task
   * is not run immediately but, rather, it is run at the next tick of time
   * (before calling any {@link TickListener}s). In particular, this method will
   * not block for execution of the task and the task will not execute until
   * after {@link #init()} has not been called.
   * <p>
   * The task is wrapped, via {@link WrappedRunnable}, to log an error if it
   * fails due to an unhandled exception.
   * <p>
   * This method is used to execute a snippet of code in the thread context used
   * for {@link TickListener}s and {@link PostTickListener}s.
   * 
   * @param task
   *          a task.
   * 
   * @see WrappedRunnable
   */
  public final void submit(Runnable task) {
    if (task == null)
      return;
    final WrappedRunnable wrapped = new WrappedRunnable(task);
    f_oneShotQueue.add(wrapped);
  }

  /**
   * A task that will, when its <tt>run()</tt> is invoked, perform the following
   * actions in the listed order:
   * <ol>
   * <li>Execute all queued "one shot" tasks.</li>
   * <li>Notify all registered {@link TickListener}s</li>
   * <li>Notify all registered {@link PostTickListener}s</li>
   * </ol>
   */
  @Vouch("ThreadSafe")
  private final Runnable f_perTickTask = new WrappedRunnable(new Runnable() {
    public void run() {
      while (true) {
        final Runnable task = f_oneShotQueue.poll();
        if (task == null)
          break;
        task.run();
      }
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
  });

  /**
   * Used by implementations to obtain a {@link Runnable} that will, when its
   * <tt>run()</tt> is invoked, perform the following actions in the listed
   * order:
   * <ol>
   * <li>Execute all queued "one shot" tasks.</li>
   * <li>Notify all registered {@link TickListener}s</li>
   * <li>Notify all registered {@link PostTickListener}s</li>
   * </ol>
   * A typical implementation will invoke
   * <tt>getNotifyTickListenersTask().run()</tt> when its particular timer calls
   * back each tick of time. It is critical that the above code is run within
   * the <i>correct thread context of the timing source</i> implementation.
   * 
   * @return the tick listener notification task.
   */
  protected Runnable getPerTickTask() {
    return f_perTickTask;
  }
}
