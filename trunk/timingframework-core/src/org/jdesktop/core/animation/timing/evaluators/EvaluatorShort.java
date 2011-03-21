package org.jdesktop.core.animation.timing.evaluators;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;

/**
 * An evaluator for {@link Short}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorShort implements Evaluator<Short> {

	@Override
	public Short evaluate(Short v0, Short v1, double fraction) {
		return (short) (v0 + (short) ((v1 - v0) * fraction));
	}

	@Override
	public Class<Short> getEvaluatorClass() {
		return Short.class;
	}
}
