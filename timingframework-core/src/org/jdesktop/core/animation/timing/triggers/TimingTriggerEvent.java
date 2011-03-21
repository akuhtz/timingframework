package org.jdesktop.core.animation.timing.triggers;

/**
 * Defines {@link TriggerEvent}s for the {@link TimingTrigger}.
 * {@link TimingTrigger}s can be set to fire when an animator starts, stops, or
 * repeats.
 * 
 * @author Chet Haase
 */
public class TimingTriggerEvent extends TriggerEvent {

	/**
	 * Event fired when an animation starts.
	 */
	public static final TimingTriggerEvent START = new TimingTriggerEvent(
			"Start");
	/**
	 * Event fired when an animation stops.
	 */
	public static final TimingTriggerEvent STOP = new TimingTriggerEvent("Stop");

	/**
	 * Event fired when an animation finishes one cycle and starts another.
	 */
	public static final TimingTriggerEvent REPEAT = new TimingTriggerEvent(
			"Repeat");

	private TimingTriggerEvent(String name) {
		super(name);
	}

	/**
	 * This method finds the opposite of the current event:
	 * <ul>
	 * <li>START -> STOP</li>
	 * <li>STOP -> START</li>
	 * </ul>
	 * Note that REPEAT has no obvious opposite so it simply returns REPEAT.
	 * This method should probably not be called for that case.
	 */
	public TriggerEvent getOppositeEvent() {
		if (this.equals(TimingTriggerEvent.START)) {
			return TimingTriggerEvent.STOP;
		} else if (this.equals(TimingTriggerEvent.STOP)) {
			return TimingTriggerEvent.START;
		}
		// Possible to reach here for REPEAT action (but probably should not
		// have been called with this event)
		return this;
	}
}
