package org.jdesktop.core.animation.timing;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.KeyFrames.Frame;
import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;

import com.surelogic.Immutable;
import com.surelogic.Vouch;

/**
 * This class manages a list of key frames to animate values via interpolation
 * between a series of key values at key times. It holds information about the
 * times at which values are sampled and the values at those times. It also
 * holds information about how to interpolate between these values for times
 * that lie between the sampling points.
 * 
 * @param <T>
 *          the type of the values.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public class KeyFrames<T> implements Iterable<Frame<T>> {

  /**
   * Represents a single key frame. This class encapsulates a value and the time
   * fraction in the range [0,1] when that value should occur. In addition, the
   * class holds the interpolator that should be used between the previous key
   * frame and this one.
   * 
   * @author Tim Halloran
   * 
   * @param <T>
   *          the type of the values at each frame.
   */
  @Immutable
  public static class Frame<T> {

    /**
     * Indicates that the time fraction when this fame occurs should be
     * calculated, linearly, from the previous and next specified time
     * fractions.
     * <p>
     * {@link KeyFramesBuilder} uses the predicate {@link #getTimeFraction()}
     * <tt>&lt; 0</tt> to note that the user-provided frame does not specify a
     * time fraction.
     */
    private static final double NOT_SET = -1;

    /**
     * The non-{@code null} value of this key frame.
     */
    private final T f_value;

    /**
     * The time fraction in the range [0,1] when that key value defined by this
     * frame should occur, or a negative number.
     */
    private final double f_timeFraction;

    /**
     * The interpolator that should be used between the previous key frame and
     * this one, or {@link null}.
     */
    private final Interpolator f_interpolator;

    /**
     * Constructs a new key frame.
     * 
     * @param value
     *          the value of this key frame.
     * @param atTimeFraction
     *          the time fraction in the range [0,1] when the value should
     *          occur. A negative value indicates, to the
     *          {@link KeyFramesBuilder} that this instance is passed to, that
     *          the time fraction when this fame occurs should be calculated,
     *          linearly, from the previous and next specified time fractions.
     * @param interpolator
     *          the interpolator that should be used between the previous key
     *          frame and this one. A {@code null} value indicates, to the
     *          {@link KeyFramesBuilder} that this instance is passed to, that
     *          either the interpolator set with
     *          {@link KeyFramesBuilder#setInterpolator(Interpolator)} or the
     *          default {@link LinearInterpolator} should be used for this key
     *          frame.
     * 
     * @throws IllegalArgumentException
     *           if <tt>value</tt> is {@code null}.
     */
    public Frame(T value, double atTimeFraction, Interpolator interpolator) {
      if (value == null)
        throw new IllegalArgumentException(I18N.err(1, "value"));
      f_value = value;
      f_timeFraction = atTimeFraction;
      f_interpolator = interpolator;
    }

    /**
     * Constructs a new key frame.
     * <p>
     * The {@link KeyFramesBuilder} that this instance is passed to will use
     * either the interpolator set with
     * {@link KeyFramesBuilder#setInterpolator(Interpolator)} or the default
     * {@link LinearInterpolator} to interpolate between the previous key frame
     * and this one.
     * 
     * @param value
     *          the value of this key frame.
     * @param atTimeFraction
     *          the time fraction in the range [0,1] when the value should
     *          occur. A negative value indicates, to the
     *          {@link KeyFramesBuilder} that this instance is passed to, that
     *          the time fraction when this fame occurs should be calculated,
     *          linearly, from the previous and next specified time fractions.
     * 
     * @throws IllegalArgumentException
     *           if <tt>value</tt> is {@code null}.
     */
    public Frame(T value, double atTimeFraction) {
      this(value, atTimeFraction, null);
    }

    /**
     * Constructs a new key frame.
     * <p>
     * The {@link KeyFramesBuilder} that this instance is passed to will
     * calculate the time fraction when this fame occurs, linearly, from the
     * previous and next specified time fractions.
     * 
     * @param value
     *          the value of this key frame.
     * @param interpolator
     *          the interpolator that should be used between the previous key
     *          frame and this one. A {@code null} value indicates, to the
     *          {@link KeyFramesBuilder} that this instance is passed to, that
     *          either the interpolator set with
     *          {@link KeyFramesBuilder#setInterpolator(Interpolator)} or the
     *          default {@link LinearInterpolator} should be used for this key
     *          frame.
     * 
     * @throws IllegalArgumentException
     *           if <tt>value</tt> is {@code null}.
     */
    public Frame(T value, Interpolator interpolator) {
      this(value, NOT_SET, interpolator);
    }

    /**
     * Constructs a new key frame.
     * <p>
     * The {@link KeyFramesBuilder} that this instance is passed to will
     * calculate the time fraction when this fame occurs, linearly, from the
     * previous and next specified time fractions.
     * <p>
     * The {@link KeyFramesBuilder} that this instance is passed to will use
     * either the interpolator set with
     * {@link KeyFramesBuilder#setInterpolator(Interpolator)} or the default
     * {@link LinearInterpolator} to interpolate between the previous key frame
     * and this one.
     * 
     * @param value
     *          the value of this key frame.
     * 
     * @throws IllegalArgumentException
     *           if <tt>value</tt> is {@code null}.
     */
    public Frame(T value) {
      this(value, NOT_SET, null);
    }

    /**
     * The value of this key frame.
     * <p>
     * The returned value will never be {@code null}.
     * 
     * @return a value.
     */
    public T getValue() {
      return f_value;
    }

    /**
     * The time fraction in the range [0,1] when that key value defined by this
     * frame should occur.
     * <p>
     * The time fraction can be negative in the case that this instance was
     * created by client code to be passed to
     * {@link KeyFramesBuilder#addFrame(KeyFrames.Frame)} and a constructor that
     * does not set the time fraction was called. A negative value indicates to
     * the {@link KeyFramesBuilder} that the time fraction should be calculated,
     * linearly, from the previous and next specified time fractions.
     * 
     * @return a time fraction in the range [0,1], or a negative number.
     */
    public double getTimeFraction() {
      return f_timeFraction;
    }

    /**
     * Gets the interpolator that should be used between the previous key frame
     * and this one.
     * <p>
     * The return value may be {@code null} in two cases:
     * <ul>
     * <li>If this instance was obtained from a {@link KeyFrames} instance,
     * either through iteration or {@link KeyFrames#getFrame(int)}, then a
     * {@code null} value indicates that this is the first frame. Because the
     * interpolator is used to interpolate between the previous frame and this
     * one&mdash;the first frame has no intepolator.</li>
     * <li>If this instance was created by client code to be passed to
     * {@link KeyFramesBuilder#addFrame(KeyFrames.Frame)} then a {@code null}
     * value indicates that either the interpolator set with
     * {@link KeyFramesBuilder#setInterpolator(Interpolator)} or the default
     * {@link LinearInterpolator} should be used for this key frame.</li>
     * </ul>
     * 
     * @return an interpolator, or {@code null}.
     */
    public Interpolator getInterpolator() {
      return f_interpolator;
    }
  }

  /**
   * The ordered list of key frames managed by this instance.
   */
  @Vouch("Immutable")
  private final Frame<T>[] f_frames;

  /**
   * Used to evaluates between two key frame values.
   */
  private final Evaluator<T> f_evaluator;

  /**
   * Constructs a key frames instance.
   * <p>
   * This constructor should only be called from
   * {@link KeyFramesBuilder#build()}.
   */
  KeyFrames(Frame<T>[] frames, Evaluator<T> evaluator) {
    f_frames = frames;
    f_evaluator = evaluator;
  }

  /**
   * Gets the number of key frames contained in this list. The returned value is
   * never less that two.
   * 
   * @return the number of key frames in this list.
   */
  public int size() {
    return f_frames.length;
  }

  /**
   * Returns the key frame at the specified position in this list.
   * 
   * @param index
   *          index of the key frame to return.
   * @return the key frame at the specified position in this list.
   * @throws IndexOutOfBoundsException
   *           if the index is out of range (
   *           <tt>index &lt; 0 || index &gt;= size()</tt>)
   */
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
   * the passed time fraction based upon the list of key frames managed by this
   * instance. The return value is the index of the key frame closest to, but
   * not after, the passed time fraction. More precisely, the returned interval
   * is <i>i</i> if <tt>fraction</tt> is within the range ( <tt>getFrame(</tt>
   * <i>i</i><tt>).getTimeFraction()</tt>, <tt>getFrame(</tt> <i>i</i>
   * <tt>+1).getTimeFraction()</tt>] unless <i>i</i>=0 in which case
   * <tt>fraction</tt> is within the range [0, <tt>getFrame(</tt><i>i</i>
   * <tt>+1).getTimeFraction()</tt>] (i.e., zero inclusive).
   * <p>
   * For example, consider the following instance (ignoring the integer key
   * frame values):
   * 
   * <pre>
   * KeyFramesBuilder&lt;Integer&gt; builder = new KeyFramesBuilder&lt;Integer&gt;(1);
   * builder.addFrame(2, 0.1); // addFrame(value, atTimeFraction)
   * builder.addFrame(3, 0.2);
   * builder.addFrame(4, 0.5);
   * builder.addFrame(5, 1);
   * KeyFrames&lt;Integer&gt; k = builder.build();
   * </pre>
   * 
   * The table below shows the results obtained from calls on this instance.
   * <p>
   * <table border="1">
   * <tr>
   * <th><i>f<i></th>
   * <th><i>i</i><tt>=k.getFrameIndexAt(</tt><i>f</i><tt>)</tt></th>
   * <th><tt>k.getFrame(</tt><i>i</i><tt>).getTimeFraction()</tt></th>
   * <th><tt>k.getFrame(</tt><i>i</i><tt>+1).getTimeFraction()</tt></th>
   * </tr>
   * <tr>
   * <td align="right">-1&dagger;</td>
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
   * <td align="right">2&dagger;</td>
   * <td align="right">3</td>
   * <td align="right">0.5</td>
   * <td align="right">1.0</td>
   * </tr>
   * </table>
   * 
   * &dagger; The first and the last entries, -1 and 2, are outside the range
   * [0,1], however the implementation clamps them to [0,1] and returns a valid
   * result.
   * 
   * @param fraction
   *          a time fraction in the range [0,1].
   * @return the index of the key frame closest to, but not after, the passed
   *         time fraction.
   */
  public int getFrameIndexAt(double fraction) {
    final int size = size();
    for (int i = 1; i < size; ++i) {
      if (fraction <= f_frames[i].getTimeFraction())
        return i - 1;
    }
    return size - 2;
  }

  /**
   * Gets the interpolated value at the passed time fraction based upon the list
   * of key frames managed by this instance. The returned value is calculated by
   * the key frames' {@link Evaluator} using the two key frames
   * <tt>fraction</tt> lies between and the {@link Interpolator} set for that
   * interval of time.
   * 
   * @param fraction
   *          a time fraction in the range [0,1].
   * @return the evaluated value at the passed time fraction.
   */
  public T getInterpolatedValueAt(double fraction) {
    final int interval = getFrameIndexAt(fraction);
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
     * Second evaluate between the two key values.
     */
    final T v0 = f_frames[interval].getValue();
    final T v1 = f_frames[interval + 1].getValue();
    return f_evaluator.evaluate(v0, v1, iFraction);
  }
}
