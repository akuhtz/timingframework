package org.jdesktop.swt.animation.timing.triggers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.triggers.Trigger;

/**
 * EventTrigger handles SWT events and starts an animation when events occur.
 * For example, to have {@code anim} start when a button is clicked, one might
 * write the following:
 * 
 * <pre>
 * EventTrigger trigger = EventTrigger.addTrigger(button, SWT.Selection, anim);
 * </pre>
 * 
 * @author Tim Halloran
 */
public final class EventTrigger extends Trigger implements Listener {

  /**
   * Creates an {@link EventTrigger} and adds it as a listener to the passed SWT
   * widget.
   * 
   * @param widget
   *          an SWT widget that will be used as an event source for this
   *          trigger.
   * @param eventType
   *          the type of event to listen for.
   * @param animator
   *          the animation to start when the event occurs
   * @return the resulting trigger.
   * 
   * @see SWT
   * @see Widget#addListener(int, Listener)
   */
  public static EventTrigger addTrigger(Widget widget, int eventType, Animator animator) {
    final EventTrigger trigger = new EventTrigger(animator);
    widget.addListener(eventType, trigger);
    return trigger;
  }

  /**
   * Creates an EventTrigger that will start the animator upon receiving any
   * ActionEvents. It should be added to any suitable object with an
   * addActionListener method.
   * 
   * @param animator
   *          the Animator that start when the event occurs
   */
  protected EventTrigger(Animator animator) {
    super(animator);
  }

  /**
   * Called by an object generating ActionEvents to which this trigger was added
   * as an ActionListener. This starts the Animator.
   */
  public void handleEvent(Event event) {
    fire();
  }
}
