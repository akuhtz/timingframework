package org.jdesktop.core.animation.timing.triggers;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Trigger;
import org.jdesktop.core.animation.timing.TriggerEvent;

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
 * animation is not running and the opposite event occurs the animation is
 * started in reverse.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@ThreadSafe(implementationOnly=true)
public abstract class AbstractTrigger implements Trigger {

  /**
   * Flags if this trigger has been disarmed. Any call to
   * {@link #fire(TriggerEvent)} will immediately return without doing anything.
   */
  private final AtomicBoolean f_disarmed = new AtomicBoolean(false);

  /**
   * The animation that is triggered, must be non-{@code null}.
   */
  private final Animator f_target;

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
   * <tt>triggerEvent</tt>. If the animation is not running and the opposite
   * event occurs the animation is started in reverse.
   * 
   * @param target
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
  protected AbstractTrigger(Animator target, TriggerEvent triggerEvent, boolean autoReverse) {
    if (target == null)
      throw new IllegalArgumentException(I18N.err(1, "target"));
    f_target = target;
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
   * disarmed nothing happens.
   * 
   * @param event
   *          the {@link TriggerEvent} that just occurred, may be {@code null}
   *          if it is known that this trigger fires on any event.
   */
  protected final void fire(TriggerEvent event) {
    if (f_disarmed.get())
      return;

    final Animator.Direction normalDirection = f_target.getStartDirection();

    if (f_triggerEvent == null || f_triggerEvent == event) {
      /*
       * Trigger event occurred - reverse the animation if it is running in the
       * opposite direction and auto-reversing is enabled, or restart the
       * animation.
       */
      if (f_oppositeEvent != null && f_target.isRunning() && f_target.getCurrentDirection() != normalDirection) {
        final boolean reverseSucceeded = f_target.reverseNow();
        if (reverseSucceeded)
          return;
      }
      f_target.stop();
      f_target.start();
    } else if (f_oppositeEvent == event) {
      /*
       * Opposite event occurred and auto-reversing is enabled - reverse the
       * animation if it is running, or restart it in reverse if it is not
       * running.
       */
      if (f_target.isRunning() && f_target.getCurrentDirection() == normalDirection) {
        final boolean reverseSucceeded = f_target.reverseNow();
        if (reverseSucceeded)
          return;
      }
      f_target.stop();
      f_target.startReverse();
    }
  }
}
