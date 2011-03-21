package org.jdesktop.swt.animation.timing.triggers;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Control;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.Trigger;

/**
 * {@link MouseTrigger} handles mouse events and triggers an animation based on
 * those events. For example, to have {@code anim} start when component receives
 * an ENTER event, one might write the following:
 * 
 * <pre>
 * MouseTrigger trigger = MouseTrigger.addTrigger(control, anim,
 * 		MouseTriggerEvent.ENTER);
 * </pre>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class MouseTrigger extends Trigger implements MouseListener,
		MouseTrackListener {

	/**
	 * Creates a non-auto-reversing {@link MouseTrigger} and adds it as a
	 * listener to component.
	 * 
	 * @param control
	 *            control that will generate mouse events for this trigger.
	 * @param animator
	 *            the animation that will start when the event occurs.
	 * @param event
	 *            the event that will cause the action to fire.
	 * @return MouseTrigger the resulting trigger
	 */
	public static MouseTrigger addTrigger(Control control, Animator animator,
			MouseTriggerEvent event) {
		return addTrigger(control, animator, event, false);
	}

	/**
	 * Creates a {@link MouseTrigger} and adds it as a listener to component.
	 * 
	 * @param control
	 *            control that will generate mouse events for this trigger.
	 * @param animator
	 *            the animation that will start when the event occurs.
	 * @param event
	 *            the event that will cause the action to fire.
	 * @param autoReverse
	 *            flag to determine whether the animator should stop and reverse
	 *            based on opposite triggerEvents.
	 * @return FocusTrigger the resulting trigger
	 */
	public static MouseTrigger addTrigger(Control control, Animator animator,
			MouseTriggerEvent event, boolean autoReverse) {
		MouseTrigger trigger = new MouseTrigger(animator, event, autoReverse);
		control.addMouseListener(trigger);
		control.addMouseTrackListener(trigger);
		return trigger;
	}

	/**
	 * Creates a non-auto-reversing {@link MouseTrigger}, which should be added
	 * to a Component that will generate the mouse events of interest.
	 */
	public MouseTrigger(Animator animator, MouseTriggerEvent event) {
		this(animator, event, false);
	}

	/**
	 * Creates a {@link MouseTrigger}, which should be added to a Component that
	 * will generate the mouse events of interest.
	 */
	public MouseTrigger(Animator animator, MouseTriggerEvent event,
			boolean autoReverse) {
		super(animator, event, autoReverse);
	}

	/**
	 * Called by the object which added this trigger as a {@link MouseListener}.
	 * This method starts the animator if the trigger is waiting for an ENTER
	 * event.
	 */
	public void mouseEnter(MouseEvent e) {
		fire(MouseTriggerEvent.ENTER);
	}

	/**
	 * Called by the object which added this trigger as a {@link MouseListener}.
	 * This method starts the animator if the trigger is waiting for an EXIT
	 * event.
	 */
	public void mouseExit(MouseEvent e) {
		fire(MouseTriggerEvent.EXIT);
	}

	public void mouseHover(MouseEvent e) {
		// Nothing to do
	}

	public void mouseDoubleClick(MouseEvent e) {
		// Nothing to do
	}

	/**
	 * Called by the object which added this trigger as a {@link MouseListener}.
	 * This method starts the animator if the trigger is waiting for a CLICK
	 * event or a PRESS event (which are the same for SWT).
	 */
	public void mouseDown(MouseEvent e) {
		fire(MouseTriggerEvent.CLICK);
		fire(MouseTriggerEvent.PRESS);
	}

	/**
	 * Called by the object which added this trigger as a {@link MouseListener}.
	 * This method starts the animator if the trigger is waiting for a RELEASE
	 * event.
	 */
	public void mouseUp(MouseEvent e) {
		fire(MouseTriggerEvent.RELEASE);
	}
}
