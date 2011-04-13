package org.jdesktop.core.animation.timing;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jdesktop.core.animation.i18n.I18N;

import com.surelogic.ThreadSafe;

/**
 * The abstract base class of all triggers. It contains two public methods,
 * {@link #disarm()} which can be used to disable a trigger and
 * {@link #isArmed()} which checks if a trigger is armed.
 * <p>
 * This abstract class should be overridden by any class wanting to implement a
 * new trigger. The subclass will define the events to trigger off of and any
 * listeners to handle those events. The subclass will call
 * {@link #fire(TriggerEvent)} to start, or trigger, the animation based on an
 * event that occurred.
 * <p>
 * The trigger may be setup to auto-reverse the animation. This reverses the
 * running animation when the opposite event to the trigger event occurs. The
 * opposite event is obtained by invoking
 * {@link TriggerEvent#getOppositeEvent()} on the trigger event. If the
 * animation is not running an the opposite event occurs the animation is
 * started in reverse.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@ThreadSafe
public abstract class Trigger {

  /**
   * Flags if this trigger has been disarmed. Any call to
   * {@link #fire(TriggerEvent)} will immediately return without doing anything.
   */
  private final AtomicBoolean f_disarmed = new AtomicBoolean(false);

  /**
   * The animation that is triggered, must be non-{@code null}.
   */
  private final Animator f_animator;

  /**
   * The particular event that triggers the animation, a value of {@code null}
   * indicates that any event fires the trigger
   */
  private final TriggerEvent f_triggerEvent;

  /**
   * The opposite event to {@link #f_triggerEvent} that causes the animation to
   * be reversed, or started in reverse. A value of {@code null} indicates that
   * no event reverses the animation.
   */
  private final TriggerEvent f_oppositeEvent;

  /**
   * Creates a trigger that will start the animator when
   * {@link #fire(TriggerEvent)} is called with an event that equals the passed
   * trigger event. If the passed trigger event is {@code null} then the
   * animation will be started each time {@link #fire(TriggerEvent)} is called.
   * <p>
   * The trigger may be setup to auto-reverse the animation via the
   * <tt>autoReverse</tt> flag. This reverses the running animation when the
   * opposite event to <tt>triggerEvent</tt> occurs. The opposite event is
   * obtained by invoking {@link TriggerEvent#getOppositeEvent()} on
   * <tt>triggerEvent</tt>. If the animation is not running an the opposite
   * event occurs the animation is started in reverse.
   * 
   * @param animator
   *          the animation that will start when the trigger is fired.
   * @param triggerEvent
   *          the trigger event that causes this trigger to fire. A value of
   *          {@code null} indicates that any event causes the trigger to fire.
   * @param autoReverse
   *          {@code true} if the animation should be reversed on opposite
   *          trigger events, {@code false} otherwise. If <tt>triggerEvent</tt>
   *          is {@code null}, this value must be {@code false}.
   * 
   * @throws IllegalArgumentException
   *           if <tt>animator</tt> is {@code null} or if <tt>triggerEvent</tt>
   *           is {@code null} and <tt>autoReverse</tt> is {@code true}.
   * 
   * @see TriggerEvent#getOppositeEvent()
   */
  protected Trigger(Animator animator, TriggerEvent triggerEvent, boolean autoReverse) {
    if (animator == null)
      throw new IllegalArgumentException(I18N.err(1, "animator"));
    f_animator = animator;
    f_triggerEvent = triggerEvent;
    if (triggerEvent == null && autoReverse)
      throw new IllegalArgumentException(I18N.err(40));
    if (autoReverse)
      f_oppositeEvent = f_triggerEvent.getOppositeEvent();
    else
      f_oppositeEvent = null;
  }

  /**
   * This method disables this trigger.
   */
  public void disarm() {
    f_disarmed.set(true);
  }

  /**
   * Gets if this trigger is armed.
   * 
   * @return {@code true} indicates that this trigger is armed, {@code false}
   *         indicates that it has been disarmed with a call to
   *         {@link #disarm()}.
   */
  public boolean isArmed() {
    return !f_disarmed.get();
  }

  /**
   * Called by subclasses to trigger the animation. If the trigger has been
   * disarmed nothing happens. If this trigger fires on any event, then the
   * animation is started&mdash;or stopped and started if it is running. If the
   * passed event matches the trigger event, then the animation is
   * started&mdash;or stopped and started if it is running. If the passed event
   * matches the event opposite the trigger event and auto-reversing was
   * requested, then the animation is reversed if it is running or started in
   * reverse if it is not running. succeeded
   * 
   * @param event
   *          the {@link TriggerEvent} that just occurred, may be {@code null}
   *          if it is known that this trigger fires on any event.
   */
  protected final void fire(TriggerEvent event) {
    if (f_disarmed.get())
      return;

    if (f_triggerEvent == null || f_triggerEvent == event) {
      /*
       * Trigger event occurred - fire/re-fire the animation.
       */
      fireAnimation();
    } else if (f_oppositeEvent == event) {
      /*
       * Opposite event occurred - reverse the animation if it is running, or
       * start it in reverse if it is not running.
       */
      fireReverseAnimation();
    }
  }

  private final void fireAnimation() {
    f_animator.stop();
    f_animator.start();
  }

  private final void fireReverseAnimation() {
    final boolean reverseSucceeded = f_animator.reverseNow();
    if (!reverseSucceeded)
      f_animator.startReverse();
  }
}
