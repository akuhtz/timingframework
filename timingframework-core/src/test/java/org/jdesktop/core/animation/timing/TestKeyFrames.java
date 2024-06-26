package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
import java.util.List;

import org.jdesktop.core.animation.timing.evaluators.EvaluatorInteger;
import org.jdesktop.core.animation.timing.evaluators.KnownEvaluators;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.interpolators.DiscreteInterpolator;
import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class TestKeyFrames {

    @Test
    public void builder1() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2);
        final KeyFrames<Integer> kf = b.build();
        Assertions.assertEquals(2, kf.size());
        int value = 1;
        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        for (int index = 0; index < kf.size(); index++) {
            KeyFrames.Frame<Integer> f = kf.getFrame(index);
            if (value == 1) {
                Assertions.assertNull(f.getInterpolator());
            }
            else {
                Assertions.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
            }
            Assertions.assertEquals(value++, f.getValue().intValue());
            Assertions.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
            timeFraction += fractionPerFrame;
        }
        Assertions.assertEquals(3, value);
    }

    @Test
    public void builder1iterator() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2);
        final KeyFrames<Integer> kf = b.build();
        Assertions.assertEquals(2, kf.size());
        int value = 1;
        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        for (KeyFrames.Frame<Integer> f : kf) {
            if (value == 1) {
                Assertions.assertNull(f.getInterpolator());
            }
            else {
                Assertions.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
            }
            Assertions.assertEquals(value++, f.getValue().intValue());
            Assertions.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
            timeFraction += fractionPerFrame;
        }
        Assertions.assertEquals(3, value);
    }

    @Test
    public void builder2() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2);
        b.addFrame(3);
        final KeyFrames<Integer> kf = b.build();
        Assertions.assertEquals(3, kf.size());
        int value = 1;
        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        for (int index = 0; index < kf.size(); index++) {
            KeyFrames.Frame<Integer> f = kf.getFrame(index);
            if (value == 1) {
                Assertions.assertNull(f.getInterpolator());
            }
            else {
                Assertions.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
            }
            Assertions.assertEquals(value++, f.getValue().intValue());
            Assertions.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
            timeFraction += fractionPerFrame;
        }
        Assertions.assertEquals(4, value);
    }

    @org.junit.jupiter.api.Test
    public void builder2iterator() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2);
        b.addFrame(3);
        final KeyFrames<Integer> kf = b.build();
        Assertions.assertEquals(3, kf.size());
        int value = 1;
        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        for (KeyFrames.Frame<Integer> f : kf) {
            if (value == 1) {
                Assertions.assertNull(f.getInterpolator());
            }
            else {
                Assertions.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
            }
            Assertions.assertEquals(value++, f.getValue().intValue());
            Assertions.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
            timeFraction += fractionPerFrame;
        }
        Assertions.assertEquals(4, value);
    }

    @Test
    public void builder3() {
        final int size = 10000; // a large (unlikely) number of frames
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        for (int i = 2; i <= size; i++) {
            b.addFrame(i);
        }
        final KeyFrames<Integer> kf = b.build();
        Assertions.assertEquals(size, kf.size());
        int value = 1;
        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        for (int index = 0; index < kf.size(); index++) {
            KeyFrames.Frame<Integer> f = kf.getFrame(index);
            if (value == 1) {
                Assertions.assertNull(f.getInterpolator());
            }
            else {
                Assertions.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
            }
            Assertions.assertEquals(value++, f.getValue().intValue());
            Assertions.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
            timeFraction += fractionPerFrame;
        }
        Assertions.assertEquals(size + 1, value);
    }

    @Test
    public void builder3iterator() {
        final int size = 10000; // a large (unlikely) number of frames
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        for (int i = 2; i <= size; i++) {
            b.addFrame(i);
        }
        final KeyFrames<Integer> kf = b.build();
        Assertions.assertEquals(size, kf.size());
        int value = 1;
        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        for (KeyFrames.Frame<Integer> f : kf) {
            if (value == 1) {
                Assertions.assertNull(f.getInterpolator());
            }
            else {
                Assertions.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
            }
            Assertions.assertEquals(value++, f.getValue().intValue());
            Assertions.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
            timeFraction += fractionPerFrame;
        }
        Assertions.assertEquals(size + 1, value);
    }

    @org.junit.jupiter.api.Test
    public void interpolator1() {
        final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2);
        b.addFrame(3);
        b.setInterpolator(i);
        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(i, kf.getFrame(1).getInterpolator());
        Assertions.assertSame(i, kf.getFrame(2).getInterpolator());
    }

    @Test
    public void interpolator2() {
        final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
        final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2, DiscreteInterpolator.getInstance());
        b.addFrame(3, a);
        b.setInterpolator(i);
        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(i, kf.getFrame(1).getInterpolator());
        Assertions.assertSame(i, kf.getFrame(2).getInterpolator());
    }

    @Test
    public void interpolator3() {
        final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
        final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.setInterpolator(i);
        b.addFrame(2, DiscreteInterpolator.getInstance());
        b.addFrame(3, a);
        b.setInterpolator(null); // clear
        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(a, kf.getFrame(2).getInterpolator());
    }

    @Test
    public void interpolator4() {
        final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
        final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2, DiscreteInterpolator.getInstance());
        b.addFrame(3, a);
        b.addFrame(4, i);
        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(3).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(a, kf.getFrame(2).getInterpolator());
        Assertions.assertSame(i, kf.getFrame(3).getInterpolator());
    }

    @Test
    public void timeFraction() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2, 0.3);
        b.addFrame(3, 0.4);
        b.addFrame(4, 1);
        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        Assertions.assertEquals(0.0, kf.getFrame(0).getTimeFraction(), 1e-9);
        Assertions.assertEquals(0.3, kf.getFrame(1).getTimeFraction(), 1e-9);
        Assertions.assertEquals(0.4, kf.getFrame(2).getTimeFraction(), 1e-9);
        Assertions.assertEquals(1.0, kf.getFrame(3).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @Test
    public void allThree() {
        final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
        final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2, 0.3, i);
        b.addFrame(3, 0.4, a);
        b.addFrame(4, 1, DiscreteInterpolator.getInstance());
        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        Assertions.assertEquals(0.0, kf.getFrame(0).getTimeFraction(), 1e-9);
        Assertions.assertEquals(0.3, kf.getFrame(1).getTimeFraction(), 1e-9);
        Assertions.assertEquals(0.4, kf.getFrame(2).getTimeFraction(), 1e-9);
        Assertions.assertEquals(1.0, kf.getFrame(3).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(i, kf.getFrame(1).getInterpolator());
        Assertions.assertSame(a, kf.getFrame(2).getInterpolator());
        Assertions.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @Test
    public void frame1() {
        KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2);
        KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3);
        KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4);
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(f1);
        b.addFrame(f2);
        b.addFrame(f3);

        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @Test
    public void frame2() {
        KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2, 0.3);
        KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3, 0.4);
        KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4, 1);
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(f1);
        b.addFrame(f2);
        b.addFrame(f3);

        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        Assertions.assertEquals(0.0, kf.getFrame(0).getTimeFraction(), 1e-9);
        Assertions.assertEquals(0.3, kf.getFrame(1).getTimeFraction(), 1e-9);
        Assertions.assertEquals(0.4, kf.getFrame(2).getTimeFraction(), 1e-9);
        Assertions.assertEquals(1.0, kf.getFrame(3).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @org.junit.jupiter.api.Test
    public void frame3() {
        final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
        final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
        KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2, i);
        KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3, a);
        KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4, DiscreteInterpolator.getInstance());
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(f1);
        b.addFrame(f2);
        b.addFrame(f3);

        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(i, kf.getFrame(1).getInterpolator());
        Assertions.assertSame(a, kf.getFrame(2).getInterpolator());
        Assertions.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @Test
    public void frame4() {
        final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
        final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
        KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2, 0.3, i);
        KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3, 0.4, a);
        KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4, 1, DiscreteInterpolator.getInstance());
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(f1);
        b.addFrame(f2);
        b.addFrame(f3);

        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        Assertions.assertEquals(0.0, kf.getFrame(0).getTimeFraction(), 1e-9);
        Assertions.assertEquals(0.3, kf.getFrame(1).getTimeFraction(), 1e-9);
        Assertions.assertEquals(0.4, kf.getFrame(2).getTimeFraction(), 1e-9);
        Assertions.assertEquals(1.0, kf.getFrame(3).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(i, kf.getFrame(1).getInterpolator());
        Assertions.assertSame(a, kf.getFrame(2).getInterpolator());
        Assertions.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @Test
    public void frame5() {
        KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2, -1, null);
        KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3, -1, null);
        KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4, -1, null);
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(f1);
        b.addFrame(f2);
        b.addFrame(f3);

        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @org.junit.jupiter.api.Test
    public void addFrames1() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrames(2, 3, 4);

        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(Integer.class, kf.getClassOfValue());

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @Test
    public void addFrames2() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>();
        b.addFrames(1, 2, 3, 4);

        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(Integer.class, kf.getClassOfValue());

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @Test
    public void addFrames3() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        List<Integer> l = new ArrayList<Integer>();
        l.add(2);
        l.add(3);
        l.add(4);
        b.addFrames(l);

        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(Integer.class, kf.getClassOfValue());

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @Test
    public void addFrames4() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>();
        List<Integer> l = new ArrayList<Integer>();
        l.add(1);
        l.add(2);
        l.add(3);
        l.add(4);
        b.addFrames(l);

        final KeyFrames<Integer> kf = b.build();

        Assertions.assertEquals(Integer.class, kf.getClassOfValue());

        Assertions.assertEquals(1, kf.getFrame(0).getValue().intValue());
        Assertions.assertEquals(2, kf.getFrame(1).getValue().intValue());
        Assertions.assertEquals(3, kf.getFrame(2).getValue().intValue());
        Assertions.assertEquals(4, kf.getFrame(3).getValue().intValue());

        double timeFraction = 0;
        final double fractionPerFrame = 1.0 / (kf.size() - 1);
        Assertions.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
        timeFraction += fractionPerFrame;
        Assertions.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

        Assertions.assertNull(kf.getFrame(0).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
        Assertions.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
    }

    @Test
    public void zeroFrame() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.build());
    }

    @Test
    public void oneFrame() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.build());
    }

    @Test
    public void nullValue1() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(null);
        b.addFrame(2);
        b.addFrame(3);
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.build());
    }

    @Test
    public void nullValue2() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame((Integer) null);
        b.addFrame(3);
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.build());
    }

    @Test
    public void nullValue3() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2);
        b.addFrame((Integer) null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.build());
    }

    @Test
    public void nullValue1frame() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.addFrame((KeyFrames.Frame<Integer>) null));
        b.addFrame(new KeyFrames.Frame<Integer>(2));
        b.addFrame(new KeyFrames.Frame<Integer>(3));
        b.build();
    }

    @Test
    public void nullValue2frame() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.addFrame((KeyFrames.Frame<Integer>) null));
        b.addFrame(new KeyFrames.Frame<Integer>(3));
        b.build();
    }

    @Test
    public void nullValue3frame() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(new KeyFrames.Frame<Integer>(2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.addFrame((KeyFrames.Frame<Integer>) null));
        b.build();
    }

    @Test
    public void nonIncreasingTimeFraction1() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2, 0.5);
        b.addFrame(3, 0.1);
        b.addFrame(4);
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.build());
    }

    @Test
    public void nonIncreasingTimeFraction2() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2, 0.1);
        b.addFrame(3, 0.2);
        b.addFrame(4, 0.3);
        b.addFrame(5, 0.2);
        b.addFrame(6, 0.5);
        b.addFrame(7);
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.build());
    }

    @Test
    public void maskFirstLast1() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>();
        b.addFrame(2, 0.1); // changed to 0 automatically
        b.addFrame(3, 0.2);
        b.addFrame(4, 0.3); // changed to 1 automatically
        b.build();
    }

    @Test
    public void maskFirstLast2() {
        KeyFrames.Frame<Integer> f0 = new KeyFrames.Frame<Integer>(1, 0.1);
        KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2, 0.2);
        KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3, 0.3);
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>();
        b.addFrame(f0); // changed to 0 automatically
        b.addFrame(f1);
        b.addFrame(f2); // changed to 1 automatically
        b.build();
    }

    @Test
    public void badEvaluator() {
        final KeyFrames.Builder<String> b = new KeyFrames.Builder<String>("first");
        b.addFrame("Last");
        Assertions.assertThrows(IllegalArgumentException.class, () -> b.build());
    }

    @Test
    public void evaluator1() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(2);
        b.setEvaluator(new EvaluatorInteger());
        b.build();
    }

    private final Evaluator<String> f_stringEvaluator = new Evaluator<String>() {
        @Override
        public Class<String> getEvaluatorClass() {
            return String.class;
        }

        @Override
        public String evaluate(String v0, String v1, double fraction) {
            if (fraction < 0.5) {
                return v0;
            }
            else {
                return v1;
            }
        }
    };

    @Test
    public void evaluator2() {
        final KeyFrames.Builder<String> b = new KeyFrames.Builder<String>("first");
        b.addFrame("last");
        b.setEvaluator(f_stringEvaluator);
        b.build();
    }

    @Test
    public void evaluator3() {
        KnownEvaluators.getInstance().register(f_stringEvaluator);
        final KeyFrames.Builder<String> b = new KeyFrames.Builder<String>("first");
        b.addFrame("last");
        b.build();

        KnownEvaluators.getInstance().unregister(f_stringEvaluator);
        final KeyFrames.Builder<String> b1 = new KeyFrames.Builder<String>("first");
        b1.addFrame("last");
        try {
            b1.build();
            Assertions.fail("No evaluator for String should be available.");
        }
        catch (IllegalArgumentException success) {
            // success
        }
    }

    @Test
    public void overall1() {
        final KeyFrames.Builder<Integer> b = new KeyFrames.Builder<Integer>(1);
        b.addFrame(100);
        final KeyFrames<Integer> kf = b.build();
        Assertions.assertEquals(Integer.class, kf.getClassOfValue());
        Assertions.assertEquals(1, kf.getInterpolatedValueAt(0).intValue());
        Assertions.assertEquals(20, kf.getInterpolatedValueAt(0.2).intValue());
        Assertions.assertEquals(25, kf.getInterpolatedValueAt(0.25).intValue());
        Assertions.assertEquals(50, kf.getInterpolatedValueAt(0.5).intValue());
        Assertions.assertEquals(100, kf.getInterpolatedValueAt(1).intValue());
    }

    @Test
    public void overall2() {
        final KeyFrames.Builder<String> b = new KeyFrames.Builder<String>("first");
        b.addFrame("last");
        b.setEvaluator(f_stringEvaluator);
        final KeyFrames<String> kf = b.build();
        Assertions.assertEquals(String.class, kf.getClassOfValue());
        Assertions.assertEquals("first", kf.getInterpolatedValueAt(0));
        Assertions.assertEquals("first", kf.getInterpolatedValueAt(0.2));
        Assertions.assertEquals("last", kf.getInterpolatedValueAt(0.5));
        Assertions.assertEquals("last", kf.getInterpolatedValueAt(0.501));
        Assertions.assertEquals("last", kf.getInterpolatedValueAt(1));
    }
}
