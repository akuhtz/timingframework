package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
import java.util.List;

import org.jdesktop.core.animation.timing.evaluators.EvaluatorInteger;
import org.jdesktop.core.animation.timing.evaluators.KnownEvaluators;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.interpolators.DiscreteInterpolator;
import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.junit.Assert;
import org.junit.Test;

public final class TestKeyFrames {

  @Test
  public void builder1() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2);
    final KeyFrames<Integer> kf = b.build();
    Assert.assertEquals(2, kf.size());
    int value = 1;
    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    for (int index = 0; index < kf.size(); index++) {
      KeyFrames.Frame<Integer> f = kf.getFrame(index);
      if (value == 1)
        Assert.assertNull(f.getInterpolator());
      else
        Assert.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
      Assert.assertEquals(value++, f.getValue().intValue());
      Assert.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
      timeFraction += fractionPerFrame;
    }
    Assert.assertEquals(3, value);
  }

  @Test
  public void builder1iterator() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2);
    final KeyFrames<Integer> kf = b.build();
    Assert.assertEquals(2, kf.size());
    int value = 1;
    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    for (KeyFrames.Frame<Integer> f : kf) {
      if (value == 1)
        Assert.assertNull(f.getInterpolator());
      else
        Assert.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
      Assert.assertEquals(value++, f.getValue().intValue());
      Assert.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
      timeFraction += fractionPerFrame;
    }
    Assert.assertEquals(3, value);
  }

  @Test
  public void builder2() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2);
    b.addFrame(3);
    final KeyFrames<Integer> kf = b.build();
    Assert.assertEquals(3, kf.size());
    int value = 1;
    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    for (int index = 0; index < kf.size(); index++) {
      KeyFrames.Frame<Integer> f = kf.getFrame(index);
      if (value == 1)
        Assert.assertNull(f.getInterpolator());
      else
        Assert.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
      Assert.assertEquals(value++, f.getValue().intValue());
      Assert.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
      timeFraction += fractionPerFrame;
    }
    Assert.assertEquals(4, value);
  }

  @Test
  public void builder2iterator() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2);
    b.addFrame(3);
    final KeyFrames<Integer> kf = b.build();
    Assert.assertEquals(3, kf.size());
    int value = 1;
    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    for (KeyFrames.Frame<Integer> f : kf) {
      if (value == 1)
        Assert.assertNull(f.getInterpolator());
      else
        Assert.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
      Assert.assertEquals(value++, f.getValue().intValue());
      Assert.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
      timeFraction += fractionPerFrame;
    }
    Assert.assertEquals(4, value);
  }

  @Test
  public void builder3() {
    final int size = 10000; // a large (unlikely) number of frames
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    for (int i = 2; i <= size; i++) {
      b.addFrame(i);
    }
    final KeyFrames<Integer> kf = b.build();
    Assert.assertEquals(size, kf.size());
    int value = 1;
    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    for (int index = 0; index < kf.size(); index++) {
      KeyFrames.Frame<Integer> f = kf.getFrame(index);
      if (value == 1)
        Assert.assertNull(f.getInterpolator());
      else
        Assert.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
      Assert.assertEquals(value++, f.getValue().intValue());
      Assert.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
      timeFraction += fractionPerFrame;
    }
    Assert.assertEquals(size + 1, value);
  }

  @Test
  public void builder3iterator() {
    final int size = 10000; // a large (unlikely) number of frames
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    for (int i = 2; i <= size; i++) {
      b.addFrame(i);
    }
    final KeyFrames<Integer> kf = b.build();
    Assert.assertEquals(size, kf.size());
    int value = 1;
    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    for (KeyFrames.Frame<Integer> f : kf) {
      if (value == 1)
        Assert.assertNull(f.getInterpolator());
      else
        Assert.assertSame(LinearInterpolator.getInstance(), f.getInterpolator());
      Assert.assertEquals(value++, f.getValue().intValue());
      Assert.assertEquals(timeFraction, f.getTimeFraction(), 1e-9);
      timeFraction += fractionPerFrame;
    }
    Assert.assertEquals(size + 1, value);
  }

  @Test
  public void interpolator1() {
    final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2);
    b.addFrame(3);
    b.setInterpolator(i);
    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(i, kf.getFrame(1).getInterpolator());
    Assert.assertSame(i, kf.getFrame(2).getInterpolator());
  }

  @Test
  public void interpolator2() {
    final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
    final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2, DiscreteInterpolator.getInstance());
    b.addFrame(3, a);
    b.setInterpolator(i);
    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(i, kf.getFrame(1).getInterpolator());
    Assert.assertSame(i, kf.getFrame(2).getInterpolator());
  }

  @Test
  public void interpolator3() {
    final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
    final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.setInterpolator(i);
    b.addFrame(2, DiscreteInterpolator.getInstance());
    b.addFrame(3, a);
    b.setInterpolator(null); // clear
    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(a, kf.getFrame(2).getInterpolator());
  }

  @Test
  public void interpolator4() {
    final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
    final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2, DiscreteInterpolator.getInstance());
    b.addFrame(3, a);
    b.addFrame(4, i);
    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(3).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(a, kf.getFrame(2).getInterpolator());
    Assert.assertSame(i, kf.getFrame(3).getInterpolator());
  }

  @Test
  public void timeFraction() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2, 0.3);
    b.addFrame(3, 0.4);
    b.addFrame(4, 1);
    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    Assert.assertEquals(0.0, kf.getFrame(0).getTimeFraction(), 1e-9);
    Assert.assertEquals(0.3, kf.getFrame(1).getTimeFraction(), 1e-9);
    Assert.assertEquals(0.4, kf.getFrame(2).getTimeFraction(), 1e-9);
    Assert.assertEquals(1.0, kf.getFrame(3).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void allThree() {
    final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
    final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2, 0.3, i);
    b.addFrame(3, 0.4, a);
    b.addFrame(4, 1, DiscreteInterpolator.getInstance());
    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    Assert.assertEquals(0.0, kf.getFrame(0).getTimeFraction(), 1e-9);
    Assert.assertEquals(0.3, kf.getFrame(1).getTimeFraction(), 1e-9);
    Assert.assertEquals(0.4, kf.getFrame(2).getTimeFraction(), 1e-9);
    Assert.assertEquals(1.0, kf.getFrame(3).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(i, kf.getFrame(1).getInterpolator());
    Assert.assertSame(a, kf.getFrame(2).getInterpolator());
    Assert.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void frame1() {
    KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2);
    KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3);
    KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4);
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(f1);
    b.addFrame(f2);
    b.addFrame(f3);

    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void frame2() {
    KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2, 0.3);
    KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3, 0.4);
    KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4, 1);
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(f1);
    b.addFrame(f2);
    b.addFrame(f3);

    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    Assert.assertEquals(0.0, kf.getFrame(0).getTimeFraction(), 1e-9);
    Assert.assertEquals(0.3, kf.getFrame(1).getTimeFraction(), 1e-9);
    Assert.assertEquals(0.4, kf.getFrame(2).getTimeFraction(), 1e-9);
    Assert.assertEquals(1.0, kf.getFrame(3).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void frame3() {
    final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
    final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
    KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2, i);
    KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3, a);
    KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4, DiscreteInterpolator.getInstance());
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(f1);
    b.addFrame(f2);
    b.addFrame(f3);

    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(i, kf.getFrame(1).getInterpolator());
    Assert.assertSame(a, kf.getFrame(2).getInterpolator());
    Assert.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void frame4() {
    final Interpolator i = new SplineInterpolator(0, 1, 0, 1);
    final Interpolator a = new AccelerationInterpolator(0.1, 0.1);
    KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2, 0.3, i);
    KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3, 0.4, a);
    KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4, 1, DiscreteInterpolator.getInstance());
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(f1);
    b.addFrame(f2);
    b.addFrame(f3);

    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    Assert.assertEquals(0.0, kf.getFrame(0).getTimeFraction(), 1e-9);
    Assert.assertEquals(0.3, kf.getFrame(1).getTimeFraction(), 1e-9);
    Assert.assertEquals(0.4, kf.getFrame(2).getTimeFraction(), 1e-9);
    Assert.assertEquals(1.0, kf.getFrame(3).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(i, kf.getFrame(1).getInterpolator());
    Assert.assertSame(a, kf.getFrame(2).getInterpolator());
    Assert.assertSame(DiscreteInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void frame5() {
    KeyFrames.Frame<Integer> f1 = new KeyFrames.Frame<Integer>(2, -1, null);
    KeyFrames.Frame<Integer> f2 = new KeyFrames.Frame<Integer>(3, -1, null);
    KeyFrames.Frame<Integer> f3 = new KeyFrames.Frame<Integer>(4, -1, null);
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(f1);
    b.addFrame(f2);
    b.addFrame(f3);

    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void addFrames1() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrames(2, 3, 4);

    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void addFrames2() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>();
    b.addFrames(1, 2, 3, 4);

    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void addFrames3() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    List<Integer> l = new ArrayList<Integer>();
    l.add(2);
    l.add(3);
    l.add(4);
    b.addFrames(l);

    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test
  public void addFrames4() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>();
    List<Integer> l = new ArrayList<Integer>();
    l.add(1);
    l.add(2);
    l.add(3);
    l.add(4);
    b.addFrames(l);

    final KeyFrames<Integer> kf = b.build();

    Assert.assertEquals(1, kf.getFrame(0).getValue().intValue());
    Assert.assertEquals(2, kf.getFrame(1).getValue().intValue());
    Assert.assertEquals(3, kf.getFrame(2).getValue().intValue());
    Assert.assertEquals(4, kf.getFrame(3).getValue().intValue());

    double timeFraction = 0;
    final double fractionPerFrame = 1.0 / (kf.size() - 1);
    Assert.assertEquals(timeFraction, kf.getFrame(0).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(1).getTimeFraction(), 1e-9);
    timeFraction += fractionPerFrame;
    Assert.assertEquals(timeFraction, kf.getFrame(2).getTimeFraction(), 1e-9);

    Assert.assertNull(kf.getFrame(0).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(1).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(2).getInterpolator());
    Assert.assertSame(LinearInterpolator.getInstance(), kf.getFrame(3).getInterpolator());
  }

  @Test(expected = IllegalArgumentException.class)
  public void zeroFrame() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>();
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void oneFrame() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullValue1() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(null);
    b.addFrame(2);
    b.addFrame(3);
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullValue2() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame((Integer) null);
    b.addFrame(3);
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullValue3() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2);
    b.addFrame((Integer) null);
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullValue1frame() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>();
    b.addFrame((KeyFrames.Frame<Integer>) null);
    b.addFrame(new KeyFrames.Frame<Integer>(2));
    b.addFrame(new KeyFrames.Frame<Integer>(3));
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullValue2frame() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame((KeyFrames.Frame<Integer>) null);
    b.addFrame(new KeyFrames.Frame<Integer>(3));
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullValue3frame() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(new KeyFrames.Frame<Integer>(2));
    b.addFrame((KeyFrames.Frame<Integer>) null);
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonIncreasingTimeFraction1() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2, 0.5);
    b.addFrame(3, 0.1);
    b.addFrame(4);
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonIncreasingTimeFraction2() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2, 0.1);
    b.addFrame(3, 0.2);
    b.addFrame(4, 0.3);
    b.addFrame(5, 0.2);
    b.addFrame(6, 0.5);
    b.addFrame(7);
    b.build();
  }

  @Test
  public void maskFirstLast1() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>();
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
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>();
    b.addFrame(f0); // changed to 0 automatically
    b.addFrame(f1);
    b.addFrame(f2); // changed to 1 automatically
    b.build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void badEvaluator() {
    final KeyFramesBuilder<String> b = new KeyFramesBuilder<String>("first");
    b.addFrame("Last");
    b.build();
  }

  @Test
  public void evaluator1() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(2);
    b.setEvaluator(new EvaluatorInteger());
    b.build();
  }

  private final Evaluator<String> f_stringEvaluator = new Evaluator<String>() {
    public Class<String> getEvaluatorClass() {
      return String.class;
    }

    public String evaluate(String v0, String v1, double fraction) {
      if (fraction < 0.5)
        return v0;
      else
        return v1;
    }
  };

  @Test
  public void evaluator2() {
    final KeyFramesBuilder<String> b = new KeyFramesBuilder<String>("first");
    b.addFrame("Last");
    b.setEvaluator(f_stringEvaluator);
    b.build();
  }

  @Test
  public void evaluator3() {
    KnownEvaluators.getInstance().register(f_stringEvaluator);
    final KeyFramesBuilder<String> b = new KeyFramesBuilder<String>("first");
    b.addFrame("Last");
    b.build();

    KnownEvaluators.getInstance().unregister(f_stringEvaluator);
    final KeyFramesBuilder<String> b1 = new KeyFramesBuilder<String>("first");
    b1.addFrame("Last");
    try {
      b1.build();
      Assert.fail("No evaluator for String should be available.");
    } catch (IllegalArgumentException e) {
      // success
    }
  }

  @Test
  public void overall() {
    final KeyFramesBuilder<Integer> b = new KeyFramesBuilder<Integer>(1);
    b.addFrame(100);
    final KeyFrames<Integer> kf = b.build();
    Assert.assertEquals(1, kf.getEvaluatedValueAt(0).intValue());
    Assert.assertEquals(20, kf.getEvaluatedValueAt(0.2).intValue());
    Assert.assertEquals(25, kf.getEvaluatedValueAt(0.25).intValue());
    Assert.assertEquals(50, kf.getEvaluatedValueAt(0.5).intValue());
    Assert.assertEquals(100, kf.getEvaluatedValueAt(1).intValue());
  }
}
