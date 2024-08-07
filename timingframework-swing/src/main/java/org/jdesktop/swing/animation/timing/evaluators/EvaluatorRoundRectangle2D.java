package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.RoundRectangle2D;

import org.jdesktop.core.animation.timing.Evaluator;

/**
 * An evaluator for {@link RoundRectangle2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public final class EvaluatorRoundRectangle2D implements Evaluator<RoundRectangle2D> {

    @Override
    public RoundRectangle2D evaluate(RoundRectangle2D v0, RoundRectangle2D v1, double fraction) {
        double x = v0.getX() + ((v1.getX() - v0.getX()) * fraction);
        double y = v0.getY() + ((v1.getY() - v0.getY()) * fraction);
        double w = v0.getWidth() + ((v1.getWidth() - v0.getWidth()) * fraction);
        double h = v0.getHeight() + ((v1.getHeight() - v0.getHeight()) * fraction);
        double arcw = v0.getArcWidth() + ((v1.getArcWidth() - v0.getArcWidth()) * fraction);
        double arch = v0.getArcHeight() + ((v1.getArcHeight() - v0.getArcHeight()) * fraction);
        RoundRectangle2D value = (RoundRectangle2D) v0.clone();
        value.setRoundRect(x, y, w, h, arcw, arch);
        return value;
    }

    @Override
    public Class<RoundRectangle2D> getEvaluatorClass() {
        return RoundRectangle2D.class;
    }
}
