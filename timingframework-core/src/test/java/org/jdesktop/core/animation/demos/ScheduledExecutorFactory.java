package org.jdesktop.core.animation.demos;

import java.util.concurrent.TimeUnit;

import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;

/**
 * Provides instance of {@link ScheduledExecutorTimingSource}.
 */
public final class ScheduledExecutorFactory implements TimingSourceFactory {

  @Override
  public TimingSource getTimingSource(int periodMillis) {
    return new ScheduledExecutorTimingSource(periodMillis, TimeUnit.MILLISECONDS);
  }

  @Override
  public String toString() {
    return "ScheduledExecutorTimingSource (Calls in timer thread)";
  }
}
