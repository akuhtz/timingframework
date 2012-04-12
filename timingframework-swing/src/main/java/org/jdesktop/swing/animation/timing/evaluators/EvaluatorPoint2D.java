package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.Point2D;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Point2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorPoint2D implements Evaluator<Point2D> {

  @RegionEffects("reads All")
  public Point2D evaluate(Point2D v0, Point2D v1, double fraction) {
    double x = v0.getX() + ((v1.getX() - v0.getX()) * fraction);
    double y = v0.getY() + ((v1.getY() - v0.getY()) * fraction);
    Point2D value = (Point2D) v0.clone();
    value.setLocation(x, y);
    return value;
  }

  @RegionEffects("none")
  public Class<Point2D> getEvaluatorClass() {
    return Point2D.class;
  }
}
