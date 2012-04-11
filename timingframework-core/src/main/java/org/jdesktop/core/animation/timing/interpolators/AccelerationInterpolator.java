package org.jdesktop.core.animation.timing.interpolators;

import org.jdesktop.core.animation.timing.Interpolator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * This class interpolates fractional values to animate movement with
 * acceleration at the beginning and deceleration at the end.
 * <p>
 * Instances of this class contain no mutable state and can be safely shared.
 * This class is thread-safe.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public class AccelerationInterpolator implements Interpolator {

  private final double f_acceleration;
  private final double f_deceleration;

  public AccelerationInterpolator(double acceleration, double deceleration) {
    if (acceleration < 0 || acceleration > 1) {
      throw new IllegalArgumentException(String.format("%s value of %.2f lies outside required [0,1] range.", "Acceleration",
          acceleration));
    }
    if (deceleration < 0 || deceleration > 1) {
      throw new IllegalArgumentException(String.format("%s value of %.2f lies outside required [0,1] range.", "Deceleration",
          deceleration));
    }
    double limit = 1.0 - deceleration;
    if (acceleration > limit) {
      throw new IllegalArgumentException(String.format("%s value of %.2f is greater than 1 - %.2f (the %s) = %.2f.",
          "Acceleration", acceleration, deceleration, "deceleration", limit));
    }
    limit = 1.0 - acceleration;
    if (deceleration > limit) {
      throw new IllegalArgumentException(String.format("%s value of %.2f is greater than 1 - %.2f (the %s) = %.2f.",
          "Deceleration", deceleration, acceleration, "acceleration", limit));
    }
    f_acceleration = acceleration;
    f_deceleration = deceleration;
  }

  @RegionEffects("reads Instance")
  public double interpolate(double fraction) {
    if (f_acceleration != 0 || f_deceleration != 0) {
      double runRate = 1.0 / (1.0 - f_acceleration / 2.0 - f_deceleration / 2.0);
      if (fraction < f_acceleration) {
        /*
         * Accelerating
         */
        double averageRunRate = runRate * (fraction / f_acceleration) / 2.0;
        fraction *= averageRunRate;
      } else if (fraction > (1.0 - f_deceleration)) {
        /*
         * Decelerating
         */
        // tdec: time spent in deceleration portion
        double tdec = fraction - (1.0 - f_deceleration);
        // pdec: proportion of tdec to total deceleration time
        double pdec = tdec / f_deceleration;
        fraction = runRate * (1.0 - (f_acceleration / 2.0) - f_deceleration + tdec * (2.0 - pdec) / 2.0);
      } else {
        /*
         * Middle - steady velocity
         */
        fraction = runRate * (fraction - (f_acceleration / 2.0));
      }
      /*
       * Clamp fraction to [0,1] since above calculations may cause rounding
       * errors.
       */
      if (fraction < 0) {
        fraction = 0;
      } else if (fraction > 1) {
        fraction = 1;
      }
    }
    return fraction;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(AccelerationInterpolator.class.getSimpleName());
    b.append("(acceleration=").append(f_acceleration);
    b.append(", deceleration=").append(f_deceleration);
    b.append(')');
    return b.toString();
  }
}
