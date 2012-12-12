package org.jdesktop.swt.animation.timing.evaluators;

import org.eclipse.swt.graphics.Rectangle;
import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Rectangle}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorRectangle implements Evaluator<Rectangle> {

  @RegionEffects("reads Instance, v0:Instance, v1:Instance")
  public Rectangle evaluate(Rectangle v0, Rectangle v1, double fraction) {
    double x = v0.x + ((v1.x - v0.x) * fraction);
    double y = v0.y + ((v1.y - v0.y) * fraction);
    double w = v0.width + ((v1.width - v0.width) * fraction);
    double h = v0.height + ((v1.height - v0.height) * fraction);
    Rectangle value = new Rectangle((int) x, (int) y, (int) w, (int) h);
    return value;
  }

  @RegionEffects("none")
  public Class<Rectangle> getEvaluatorClass() {
    return Rectangle.class;
  }
}
