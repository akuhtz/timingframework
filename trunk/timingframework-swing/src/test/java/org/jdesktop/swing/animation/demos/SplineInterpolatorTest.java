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

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * A Swing application that compares the elapsed fraction, in real time, to the
 * elapsed fraction when using a sample {@link SplineInterpolator}.
 * <p>
 * This is based upon the TimingResolution demo discussed in Chapter 14 on pages
 * 372&ndash;375 (the section on <i>Resolution</i>) of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008), however it has been
 * changed in several ways. First, it outputs to a Swing window. This avoids the
 * test program crashing when the Swing EDT exits (which seemed to occur on some
 * systems&mdash;recent versions of Swing must have realized that the
 * console-based program had no windows). Second, it uses a global timer (as
 * advocated by Haase in his JavaOne 2008 talk) which causes the "ticks" to be
 * slightly off from what is shown in the book (sometimes&mdash;you might get
 * lucky).
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class SplineInterpolatorTest extends TimingTargetAdapter {

  public static void main(String args[]) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    TimingSource ts = new SwingTimerTimingSource(DURATION / 10, MILLISECONDS);
    Animator.setDefaultTimingSource(ts);
    ts.init();

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        setupGUI();
      }
    });
  }

  static final JTextArea f_benchmarkOutput = new JTextArea("");

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
   * Sets up the simple text output window and then starts a thread to perform
   * the benchmark runs.
   */
  static void setupGUI() {
    JFrame frame = new JFrame("Swing SplineInterpolator Test");
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

    frame.setMinimumSize(new Dimension(350, 500));
    frame.pack();
    frame.setVisible(true);

    SplineInterpolator si = new SplineInterpolator(1, 0, 0, 1);
    Animator animator = new Animator.Builder().setDuration(DURATION, MILLISECONDS).setInterpolator(si)
        .addTarget(new SplineInterpolatorTest()).build();
    animator.start();
  }

  private long startTime;
  private final static int DURATION = 5000; // milliseconds

  @Override
  public void begin(Animator source) {
    startTime = System.nanoTime() / 1000000;
    out("real  interpolated");
    out("----  ------------");
  }

  /**
   * Calculate the real fraction elapsed and output that along with the fraction
   * parameter, which has been non-linearly interpolated.
   */
  @Override
  public void timingEvent(Animator source, double fraction) {
    long currentTime = System.nanoTime() / 1000000;
    long elapsedTime = currentTime - startTime;
    double realFraction = (double) elapsedTime / DURATION;
    out(String.format("%.2f          %.2f", realFraction, fraction));
  }

  @Override
  public void end(Animator source) {
    out("");
    out("\"real\" fraction may exceed");
    out("1.00 due to callback delays");
    out("(interpolated never should)");
  }
}
