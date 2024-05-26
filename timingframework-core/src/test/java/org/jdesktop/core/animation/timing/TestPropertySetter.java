package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.timing.Animator.Direction;
import org.jdesktop.core.animation.timing.sources.ManualTimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class TestPropertySetter {

    static class MyProps {

        int value;

        public int getValue() {
            return value;
        }

        public void setValue(Object value) {
            throw new IllegalStateException("should not get matched");
        }

        public void setValue(double value) {
            /*
             * Forced non-exact match on primitive value
             */
            this.value = (int) value;
        }

        private byte byteValue;

        public byte sneakyGetByteValue() {
            return byteValue;
        }

        public void setByteValue(byte byteValue) {
            this.byteValue = byteValue;
        }

        public void setByteValue(Object byteValue) {
            throw new IllegalStateException("should not get matched");
        }

        public void setByteValue(int byteValue) {
            throw new IllegalStateException("should not get matched");
        }

        @Override
        public String toString() {
            return "PropTest [value=" + value + ", byteValue=" + byteValue + "]";
        }
    }

    static class ValueTarget extends TimingTargetAdapter {

        // values to check
        int valueAtBegin;

        Direction startDirectionAtBegin;

        Direction currentDirectionAtBegin;

        int valueAtFirstTimingEvent;

        Direction currentDirectionAtFirstTimingEvent;

        // internals
        private final MyProps props;

        private boolean firstTimingEvent = true;

        ValueTarget(MyProps value) {
            props = value;
        }

        @Override
        public void begin(Animator source) {
            valueAtBegin = props.getValue();
            startDirectionAtBegin = source.getStartDirection();
            currentDirectionAtBegin = source.getCurrentDirection();
        }

        @Override
        public void timingEvent(Animator source, double fraction) {
            if (firstTimingEvent) {
                firstTimingEvent = false;
                valueAtFirstTimingEvent = props.getValue();
                currentDirectionAtFirstTimingEvent = source.getCurrentDirection();
            }
        }
    }

    @Test
    public void noSuchProperty1() {
        MyProps pt = new MyProps();
        Assertions.assertThrows(IllegalArgumentException.class, () -> PropertySetter.getTarget(pt, "wrong", 1, 2, 3));
    }

    @Test
    public void noSuchPropertyTo1() {
        MyProps pt = new MyProps();
        Assertions.assertThrows(IllegalArgumentException.class, () -> PropertySetter.getTargetTo(pt, "wrong", 1, 2, 3));
    }

    @Test
    public void noSuchPropertyTo2() {
        MyProps pt = new MyProps();
        Assertions
            .assertThrows(IllegalArgumentException.class,
                () -> PropertySetter.getTargetTo(pt, "byteValue", (byte) 1, (byte) 2, (byte) 3));
    }

    @Test
    public void valueProperty() {
        MyProps pt = new MyProps();
        TimingTarget tt = PropertySetter.getTarget(pt, "value", 1, 2, 3);
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).addTarget(tt).build();
        a.start();
        ts.tick();
        Assertions.assertEquals(1, pt.getValue());
        while (a.isRunning()) {
            ts.tick();
        }
        Assertions.assertEquals(3, pt.getValue());
    }

    @Test
    public void byteValueProperty() {
        MyProps pt = new MyProps();
        TimingTarget tt = PropertySetter.getTarget(pt, "byteValue", (byte) 1, (byte) 2, (byte) 3);
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).addTarget(tt).build();
        a.start();
        ts.tick();
        Assertions.assertEquals((byte) 1, pt.sneakyGetByteValue());
        while (a.isRunning()) {
            ts.tick();
        }
        Assertions.assertEquals((byte) 3, pt.sneakyGetByteValue());
    }

    @Test
    public void byteValueToIntProperty() {
        /*
         * Test assignable match to a primitive method
         */
        MyProps pt = new MyProps();
        TimingTarget tt = PropertySetter.getTarget(pt, "value", (byte) 1, (byte) 2, (byte) 3);
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).addTarget(tt).build();
        a.start();
        ts.tick();
        Assertions.assertEquals(1, pt.getValue());
        while (a.isRunning()) {
            ts.tick();
        }
        Assertions.assertEquals(3, pt.getValue());
    }

    @Test
    public void valuePropertyTo() {
        MyProps pt = new MyProps();
        pt.setValue(100);
        TimingTarget tt = PropertySetter.getTargetTo(pt, "value", 1, 2, 3);
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).addTarget(tt).build();
        a.start();
        ts.tick();
        Assertions.assertEquals(100, pt.getValue());
        while (a.isRunning()) {
            ts.tick();
        }
        Assertions.assertEquals(3, pt.getValue());
    }

    @Test
    public void valueSetInBegin() {
        MyProps pt = new MyProps();
        TimingTarget tt = PropertySetter.getTarget(pt, "value", 1, 2);
        ValueTarget vt = new ValueTarget(pt);
        ManualTimingSource ts = new ManualTimingSource();
        Animator a = new Animator.Builder(ts).addTarget(tt).addTarget(vt).build();
        pt.setValue(-999);
        a.start();
        ts.tick();
        Assertions.assertEquals(1, vt.valueAtBegin);
        while (a.isRunning()) {
            ts.tick();
        }
        Assertions.assertEquals(2, pt.getValue());
    }

    @Test
    public void forwardStartCalled() throws InterruptedException {
        MyProps pt = new MyProps();
        TimingTarget tt = PropertySetter.getTarget(pt, "value", 1, 50);
        ValueTarget vt = new ValueTarget(pt);
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).addTarget(tt).addTarget(vt).setStartDirection(Direction.FORWARD).build();
        pt.setValue(-999);
        a.start();
        a.await();
        ts.dispose();

        Assertions.assertEquals(1, vt.valueAtBegin);
        Assertions.assertSame(vt.startDirectionAtBegin, Direction.FORWARD);
        Assertions.assertSame(vt.currentDirectionAtBegin, Direction.FORWARD);

        Assertions.assertTrue(vt.valueAtFirstTimingEvent < 4); // time dependent
        Assertions.assertSame(vt.currentDirectionAtFirstTimingEvent, Direction.FORWARD);

        Assertions.assertEquals(50, pt.getValue());
    }

    @Test
    public void backwardStartCalled() throws InterruptedException {
        MyProps pt = new MyProps();
        TimingTarget tt = PropertySetter.getTarget(pt, "value", 1, 50);
        ValueTarget vt = new ValueTarget(pt);
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).addTarget(tt).addTarget(vt).setStartDirection(Direction.BACKWARD).build();
        pt.setValue(-999);
        a.start();
        a.await();
        ts.dispose();

        Assertions.assertEquals(50, vt.valueAtBegin);
        Assertions.assertSame(vt.startDirectionAtBegin, Direction.BACKWARD);
        Assertions.assertSame(vt.currentDirectionAtBegin, Direction.BACKWARD);

        Assertions.assertTrue(vt.valueAtFirstTimingEvent > 46); // time dependent
        Assertions.assertSame(vt.currentDirectionAtFirstTimingEvent, Direction.BACKWARD);

        Assertions.assertEquals(1, pt.getValue());
    }

    @Test
    public void forwardStartReverseCalled() throws InterruptedException {
        MyProps pt = new MyProps();
        TimingTarget tt = PropertySetter.getTarget(pt, "value", 1, 50);
        ValueTarget vt = new ValueTarget(pt);
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).addTarget(tt).addTarget(vt).setStartDirection(Direction.FORWARD).build();
        pt.setValue(-999);
        a.startReverse();
        a.await();
        ts.dispose();

        Assertions.assertEquals(50, vt.valueAtBegin);
        Assertions.assertSame(vt.startDirectionAtBegin, Direction.FORWARD);
        Assertions.assertSame(vt.currentDirectionAtBegin, Direction.BACKWARD);

        Assertions.assertTrue(vt.valueAtFirstTimingEvent > 46); // time dependent
        Assertions.assertSame(vt.currentDirectionAtFirstTimingEvent, Direction.BACKWARD);

        Assertions.assertEquals(1, pt.getValue());
    }

    @Test
    public void backwardStartReverseCalled() throws InterruptedException {
        MyProps pt = new MyProps();
        TimingTarget tt = PropertySetter.getTarget(pt, "value", 1, 50);
        ValueTarget vt = new ValueTarget(pt);
        TimingSource ts = new ScheduledExecutorTimingSource();
        ts.init();
        Animator a = new Animator.Builder(ts).addTarget(tt).addTarget(vt).setStartDirection(Direction.BACKWARD).build();
        pt.setValue(-999);
        a.startReverse();
        a.await();
        ts.dispose();

        Assertions.assertEquals(1, vt.valueAtBegin);
        Assertions.assertSame(vt.startDirectionAtBegin, Direction.BACKWARD);
        Assertions.assertSame(vt.currentDirectionAtBegin, Direction.FORWARD);

        Assertions.assertTrue(vt.valueAtFirstTimingEvent < 4); // time dependent
        Assertions.assertSame(vt.currentDirectionAtFirstTimingEvent, Direction.FORWARD);

        Assertions.assertEquals(50, pt.getValue());
    }
}
