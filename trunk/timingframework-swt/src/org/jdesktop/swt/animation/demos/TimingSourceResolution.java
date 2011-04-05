package org.jdesktop.swt.animation.demos;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

/**
 * A SWT application that outputs benchmarks of the available
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
 * <li>{@link SWTTimingSource} (within SWT UI thread) &ndash; As discussed in
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
public class TimingSourceResolution {

  private static Display f_display;
  private static Text f_benchmarkOutput;

  public static void main(String args[]) {
    f_display = Display.getDefault();
    final Shell f_shell = new Shell(f_display);
    f_shell.setText("SWT TimingSource Resolution Benchmark");
    f_shell.setLayout(new FillLayout());
    f_benchmarkOutput = new Text(f_shell, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
    f_benchmarkOutput.setEditable(false);
    Font fixed = new Font(f_display, "Courier", 11, SWT.NONE);
    f_benchmarkOutput.setFont(fixed);
    f_benchmarkOutput.setBackground(f_display.getSystemColor(SWT.COLOR_BLACK));
    f_benchmarkOutput.setForeground(f_display.getSystemColor(SWT.COLOR_GREEN));

    f_shell.setSize(450, 600);
    f_shell.open();

    /*
     * Run the benchmarks in a thread outside the SWT UI thread.
     */
    new Thread(f_runBenchmarks).start();

    while (!f_shell.isDisposed()) {
      if (!f_display.readAndDispatch())
        f_display.sleep();
    }
    f_display.dispose();
    System.exit(0);
  }

  /**
   * This method outputs the string to the GUI {@link #f_benchmarkOutput}.
   * 
   * @param s
   *          a string to append to the output.
   */
  private static void out(final String s) {
    final Runnable addToTextArea = new Runnable() {
      @Override
      public void run() {
        final StringBuffer b = new StringBuffer(f_benchmarkOutput.getText());
        b.append(s);
        b.append(Text.DELIMITER);
        f_benchmarkOutput.setText(b.toString());
        f_benchmarkOutput.setSelection(b.length());
      }
    };
    if (f_display.getThread().equals(Thread.currentThread())) {
      addToTextArea.run();
    } else {
      f_display.asyncExec(addToTextArea);
    }
  }

  /**
   * This method measures the accuracy of a timing source, which is internally
   * dependent upon both the internal timing mechanisms.
   */
  public void measureTimingSource(TimingSourceFactory factory, String testName, final boolean edt) {
    final AtomicInteger timerIteration = new AtomicInteger();

    out("BENCHMARK: " + testName);
    out("                          measured");
    out("period  iterations  total time  per-tick");
    out("------  ----------  --------------------");
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
          if (edt && !f_display.getThread().equals(Thread.currentThread())) {
            out("!! We are not in the SWT UI thread when we should be !!");
          }
          if (timerIteration.incrementAndGet() > iterations) {
            if (outputResults.get()) {
              outputResults.set(false); // only output once

              source.dispose(); // end timer

              final long endTime = System.nanoTime();
              final long totalTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
              final float calculatedDelayTime = totalTime / (float) iterations;
              out(String.format(" %2d ms       %5d    %5d ms  %5.2f ms", thisPeriodMillis, iterations, totalTime,
                  calculatedDelayTime));
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

  static class SWTTimerFactory implements TimingSourceFactory {
    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new SWTTimingSource(periodMillis, TimeUnit.MILLISECONDS, f_display);
    }
  }

  static class ScheduledExecutorFactory implements TimingSourceFactory {
    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new ScheduledExecutorTimingSource(periodMillis, TimeUnit.MILLISECONDS);
    }
  }

  /**
   * Invokes the benchmark runs.
   */
  private static final Runnable f_runBenchmarks = new Runnable() {
    @Override
    public void run() {
      TimingSourceResolution timeResolution = new TimingSourceResolution();

      out(String.format("%d processors available on this machine\n", Runtime.getRuntime().availableProcessors()));

      timeResolution.measureTimingSource(new SWTTimerFactory(), "SWTTimingSource (Calls in SWT UI thread)", true);
      timeResolution.measureTimingSource(new ScheduledExecutorFactory(), "ScheduledExecutorTimingSource (Calls in timer thread)",
          false);

      out("Runs complete...");
    }
  };
}
