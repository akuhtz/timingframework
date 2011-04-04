package org.jdesktop.swt.animation.timing.demos.ch15;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
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
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class BasicRace extends TimingTargetAdapter {

  public static void main(String args[]) {
    final Display display = Display.getDefault();
    final Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());

    final TimingSource ts = new SWTTimingSource(15, TimeUnit.MILLISECONDS, display);
    AnimatorBuilder.setDefaultTimingSource(ts);
    ts.init();

    new BasicRace(shell, "SWT Basic Race");

    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    ts.dispose();
    display.dispose();
  }

  public static final int RACE_TIME = 2000;
  Point start = TrackView.START_POS;
  Point end = TrackView.FIRST_TURN_START;
  Point current = new Point(0, 0);
  protected Animator animator;
  TrackView track;
  RaceControlPanel controlPanel;

  /** Creates a new instance of BasicRace */
  public BasicRace(Shell shell, String appName) {
    final RaceGUI basicGUI = new RaceGUI(shell, appName);
    controlPanel = basicGUI.getControlPanel();
    controlPanel.getGoButton().addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        animator.stop();
        animator.start();
        basicGUI.getTrack().setCarRotation(0);
      }
    });
    controlPanel.getReverseButton().addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        if (animator.isPaused()) {
          java.awt.Toolkit.getDefaultToolkit().beep();
          return;
        }

        if (animator.isRunning()) {
          animator.reverseNow();
          basicGUI.getTrack().reverseCarRotation();
        } else {
          animator.startReverse();
          basicGUI.getTrack().setCarRotation(180);
        }
      }
    });
    controlPanel.getPauseResumeButton().addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        if (animator.isPaused()) {
          animator.resume();
        } else {
          if (animator.isRunning())
            animator.pause();
        }
      }
    });
    controlPanel.getStopButton().addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
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
    return new AnimatorBuilder().setDuration(RACE_TIME, TimeUnit.MILLISECONDS).addTarget(this).build();
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
}
