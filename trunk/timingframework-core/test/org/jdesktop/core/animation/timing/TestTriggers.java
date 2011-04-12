package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.jdesktop.core.animation.timing.triggers.TimingTrigger;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;
import org.junit.Assert;
import org.junit.Test;

public final class TestTriggers {

  @Test
  public void test1() throws InterruptedException {
    TimingSource ts = new ScheduledExecutorTimingSource();
    ts.init();
    Animator a = new AnimatorBuilder(ts).build(); // 1 second
    CountingTimingTarget counterA = new CountingTimingTarget();
    a.addTarget(counterA);
    Animator t = new AnimatorBuilder(ts).build(); // 1 second
    CountingTimingTarget counterT = new CountingTimingTarget();
    t.addTarget(counterT);
    TimingTrigger.addTrigger(a, t, TimingTriggerEvent.STOP);
    a.start();
    a.await();
    t.await();

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
