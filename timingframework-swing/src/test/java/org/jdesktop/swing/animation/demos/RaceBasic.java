package org.jdesktop.swing.animation.demos;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * The simplest version of the race track animation. It sets up an
 * {@link Animator} to move the car from one position to another, linearly, over
 * a given time period.
 * <p>
 * This demo is discussed in Chapter 14 on pages 357&ndash;359 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008). In the book it is referred
 * to as <tt>BasicRace</tt> rather than <tt>RaceBasic</tt>.
 * 
 * @author Chet Haase
 */
public class RaceBasic extends TimingTargetAdapter {

  public static void main(String args[]) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    TimingSource ts = new SwingTimerTimingSource();
    Animator.setDefaultTimingSource(ts);
    ts.init();
    Runnable doCreateAndShowGUI = new Runnable() {
      public void run() {
        new RaceBasic("Swing Race (Basic)");
      }
    };
    SwingUtilities.invokeLater(doCreateAndShowGUI);
  }

  public static final int RACE_TIME = 2;

  Point start = RaceTrackView.START_POS;
  Point end = RaceTrackView.FIRST_TURN_START;
  Point current = new Point();
  Animator animator;
  RaceTrackView track;
  RaceControlPanel controlPanel;

  /** Creates a new instance of BasicRace */
  public RaceBasic(String appName) {
    final RaceGUI basicGUI = new RaceGUI(appName);
    controlPanel = basicGUI.getControlPanel();
    final JButton goButton = controlPanel.getGoButton();
    final JButton reverseButton = controlPanel.getReverseButton();
    final JButton pauseResumeButton = controlPanel.getPauseResumeButton();
    final JButton stopButton = controlPanel.getStopButton();

    goButton.setEnabled(true);
    reverseButton.setEnabled(false);
    pauseResumeButton.setEnabled(false);
    stopButton.setEnabled(false);

    goButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        animator.restart();
        basicGUI.getTrack().setCarReverse(false);
      }
    });
    reverseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (animator.isPaused()) {
          java.awt.Toolkit.getDefaultToolkit().beep();
          return;
        }

        final boolean reverseSucceeded = animator.reverseNow();
        if (reverseSucceeded) {
          basicGUI.getTrack().toggleCarReverse();
        } else {
          animator.startReverse();
          basicGUI.getTrack().setCarReverse(true);
        }
      }
    });
    pauseResumeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (animator.isPaused()) {
          animator.resume();
          reverseButton.setEnabled(true);
          stopButton.setEnabled(true);
        } else {
          if (animator.isRunning()) {
            animator.pause();
            reverseButton.setEnabled(false);
            stopButton.setEnabled(false);
          }
        }
      }
    });
    stopButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        animator.stop();
      }
    });
    track = basicGUI.getTrack();
    animator = getAnimator();
  }

  /**
   * So that subclasses can customize.
   * 
   * @return an animation.
   */
  protected Animator getAnimator() {
    return new Animator.Builder().setDuration(RACE_TIME, SECONDS).addTarget(this).build();
  }

  /**
   * Calculate and set the current car position based on the animation fraction.
   */
  @Override
  public void timingEvent(Animator source, double fraction) {
    // Simple linear interpolation to find current position
    current.x = (int) (start.x + (end.x - start.x) * fraction);
    current.y = (int) (start.y + (end.y - start.y) * fraction);

    // set the new position; this will force a repaint in TrackView
    // and will display the car in the new position
    track.setCarPosition(current);
  }

  @Override
  public void begin(Animator source) {
    controlPanel.getReverseButton().setEnabled(true);
    controlPanel.getPauseResumeButton().setEnabled(true);
    controlPanel.getStopButton().setEnabled(true);
  }

  @Override
  public void end(Animator source) {
    controlPanel.getReverseButton().setEnabled(false);
    controlPanel.getPauseResumeButton().setEnabled(false);
    controlPanel.getStopButton().setEnabled(false);
  }
}
