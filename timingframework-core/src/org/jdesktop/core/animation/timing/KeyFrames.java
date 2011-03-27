package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;

import com.surelogic.Immutable;

/**
 * KeyFrames holds information about the times at which values are sampled (
 * {@link KeyTimes}) and the values at those times ({@link KeyValues}). It also
 * holds information about how to interpolate between these values for times
 * that lie between the sampling points.
 * 
 * @param <T>
 *            the type of the values.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public class KeyFrames<T> {

	private final KeyValues<T> f_keyValues;
	private final KeyTimes f_keyTimes;
	private final List<Interpolator> f_interpolators;

	/**
	 * The simplest variation of building a KeyFrames object, it determines
	 * keyTimes based on even division of 0-1 range based on number of
	 * keyValues. This version uses linear interpolation.
	 * 
	 * @param keyValues
	 *            values that will be assumed at each time in keyTimes
	 */
	public static <T> KeyFrames<T> build(KeyValues<T> keyValues) {
		return new KeyFrames<T>(keyValues, null, (Interpolator) null);
	}

	/**
	 * This variant of building a KeyFrames object takes both keyValues (values
	 * at each point in time) and keyTimes (times at which values are sampled).
	 * 
	 * @param keyValues
	 *            values that the animation will assume at each of the
	 *            corresponding times in keyTimes
	 * @param keyTimes
	 *            times at which the animation will assume the corresponding
	 *            values in keyValues
	 * @throws IllegalArgumentException
	 *             keyTimes and keySizes must have the same number of elements
	 *             since these structures are meant to have corresponding
	 *             entries; an exception is thrown otherwise.
	 */
  public static <T> KeyFrames<T> build(KeyValues<T> keyValues,
			KeyTimes keyTimes) {
		return new KeyFrames<T>(keyValues, keyTimes, (Interpolator) null);
	}

	/**
	 * Constructs a KeyFrames object using the provided neede all key*
	 * structures which will be used to calculate between all times in the
	 * keyTimes list. A null interpolator parameter is equivalent to calling
	 * {@link KeyFrames#build(KeyValues, KeyTimes)}.
	 * 
	 * @param keyValues
	 *            values that the animation will assume at each of the
	 *            corresponding times in keyTimes
	 * @param keyTimes
	 *            times at which the animation will assume the corresponding
	 *            values in keyValues
	 * @param interpolators
	 *            collection of Interpolators that control the calculation of
	 *            values in each of the intervals defined by keyFrames. If this
	 *            value is null, a {@link LinearInterpolator} will be used for
	 *            all intervals. If there is only one interpolator, that
	 *            interpolator will be used for all intervals. Otherwise, there
	 *            must be a number of interpolators equal to the number of
	 *            intervals (which is one less than the number of keyTimes).
	 * @throws IllegalArgumentException
	 *             keyTimes and keyValues must have the same number of elements
	 *             since these structures are meant to have corresponding
	 *             entries; an exception is thrown otherwise.
	 * @throws IllegalArgumentException
	 *             The number of interpolators must either be zero
	 *             (interpolators == null), one, or one less than the size of
	 *             keyTimes.
	 */
	public static <T> KeyFrames<T> build(KeyValues<T> keyValues,
			KeyTimes keyTimes, Interpolator... interpolators) {
		return new KeyFrames<T>(keyValues, keyTimes, interpolators);
	}

	/**
	 * Utility constructor that assumes even division of times according to size
	 * of keyValues and interpolation according to interpolators parameter.
	 * 
	 * @param keyValues
	 *            values that the animation will assume at each of the
	 *            corresponding times in keyTimes
	 * @param interpolators
	 *            collection of Interpolators that control the calculation of
	 *            values in each of the intervals defined by keyFrames. If this
	 *            value is null, a {@link LinearInterpolator} will be used for
	 *            all intervals. If there is only one interpolator, that
	 *            interpolator will be used for all intervals. Otherwise, there
	 *            must be a number of interpolators equal to the number of
	 *            intervals (which is one less than the number of keyTimes).
	 * @throws IllegalArgumentException
	 *             The number of interpolators must either be zero
	 *             (interpolators == null), one, or one less than the size of
	 *             keyTimes.
	 */
	public static <T> KeyFrames<T> build(KeyValues<T> keyValues,
			Interpolator... interpolators) {
		return new KeyFrames<T>(keyValues, null, interpolators);
	}

	/**
	 * Private constructor, called by factory methods.
	 */
	private KeyFrames(KeyValues<T> keyValues, KeyTimes keyTimes,
			Interpolator... interpolators) {
		int numFrames = keyValues.getSize();
		// If keyTimes null, create our own
		if (keyTimes == null) {
			double keyTimesArray[] = new double[numFrames];
			double timeVal = 0.0f;
			keyTimesArray[0] = timeVal;
			for (int i = 1; i < (numFrames - 1); ++i) {
				timeVal += (1.0f / (numFrames - 1));
				keyTimesArray[i] = timeVal;
			}
			keyTimesArray[numFrames - 1] = 1.0f;
			f_keyTimes = KeyTimes.build(keyTimesArray);
		} else {
			f_keyTimes = keyTimes;
		}
		f_keyValues = keyValues;
		if (numFrames != f_keyTimes.size()) {
			throw new IllegalArgumentException("keyValues and keyTimes"
					+ " must be of equal size");
		}
		if (interpolators != null && (interpolators.length != (numFrames - 1))
				&& (interpolators.length != 1)) {
			throw new IllegalArgumentException(
					"interpolators must be "
							+ "either null (implying interpolation for all intervals), "
							+ "a single interpolator (which will be used for all "
							+ "intervals), or a number of interpolators equal to "
							+ "one less than the number of times.");
		}

		final int numIntervals = numFrames - 1;
		final List<Interpolator> interpolatorList = new ArrayList<Interpolator>();
		if (interpolators == null || interpolators[0] == null) {
			for (int i = 0; i < numIntervals; ++i) {
				interpolatorList.add(LinearInterpolator.getInstance());
			}
		} else if (interpolators.length < numIntervals) {
			for (int i = 0; i < numIntervals; ++i) {
				interpolatorList.add(interpolators[0]);
			}
		} else {
			for (int i = 0; i < numIntervals; ++i) {
				interpolatorList.add(interpolators[i]);
			}
		}
		f_interpolators = Collections.unmodifiableList(interpolatorList);
	}

	/**
	 * Returns time interval that contains this time fraction
	 */
	public int getInterval(double fraction) {
		return f_keyTimes.getInterval(fraction);
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
	T getValue(double fraction) {
		/*
		 * First, figure out the real fraction to use, given the interpolation
		 * type and keyTimes.
		 */
		int interval = getInterval(fraction);
		double t0 = f_keyTimes.get(interval);
		double t1 = f_keyTimes.get(interval + 1);
		double t = (fraction - t0) / (t1 - t0);
		double interpolatedT = f_interpolators.get(interval).interpolate(t);
		/*
		 * Clamp to avoid problems with buggy interpolators.
		 */
		if (interpolatedT < 0f) {
			interpolatedT = 0f;
		} else if (interpolatedT > 1f) {
			interpolatedT = 1f;
		}
		return f_keyValues.getValue(interval, (interval + 1), interpolatedT);
	}

	Class<?> getType() {
		return f_keyValues.getType();
	}

	KeyValues<T> getKeyValues() {
		return f_keyValues;
	}

	KeyTimes getKeyTimes() {
		return f_keyTimes;
	}
}
