package org.jdesktop.core.animation.timing.interpolators;

import org.jdesktop.core.animation.timing.Interpolator;

import com.surelogic.Immutable;

/**
 * This class interpolates fractional values to animate movement at a constant
 * rate.
 * <p>
 * Because this class has no state, it is implemented as a singleton that is
 * referenced using the {@link #getInstance} static method. The singleton
 * instance is thread-safe
 * 
 * @author Chet Haase
 */
@Immutable
public final class LinearInterpolator implements Interpolator {

	private static LinearInterpolator INSTANCE = new LinearInterpolator();

	private LinearInterpolator() {
		// singleton
	}

	/**
	 * Gets the single {@link LinearInterpolator} object.
	 * 
	 * @return the single {@link LinearInterpolator} object.
	 */
	public static LinearInterpolator getInstance() {
		return INSTANCE;
	}

	/**
	 * This method always returns the value it was given, which will cause
	 * callers to calculate a linear interpolation between boundary values.
	 * 
	 * @param fraction
	 *            a value between 0 and 1, representing the elapsed fraction of
	 *            a time interval (either an entire animation cycle or an
	 *            interval between two KeyTimes, depending on where this
	 *            {@link Interpolator} has been set)
	 * @return the value passed in as the <code>fraction</code> parameter.
	 */
	public double interpolate(double fraction) {
		return fraction;
	}
}
