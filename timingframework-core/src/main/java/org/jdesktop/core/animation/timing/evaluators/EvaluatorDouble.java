package org.jdesktop.core.animation.timing.evaluators;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Double}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorDouble implements Evaluator<Double> {

  @RegionEffects("none")
  public Double evaluate(Double v0, Double v1, double fraction) {
    return v0 + ((v1 - v0) * fraction);
  }

  @RegionEffects("none")
  public Class<Double> getEvaluatorClass() {
    return Double.class;
  }
}
