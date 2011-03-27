package org.jdesktop.core.animation.timing.sources;

import org.jdesktop.core.animation.timing.TimingSource;

/**
 * A timing source where ticks are manually controlled via invocation of the
 * {@link #tick()} method.
 * <p>
 * The {@link #init()} and {@link #dispose()} methods do nothing in this
 * implementation and do not need to be invoked.
 * 
 * @author Tim Halloran
 */
public final class ManualTimingSource extends TimingSource {

  public ManualTimingSource() {
    super(null);
  }

  @Override
  public void init() {
    // nothing to do
  }

  /**
   * Called to "tick" time along.
   */
  public void tick() {
    notifyTickListeners();
  }

  @Override
  public void dispose() {
    // nothing to do
  }
}
