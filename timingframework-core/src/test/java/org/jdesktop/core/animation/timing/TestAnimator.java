package org.jdesktop.core.animation.timing;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jdesktop.core.animation.timing.Animator.Direction;
import org.jdesktop.core.animation.timing.Animator.EndBehavior;
import org.jdesktop.core.animation.timing.Animator.RepeatBehavior;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
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

  @Test
  public void reuse1() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    Assert.assertFalse(a.isRunning());
    a.start();
    Assert.assertTrue(a.isRunning());
    Assert.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
    a.await();
    Assert.assertFalse(a.isRunning());
    a.start();
    Assert.assertTrue(a.isRunning());
    Assert.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
    a.await();
    Assert.assertFalse(a.isRunning());
    a.start();
    Assert.assertTrue(a.isRunning());
    Assert.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
    a.await();
    Assert.assertFalse(a.isRunning());
    ts.dispose();
  }

  @Test
  public void reuse2() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    Assert.assertFalse(a.isRunning());
    a.startReverse();
    Assert.assertTrue(a.isRunning());
    Assert.assertSame(Animator.Direction.BACKWARD, a.getCurrentDirection());
    a.await();
    Assert.assertFalse(a.isRunning());
    a.start();
    Assert.assertTrue(a.isRunning());
    Assert.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
    a.await();
    Assert.assertFalse(a.isRunning());
    a.startReverse();
    Assert.assertTrue(a.isRunning());
    Assert.assertSame(Animator.Direction.BACKWARD, a.getCurrentDirection());
    a.await();
    Assert.assertFalse(a.isRunning());
    ts.dispose();
  }

  @Test
  public void reuse3() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    Assert.assertFalse(a.isRunning());
    a.start();
    Assert.assertTrue(a.isRunning());
    Assert.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
    a.await();
    Assert.assertFalse(a.isRunning());
    a.startReverse();
    Assert.assertTrue(a.isRunning());
    Assert.assertSame(Animator.Direction.BACKWARD, a.getCurrentDirection());
    a.await();
    Assert.assertFalse(a.isRunning());
    a.start();
    Assert.assertTrue(a.isRunning());
    Assert.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
    a.await();
    Assert.assertFalse(a.isRunning());
    ts.dispose();
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

  @Test
  public void reverseNow6() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    Direction going = a.getCurrentDirection();
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow()); // even
    ts.tick();
    Assert.assertSame(going, a.getCurrentDirection());
  }

  @Test
  public void reverseNow7() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    Direction going = a.getCurrentDirection();
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow()); // even
    ts.tick();
    Assert.assertSame(going, a.getCurrentDirection());
  }

  @Test
  public void reverseNow8() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    Direction going = a.getCurrentDirection();
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow()); // odd
    ts.tick();
    Assert.assertSame(going.getOppositeDirection(), a.getCurrentDirection());
  }

  @Test
  public void reverseNow9() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    Direction going = a.getCurrentDirection();
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow());
    Assert.assertTrue(a.reverseNow()); // odd
    ts.tick();
    Assert.assertSame(going.getOppositeDirection(), a.getCurrentDirection());
  }

  @Test
  public void restart1() {
    ManualTimingSource ts = new ManualTimingSource();
    CountingTimingTarget cct = new CountingTimingTarget();
    Animator a = new Animator.Builder(ts).build();
    a.addTarget(cct);
    a.start();
    a.restart();
    ts.tick();
    ts.tick();
    ts.tick();
    Assert.assertEquals(2, cct.getBeginCount());
    Assert.assertEquals(1, cct.getEndCount());
  }

  @Test
  public void restart2() {
    ManualTimingSource ts = new ManualTimingSource();
    CountingTimingTarget cct = new CountingTimingTarget();
    Animator a = new Animator.Builder(ts).build();
    a.addTarget(cct);
    a.start();
    a.restart();
    a.restart();
    ts.tick();
    ts.tick();
    ts.tick();
    Assert.assertEquals(2, cct.getBeginCount());
    Assert.assertEquals(1, cct.getEndCount());
  }

  @Test
  public void restart3() {
    ManualTimingSource ts = new ManualTimingSource();
    CountingTimingTarget cct = new CountingTimingTarget();
    Animator a = new Animator.Builder(ts).build();
    a.addTarget(cct);
    a.start();
    ts.tick();
    a.restart();
    a.restart();
    a.restart();
    ts.tick();
    ts.tick();
    ts.tick();
    Assert.assertEquals(2, cct.getBeginCount());
    Assert.assertEquals(1, cct.getEndCount());
  }

  @Test(expected = IllegalStateException.class)
  public void restartThenStart() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    a.restart();
    a.start();
  }

  @Test(expected = IllegalStateException.class)
  public void restartThenStartReverse() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    a.restart();
    a.startReverse();
  }

  @Test
  public void restartReverse1() {
    ManualTimingSource ts = new ManualTimingSource();
    CountingTimingTarget cct = new CountingTimingTarget();
    Animator a = new Animator.Builder(ts).build();
    a.addTarget(cct);
    a.start();
    a.restartReverse();
    ts.tick();
    ts.tick();
    ts.tick();
    Assert.assertEquals(2, cct.getBeginCount());
    Assert.assertEquals(1, cct.getEndCount());
  }

  @Test
  public void restartReverse2() {
    ManualTimingSource ts = new ManualTimingSource();
    CountingTimingTarget cct = new CountingTimingTarget();
    Animator a = new Animator.Builder(ts).build();
    a.addTarget(cct);
    a.start();
    a.restartReverse();
    a.restartReverse();
    ts.tick();
    ts.tick();
    ts.tick();
    Assert.assertEquals(2, cct.getBeginCount());
    Assert.assertEquals(1, cct.getEndCount());
  }

  @Test
  public void restartReverse3() {
    ManualTimingSource ts = new ManualTimingSource();
    CountingTimingTarget cct = new CountingTimingTarget();
    Animator a = new Animator.Builder(ts).build();
    a.addTarget(cct);
    a.start();
    ts.tick();
    a.restartReverse();
    a.restartReverse();
    a.restartReverse();
    ts.tick();
    ts.tick();
    ts.tick();
    Assert.assertEquals(2, cct.getBeginCount());
    Assert.assertEquals(1, cct.getEndCount());
  }

  @Test(expected = IllegalStateException.class)
  public void restartReverseThenStart() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    a.restartReverse();
    a.start();
  }

  @Test(expected = IllegalStateException.class)
  public void restartReverseThenStartReverse() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    ts.tick();
    a.restartReverse();
    a.startReverse();
  }

  @Test
  public void restartAndRestartReverse1() {
    ManualTimingSource ts = new ManualTimingSource();
    CountingTimingTarget cct = new CountingTimingTarget();
    Animator a = new Animator.Builder(ts).build();
    a.addTarget(cct);
    a.start();
    ts.tick();
    a.restart();
    a.restartReverse();
    a.restart();
    ts.tick();
    ts.tick();
    ts.tick();
    Assert.assertEquals(2, cct.getBeginCount());
    Assert.assertEquals(1, cct.getEndCount());
  }

  @Test
  public void restartAndRestartReverse2() {
    ManualTimingSource ts = new ManualTimingSource();
    CountingTimingTarget cct = new CountingTimingTarget();
    Animator a = new Animator.Builder(ts).build();
    a.addTarget(cct);
    a.start();
    ts.tick();
    a.restartReverse();
    a.restart();
    a.restartReverse();
    ts.tick();
    ts.tick();
    ts.tick();
    Assert.assertEquals(2, cct.getBeginCount());
    Assert.assertEquals(1, cct.getEndCount());
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeDuration() {
    new Animator.Builder(new ManualTimingSource()).setDuration(-10, MILLISECONDS).build();
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
  public void stop5() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    SleepOnTickTimingTarget sleeper = new SleepOnTickTimingTarget();
    a.addTarget(sleeper);
    a.start();
    Thread.sleep(100);
    Assert.assertTrue(a.stop());
    a.await();
    ts.dispose();
    Assert.assertTrue(sleeper.getProtocolMsg(), sleeper.isProtocolOkay());
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
  public void cancel5() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    SleepOnTickTimingTarget sleeper = new SleepOnTickTimingTarget();
    a.addTarget(sleeper);
    a.start();
    Thread.sleep(100);
    Assert.assertTrue(a.cancel());
    a.await();
    ts.dispose();
    Assert.assertTrue(sleeper.getProtocolMsg(), sleeper.isProtocolOkay());
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
    Assert.assertTrue("expected roughly 66 ticks; actually " + ticks + " ticks", ticks > 55 && ticks < 75);
    ts.dispose();
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void timingTarget2() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setDuration(150, MILLISECONDS).setRepeatCount(4).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    a.await();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(3, counter.getRepeatCount());
    int ticks = counter.getTimingEventCount();
    Assert.assertTrue("expected roughly 40 ticks; actually " + ticks + " ticks", ticks > 35 && ticks < 45);
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
    Assert.assertTrue("counter.getReverseCount()=" + counter.getReverseCount() + " should be >= 1", counter.getReverseCount() >= 1);
    Assert.assertEquals(0, counter.getRepeatCount());
    int ticks = counter.getTimingEventCount();
    /*
     * If this fails try again (it is highly timing dependent)
     */
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

  @Test
  public void timingTargetDirection1() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    DirectionTimingTarget direction = new DirectionTimingTarget(a.getStartDirection());
    a.addTarget(direction);
    a.start();
    a.await();
    ts.dispose();
    Assert.assertTrue(direction.getDirectionMsg(), direction.isDirectionOkay());
    Assert.assertTrue(direction.allTimingEventsMsg(), direction.allTimingEventsOkay());
  }

  @Test
  public void timingTargetDirection2() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    DirectionTimingTarget direction = new DirectionTimingTarget(a.getStartDirection().getOppositeDirection());
    a.addTarget(direction);
    a.startReverse();
    a.await();
    ts.dispose();
    Assert.assertTrue(direction.getDirectionMsg(), direction.isDirectionOkay());
    Assert.assertTrue(direction.allTimingEventsMsg(), direction.allTimingEventsOkay());
  }

  @Test
  public void timingTargetDirection3() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setRepeatCount(2).setRepeatBehavior(RepeatBehavior.REVERSE).build();
    DirectionTimingTarget direction = new DirectionTimingTarget(a.getStartDirection());
    a.addTarget(direction);
    a.start();
    a.await();
    ts.dispose();
    Assert.assertTrue(direction.getDirectionMsg(), direction.isDirectionOkay());
    Assert.assertTrue(direction.allTimingEventsMsg(), direction.allTimingEventsOkay());
  }

  @Test
  public void timingTargetDirection4() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setRepeatCount(2).setRepeatBehavior(RepeatBehavior.REVERSE).build();
    DirectionTimingTarget direction = new DirectionTimingTarget(a.getStartDirection());
    a.addTarget(direction);
    a.start();
    a.await();
    Assert.assertTrue(direction.getDirectionMsg(), direction.isDirectionOkay());
    Assert.assertTrue(direction.allTimingEventsMsg(), direction.allTimingEventsOkay());
    // reuse animator
    a.removeTarget(direction);
    direction = new DirectionTimingTarget(a.getStartDirection());
    a.addTarget(direction);
    a.start();
    a.await();
    ts.dispose();
    Assert.assertTrue(direction.getDirectionMsg(), direction.isDirectionOkay());
    Assert.assertTrue(direction.allTimingEventsMsg(), direction.allTimingEventsOkay());
  }

  @Test
  public void timingTargetDirection5() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setRepeatCount(3).setRepeatBehavior(RepeatBehavior.REVERSE).build();
    DirectionTimingTarget direction = new DirectionTimingTarget(a.getStartDirection());
    a.addTarget(direction);
    a.start();
    a.await();
    ts.dispose();
    Assert.assertTrue(direction.getDirectionMsg(), direction.isDirectionOkay());
    Assert.assertTrue(direction.allTimingEventsMsg(), direction.allTimingEventsOkay());
  }

  @Test
  public void timingTargetDirection6() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    DirectionTimingTarget direction = new DirectionTimingTarget(a.getStartDirection());
    a.addTarget(direction);
    a.start();
    Thread.sleep(40);
    Assert.assertTrue(a.reverseNow());
    a.await();
    ts.dispose();
    Assert.assertTrue(direction.getDirectionMsg(), direction.isDirectionOkay());
    Assert.assertTrue(direction.allTimingEventsMsg(), direction.allTimingEventsOkay());
  }

  @Test
  public void timingTargetDirection7() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setRepeatCount(2).setRepeatBehavior(RepeatBehavior.REVERSE).build();
    DirectionTimingTarget direction = new DirectionTimingTarget(a.getStartDirection());
    a.addTarget(direction);
    a.start();
    Thread.sleep(40);
    Assert.assertTrue(a.reverseNow());
    a.await();
    ts.dispose();
    Assert.assertTrue(direction.getDirectionMsg(), direction.isDirectionOkay());
    // Because of the way the reverse callback works the below could fail
    // (rarely) if it hits right at a reversal
    Assert.assertTrue(direction.allTimingEventsMsg(), direction.allTimingEventsOkay());
  }

  @Test
  public void disposeTimingTarget1() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setDisposeTimingSource(true).build();
    a.start();
    a.await();
    Assert.assertTrue(ts.isDisposed());
  }

  @Test
  public void disposeTimingTarget2() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setRepeatCount(2).setRepeatBehavior(RepeatBehavior.REVERSE).setDisposeTimingSource(true)
        .build();
    a.start();
    a.await();
    Assert.assertTrue(ts.isDisposed());
  }

  @Test
  public void disposeTimingTarget3() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    a.start();
    a.await();
    Assert.assertFalse(ts.isDisposed());
    ts.dispose();
  }

  @Test
  public void disposeTimingTarget4() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setDisposeTimingSource(false).build();
    a.start();
    a.await();
    Assert.assertFalse(ts.isDisposed());
    ts.dispose();
  }

  @Test
  public void addTimingTargetAfterStart() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.start();
    a.addTarget(counter);
    Assert.assertTrue(a.stop());
    ts.tick();
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    Assert.assertTrue(counter.getProtocolMsg(), counter.isProtocolOkay());
  }

  @Test
  public void startDelay1() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setStartDelay(1, SECONDS).build();
    long duration = System.nanoTime();
    a.start();
    a.await();
    duration = System.nanoTime() - duration;
    Assert.assertTrue(duration + MILLISECONDS.toNanos(10) > SECONDS.toNanos(2));
    ts.dispose();
  }

  @Test
  public void startDelay2() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setStartDelay(50, MILLISECONDS).build();
    Assert.assertEquals(50, a.getStartDelay());
    Assert.assertSame(MILLISECONDS, a.getStartDelayTimeUnit());
    a.start();
    a.stopAndAwait();
    ts.dispose();
  }

  @Test
  public void startDelay3() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).setStartDelay(25, null).build();
    Assert.assertEquals(25, a.getStartDelay());
    Assert.assertSame(SECONDS, a.getStartDelayTimeUnit());
    a.start();
    a.stopAndAwait();
    ts.dispose();
  }

  @Test
  public void startDelay4() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new Animator.Builder(ts).build();
    Assert.assertEquals(0, a.getStartDelay());
    Assert.assertSame(SECONDS, a.getStartDelayTimeUnit());
    a.start();
    a.stopAndAwait();
    ts.dispose();
  }

  @Test
  public void testCopyBuilder1() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder ab1 = new Animator.Builder(ts);
    Animator.Builder ab2 = new Animator.Builder(ts);
    Interpolator i = new AccelerationInterpolator(0.4, 0.4);

    ab1.setDebugName("expected");
    ab1.setInterpolator(i);
    ab1.setDuration(5, TimeUnit.MINUTES);
    ab1.setEndBehavior(EndBehavior.HOLD);
    ab1.setRepeatBehavior(RepeatBehavior.LOOP);
    ab1.setRepeatCount(5);
    ab1.setStartDirection(Direction.BACKWARD);
    ab1.setStartDelay(2, TimeUnit.MINUTES);

    ab2.copy(ab1);
    Animator a2 = ab2.build();
    Assert.assertEquals("expected", a2.getDebugName());
    Assert.assertSame(i, a2.getInterpolator());
    Assert.assertEquals(5, a2.getDuration());
    Assert.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
    Assert.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
    Assert.assertEquals(5, a2.getRepeatCount());
    Assert.assertSame(Direction.BACKWARD, a2.getStartDirection());
    Assert.assertEquals(2, a2.getStartDelay());
    Assert.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
    Assert.assertFalse(a2.getDisposeTimingSource());
    Assert.assertEquals(0, a2.getTargets().size());
  }

  @Test
  public void testCopyBuilder2() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder ab1 = new Animator.Builder(ts);
    Animator.Builder ab2 = new Animator.Builder(ts);
    Interpolator i = new AccelerationInterpolator(0.4, 0.4);
    TimingTarget t1 = new CountingTimingTarget();

    ab1.setDebugName("expected");
    ab1.setInterpolator(i);
    ab1.setDuration(5, TimeUnit.MINUTES);
    ab1.setEndBehavior(EndBehavior.HOLD);
    ab1.setRepeatBehavior(RepeatBehavior.LOOP);
    ab1.setRepeatCount(5);
    ab1.setStartDirection(Direction.BACKWARD);
    ab1.setStartDelay(2, TimeUnit.MINUTES);
    ab1.addTarget(t1);

    ab2.copy(ab1);
    Animator a2 = ab2.build();
    Assert.assertEquals("expected", a2.getDebugName());
    Assert.assertSame(i, a2.getInterpolator());
    Assert.assertEquals(5, a2.getDuration());
    Assert.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
    Assert.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
    Assert.assertEquals(5, a2.getRepeatCount());
    Assert.assertSame(Direction.BACKWARD, a2.getStartDirection());
    Assert.assertEquals(2, a2.getStartDelay());
    Assert.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
    Assert.assertFalse(a2.getDisposeTimingSource());
    Assert.assertEquals(1, a2.getTargets().size());
  }

  @Test
  public void testCopyBuilder3() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder ab1 = new Animator.Builder(ts);
    Animator.Builder ab2 = new Animator.Builder(ts);
    Interpolator i = new AccelerationInterpolator(0.4, 0.4);
    TimingTarget t1 = new CountingTimingTarget();

    ab1.setDebugName("expected");
    ab1.setInterpolator(i);
    ab1.setDuration(5, TimeUnit.MINUTES);
    ab1.setEndBehavior(EndBehavior.HOLD);
    ab1.setRepeatBehavior(RepeatBehavior.LOOP);
    ab1.setRepeatCount(5);
    ab1.setStartDirection(Direction.BACKWARD);
    ab1.setStartDelay(2, TimeUnit.MINUTES);
    ab1.addTarget(t1);

    ab2.copy(ab1, true);
    Animator a2 = ab2.build();
    Assert.assertEquals("expected", a2.getDebugName());
    Assert.assertSame(i, a2.getInterpolator());
    Assert.assertEquals(5, a2.getDuration());
    Assert.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
    Assert.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
    Assert.assertEquals(5, a2.getRepeatCount());
    Assert.assertSame(Direction.BACKWARD, a2.getStartDirection());
    Assert.assertEquals(2, a2.getStartDelay());
    Assert.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
    Assert.assertFalse(a2.getDisposeTimingSource());
    Assert.assertEquals(1, a2.getTargets().size());
  }

  @Test
  public void testCopyBuilder4() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder ab1 = new Animator.Builder(ts);
    Animator.Builder ab2 = new Animator.Builder(ts);
    Interpolator i = new AccelerationInterpolator(0.4, 0.4);
    TimingTarget t1 = new CountingTimingTarget();

    ab1.setDebugName("expected");
    ab1.setInterpolator(i);
    ab1.setDuration(5, TimeUnit.MINUTES);
    ab1.setEndBehavior(EndBehavior.HOLD);
    ab1.setRepeatBehavior(RepeatBehavior.LOOP);
    ab1.setRepeatCount(5);
    ab1.setStartDirection(Direction.BACKWARD);
    ab1.setStartDelay(2, TimeUnit.MINUTES);
    ab1.addTarget(t1);

    ab2.copy(ab1, false);
    Animator a2 = ab2.build();
    Assert.assertEquals("expected", a2.getDebugName());
    Assert.assertSame(i, a2.getInterpolator());
    Assert.assertEquals(5, a2.getDuration());
    Assert.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
    Assert.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
    Assert.assertEquals(5, a2.getRepeatCount());
    Assert.assertSame(Direction.BACKWARD, a2.getStartDirection());
    Assert.assertEquals(2, a2.getStartDelay());
    Assert.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
    Assert.assertFalse(a2.getDisposeTimingSource());
    Assert.assertEquals(0, a2.getTargets().size());
  }

  @Test
  public void testCopyAnimation1() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder ab1 = new Animator.Builder(ts);
    Animator.Builder ab2 = new Animator.Builder(ts);
    Interpolator i = new AccelerationInterpolator(0.4, 0.4);

    ab1.setDebugName("expected");
    ab1.setInterpolator(i);
    ab1.setDuration(5, TimeUnit.MINUTES);
    ab1.setEndBehavior(EndBehavior.HOLD);
    ab1.setRepeatBehavior(RepeatBehavior.LOOP);
    ab1.setRepeatCount(5);
    ab1.setStartDirection(Direction.BACKWARD);
    ab1.setStartDelay(2, TimeUnit.MINUTES);
    Animator a1 = ab1.build();

    ab2.copy(a1);
    Animator a2 = ab2.build();
    Assert.assertEquals("expected", a2.getDebugName());
    Assert.assertSame(i, a2.getInterpolator());
    Assert.assertEquals(5, a2.getDuration());
    Assert.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
    Assert.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
    Assert.assertEquals(5, a2.getRepeatCount());
    Assert.assertSame(Direction.BACKWARD, a2.getStartDirection());
    Assert.assertEquals(2, a2.getStartDelay());
    Assert.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
    Assert.assertFalse(a2.getDisposeTimingSource());
    Assert.assertEquals(0, a2.getTargets().size());
  }

  @Test
  public void testCopyAnimation2() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder ab1 = new Animator.Builder(ts);
    Animator.Builder ab2 = new Animator.Builder(ts);
    Interpolator i = new AccelerationInterpolator(0.4, 0.4);
    TimingTarget t1 = new CountingTimingTarget();

    ab1.setDebugName("expected");
    ab1.setInterpolator(i);
    ab1.setDuration(5, TimeUnit.MINUTES);
    ab1.setEndBehavior(EndBehavior.HOLD);
    ab1.setRepeatBehavior(RepeatBehavior.LOOP);
    ab1.setRepeatCount(5);
    ab1.setStartDirection(Direction.BACKWARD);
    ab1.setStartDelay(2, TimeUnit.MINUTES);
    ab1.addTarget(t1);
    Animator a1 = ab1.build();

    ab2.copy(a1);
    Animator a2 = ab2.build();
    Assert.assertEquals("expected", a2.getDebugName());
    Assert.assertSame(i, a2.getInterpolator());
    Assert.assertEquals(5, a2.getDuration());
    Assert.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
    Assert.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
    Assert.assertEquals(5, a2.getRepeatCount());
    Assert.assertSame(Direction.BACKWARD, a2.getStartDirection());
    Assert.assertEquals(2, a2.getStartDelay());
    Assert.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
    Assert.assertFalse(a2.getDisposeTimingSource());
    Assert.assertEquals(1, a2.getTargets().size());
  }

  @Test
  public void testCopyAnimation3() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder ab1 = new Animator.Builder(ts);
    Animator.Builder ab2 = new Animator.Builder(ts);
    Interpolator i = new AccelerationInterpolator(0.4, 0.4);
    TimingTarget t1 = new CountingTimingTarget();

    ab1.setDebugName("expected");
    ab1.setInterpolator(i);
    ab1.setDuration(5, TimeUnit.MINUTES);
    ab1.setEndBehavior(EndBehavior.HOLD);
    ab1.setRepeatBehavior(RepeatBehavior.LOOP);
    ab1.setRepeatCount(5);
    ab1.setStartDirection(Direction.BACKWARD);
    ab1.setStartDelay(2, TimeUnit.MINUTES);
    ab1.addTarget(t1);
    Animator a1 = ab1.build();

    ab2.copy(a1, true);
    Animator a2 = ab2.build();
    Assert.assertEquals("expected", a2.getDebugName());
    Assert.assertSame(i, a2.getInterpolator());
    Assert.assertEquals(5, a2.getDuration());
    Assert.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
    Assert.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
    Assert.assertEquals(5, a2.getRepeatCount());
    Assert.assertSame(Direction.BACKWARD, a2.getStartDirection());
    Assert.assertEquals(2, a2.getStartDelay());
    Assert.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
    Assert.assertFalse(a2.getDisposeTimingSource());
    Assert.assertEquals(1, a2.getTargets().size());
  }

  @Test
  public void testCopyAnimation4() {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder ab1 = new Animator.Builder(ts);
    Animator.Builder ab2 = new Animator.Builder(ts);
    Interpolator i = new AccelerationInterpolator(0.4, 0.4);
    TimingTarget t1 = new CountingTimingTarget();

    ab1.setDebugName("expected");
    ab1.setInterpolator(i);
    ab1.setDuration(5, TimeUnit.MINUTES);
    ab1.setEndBehavior(EndBehavior.HOLD);
    ab1.setRepeatBehavior(RepeatBehavior.LOOP);
    ab1.setRepeatCount(5);
    ab1.setStartDirection(Direction.BACKWARD);
    ab1.setStartDelay(2, TimeUnit.MINUTES);
    ab1.addTarget(t1);
    Animator a1 = ab1.build();

    ab2.copy(a1, false);
    Animator a2 = ab2.build();
    Assert.assertEquals("expected", a2.getDebugName());
    Assert.assertSame(i, a2.getInterpolator());
    Assert.assertEquals(5, a2.getDuration());
    Assert.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
    Assert.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
    Assert.assertEquals(5, a2.getRepeatCount());
    Assert.assertSame(Direction.BACKWARD, a2.getStartDirection());
    Assert.assertEquals(2, a2.getStartDelay());
    Assert.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
    Assert.assertFalse(a2.getDisposeTimingSource());
    Assert.assertEquals(0, a2.getTargets().size());
  }
}
