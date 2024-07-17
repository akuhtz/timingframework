package org.jdesktop.core.animation.timing.evaluators;

import org.jdesktop.core.animation.timing.Evaluator;

/**
 * An evaluator for {@link Integer}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public final class EvaluatorInteger implements Evaluator<Integer> {

    @Override
    public Integer evaluate(Integer v0, Integer v1, double fraction) {
        return v0 + (int) ((v1 - v0) * fraction);
    }

    @Override
    public Class<Integer> getEvaluatorClass() {
        return Integer.class;
    }
}
