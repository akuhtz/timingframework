package org.jdesktop.core.animation.demos;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;

/**
 * A thread that runs timing source resolution benchmarks in the background.
 * <p
 * If it needs to be cancelled then {@link #stopSafely()} should be invoked on
 * the thread.
 */
public final class TimingSourceResolutionThread extends Thread {

  /**
   * A location to deposit output from the benchmark thread to. Make sure you
   * change thread context if necessary, e.g., if you are writing output into a
   * GUI text component.
   */
  public interface Depository {
    void out(String msg);
  }

  final Depository f_to;
  final List<TimingSourceFactory> f_timingSourceFactories;

  public TimingSourceResolutionThread(Depository to, TimingSourceFactory... timingSourceFactories) {
    f_to = to;
    f_timingSourceFactories = new ArrayList<TimingSourceFactory>(Arrays.asList(timingSourceFactories));
  }

  final AtomicBoolean f_running = new AtomicBoolean(true);

  public void stopSafely() {
    f_running.set(false);
  }

  private boolean cancelled() {
    return !f_running.get();
  }

  @Override
  public void run() {
    f_to.out(String.format("%d processors available on this machine\n", Runtime.getRuntime().availableProcessors()));

    for (TimingSourceFactory factory : f_timingSourceFactories) {
      if (cancelled())
        return;
      measureTimingSource(factory, factory.toString());
    }

    if (cancelled())
      return;

    f_to.out("Runs complete...");
  }

  /**
   * This method measures the accuracy of a timing source, which is internally
   * dependent upon both the internal timing mechanisms.
   */
  private void measureTimingSource(TimingSourceFactory factory, String testName) {
    final AtomicInteger timerIteration = new AtomicInteger();

    f_to.out("BENCHMARK: " + testName);
    f_to.out("                   measured");
    f_to.out("period  iter  duration  per-tick");
    f_to.out("------  ----  ------------------");
    for (int periodMillis = 1; periodMillis <= 20; periodMillis++) {
      final long startTime = System.nanoTime();
      final int thisPeriodMillis = periodMillis;
      final int iterations = 1000 / periodMillis;
      timerIteration.set(1);
      final TimingSource source = factory.getTimingSource(periodMillis);
      final CountDownLatch testComplete = new CountDownLatch(1);
      final AtomicBoolean outputResults = new AtomicBoolean(true);
      source.addTickListener(new TickListener() {
        @Override
        public void timingSourceTick(TimingSource source, long nanoTime) {

          if (timerIteration.incrementAndGet() > iterations) {
            if (outputResults.get()) {
              outputResults.set(false); // only output once

              source.dispose(); // end timer

              final long endTime = System.nanoTime();
              final long totalTime = NANOSECONDS.toMillis(endTime - startTime);
              final float calculatedDelayTime = totalTime / (float) iterations;
              f_to.out(String.format(" %2d ms %5d  %5d ms  %5.2f ms", thisPeriodMillis, iterations, totalTime, calculatedDelayTime));
              testComplete.countDown();
            }
          }
        }
      });
      source.init();
      try {
        testComplete.await();
      } catch (InterruptedException e) {
        // should not happen
        e.printStackTrace();
      }

      if (cancelled()) {
        testComplete.countDown();
        return;
      }
    }
    f_to.out("\n");
  }
}
