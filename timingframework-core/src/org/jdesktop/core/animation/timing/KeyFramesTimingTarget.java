package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.i18n.I18N;

import com.surelogic.Immutable;

/**
 * Abstract timing target that allows interpolation of values throughout an
 * animation via key frames.
 * <p>
 * If your animation simply changes a property on an object then consider using
 * the {@link PropertySetter} utility instead of extending this class.
 * 
 * @author Tim Halloran
 * 
 * @param <T>
 *          the type of the values.
 * 
 * @see PropertySetter
 */
@Immutable
public abstract class KeyFramesTimingTarget<T> extends TimingTargetAdapter {

  private final KeyFrames<T> f_keyFrames;

  /**
   * Constructs an instance using the passed key frames.
   * 
   * @param keyFrames
   *          the key frames to use to provide interpolated values throughout
   *          the animation.
   */
  public KeyFramesTimingTarget(KeyFrames<T> keyFrames) {
    if (keyFrames == null)
      throw new IllegalArgumentException(I18N.err(1, "keyFrames"));
    f_keyFrames = keyFrames;
  }

  /**
   * Gets the key frames object for this timing target. The return value will
   * not be {@code null}.
   * 
   * @return a key frames object.
   */
  public KeyFrames<T> getKeyFrames() {
    return f_keyFrames;
  }

  @Override
  public void timingEvent(Animator source, double fraction) {
    valueAtTimingEvent(f_keyFrames.getInterpolatedValueAt(fraction), fraction, source);
  }

  /**
   * This method will receive the interpolated value at each timing event during
   * an animation. It is directly invoked by the
   * {@link #timingEvent(Animator, double)} method in the following manner:
   * 
   * <pre>
   * public void timingEvent(Animator source, double fraction) {
   *   valueAtTimingEvent(getKeyFrames().getInterpolatedValueAt(fraction), fraction, source);
   * }
   * </pre>
   * 
   * If {@link #timingEvent(Animator, double)} is overridden by the
   * implementation and the above snippet does not appear then this method will
   * not be invoked.
   * <p>
   * The fraction is the percent elapsed (0 to 1) of the current animation
   * cycle.
   * 
   * @param value
   *          the interpolated value, determined using the object's key frames.
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
  public abstract void valueAtTimingEvent(T value, double fraction, Animator source);
}
