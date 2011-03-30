package org.jdesktop.core.animation.timing;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.KeyFrames.Frame;

import com.surelogic.Immutable;
import com.surelogic.ThreadSafe;
import com.surelogic.Vouch;

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
@ThreadSafe
public class KeyFrames<T> implements Iterable<Frame<T>> {

  @Immutable
  public static class Frame<T> {
    private final T f_value;
    private final double f_timeFraction;
    private final Interpolator f_interpolator;

    public Frame(T value, double atTimeFraction, Interpolator interpolator) {
      this.f_value = value;
      this.f_timeFraction = atTimeFraction;
      this.f_interpolator = interpolator;
    }

    public T getValue() {
      return f_value;
    }

    public double getTimeFraction() {
      return f_timeFraction;
    }

    public Interpolator getInterpolator() {
      return f_interpolator;
    }
  }

  @Vouch("Immutable")
  private final Frame<T>[] f_frames;

  private final Evaluator<T> f_evaluator;

  /**
   * Constructs a list of key frames.
   * <p>
   * This constructor should only be called from
   * {@link KeyFramesBuilder#build()}.
   */
  KeyFrames(Frame<T>[] frames, Evaluator<T> evaluator) {
    f_frames = frames;
    f_evaluator = evaluator;
  }

  public int size() {
    return f_frames.length;
  }

  public Frame<T> getFrame(int index) {
    return f_frames[index];
  }

  @Override
  public Iterator<Frame<T>> iterator() {
    final Iterator<Frame<T>> result = new Iterator<Frame<T>>() {

      final AtomicInteger f_index = new AtomicInteger(0);

      public boolean hasNext() {
        return f_index.get() < size();
      }

      public Frame<T> next() {
        if (!hasNext())
          throw new NoSuchElementException(I18N.err(27));
        final int index = f_index.getAndIncrement();
        final Frame<T> result = f_frames[index];
        return result;
      }

      public void remove() {
        throw new UnsupportedOperationException(I18N.err(28));
      }
    };
    return result;
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
   * <th><i>i</i><tt>=k.getStartFrameIndexAt(</tt><i>f</i><tt>)</tt></th>
   * <th><tt>k.getFrame(</tt><i>i</i><tt>).getTimeFraction()</tt></th>
   * <th><tt>k.getFrame(</tt><i>i</i><tt>+1).getTimeFraction()</tt></th>
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
  public int getStartFrameIndexAt(double fraction) {
    final int size = size();
    for (int i = 1; i < size; ++i) {
      if (fraction <= f_frames[i].getTimeFraction())
        return i - 1;
    }
    return size - 2;
  }

  public T getInterpolatedValueAt(double fraction) {
    final int interval = getStartFrameIndexAt(fraction);
    /*
     * First, figure out the real fraction to use, given the interpolation type
     * and start and end time of the interval.
     */
    final double t0 = f_frames[interval].getTimeFraction();
    final double t1 = f_frames[interval + 1].getTimeFraction();
    final double t = (fraction - t0) / (t1 - t0);
    double iFraction = f_frames[interval + 1].getInterpolator().interpolate(t);
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
    final T v0 = f_frames[interval].getValue();
    final T v1 = f_frames[interval + 1].getValue();
    return f_evaluator.evaluate(v0, v1, iFraction);
  }
}
