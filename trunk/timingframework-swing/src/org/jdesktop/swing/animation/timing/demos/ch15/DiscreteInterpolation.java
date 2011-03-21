package org.jdesktop.swing.animation.timing.demos.ch15;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.KeyValues;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.DiscreteInterpolator;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * A console application that demonstrates use of a {@link DiscreteInterpolator}
 * using {@link KeyFrames} within a {@link PropertySetter} animation.
 * <p>
 * This demo is discussed in Chapter 15 on page 410 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * <p>
 * Because this is a console application it is a good chance to use the
 * <tt>util.concurrent</tt>-based {@link ScheduledExecutorTimingSource} rather
 * than a {@link SwingTimerTimingSource}. This timing source avoids any
 * dependency upon Swing.
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
		TimingSource ts = new ScheduledExecutorTimingSource(100,
				TimeUnit.MILLISECONDS);
		AnimatorBuilder.setDefaultTimingSource(ts);
		ts.init();

		DiscreteInterpolation object = new DiscreteInterpolation();

		final KeyValues<Integer> keyValues = KeyValues.build(2, 6, 3, 5, 4);
		final KeyFrames<Integer> keyFrames = KeyFrames.build(keyValues,
				DiscreteInterpolator.getInstance());
		final PropertySetter ps = new PropertySetter(object, "intValue",
				keyFrames);

		final Animator anim = new AnimatorBuilder()
				.setDuration(3, TimeUnit.SECONDS).addTarget(ps)
				.addTarget(object).build();
		anim.start();

		try {
			object.f_done.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ts.dispose();
	}
}
