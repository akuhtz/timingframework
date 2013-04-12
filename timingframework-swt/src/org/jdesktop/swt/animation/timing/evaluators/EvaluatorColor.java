package org.jdesktop.swt.animation.timing.evaluators;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Immutable;
import com.surelogic.RegionEffects;

/**
 * An evaluator for {@link Color}.
 * <p>
 * <b>Warning:</b> This class leaks colors until the display is disposed. If
 * this is a problem use {@link EvaluatorRGB} instead.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class EvaluatorColor implements Evaluator<Color> {

  @RegionEffects("reads Instance, v0:Instance, v1:Instance")
  public Color evaluate(Color v0, Color v1, double fraction) {
    int r = v0.getRed() + (int) ((v1.getRed() - v0.getRed()) * fraction + 0.5f);
    int g = v0.getGreen() + (int) ((v1.getGreen() - v0.getGreen()) * fraction + 0.5f);
    int b = v0.getBlue() + (int) ((v1.getBlue() - v0.getBlue()) * fraction + 0.5f);
    final Device device = v0.getDevice();
    final Color value = new Color(device, r, g, b);
    if (device instanceof Display) {
      final Display display = (Display) device;
      display.disposeExec(new Runnable() {
        public void run() {
          value.dispose();
        }
      });
    }
    return value;
  }

  @RegionEffects("none")
  public Class<Color> getEvaluatorClass() {
    return Color.class;
  }
}
