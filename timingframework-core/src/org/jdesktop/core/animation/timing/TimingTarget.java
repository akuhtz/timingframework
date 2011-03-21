package org.jdesktop.core.animation.timing;

/**
 * This interface provides the methods which are called by Animator during the
 * course of a timing sequence. Applications that wish to receive timing events
 * will either create a subclass of TimingTargetAdapter and override or they can
 * create or use an implementation of TimingTarget. A TimingTarget can be passed
 * into the constructor of Animator or set later with the
 * {@link Animator#addTarget(TimingTarget)} method. Any Animator may have
 * multiple TimingTargets.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public interface TimingTarget {

	/**
	 * Called when the animation begins. This provides a chance for targets to
	 * perform any setup required at animation start time.
	 * 
	 * @param source
	 *            the animation.
	 */
	public void begin(Animator source);

	/**
	 * Called when the animation ends.
	 * 
	 * @param source
	 *            the animation.
	 */
	public void end(Animator source);

	/**
	 * Called when the animation repeats the animation cycle.
	 * 
	 * @param source
	 *            the animation.
	 */
	public void repeat(Animator source);

	/**
	 * This method will receive all of the timing events during an animation.
	 * The fraction is the percent elapsed (0 to 1) of the current animation
	 * cycle.
	 * 
	 * @param fraction
	 *            the fraction of completion between the start and end of the
	 *            current cycle. Note that on reversing cycles (
	 *            {@link Animator.Direction#BACKWARD}) the fraction decreases
	 *            from 1.0 to 0 on backwards-running cycles. Note also that
	 *            animations with a duration of {@link Animator#INFINITE
	 *            INFINITE} will have an undefined value for the fraction, since
	 *            there is no fraction that makes sense if the animation has no
	 *            defined length.
	 * @param direction
	 *            the current direction of the animation.
	 * @param source
	 *            the animation.
	 * @see Animator.Direction
	 */
	public void timingEvent(double fraction, Animator.Direction direction,
			Animator source);
}
