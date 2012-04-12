package org.jdesktop.core.animation.timing.sources;

import org.jdesktop.core.animation.timing.TimingSource;

import com.surelogic.ThreadSafe;

/**
 * A timing source where ticks are manually controlled via invocation of the
 * {@link #tick()} method.
 * <p>
 * The {@link #init()} and {@link #dispose()} methods do nothing in this
 * implementation and do not need to be invoked.
 * 
 * @author Tim Halloran
 */
@ThreadSafe
public final class ManualTimingSource extends TimingSource {

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

  @Override
  protected void runTaskInThreadContext(Runnable task) {
    if (task == null)
      return;
    task.run();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(ManualTimingSource.class.getSimpleName()).append('@').append(Integer.toHexString(hashCode()));
    return b.toString();
  }
}
