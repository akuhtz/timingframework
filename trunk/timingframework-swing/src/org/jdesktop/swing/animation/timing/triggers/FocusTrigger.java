package org.jdesktop.swing.animation.timing.triggers;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.triggers.FocusTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.Trigger;

import com.surelogic.ThreadSafe;

/**
 * FocusTrigger handles focus events and triggers an animation based on those
 * events. For example, to have {@code anim} start when component receives an IN
 * event, one might write the following:
 * 
 * <pre>
 * FocusTrigger trigger = FocusTrigger.addTrigger(component, anim, FocusTriggerEvent.IN);
 * </pre>
 * 
 * @author Chet Haase
 */
@ThreadSafe
public class FocusTrigger extends Trigger implements FocusListener {

  /**
   * Creates a non-auto-reversing {@link FocusTrigger} and adds it as a
   * {@link FocusListener} to the component.
   * 
   * @param component
   *          component that will generate focus events for this trigger.
   * @param animator
   *          the animation that will start when the event occurs.
   * @param event
   *          the event that will cause the action to fire.
   * @return FocusTrigger the resulting trigger
   */
  public static FocusTrigger addTrigger(JComponent component, Animator animator, FocusTriggerEvent event) {
    return addTrigger(component, animator, event, false);
  }

  /**
   * Creates a {@link FocusTrigger} and adds it as a {@link FocusListener} to
   * the component.
   * 
   * @param component
   *          component that will generate focus events for this trigger.
   * @param animator
   *          the animation that will start when the event occurs.
   * @param event
   *          the event that will cause the action to fire.
   * @param autoReverse
   *          flag to determine whether the animator should stop and reverse
   *          based on opposite triggerEvents.
   * @return FocusTrigger the resulting trigger
   */
  public static FocusTrigger addTrigger(JComponent component, Animator animator, FocusTriggerEvent event, boolean autoReverse) {
    FocusTrigger trigger = new FocusTrigger(animator, event, autoReverse);
    component.addFocusListener(trigger);
    return trigger;
  }

  /**
   * Creates a non-auto-reversing {@link FocusTrigger}, which should be added to
   * a component that will generate the focus events of interest.
   * 
   * @param animator
   *          the animation that will start when the event occurs.
   * @param event
   *          the event that will cause the action to fire.
   */
  public FocusTrigger(Animator animator, FocusTriggerEvent event) {
    this(animator, event, false);
  }

  /**
   * Creates a {@link FocusTrigger}, which should be added to a component that
   * will generate the focus events of interest.
   * 
   * @param animator
   *          the animation that will start when the event occurs.
   * @param event
   *          the event that will cause the action to fire.
   * @param autoReverse
   *          flag to determine whether the animator should stop and reverse
   *          based on opposite triggerEvents.
   */
  public FocusTrigger(Animator animator, FocusTriggerEvent event, boolean autoReverse) {
    super(animator, event, autoReverse);
  }

  public void focusGained(FocusEvent e) {
    fire(FocusTriggerEvent.IN);
  }

  public void focusLost(FocusEvent e) {
    fire(FocusTriggerEvent.OUT);
  }
}
