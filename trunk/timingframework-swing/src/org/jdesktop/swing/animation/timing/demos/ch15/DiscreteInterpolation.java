package org.jdesktop.swing.animation.timing.demos.ch15;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.KeyFramesBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.DiscreteInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * A Swing application that demonstrates use of a {@link DiscreteInterpolator}
 * using {@link KeyFrames} within a {@link PropertySetter} animation.
 * <p>
 * This demo is discussed in Chapter 15 on page 410 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class DiscreteInterpolation extends TimingTargetAdapter {

  public static void main(String args[]) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    TimingSource ts = new SwingTimerTimingSource(100, TimeUnit.MILLISECONDS);
    AnimatorBuilder.setDefaultTimingSource(ts);
    ts.init();

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        setupGUI();
      }
    });
  }

  private int f_intValue;

  public void setIntValue(int intValue) {
    f_intValue = intValue;
    out("intValue = " + f_intValue);
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
   * Sets up the simple text output window and then starts a thread to perform
   * the benchmark runs.
   */
  private static void setupGUI() {
    JFrame frame = new JFrame("Swing DiscreteInterpolation Test");
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

    frame.setMinimumSize(new Dimension(650, 500));
    frame.pack();
    frame.setVisible(true);

    DiscreteInterpolation object = new DiscreteInterpolation();

    final KeyFrames<Integer> keyFrames = new KeyFramesBuilder<Integer>().addFrames(2, 6, 3, 5, 4)
        .setInterpolator(DiscreteInterpolator.getInstance()).build();
    out("Constructed Key Frames");
    out("----------------------");
    int i = 0;
    for (KeyFrames.Frame<Integer> keyFrame : keyFrames) {
      final String s = keyFrame.getInterpolator() == null ? "null" : keyFrame.getInterpolator().getClass().getSimpleName();
      out(String.format("Frame %d: value=%d timeFraction=%f interpolator=%s", i++, keyFrame.getValue(), keyFrame.getTimeFraction(),
          s));
    }
    final Animator animator = new AnimatorBuilder().setDuration(3, TimeUnit.SECONDS)
        .addTarget(PropertySetter.getTarget(object, "intValue", keyFrames)).addTarget(object).build();
    out("");
    out("Animation of intValue");
    out("---------------------");
    animator.start();
  }
}
