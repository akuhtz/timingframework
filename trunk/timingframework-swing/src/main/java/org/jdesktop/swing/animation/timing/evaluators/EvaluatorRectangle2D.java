package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.Rectangle2D;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Rectangle2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorRectangle2D implements Evaluator<Rectangle2D> {

  @RegionEffects("reads All")
  public Rectangle2D evaluate(Rectangle2D v0, Rectangle2D v1, double fraction) {
    double x = v0.getX() + ((v1.getX() - v0.getX()) * fraction);
    double y = v0.getY() + ((v1.getY() - v0.getY()) * fraction);
    double w = v0.getWidth() + ((v1.getWidth() - v0.getWidth()) * fraction);
    double h = v0.getHeight() + ((v1.getHeight() - v0.getHeight()) * fraction);
    Rectangle2D value = (Rectangle2D) v0.clone();
    value.setRect(x, y, w, h);
    return value;
  }

  @RegionEffects("none")
  public Class<Rectangle2D> getEvaluatorClass() {
    return Rectangle2D.class;
  }
}
