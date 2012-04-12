package org.jdesktop.swing.animation.timing.evaluators;

import java.awt.Color;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Color}.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorColor implements Evaluator<Color> {

  @RegionEffects("reads All")
  public Color evaluate(Color v0, Color v1, double fraction) {
    int r = v0.getRed() + (int) ((v1.getRed() - v0.getRed()) * fraction + 0.5f);
    int g = v0.getGreen() + (int) ((v1.getGreen() - v0.getGreen()) * fraction + 0.5f);
    int b = v0.getBlue() + (int) ((v1.getBlue() - v0.getBlue()) * fraction + 0.5f);
    int a = v0.getAlpha() + (int) ((v1.getAlpha() - v0.getAlpha()) * fraction + 0.5f);
    Color value = new Color(r, g, b, a);
    return value;
  }

  @RegionEffects("none")
  public Class<Color> getEvaluatorClass() {
    return Color.class;
  }
}
