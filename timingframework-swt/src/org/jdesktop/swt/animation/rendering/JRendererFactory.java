package org.jdesktop.swt.animation.rendering;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

/**
 * Used to construct an SWT renderer. SWT does not support active rendering so
 * only passive rendering is supported.
 * 
 * @author Tim Halloran
 */
public final class JRendererFactory {

  /**
   * Gets a passive renderer for the passed canvas.
   * 
   * @param on
   *          the canvas to render on.
   * @param target
   *          the rendering implementation to callback to.
   * @return a renderer for <tt>on</tt>.
   */
  public static JRenderer<Canvas> getDefaultRenderer(Canvas on, JRendererTarget<Display, GC> target) {
    final JRenderer<Canvas> result;
    final TimingSource timingSource = new SWTTimingSource(on.getDisplay());
    result = new JPassiveRenderer(on, target, timingSource);
    timingSource.init();
    AnimatorBuilder.setDefaultTimingSource(result.getTimingSource());
    return result;
  }

  private JRendererFactory() {
    // no instances
  }
}
