package org.jdesktop.core.animation.timing.evaluators;

import org.jdesktop.core.animation.timing.Evaluator;

/**
 * An evaluator for {@link Long}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public final class EvaluatorLong implements Evaluator<Long> {

    @Override
    public Long evaluate(Long v0, Long v1, double fraction) {
        return v0 + (long) ((v1 - v0) * fraction);
    }

    @Override
    public Class<Long> getEvaluatorClass() {
        return Long.class;
    }
}
