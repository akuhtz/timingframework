package org.jdesktop.swt.animation.demos;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

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
 * @author Tim Halloran
 */
public class RaceBasic extends TimingTargetAdapter {

  public static void main(String args[]) {
    final Display display = Display.getDefault();
    final Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());

    final TimingSource ts = new SWTTimingSource(display);
    AnimatorBuilder.setDefaultTimingSource(ts);
    ts.init();

    new RaceBasic(shell, "SWT Race (Basic)");

    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    ts.dispose();
    display.dispose();
  }

  protected static final int RACE_TIME = 2;
  Point start = RaceTrackView.START_POS;
  Point end = RaceTrackView.FIRST_TURN_START;
  Point current = new Point(0, 0);
  protected Animator animator;
  RaceTrackView track;
  RaceControlPanel controlPanel;

  /** Creates a new instance of BasicRace */
  public RaceBasic(Shell shell, String appName) {
    final RaceGUI basicGUI = new RaceGUI(shell, appName);
    controlPanel = basicGUI.getControlPanel();
    final Button goButton = controlPanel.getGoButton();
    final Button reverseButton = controlPanel.getReverseButton();
    final Button pauseResumeButton = controlPanel.getPauseResumeButton();
    final Button stopButton = controlPanel.getStopButton();

    goButton.setEnabled(true);
    reverseButton.setEnabled(false);
    pauseResumeButton.setEnabled(false);
    stopButton.setEnabled(false);

    goButton.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        animator.start();
        goButton.setEnabled(false);
        reverseButton.setEnabled(true);
        pauseResumeButton.setEnabled(true);
        stopButton.setEnabled(true);
        basicGUI.getTrack().setCarReverse(false);
      }
    });
    reverseButton.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
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
    pauseResumeButton.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
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
    stopButton.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        animator.stop();
        goButton.setEnabled(true);
        reverseButton.setEnabled(false);
        pauseResumeButton.setEnabled(false);
        stopButton.setEnabled(false);
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
    return new AnimatorBuilder().setDuration(RACE_TIME, TimeUnit.SECONDS).addTarget(this).build();
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
  public void end(Animator source) {
    controlPanel.getGoButton().setEnabled(true);
    controlPanel.getReverseButton().setEnabled(false);
    controlPanel.getPauseResumeButton().setEnabled(false);
    controlPanel.getStopButton().setEnabled(false);
  }
}
