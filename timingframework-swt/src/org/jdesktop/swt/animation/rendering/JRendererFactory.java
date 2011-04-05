package org.jdesktop.swt.animation.rendering;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

public final class JRendererFactory {

  public static JRenderer<Canvas> getDefaultRenderer(Canvas on, JRendererTarget<Display, GC> target, boolean hasChildren) {
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
