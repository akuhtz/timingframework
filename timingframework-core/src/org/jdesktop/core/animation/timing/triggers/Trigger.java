package org.jdesktop.core.animation.timing.triggers;

import org.jdesktop.core.animation.timing.Animator;

/**
 * This abstract class should be overridden by any class wanting to implement a
 * new Trigger. The subclass will define the events to trigger off of and any
 * listeners to handle those events. That subclass will call either
 * {@link #fire()} or {@link #fire(TriggerEvent)} to start the animator based on
 * an event that occurred.
 * <p>
 * Subclasses should call one of the constructors in Trigger, according to
 * whether they want Trigger to discern between different TriggerEvents and
 * whether they want Trigger to auto-reverse the animation based on opposite
 * TriggerEvents.
 * <p>
 * Subclasses should call one of the <code>fire</code> methods based on whether
 * they want Trigger to perform any event logic or simply start the animation.
 * 
 * @author Chet Haase
 */
public abstract class Trigger {

	private boolean f_disarmed = false;
	private Animator f_animator;
	private TriggerEvent f_triggerEvent;
	private boolean f_autoReverse = false;

	/**
	 * Creates a Trigger that will start the animator when {@link #fire()} is
	 * called. Subclasses call this method to set up a simple Trigger that will
	 * be started by calling {@link #fire()}, and will have no dependency upon
	 * the specific {@link TriggerEvent} that must have occurred to start the
	 * animator.
	 * 
	 * @param animator
	 *            the animation that will start when the Trigger is fired.
	 */
	protected Trigger(Animator animator) {
		this(animator, null);
	}

	/**
	 * Creates a Trigger that will start the animator when
	 * {@link #fire(TriggerEvent)} is called with an event that equals
	 * triggerEvent.
	 * 
	 * @param animator
	 *            the animation that will start when the Trigger is fired.
	 * @param triggerEvent
	 *            the TriggerEvent that must occur for this Trigger to fire
	 */
	protected Trigger(Animator animator, TriggerEvent triggerEvent) {
		this(animator, triggerEvent, false);
	}

	/**
	 * Creates a Trigger that will start the animator when
	 * {@link #fire(TriggerEvent)} is called with an event that equals
	 * triggerEvent. Also, automatically stops and reverses animator when
	 * opposite event occurs, and stops reversing animator likewise when
	 * triggerEvent occurs.
	 * 
	 * @param animator
	 *            the animation that will start when the Trigger is fired.
	 * @param triggerEvent
	 *            the TriggerEvent that must occur for this Trigger to fire
	 * @param autoReverse
	 *            flag to determine whether the animator should stop and reverse
	 *            based on opposite triggerEvents.
	 * @see TriggerEvent#getOppositeEvent()
	 */
	protected Trigger(Animator animator, TriggerEvent triggerEvent,
			boolean autoReverse) {
		if (animator == null)
			throw new IllegalArgumentException("animator must be non-null.");
		f_animator = animator;
		f_triggerEvent = triggerEvent;
		f_autoReverse = autoReverse;
	}

	/**
	 * This method disables this Trigger and effectively noop's any actions that
	 * would otherwise occur
	 */
	public void disarm() {
		f_disarmed = true;
	}

	/**
	 * Called by subclasses to start the animator if currentEvent equals the
	 * event that the Trigger is based upon. Also, if the Trigger is set to
	 * autoReverse, it reverses the animator so that it is running in the
	 * opposite direction.
	 * 
	 * @param currentEvent
	 *            the {@link TriggerEvent} that just occurred, which will be
	 *            compared with the TriggerEvent used to construct this Trigger
	 *            and determine whether the animator should be started or
	 *            reversed
	 */
	protected final void fire(TriggerEvent currentEvent) {
		if (f_disarmed)
			return;

		if (currentEvent == f_triggerEvent) {
			/*
			 * The trigger event occurred - fire/re-fire the animation.
			 */
			if (f_animator.isRunning()) {
				f_animator.stop();
			}
			f_animator.start();
		} else if (f_triggerEvent != null
				&& currentEvent == f_triggerEvent.getOppositeEvent()) {
			/*
			 * Opposite event occurred - run reverse animation if auto-reverse
			 * was requested.
			 */
			if (f_autoReverse) {
				if (f_animator.isRunning()) {
					f_animator.reverseNow();
				} else {
					f_animator.startReverse();
				}
			}
		}
	}

	/**
	 * Utility method called by subclasses to start the animator. This variant
	 * assumes that there need be no check of the TriggerEvent that fired, which
	 * is useful for subclasses with simple events.
	 */
	protected final void fire() {
		if (f_disarmed)
			return;

		if (f_animator.isRunning()) {
			f_animator.stop();
		}
		f_animator.start();
	}
}
