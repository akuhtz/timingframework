package org.jdesktop.swing.animation.demos;

import java.awt.Color;
import java.awt.Point;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.jdesktop.swing.animation.timing.triggers.TriggerUtility;

/**
 * This demonstration uses property setters and triggers together to shimmy two
 * buttons back and forth across the window. "Infinite" bounces back and forth
 * until it is clicked again. "Once" goes back and forth once and then stops.
 * 
 * @author John
 * @author Tim Halloran
 */
public class TwoButtonShimmy {

  public static void main(String[] args) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    TimingSource ts = new SwingTimerTimingSource();
    Animator.setDefaultTimingSource(ts);
    ts.init();

    Runnable doCreateAndShowGUI = new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    };
    SwingUtilities.invokeLater(doCreateAndShowGUI);
  }

  static void createAndShowGUI() {
    /* Creating JButton with a infinite number of repetitions */
    final JButton btnInfinite = new JButton("Infinite");
    btnInfinite.setBounds(10, 10, 130, 30);
    TimingTarget ttInfinite = PropertySetter.getTarget(btnInfinite, "location", new Point(10, 10), new Point(250, 10));
    Animator animatorInfinite = new Animator.Builder().setRepeatCount(Animator.INFINITE).setDuration(3, TimeUnit.SECONDS)
        .addTarget(ttInfinite).build();
    TriggerUtility.addActionTrigger(btnInfinite, animatorInfinite);

    /* Creating JButton with a finite number of repetitions */
    final JButton btnFinite = new JButton("Once");
    btnFinite.setBounds(10, 50, 130, 30);
    TimingTarget ttFinite = PropertySetter.getTarget(btnFinite, "location", new Point(10, 50), new Point(250, 50),
        new Point(10, 50));
    Animator animatorFinite = new Animator.Builder().setDuration(3, TimeUnit.SECONDS).addTarget(ttFinite).build();
    TriggerUtility.addActionTrigger(btnFinite, animatorFinite);

    /* Creating and setting JFrame */
    final JFrame frame = new JFrame(TwoButtonShimmy.class.getSimpleName());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(null);
    frame.setLocationRelativeTo(null);
    frame.getContentPane().setBackground(Color.DARK_GRAY);
    frame.getContentPane().add(btnInfinite);
    frame.getContentPane().add(btnFinite);
    frame.setSize(400, 200);
    frame.setVisible(true);
  }
}
