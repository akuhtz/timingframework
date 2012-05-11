package org.jdesktop.core.animation.timing;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jdesktop.core.animation.timing.sources.ManualTimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.junit.Assert;
import org.junit.Test;

public final class TestAnimator {

  @Test(expected = IllegalArgumentException.class)
  public void noTimingSource1() {
    new Animator.Builder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void noTimingSource2() {
    Animator.setDefaultTimingSource(new ManualTimingSource());
    try {
      new Animator.Builder().build();
    } catch (IllegalArgumentException e) {
      Assert.fail("An Animator built with a non-null default TimingSource should be okay.");
    }
    Animator.setDefaultTimingSource(null);
    new Animator.Builder().build();
  }

  @Test(expected = IllegalStateException.class)
  public void start1() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    a.start();
    a.start();
  }

  @Test(expected = IllegalStateException.class)
  public void start2() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    a.startReverse();
    a.start();
  }

  @Test(expected = IllegalStateException.class)
  public void startReverse1() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    a.startReverse();
    a.startReverse();
  }

  @Test(expected = IllegalStateException.class)
  public void startReverse2() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    a.start();
    a.startReverse();
  }

  public void reverseNow1() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    Assert.assertFalse(a.reverseNow());
  }

  public void reverseNow2() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    a.pause();
    Assert.assertFalse(a.reverseNow());
  }

  public void reverseNow3() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    a.pause();
    ts.tick();
    Assert.assertFalse(a.reverseNow());
  }

  @Test
  public void reverseNow4() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    Assert.assertTrue(a.reverseNow());
  }

  @Test
  public void reverseNow5() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    Assert.assertTrue(a.reverseNow());
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeDuration() {
    new Animator.Builder(new ManualTimingSource()).setDuration(-10, TimeUnit.MILLISECONDS).build();
  }

  @Test
  public void stop1() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    Assert.assertTrue(a.stop());
    ts.tick();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void stop2() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    ts.tick();
    Assert.assertTrue(a.stop());
    ts.tick();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void stop3() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    ts.tick();
    Assert.assertTrue(a.stop());
    ts.tick();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    Assert.assertFalse(a.stop());
    Assert.assertFalse(a.cancel());
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void stop4() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    Assert.assertFalse(a.stop());
    ts.tick();
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void cancel1() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    Assert.assertTrue(a.cancel());
    ts.tick();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(0, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void cancel2() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    ts.tick();
    Assert.assertTrue(a.cancel());
    ts.tick();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(0, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void cancel3() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    ts.tick();
    Assert.assertTrue(a.cancel());
    ts.tick();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(0, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    Assert.assertFalse(a.stop());
    Assert.assertFalse(a.cancel());
    ts.tick();
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void cancel4() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    Assert.assertFalse(a.cancel());
    ts.tick();
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void timingTarget1() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build(); // 1 second
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    a.await();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    int ticks = counter.getTimingEventCount();
    Assert.assertTrue("expected roughly 66 ticks; actually " + ticks + " ticks", ticks > 56 && ticks < 76);
    ts.dispose();
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void timingTarget2() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setDuration(150, TimeUnit.MILLISECONDS).setRepeatCount(4).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    a.await();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(3, counter.getRepeatCount());
    int ticks = counter.getTimingEventCount();
    Assert.assertTrue("roughly 40 ticks", ticks > 36 && ticks < 44);
    ts.dispose();
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void timingTarget3() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    Assert.assertTrue(a.reverseNow());
    a.await();
    Thread.sleep(1); // wait for callbacks
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(1, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    // duration will be short due to quick reverse
    ts.dispose();
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void timingTarget4() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    Thread.sleep(10);
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    a.await();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(2, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    int ticks = counter.getTimingEventCount();
    Assert.assertTrue("roughly 66 ticks", ticks > 63 && ticks < 69);
    ts.dispose();
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void timingTargetOrder1() throws InterruptedException {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    // Add targets to Animator
    List<OrderedTimingTarget> targets = OrderedTimingTarget.getSomeTargets();
    for (OrderedTimingTarget ott : targets) {
      a.addTarget(ott);
    }
    a.start();
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailureMessage(), ott.invokedInOrder());
    }
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailureMessage(), ott.invokedInOrder());
    }
    Assert.assertTrue(a.stop());
    for (OrderedTimingTarget ott : targets) {
      Assert.assertTrue(ott.getProtocolMsg(), ott.isProtocolOkay());
    }
  }

  @Test
  public void timingTargetOrder2() throws InterruptedException {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder b = new Animator.Builder(ts);
    // Add targets to Builder
    List<OrderedTimingTarget> targets = OrderedTimingTarget.getSomeTargets();
    for (OrderedTimingTarget ott : targets) {
      b.addTarget(ott);
    }
    Animator a = b.build();
    a.start();
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailureMessage(), ott.invokedInOrder());
    }
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailureMessage(), ott.invokedInOrder());
    }
    Assert.assertTrue(a.stop());
    for (OrderedTimingTarget ott : targets) {
      Assert.assertTrue(ott.getProtocolMsg(), ott.isProtocolOkay());
    }
  }

  @Test
  public void timingTargetOrder3() throws InterruptedException {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder b = new Animator.Builder(ts);
    // Add half of targets to Builder
    List<OrderedTimingTarget> targets = OrderedTimingTarget.getSomeTargets();
    int half = targets.size() / 2;
    for (OrderedTimingTarget ott : targets) {
      if (ott.getIndex() <= half)
        b.addTarget(ott);
    }
    Animator a = b.build();
    // Add half of targets to Animator
    for (OrderedTimingTarget ott : targets) {
      if (ott.getIndex() > half)
        a.addTarget(ott);
    }
    a.start();
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailureMessage(), ott.invokedInOrder());
    }
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailureMessage(), ott.invokedInOrder());
    }
    Assert.assertTrue(a.stop());
    for (OrderedTimingTarget ott : targets) {
      Assert.assertTrue(ott.getProtocolMsg(), ott.isProtocolOkay());
    }
  }
}