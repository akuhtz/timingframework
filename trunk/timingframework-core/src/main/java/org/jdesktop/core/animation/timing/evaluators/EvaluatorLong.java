package org.jdesktop.core.animation.timing.evaluators;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Long}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorLong implements Evaluator<Long> {

  @RegionEffects("none")
  public Long evaluate(Long v0, Long v1, double fraction) {
    return v0 + (long) ((v1 - v0) * fraction);
  }

  @RegionEffects("none")
  public Class<Long> getEvaluatorClass() {
    return Long.class;
  }
}
