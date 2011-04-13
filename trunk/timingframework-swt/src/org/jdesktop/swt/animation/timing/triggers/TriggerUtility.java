package org.jdesktop.swt.animation.timing.triggers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Trigger;
import org.jdesktop.core.animation.timing.triggers.TimingTrigger;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;

import com.surelogic.Immutable;
import com.surelogic.Utility;

/**
 * A utility that creates triggers for SWT applications and Eclipse plug-ins.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
@Utility
public final class TriggerUtility {

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
   * 
   * @see TimingTrigger
   */
  public static Trigger addTimingTrigger(Animator source, Animator target, TimingTriggerEvent event) {
    return TimingTrigger.addTrigger(source, target, event);
  }

  /**
   * Creates a timing trigger and adds it as a target to the source animation.
   * For example, one {@link Animator} can be set to start when another ends
   * using this trigger. For example, to have <tt>anim2</tt> start when
   * <tt>anim1</tt> ends and visa versa, have <tt>anim2</tt> stop when
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
   * 
   * @see TimingTrigger
   */
  public static Trigger addTimingTrigger(Animator source, Animator target, TimingTriggerEvent event, boolean autoReverse) {
    return TimingTrigger.addTrigger(source, target, event, autoReverse);
  }

  /**
   * Creates an event trigger and adds it as a {@link Listener} to the passed
   * SWT widget. For example, to have {@code anim} start when a button is
   * clicked, one might write the following:
   * 
   * <pre>
   * Trigger trigger = TriggerUtility.addTrigger(button, SWT.Selection, anim);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TriggerUtility.addTrigger(button, SWT.Selection, anim);
   * </pre>
   * 
   * @param widget
   *          an SWT widget that will be used as an event source for this
   *          trigger.
   * @param eventType
   *          the type of event to listen for.
   * @param target
   *          the animation that will start when the event occurs.
   * @return the resulting trigger.
   * 
   * @see SWT
   * @see Widget#addListener(int, Listener)
   */
  public static Trigger addEventTrigger(Widget widget, int eventType, Animator target) {
    final EventTriggerHelper trigger = new EventTriggerHelper(widget, eventType, target);
    trigger.init();
    return trigger;
  }

  private static final class EventTriggerHelper extends Trigger implements Listener {

    private final Widget f_widget;
    private final int f_eventType;

    protected EventTriggerHelper(Widget widget, int eventType, Animator target) {
      super(target, null, false);
      f_widget = widget;
      f_eventType = eventType;
    }

    public void init() {
      f_widget.addListener(f_eventType, this);
    }

    @Override
    public void disarm() {
      super.disarm();
      f_widget.removeListener(f_eventType, this);
    }

    @Override
    public void handleEvent(Event event) {
      fire(null);
    }
  }

  private TriggerUtility() {
    throw new AssertionError();
  }
}
