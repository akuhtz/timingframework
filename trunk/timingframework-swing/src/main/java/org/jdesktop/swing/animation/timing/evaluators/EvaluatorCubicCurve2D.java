package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.CubicCurve2D;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link CubicCurve2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorCubicCurve2D implements Evaluator<CubicCurve2D> {

  @RegionEffects("reads All")
  public CubicCurve2D evaluate(CubicCurve2D v0, CubicCurve2D v1, double fraction) {
    double x1 = v0.getX1() + ((v1.getX1() - v0.getX1()) * fraction);
    double y1 = v0.getY1() + ((v1.getY1() - v0.getY1()) * fraction);
    double x2 = v0.getX2() + ((v1.getX2() - v0.getX2()) * fraction);
    double y2 = v0.getY2() + ((v1.getY2() - v0.getY2()) * fraction);
    double ctrlx1 = v0.getCtrlX1() + ((v1.getCtrlX1() - v0.getCtrlX1()) * fraction);
    double ctrly1 = v0.getCtrlY1() + ((v1.getCtrlY1() - v0.getCtrlY1()) * fraction);
    double ctrlx2 = v0.getCtrlX2() + ((v1.getCtrlX2() - v0.getCtrlX2()) * fraction);
    double ctrly2 = v0.getCtrlY2() + ((v1.getCtrlY2() - v0.getCtrlY2()) * fraction);
    CubicCurve2D value = (CubicCurve2D) v0.clone();
    value.setCurve(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
    return value;
  }

  @RegionEffects("none")
  public Class<CubicCurve2D> getEvaluatorClass() {
    return CubicCurve2D.class;
  }
}
