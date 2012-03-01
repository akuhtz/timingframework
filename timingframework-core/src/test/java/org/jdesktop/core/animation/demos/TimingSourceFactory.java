package org.jdesktop.core.animation.demos;

import org.jdesktop.core.animation.timing.TimingSource;

/**
 * A factory for timing sources used in the timing source resolution benchmark.
 * Each implementation should also override {@link #toString()} to describe the
 * timing source it provides.
 */
public interface TimingSourceFactory {
  /**
   * Constructs a timing source with the specified period.
   * 
   * @param periodMillis
   *          the period in milliseconds.
   * @return a new timing source.
   */
  TimingSource getTimingSource(int periodMillis);
}
