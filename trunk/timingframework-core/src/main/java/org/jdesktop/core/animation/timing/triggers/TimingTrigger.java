package org.jdesktop.core.animation.timing.triggers;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.Trigger;

import com.surelogic.Immutable;
import com.surelogic.ThreadSafe;
import com.surelogic.Utility;

/**
 * A trigger that starts an animation when an animation timing event occurs in
 * another animation. This class can be useful in sequencing different
 * animations. For example, one {@link Animator} can be set to start when
 * another ends using this trigger. For example, to have <tt>anim2</tt> start
 * when <tt>anim1</tt> ends, one might write the following:
 * 
 * <pre>
 * Trigger trigger = TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP);
 * </pre>
 * 
 * The returned trigger object can be safely ignored if the code never needs to
 * disarm the trigger.
 * 
 * <pre>
 * TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP);
 * </pre>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
@Utility
public final class TimingTrigger {

  /**
   * Creates a non-auto-reversing timing trigger and adds it as a target to the
   * source animation. For example, one {@link Animator} can be set to start
   * when another ends using this trigger. For example, to have <tt>anim2</tt>
   * start when <tt>anim1</tt> ends, one might write the following:
   * 
   * <pre>
   * Trigger trigger = TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP);
   * </pre>
   * 
   * @param source
   *          the animation that will be listened to for events to start the
   *          target animation.
   * @param target
   *          the animation that will start when the event occurs.
   * @param event
   *          the {@link TimingTriggerEvent} on <tt>source</tt> that will cause
   *          <tt>target</tt> to start.
   * @return the resulting trigger.
   * 
   * @throws IllegalArgumentException
   *           if any of the parameters is {@code null}.
   */
  public static Trigger addTrigger(Animator source, Animator target, TimingTriggerEvent event) {
    return addTrigger(source, target, event, false);
  }

  /**
   * Creates a timing trigger and adds it as a target to the source animation.
   * For example, one {@link Animator} can be set to start when another ends and
   * visa versa using this trigger. For example, to have <tt>anim2</tt> start
   * when <tt>anim1</tt> ends and visa versa, have <tt>anim2</tt> stop when
   * <tt>anim1</tt> starts, one might write the following:
   * 
   * <pre>
   * Trigger trigger = TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP, true);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP, true);
   * </pre>
   * 
   * @param source
   *          the animation that will be listened to for events to start the
   *          target animation.
   * @param target
   *          the animation that will start when the event occurs.
   * @param event
   *          the {@link TimingTriggerEvent} on <tt>source</tt> that will cause
   *          <tt>target</tt> to start.
   * @param autoReverse
   *          {@code true} if the animation should be reversed on opposite
   *          trigger events, {@code false} otherwise.
   * @return the resulting trigger.
   * 
   * @throws IllegalArgumentException
   *           if any of the parameters is {@code null}.
   */
  public static Trigger addTrigger(Animator source, Animator target, TimingTriggerEvent event, boolean autoReverse) {
    if (source == null)
      throw new IllegalArgumentException(I18N.err(1, "source"));
    if (target == null)
      throw new IllegalArgumentException(I18N.err(1, "target"));
    if (event == null)
      throw new IllegalArgumentException(I18N.err(1, "event"));
    final TimingTriggerHelper trigger = new TimingTriggerHelper(source, target, event, autoReverse);
    trigger.init();
    return trigger;
  }

  private TimingTrigger() {
    throw new AssertionError();
  }

  @ThreadSafe
  private static final class TimingTriggerHelper extends AbstractTrigger implements TimingTarget {

    private final Animator f_source;

    public TimingTriggerHelper(Animator source, Animator target, TimingTriggerEvent event, boolean autoReverse) {
      super(target, event, autoReverse);
      f_source = source;
    }

    public void init() {
      f_source.addTarget(this);
    }

    @Override
    public void disarm() {
      super.disarm();
      f_source.removeTarget(this);
    }

    //
    // TimingTarget implementation methods
    //

    public void begin(Animator source) {
      fire(TimingTriggerEvent.START);
    }

    public void end(Animator source) {
      fire(TimingTriggerEvent.STOP);
    }

    public void repeat(Animator source) {
      fire(TimingTriggerEvent.REPEAT);
    }

    public void reverse(Animator source) {
      fire(TimingTriggerEvent.REVERSE);
    }

    public void timingEvent(Animator source, double fraction) {
      // Nothing to do
    }
  };
}
