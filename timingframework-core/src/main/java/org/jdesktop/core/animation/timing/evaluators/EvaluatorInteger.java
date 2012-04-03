package org.jdesktop.core.animation.timing.evaluators;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Integer}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorInteger implements Evaluator<Integer> {

  @RegionEffects("none")
  public Integer evaluate(Integer v0, Integer v1, double fraction) {
    return v0 + (int) ((v1 - v0) * fraction);
  }

  @RegionEffects("none")
  public Class<Integer> getEvaluatorClass() {
    return Integer.class;
  }
}
