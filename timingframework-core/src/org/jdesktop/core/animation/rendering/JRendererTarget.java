package org.jdesktop.core.animation.rendering;

/**
 * Allows implementors to control the contents of a component being drawn with
 * either active or passive rendering.
 * <p>
 * All of these methods are invoked within the context of the rendering thread
 * of the {@link JRenderer} this implementation is controlling.
 * 
 * @author Tim Halloran
 * 
 * @param <D>
 *          screen information for set up. <tt>GraphicsConfiguration</tt> is
 *          used for Swing. <tt>Display</tt> is used for SWT.
 * @param <G>
 *          a graphics context usable for painting on the screen.
 *          <tt>Graphics2D</tt> is used for Swing. <tt>GC</tt> is used for SWT.
 * 
 * @see JRenderer
 */
public interface JRendererTarget<D, G> {

  /**
   * Invoked once when the component being rendered is made visible to allow the
   * implementation to perform any necessary setup.
   * <p>
   * The passed screen information allows the the creation of compatible images.
   * 
   * @param d
   *          describes the characteristics of the screen.
   */
  void renderSetup(D d);

  /**
   * Invoked once per rendering cycle to allow the implementation to update its
   * state.
   * <p>
   * Invoked prior to {@link #render(Object, int, int)}.
   */
  void renderUpdate();

  /**
   * Invoked once per rendering cycle to allow the implementation to paint onto
   * a graphics object that is either the screen or an off-screen image that
   * will subsequently painted onto the screen.
   * <p>
   * Invoked after {@link #renderUpdate()}.
   * 
   * @param g
   *          a graphics context to paint/draw with.
   * @param width
   *          the width of the drawing area.
   * @param height
   *          the height of the drawing area.
   */
  void render(G g, int width, int height);

  /**
   * Invoked when rendering is shutdown to allow the implementation to perform
   * any necessary cleanup.
   */
  void renderShutdown();
}
