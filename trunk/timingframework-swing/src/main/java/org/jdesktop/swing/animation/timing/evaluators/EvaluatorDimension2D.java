package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.geom.Dimension2D;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Dimension2D}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorDimension2D implements Evaluator<Dimension2D> {

  @RegionEffects("reads All")
  public Dimension2D evaluate(Dimension2D v0, Dimension2D v1, double fraction) {
    double w = v0.getWidth() + ((v1.getWidth() - v0.getWidth()) * fraction);
    double h = v0.getHeight() + ((v1.getHeight() - v0.getHeight()) * fraction);
    Dimension2D value = (Dimension2D) v0.clone();
    value.setSize(w, h);
    return value;
  }

  @RegionEffects("none")
  public Class<Dimension2D> getEvaluatorClass() {
    return Dimension2D.class;
  }
}
