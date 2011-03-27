package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdesktop.core.animation.timing.evaluators.KnownEvaluators;

import com.surelogic.ThreadSafe;

/**
 * Stores a list of values that correspond to the times in a {@link KeyTimes}
 * object. These structures are then used to create a {@link KeyFrames} object,
 * which is then used to create a {@link PropertySetter} for the purposes of
 * modifying an object's property over time.
 * <p>
 * At each of the times in {@link KeyTimes}, the property will take on the
 * corresponding value in the {@link KeyValues} object. Between these times, the
 * property will take on a value based on the interpolation information stored
 * in the {@link KeyFrames} object and the {@link Evaluator} for the type of the
 * values in {@link KeyValues}.
 * <p>
 * This class has built-in support for various known types, as defined in
 * {@link Evaluator}.
 * <p>
 * For a simple example using KeyValues to create a KeyFrames and PropertySetter
 * object, see the class header comments in {@link PropertySetter}.
 * 
 * @param <T>
 *            the type of the values.
 * 
 * @author Chet Haase
 */
@ThreadSafe
public class KeyValues<T> {

	private final CopyOnWriteArrayList<T> f_values;
	private final Evaluator<T> f_evaluator;
	private final Class<T> f_type;

	/**
	 * Constructs a KeyValues object from one or more values. The internal
	 * Evaluator is automatically determined by the type of the parameters.
	 * 
	 * @param params
	 *            the values to interpolate between. If there is only one
	 *            parameter, this is assumed to be a "to" animation where the
	 *            first value is dynamically determined at runtime when the
	 *            animation is started.
	 * @throws IllegalArgumentException
	 *             if an {@link Evaluator} cannot be found that can interpolate
	 *             between the value types supplied
	 */
	public static <T> KeyValues<T> build(T... params) {
		@SuppressWarnings("unchecked")
		final Evaluator<T> evaluator = KnownEvaluators.getInstance()
				.getEvaluatorFor(
						(Class<T>) params.getClass().getComponentType());
		return new KeyValues<T>(evaluator, params);
	}

	/**
	 * Constructs a KeyValues object from a Evaluator and one or more values.
	 * 
	 * @param params
	 *            the values to interpolate between. If there is only one
	 *            parameter, this is assumed to be a "to" animation where the
	 *            first value is dynamically determined at runtime when the
	 *            animation is started.
	 * @throws IllegalArgumentException
	 *             if params does not have at least one value.
	 */
	public static <T> KeyValues<T> build(Evaluator<T> evaluator, T... params) {
		return new KeyValues<T>(evaluator, params);
	}

	/**
	 * Private constructor, called by factory methods.
	 */
	private KeyValues(Evaluator<T> evaluator, T... params) {
		if (params == null) {
			throw new IllegalArgumentException("params array must be non-null");
		} else if (params.length == 0) {
			throw new IllegalArgumentException(
					"params array must have at least one element");
		}
		final List<T> values = new ArrayList<T>();
		if (params.length == 1) {
			/*
			 * This is a "to" animation; set first element to null until we know
			 * what it is via a call to setStartValue().
			 */
			values.add(null);
		}
		Collections.addAll(values, params);
		f_values = new CopyOnWriteArrayList<T>(values);
		@SuppressWarnings("unchecked")
		final Class<T> type = (Class<T>) params.getClass().getComponentType();
		f_type = type;
		f_evaluator = evaluator;
	}

	/**
	 * Returns the number of values stored in this object.
	 * 
	 * @return the number of values stored in this object
	 */
	int getSize() {
		return f_values.size();
	}

	/**
	 * Returns the data type of the values stored in this object.
	 * 
	 * @return a Class value representing the type of values stored in this
	 *         object
	 */
  Class<T> getType() {
		return f_type;
	}

	/**
	 * Called at start of animation; sets starting value in simple "to"
	 * animations.
	 */
	void setStartValue(T startValue) {
		if (isToAnimation()) {
			f_values.set(0, startValue);
		}
	}

	/**
	 * Utility method for determining whether this is a "to" animation (true if
	 * the first value is null).
	 */
	boolean isToAnimation() {
		return (f_values.get(0) == null);
	}

	/**
	 * Returns value calculated from the value at the lower index, the value at
	 * the upper index, the fraction elapsed between these endpoints, and the
	 * evaluator set up by this object at construction time.
	 */
	T getValue(int i0, int i1, double fraction) {
		T value;
		T lowerValue = f_values.get(i0);
		if (lowerValue == null) {
			/*
			 * This is a "to" animation where setStartValue() was not called.
			 * This is a code bug.
			 */
			throw new IllegalStateException(
					"setStartValue() was not called during a \"to\" animation.");
		}
		if (i0 == i1) {
			// trivial case
			value = lowerValue;
		} else {
			T v0 = lowerValue;
			T v1 = f_values.get(i1);
			value = f_evaluator.evaluate(v0, v1, fraction);
		}
		return value;
	}
}
