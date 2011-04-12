package org.jdesktop.swing.animation.timing.triggers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Trigger;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;

import com.surelogic.ThreadSafe;

/**
 * {@link MouseTrigger} handles mouse events and triggers an animation based on
 * those events. For example, to have {@code anim} start when component receives
 * an ENTER event, one might write the following:
 * 
 * <pre>
 * MouseTrigger trigger = MouseTrigger.addTrigger(component, anim, MouseTriggerEvent.ENTER);
 * </pre>
 * 
 * @author Chet Haase
 */
@ThreadSafe
public class MouseTrigger extends Trigger implements MouseListener {

  /**
   * Creates a non-auto-reversing {@link MouseTrigger} and adds it as a listener
   * to component.
   * 
   * @param component
   *          component that will generate mouse events for this trigger.
   * @param animator
   *          the animation that will start when the event occurs.
   * @param event
   *          the event that will cause the action to fire.
   * @return MouseTrigger the resulting trigger
   */
  public static MouseTrigger addTrigger(JComponent component, Animator animator, MouseTriggerEvent event) {
    return addTrigger(component, animator, event, false);
  }

  /**
   * Creates a {@link MouseTrigger} and adds it as a listener to component.
   * 
   * @param component
   *          component that will generate mouse events for this trigger.
   * @param animator
   *          the animation that will start when the event occurs.
   * @param event
   *          the event that will cause the action to fire.
   * @param autoReverse
   *          flag to determine whether the animator should stop and reverse
   *          based on opposite triggerEvents.
   * @return FocusTrigger the resulting trigger
   */
  public static MouseTrigger addTrigger(JComponent component, Animator animator, MouseTriggerEvent event, boolean autoReverse) {
    MouseTrigger trigger = new MouseTrigger(animator, event, autoReverse);
    component.addMouseListener(trigger);
    return trigger;
  }

  /**
   * Creates a non-auto-reversing {@link MouseTrigger}, which should be added to
   * a Component that will generate the mouse events of interest.
   */
  public MouseTrigger(Animator animator, MouseTriggerEvent event) {
    this(animator, event, false);
  }

  /**
   * Creates a {@link MouseTrigger}, which should be added to a Component that
   * will generate the mouse events of interest.
   */
  public MouseTrigger(Animator animator, MouseTriggerEvent event, boolean autoReverse) {
    super(animator, event, autoReverse);
  }

  public void mouseEntered(MouseEvent e) {
    fire(MouseTriggerEvent.ENTER);
  }

  public void mouseExited(MouseEvent e) {
    fire(MouseTriggerEvent.EXIT);
  }

  public void mousePressed(MouseEvent e) {
    fire(MouseTriggerEvent.PRESS);
  }

  public void mouseReleased(MouseEvent e) {
    fire(MouseTriggerEvent.RELEASE);
  }

  public void mouseClicked(MouseEvent e) {
    fire(MouseTriggerEvent.CLICK);
  }
}
