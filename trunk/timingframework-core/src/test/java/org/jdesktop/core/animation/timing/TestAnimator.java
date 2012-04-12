package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
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
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    a.start();
    a.pause();
    Assert.assertFalse(a.reverseNow());
  }

  @Test
  public void reverseNow3() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    a.start();
    Assert.assertTrue(a.reverseNow());
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeDuration() {
    new Animator.Builder(new ManualTimingSource()).setDuration(-10, TimeUnit.MILLISECONDS).build();
  }

  @Test
  public void stop1() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    Assert.assertTrue(a.stop());
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
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
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
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
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(1, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    Assert.assertFalse(a.stop());
    Assert.assertFalse(a.cancel());
  }

  @Test
  public void stop4() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    Assert.assertFalse(a.stop());
  }

  @Test
  public void cancel1() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    a.start();
    Assert.assertTrue(a.cancel());
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(0, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
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
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(0, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
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
    Assert.assertEquals(1, counter.getBeginCount());
    Assert.assertEquals(0, counter.getEndCount());
    Assert.assertEquals(0, counter.getReverseCount());
    Assert.assertEquals(0, counter.getRepeatCount());
    Assert.assertFalse(a.stop());
    Assert.assertFalse(a.cancel());
  }

  @Test
  public void cancel4() {
    Animator a = new Animator.Builder(new ManualTimingSource()).build();
    CountingTimingTarget counter = new CountingTimingTarget();
    a.addTarget(counter);
    Assert.assertFalse(a.cancel());
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
    Assert.assertTrue("roughly 66 ticks", ticks > 63 && ticks < 69);
    ts.dispose();
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
  }

  @Test
  public void timingTargetOrder1() throws InterruptedException {
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    // Add targets to Animator
    List<OrderedTimingTarget> targets = getTargets();
    for (OrderedTimingTarget ott : targets) {
      a.addTarget(ott);
    }
    a.start();
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailedMessage(), ott.getFailed());
    }
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailedMessage(), ott.getFailed());
    }
    Assert.assertTrue(a.stop());
  }

  @Test
  public void timingTargetOrder2() throws InterruptedException {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder b = new Animator.Builder(ts);
    // Add targets to Builder
    List<OrderedTimingTarget> targets = getTargets();
    for (OrderedTimingTarget ott : targets) {
      b.addTarget(ott);
    }
    Animator a = b.build();
    a.start();
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailedMessage(), ott.getFailed());
    }
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailedMessage(), ott.getFailed());
    }
    Assert.assertTrue(a.stop());
  }

  @Test
  public void timingTargetOrder3() throws InterruptedException {
    ManualTimingSource ts = new ManualTimingSource();
    Animator.Builder b = new Animator.Builder(ts);
    // Add half of targets to Builder
    List<OrderedTimingTarget> targets = getTargets();
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
      Assert.assertFalse(ott.getFailedMessage(), ott.getFailed());
    }
    OrderedTimingTarget.resetCallOrder();
    ts.tick();
    for (OrderedTimingTarget ott : targets) {
      Assert.assertFalse(ott.getFailedMessage(), ott.getFailed());
    }
    Assert.assertTrue(a.stop());
  }

  private static class OrderedTimingTarget implements TimingTarget {

    private static int f_callOrder = 0;

    static void resetCallOrder() {
      f_callOrder = 0;
    }

    private final int f_index;

    private String f_failed = null;

    OrderedTimingTarget(int index) {
      f_index = index;
    }

    int getIndex() {
      return f_index;
    }

    boolean getFailed() {
      return f_failed != null;
    }

    String getFailedMessage() {
      return f_failed;
    }

    @Override
    public void begin(Animator source) {
      // nothing
    }

    @Override
    public void end(Animator source) {
      // nothing

    }

    @Override
    public void repeat(Animator source) {
      // nothing

    }

    @Override
    public void reverse(Animator source) {
      // nothing

    }

    @Override
    public void timingEvent(Animator source, double fraction) {
      /*
       * Reset
       */
      f_failed = null;
      /*
       * Signal a failure if we were not called in the right order.
       */
      if (f_index != f_callOrder)
        f_failed = "Was #" + f_callOrder + " target called but should have been #" + f_index + " target called";
      f_callOrder++;
    }

  }

  private List<OrderedTimingTarget> getTargets() {
    final List<OrderedTimingTarget> result = new ArrayList<TestAnimator.OrderedTimingTarget>();
    for (int i = 0; i < 10; i++) {
      result.add(new OrderedTimingTarget(i));
    }
    return result;
  }
}
