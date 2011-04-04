package org.jdesktop.swing.animation.timing.demos.ch15;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Animator.RepeatBehavior;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.KeyFramesBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.jdesktop.swing.animation.timing.triggers.ActionTrigger;

/**
 * The full-blown demo with all of the bells and whistles. This one uses the
 * facilities shown in all of the other variations, but adds both multi-step and
 * non-linear interpolation. It does this by creating a KeyFrames object to hold
 * the times/values/splines used for each segment of the race. It also adds an
 * animation for the rotation of the car (since the car should turn as it goes
 * around the curves) and sound effects (just to go completely overboard).
 * 
 * @author Chet Haase
 */
public final class MultiStepRace {

  public static void main(String args[]) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    TimingSource ts = new SwingTimerTimingSource(10, TimeUnit.MILLISECONDS);
    AnimatorBuilder.setDefaultTimingSource(ts);
    ts.init();

    Runnable doCreateAndShowGUI = new Runnable() {
      public void run() {
        new MultiStepRace("Multi-Step Race");
      }
    };
    SwingUtilities.invokeLater(doCreateAndShowGUI);
  }

  public static final int RACE_TIME = 10000;

  private final Animator animator;
  private final SoundEffects soundEffects;

  /** Creates a new instance of BasicRace */
  public MultiStepRace(String appName) {
    RaceGUI basicGUI = new RaceGUI(appName);

    animator = new AnimatorBuilder().setDuration(RACE_TIME, TimeUnit.MILLISECONDS).setRepeatCount(Animator.INFINITE)
        .setRepeatBehavior(RepeatBehavior.LOOP).build();

    // We're going to need a more involved PropertyRange object
    // that has all curves of the track in it, as well as
    // non-linear movement around the curves
    Point[] values = { TrackView.START_POS, TrackView.FIRST_TURN_START, TrackView.FIRST_TURN_END, TrackView.SECOND_TURN_START,
        TrackView.SECOND_TURN_END, TrackView.THIRD_TURN_START, TrackView.THIRD_TURN_END, TrackView.FOURTH_TURN_START,
        TrackView.START_POS };
    // Calculate the keyTimes based on the distances that must be
    // traveled on each leg of the journey
    double totalDistance = 0;
    double segmentDistance[] = new double[values.length];
    for (int i = 0; i < (values.length - 1); ++i) {
      segmentDistance[i] = values[i].distance(values[i + 1]);
      totalDistance += segmentDistance[i];
    }
    segmentDistance[(values.length - 1)] = values[(values.length - 1)].distance(values[0]);
    totalDistance += segmentDistance[(values.length - 1)];
    double times[] = new double[values.length];
    double elapsedTime = 0.0f;
    times[0] = 0.0f;
    times[values.length - 1] = 1.0f;
    for (int i = 0; i < (values.length - 2); ++i) {
      times[i + 1] = elapsedTime + (segmentDistance[i] / totalDistance);
      elapsedTime = times[i + 1];
    }

    /*
     * For realistic movement, we want a big acceleration on the straightaways.
     */
    Interpolator initialSpline = new SplineInterpolator(1.00f, 0.00f, 0.2f, .2f);
    Interpolator straightawaySpline = new SplineInterpolator(0.50f, 0.20f, .50f, .80f);
    Interpolator curveSpline = new SplineInterpolator(0.50f, 0.20f, .50f, .80f);
    Interpolator finalSpline = new SplineInterpolator(0.50f, 0.00f, .50f, 1.00f);
    Interpolator[] interps = { null, initialSpline, curveSpline, straightawaySpline, curveSpline, straightawaySpline, curveSpline,
        straightawaySpline, finalSpline };

    final KeyFramesBuilder<Point> builder = new KeyFramesBuilder<Point>(values[0]);
    for (int i = 1; i < values.length; i++) {
      builder.addFrame(values[i], times[i], interps[i]);

    }
    final KeyFrames<Point> keyFrames = builder.build();

    /*
     * This PropertySetter enables the animation for the car movement all the
     * way around the track.
     */
    final TimingTarget modifier = PropertySetter.getTarget(basicGUI.getTrack(), "carPosition", keyFrames);
    animator.addTarget(modifier);

    /*
     * Now create similar keyframes for rotation of car.
     */
    int[] rotationKeyValues = { 360, 315, 270, 225, 180, 135, 90, 45, 0 };
    Interpolator straightawayTurnSpline = new SplineInterpolator(1.0f, 0.0f, 1.0f, 0.0f);
    Interpolator curveTurnSpline = new SplineInterpolator(0.0f, 0.5f, 0.5f, 1.0f);
    Interpolator[] rotationInterps = { null, straightawayTurnSpline, curveTurnSpline, straightawayTurnSpline, curveTurnSpline,
        straightawayTurnSpline, curveTurnSpline, straightawayTurnSpline, curveTurnSpline };
    final KeyFramesBuilder<Integer> rotationBuilder = new KeyFramesBuilder<Integer>(rotationKeyValues[0]);
    for (int i = 1; i < values.length; i++) {
      rotationBuilder.addFrame(rotationKeyValues[i], times[i], rotationInterps[i]);
    }
    KeyFrames<Integer> rotationKeyFrames = rotationBuilder.build();
    final TimingTarget rotationModifier = PropertySetter.getTarget(basicGUI.getTrack(), "carRotation", rotationKeyFrames);
    animator.addTarget(rotationModifier);

    /*
     * Finally, add sound effects, triggered by the same animator.
     */
    soundEffects = new SoundEffects(rotationKeyFrames);
    animator.addTarget(soundEffects);

    /*
     * Instead of manually tracking the events, have the framework do the work
     * by setting up a trigger.
     */
    JButton goButton = basicGUI.getControlPanel().getGoButton();
    JButton pauseResumeButton = basicGUI.getControlPanel().getPauseResumeButton();
    JButton stopButton = basicGUI.getControlPanel().getStopButton();
    ActionTrigger.addTrigger(goButton, animator);
    pauseResumeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (animator.isPaused()) {
          animator.resume();
        } else {
          if (animator.isRunning())
            animator.pause();
        }
      }
    });
    stopButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (animator.isRunning())
          animator.stop();
        stopSoundEffects();
      }
    });
  }

  public void stopSoundEffects() {
    if (soundEffects != null)
      soundEffects.stop();
  }
}
