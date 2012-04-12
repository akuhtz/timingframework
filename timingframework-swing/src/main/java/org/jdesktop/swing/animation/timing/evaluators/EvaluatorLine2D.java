package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.Line2D;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Line2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorLine2D implements Evaluator<Line2D> {

  @RegionEffects("reads All")
  public Line2D evaluate(Line2D v0, Line2D v1, double fraction) {
    double x1 = v0.getX1() + ((v1.getX1() - v0.getX1()) * fraction);
    double y1 = v0.getY1() + ((v1.getY1() - v0.getY1()) * fraction);
    double x2 = v0.getX2() + ((v1.getX2() - v0.getX2()) * fraction);
    double y2 = v0.getY2() + ((v1.getY2() - v0.getY2()) * fraction);
    Line2D value = (Line2D) v0.clone();
    value.setLine(x1, y1, x2, y2);
    return value;
  }

  @RegionEffects("none")
  public Class<Line2D> getEvaluatorClass() {
    return Line2D.class;
  }
}
