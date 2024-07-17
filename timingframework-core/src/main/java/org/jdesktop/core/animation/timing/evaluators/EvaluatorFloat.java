package org.jdesktop.core.animation.timing.evaluators;

import org.jdesktop.core.animation.timing.Evaluator;

/**
 * An evaluator for {@link Float}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public final class EvaluatorFloat implements Evaluator<Float> {

    @Override
    public Float evaluate(Float v0, Float v1, double fraction) {
        return v0 + ((v1 - v0) * (float) fraction);
    }

    @Override
    public Class<Float> getEvaluatorClass() {
        return Float.class;
    }
}
