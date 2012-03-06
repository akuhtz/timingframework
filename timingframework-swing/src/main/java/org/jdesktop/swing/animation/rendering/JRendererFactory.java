package org.jdesktop.swing.animation.rendering;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;

import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.sources.ManualTimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import com.surelogic.Utility;

/**
 * Used to construct a Swing renderer based upon the users preference for active
 * or passive rendering.
 * <p>
 * Active rendering is used if the <tt>org.jdesktop.renderer.active</tt>
 * property is defined. For example with the following passed to the Java
 * interpreter.
 * 
 * <pre>
 * -Dorg.jdesktop.renderer.active=true
 * </pre>
 * 
 * @author Tim Halloran
 */
@Utility
public final class JRendererFactory {

  /**
   * Gets a renderer based upon the users preference for active or passive
   * rendering.
   * <p>
   * Active rendering is used if the <tt>org.jdesktop.renderer.active</tt>
   * property is defined. For example with the following passed to the Java
   * interpreter.
   * 
   * <pre>
   * -Dorg.jdesktop.renderer.active=true
   * </pre>
   * 
   * This method constructs and starts an {@link SwingTimerTimingSource} or a
   * {@link ManualTimingSource} and sets it as the default for all animations. A
   * handle to this timing source can be obtained by invoking
   * {@link JRenderer#getTimingSource()}, for example, if it needs to be
   * disposed.
   * 
   * @param on
   *          the panel to render on.
   * @param target
   *          the rendering implementation to callback to.
   * @param hasChildren
   *          if <tt>on</tt> manages child Swing components. {@code true} if it
   *          does. {@code false} if it does not. This helps the renderer
   *          improve performance if no child components are ever used.
   * @return a renderer for <tt>on</tt>.
   */
  public static JRenderer getDefaultRenderer(JRendererPanel on, JRendererTarget<GraphicsConfiguration, Graphics2D> target,
      boolean hasChildren) {
    final JRenderer result;
    if (useActiveRenderer()) {
      result = new JActiveRenderer(on, target, hasChildren);
    } else {
      final TimingSource timingSource = new SwingTimerTimingSource();
      result = new JPassiveRenderer(on, target, timingSource);
      timingSource.init();
    }
    Animator.setDefaultTimingSource(result.getTimingSource());
    return result;
  }

  public static final String PROPERTY = "org.jdesktop.renderer.active";

  /**
   * Gets if active rendering is desired.
   * 
   * @return {@code true} if active rendering is desired, {@code false}
   *         otherwise.
   */
  public static boolean useActiveRenderer() {
    return System.getProperty(PROPERTY) != null;
  }

  private JRendererFactory() {
    // no instances
  }
}
