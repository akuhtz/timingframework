package org.jdesktop.core.animation.timing;

/**
 * This interface provides the methods which are called by an animation during
 * the course of a timing sequence. Applications that wish to receive timing
 * events will either create a subclass of {@link TimingTargetAdapter} and
 * override methods of interest or they can create or use an implementation of
 * {@link TimingTarget}. A timing target can be passed into an
 * {@link Animator.Builder} via the
 * {@link Animator.Builder#addTarget(TimingTarget)} method or set later with the
 * {@link Animator#addTarget(TimingTarget)} method. Any animation may have
 * multiple timing targets.
 * <p>
 * The thread context of calls to all the methods defined below is that of the
 * of the timing source being used by the animation. This thread context is
 * typically documented for each {@link TimingSource} subclass.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public interface TimingTarget {

  /**
   * Called once when the animation begins. This provides a chance for targets
   * to perform any setup required at animation start time.
   * <p>
   * This is always the first call made to any timing target.
   * 
   * @param source
   *          the animation.
   */
  public void begin(Animator source);

  /**
   * Called once when the animation ends.
   * <p>
   * This is always the last call made to any timing target.
   * 
   * @param source
   *          the animation.
   */
  public void end(Animator source);

  /**
   * Called when the animation repeats the animation cycle. This method will be
   * invoked {@link Animator#getRepeatCount()} - 1 times, unless
   * {@link Animator#getRepeatCount()} == {@link Animator#INFINITE} in which
   * case it will be invoked until the animation is manually stopped.
   * <p>
   * if {@link Animator.RepeatBehavior#REVERSE} is used then the animation will
   * reverse direction on repeat.
   * 
   * @param source
   *          the animation.
   */
  public void repeat(Animator source);

  /**
   * Called when a running animation is reversed via
   * {@link Animator#reverseNow()}. This method is not invoked when
   * {@link Animator#startReverse()} is called or when the direction changes on
   * repeat&mdash;it is only used as a notification when an animation is
   * reversed via {@link Animator#reverseNow()}.
   * <p>
   * Notifications occur at the rate of the {@link TimingSource} being used by
   * the animation. Therefore, several calls to {@link Animator#reverseNow()}
   * may be coalesced into a single call of this method. However, all directed
   * reversals take place, i.e., calls to {@link Animator#reverseNow()} are not
   * ignored.
   * <p>
   * It is also possible, if {@link Animator.RepeatBehavior#REVERSE} is used,
   * that a repeat which reverses the animation could occur during the same tick
   * of the {@link TimingSource} that a reversal due to
   * {@link Animator#reverseNow()} does. In this case both reversals are taken
   * into account before this method is invoked. If this occurs this method is
   * invoked before {@link #repeat(Animator)}.
   * <p>
   * Overall, client code should understand that this method is not a general
   * notification that the animation's direction has changed. If such a
   * mechanism is needed the value of {@link Animator#getCurrentDirection()}
   * should be monitored in calls to {@link #timingEvent(Animator, double)}.
   * This method's purpose is to inform that the {@link Animator#reverseNow()}
   * method has been invoked one or more times on the animation.
   * 
   * @param source
   *          the animation.
   */
  public void reverse(Animator source);

  /**
   * This method will receive all of the timing events during an animation. The
   * fraction is the percent elapsed (0 to 1) of the current animation cycle.
   * 
   * @param source
   *          the animation.
   * @param fraction
   *          the fraction of completion between the start and end of the
   *          current cycle. Note that on reversing cycles (
   *          {@link Animator.Direction#BACKWARD}) the fraction decreases from
   *          1.0 to 0 on backwards-running cycles (A call to
   *          {@link Animator#getCurrentDirection()} will report which direction
   *          the animation is going). Note also that animations with a duration
   *          of {@link Animator#INFINITE INFINITE} will have an undefined value
   *          for the fraction, since there is no fraction that makes sense if
   *          the animation has no defined length.
   */
  public void timingEvent(Animator source, double fraction);
}
