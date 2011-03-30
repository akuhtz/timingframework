package org.jdesktop.core.animation.timing;

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
  /**
   * <tt>f_interpolators[0] = null</tt> because be store the interpolator for a
   * time interval with the end time (not the start time).
   */
  private final Interpolator[] f_interpolators;
  private final Evaluator<T> f_evaluator;

  /**
   * Constructs a list of key frames.
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

  public int size() {
    return f_values.length;
  }

  public T getValue(int index) {
    return f_values[index];
  }

  public double getTime(int index) {
    return f_times[index];
  }

  public Interpolator getInterpolator(int index) {
    return f_interpolators[index];
  }

  /**
   * Returns interval of time, 0 to {@link KeyFrames#size()} - 2, that contains
   * this time fraction. The interval returned is the index of the start key
   * time of the interval the passed fraction falls within.
   * <p>
   * The returned interval is <i>i</i> if <tt>fraction</tt> is within the range
   * (<tt>getKeyTime(</tt><i>i</i><tt>)</tt>, <tt>getKeyTime(</tt> <i>i</i>
   * <tt>+1)</tt>] unless <i>i</i>=0 in which case <tt>fraction</tt> is within
   * the range [0, <tt>getKeyTime(</tt><i>i</i><tt>+1)</tt>] (i.e., zero
   * inclusive).
   * <p>
   * For example, consider the following instance:
   * 
   * <pre>
   * KeyFrames k = TODO.build(0, 0.1, 0.2, 0.5, 1);
   * </pre>
   * 
   * The table below shows the results obtained from calls on this instance.
   * <p>
   * <table border="1">
   * <tr>
   * <th><i>f<i></th>
   * <th><i>i</i><tt>=k.getInterval(</tt><i>f</i><tt>)</tt></th>
   * <th><tt>k.getTime(</tt><i>i</i><tt>)</tt></th>
   * <th><tt>k.getTime(</tt><i>i</i><tt>+1)</tt></th>
   * </tr>
   * <tr>
   * <td align="right">-1*</td>
   * <td align="right">0</td>
   * <td align="right">0.0</td>
   * <td align="right">0.1</td>
   * </tr>
   * <tr>
   * <td align="right">0</td>
   * <td align="right">0</td>
   * <td align="right">0.0</td>
   * <td align="right">0.1</td>
   * </tr>
   * <tr>
   * <td align="right">0.1</td>
   * <td align="right">0</td>
   * <td align="right">0.0</td>
   * <td align="right">0.1</td>
   * </tr>
   * <tr>
   * <td align="right">0.11</td>
   * <td align="right">1</td>
   * <td align="right">0.1</td>
   * <td align="right">0.2</td>
   * </tr>
   * <tr>
   * <td align="right">0.2</td>
   * <td align="right">1</td>
   * <td align="right">0.1</td>
   * <td align="right">0.2</td>
   * </tr>
   * <tr>
   * <td align="right">0.34</td>
   * <td align="right">2</td>
   * <td align="right">0.2</td>
   * <td align="right">0.5</td>
   * </tr>
   * <tr>
   * <td align="right">0.5</td>
   * <td align="right">2</td>
   * <td align="right">0.2</td>
   * <td align="right">0.5</td>
   * </tr>
   * <tr>
   * <td align="right">0.6</td>
   * <td align="right">3</td>
   * <td align="right">0.5</td>
   * <td align="right">1.0</td>
   * </tr>
   * <tr>
   * <td align="right">1</td>
   * <td align="right">3</td>
   * <td align="right">0.5</td>
   * <td align="right">1.0</td>
   * </tr>
   * <tr>
   * <td align="right">2*</td>
   * <td align="right">3</td>
   * <td align="right">0.5</td>
   * <td align="right">1.0</td>
   * </tr>
   * </table>
   * 
   * * The first and the last entries, -1 and 2, are outside the range [0,1],
   * however the implementation clamps them to [0,1] and returns a valid result.
   * 
   * @param fraction
   *          a time fraction in the range [0,1].
   * @return the index of the start key time of the interval the passed fraction
   *         falls within.
   */
  public int getInterval(double fraction) {
    for (int i = 1; i < f_times.length; ++i) {
      if (fraction <= f_times[i])
        return i - 1;
    }
    return f_times.length - 2;
  }

  public T getValue(double fraction) {
    final int interval = getInterval(fraction);
    /*
     * First, figure out the real fraction to use, given the interpolation type
     * and start and end time of the interval.
     */
    final double t0 = f_times[interval];
    final double t1 = f_times[interval + 1];
    final double t = (fraction - t0) / (t1 - t0);
    double iFraction = f_interpolators[interval + 1].interpolate(t);
    /*
     * Clamp to [0,1] to any avoid problems with buggy interpolators.
     */
    if (iFraction < 0) {
      iFraction = 0;
    } else if (iFraction > 1) {
      iFraction = 1;
    }
    /*
     * Second interpolate between the two key values.
     */
    final T v0 = f_values[interval];
    final T v1 = f_values[interval + 1];
    return f_evaluator.evaluate(v0, v1, iFraction);
  }
}
