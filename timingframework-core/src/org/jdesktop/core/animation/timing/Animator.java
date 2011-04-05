package org.jdesktop.core.animation.timing;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;

import com.surelogic.InRegion;
import com.surelogic.Region;
import com.surelogic.RegionLock;
import com.surelogic.RequiresLock;
import com.surelogic.ThreadSafe;
import com.surelogic.Unique;

/**
 * This class controls animations. Instances are constructed by a
 * {@link AnimatorBuilder} instance by invoking various set methods control the
 * parameters under which the desired animation is run. The parameters of this
 * class use the concepts of a "cycle" (the base animation) and an "envelope"
 * that controls how the cycle is started, ended, and repeated.
 * <p>
 * For example, this animation will run for 1 second, calling your
 * {@link TimingTarget}, {@code myTarget}, with timing events when the animation
 * is started, running, and stopped:
 * 
 * <pre>
 * AnimatorBuilder.setDefaultTimingSource(source); // shared timing source
 * 
 * Animator animator = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).addTarget(myTarget).build();
 * animator.start();
 * </pre>
 * 
 * The following variation will run a half-second animation 4 times, reversing
 * direction each time:
 * 
 * <pre>
 * Animator animator = new AnimatorBuilder().setDuration(500, TimeUnit.MILLISECONDS).setRepeatCount(4).addTarget(myTarget).build();
 * animator.start();
 * </pre>
 * 
 * More complex animations can be created through the use of the complete set of
 * properties in {@link AnimatorBuilder}.
 * <p>
 * This class is thread-safe.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 * 
 * @see AnimatorBuilder
 */
@ThreadSafe
@Region("private AnimatorState")
@RegionLock("AnimatorLock is f_lock protects AnimatorState")
public final class Animator implements TickListener {

  /**
   * EndBehavior determines what happens at the end of the animation.
   * 
   * @see AnimatorBuilder#setEndBehavior(Animator.EndBehavior)
   */
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
   * @see AnimatorBuilder#setStartDirection(Animator.Direction)
   */
  public static enum Direction {
    /**
     * The cycle proceeds forward.
     */
    FORWARD,
    /**
     * The cycle proceeds backward.
     */
    BACKWARD,
  };

  /**
   * RepeatBehavior determines how each successive cycle will flow.
   * 
   * @see AnimatorBuilder#setRepeatBehavior(Animator.RepeatBehavior)
   */
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
   * Used to specify unending duration or repeat count.
   * 
   * @see AnimatorBuilder#setDuration(long, java.util.concurrent.TimeUnit)
   * @see AnimatorBuilder#setRepeatCount(long)
   * */
  public static final long INFINITE = -1;

  /*
   * Immutable state set by the builder.
   */

  private final long f_duration;
  private final TimeUnit f_durationTimeUnit;
  private final EndBehavior f_endBehavior;
  private final Interpolator f_interpolator;
  private final RepeatBehavior f_repeatBehavior;
  private final long f_repeatCount;
  private final Direction f_startDirection;
  private final TimingSource f_timingSource;

  /**
   * Gets the duration of this animation. The units of this value are obtained
   * by calling {@link #getDurationTimeUnit()}.
   * 
   * @return the duration of the animation. This value must be >= 1 or
   *         {@link Animator#INFINITE}, meaning the animation will run until
   *         manually stopped.
   * 
   * @see #getDurationTimeUnit()
   */
  public long getDuration() {
    return f_duration;
  }

  /**
   * Gets the time unit of the duration of this animation. The duration is
   * obtained by calling {@link #getDuration()}.
   * 
   * @return the time unit of the value parameter.
   * 
   * @see #getDuration()
   */
  public TimeUnit getDurationTimeUnit() {
    return f_durationTimeUnit;
  }

  /**
   * Gets the behavior at the end of this animation.
   * 
   * @return the behavior at the end of the animation.
   */
  public EndBehavior getEndBehavior() {
    return f_endBehavior;
  }

  /**
   * Gets the interpolator for this animation.
   * 
   * @return the interpolation to use each animation cycle.
   */
  public Interpolator getInterpolator() {
    return f_interpolator;
  }

  /**
   * Gets the repeat behavior of this animation.
   * 
   * @return the behavior for each successive animation cycle.
   */
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
  public long getRepeatCount() {
    return f_repeatCount;
  }

  /**
   * Gets the start direction for the initial animation cycle.
   * 
   * @return initial animation cycle direction.
   */
  public Direction getStartDirection() {
    return f_startDirection;
  }

  /**
   * Gets the timing source for this animation.
   * 
   * @return a timing source.
   */
  public TimingSource getTimingSource() {
    return f_timingSource;
  }

  /*
   * Mutable thread-safe state that is managed by this animation.
   */

  /**
   * This animation may have multiple {@link TimingTarget} listeners.
   */
  private final CopyOnWriteArraySet<TimingTarget> f_targets = new CopyOnWriteArraySet<TimingTarget>();

  /**
   * Protects the mutable state of this animation.
   * <p>
   * Do not hold this lock when invoking any callbacks, e.g., looping through
   * {@link #f_targets}.
   * <p>
   * Do not hold this lock when invoking any method on {@link #f_timingSource}.
   */
  private final Object f_lock = new Object();

  /**
   * Tracks the original start time in nanoseconds of the animation.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_lock}.
   */
  @InRegion("AnimatorState")
  private long f_startTimeNanos;

  /**
   * Tracks start time of current cycle.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_lock}.
   */
  @InRegion("AnimatorState")
  private long f_cycleStartTimeNanos;

  /**
   * Ensures that listeners are notified that the animation has begun.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_lock}.
   */
  @InRegion("AnimatorState")
  private boolean f_listenersToldAboutBegin;

  /**
   * This get triggered during fraction calculation.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_lock}.
   */
  @InRegion("AnimatorState")
  private boolean f_timeToStop;

  /**
   * This get triggered during fraction calculation.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_lock}.
   */
  @InRegion("AnimatorState")
  private boolean f_tellListenersAboutRepeat;

  /**
   * This get triggered if {@link Animator#reverseNow()} is invoked.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_lock}.
   */
  @InRegion("AnimatorState")
  private boolean f_tellListenersAboutReverse;

  /**
   * Used for pause/resume.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_lock}.
   */
  @InRegion("AnimatorState")
  private long f_pauseBeginTimeNanos;

  /**
   * Indicates that the animation is running.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_lock}.
   */
  @InRegion("AnimatorState")
  private boolean f_running = false;

  /**
   * The current direction of the animation.
   * <p>
   * Accesses must be guarded by a lock on {@link #f_lock}.
   */
  @InRegion("AnimatorState")
  private Direction f_currentDirection;

  /**
   * Constructs an animation.
   * <p>
   * This constructor should only be called from {@link AnimatorBuilder#build()}.
   */
  @Unique("return")
  Animator(long duration, TimeUnit durationTimeUnit, EndBehavior endBehavior, Interpolator interpolator,
      RepeatBehavior repeatBehavior, long repeatCount, Direction startDirection, TimingSource timingSource) {
    f_duration = duration;
    f_durationTimeUnit = durationTimeUnit;
    f_endBehavior = endBehavior;
    f_interpolator = interpolator;
    f_repeatBehavior = repeatBehavior;
    f_repeatCount = repeatCount;
    f_startDirection = f_currentDirection = startDirection;
    f_timingSource = timingSource;
  }

  /**
   * Adds a {@link TimingTarget} to the list of targets that get notified of
   * each timing event while the animation is running.
   * <p>
   * This can be done at any time before, during, or after the animation has
   * started or completed; the new target will begin having its methods called
   * as soon as it is added. If {@code target} is already on the list of targets
   * in this animation, it is not added again (there will be only one instance
   * of any given target in any animation's list of targets).
   * 
   * @param target
   *          a {@link TimingTarget} object.
   */
  public void addTarget(TimingTarget target) {
    if (target != null)
      f_targets.add(target);
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
    synchronized (f_lock) {
      if (f_running)
        throw new IllegalStateException(I18N.err(12, "start()"));

      f_startTimeNanos = f_cycleStartTimeNanos = System.nanoTime();
      f_listenersToldAboutBegin = f_timeToStop = f_tellListenersAboutRepeat = f_tellListenersAboutReverse = false;
      f_currentDirection = f_startDirection;
      f_pauseBeginTimeNanos = 0;
      f_running = true;
    }
    f_timingSource.addTickListener(this);
  }

  /**
   * Starts the animation in the reverse direction.
   * 
   * @throws IllegalStateException
   *           if animation is already running; this command may only be run
   *           prior to starting the animation or after the animation has ended.
   */
  public void startReverse() {
    synchronized (f_lock) {
      if (f_running)
        throw new IllegalStateException(I18N.err(12, "startReverse()"));

      f_startTimeNanos = f_cycleStartTimeNanos = System.nanoTime();
      f_listenersToldAboutBegin = f_timeToStop = f_tellListenersAboutRepeat = f_tellListenersAboutReverse = false;
      f_currentDirection = (f_startDirection == Direction.FORWARD) ? Direction.BACKWARD : Direction.FORWARD;
      f_pauseBeginTimeNanos = 0;
      f_running = true;
    }
    f_timingSource.addTickListener(this);
  }

  /**
   * Returns whether this animation is currently running.
   * 
   * @return {@code true} if the animation is running, {@code false} if it is
   *         not.
   */
  public boolean isRunning() {
    synchronized (f_lock) {
      return f_running;
    }
  }

  /**
   * Returns the current direction of the animation. If the animation is not
   * running then the value returned will be the starting direction of the
   * animation.
   * 
   * @return the current direction of the animation.
   */
  public Direction getCurrentDirection() {
    synchronized (f_lock) {
      return f_currentDirection;
    }
  }

  /**
   * Clients may invoke this method to stop a running animation, however, most
   * animations will stop on their own.
   * <p>
   * This call will result in calls to the {@link TimingTarget#end(Animator)}
   * method of all the registered {@link TimingTargets} of this animation.
   * 
   * @see #cancel()
   */
  public void stop() {
    stopHelper(true, false);
  }

  /**
   * This method is like the {@link #stop} method, only this one will not result
   * in a calls to the <code>end()</code> method in all TimingTargets of this
   * Animation; it simply cancels the Animator immediately.
   * 
   * @see #stop()
   */
  public void cancel() {
    stopHelper(false, false);
  }

  /**
   * This method pauses a running animation. No further events are sent to
   * TimingTargets. A paused animation may be started again by calling the
   * {@link #resume} method. Pausing a non-running animation has no effect.
   * 
   * @see #resume()
   * @see #isRunning()
   */
  public void pause() {
    if (isRunning()) {
      f_timingSource.removeTickListener(this);
      synchronized (f_lock) {
        f_pauseBeginTimeNanos = System.nanoTime();
        f_running = false;
      }
    }
  }

  /**
   * Returns whether this animation is currently paused.
   * 
   * @return {@code true} if the animation is paused, {@code false} if it is
   *         not.
   */
  public boolean isPaused() {
    synchronized (f_lock) {
      return !f_running && f_pauseBeginTimeNanos > 0;
    }
  }

  /**
   * This method resumes a paused animation. Resuming an animation that is not
   * paused has no effect.
   * 
   * @see #pause()
   */
  public void resume() {
    final boolean paused;
    synchronized (f_lock) {
      paused = f_pauseBeginTimeNanos > 0;
      if (paused) {
        long pauseDeltaNanos = System.nanoTime() - f_pauseBeginTimeNanos;
        f_startTimeNanos += pauseDeltaNanos;
        f_cycleStartTimeNanos += pauseDeltaNanos;
        f_pauseBeginTimeNanos = 0;
        f_running = true;
      }
    }
    if (paused) {
      f_timingSource.addTickListener(this);
    }
  }

  /**
   * Reverses the direction of the running animation.
   * 
   * @throws IllegalStateException
   *           if the animation is not running.
   */
  public void reverseNow() {
    synchronized (f_lock) {
      if (!f_running)
        throw new IllegalStateException(I18N.err(13, "reverseNow()"));

      final long now = System.nanoTime();
      final long cycleElapsedTimeNanos = getCycleElapsedTime(now);
      final long durationNanos = f_durationTimeUnit.toNanos(f_duration);
      final long timeLeft = durationNanos - cycleElapsedTimeNanos;
      final long deltaNanos = (now - timeLeft) - f_cycleStartTimeNanos;
      f_cycleStartTimeNanos += deltaNanos;
      f_startTimeNanos += deltaNanos;
      /*
       * Reverse the direction of the animation.
       */
      f_currentDirection = (f_currentDirection == Direction.FORWARD) ? Direction.BACKWARD : Direction.FORWARD;
      f_tellListenersAboutReverse = true;
    }
  }

  /**
   * Returns the elapsed time in nanoseconds for the current animation
   * cycle.Uses {@link System#nanoTime()} to get the current time.
   * 
   * @return the time elapsed in nanoseconds between the time the current
   *         animation cycle started and the current time.
   */
  public long getCycleElapsedTime() {
    return getCycleElapsedTime(System.nanoTime());
  }

  /**
   * Returns the elapsed time in nanoseconds for the current animation cycle.
   * 
   * @param currentTimeNanos
   *          value of current time, from {@link System#nanoTime()}, to use in
   *          calculating the elapsed time.
   * @return the time elapsed in nanoseconds between the time this cycle started
   *         and the passed time.
   */
  public long getCycleElapsedTime(long currentTimeNanos) {
    synchronized (f_lock) {
      return (currentTimeNanos - f_cycleStartTimeNanos);
    }
  }

  /**
   * Returns the total elapsed time in nanoseconds for the current animation.
   * Uses {@link System#nanoTime()} to get the current time.
   * 
   * @return the total time elapsed in nanoseconds between the time this
   *         animation started and the current time.
   */
  public long getTotalElapsedTime() {
    return getTotalElapsedTime(System.nanoTime());
  }

  /**
   * Returns the total elapsed time in nanoseconds for the current animation.
   * 
   * @param currentTimeNanos
   *          value of current time, from {@link System#nanoTime()}, to use in
   *          calculating elapsed time.
   * @return the total time elapsed between the time the Animator started and
   *         the passed time.
   */
  public long getTotalElapsedTime(long currentTimeNanos) {
    synchronized (f_lock) {
      return (currentTimeNanos - f_startTimeNanos);
    }
  }

  /**
   * Do not call this method holding {@link #f_lock}.
   * 
   * @param nanoTime
   *          the current value of the most precise available system timer, in
   *          nanoseconds.
   */
  private void notifyListenersAboutATimingSourceTick(long nanoTime) {
    /*
     * We can't hold f_lock when we invoke callbacks so we calculate results
     * from the mutable state and save it in local variables.
     */
    final double fraction;
    final boolean timeToStop;
    final boolean notifyBegin;
    final boolean notifyRepeat;
    final boolean notifyReverse;
    synchronized (f_lock) {
      fraction = calcInterpolatedTimingFraction(nanoTime);
      timeToStop = f_timeToStop;
      if (!f_listenersToldAboutBegin) {
        notifyBegin = true;
        f_listenersToldAboutBegin = true;
      } else {
        notifyBegin = false;
      }
      if (f_tellListenersAboutRepeat) {
        notifyRepeat = true;
        f_tellListenersAboutRepeat = false;
      } else {
        notifyRepeat = false;
      }
      if (f_tellListenersAboutReverse) {
        notifyReverse = true;
        f_tellListenersAboutReverse = false;
      } else {
        notifyReverse = false;
      }
    }
    if (notifyBegin) {
      if (!f_targets.isEmpty())
        for (TimingTarget target : f_targets) {
          target.begin(this);
        }
    }
    if (notifyRepeat) {
      if (!f_targets.isEmpty())
        for (TimingTarget target : f_targets) {
          target.repeat(this);
        }
    }
    if (notifyReverse) {
      if (!f_targets.isEmpty())
        for (TimingTarget target : f_targets) {
          target.reverse(this);
        }
    }
    if (!f_targets.isEmpty())
      for (TimingTarget target : f_targets) {
        target.timingEvent(this, fraction);
      }
    if (timeToStop) {
      stopHelper(true, true);
    }
  }

  /**
   * This method calculates and returns the fraction elapsed of the current
   * cycle based on the current time and the {@link Interpolator} used by the
   * animation.
   * <p>
   * {@link #f_lock} should be held when invoking this method.
   * 
   * @param nanoTime
   *          the current value of the most precise available system timer, in
   *          nanoseconds.
   * @return fraction elapsed of the current animation cycle.
   */
  @RequiresLock("AnimatorLock")
  private double calcInterpolatedTimingFraction(long nanoTime) {
    final long cycleElapsedTimeNanos = getCycleElapsedTime(nanoTime);
    final long totalElapsedTimeNanos = getTotalElapsedTime(nanoTime);
    final long durationNanos = f_durationTimeUnit.toNanos(f_duration);
    final long currentCycleCount = totalElapsedTimeNanos / durationNanos;

    double fraction;

    if ((f_duration != INFINITE) && (f_repeatCount != INFINITE) && (currentCycleCount >= f_repeatCount)) {
      /*
       * Animation End: Stop based on specified end behavior.
       */
      switch (f_endBehavior) {
      case HOLD:
        /*
         * HOLD requires setting the final end value.
         */
        if (f_currentDirection == Direction.BACKWARD) {
          fraction = 0;
        } else {
          fraction = 1;
        }
        break;
      case RESET:
        /*
         * RESET requires setting the final value to the start value.
         */
        fraction = 0;
        break;
      default:
        throw new IllegalStateException(I18N.err(2, EndBehavior.class.getName(), f_endBehavior.toString()));
      }
      f_timeToStop = true;
    } else if ((f_duration != INFINITE) && (cycleElapsedTimeNanos > durationNanos)) {
      /*
       * Animation Cycle End: Time to stop or change the behavior of the timer.
       */
      long overCycleTimeNanos = cycleElapsedTimeNanos % durationNanos;
      fraction = (double) overCycleTimeNanos / durationNanos;
      /*
       * Set a new start time for this cycle.
       */
      f_cycleStartTimeNanos = nanoTime - overCycleTimeNanos;

      if (f_repeatBehavior == RepeatBehavior.REVERSE) {
        /*
         * Reverse the direction of the animation.
         */
        f_currentDirection = (f_currentDirection == Direction.FORWARD) ? Direction.BACKWARD : Direction.FORWARD;
      }
      if (f_currentDirection == Direction.BACKWARD) {
        fraction = 1 - fraction;
      }
      f_tellListenersAboutRepeat = true;
    } else {
      /*
       * Animation Mid-Stream: Calculate fraction of animation between start and
       * end times and send fraction to target.
       */
      fraction = 0;
      if (f_duration != INFINITE) {
        /*
         * Only limited duration animations need a fraction.
         */
        fraction = (double) cycleElapsedTimeNanos / (double) durationNanos;
        if (f_currentDirection == Direction.BACKWARD) {
          /*
           * If this is a reversing cycle, want to know inverse fraction; how
           * much from start to finish, not finish to start.
           */
          fraction = 1.0 - fraction;
        }
        /*
         * Clamp fraction in case timing mechanism caused out of bounds value.
         */
        fraction = Math.min(fraction, 1.0);
        fraction = Math.max(fraction, 0.0);
      }
    }
    return f_interpolator == null ? fraction : f_interpolator.interpolate(fraction);
  }

  /**
   * Helper routine to stop the running animation. It optionally invokes the
   * {@link TimingTarget#end(Animator)} method of registered timing targets in
   * the correct thread context.
   * 
   * @param notify
   *          {@code true} if the {@link TimingTarget#end(Animator)} method
   *          should be called for registered timing targets, {@code false} if
   *          calls should not be made.
   * @param inCallbackContext
   *          {@link true} if the call to this method was made from the thread
   *          context of {@link TimingSource.TickListener}, {@link false} if it
   *          was not.
   */
  private void stopHelper(boolean notify, boolean inCallbackContext) {
    if (isRunning()) {
      f_timingSource.removeTickListener(this);
      synchronized (f_lock) {
        f_running = false;
        f_currentDirection = f_startDirection;
      }
      if (notify && !f_targets.isEmpty()) {
        final Runnable task = new Runnable() {
          @Override
          public void run() {
            for (TimingTarget target : f_targets) {
              target.end(Animator.this);
            }
          }
        };
        if (inCallbackContext)
          task.run();
        else
          f_timingSource.submit(task);
      }
    }
  }

  /**
   * Not intended for use by client code.
   */
  @Override
  public void timingSourceTick(TimingSource source, long nanoTime) {
    if (isRunning())
      notifyListenersAboutATimingSourceTick(nanoTime);
  }
}
