package org.jdesktop.core.animation.demos;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;

/**
 * Provides instance of {@link ScheduledExecutorTimingSource}.
 */
public final class ScheduledExecutorFactory implements TimingSourceFactory {

  @Override
  public TimingSource getTimingSource(int periodMillis) {
    return new ScheduledExecutorTimingSource(periodMillis, MILLISECONDS);
  }

  @Override
  public String toString() {
    return "ScheduledExecutorTimingSource (Calls in timer thread)";
  }
}
