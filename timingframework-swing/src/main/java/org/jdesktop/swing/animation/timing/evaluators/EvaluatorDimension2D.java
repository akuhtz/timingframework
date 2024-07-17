package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.Dimension2D;

import org.jdesktop.core.animation.timing.Evaluator;

/**
 * An evaluator for {@link Dimension2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public final class EvaluatorDimension2D implements Evaluator<Dimension2D> {

    @Override
    public Dimension2D evaluate(Dimension2D v0, Dimension2D v1, double fraction) {
        double w = v0.getWidth() + ((v1.getWidth() - v0.getWidth()) * fraction);
        double h = v0.getHeight() + ((v1.getHeight() - v0.getHeight()) * fraction);
        final Dimension2D value = (Dimension2D) v0.clone();
        value.setSize(w, h);
        return value;
    }

    @Override
    public Class<Dimension2D> getEvaluatorClass() {
        return Dimension2D.class;
    }
}
