package org.jdesktop.core.animation.timing.triggers;

/**
 * Mouse Enter/Exit/Press/Release/Click events.
 * 
 * @author Chet Haase
 */
public class MouseTriggerEvent extends TriggerEvent {

	/**
	 * Event fired when mouse enters.
	 */
	public static final MouseTriggerEvent ENTER = new MouseTriggerEvent(
			"Entered");

	/**
	 * Event fired when mouse exits.
	 */
	public static final MouseTriggerEvent EXIT = new MouseTriggerEvent("Exit");

	/**
	 * Event fired when mouse button is pressed.
	 */
	public static final MouseTriggerEvent PRESS = new MouseTriggerEvent("Press");

	/**
	 * Event fired when mouse button is released.
	 */
	public static final MouseTriggerEvent RELEASE = new MouseTriggerEvent(
			"Release");
	/**
	 * Event fired when mouse is clicked.
	 */
	public static final MouseTriggerEvent CLICK = new MouseTriggerEvent("Click");

	/**
	 * Protected constructor; this helps ensure type-safe use of pre-define
	 * TriggerEvent objects.
	 */
	private MouseTriggerEvent(String name) {
		super(name);
	}

	/**
	 * This method finds the opposite of the current event:
	 * <ul>
	 * <li>ENTER -> EXIT</li>
	 * <li>EXIT -> ENTER</li>
	 * <li>PRESS -> RELEASE</li>
	 * <li>RELEASE -> PRESS</li>
	 * </ul>
	 * Note that CLICK has no obvious opposite so it simply returns CLICK (this
	 * method should probably not be called for that case).
	 * 
	 */
	public TriggerEvent getOppositeEvent() {
		if (this == MouseTriggerEvent.ENTER) {
			return MouseTriggerEvent.EXIT;
		} else if (this == MouseTriggerEvent.EXIT) {
			return MouseTriggerEvent.ENTER;
		} else if (this == MouseTriggerEvent.PRESS) {
			return MouseTriggerEvent.RELEASE;
		} else if (this == MouseTriggerEvent.RELEASE) {
			return MouseTriggerEvent.PRESS;
		}
		// Possible to reach here for REPEAT action (but probably should not
		// have been called with this event)
		return this;
	}
}
