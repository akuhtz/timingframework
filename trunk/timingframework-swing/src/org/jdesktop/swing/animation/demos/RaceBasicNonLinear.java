package org.jdesktop.swing.animation.demos;

import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * Simple subclass of {@link RaceBasic} that uses a
 * {@link AccelerationInterpolator} to give a non-linear motion effect to the
 * car's movement.
 * <p>
 * This demo is discussed in Chapter 14 on pages 363&ndash;364 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008). In the book it is referred
 * to as <tt>NonLinearRace</tt> rather than <tt>RaceBasicNonLinear</tt>.
 * 
 * @author Chet Haase
 */
public class RaceBasicNonLinear extends RaceBasic {

  public static void main(String args[]) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    TimingSource ts = new SwingTimerTimingSource(10, TimeUnit.MILLISECONDS);
    AnimatorBuilder.setDefaultTimingSource(ts);
    ts.init();

    Runnable doCreateAndShowGUI = new Runnable() {
      public void run() {
        new RaceBasicNonLinear("Non-Linear Race");
      }
    };
    SwingUtilities.invokeLater(doCreateAndShowGUI);
  }

  public RaceBasicNonLinear(String appName) {
    super(appName);
  }

  @Override
  protected Animator getAnimator() {
    return new AnimatorBuilder().setDuration(RACE_TIME, TimeUnit.MILLISECONDS)
        .setInterpolator(new AccelerationInterpolator(0.5, 0.2)).addTarget(this).build();
  }
}
