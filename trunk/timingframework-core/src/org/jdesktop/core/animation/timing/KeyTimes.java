package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.surelogic.Immutable;

/**
 * Stores a list of times from 0 to 1 representing the elapsed fraction of an
 * animation cycle. This list of times is used for calculating interpolated
 * values for a {@link PropertySetter} given a matching set of {@link KeyValues}
 * and {@link Interpolator}s for those time intervals. In the simplest case, a
 * {@link KeyFrames} will consist of just two times in its {@link KeyTimes}: 0
 * and 1.
 * 
 * @author Chet Haase
 */
@Immutable
public class KeyTimes {

	private final List<Double> f_times;

	/**
	 * Constructs a new instance of {@link KeyTimes}. Times should be in
	 * increasing order and should all be in the range [0,1], with the first
	 * value being zero and the last being one
	 * 
	 * @throws IllegalArgumentException
	 *             Time values must be ordered in increasing value, the first
	 *             value must be 0 and the last value must be 1
	 */
	public static KeyTimes build(double... times) {
		return new KeyTimes(times);
	}

	/**
	 * Private constructor, called by factory method. This use makes this class
	 * consistent with how {@link KeyValues} and {@link KeyFrames} are
	 * constructed.
	 */
	private KeyTimes(double... times) {
		final List<Double> timesList = new ArrayList<Double>();
		if (times[0] != 0) {
			throw new IllegalArgumentException("First time value must"
					+ " be zero");
		}
		if (times[times.length - 1] != 1.0f) {
			throw new IllegalArgumentException("Last time value must"
					+ " be one");
		}
		double prevTime = 0;
		for (double time : times) {
			if (time < prevTime) {
				throw new IllegalArgumentException("Time values must be"
						+ " in increasing order");
			}
			timesList.add(time);
			prevTime = time;
		}
		f_times = Collections.unmodifiableList(timesList);
	}

	int getSize() {
		return f_times.size();
	}

	/**
	 * Returns time interval that contains this time fraction
	 */
	int getInterval(double fraction) {
		int prevIndex = 0;
		for (int i = 1; i < f_times.size(); ++i) {
			double time = f_times.get(i);
			if (time >= fraction) {
				// inclusive of start time at next interval. So fraction==1
				// will return the final interval (times.size() - 1)
				return prevIndex;
			}
			prevIndex = i;
		}
		return prevIndex;
	}

	double getTime(int index) {
		return f_times.get(index);
	}

	List<Double> getTimes() {
		return f_times;
	}
}