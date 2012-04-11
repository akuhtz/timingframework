package org.jdesktop.core.animation.timing.interpolators;

import org.jdesktop.core.animation.timing.Interpolator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;
import com.surelogic.Singleton;

/**
 * This class interpolates fractional values to animate movement in "discrete"
 * steps. A discrete animation is defined to be one where the values during an
 * animation do not change smoothly between the boundary values, but suddenly,
 * at the boundary points. For example, a discrete animation with KeyFrames
 * where the KeyTimes are {0, .5, 1.0} and the KeyValues are (0, 1, 2} would,
 * during the animation, retain the value of 0 until half-way through the
 * animation and 1 through the rest of the animation.
 * <p>
 * Because this class has no state, it is implemented as a singleton that is
 * referenced using the {@link #getInstance} static method. The singleton
 * instance is thread-safe.
 * 
 * @author Chet Haase
 */
@Immutable
@Singleton
public final class DiscreteInterpolator implements Interpolator {

  private static final DiscreteInterpolator INSTANCE = new DiscreteInterpolator();;

  private DiscreteInterpolator() {
    // singleton
  }

  /**
   * Gets the single {@link DiscreteInterpolator} object.
   * 
   * @return the single {@link DiscreteInterpolator} object.
   */
  public static DiscreteInterpolator getInstance() {
    return INSTANCE;
  }

  /**
   * This method always returns 0 for inputs less than 1, which will force users
   * of this interpolation to assign a value equal to the value at the beginning
   * of this timing interval, which is the desired behavior for discrete
   * animations. An input of 1 will return 1, since this means the end of the
   * current interval (and start to the next interval).
   * 
   * @param fraction
   *          a value between 0 and 1, representing the elapsed fraction of a
   *          time interval (either an entire animation cycle or an interval
   *          between two KeyTimes, depending on where this Interpolator has
   *          been set)
   * @return a number representing the start of the current interval, usually
   *         {@code 0}, but if {@code fraction == 1}, returns {@code 1}.
   */
  @RegionEffects("reads Instance")
  public double interpolate(double fraction) {
    if (fraction < 1) {
      return 0;
    }
    return 1;
  }

  @Override
  public String toString() {
    return DiscreteInterpolator.class.getSimpleName();
  }
}
