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
 * The thread context of notifications can be changed by providing an
 * implementation of {@link TickListenerNotificationContext} to the constructor.
 * A default notification context, that does not change the notification thread,
 * is provided.
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
   * {@link TimingSource#addTickListener(TickListener)} method.
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
   * This interface is implemented by any object wishing to change the calling
   * thread context of notifications to this {@link TimingSource} object's
   * {@link TickListener} objects.
   */
  public interface TickListenerNotificationContext {

    /**
     * This method notifies the passed {@link TimingSource} object's
     * {@link TickListener} objects that a tick of time has elapsed. At some
     * point the implementation should ensure that it calls
     * {@link TimingSource#notifyTickListeners()} on the passed
     * {@link TimingSource} object. The default implementation is shown below.
     * 
     * <pre>
     * public void notifyTickListeners(TimingSource source) {
     *   source.notifyTickListeners();
     * }
     * </pre>
     * 
     * This default implementation notifies listeners in the thread context of
     * the {@link TimingSource} object. Other implementations could ensure that
     * listeners are notified in any thread context. For example, within the
     * Swing EDT or the SWT EDT.
     * 
     * @param source
     *          the object that invoked this call.
     */
    public void notifyTickListeners(TimingSource source);
  }

  /**
   * The context that the listeners will be notified within. A value of
   * {@code null} indicates no context change is desired.
   */
  private final TickListenerNotificationContext f_notificationContext;

  /**
   * Constructs an instance with the passed notification context.
   * 
   * @param notificationContext
   *          the context that the listeners will be notified within. A value of
   *          {@code null} uses the default notification context.
   */
  public TimingSource(TickListenerNotificationContext notificationContext) {
    f_notificationContext = notificationContext;
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
   * This method notifies this object's {@link TickListener}s and, subsequently,
   * {@link PostTickListener}s that a tick of time has elapsed.
   * <p>
   * This method should only be invoked by implementations of
   * {@link TickListenerNotificationContext}. <b>Clients should not invoke this
   * method in any other context.</b>
   * 
   * @see TickListenerNotificationContext#notifyTickListeners(TimingSource)
   */
  public final void notifyTickListeners() {
    final long nanoTime = System.nanoTime();
    if (!f_tickListeners.isEmpty())
      for (TickListener listener : f_tickListeners) {
        listener.timingSourceTick(this, nanoTime);
      }
    if (!f_postTickListeners.isEmpty())
      for (PostTickListener listener : f_postTickListeners) {
        listener.timingSourcePostTick(this, nanoTime);
      }
  }

  /**
   * This method notifies this object's {@link TickListener} objects that a tick
   * of time has elapsed by calling through the object's
   * {@link TickListenerNotificationContext}.
   * <p>
   * Implementations should invoke this method when they want to notify
   * {@link TickListener}s that a tick of time has elapsed. They should
   * <i>not</i> invoke {@link #notifyTickListeners()} directly.
   */
  protected final void contextAwareNotifyTickListeners() {
    if (f_notificationContext == null)
      notifyTickListeners();
    else
      f_notificationContext.notifyTickListeners(this);
  }
}
