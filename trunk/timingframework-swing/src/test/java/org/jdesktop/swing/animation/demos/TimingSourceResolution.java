package org.jdesktop.swing.animation.demos;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.core.animation.demos.ScheduledExecutorFactory;
import org.jdesktop.core.animation.demos.TimingSourceFactory;
import org.jdesktop.core.animation.demos.TimingSourceResolutionThread;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
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
public class TimingSourceResolution implements TimingSourceResolutionThread.Depository {

  public static void main(String args[]) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new TimingSourceResolution().setupGUI();
      }
    });
  }

  /**
   * Sets up the simple text output window and then starts a thread to perform
   * the benchmark runs.
   */
  void setupGUI() {
    JFrame frame = new JFrame("Swing TimingSource Resolution Benchmark");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
    f_benchmarkThread = new TimingSourceResolutionThread(this, new ScheduledExecutorFactory(), new SwingTimerFactory());
    f_benchmarkThread.start();
  }

  final JTextArea f_benchmarkOutput = new JTextArea("");

  TimingSourceResolutionThread f_benchmarkThread = null;

  /**
   * This method outputs the string to the GUI {@link #f_benchmarkOutput}.
   * 
   * @param s
   *          a string to append to the output.
   */
  public void out(final String s) {
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

  static class SwingTimerFactory implements TimingSourceFactory {

    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new SwingTimerTimingSource(periodMillis, MILLISECONDS);
    }

    @Override
    public String toString() {
      return "SwingTimerTimingSource (Calls in EDT)";
    }
  }
}
