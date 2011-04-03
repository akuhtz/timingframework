package org.jdesktop.core.animation.timing;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jdesktop.core.animation.i18n.I18N;

import com.surelogic.NotThreadSafe;

/**
 * This class is used to construct {@link Animator} instances.
 * <p>
 * Instances of this class are not thread safe and are intended to be
 * thread-confined. However, the {@link Animator} objects produces are
 * thread-safe.
 * 
 * @author Tim Halloran
 */
@NotThreadSafe
public class AnimatorBuilder {

  /**
   * A default timing source used for the construction of animations.
   * <p>
   * <i>Implementation note:</i> The setting and getting of the default timing
   * source is thread safe.
   */
  private static AtomicReference<TimingSource> f_defaultTimingSource = new AtomicReference<TimingSource>();

  /**
   * Sets the passed timing source as the default used for the construction of
   * animations. The no-argument constructor ({@link #AnimatorBuilder()}) uses
   * the default timing source set by this method.
   * <p>
   * Passing {@code null} to this method clears the default timing source.
   * <p>
   * The client code remains responsible for disposing of the timing source when
   * it is finished using it.
   * 
   * @param timingSource
   *          a timing source or {@code null} to clear the default.
   */
  public static void setDefaultTimingSource(TimingSource timingSource) {
    f_defaultTimingSource.set(timingSource);
  }

  private long f_duration = 1;
  private TimeUnit f_durationTimeUnit = TimeUnit.SECONDS;
  private Animator.EndBehavior f_endBehavior = Animator.EndBehavior.HOLD;
  private Interpolator f_interpolator = null; // use the built-in default
  private Animator.RepeatBehavior f_repeatBehavior = Animator.RepeatBehavior.REVERSE;
  private long f_repeatCount = 1;
  private Animator.Direction f_startDirection = Animator.Direction.FORWARD;
  private final Set<TimingTarget> f_targets = new HashSet<TimingTarget>();
  private final TimingSource f_timingSource;

  /**
   * Constructs an animation builder instance.
   * 
   * @param timingSource
   *          the timing source for the animation.
   */
  public AnimatorBuilder(TimingSource timingSource) {
    if (timingSource == null)
      throw new IllegalArgumentException(I18N.err(11));
    f_timingSource = timingSource;
  }

  /**
   * Constructs an animation builder instance using the default timing source.
   * 
   * @see #setDefaultTimingSource(TimingSource)
   */
  public AnimatorBuilder() {
    this(f_defaultTimingSource.get());
  }

  /**
   * Adds a {@link TimingTarget} to the list of targets that get notified of
   * each timing event while the animation is running.
   * 
   * @param target
   *          a {@link TimingTarget} object.
   * @return this builder (to allow chained operations).
   */
  public AnimatorBuilder addTarget(TimingTarget target) {
    if (target != null)
      f_targets.add(target);
    return this;
  }

  /**
   * Sets the duration for the animation. The default value is one second.
   * 
   * @param value
   *          the duration of the animation. This value must be >= 1 or
   *          {@link Animator#INFINITE}, meaning the animation will run until
   *          manually stopped.
   * @param unit
   *          the time unit of the value parameter. A {@code null} value is
   *          equivalent to setting the default unit of {@link TimeUnit#SECONDS}
   *          .
   * @return this builder (to allow chained operations).
   * @throws IllegalStateException
   *           if value is not >= 1 or {@link Animator#INFINITE}.
   */
  public AnimatorBuilder setDuration(long value, TimeUnit unit) {
    if (value < 1 && value != Animator.INFINITE) {
      throw new IllegalArgumentException(I18N.err(10, value));
    }
    f_duration = value;
    f_durationTimeUnit = unit != null ? unit : TimeUnit.SECONDS;
    return this;
  }

  /**
   * Sets the behavior at the end of the animation. The default value is
   * {@link Animator.EndBehavior#HOLD}.
   * 
   * @param value
   *          the behavior at the end of the animation. A {@code null} value is
   *          equivalent to setting the default value.
   * @return this builder (to allow chained operations).
   */
  public AnimatorBuilder setEndBehavior(Animator.EndBehavior value) {
    f_endBehavior = value != null ? value : Animator.EndBehavior.HOLD;
    return this;
  }

  /**
   * Sets the interpolator for each animation cycle. The default interpolator is
   * the built-in linear interpolator.
   * 
   * @param value
   *          the interpolation to use each animation cycle. A {@code null}
   *          value is equivalent to setting the default value.
   * @return this builder (to allow chained operations).
   */
  public AnimatorBuilder setInterpolator(Interpolator value) {
    f_interpolator = value;
    return this;
  }

  /**
   * Sets the repeat behavior of the animation. The default value is
   * {@link Animator.RepeatBehavior#REVERSE}.
   * 
   * @param value
   *          the behavior for each successive animation cycle. A {@code null}
   *          value is equivalent to setting the default value.
   * @return this builder (to allow chained operations).
   */
  public AnimatorBuilder setRepeatBehavior(Animator.RepeatBehavior value) {
    f_repeatBehavior = value != null ? value : Animator.RepeatBehavior.REVERSE;
    return this;
  }

  /**
   * Sets the number of times the animation cycle will repeat. The default value
   * is 1.
   * 
   * @param value
   *          number of times the animation cycle will repeat. This value must
   *          be >= 1 or {@link Animator#INFINITE} for animations that repeat
   *          indefinitely.
   * @return this builder (to allow chained operations).
   * @throws IllegalArgumentException
   *           if value is not >=1 or {@link Animator#INFINITE}.
   */
  public AnimatorBuilder setRepeatCount(long value) {
    if (value < 1 && value != Animator.INFINITE) {
      throw new IllegalArgumentException(I18N.err(10, value));
    }
    f_repeatCount = value;
    return this;
  }

  /**
   * Sets the start direction for the initial animation cycle. The default start
   * direction is {@link Animator.Direction#FORWARD}.
   * 
   * @param value
   *          initial animation cycle direction. A {@code null} value is
   *          equivalent to setting the default value.
   * @return this builder (to allow chained operations).
   */
  public AnimatorBuilder setStartDirection(Animator.Direction value) {
    f_startDirection = value != null ? value : Animator.Direction.FORWARD;
    return this;
  }

  /**
   * Constructs an animation with the settings defined by this builder.
   * 
   * @return an animation.
   */
  public Animator build() {
    final Animator result = new Animator(f_duration, f_durationTimeUnit, f_endBehavior, f_interpolator, f_repeatBehavior,
        f_repeatCount, f_startDirection, f_timingSource);
    for (TimingTarget target : f_targets) {
      result.addTarget(target);
    }
    return result;
  }
}
