package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.evaluators.KnownEvaluators;
import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;

import com.surelogic.NotThreadSafe;

@NotThreadSafe
public class KeyFramesBuilder<T> {

  private Evaluator<T> f_evaluator = null;
  private final List<T> f_values = new ArrayList<T>();
  private final LinkedList<Double> f_timeFractions = new LinkedList<Double>();
  private final List<Interpolator> f_interpolators = new ArrayList<Interpolator>();
  private Interpolator f_interpolator = null;

  public KeyFramesBuilder() {
    // Nothing to do
  }

  public KeyFramesBuilder(T startValue) {
    f_values.add(startValue);
    f_timeFractions.add(Double.valueOf(0));
    f_interpolators.add(null);
  }

  public KeyFramesBuilder<T> addFrame(T value) {
    f_values.add(value);
    f_timeFractions.add(null);
    f_interpolators.add(LinearInterpolator.getInstance());
    return this;
  }

  public KeyFramesBuilder<T> addFrame(T value, double atTimeFraction) {
    f_values.add(value);
    f_timeFractions.add(atTimeFraction);
    f_interpolators.add(LinearInterpolator.getInstance());
    return this;
  }

  public KeyFramesBuilder<T> addFrame(T value, Interpolator interpolator) {
    f_values.add(value);
    f_timeFractions.add(null);
    f_interpolators.add(interpolator);
    return this;
  }

  public KeyFramesBuilder<T> addFrame(T value, double atTimeFraction, Interpolator interpolator) {
    f_values.add(value);
    f_timeFractions.add(atTimeFraction);
    f_interpolators.add(interpolator);
    return this;
  }

  public KeyFramesBuilder<T> addFrame(KeyFrames.Frame<T> frame) {
    f_values.add(frame.getValue());
    f_timeFractions.add(frame.getTimeFraction());
    f_interpolators.add(frame.getInterpolator());
    return this;
  }

  public KeyFramesBuilder<T> addFrames(T... values) {
    for (T value : values)
      addFrame(value);
    return this;
  }

  public KeyFramesBuilder<T> addFrames(KeyFrames.Frame<T>... frames) {
    for (KeyFrames.Frame<T> frame : frames)
      addFrame(frame);
    return this;
  }

  public KeyFramesBuilder<T> setInterpolator(Interpolator interpolator) {
    f_interpolator = interpolator;
    return this;
  }

  public KeyFramesBuilder<T> setEvaluator(Evaluator<T> evaluator) {
    f_evaluator = evaluator;
    return this;
  }

  public KeyFrames<T> build() {
    final int frameCount = f_values.size();
    /*
     * There must be at least two frames and the lists must be the same size.
     */
    if (frameCount < 2)
      throw new IllegalArgumentException(I18N.err(20));
    if (f_timeFractions.size() != frameCount)
      throw new IllegalArgumentException(I18N.err(21, frameCount, f_timeFractions.size()));
    if (f_interpolators.size() != frameCount)
      throw new IllegalArgumentException(I18N.err(22, frameCount, f_interpolators.size()));
    /*
     * Change the first key time to zero, unless it already is zero.
     */
    if (f_timeFractions.getFirst() == null || f_timeFractions.getFirst() != 0) {
      f_timeFractions.removeFirst();
      f_timeFractions.addFirst(Double.valueOf(0));
    }
    /*
     * Change the last key time to one, unless it already is one.
     */
    if (f_timeFractions.getLast() == null || f_timeFractions.getLast() != 1) {
      f_timeFractions.removeLast();
      f_timeFractions.addLast(Double.valueOf(1));
    }
    /*
     * For any unspecified (null) time fractions we compute a linear
     * interpolated value from the previous and next specified fractions.
     */
    List<Integer> fillList = new ArrayList<Integer>();
    double prev = -1;
    for (int i = 0; i < f_timeFractions.size(); i++) {
      Double curr = f_timeFractions.get(i);
      if (curr == null) {
        if (prev == -1)
          throw new IllegalStateException(I18N.err(3, "Time fraction of the first key frame is null, it should be zero"));
        fillList.add(i);
      } else {
        if (!fillList.isEmpty()) {
          final double delta = (curr.doubleValue() - prev) / (fillList.size() + 1);
          int count = 1;
          for (int j : fillList) {
            final double timeFraction = prev + (count * delta);
            f_timeFractions.set(j, timeFraction);
            count++;
          }
          fillList.clear();
        }
        prev = curr.doubleValue();
      }
    }

    /*
     * Construct an array of frames and perform null checks.
     */
    @SuppressWarnings("unchecked")
    final KeyFrames.Frame<T>[] frames = new KeyFrames.Frame[frameCount];
    for (int i = 0; i < frameCount; i++) {
      final T value = f_values.get(i);
      if (value == null)
        throw new IllegalArgumentException(I18N.err(23, i));
      final Double atTimeFraction = f_timeFractions.get(i);
      if (atTimeFraction == null)
        throw new IllegalArgumentException(I18N.err(24, i));
      final Interpolator canidate = f_interpolator == null ? f_interpolators.get(i) : f_interpolator;
      final Interpolator interpolator = i == 0 ? null : canidate;
      if (i != 0 && interpolator == null)
        throw new IllegalArgumentException(I18N.err(25, i));

      frames[i] = new KeyFrames.Frame<T>(value, atTimeFraction, interpolator);
    }

    /*
     * Check that key times are less than one and that they increase.
     */
    double prevTime = 0;
    for (KeyFrames.Frame<T> frame : frames) {
      final double atTime = frame.getTimeFraction();
      if (atTime < prevTime)
        throw new IllegalArgumentException(I18N.err(26, f_timeFractions.toString()));
      prevTime = atTime;
    }

    /*
     * Try to find an evaluator unless one was set.
     */
    if (f_evaluator == null) {
      @SuppressWarnings("unchecked")
      final Class<T> c = (Class<T>) frames[0].getValue().getClass();
      f_evaluator = KnownEvaluators.getInstance().getEvaluatorFor(c);
    }

    final KeyFrames<T> result = new KeyFrames<T>(frames, f_evaluator);
    return result;
  }
}
