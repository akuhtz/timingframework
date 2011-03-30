package org.jdesktop.swt.animation.timing.triggers;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.triggers.FocusTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.Trigger;

/**
 * {@link FocusTrigger} handles focus events and triggers an animation based on
 * those events. For example, to have {@code anim} start when component receives
 * an IN event, one might write the following:
 * 
 * <pre>
 * FocusTrigger trigger = FocusTrigger.addTrigger(control, anim, FocusTriggerEvent.IN);
 * </pre>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class FocusTrigger extends Trigger implements FocusListener {

  /**
   * Creates a non-auto-reversing {@link FocusTrigger} and adds it as a
   * {@link FocusListener} to the component.
   * 
   * @param control
   *          control that will generate focus events for this trigger
   * @param animator
   *          the Animator that will start when the event occurs
   * @param event
   *          the event that will cause the action to fire
   * @return FocusTrigger the resulting trigger
   */
  public static FocusTrigger addTrigger(Control control, Animator animator, FocusTriggerEvent event) {
    return addTrigger(control, animator, event, false);
  }

  /**
   * Creates a {@link FocusTrigger} and adds it as a {@link FocusListener} to
   * the component.
   * 
   * @param control
   *          control that will generate focus events for this trigger.
   * @param animator
   *          the animation that will start when the event occurs.
   * @param event
   *          the event that will cause the action to fire.
   * @param autoReverse
   *          flag to determine whether the animator should stop and reverse
   *          based on opposite triggerEvents.
   * @return FocusTrigger the resulting trigger
   */
  public static FocusTrigger addTrigger(Control control, Animator animator, FocusTriggerEvent event, boolean autoReverse) {
    FocusTrigger trigger = new FocusTrigger(animator, event, autoReverse);
    control.addFocusListener(trigger);
    return trigger;
  }

  /**
   * Creates a non-auto-reversing {@link FocusTrigger}, which should be added to
   * a Component that will generate the focus events of interest.
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
   * Creates a {@link FocusTrigger}, which should be added to a Component that
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

  /**
   * Called by the object which added this trigger as a {@link FocusListener}.
   * This method starts the animator if the trigger is waiting for a IN event.
   */
  public void focusGained(FocusEvent e) {
    fire(FocusTriggerEvent.IN);
  }

  /**
   * Called by the object which added this trigger as a {@link FocusListener}.
   * This method starts the animator if the trigger is waiting for a OUT event.
   */
  public void focusLost(FocusEvent e) {
    fire(FocusTriggerEvent.OUT);
  }
}
