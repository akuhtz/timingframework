package org.jdesktop.swt.animation.timing.demos.ch15;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.KeyFramesBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.DiscreteInterpolator;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

/**
 * A console application that demonstrates use of a {@link DiscreteInterpolator}
 * using {@link KeyFrames} within a {@link PropertySetter} animation.
 * <p>
 * This demo is discussed in Chapter 15 on page 410 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * <p>
 * Because this is a console application it is a good chance to use the
 * <tt>util.concurrent</tt>-based {@link ScheduledExecutorTimingSource} rather
 * than a {@link SWTTimingSource}. This timing source avoids any dependency upon
 * Swing.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class DiscreteInterpolation extends TimingTargetAdapter {

  private final CountDownLatch f_done = new CountDownLatch(1);
  private int f_intValue;

  /** Creates a new instance of DiscreteInterpolation */
  public DiscreteInterpolation() {
  }

  public void setIntValue(int intValue) {
    f_intValue = intValue;
    System.out.println("intValue = " + f_intValue);
  }

  @Override
  public void end(Animator source) {
    f_done.countDown();
  }

  public static void main(String[] args) {
    TimingSource ts = new ScheduledExecutorTimingSource(100, TimeUnit.MILLISECONDS);
    AnimatorBuilder.setDefaultTimingSource(ts);
    ts.init();

    DiscreteInterpolation object = new DiscreteInterpolation();

    final KeyFrames<Integer> keyFrames = new KeyFramesBuilder<Integer>().addFrames(2, 6, 3, 5, 4)
        .setInterpolator(DiscreteInterpolator.getInstance()).build();
    System.out.println("-- Key Frames --");
    int i = 0;
    for (KeyFrames.Frame<Integer> frame : keyFrames) {
      final String s = frame.getInterpolator() == null ? "null" : frame.getInterpolator().getClass().getSimpleName();
      System.out.printf("Frame %d: value=%d timeFraction=%f interpolator=%s\n", i++, frame.getValue(), frame.getTimeFraction(), s);
    }
    final Animator anim = new AnimatorBuilder().setDuration(3, TimeUnit.SECONDS)
        .addTarget(PropertySetter.getTarget(object, "intValue", keyFrames)).addTarget(object).build();
    System.out.println("-- Animation --");
    anim.start();

    try {
      object.f_done.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    ts.dispose();
  }
}
