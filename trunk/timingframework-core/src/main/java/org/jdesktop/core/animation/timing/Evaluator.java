package org.jdesktop.core.animation.timing;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * This interface provides a mechanism for evaluating between two boundary
 * values of a particular type.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 * 
 * @param <T>
 *          the type the implementation evaluates.
 */
@Immutable
public interface Evaluator<T> {

  /**
   * Evaluates between two boundary values. Typically implementations use linear
   * parametric evaluation:
   * 
   * <pre>
   * v = v0 + (v1 - v0) * fraction
   * </pre>
   * 
   * Implementations of {@link Evaluator} will need to override this method and
   * do something similar for their own types.
   * <p>
   * Note that this mechanism may be used to create non-linear interpolators for
   * specific value types, although it may be simpler to just use the
   * linear/parametric interpolation technique here and perform non-linear
   * interpolation through a custom {@link Interpolator} rather than perform
   * custom calculations in this method. The point of this class is to allow
   * calculations with new/unknown types, not to provide another mechanism for
   * non-linear interpolation.
   */
  @RegionEffects("reads All")
  T evaluate(T v0, T v1, double fraction);

  /**
   * Gets the class that the evaluator provides an implementation.
   * 
   * @return the class that the evaluator provides an implementation.
   */
  @RegionEffects("none")
  Class<T> getEvaluatorClass();
}
