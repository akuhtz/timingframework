package org.jdesktop.core.animation.timing;

import com.surelogic.ThreadSafe;

/**
 * Interface that provides a mechanism for animating object properties between
 * different values. It defines the single {@link #interpolate(double)} method.
 * This interface is implemented by built-in interpolators. Applications may
 * choose to implement their own Interpolator to get custom interpolation
 * behavior.
 * 
 * @author Chet Haase
 */
@ThreadSafe
public interface Interpolator {

  /**
   * This function takes an input value between 0 and 1 and returns another
   * value, also between 0 and 1. The purpose of the function is to define how
   * time (represented as a (0-1) fraction of the duration of an animation) is
   * altered to derive different value calculations during an animation.
   * 
   * @param fraction
   *          a value between 0 and 1, representing the elapsed fraction of a
   *          time interval (either an entire animation cycle or an interval
   *          between two KeyTimes, depending on where this {@link Interpolator}
   *          has been set)
   * @return a value between 0 and 1. Values outside of this boundary may be
   *         clamped to the interval [0,1] and cause undefined results.
   */
  public double interpolate(double fraction);
}
