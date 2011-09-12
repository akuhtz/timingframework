package org.jdesktop.swing.animation.rendering;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;

import javax.swing.JPanel;

import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;

public class JRendererPanel extends JPanel {

  /**
   * Flags if rendering has started.
   */
  private boolean f_renderingStarted = false;

  /**
   * A rendering target used for passive rendering.
   */
  private JRendererTarget<GraphicsConfiguration, Graphics2D> f_target = null;

  /**
   * A timing source used for passive rendering.
   */
  private TimingSource f_ts = null;

  /**
   * A post tick listener used for passive rendering.
   */
  private PostTickListener f_postTick = null;

  /**
   * Sets a rendering target for {@link #paintComponent(Graphics)} to invoke.
   * 
   * @param target
   *          a rendering target used for passive rendering..
   * @param timingSource
   *          used for passive rendering.
   * @param postTick
   *          used for passive rendering.
   */
  void setTarget(JRendererTarget<GraphicsConfiguration, Graphics2D> target, TimingSource timingSource, PostTickListener postTick) {
    f_target = target;
    f_ts = timingSource;
    f_postTick = postTick;
  }

  /**
   * Clears the rendering target other passive rendering information out of this
   * control.
   */
  void clearTarget() {
    f_ts.removePostTickListener(f_postTick);
    f_target = null;
    f_ts = null;
    f_postTick = null;
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (f_target != null) {
      if (isVisible() && !f_renderingStarted) {
        f_renderingStarted = true;
        f_target.renderSetup(getGraphicsConfiguration());
        f_ts.addPostTickListener(f_postTick);
      }
      final Graphics2D g2d = (Graphics2D) g.create();
      f_target.render(g2d, getWidth(), getHeight());
      g2d.dispose();
    } else
      super.paintComponent(g);
  }

  void renderChildren(Graphics2D g2d) {
    paintChildren(g2d);
  }

  private static final long serialVersionUID = 2828333835332610056L;
}
