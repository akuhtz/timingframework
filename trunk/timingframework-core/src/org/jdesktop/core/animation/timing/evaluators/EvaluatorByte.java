package org.jdesktop.core.animation.timing.evaluators;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;

/**
 * An evaluator for {@link Byte}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorByte implements Evaluator<Byte> {

	@Override
	public Byte evaluate(Byte v0, Byte v1, double fraction) {
		return (byte) (v0 + (byte) ((v1 - v0) * fraction));
	}

	@Override
	public Class<Byte> getEvaluatorClass() {
		return Byte.class;
	}
}
