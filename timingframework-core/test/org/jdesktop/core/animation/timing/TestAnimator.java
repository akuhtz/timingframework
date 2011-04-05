package org.jdesktop.core.animation.timing;

import java.util.concurrent.TimeUnit;

import org.jdesktop.core.animation.timing.sources.ManualTimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.junit.Assert;
import org.junit.Test;

public class TestAnimator {

  @Test(expected = IllegalArgumentException.class)
  public void noTimingSource1() {
    new AnimatorBuilder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void noTimingSource2() {
    AnimatorBuilder.setDefaultTimingSource(new ManualTimingSource());
    try {
      new AnimatorBuilder().build();
    } catch (IllegalArgumentException e) {
      Assert.fail("An Animator built with a non-null default TimingSource should be okay.");
    }
    AnimatorBuilder.setDefaultTimingSource(null);
    new AnimatorBuilder().build();
  }

  @Test(expected = IllegalStateException.class)
  public void start1() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.start();
    a.start();
  }

  @Test(expected = IllegalStateException.class)
  public void start2() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.startReverse();
    a.start();
  }

  @Test(expected = IllegalStateException.class)
  public void startReverse1() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.startReverse();
    a.startReverse();
  }

  @Test(expected = IllegalStateException.class)
  public void startReverse2() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.start();
    a.startReverse();
  }

  @Test(expected = IllegalStateException.class)
  public void reverseNow1() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.reverseNow();
  }

  @Test(expected = IllegalStateException.class)
  public void reverseNow2() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.start();
    a.pause();
    a.reverseNow();
  }

  @Test
  public void reverseNow3() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.start();
    a.reverseNow();
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeDuration() {
    new AnimatorBuilder(new ManualTimingSource()).setDuration(-10, TimeUnit.MILLISECONDS).build();
  }

  private int beginCount, endCount, reverseCount, repeatCount, timingEventCount;

  private void resetCounts() {
    beginCount = endCount = reverseCount = repeatCount = timingEventCount = 0;
  }

  final private TimingTarget counter = new TimingTarget() {

    public void timingEvent(Animator source, double fraction) {
      timingEventCount++;
    }

    public void reverse(Animator source) {
      reverseCount++;
    }

    public void repeat(Animator source) {
      repeatCount++;
    }

    public void end(Animator source) {
      endCount++;
    }

    public void begin(Animator source) {
      beginCount++;
    }
  };

  @Test
  public void timingTarget1() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new AnimatorBuilder(ts).build(); // 1 second
    resetCounts();
    a.addTarget(counter);
    a.start();
    a.await();
    Assert.assertEquals(1, beginCount);
    Assert.assertEquals(1, endCount);
    Assert.assertEquals(0, reverseCount);
    Assert.assertEquals(0, repeatCount);
    Assert.assertTrue("roughly 66 ticks", timingEventCount > 63 && timingEventCount < 69);
    ts.dispose();
  }

  @Test
  public void timingTarget2() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new AnimatorBuilder(ts).setDuration(150, TimeUnit.MILLISECONDS).setRepeatCount(4).build();
    resetCounts();
    a.addTarget(counter);
    a.start();
    a.await();
    Assert.assertEquals(1, beginCount);
    Assert.assertEquals(1, endCount);
    Assert.assertEquals(0, reverseCount);
    Assert.assertEquals(3, repeatCount);
    Assert.assertTrue("roughly 40 ticks", timingEventCount > 36 && timingEventCount < 44);
    ts.dispose();
  }

  @Test
  public void timingTarget3() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new AnimatorBuilder(ts).build();
    resetCounts();
    a.addTarget(counter);
    a.start();
    a.reverseNow();
    a.await();
    Thread.sleep(1); // wait for callbacks
    Assert.assertEquals(1, beginCount);
    Assert.assertEquals(1, endCount);
    Assert.assertEquals(1, reverseCount);
    Assert.assertEquals(0, repeatCount);
    // duration will be short due to quick reverse
    ts.dispose();
  }

  @Test
  public void timingTarget4() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new AnimatorBuilder(ts).build();
    resetCounts();
    a.addTarget(counter);
    a.start();
    Thread.sleep(10);
    a.reverseNow();
    a.reverseNow();
    a.await();
    Assert.assertEquals(1, beginCount);
    Assert.assertEquals(1, endCount);
    Assert.assertEquals(2, reverseCount);
    Assert.assertEquals(0, repeatCount);
    Assert.assertTrue("roughly 66 ticks", timingEventCount > 63 && timingEventCount < 69);
    ts.dispose();
  }
}
