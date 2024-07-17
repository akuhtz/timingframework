package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.Arc2D;

import org.jdesktop.core.animation.timing.Evaluator;

/**
 * An evaluator for {@link Arc2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public final class EvaluatorArc2D implements Evaluator<Arc2D> {

    @Override
    public Arc2D evaluate(Arc2D v0, Arc2D v1, double fraction) {
        double x = v0.getX() + ((v1.getX() - v0.getX()) * fraction);
        double y = v0.getY() + ((v1.getY() - v0.getY()) * fraction);
        double w = v0.getWidth() + ((v1.getWidth() - v0.getWidth()) * fraction);
        double h = v0.getHeight() + ((v1.getHeight() - v0.getHeight()) * fraction);
        double start = v0.getAngleStart() + ((v1.getAngleStart() - v0.getAngleStart()) * fraction);
        double extent = v0.getAngleExtent() + ((v1.getAngleExtent() - v0.getAngleExtent()) * fraction);
        Arc2D value = (Arc2D) v0.clone();
        value.setArc(x, y, w, h, start, extent, v0.getArcType());
        return value;
    }

    @Override
    public Class<Arc2D> getEvaluatorClass() {
        return Arc2D.class;
    }
}
