package org.jdesktop.swt.animation.timing.evaluators;

import org.eclipse.swt.graphics.RGB;
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
public final class EvaluatorRGB implements Evaluator<RGB> {

  @RegionEffects("none")
  public RGB evaluate(RGB v0, RGB v1, double fraction) {
    int r = v0.red + (int) ((v1.red - v0.red) * fraction + 0.5);
    int g = v0.green + (int) ((v1.green - v0.green) * fraction + 0.5);
    int b = v0.blue + (int) ((v1.green - v0.green) * fraction + 0.5);
    RGB value = new RGB(r, g, b);
    return value;
  }

  @RegionEffects("none")
  public Class<RGB> getEvaluatorClass() {
    return RGB.class;
  }
}
