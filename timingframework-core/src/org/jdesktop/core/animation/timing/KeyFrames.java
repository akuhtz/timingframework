package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;

import com.surelogic.Immutable;

/**
 * KeyFrames holds information about the times at which values are sampled and
 * the values at those times. It also holds information about how to interpolate
 * between these values for times that lie between the sampling points.
 * 
 * @param <T>
 *          the type of the values.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public class KeyFrames<T> {

  private final T[] f_values;
  private final double[] f_times;
  private final Interpolator[] f_interpolators;
  private final Evaluator<T> f_evaluator;

  /**
   * Constructs an list of key frames.
   * <p>
   * This constructor should only be called from
   * {@link KeyFramesBuilder#build()}.
   */
  KeyFrames(T[] values, double[] times, Interpolator[] interpolators, Evaluator<T> evaluator) {
    f_values = values;
    f_times = times;
    f_interpolators = interpolators;
    f_evaluator = evaluator;
  }

  public int getInterval(double fraction) {
    int i = 0;
    while (i < f_times.length) {
      if (fraction <= f_times[i])
        return i;
      else
        i++;
    }
    return i;
  }

  /**
   * Returns value calculated from the value at the lower index, the value at
   * the upper index, the fraction elapsed between these endpoints, and the
   * evaluator set up by this object at construction time.
   */
  private T getValue(int i0, int i1, double fraction) {
    T value;
    T lowerValue = f_values[i0];
    if (i0 == i1) {
      // trivial case
      value = lowerValue;
    } else {
      T v0 = lowerValue;
      T v1 = f_values[i1];
      value = f_evaluator.evaluate(v0, v1, fraction);
    }
    return value;
  }

  /**
   * Returns a value for the given fraction elapsed of the animation cycle.
   * Given the fraction, this method will determine what interval the fraction
   * lies within, how much of that interval has elapsed, what the boundary
   * values are (from KeyValues), what the interpolated fraction is (from the
   * Interpolator for the interval), and what the final interpolated
   * intermediate value is (using the appropriate Evaluator). This method will
   * call into the Interpolator for the time interval to get the interpolated
   * method. To ensure that future operations succeed, the value received from
   * the interpolation will be clamped to the interval [0,1].
   */
  public T getValue(double fraction) {
    /*
     * First, figure out the real fraction to use, given the interpolation type
     * and keyTimes.
     */
    int interval = getInterval(fraction);
    final double t0 = interval == 0 ? 0.0 : f_times[interval - 1];
    final double t1 = f_times[interval];
    final double t = (fraction - t0) / (t1 - t0);
    double interpolatedT = f_interpolators[interval].interpolate(t);
    /*
     * Clamp to avoid problems with buggy interpolators.
     */
    if (interpolatedT < 0) {
      interpolatedT = 0;
    } else if (interpolatedT > 1) {
      interpolatedT = 1;
    }
    return getValue(interval, interval + 1, interpolatedT);
  }
}
