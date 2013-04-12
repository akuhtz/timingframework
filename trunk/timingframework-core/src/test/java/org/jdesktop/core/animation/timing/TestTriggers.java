package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.timing.sources.ManualTimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.jdesktop.core.animation.timing.triggers.TimingTrigger;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;
import org.junit.Assert;
import org.junit.Test;

public final class TestTriggers {

  @Test
  public void disarm() {
    TimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counterA = new CountingTimingTarget();
    a.addTarget(counterA);
    Animator t = new Animator.Builder(ts).build();
    CountingTimingTarget counterT = new CountingTimingTarget();
    t.addTarget(counterT);
    final Trigger trigger = TimingTrigger.addTrigger(a, t, TimingTriggerEvent.STOP);
    Assert.assertTrue(trigger.isArmed());
    trigger.disarm();
    Assert.assertFalse(trigger.isArmed());
  }

  @Test
  public void timingTrigger() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    CountingTimingTarget counterA = new CountingTimingTarget();
    Animator a = new Animator.Builder(ts).addTarget(counterA).build();
    CountingTimingTarget counterT = new CountingTimingTarget();
    Animator t = new Animator.Builder(ts).addTarget(counterT).build();
    TimingTrigger.addTrigger(a, t, TimingTriggerEvent.STOP);
    a.start();
    a.await();
    t.await();
    ts.dispose();

    Assert.assertEquals(1, counterA.getBeginCount());
    Assert.assertEquals(1, counterA.getEndCount());
    Assert.assertEquals(0, counterA.getReverseCount());
    Assert.assertEquals(0, counterA.getRepeatCount());

    Assert.assertEquals(1, counterT.getBeginCount());
    Assert.assertEquals(1, counterT.getEndCount());
    Assert.assertEquals(0, counterT.getReverseCount());
    Assert.assertEquals(0, counterT.getRepeatCount());
  }
}
