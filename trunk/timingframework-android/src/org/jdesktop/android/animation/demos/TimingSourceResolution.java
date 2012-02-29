package org.jdesktop.android.animation.demos;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdesktop.android.animation.timing.sources.AndroidTimingSource;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * A Swing application that outputs benchmarks of the available
 * {@link TimingSource} implementations.
 * <p>
 * This is based upon the TimingResolution demo discussed in Chapter 12 on pages
 * 288&ndash;300 (the section on <i>Resolution</i>) of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008), however it used the timers
 * via their Timing Framework {@link TimingSource} implementations rather than
 * directly.
 * <p>
 * Two timing source configurations are benchmarked:
 * <ol>
 * <li>{@link SwingTimerTimingSource} (within Swing EDT) &ndash; As discussed in
 * the book, this timer has the advantage that all calls made from it are within
 * the EDT.</li>
 * <li>{@link ScheduledExecutorTimingSource} (within timer thread) &ndash; This
 * timing source is provided by a <tt>util.concurrent</tt> and calls from it are
 * within its tread context.</li>
 * </ol>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class TimingSourceResolution extends Activity {

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.timing_source_resolution);

    TextView text = (TextView) findViewById(R.id.TextViewTSR);

    /*
     * Run the benchmarks in a thread outside the EDT.
     */
    f_benchmarkOutput = text;
    new Thread(f_runBenchmarks).start();
  }

  private TextView f_benchmarkOutput = null;

  /**
   * This method outputs the string to the GUI {@link #f_benchmarkOutput}.
   * 
   * @param s
   *          a string to append to the output.
   */
  private void out(final String s) {
    final Runnable addToTextArea = new Runnable() {
      @Override
      public void run() {
        final StringBuffer b = new StringBuffer(f_benchmarkOutput.getText());
        b.append(s);
        b.append("\n");
        f_benchmarkOutput.setText(b.toString());
      }
    };
    runOnUiThread(addToTextArea);
  }

  /**
   * This method measures the accuracy of a timing source, which is internally
   * dependent upon both the internal timing mechanisms.
   */
  public void measureTimingSource(TimingSourceFactory factory, String testName) {
    final AtomicInteger timerIteration = new AtomicInteger();

    out("BENCHMARK: " + testName);
    out("                   measured");
    out("period  iter  duration  per-tick");
    out("------  ----  ------------------");
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
              final long totalTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
              final float calculatedDelayTime = totalTime / (float) iterations;
              out(String.format(" %2d ms %5d  %5d ms  %5.2f ms", thisPeriodMillis, iterations, totalTime, calculatedDelayTime));
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
    }
    out("\n");
  }

  /*
   * Factories to provide timing sources to the benchmark code.
   */

  interface TimingSourceFactory {
    TimingSource getTimingSource(int periodMillis);
  }

  static class ScheduledExecutorFactory implements TimingSourceFactory {
    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new ScheduledExecutorTimingSource(periodMillis, TimeUnit.MILLISECONDS);
    }
  }

  static class AndroidFactory implements TimingSourceFactory {
    final Activity f_activity;

    AndroidFactory(Activity activity) {
      f_activity = activity;
    }

    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new AndroidTimingSource(periodMillis, TimeUnit.MILLISECONDS, f_activity);
    }
  }

  /**
   * Invokes the benchmark runs.
   */
  private final Runnable f_runBenchmarks = new Runnable() {
    @Override
    public void run() {
      out(String.format("%d processors available on this machine\n", Runtime.getRuntime().availableProcessors()));

      measureTimingSource(new ScheduledExecutorFactory(), "ScheduledExecutorTimingSource (Calls in timer thread)");
      measureTimingSource(new AndroidFactory(TimingSourceResolution.this), "AndroidTimingSource (Calls in UI thread)");

      out("Runs complete...");
    }
  };
}
