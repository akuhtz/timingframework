package org.jdesktop.core.animation.rendering;

import org.jdesktop.core.animation.timing.TimingSource;

/**
 * Defines the public interface to a renderer implementation.
 * 
 * @author Tim Halloran
 * 
 * @see JRendererTarget
 */
public interface JRenderer {

  /**
   * Submits a task to be run by the renderer in the same thread context that
   * its {@link JRendererTarget} uses.
   * <p>
   * Safe to be called at any time within any thread.
   * 
   * @param task
   *          a task for the renderer.
   */
  void invokeLater(final Runnable task);

  /**
   * Gets the timing source being used by the renderer.
   * 
   * @return the timing source being used by the renderer.
   */
  TimingSource getTimingSource();

  /**
   * Calculates the frames per second being drawn to the screen.
   * <p>
   * Safe to be called at any time within any thread.
   * 
   * @return the frames per second being drawn to the screen.
   */
  long getFPS();

  /**
   * Calculates the total average time for each rendering cycle.
   * <p>
   * Safe to be called at any time within any thread.
   * 
   * @return average time in nanoseconds.
   */
  long getAverageCycleTimeNanos();

  /**
   * Shuts down rendering.
   * <p>
   * Safe to be called at any time within any thread.
   */
  public void shutdown();
}
