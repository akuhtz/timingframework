package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.Ellipse2D;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Ellipse2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorEllipse2D implements Evaluator<Ellipse2D> {

  @RegionEffects("reads All")
  public Ellipse2D evaluate(Ellipse2D v0, Ellipse2D v1, double fraction) {
    double x = v0.getX() + ((v1.getX() - v0.getX()) * fraction);
    double y = v0.getY() + ((v1.getY() - v0.getY()) * fraction);
    double w = v0.getWidth() + ((v1.getWidth() - v0.getWidth()) * fraction);
    double h = v0.getHeight() + ((v1.getHeight() - v0.getHeight()) * fraction);
    Ellipse2D value = (Ellipse2D) v0.clone();
    value.setFrame(x, y, w, h);
    return value;
  }

  @RegionEffects("none")
  public Class<Ellipse2D> getEvaluatorClass() {
    return Ellipse2D.class;
  }
}
