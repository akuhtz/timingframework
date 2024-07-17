package org.jdesktop.core.animation.timing.evaluators;

import org.jdesktop.core.animation.timing.Evaluator;

/**
 * An evaluator for {@link Double}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public final class EvaluatorDouble implements Evaluator<Double> {

    @Override
    public Double evaluate(Double v0, Double v1, double fraction) {
        return v0 + ((v1 - v0) * fraction);
    }

    @Override
    public Class<Double> getEvaluatorClass() {
        return Double.class;
    }
}
