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
   * Called when the animation begins. This provides a chance for targets to
   * perform any setup required at animation start time.
   * 
   * @param source
   *          the animation.
   */
  public void begin(Animator source);

  /**
   * Called when the animation ends.
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
   * 
   * @param source
   *          the animation.
   */
  public void repeat(Animator source);

  /**
   * Called when a running animation is reversed via
   * {@link Animator#reverseNow()}. This method is not invoked when
   * {@link Animator#startReverse()} is called&mdash;it is only used as a
   * notification when a running animation is reversed.
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
   *          1.0 to 0 on backwards-running cycles. Note also that animations
   *          with a duration of {@link Animator#INFINITE INFINITE} will have an
   *          undefined value for the fraction, since there is no fraction that
   *          makes sense if the animation has no defined length.
   */
  public void timingEvent(Animator source, double fraction);
}
