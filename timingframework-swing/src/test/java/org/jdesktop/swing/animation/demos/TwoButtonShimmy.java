package org.jdesktop.swing.animation.demos;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.jdesktop.swing.animation.timing.triggers.TriggerUtility;

/**
 * A demonstration of moving buttons using the Timing Framework.
 * <p>
 * This demonstration uses property setters and triggers together to shimmy two
 * buttons back and forth across the window when they are clicked. "Infinite"
 * bounces back and forth until it is clicked again. "Once" goes back and forth
 * once and then stops. A mouse hover on either button causes the button to
 * slowly change to red. It slowly goes back to black when the mouse is moved
 * away.
 * 
 * @author Jan Studeny
 * @author Tim Halloran
 */
public final class TwoButtonShimmy {

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
    /*
     * Creating JButton with a infinite number of back and forth shimmy
     * repetitions
     */
    JButton btnInfinite = new JButton("Infinite");
    btnInfinite.setBounds(10, 10, 130, 30);
    // Movement on click
    TimingTarget ttInfinite = PropertySetter.getTarget(btnInfinite, "location", new Point(10, 10), new Point(250, 10));
    Animator animatorInfinite = new Animator.Builder().setRepeatCount(Animator.INFINITE).setDuration(3, SECONDS)
        .addTarget(ttInfinite).build();
    TriggerUtility.addActionTrigger(btnInfinite, animatorInfinite);
    // Red text color on mouse hover
    TimingTarget ttInfinite2 = PropertySetter.getTarget(btnInfinite, "foreground", Color.BLACK, Color.RED);
    Animator animatorInfinite2 = new Animator.Builder().setDuration(2, SECONDS).addTarget(ttInfinite2).build();
    TriggerUtility.addMouseTrigger(btnInfinite, animatorInfinite2, MouseTriggerEvent.ENTER, true);

    /*
     * Creating JButton with one back and forth shimmy
     */
    JButton btnFinite = new JButton("Once");
    btnFinite.setBounds(10, 50, 130, 30);
    // Movement on click
    TimingTarget ttFinite = PropertySetter.getTarget(btnFinite, "location", new Point(10, 50), new Point(250, 50),
        new Point(10, 50));
    Animator animatorFinite = new Animator.Builder().setDuration(6, SECONDS).addTarget(ttFinite).build();
    TriggerUtility.addActionTrigger(btnFinite, animatorFinite);
    // Red text color on mouse hover
    TimingTarget ttFinite2 = PropertySetter.getTarget(btnFinite, "foreground", Color.BLACK, Color.RED);
    Animator animatorFinite2 = new Animator.Builder().setDuration(2, SECONDS).addTarget(ttFinite2).build();
    TriggerUtility.addMouseTrigger(btnFinite, animatorFinite2, MouseTriggerEvent.ENTER, true);

    final JFrame frame = new JFrame("TwoButtonShimmy");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 200);
    frame.setLocationRelativeTo(null);
    frame.setLayout(null);
    frame.getContentPane().setBackground(new Color(225, 227, 185));
    frame.getContentPane().add(btnInfinite);
    frame.getContentPane().add(btnFinite);
    frame.setVisible(true);
  }
}
