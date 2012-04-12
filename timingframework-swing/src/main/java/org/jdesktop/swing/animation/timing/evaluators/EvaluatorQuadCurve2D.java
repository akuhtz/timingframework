package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.QuadCurve2D;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link QuadCurve2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorQuadCurve2D implements Evaluator<QuadCurve2D> {

  @RegionEffects("reads All")
  public QuadCurve2D evaluate(QuadCurve2D v0, QuadCurve2D v1, double fraction) {
    double x1 = v0.getX1() + ((v1.getX1() - v0.getX1()) * fraction);
    double y1 = v0.getY1() + ((v1.getY1() - v0.getY1()) * fraction);
    double x2 = v0.getX2() + ((v1.getX2() - v0.getX2()) * fraction);
    double y2 = v0.getY2() + ((v1.getY2() - v0.getY2()) * fraction);
    double ctrlx = v0.getCtrlX() + ((v1.getCtrlX() - v0.getCtrlX()) * fraction);
    double ctrly = v0.getCtrlY() + ((v1.getCtrlY() - v0.getCtrlY()) * fraction);
    QuadCurve2D value = (QuadCurve2D) v0.clone();
    value.setCurve(x1, y1, ctrlx, ctrly, x2, y2);
    return value;
  }

  @RegionEffects("none")
  public Class<QuadCurve2D> getEvaluatorClass() {
    return QuadCurve2D.class;
  }
}
