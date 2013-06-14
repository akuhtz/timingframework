package org.jdesktop.core.animation.timing;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;

import com.surelogic.Immutable;
import com.surelogic.InRegion;
import com.surelogic.NonNull;
import com.surelogic.NotThreadSafe;
import com.surelogic.Nullable;
import com.surelogic.ReferenceObject;
import com.surelogic.Region;
import com.surelogic.RegionEffects;
import com.surelogic.RegionLock;
import com.surelogic.ThreadSafe;
import com.surelogic.Unique;
import com.surelogic.Vouch;

/**
 * This class controls the timing of animations. Instances are constructed by a
 * {@link Animator.Builder} instance by invoking various set methods control the
 * parameters under which the desired animation is run. The parameters of this
 * class use the concepts of a "cycle" (the base animation) and an "envelope"
 * that controls how the cycle is started, ended, and repeated.
 * <p>
 * For example, this animation will run for 1 second, calling your
 * {@link TimingTarget}, {@code myTarget}, with timing events when the animation
 * is started, running, and stopped:
 * 
 * <pre>
 * Animator.setDefaultTimingSource(source); // shared timing source
 * 
 * Animator animator = new Animator.Builder().setDuration(1, TimeUnit.SECONDS).addTarget(myTarget).build();
 * animator.start();
 * </pre>
 * 
 * The following variation will run a half-second animation 4 times, reversing
 * direction each time:
 * 
 * <pre>
 * Animator animator = new Animator.Builder().setDuration(500, TimeUnit.MILLISECONDS).setRepeatCount(4).addTarget(myTarget).build();
 * animator.start();
 * </pre>
 * 
 * More complex animations can be created through the use of the complete set of
 * properties in {@link Animator.Builder}.
 * <p>
 * This class provides a useful "debug" name via
 * {@link Builder#setDebugName(String)} and {@link #getDebugName()}. The debug
 * name is also output by {@link #toString()}. This feature is intended to aid
 * debugging.
 * <p>
 * Instances can be started again after they complete, however, ensure that they
 * are not running, via <tt>!</tt>{@link #isRunning()} or {@link #await()},
 * before {@link #start()} or {@link #startReverse()} is called. Even if you
 * successfully invoked {@link #stop()} or {@link #cancel()} it can take some
 * time for all the calls to registered {@link TimingTarget}s to complete. Use
 * of {@link #await()} is far more efficient than polling the state of the
 * animation with {@link #isRunning()}. However, do not call {@link #await()} in
 * the thread context of the timing source for this animation or it will block
 * forever.
 * <p>
 * This class is thread-safe. Synchronizing on the instance(<tt>this</tt>) may
 * be used to protect state changes over several calls.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 * 
 * @see Builder
 */
@ThreadSafe
@ReferenceObject
@Region("AnimatorState")
@RegionLock("AnimatorLock is this protects AnimatorState")
public final class Animator implements TickListener {

  /**
   * EndBehavior determines what happens at the end of the animation.
   * 
   * @see Builder#setEndBehavior(Animator.EndBehavior)
   */
  @Immutable
  public static enum EndBehavior {
    /**
     * Timing sequence will maintain its final value at the end.
     */
    HOLD,
    /**
     * Timing sequence should reset to the initial value at the end.
     */
    RESET,
  };

  /**
   * Direction is used to set the initial direction in which the animation
   * starts.
   * 
   * @see Builder#setStartDirection(Animator.Direction)
   */
  @Immutable
  public static enum Direction {
    /**
     * The cycle proceeds forward.
     */
    FORWARD {
      @Override
      public Direction getOppositeDirection() {
        return BACKWARD;
      }
    },
    /**
     * The cycle proceeds backward.
     */
    BACKWARD {
      @Override
      public Direction getOppositeDirection() {
        return FORWARD;
      }
    };

    abstract public Direction getOppositeDirection();
  };

  /**
   * RepeatBehavior determines how each successive cycle will flow.
   * 
   * @see Builder#setRepeatBehavior(Animator.RepeatBehavior)
   */
  @Immutable
  public static enum RepeatBehavior {
    /**
     * Each repeated cycle proceeds in the same direction as the previous one.
     */
    LOOP,
    /**
     * Each repeated cycle proceeds in the opposite direction as the previous
     * one.
     */
    REVERSE
  };

  /**
   * Used to specify unending repeat count.
   * 
   * @see Builder#setRepeatCount(long)
   * */
  public static final long INFINITE = -1;

  /**
   * Sets the passed timing source as the default used for the construction of
   * animations. The no-argument constructor for {@link Animator.Builder} uses
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
  public static void setDefaultTimingSource(@Nullable TimingSource timingSource) {
    Builder.setDefaultTimingSource(timingSource);
  }

  /**
   * Gets the timing source being used as the default for the construction on
   * animations. The no-argument constructor for {@link Animator.Builder} uses
   * the default timing source. A {@code null} result indicates that no default
   * timing source has been set.
   * 
   * @return the timing source being used as the default for the construction on
   *         animations, or {@code null} if none.
   */
  @Nullable
  public static TimingSource getDefaultTimingSource() {
    return Builder.getDefaultTimingSource();
  }

  /**
   * This class is used to construct {@link Animator} instances.
   * <p>
   * The default values are listed in the table below.
   * <p>
   * <table border="1">
   * <tr>
   * <th>Method</th>
   * <th>Description</th>
   * <th>Default</th>
   * </tr>
   * <tr>
   * <td>{@link #addTarget(TimingTarget)}</td>
   * <td>gets timing events while the animation is running</td>
   * <td align="right"><i>none</i></td>
   * </tr>
   * <tr>
   * <td>{@link Animator#setDefaultTimingSource(TimingSource)} or
   * {@link Animator.Builder#Animator.Builder(TimingSource)}</td>
   * <td>a timing source for the animation</td>
   * <td align="right"><i>none</i></td>
   * </tr>
   * <tr>
   * <td>{@link #setDuration(long, TimeUnit)}</td>
   * <td>the duration of one cycle of the animation</td>
   * <td align="right">1 second</td>
   * </tr>
   * <tr>
   * <td>{@link #setDisposeTimingSource(boolean)}</td>
   * <td>if the {@link TimingSource} used by the animation should be disposed at
   * the end of the animation.</td>
   * <td align="right">{@code false}</td>
   * </tr>
   * <tr>
   * <td>{@link #setEndBehavior(Animator.EndBehavior)}</td>
   * <td>what happens at the end of the animation</td>
   * <td align="right">{@link Animator.EndBehavior#HOLD}</td>
   * </tr>
   * <tr>
   * <td>{@link #setInterpolator(Interpolator)}</td>
   * <td>the interpolator for each animation cycle</td>
   * <td align="right">{@link LinearInterpolator}</td>
   * </tr>
   * <tr>
   * <td>{@link #setRepeatBehavior(Animator.RepeatBehavior)}</td>
   * <td>the repeat behavior of the animation</td>
   * <td align="right">{@link Animator.RepeatBehavior#REVERSE}</td>
   * </tr>
   * <tr>
   * <td>{@link #setRepeatCount(long)}</td>
   * <td>the number of times the animation cycle will repeat</td>
   * <td align="right">1</td>
   * </tr>
   * <tr>
   * <td>{@link #setStartDirection(Animator.Direction)}</td>
   * <td>the start direction for the initial animation cycle</td>
   * <td align="right">{@link Animator.Direction#FORWARD}</td>
   * </tr>
   * <tr>
   * <td>{@link #setStartDelay(long, TimeUnit)}</td>
   * <td>a delay prior to starting the first animation cycle after the call to
   * {@link Animator#start} or {@link Animator#startReverse}.</td>
   * <td align="right"><i>none</i></td>
   * </tr>
   * <tr>
   * <td>{@link #setDebugName(String)}</td>
   * <td>a meaningful name for the animation used by the
   * {@link Animator#toString()} method</td>
   * <td align="right">null</td>
   * </tr>
   * </table>
   * <p>
   * Values can be "cloned" from another {@link Animator} or
   * {@link Animator.Builder} using one of several <tt>copy</tt> methods
   * provided by this class.
   * <p>
   * Instances of this class are not thread safe and are intended to be
   * thread-confined. However, the {@link Animator} objects produced are
   * thread-safe.
   * 
   * @author Tim Halloran
   */
  @NotThreadSafe
  public static class Builder {

    /**
     * A default timing source used for the construction of animations.
     * <p>
     * <i>Implementation note:</i> The setting and getting of the default timing
     * source is thread safe.
     */
    static AtomicReference<TimingSource> f_defaultTimingSource = new AtomicReference<TimingSource>();

    /**
     * Sets the passed timing source as the default used for the construction of
     * animations. The no-argument constructor ({@link #Builder()}) uses the
     * default timing source set by this method.
     * <p>
     * Passing {@code null} to this method clears the default timing source.
     * <p>
     * This method is only called by
     * {@link Animator#setDefaultTimingSource(TimingSource)}.
     * 
     * @param timingSource
     *          a timing source or {@code null} to clear the default.
     */
    static void setDefaultTimingSource(@Nullable TimingSource timingSource) {
      f_defaultTimingSource.set(timingSource);
    }

    /**
     * Gets the timing source being used as the default for the construction on
     * animations.
     * 
     * @return the timing source being used as the default for the construction
     *         on animations, or {@code null} if none.
     */
    @Nullable
    static TimingSource getDefaultTimingSource() {
      return f_defaultTimingSource.get();
    }

    @Nullable
    private String f_debugName = null;
    private long f_duration = 1;
    @NonNull
    private TimeUnit f_durationTimeUnit = SECONDS;
    @NonNull
    private Animator.EndBehavior f_endBehavior = Animator.EndBehavior.HOLD;
    @NonNull
    private Interpolator f_interpolator = LinearInterpolator.getInstance();
    @NonNull
    private Animator.RepeatBehavior f_repeatBehavior = Animator.RepeatBehavior.REVERSE;
    private long f_repeatCount = 1;
    @NonNull
    private Animator.Direction f_startDirection = Animator.Direction.FORWARD;
    private long f_startDelay = 0;
    @NonNull
    private TimeUnit f_startDelayTimeUnit = SECONDS;
    @NonNull
    private final List<TimingTarget> f_targets = new ArrayList<TimingTarget>();
    @NonNull
    private final TimingSource f_timingSource;
    private boolean f_disposeTimingSource = false;

    /**
     * Constructs an animation builder instance.
     * 
     * @param timingSource
     *          the timing source for the animation.
     */
    public Builder(TimingSource timingSource) {
      if (timingSource == null)
        throw new IllegalArgumentException(I18N.err(11));
      f_timingSource = timingSource;
    }

    /**
     * Constructs an animation builder instance using the default timing source.
     * 
     * @see #setDefaultTimingSource(TimingSource)
     */
    public Builder() {
      this(f_defaultTimingSource.get());
    }

    /**
     * Adds a {@link TimingTarget} to the list of targets that get notified of
     * each timing event while the animation is running.
     * <p>
     * {@link TimingTarget}s will be called in the order they are added.
     * Duplicate additions are ignored.
     * 
     * @param target
     *          a {@link TimingTarget} object.
     * @return this builder (to allow chained operations).
     */
    @NonNull
    public Builder addTarget(TimingTarget target) {
      if (target != null && !f_targets.contains(target))
        f_targets.add(target);
      return this;
    }

    /**
     * Adds the collection of passed {@link TimingTarget}s to the list of
     * targets that get notified of each timing event while the animation is
     * running.
     * <p>
     * {@link TimingTarget}s will be called in the order they are added.
     * Duplicate additions are ignored.
     * 
     * @param targets
     *          a collection of {@link TimingTarget} objects.
     * @return this builder (to allow chained operations).
     */
    public Builder addTargets(Collection<TimingTarget> targets) {
      if (targets != null)
        for (TimingTarget target : targets)
          addTarget(target);
      return this;
    }

    /**
     * Adds the collection of passed {@link TimingTarget}s to the list of
     * targets that get notified of each timing event while the animation is
     * running.
     * <p>
     * {@link TimingTarget}s will be called in the order they are added.
     * Duplicate additions are ignored.
     * 
     * @param targets
     *          a collection of {@link TimingTarget} objects.
     * @return this builder (to allow chained operations).
     */
    @NonNull
    public Builder addTargets(TimingTarget... targets) {
      addTargets(Arrays.asList(targets));
      return this;
    }

    /**
     * Sets the "debug" name of the animation. The default value is {@code null}
     * .
     * 
     * @param name
     *          a name of the animation. A {@code null} value is allowed.
     * @return this builder (to allow chained operations).
     */
    @NonNull
    public Builder setDebugName(String name) {
      f_debugName = name;
      return this;
    }

    /**
     * Sets if the animation should invoke {@link TimingSource#dispose()} on its
     * timing source when it ends. The default value is {@code false}.
     * 
     * @param value
     *          {@code true} if the animation should invoke
     *          {@link TimingSource#dispose()} on its timing source when it
     *          ends, {@code false} if not.
     * @return this builder (to allow chained operations).
     */
    @NonNull
    public Builder setDisposeTimingSource(boolean value) {
      f_disposeTimingSource = value;
      return this;
    }

    /**
     * Sets the duration of one cycle of the animation. The default value is one
     * second.
     * 
     * @param value
     *          the duration of the animation. This value must be >= 1.
     * @param unit
     *          the time unit of the value parameter. A {@code null} value is
     *          equivalent to setting the default unit of
     *          {@link TimeUnit#SECONDS}.
     * @return this builder (to allow chained operations).
     * 
     * @throws IllegalStateException
     *           if value is not >= 1.
     */
    @NonNull
    public Builder setDuration(long value, TimeUnit unit) {
      if (value < 1)
        throw new IllegalArgumentException(I18N.err(10, value));

      f_duration = value;
      f_durationTimeUnit = unit != null ? unit : SECONDS;
      return this;
    }

    /**
     * Sets the behavior at the end of the animation. The default value is
     * {@link Animator.EndBehavior#HOLD}.
     * 
     * @param value
     *          the behavior at the end of the animation. A {@code null} value
     *          is equivalent to setting the default value.
     * @return this builder (to allow chained operations).
     */
    @NonNull
    public Builder setEndBehavior(Animator.EndBehavior value) {
      f_endBehavior = value != null ? value : Animator.EndBehavior.HOLD;
      return this;
    }

    /**
     * Sets the interpolator for each animation cycle. The default interpolator
     * is the built-in linear interpolator.
     * 
     * @param value
     *          the interpolation to use each animation cycle. A {@code null}
     *          value is equivalent to setting the default value.
     * @return this builder (to allow chained operations).
     */
    @NonNull
    public Builder setInterpolator(Interpolator value) {
      f_interpolator = value != null ? value : LinearInterpolator.getInstance();
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
    @NonNull
    public Builder setRepeatBehavior(Animator.RepeatBehavior value) {
      f_repeatBehavior = value != null ? value : Animator.RepeatBehavior.REVERSE;
      return this;
    }

    /**
     * Sets the number of times the animation cycle will repeat. The default
     * value is 1.
     * 
     * @param value
     *          number of times the animation cycle will repeat. This value must
     *          be >= 1 or {@link Animator#INFINITE} for animations that repeat
     *          indefinitely.
     * @return this builder (to allow chained operations).
     * 
     * @throws IllegalArgumentException
     *           if value is not >=1 or {@link Animator#INFINITE}.
     */
    @NonNull
    public Builder setRepeatCount(long value) {
      if (value < 1 && value != Animator.INFINITE)
        throw new IllegalArgumentException(I18N.err(10, value));

      f_repeatCount = value;
      return this;
    }

    /**
     * Sets the start direction for the initial animation cycle. The default
     * start direction is {@link Animator.Direction#FORWARD}.
     * 
     * @param value
     *          initial animation cycle direction. A {@code null} value is
     *          equivalent to setting the default value.
     * @return this builder (to allow chained operations).
     */
    @NonNull
    public Builder setStartDirection(Animator.Direction value) {
      f_startDirection = value != null ? value : Animator.Direction.FORWARD;
      return this;
    }

    /**
     * Sets the start delay of the animation. This sets a delay prior to
     * starting the first animation cycle after the call to
     * {@link Animator#start} or {@link Animator#startReverse}. The default
     * value is 0 indicating no start delay.
     * 
     * @param value
     *          the start delay of the animation. This value must be >= 0 where
     *          a value of 0 means no start delay.
     * @param unit
     *          the time unit of the value parameter. A {@code null} value is
     *          equivalent to setting the default unit of
     *          {@link TimeUnit#SECONDS}.
     * @return this builder (to allow chained operations).
     * 
     * @throws IllegalStateException
     *           if value is not >= 0.
     */
    @NonNull
    public Builder setStartDelay(long value, TimeUnit unit) {
      if (value < 0)
        throw new IllegalArgumentException(I18N.err(13, value));

      f_startDelay = value;
      f_startDelayTimeUnit = unit != null ? unit : SECONDS;
      return this;
    }

    /**
     * Copies all values from the passed animator into this builder <b>not
     * including its timing source</b>. The timing targets of the animation are
     * copied as well.
     * <p>
     * This method allows an animation to be used as a template for other
     * animations and can simplify construction of a large number of similar
     * animations.
     * 
     * @param from
     *          an animation, ignored if {@code null}.
     * @return this builder (to allow chained operations).
     * 
     * @see #copy(Animator, boolean)
     */
    @NonNull
    public Builder copy(Animator from) {
      return copy(from, true);
    }

    /**
     * Copies all values from the passed animator into this builder <b>not
     * including its timing source</b>. The timing targets of the animation may
     * optionally be copied as well.
     * <p>
     * This method allows an animation to be used as a template for other
     * animations and can simplify construction of a large number of similar
     * animations.
     * 
     * @param from
     *          an animation, ignored if {@code null}.
     * @param copyTargets
     *          {@code true} if timing targets should also be copied into this
     *          builder, {@code false} if not.
     * @return this builder (to allow chained operations).
     * 
     * @see #copy(Animator)
     */
    @NonNull
    public Builder copy(Animator from, boolean copyTargets) {
      if (from != null) {
        setDebugName(from.getDebugName());
        setDuration(from.getDuration(), from.getDurationTimeUnit());
        setEndBehavior(from.getEndBehavior());
        setInterpolator(from.getInterpolator());
        setRepeatBehavior(from.getRepeatBehavior());
        setRepeatCount(from.getRepeatCount());
        setStartDirection(from.getStartDirection());
        setStartDelay(from.getStartDelay(), from.getStartDelayTimeUnit());
        setDisposeTimingSource(from.getDisposeTimingSource());
        // Warn the user if the auto-dispose setting makes no sense.
        if (f_disposeTimingSource && from.getTimingSource() == f_timingSource) {
          Logger.getAnonymousLogger().log(Level.WARNING, I18N.err(50, f_timingSource.toString()), new Exception());
        }
        if (copyTargets)
          addTargets(from.getTargets());
      }
      return this;
    }

    /**
     * Copies all values from the passed animation builder into this builder
     * <b>not including its timing source</b>. The timing targets of the
     * animation are copied as well.
     * <p>
     * This method allows an animation builder to be used as a template for
     * other animations and can simplify construction of a large number of
     * similar animations.
     * 
     * @param from
     *          an animation builder, ignored if {@code null}.
     * @return this builder (to allow chained operations).
     * 
     * @see #copy(Animator.Builder, boolean)
     */
    @NonNull
    public Builder copy(Builder from) {
      return copy(from, true);
    }

    /**
     * Copies all values from the passed animation builder into this builder
     * <b>not including its timing source</b>. The timing targets of the
     * animation builder may optionally be copied as well.
     * <p>
     * This method allows an animation builder to be used as a template for
     * other animations and can simplify construction of a large number of
     * similar animations.
     * 
     * @param from
     *          an animation builder, ignored if {@code null}.
     * @param copyTargets
     *          {@code true} if timing targets should also be copied into this
     *          builder, {@code false} if not.
     * @return this builder (to allow chained operations).
     * 
     * @see #copy(Animator.Builder)
     */
    @NonNull
    public Builder copy(Builder from, boolean copyTargets) {
      if (from != null) {
        setDebugName(from.f_debugName);
        setDuration(from.f_duration, from.f_durationTimeUnit);
        setEndBehavior(from.f_endBehavior);
        setInterpolator(from.f_interpolator);
        setRepeatBehavior(from.f_repeatBehavior);
        setRepeatCount(from.f_repeatCount);
        setStartDirection(from.f_startDirection);
        setStartDelay(from.f_startDelay, from.f_startDelayTimeUnit);
        setDisposeTimingSource(from.f_disposeTimingSource);
        // Warn the user if the auto-dispose setting makes no sense.
        if (f_disposeTimingSource && from.f_timingSource == f_timingSource) {
          Logger.getAnonymousLogger().log(Level.WARNING, I18N.err(50, f_timingSource.toString()), new Exception());
        }
        if (copyTargets)
          addTargets(from.f_targets);
      }
      return this;
    }

    /**
     * Constructs an animation with the settings defined by this builder.
     * 
     * @return an animation.
     */
    @NonNull
    public Animator build() {
      final Animator result = new Animator(f_debugName, f_duration, f_durationTimeUnit, f_endBehavior, f_interpolator,
          f_repeatBehavior, f_repeatCount, f_startDirection, f_startDelay, f_startDelayTimeUnit, f_timingSource,
          f_disposeTimingSource, f_targets);
      return result;
    }
  }

  /*
   * Immutable state set by the builder. We use "default" to avoid synthetic
   * accessors being generated.
   */

  @Nullable
  final String f_debugName;
  final long f_duration;
  @NonNull
  final TimeUnit f_durationTimeUnit;
  final long f_durationNanos; // calculated
  @NonNull
  final EndBehavior f_endBehavior;
  @NonNull
  final Interpolator f_interpolator;
  @NonNull
  final RepeatBehavior f_repeatBehavior;
  final long f_repeatCount;
  @NonNull
  final Direction f_startDirection;
  final long f_startDelay;
  @NonNull
  final TimeUnit f_startDelayTimeUnit;
  final long f_startDelayNanos; // calculated
  @NonNull
  final TimingSource f_timingSource;
  final boolean f_disposeTimingSource; // at end

  /**
   * Gets the "debug" name of this animation.
   * 
   * @return the "debug" name of this animation. May be {@code null}.
   */
  @Nullable
  @RegionEffects("reads Instance")
  public String getDebugName() {
    return f_debugName;
  }

  /**
   * Gets the duration of one cycle of this animation. The units of this value
   * are obtained by calling {@link #getDurationTimeUnit()}.
   * 
   * @return the duration of the animation. This value must be >= 1 or
   *         {@link Animator#INFINITE}, meaning the animation will run until
   *         manually stopped.
   * 
   * @see #getDurationTimeUnit()
   */
  @RegionEffects("reads Instance")
  public long getDuration() {
    return f_duration;
  }

  /**
   * Gets the time unit of the duration of one cycle of this animation. The
   * duration is obtained by calling {@link #getDuration()}.
   * 
   * @return the time unit of the value parameter.
   * 
   * @see #getDuration()
   */
  @NonNull
  @RegionEffects("reads Instance")
  public TimeUnit getDurationTimeUnit() {
    return f_durationTimeUnit;
  }

  /**
   * Gets the behavior at the end of this animation.
   * 
   * @return the behavior at the end of the animation.
   */
  @NonNull
  @RegionEffects("reads Instance")
  public EndBehavior getEndBehavior() {
    return f_endBehavior;
  }

  /**
   * Gets the interpolator for this animation.
   * 
   * @return the interpolation to use each animation cycle.
   */
  @NonNull
  public Interpolator getInterpolator() {
    return f_interpolator;
  }

  /**
   * Gets the repeat behavior of this animation.
   * 
   * @return the behavior for each successive animation cycle.
   */
  @NonNull
  @RegionEffects("reads Instance")
  public RepeatBehavior getRepeatBehavior() {
    return f_repeatBehavior;
  }

  /**
   * Gets the number of times the animation cycle will repeat.
   * 
   * @return number of times the animation cycle will repeat. This value is >= 1
   *         or {@link Animator#INFINITE} for animations that repeat
   *         indefinitely.
   */
  @RegionEffects("reads Instance")
  public long getRepeatCount() {
    return f_repeatCount;
  }

  /**
   * Gets the start direction for the initial animation cycle.
   * 
   * @return initial animation cycle direction.
   */
  @NonNull
  @RegionEffects("reads Instance")
  public Direction getStartDirection() {
    return f_startDirection;
  }

  /**
   * Gets the start delay of this animation. This is the delay prior to starting
   * the first animation cycle after the call to {@link Animator#start} or
   * {@link Animator#startReverse}. The units of this value are obtained by
   * calling {@link #getStartDelayTimeUnit()}.
   * 
   * @return the start delay of the animation. This value must be >= 0 where a
   *         value of 0 means no start delay.
   * 
   * @see #getStartDelayTimeUnit()
   */
  @RegionEffects("reads Instance")
  public long getStartDelay() {
    return f_startDelay;
  }

  /**
   * Gets the time unit of the start delay of this animation. This is the delay
   * prior to starting the first animation cycle after the call to
   * {@link Animator#start} or {@link Animator#startReverse}. The duration is
   * obtained by calling {@link #getStartDelay()}.
   * 
   * @return the time unit of the value parameter.
   * 
   * @see #getStartDelay()
   */
  @NonNull
  @RegionEffects("reads Instance")
  public TimeUnit getStartDelayTimeUnit() {
    return f_startDelayTimeUnit;
  }

  /**
   * Gets the timing source for this animation.
   * 
   * @return a timing source.
   */
  @NonNull
  @RegionEffects("reads Instance")
  public TimingSource getTimingSource() {
    return f_timingSource;
  }

  /**
   * Gets if this animation will invoke {@link TimingSource#dispose()} on its
   * timing source when it ends.
   * 
   * @return {@code true} if the animation will invoke
   *         {@link TimingSource#dispose()} on its timing source when it ends,
   *         {@code false} if not.
   */
  @RegionEffects("reads Instance")
  public boolean getDisposeTimingSource() {
    return f_disposeTimingSource;
  }

  /*
   * Mutable thread-safe state that is managed by this animation.
   */

  /**
   * This animation may have multiple {@link TimingTarget} listeners.
   * <p>
   * Protects the mutable state of this animation (rather than creating a new
   * Object).
   * <p>
   * Do not hold this lock when invoking any callbacks, e.g., looping through
   * {@link #f_targets}.
   * <p>
   * Do not hold this lock when invoking any method on {@link #f_timingSource}.
   */
  @Vouch("AnnotationBounds")
  final CopyOnWriteArrayList<TimingTarget> f_targets = new CopyOnWriteArrayList<TimingTarget>();

  /**
   * Tracks the original start time in nanoseconds of the animation.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_targets}.
   */
  @InRegion("AnimatorState")
  long f_startTimeNanos;

  /**
   * Tracks start time of current cycle. For the first cycle this time may be in
   * the future if and only if the user set a start delay.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_targets}.
   */
  @InRegion("AnimatorState")
  long f_cycleStartTimeNanos;

  /**
   * Used for pause/resume. If this value is non-zero and the animation is
   * running, then the animation is paused.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_targets}.
   */
  @InRegion("AnimatorState")
  long f_pauseBeginTimeNanos;

  /**
   * The current direction of the animation.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_targets}.
   */
  @NonNull
  @InRegion("AnimatorState")
  Direction f_currentDirection;

  /**
   * Indicates that {@link #reverseNow()} was invoked <i>x</i> times. The actual
   * reverse occurs during the next call to this animation's
   * {@link #timingSourceTick(TimingSource, long)} method so we need to remember
   * how many calls were made.
   */
  @InRegion("AnimatorState")
  int f_reverseNowCallCount;

  /**
   * A latch used to indicate the animation is running and to allow client code
   * to wait until the animation is completed. When this field is non-
   * {@code null} then the animation is running (note that a paused animation is
   * still considered to be running).
   * <p>
   * This field may be non-{@code null} long after {@link #stop()} or
   * {@link #cancel()} are called because the latch is not triggered and changed
   * to a {@code null} value until all callbacks to registered
   * {@link TimingTarget}s have completed. The flag {@link #f_stopping}
   * indicates the animation is in the process of stopping.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_targets}.
   */
  @InRegion("AnimatorState")
  @Nullable
  CountDownLatch f_runningAnimationLatch;

  /**
   * Indicates the animation is stopping &mdash; it is in a shutdown phase. This
   * gets set when the animation completes normally or when {@link #stop()} /
   * {@link #cancel()} are invoked by the client. A value of {@code true}
   * indicates that a running animation is in a shutdown phase and is finishing
   * up any needed callbacks to registered {@link TimingSource}s.
   * <p>
   * This flag is used as a guard so that we don't run try to stop the animation
   * multiple times.
   * <p>
   * This guard is needed because a long period of time can elapse between when
   * the animation knows it is trying to stop and when the callbacks to the
   * client code complete. The animation is still running but should not try to
   * stop again &mdash; avoiding this problem is the purpose of this guard.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_targets}.
   */
  @InRegion("AnimatorState")
  boolean f_stopping;

  /**
   * Constructs an animation.
   * <p>
   * This constructor should only be called from {@link Builder#build()}.
   */
  @Unique("return")
  Animator(String debugName, long duration, @NonNull TimeUnit durationTimeUnit, @NonNull EndBehavior endBehavior,
      @NonNull Interpolator interpolator, @NonNull RepeatBehavior repeatBehavior, long repeatCount,
      @NonNull Direction startDirection, long startDelay, @NonNull TimeUnit startDelayTimeUnit, @NonNull TimingSource timingSource,
      boolean disposeTimingSource, @NonNull Collection<TimingTarget> targets) {
    f_debugName = debugName;
    f_duration = duration;
    f_durationTimeUnit = durationTimeUnit;
    f_endBehavior = endBehavior;
    f_interpolator = interpolator;
    f_repeatBehavior = repeatBehavior;
    f_repeatCount = repeatCount;
    f_startDirection = f_currentDirection = startDirection;
    f_startDelay = startDelay;
    f_startDelayTimeUnit = startDelayTimeUnit;
    f_timingSource = timingSource;
    f_disposeTimingSource = disposeTimingSource;
    f_targets.addAll(targets);

    f_durationNanos = f_durationTimeUnit.toNanos(f_duration);
    f_startDelayNanos = f_startDelayTimeUnit.toNanos(f_startDelay);
  }

  /**
   * Adds a {@link TimingTarget} to the list of targets that get notified of
   * each timing event while the animation is running.
   * <p>
   * This can be done at any time before, during, or after the animation has
   * started or completed; the new target will begin having its methods called
   * as soon as it is added.
   * <p>
   * {@link TimingTarget}s will be called in the order they are added. Duplicate
   * additions are ignored.
   * 
   * @param target
   *          a {@link TimingTarget} object.
   */
  public void addTarget(final TimingTarget target) {
    /*
     * This is complicated because a target can be added after the animation has
     * started. In this case we need to call its begin(Animator) method via a
     * submit(Runnable) call on the timing source.
     * 
     * Because we don't want the state of the animation to change during this
     * call, in particular to start or stop running, we hold the lock. This
     * creates a dependency on how the isRunning() method works or, more
     * specifically, how the animation defines when it is running.
     */
    synchronized (this) {
      if (target != null && !f_targets.contains(target)) {
        if (isRunning()) {
          final Runnable task = new Runnable() {
            public void run() {
              for (TimingTarget target : f_targets) {
                target.begin(Animator.this);
              }
            }
          };
          f_timingSource.submit(task);
        }
        f_targets.add(target);
      }
    }
  }

  /**
   * Adds the collection of passed {@link TimingTarget}s to the list of targets
   * that get notified of each timing event while the animation is running.
   * <p>
   * This can be done at any time before, during, or after the animation has
   * started or completed; the new target will begin having its methods called
   * as soon as it is added.
   * <p>
   * {@link TimingTarget}s will be called in the order they are added. Duplicate
   * additions are ignored.
   * 
   * @param targets
   *          a collection of {@link TimingTarget} objects.
   */
  public void addTargets(Collection<TimingTarget> targets) {
    if (targets != null) {
      synchronized (this) {
        for (TimingTarget target : targets)
          addTarget(target);
      }
    }
  }

  /**
   * Adds the collection of passed {@link TimingTarget}s to the list of targets
   * that get notified of each timing event while the animation is running.
   * <p>
   * This can be done at any time before, during, or after the animation has
   * started or completed; the new target will begin having its methods called
   * as soon as it is added.
   * <p>
   * {@link TimingTarget}s will be called in the order they are added. Duplicate
   * additions are ignored.
   * 
   * @param targets
   *          a collection of {@link TimingTarget} objects.
   */
  public void addTargets(TimingTarget... targets) {
    addTargets(Arrays.asList(targets));
  }

  /**
   * Removes the specified {@link TimingTarget} from the list of targets that
   * get notified of each timing event while the animation is running.
   * <p>
   * This can be done at any time before, during, or after the animation has
   * started or completed; the target will cease having its methods called as
   * soon as it is removed.
   * 
   * @param target
   *          a {@link TimingTarget} object.
   */
  public void removeTarget(TimingTarget target) {
    f_targets.remove(target);
  }

  /**
   * Removes the specified collection of {@link TimingTarget}s from the list of
   * targets that get notified of each timing event while the animation is
   * running.
   * <p>
   * This can be done at any time before, during, or after the animation has
   * started or completed; the target will cease having its methods called as
   * soon as it is removed.
   * 
   * @param targets
   *          a collection of {@link TimingTarget} objects.
   */
  public void removeTargets(Collection<TimingTarget> targets) {
    if (targets != null)
      f_targets.removeAll(targets);
  }

  /**
   * Removes the specified collection of {@link TimingTarget}s from the list of
   * targets that get notified of each timing event while the animation is
   * running.
   * <p>
   * This can be done at any time before, during, or after the animation has
   * started or completed; the target will cease having its methods called as
   * soon as it is removed.
   * 
   * @param targets
   *          a collection of {@link TimingTarget} objects.
   */
  public void removeTargets(TimingTarget... targets) {
    removeTargets(Arrays.asList(targets));
  }

  /**
   * Gets the list of {@link TimingTarget}s that get notified of each timing
   * event while the animation is running.
   * <p>
   * The returned list is a copy and can be mutated freely.
   * 
   * @return the {@link TimingTarget}s of this animation.
   */
  public ArrayList<TimingTarget> getTargets() {
    return new ArrayList<TimingTarget>(f_targets);
  }

  /**
   * Removes all of the elements from from the list of targets that get notified
   * of each timing event while the animation is running.
   * <p>
   * The set of registered {@link TimingTarget} objects will be empty after this
   * call returns.
   */
  public void clearTargets() {
    f_targets.clear();
  }

  /**
   * Starts the animation.
   * 
   * @throws IllegalStateException
   *           if animation is already running; this command may only be run
   *           prior to starting the animation or after the animation has ended.
   */
  public void start() {
    startHelper(f_startDirection, "start()");
  }

  /**
   * Restarts the animation. If the animation is not running than this method is
   * the same as calling {@link #start()}.
   * <p>
   * This call does not block.
   * <p>
   * Shutdown callbacks to any registered {@link TimingTarget} instances are
   * made at the next tick of this animation's timing source, after that time
   * the animation actually restarts. Therefore it is possible to call
   * {@link #restart()} or {@link #restartReverse()} multiple times but only get
   * the effect of a single restart. If this is done then only the first call
   * takes effect, any others are ignored.
   */
  public void restart() {
    synchronized (this) {
      if (isRunning()) {
        stopHelper(true);
        f_timingSource.submit(new Runnable() {
          public void run() {
            synchronized (Animator.this) {
              /*
               * If the animation is running then it has been started by another
               * restart() or restartReverse() call. Guard against throwing an
               * exception in this case, in effect this call is ignored.
               */
              if (!isRunning())
                startHelper(f_startDirection, "restart()");
            }
          }
        });
      } else {
        startHelper(f_startDirection, "restart()");
      }
    }
  }

  /**
   * Starts the animation in the reverse direction.
   * 
   * @throws IllegalStateException
   *           if animation is already running; this command may only be run
   *           prior to starting the animation or after the animation has ended.
   */
  public void startReverse() {
    startHelper(f_startDirection.getOppositeDirection(), "startReverse()");
  }

  /**
   * Restarts the animation in the reverse direction. If the animation is not
   * running than this method is the same as calling {@link #startReverse()}.
   * <p>
   * This call does not block.
   * <p>
   * Shutdown callbacks to any registered {@link TimingTarget} instances are
   * made at the next tick of this animation's timing source, after that time
   * the animation actually restarts. Therefore it is possible to call
   * {@link #restart()} or {@link #restartReverse()} multiple times but only get
   * the effect of a single restart. If this is done then only the first call
   * takes effect, any others are ignored.
   */
  public void restartReverse() {
    synchronized (this) {
      if (isRunning()) {
        stopHelper(true);
        f_timingSource.submit(new Runnable() {
          public void run() {
            synchronized (Animator.this) {
              /*
               * If the animation is running then it has been started by another
               * restart() or restartReverse() call. Guard against throwing an
               * exception in this case, in effect this call is ignored.
               */
              if (!isRunning())
                startHelper(f_startDirection.getOppositeDirection(), "restartReverse()");
            }
          }
        });
      } else {
        startHelper(f_startDirection.getOppositeDirection(), "restartReverse()");
      }
    }
  }

  /**
   * Returns whether this has been started and has not yet completed. An
   * animation is running from when it is started via a call to {@link #start()}
   * or {@link #startReverse()} and when it (a) completes normally, (b)
   * {@link #stop()} is called on it and all callbacks to registered
   * {@link TimingTarget}s have completed, or (c) {@link #cancel()} is called on
   * it.
   * <p>
   * A paused animation is still considered to be running.
   * 
   * @return {@code true} if the animation is running, {@code false} if it is
   *         not.
   */
  @RegionEffects("reads Instance")
  public boolean isRunning() {
    synchronized (this) {
      return f_runningAnimationLatch != null;
    }
  }

  /**
   * Returns the current direction of the animation. If the animation is not
   * running then the value returned will be the starting direction of the
   * animation.
   * 
   * @return the current direction of the animation.
   */
  @NonNull
  @RegionEffects("reads Instance")
  public Direction getCurrentDirection() {
    synchronized (this) {
      return f_currentDirection;
    }
  }

  /**
   * Clients may invoke this method to stop a running animation, however, most
   * animations will stop on their own. If the animation is not running, or is
   * stopping, then this method returns {@code false}.
   * <p>
   * This call does not block.
   * <p>
   * This call will result in calls to the {@link TimingTarget#end(Animator)}
   * method of all the registered timing targets of this animation.
   * <p>
   * The animation takes some period of time to actually stop. The stop started
   * by this call finishes upon the next tick of this animation's timing source.
   * What happens is that {@link TimingTarget#end(Animator)} callbacks to any
   * registered {@link TimingTarget} instances are made at the next tick of this
   * animation's timing source. This is done to ensure all callbacks are made in
   * the thread context of this animation's timing target. This means that the
   * code snippet "{@code a.stop(); a.start();}" could fail throwing an
   * {@link IllegalStateException} at the call to {@link #start()} because the
   * animation is not immediately ready to be started again. If you are trying
   * to wait for the animation to stop so that it can be restarted, then you
   * should use {@link #restart()} or {@link #restartReverse()} which are safe
   * to call in the thread context of this animation's timing source.
   * 
   * @return {@code true} if the animation was running and was successfully
   *         stopped, {@code false} if the animation was not running or was in
   *         the process of stopping and didn't need to be stopped.
   * 
   * @see #cancel()
   */
  public boolean stop() {
    return stopHelper(true);
  }

  /**
   * A convenience method that is equivalent to the code below.
   * 
   * <pre>
   * a.stop();
   * try {
   *   a.await();
   * } catch (InterruptedException ignore) {
   * }
   * </pre>
   * 
   * {@code a} is the animator this method is invoked on.
   * <p>
   * This is a blocking call.
   * <p>
   * <b>Never invoke this method within the thread context of this animation's
   * timing source&mdash;doing so will cause this call to block forever.</b> If
   * you are trying to wait for the animation to stop so that it can be
   * restarted, then you should use {@link #restart()} or
   * {@link #restartReverse()} which are safe to call in the thread context of
   * this animation's timing source.
   * <p>
   * This method is primarily intended for testing.
   */
  public void stopAndAwait() {
    stop();
    try {
      await();
    } catch (InterruptedException ignore) {
    }
  }

  /**
   * This method is like the {@link #stop} method, only this one will not result
   * in a calls to the {@link TimingTarget#end(Animator)} method of all the
   * registered timing targets of this animation; it simply stops the animation
   * immediately and returns. If the animation is not running, or is stopping,
   * then this method returns {@code false}.
   * <p>
   * This call does not block.
   * <p>
   * The animation may take some period of time to actually cancel. The cancel
   * started by this call finishes upon the next tick of this animation's timing
   * source. This means that the code snippet "{@code a.cancel(); a.start();}"
   * could fail throwing an {@link IllegalStateException} at the call to
   * {@link #start()} because the animation is not immediately ready to be
   * started again. If you are trying to wait for the animation to stop so that
   * it can be restarted, then you should use {@link #restart()} or
   * {@link #restartReverse()} which are safe to call in the thread context of
   * this animation's timing source.
   * 
   * @return {@code true} if the animation was running and was successfully
   *         stopped, {@code false} if the animation was not running or was in
   *         the process of stopping and didn't need to be stopped.
   * 
   * @see #stop()
   */
  public boolean cancel() {
    return stopHelper(false);
  }

  /**
   * A convenience method that is equivalent to the code below.
   * 
   * <pre>
   * a.cancel();
   * try {
   *   a.await();
   * } catch (InterruptedException ignore) {
   * }
   * </pre>
   * 
   * {@code a} is the animator this method is invoked on.
   * <p>
   * This is a blocking call. Never invoke this method within the thread context
   * of this animation's timing source&mdash;doing so will cause this call to
   * block forever.
   * <p>
   * This method is primarily intended for testing.
   */
  public void cancelAndAwait() {
    cancel();
    try {
      await();
    } catch (InterruptedException ignore) {
    }
  }

  /**
   * This method pauses a running animation. No further events are sent to
   * registered timing targets. A paused animation may be started again by
   * calling the {@link #resume} method.
   * <p>
   * Pausing a non-running, stopping, or already paused animation has no effect.
   * 
   * @see #resume()
   * @see #isRunning()
   * @see #isPaused()
   */
  public void pause() {
    synchronized (this) {
      final boolean canPause = isRunning() && !f_stopping && f_pauseBeginTimeNanos == 0;
      if (canPause) {
        f_timingSource.removeTickListener(this);
        f_pauseBeginTimeNanos = System.nanoTime();
      }
    }
  }

  /**
   * Returns whether this animation is currently running &mdash; but paused. If
   * the animation is not running or is in the process of stopping {@code false}
   * is returned.
   * 
   * @return {@code true} if the animation is currently running &mdash; but
   *         paused, {@code false} otherwise.
   */
  public boolean isPaused() {
    synchronized (this) {
      return isRunning() && !f_stopping && f_pauseBeginTimeNanos > 0;
    }
  }

  /**
   * This method resumes a paused animation. Resuming an animation that is not
   * paused has no effect.
   * 
   * @see #pause()
   */
  public void resume() {
    synchronized (this) {
      final boolean paused = isPaused();
      if (paused) {
        long pauseDeltaNanos = System.nanoTime() - f_pauseBeginTimeNanos;
        f_startTimeNanos += pauseDeltaNanos;
        f_cycleStartTimeNanos += pauseDeltaNanos;
        f_pauseBeginTimeNanos = 0;
        f_timingSource.addTickListener(this);
      }
    }
  }

  /**
   * Reverses the direction of the animation if it is running and is not paused
   * or stopping. If it is not possible to reverse the animation now, the method
   * returns {@code false}.
   * <p>
   * The actual reverse occurs at the next tick of this animation's
   * {@link TimingSource}. All calls are remembered, however, so no successful
   * reversals are lost.
   * 
   * @return {@code true} if the animation was successfully reversed,
   *         {@code false} if the attempt to reverse the animation failed.
   */
  public boolean reverseNow() {
    synchronized (this) {
      final boolean canReverse = isRunning() && !f_stopping && f_pauseBeginTimeNanos == 0;
      if (canReverse) {
        f_reverseNowCallCount++;
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * Causes the current thread to wait until the animation completes, either on
   * its own or due to a call to {@link #stop()} or {@link #cancel()}, unless
   * the thread is {@linkplain Thread#interrupt interrupted}. All callbacks to
   * registered {@link TimingTarget}s have been completed when this method
   * returns (unless, as noted above, the thread is
   * {@linkplain Thread#interrupt interrupted}).
   * <p>
   * If the animation is not running then this method returns immediately,
   * otherwise this is a blocking call.
   * <p>
   * <b>Never invoke this method within the thread context of this animation's
   * timing source&mdash;doing so will cause this call to block forever.</b> If
   * you are trying to wait for the animation to stop so that it can be
   * restarted, then you should use {@link #restart()} or
   * {@link #restartReverse()} which are safe to call in the thread context of
   * this animation's timing source.
   * <p>
   * If the current thread:
   * <ul>
   * <li>has its interrupted status set on entry to this method; or
   * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
   * </ul>
   * then {@link InterruptedException} is thrown and the current thread's
   * interrupted status is cleared.
   * <p>
   * This method is primarily intended for testing.
   * 
   * @throws InterruptedException
   *           if the current thread is interrupted while waiting.
   */
  public void await() throws InterruptedException {
    final CountDownLatch latch;
    synchronized (this) {
      latch = f_runningAnimationLatch;
    }
    if (latch != null)
      latch.await();
  }

  /**
   * Returns the elapsed time in nanoseconds for the current animation
   * cycle.Uses {@link System#nanoTime()} to get the current time.
   * <p>
   * If a start delay is set on this animation, then it is possible that the
   * value returned from this method will be negative. This situation occurs
   * when the animation has been started but is waiting for the start delay to
   * elapse.
   * 
   * @return the time elapsed in nanoseconds between the time the current
   *         animation cycle started and the current time.
   */
  public long getCycleElapsedTime() {
    return getCycleElapsedTime(System.nanoTime());
  }

  /**
   * Returns the elapsed time in nanoseconds for the current animation cycle
   * from the passed time.
   * <p>
   * If a start delay is set on this animation, then it is possible that the
   * value returned from this method will be negative. This situation occurs
   * when the animation has been started but is waiting for the start delay to
   * elapse.
   * 
   * @param currentTimeNanos
   *          value of current time, from {@link System#nanoTime()}, to use in
   *          calculating the elapsed time.
   * @return the time elapsed in nanoseconds between the time this cycle started
   *         and the passed time.
   */
  @RegionEffects("reads Instance")
  public long getCycleElapsedTime(long currentTimeNanos) {
    synchronized (this) {
      return (currentTimeNanos - f_cycleStartTimeNanos);
    }
  }

  /**
   * Returns the total elapsed time in nanoseconds for the current animation.
   * Uses {@link System#nanoTime()} to get the current time.
   * <p>
   * This value does not consider any start delay, it simple returns the time
   * elapsed from when the animation was started until now.
   * 
   * @return the total time elapsed in nanoseconds between the time this
   *         animation started and the current time.
   */
  public long getTotalElapsedTime() {
    return getTotalElapsedTime(System.nanoTime());
  }

  /**
   * Returns the total elapsed time in nanoseconds for the current animation
   * from the passed time.
   * <p>
   * This value does not consider any start delay, it simple returns the time
   * elapsed from when the animation was started until the passed time.
   * 
   * @param currentTimeNanos
   *          value of current time, from {@link System#nanoTime()}, to use in
   *          calculating elapsed time.
   * @return the total time elapsed between the time this animation started and
   *         the passed time.
   */
  @RegionEffects("reads Instance")
  public long getTotalElapsedTime(long currentTimeNanos) {
    synchronized (this) {
      return (currentTimeNanos - f_startTimeNanos);
    }
  }

  @Override
  @NonNull
  @RegionEffects("reads Instance")
  @Vouch("Uses StringBuilder")
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(Animator.class.getSimpleName()).append('@');
    b.append(f_debugName != null ? f_debugName : Integer.toHexString(hashCode()));
    b.append("(duration=").append(f_duration).append(' ').append(f_durationTimeUnit.toString());
    b.append(", interpolator=").append(getInterpolator().toString());
    b.append(", startDirection=").append(f_startDirection.toString());
    b.append(", repeatBehavior=").append(f_repeatBehavior.toString());
    b.append(", repeatCount=").append(f_repeatCount);
    b.append(", endBehavior=").append(f_endBehavior.toString());
    b.append(", startDelay=").append(f_startDelay).append(' ').append(f_startDelayTimeUnit.toString());
    b.append(", timingSource=").append(f_timingSource.toString());
    b.append(')');
    return b.toString();
  }

  /**
   * Factors out common code between {@link #start()} and
   * {@link #startReverse()}.
   * 
   * @param direction
   *          the direction to start the animation going in.
   * @param methodName
   *          the short name of the calling method, used only for error
   *          reporting.
   * 
   * @throws IllegalStateException
   *           if the this animation is already running.
   */
  void startHelper(Direction direction, String methodName) {
    synchronized (this) {
      if (isRunning())
        throw new IllegalStateException(I18N.err(12, methodName));

      final long nanoTime = System.nanoTime();
      f_startTimeNanos = nanoTime;
      f_cycleStartTimeNanos = nanoTime + f_startDelayNanos;
      f_currentDirection = direction;
      f_stopping = false;
      f_pauseBeginTimeNanos = f_reverseNowCallCount = 0;
      f_runningAnimationLatch = new CountDownLatch(1);
      /*
       * Because the submit() call only places the Runnable into a queue holding
       * the lock below cannot lead to a deadlock.
       * 
       * Holding the lock is not really necessary, but it makes this code
       * similar to reverseNow() (where holding the lock is critical to correct
       * behavior).
       */
      if (!f_targets.isEmpty()) {
        final Runnable task = new Runnable() {
          public void run() {
            for (TimingTarget target : f_targets) {
              target.begin(Animator.this);
            }
          }
        };
        f_timingSource.submit(task);
      }
    }
    f_timingSource.addTickListener(this);
  }

  /**
   * Helper routine to stop the running animation. It optionally invokes the
   * {@link TimingTarget#end(Animator)} method of registered timing targets in
   * the correct thread context. If the animation was not running (or is already
   * stopping) then this method returns {@code false}.
   * 
   * @param notify
   *          {@code true} if the {@link TimingTarget#end(Animator)} method
   *          should be called for registered timing targets, {@code false} if
   *          calls should not be made.
   * 
   * @return {@code true} if the animation was running and was successfully
   *         stopped, {@code false} if the animation was not running or was in
   *         the process of stopping and didn't need to be stopped.
   */
  boolean stopHelper(final boolean notify) {
    synchronized (this) {
      /*
       * If we are not running at all we return immediately.
       */
      if (f_runningAnimationLatch == null)
        return false;
      /*
       * If we are already stopping we return immediately.
       */
      if (f_stopping)
        return false;

      f_stopping = true;
    }
    f_timingSource.removeTickListener(this);
    final Runnable task = new Runnable() {
      public void run() {
        try {
          if (f_disposeTimingSource)
            f_timingSource.dispose();
          if (notify)
            for (TimingTarget target : f_targets) {
              target.end(Animator.this);
            }
        } finally {
          latchCountDown();
        }
      }
    };
    f_timingSource.submit(task);
    return true;
  }

  /**
   * Helper routine to trip the latch to notify anyone blocked on
   * {@link #await()} after the animation is ready to be completely stopped.
   * <p>
   * {@link #f_targets} should NOT be held when invoking this method.
   */
  void latchCountDown() {
    final CountDownLatch latch;
    synchronized (this) {
      latch = f_runningAnimationLatch;
      f_runningAnimationLatch = null;
    }
    latch.countDown();
  }

  /**
   * Not intended for use by client code.
   */
  public void timingSourceTick(TimingSource source, long nanoTime) {
    /*
     * Implementation note: This is a big method, however, breaking it up
     * requires the introduction of several fields that are really
     * implementation details of the calculations below and flags about what to
     * do next.
     */
    final double fraction;
    boolean timeToStop = false;
    boolean notifyRepeat = false;
    boolean notifyOfReverse = false;
    synchronized (this) {
      /*
       * A guard against running logic within this method if any of the
       * following conditions are true:
       * 
       * o The animation is not running
       * 
       * o The animation is stopping
       * 
       * o The animation is paused
       * 
       * o The animation is waiting for its start delay to elapse
       */
      final boolean skipTick = f_runningAnimationLatch == null || f_stopping || f_pauseBeginTimeNanos != 0
          || f_cycleStartTimeNanos >= nanoTime;
      if (skipTick)
        return;

      /*
       * Note that we need to notify of a reverseNow() call and reset the field.
       */
      if (f_reverseNowCallCount > 0) {
        notifyOfReverse = true;
        final boolean reverseCallsCancelOut = /* isEven */(f_reverseNowCallCount & 1) == 0;
        f_reverseNowCallCount = 0; // reset

        if (!reverseCallsCancelOut) {
          final long cycleElapsedTimeNanos = getCycleElapsedTime(nanoTime);
          final long timeLeft = f_durationNanos - cycleElapsedTimeNanos;
          final long deltaNanos = (nanoTime - timeLeft) - f_cycleStartTimeNanos;
          f_cycleStartTimeNanos += deltaNanos;
          f_startTimeNanos += deltaNanos;
          f_currentDirection = f_currentDirection.getOppositeDirection();
        }
      }

      /*
       * This code calculates and returns the fraction elapsed of the current
       * cycle based on the current time and the {@link Interpolator} used by
       * the animation.
       */
      final long cycleElapsedTimeNanos = getCycleElapsedTime(nanoTime);
      final long currentCycleCount = (getTotalElapsedTime(nanoTime) - f_startDelayNanos) / f_durationNanos;

      double fractionScratch;

      if (f_repeatCount != INFINITE && currentCycleCount >= f_repeatCount) {
        /*
         * Animation End: Stop based on specified end behavior.
         */
        switch (f_endBehavior) {
        case HOLD:
          /*
           * HOLD requires setting the final end value.
           */
          if (f_currentDirection == Direction.BACKWARD) {
            fractionScratch = 0;
          } else {
            fractionScratch = 1;
          }
          break;
        case RESET:
          /*
           * RESET requires setting the final value to the start value.
           */
          fractionScratch = 0;
          break;
        default:
          throw new IllegalStateException(I18N.err(2, EndBehavior.class.getName(), f_endBehavior.toString()));
        }
        timeToStop = true;
      } else if (cycleElapsedTimeNanos > f_durationNanos) {
        /*
         * Animation Cycle End: Time to stop or change the behavior of the
         * timer.
         */
        final long overCycleTimeNanos = cycleElapsedTimeNanos % f_durationNanos;
        fractionScratch = (double) overCycleTimeNanos / (double) f_durationNanos;
        /*
         * Set a new start time for this cycle.
         */
        f_cycleStartTimeNanos = nanoTime - overCycleTimeNanos;

        if (f_repeatBehavior == RepeatBehavior.REVERSE) {
          /*
           * Reverse the direction of the animation.
           */
          f_currentDirection = f_currentDirection.getOppositeDirection();
        }
        if (f_currentDirection == Direction.BACKWARD) {
          fractionScratch = 1 - fractionScratch;
        }
        notifyRepeat = true;
      } else {
        /*
         * Animation Mid-Stream: Calculate fraction of animation between start
         * and end times and send fraction to target.
         */
        fractionScratch = (double) cycleElapsedTimeNanos / (double) f_durationNanos;
        if (f_currentDirection == Direction.BACKWARD) {
          /*
           * If this is a backwards cycle, want to send the inverse fraction;
           * how much from start to finish, not finish to start.
           */
          fractionScratch = 1.0 - fractionScratch;
        }
        /*
         * Clamp fraction in case timing mechanism caused out of bounds value.
         */
        fractionScratch = Math.min(fractionScratch, 1.0);
        fractionScratch = Math.max(fractionScratch, 0.0);
      }
      fraction = f_interpolator.interpolate(fractionScratch);
    } // lock release

    if (notifyOfReverse && !f_targets.isEmpty()) {
      for (TimingTarget target : f_targets) {
        target.reverse(this);
      }
    }
    if (notifyRepeat && !f_targets.isEmpty()) {
      for (TimingTarget target : f_targets) {
        target.repeat(this);
      }
    }
    if (!f_targets.isEmpty())
      for (TimingTarget target : f_targets) {
        target.timingEvent(this, fraction);
      }
    if (timeToStop) {
      stopHelper(true);
    }
  }
}
