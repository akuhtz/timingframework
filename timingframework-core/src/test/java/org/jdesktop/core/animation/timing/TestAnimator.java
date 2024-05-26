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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class TestAnimator {

    @Test
    public void noTimingSource1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Animator.Builder().build());
    }

    @Test
    public void noTimingSource2() {
        Animator.setDefaultTimingSource(new ManualTimingSource());
        try {
            new Animator.Builder().build();
        }
        catch (IllegalArgumentException e) {
            Assertions.fail("An Animator built with a non-null default TimingSource should be okay.");
        }
        Animator.setDefaultTimingSource(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Animator.Builder().build());
    }

    @Test
    public void start1() {
        Animator a = new Animator.Builder(new ManualTimingSource()).build();
        a.start();
        Assertions.assertThrows(IllegalStateException.class, () -> a.start());
    }

    @Test
    public void start2() {
        Animator a = new Animator.Builder(new ManualTimingSource()).build();
        a.startReverse();
        Assertions.assertThrows(IllegalStateException.class, () -> a.start());
    }

    @Test
    public void startReverse1() {
        Animator a = new Animator.Builder(new ManualTimingSource()).build();
        a.startReverse();
        Assertions.assertThrows(IllegalStateException.class, () -> a.startReverse());
    }

    @Test
    public void startReverse2() {
        Animator a = new Animator.Builder(new ManualTimingSource()).build();
        a.start();
        Assertions.assertThrows(IllegalStateException.class, () -> a.startReverse());
    }

    @Test
    public void reuse1() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).build();
        Assertions.assertFalse(a.isRunning());
        a.start();
        Assertions.assertTrue(a.isRunning());
        Assertions.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
        a.await();
        Assertions.assertFalse(a.isRunning());
        a.start();
        Assertions.assertTrue(a.isRunning());
        Assertions.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
        a.await();
        Assertions.assertFalse(a.isRunning());
        a.start();
        Assertions.assertTrue(a.isRunning());
        Assertions.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
        a.await();
        Assertions.assertFalse(a.isRunning());
        ts.dispose();
    }

    @Test
    public void reuse2() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).build();
        Assertions.assertFalse(a.isRunning());
        a.startReverse();
        Assertions.assertTrue(a.isRunning());
        Assertions.assertSame(Animator.Direction.BACKWARD, a.getCurrentDirection());
        a.await();
        Assertions.assertFalse(a.isRunning());
        a.start();
        Assertions.assertTrue(a.isRunning());
        Assertions.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
        a.await();
        Assertions.assertFalse(a.isRunning());
        a.startReverse();
        Assertions.assertTrue(a.isRunning());
        Assertions.assertSame(Animator.Direction.BACKWARD, a.getCurrentDirection());
        a.await();
        Assertions.assertFalse(a.isRunning());
        ts.dispose();
    }

    @Test
    public void reuse3() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).build();
        Assertions.assertFalse(a.isRunning());
        a.start();
        Assertions.assertTrue(a.isRunning());
        Assertions.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
        a.await();
        Assertions.assertFalse(a.isRunning());
        a.startReverse();
        Assertions.assertTrue(a.isRunning());
        Assertions.assertSame(Animator.Direction.BACKWARD, a.getCurrentDirection());
        a.await();
        Assertions.assertFalse(a.isRunning());
        a.start();
        Assertions.assertTrue(a.isRunning());
        Assertions.assertSame(Animator.Direction.FORWARD, a.getCurrentDirection());
        a.await();
        Assertions.assertFalse(a.isRunning());
        ts.dispose();
    }

    public void reverseNow1() {
        Animator a = new Animator.Builder(new ManualTimingSource()).build();
        Assertions.assertFalse(a.reverseNow());
    }

    public void reverseNow2() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        a.pause();
        Assertions.assertFalse(a.reverseNow());
    }

    public void reverseNow3() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        a.pause();
        ts.tick();
        Assertions.assertFalse(a.reverseNow());
    }

    @Test
    public void reverseNow4() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        Assertions.assertTrue(a.reverseNow());
    }

    @Test
    public void reverseNow5() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        Assertions.assertTrue(a.reverseNow());
    }

    @Test
    public void reverseNow6() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        Direction going = a.getCurrentDirection();
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow()); // even
        ts.tick();
        Assertions.assertSame(going, a.getCurrentDirection());
    }

    @Test
    public void reverseNow7() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        Direction going = a.getCurrentDirection();
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow()); // even
        ts.tick();
        Assertions.assertSame(going, a.getCurrentDirection());
    }

    @Test
    public void reverseNow8() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        Direction going = a.getCurrentDirection();
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow()); // odd
        ts.tick();
        Assertions.assertSame(going.getOppositeDirection(), a.getCurrentDirection());
    }

    @Test
    public void reverseNow9() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        Direction going = a.getCurrentDirection();
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow()); // odd
        ts.tick();
        Assertions.assertSame(going.getOppositeDirection(), a.getCurrentDirection());
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
        Assertions.assertEquals(2, cct.getBeginCount());
        Assertions.assertEquals(1, cct.getEndCount());
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
        Assertions.assertEquals(2, cct.getBeginCount());
        Assertions.assertEquals(1, cct.getEndCount());
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
        Assertions.assertEquals(2, cct.getBeginCount());
        Assertions.assertEquals(1, cct.getEndCount());
    }

    @Test
    public void restartThenStart() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        a.restart();
        Assertions.assertThrows(IllegalStateException.class, () -> a.start());
    }

    @Test
    public void restartThenStartReverse() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        a.restart();
        Assertions.assertThrows(IllegalStateException.class, () -> a.startReverse());
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
        Assertions.assertEquals(2, cct.getBeginCount());
        Assertions.assertEquals(1, cct.getEndCount());
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
        Assertions.assertEquals(2, cct.getBeginCount());
        Assertions.assertEquals(1, cct.getEndCount());
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
        Assertions.assertEquals(2, cct.getBeginCount());
        Assertions.assertEquals(1, cct.getEndCount());
    }

    @Test
    public void restartReverseThenStart() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        a.restartReverse();
        Assertions.assertThrows(IllegalStateException.class, () -> a.start());
    }

    @Test
    public void restartReverseThenStartReverse() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        ts.tick();
        a.restartReverse();
        Assertions.assertThrows(IllegalStateException.class, () -> a.startReverse());
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
        Assertions.assertEquals(2, cct.getBeginCount());
        Assertions.assertEquals(1, cct.getEndCount());
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
        Assertions.assertEquals(2, cct.getBeginCount());
        Assertions.assertEquals(1, cct.getEndCount());
    }

    @Test
    public void negativeDuration() {
        Assertions
            .assertThrows(IllegalArgumentException.class,
                () -> new Animator.Builder(new ManualTimingSource()).setDuration(-10, MILLISECONDS).build());
    }

    @Test
    public void stop1() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.addTarget(counter);
        a.start();
        Assertions.assertTrue(a.stop());
        ts.tick();
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(1, counter.getEndCount());
        Assertions.assertEquals(0, counter.getReverseCount());
        Assertions.assertEquals(0, counter.getRepeatCount());
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
    }

    @Test
    public void stop2() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.addTarget(counter);
        a.start();
        ts.tick();
        Assertions.assertTrue(a.stop());
        ts.tick();
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(1, counter.getEndCount());
        Assertions.assertEquals(0, counter.getReverseCount());
        Assertions.assertEquals(0, counter.getRepeatCount());
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
    }

    @Test
    public void stop3() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.addTarget(counter);
        a.start();
        ts.tick();
        Assertions.assertTrue(a.stop());
        ts.tick();
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(1, counter.getEndCount());
        Assertions.assertEquals(0, counter.getReverseCount());
        Assertions.assertEquals(0, counter.getRepeatCount());
        Assertions.assertFalse(a.stop());
        Assertions.assertFalse(a.cancel());
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
    }

    @Test
    public void stop4() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.addTarget(counter);
        Assertions.assertFalse(a.stop());
        ts.tick();
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
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
        Assertions.assertTrue(a.stop());
        a.await();
        ts.dispose();
        Assertions.assertTrue(sleeper.isProtocolOkay(), sleeper.getProtocolMsg());
    }

    @Test
    public void cancel1() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.addTarget(counter);
        a.start();
        Assertions.assertTrue(a.cancel());
        ts.tick();
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(0, counter.getEndCount());
        Assertions.assertEquals(0, counter.getReverseCount());
        Assertions.assertEquals(0, counter.getRepeatCount());
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
    }

    @Test
    public void cancel2() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.addTarget(counter);
        a.start();
        ts.tick();
        Assertions.assertTrue(a.cancel());
        ts.tick();
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(0, counter.getEndCount());
        Assertions.assertEquals(0, counter.getReverseCount());
        Assertions.assertEquals(0, counter.getRepeatCount());
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
    }

    @Test
    public void cancel3() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.addTarget(counter);
        a.start();
        ts.tick();
        Assertions.assertTrue(a.cancel());
        ts.tick();
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(0, counter.getEndCount());
        Assertions.assertEquals(0, counter.getReverseCount());
        Assertions.assertEquals(0, counter.getRepeatCount());
        Assertions.assertFalse(a.stop());
        Assertions.assertFalse(a.cancel());
        ts.tick();
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
    }

    @Test
    public void cancel4() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.addTarget(counter);
        Assertions.assertFalse(a.cancel());
        ts.tick();
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
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
        Assertions.assertTrue(a.cancel());
        a.await();
        ts.dispose();
        Assertions.assertTrue(sleeper.isProtocolOkay(), sleeper.getProtocolMsg());
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
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(1, counter.getEndCount());
        Assertions.assertEquals(0, counter.getReverseCount());
        Assertions.assertEquals(0, counter.getRepeatCount());
        int ticks = counter.getTimingEventCount();
        Assertions.assertTrue(ticks > 55 && ticks < 75, "expected roughly 66 ticks; actually " + ticks + " ticks");
        ts.dispose();
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
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
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(1, counter.getEndCount());
        Assertions.assertEquals(0, counter.getReverseCount());
        Assertions.assertEquals(3, counter.getRepeatCount());
        int ticks = counter.getTimingEventCount();
        Assertions.assertTrue(ticks > 35 && ticks < 45, "expected roughly 40 ticks; actually " + ticks + " ticks");
        ts.dispose();
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
    }

    @Test
    public void timingTarget3() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.addTarget(counter);
        a.start();
        Assertions.assertTrue(a.reverseNow());
        a.await();
        Thread.sleep(1); // wait for callbacks
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(1, counter.getEndCount());
        Assertions.assertEquals(1, counter.getReverseCount());
        Assertions.assertEquals(0, counter.getRepeatCount());
        // duration will be short due to quick reverse
        ts.dispose();
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
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
        Assertions.assertTrue(a.reverseNow());
        Assertions.assertTrue(a.reverseNow());
        a.await();
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(1, counter.getEndCount());
        Assertions
            .assertTrue(counter.getReverseCount() >= 1,
                "counter.getReverseCount()=" + counter.getReverseCount() + " should be >= 1");
        Assertions.assertEquals(0, counter.getRepeatCount());
        int ticks = counter.getTimingEventCount();
        /*
         * If this fails try again (it is highly timing dependent)
         */
        Assertions.assertTrue(ticks > 63 && ticks < 69, "roughly 66 ticks");
        ts.dispose();
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
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
            Assertions.assertFalse(ott.invokedInOrder(), ott.getFailureMessage());
        }
        OrderedTimingTarget.resetCallOrder();
        ts.tick();
        for (OrderedTimingTarget ott : targets) {
            Assertions.assertFalse(ott.invokedInOrder(), ott.getFailureMessage());
        }
        Assertions.assertTrue(a.stop());
        for (OrderedTimingTarget ott : targets) {
            Assertions.assertTrue(ott.isProtocolOkay(), ott.getProtocolMsg());
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
            Assertions.assertFalse(ott.invokedInOrder(), ott.getFailureMessage());
        }
        OrderedTimingTarget.resetCallOrder();
        ts.tick();
        for (OrderedTimingTarget ott : targets) {
            Assertions.assertFalse(ott.invokedInOrder(), ott.getFailureMessage());
        }
        Assertions.assertTrue(a.stop());
        for (OrderedTimingTarget ott : targets) {
            Assertions.assertTrue(ott.isProtocolOkay(), ott.getProtocolMsg());
        }
    }

    @org.junit.jupiter.api.Test
    public void timingTargetOrder3() throws InterruptedException {
        ManualTimingSource ts = new ManualTimingSource();
        Animator.Builder b = new Animator.Builder(ts);
        // Add half of targets to Builder
        List<OrderedTimingTarget> targets = OrderedTimingTarget.getSomeTargets();
        int half = targets.size() / 2;
        for (OrderedTimingTarget ott : targets) {
            if (ott.getIndex() <= half) {
                b.addTarget(ott);
            }
        }
        Animator a = b.build();
        // Add half of targets to Animator
        for (OrderedTimingTarget ott : targets) {
            if (ott.getIndex() > half) {
                a.addTarget(ott);
            }
        }
        a.start();
        OrderedTimingTarget.resetCallOrder();
        ts.tick();
        for (OrderedTimingTarget ott : targets) {
            Assertions.assertFalse(ott.invokedInOrder(), ott.getFailureMessage());
        }
        OrderedTimingTarget.resetCallOrder();
        ts.tick();
        for (OrderedTimingTarget ott : targets) {
            Assertions.assertFalse(ott.invokedInOrder(), ott.getFailureMessage());
        }
        Assertions.assertTrue(a.stop());
        for (OrderedTimingTarget ott : targets) {
            Assertions.assertTrue(ott.isProtocolOkay(), ott.getProtocolMsg());
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
        Assertions.assertTrue(direction.isDirectionOkay(), direction.getDirectionMsg());
        Assertions.assertTrue(direction.allTimingEventsOkay(), direction.allTimingEventsMsg());
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
        Assertions.assertTrue(direction.isDirectionOkay(), direction.getDirectionMsg());
        Assertions.assertTrue(direction.allTimingEventsOkay(), direction.allTimingEventsMsg());
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
        Assertions.assertTrue(direction.isDirectionOkay(), direction.getDirectionMsg());
        Assertions.assertTrue(direction.allTimingEventsOkay(), direction.allTimingEventsMsg());
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
        Assertions.assertTrue(direction.isDirectionOkay(), direction.getDirectionMsg());
        Assertions.assertTrue(direction.allTimingEventsOkay(), direction.allTimingEventsMsg());
        // reuse animator
        a.removeTarget(direction);
        direction = new DirectionTimingTarget(a.getStartDirection());
        a.addTarget(direction);
        a.start();
        a.await();
        ts.dispose();
        Assertions.assertTrue(direction.isDirectionOkay(), direction.getDirectionMsg());
        Assertions.assertTrue(direction.allTimingEventsOkay(), direction.allTimingEventsMsg());
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
        Assertions.assertTrue(direction.isDirectionOkay(), direction.getDirectionMsg());
        Assertions.assertTrue(direction.allTimingEventsOkay(), direction.allTimingEventsMsg());
    }

    @org.junit.jupiter.api.Test
    public void timingTargetDirection6() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).build();
        DirectionTimingTarget direction = new DirectionTimingTarget(a.getStartDirection());
        a.addTarget(direction);
        a.start();
        Thread.sleep(40);
        Assertions.assertTrue(a.reverseNow());
        a.await();
        ts.dispose();
        Assertions.assertTrue(direction.isDirectionOkay(), direction.getDirectionMsg());
        Assertions.assertTrue(direction.allTimingEventsOkay(), direction.allTimingEventsMsg());
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
        Assertions.assertTrue(a.reverseNow());
        a.await();
        ts.dispose();
        Assertions.assertTrue(direction.isDirectionOkay(), direction.getDirectionMsg());
        // Because of the way the reverse callback works the below could fail
        // (rarely) if it hits right at a reversal
        Assertions.assertTrue(direction.allTimingEventsOkay(), direction.allTimingEventsMsg());
    }

    @Test
    public void disposeTimingTarget1() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).setDisposeTimingSource(true).build();
        a.start();
        a.await();
        Assertions.assertTrue(ts.isDisposed());
    }

    @org.junit.jupiter.api.Test
    public void disposeTimingTarget2() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a =
            new Animator.Builder(ts)
                .setRepeatCount(2).setRepeatBehavior(RepeatBehavior.REVERSE).setDisposeTimingSource(true).build();
        a.start();
        a.await();
        Assertions.assertTrue(ts.isDisposed());
    }

    @Test
    public void disposeTimingTarget3() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).build();
        a.start();
        a.await();
        Assertions.assertFalse(ts.isDisposed());
        ts.dispose();
    }

    @Test
    public void disposeTimingTarget4() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).setDisposeTimingSource(false).build();
        a.start();
        a.await();
        Assertions.assertFalse(ts.isDisposed());
        ts.dispose();
    }

    @Test
    public void addTimingTargetAfterStart() {
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).build();
        CountingTimingTarget counter = new CountingTimingTarget();
        a.start();
        a.addTarget(counter);
        ts.tick();
        Assertions.assertTrue(a.stop());
        ts.tick();
        Assertions.assertEquals(1, counter.getBeginCount());
        Assertions.assertEquals(1, counter.getEndCount());
        Assertions.assertEquals(0, counter.getReverseCount());
        Assertions.assertEquals(0, counter.getRepeatCount());
        Assertions.assertTrue(counter.isProtocolOkay(), counter.getProtocolMsg());
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
        Assertions.assertTrue(duration + MILLISECONDS.toNanos(10) > SECONDS.toNanos(2));
        ts.dispose();
    }

    @Test
    public void startDelay2() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).setStartDelay(50, MILLISECONDS).build();
        Assertions.assertEquals(50, a.getStartDelay());
        Assertions.assertSame(MILLISECONDS, a.getStartDelayTimeUnit());
        a.start();
        a.stopAndAwait();
        ts.dispose();
    }

    @Test
    public void startDelay3() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).setStartDelay(25, null).build();
        Assertions.assertEquals(25, a.getStartDelay());
        Assertions.assertSame(SECONDS, a.getStartDelayTimeUnit());
        a.start();
        a.stopAndAwait();
        ts.dispose();
    }

    @Test
    public void startDelay4() throws InterruptedException {
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).build();
        Assertions.assertEquals(0, a.getStartDelay());
        Assertions.assertSame(SECONDS, a.getStartDelayTimeUnit());
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
        Assertions.assertEquals("expected", a2.getDebugName());
        Assertions.assertSame(i, a2.getInterpolator());
        Assertions.assertEquals(5, a2.getDuration());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
        Assertions.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
        Assertions.assertEquals(5, a2.getRepeatCount());
        Assertions.assertSame(Direction.BACKWARD, a2.getStartDirection());
        Assertions.assertEquals(2, a2.getStartDelay());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
        Assertions.assertFalse(a2.getDisposeTimingSource());
        Assertions.assertEquals(0, a2.getTargets().size());
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
        Assertions.assertEquals("expected", a2.getDebugName());
        Assertions.assertSame(i, a2.getInterpolator());
        Assertions.assertEquals(5, a2.getDuration());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
        Assertions.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
        Assertions.assertEquals(5, a2.getRepeatCount());
        Assertions.assertSame(Direction.BACKWARD, a2.getStartDirection());
        Assertions.assertEquals(2, a2.getStartDelay());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
        Assertions.assertFalse(a2.getDisposeTimingSource());
        Assertions.assertEquals(1, a2.getTargets().size());
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
        Assertions.assertEquals("expected", a2.getDebugName());
        Assertions.assertSame(i, a2.getInterpolator());
        Assertions.assertEquals(5, a2.getDuration());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
        Assertions.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
        Assertions.assertEquals(5, a2.getRepeatCount());
        Assertions.assertSame(Direction.BACKWARD, a2.getStartDirection());
        Assertions.assertEquals(2, a2.getStartDelay());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
        Assertions.assertFalse(a2.getDisposeTimingSource());
        Assertions.assertEquals(1, a2.getTargets().size());
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
        Assertions.assertEquals("expected", a2.getDebugName());
        Assertions.assertSame(i, a2.getInterpolator());
        Assertions.assertEquals(5, a2.getDuration());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
        Assertions.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
        Assertions.assertEquals(5, a2.getRepeatCount());
        Assertions.assertSame(Direction.BACKWARD, a2.getStartDirection());
        Assertions.assertEquals(2, a2.getStartDelay());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
        Assertions.assertFalse(a2.getDisposeTimingSource());
        Assertions.assertEquals(0, a2.getTargets().size());
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
        Assertions.assertEquals("expected", a2.getDebugName());
        Assertions.assertSame(i, a2.getInterpolator());
        Assertions.assertEquals(5, a2.getDuration());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
        Assertions.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
        Assertions.assertEquals(5, a2.getRepeatCount());
        Assertions.assertSame(Direction.BACKWARD, a2.getStartDirection());
        Assertions.assertEquals(2, a2.getStartDelay());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
        Assertions.assertFalse(a2.getDisposeTimingSource());
        Assertions.assertEquals(0, a2.getTargets().size());
    }

    @org.junit.jupiter.api.Test
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
        Assertions.assertEquals("expected", a2.getDebugName());
        Assertions.assertSame(i, a2.getInterpolator());
        Assertions.assertEquals(5, a2.getDuration());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
        Assertions.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
        Assertions.assertEquals(5, a2.getRepeatCount());
        Assertions.assertSame(Direction.BACKWARD, a2.getStartDirection());
        Assertions.assertEquals(2, a2.getStartDelay());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
        Assertions.assertFalse(a2.getDisposeTimingSource());
        Assertions.assertEquals(1, a2.getTargets().size());
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
        Assertions.assertEquals("expected", a2.getDebugName());
        Assertions.assertSame(i, a2.getInterpolator());
        Assertions.assertEquals(5, a2.getDuration());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
        Assertions.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
        Assertions.assertEquals(5, a2.getRepeatCount());
        Assertions.assertSame(Direction.BACKWARD, a2.getStartDirection());
        Assertions.assertEquals(2, a2.getStartDelay());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
        Assertions.assertFalse(a2.getDisposeTimingSource());
        Assertions.assertEquals(1, a2.getTargets().size());
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
        Assertions.assertEquals("expected", a2.getDebugName());
        Assertions.assertSame(i, a2.getInterpolator());
        Assertions.assertEquals(5, a2.getDuration());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getDurationTimeUnit());
        Assertions.assertSame(RepeatBehavior.LOOP, a2.getRepeatBehavior());
        Assertions.assertEquals(5, a2.getRepeatCount());
        Assertions.assertSame(Direction.BACKWARD, a2.getStartDirection());
        Assertions.assertEquals(2, a2.getStartDelay());
        Assertions.assertSame(TimeUnit.MINUTES, a2.getStartDelayTimeUnit());
        Assertions.assertFalse(a2.getDisposeTimingSource());
        Assertions.assertEquals(0, a2.getTargets().size());
    }

    @org.junit.jupiter.api.Test
    public void jira25() throws InterruptedException {
        /*
         * Ensure that adding timing targets after animation start works properly and calls the target protocol
         * correctly.
         */
        ScheduledExecutorTimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        try {
            Animator.Builder ab1 = new Animator.Builder(ts);
            CountingTimingTarget tt1 = new CountingTimingTarget();
            CountingTimingTarget tt2 = new CountingTimingTarget();
            CountingTimingTarget tt3 = new CountingTimingTarget();
            CountingTimingTarget tt4 = new CountingTimingTarget();
            CountingTimingTarget tt5 = new CountingTimingTarget();
            final Animator a = ab1.addTarget(tt1).addTarget(tt2).setDuration(3, SECONDS).build();
            a.start();
            a.addTarget(tt3);
            Thread.sleep(10);
            a.addTarget(tt4);
            Thread.sleep(1000);
            a.addTarget(tt5);
            a.await();
            Assertions.assertTrue(tt1.isProtocolOkay(), tt1.getProtocolMsg());
            Assertions.assertTrue(tt2.isProtocolOkay(), tt2.getProtocolMsg());
            Assertions.assertTrue(tt3.isProtocolOkay(), tt3.getProtocolMsg());
            Assertions.assertTrue(tt4.isProtocolOkay(), tt4.getProtocolMsg());
            Assertions.assertTrue(tt5.isProtocolOkay(), tt5.getProtocolMsg());
            Assertions.assertEquals(1, tt1.getBeginCount());
            Assertions.assertEquals(1, tt2.getBeginCount());
            Assertions.assertEquals(1, tt3.getBeginCount());
            Assertions.assertEquals(1, tt4.getBeginCount());
            Assertions.assertEquals(1, tt5.getBeginCount());
            Assertions.assertEquals(1, tt1.getEndCount());
            Assertions.assertEquals(1, tt2.getEndCount());
            Assertions.assertEquals(1, tt3.getEndCount());
            Assertions.assertEquals(1, tt4.getEndCount());
            Assertions.assertEquals(1, tt5.getEndCount());
        }
        finally {
            ts.dispose();
        }
    }
}
