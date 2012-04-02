package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.interpolators.DiscreteInterpolator;
import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * This interface provides a mechanism for animating object properties between
 * different values. It defines the single {@link #interpolate(double)} method.
 * <p>
 * This interface is implemented by built-in interpolators. Applications may
 * choose to implement their own interpolator to get custom interpolation
 * behavior.
 * 
 * @author Chet Haase
 * 
 * @see AccelerationInterpolator
 * @see DiscreteInterpolator
 * @see LinearInterpolator
 * @see SplineInterpolator
 */
@Immutable
public interface Interpolator {

  /**
   * This function takes an input value between 0 and 1 and returns another
   * value, also between 0 and 1. The purpose of the function is to define how
   * time (represented as a (0-1) fraction of the duration of an animation) is
   * altered to derive different value calculations during an animation.
   * 
   * @param fraction
   *          a value between 0 and 1, inclusive, representing the elapsed
   *          fraction of a time interval.
   * @return a value between 0 and 1, inclusive. Values outside of this boundary
   *         may be clamped to the interval [0,1] and cause undefined results.
   */
  @RegionEffects("reads All")
  public double interpolate(double fraction);
}
