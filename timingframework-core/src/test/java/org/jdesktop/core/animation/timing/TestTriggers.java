package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.timing.sources.ManualTimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.jdesktop.core.animation.timing.triggers.TimingTrigger;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class TestTriggers {

  @org.junit.jupiter.api.Test
  public void disarm() {
    TimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).build();
    CountingTimingTarget counterA = new CountingTimingTarget();
    a.addTarget(counterA);
    Animator t = new Animator.Builder(ts).build();
    CountingTimingTarget counterT = new CountingTimingTarget();
    t.addTarget(counterT);
    final Trigger trigger = TimingTrigger.addTrigger(a, t, TimingTriggerEvent.STOP);
    Assertions.assertTrue(trigger.isArmed());
    trigger.disarm();
    Assertions.assertFalse(trigger.isArmed());
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

    Assertions.assertEquals(1, counterA.getBeginCount());
    Assertions.assertEquals(1, counterA.getEndCount());
    Assertions.assertEquals(0, counterA.getReverseCount());
    Assertions.assertEquals(0, counterA.getRepeatCount());

    Assertions.assertEquals(1, counterT.getBeginCount());
    Assertions.assertEquals(1, counterT.getEndCount());
    Assertions.assertEquals(0, counterT.getReverseCount());
    Assertions.assertEquals(0, counterT.getRepeatCount());
  }
}
