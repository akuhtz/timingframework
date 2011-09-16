package org.jdesktop.swt.animation.timing.evaluators;

import org.eclipse.swt.graphics.Point;
import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;

/**
 * An evaluator for {@link Point}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorPoint implements Evaluator<Point> {

  @Override
  public Point evaluate(Point v0, Point v1, double fraction) {
    double x = v0.x + ((v1.x - v0.x) * fraction);
    double y = v0.y + ((v1.y - v0.y) * fraction);
    Point value = new Point((int) x, (int) y);
    return value;
  }

  @Override
  public Class<Point> getEvaluatorClass() {
    return Point.class;
  }
}
