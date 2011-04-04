package org.jdesktop.swing.animation.timing.demos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingNotificationContext;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

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
 * Three timing source configurations are benchmarked:
 * <ol>
 * <li>{@link SwingTimerTimingSource} (within Swing EDT) &ndash; As discussed in
 * the book, this timer has the advantage that all calls made from it are within
 * the EDT.</li>
 * <li>{@link ScheduledExecutorTimingSource} (within Swing EDT) &ndash; This
 * timing source is provided by a <tt>util.concurrent</tt> and uses
 * {@link SwingUtilities#invokeLater(Runnable)} to ensure that all calls from it
 * are within the EDT.</li>
 * <li>{@link ScheduledExecutorTimingSource} (within timer thread) &ndash; This
 * timing source is provided by a <tt>util.concurrent</tt> and calls from it are
 * within its tread context.</li>
 * </ol>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class TimingSourceResolution {

  public static void main(String args[]) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        setupGUI();
      }
    });
  }

  private static final JTextArea f_benchmarkOutput = new JTextArea("");

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
        b.append("\n");
        f_benchmarkOutput.setText(b.toString());
      }
    };
    if (SwingUtilities.isEventDispatchThread()) {
      addToTextArea.run();
    } else {
      SwingUtilities.invokeLater(addToTextArea);
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
          if (edt && !SwingUtilities.isEventDispatchThread()) {
            out("!! We are not in the Swing EDT when we should be !!");
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

  static class SwingTimerFactory implements TimingSourceFactory {
    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new SwingTimerTimingSource(periodMillis, TimeUnit.MILLISECONDS);
    }
  }

  static class SwingScheduledExecutorFactory implements TimingSourceFactory {
    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new ScheduledExecutorTimingSource(new SwingNotificationContext(), periodMillis, TimeUnit.MILLISECONDS);
    }
  }

  static class ScheduledExecutorFactory implements TimingSourceFactory {
    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new ScheduledExecutorTimingSource(periodMillis, TimeUnit.MILLISECONDS);
    }
  }

  /**
   * Sets up the simple text output window and then starts a thread to perform
   * the benchmark runs.
   */
  private static void setupGUI() {
    JFrame frame = new JFrame("Swing TimingSource Resolution Benchmark");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
        System.exit(0);
      }
    });
    f_benchmarkOutput.setEditable(false);
    final Font fixed = new Font("Courier", Font.PLAIN, 14);
    f_benchmarkOutput.setFont(fixed);
    f_benchmarkOutput.setBackground(Color.black);
    f_benchmarkOutput.setForeground(Color.green);
    JScrollPane scrollPane = new JScrollPane(f_benchmarkOutput);
    frame.add(scrollPane);

    frame.setMinimumSize(new Dimension(450, 600));
    frame.pack();
    frame.setVisible(true);

    /*
     * Run the benchmarks in a thread outside the EDT.
     */
    new Thread(f_runBenchmarks).start();
  }

  /**
   * Invokes the benchmark runs.
   */
  private static final Runnable f_runBenchmarks = new Runnable() {
    @Override
    public void run() {
      TimingSourceResolution timeResolution = new TimingSourceResolution();

      out(String.format("%d processors available on this machine\n", Runtime.getRuntime().availableProcessors()));

      timeResolution.measureTimingSource(new SwingTimerFactory(), "SwingTimerTimingSource (Calls in EDT)", true);
      timeResolution.measureTimingSource(new SwingScheduledExecutorFactory(), "ScheduledExecutorTimingSource (Calls in EDT)", true);
      timeResolution.measureTimingSource(new ScheduledExecutorFactory(), "ScheduledExecutorTimingSource (Calls in timer thread)",
          false);

      out("Runs complete...");
    }
  };
}
