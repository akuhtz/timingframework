package org.jdesktop.core.animation.timing.sources;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jdesktop.core.animation.timing.TimingSource;

import com.surelogic.ThreadSafe;

/**
 * A timing source where ticks are manually controlled via invocation of the
 * {@link #tick()} method. This is <i>not</i> a general use timing source and
 * should be used with care. It is intended for testing purposes as well as
 * active rendering.
 * <p>
 * Callers of the {@link #tick()} must ensure that a consistent thread context
 * is maintained, i.e., always call this method from the same thread.
 * <p>
 * The {@link #init()} and {@link #dispose()} methods do nothing in this
 * implementation and do not need to be invoked. However, {@link #isDisposed()}
 * will reflect if {@link #dispose()} has been invoked.
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
   * <p>
   * Callers must ensure that a consistent thread context is maintained, i.e.,
   * always call this method from the same thread.
   */
  public void tick() {
    if (f_isDisposed.get())
      throw new IllegalStateException("tick() invoked after dispose()");
    runPerTick();
  }

  private final AtomicBoolean f_isDisposed = new AtomicBoolean(false);

  @Override
  public void dispose() {
    f_isDisposed.set(true);
  }

  @Override
  public boolean isDisposed() {
    return f_isDisposed.get();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(ManualTimingSource.class.getSimpleName()).append('@').append(Integer.toHexString(hashCode()));
    return b.toString();
  }
}
