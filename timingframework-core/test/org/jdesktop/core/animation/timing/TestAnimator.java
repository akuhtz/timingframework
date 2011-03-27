package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.timing.sources.ManualTimingSource;
import org.junit.Assert;
import org.junit.Test;

public class TestAnimator {

  @Test(expected = IllegalArgumentException.class)
  public void noTimingSource1() {
    new AnimatorBuilder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void noTimingSource2() {
    AnimatorBuilder.setDefaultTimingSource(new ManualTimingSource());
    try {
      new AnimatorBuilder().build();
    } catch (IllegalArgumentException e) {
      Assert.fail("An Animator built with a non-null default TimingSource should be okay.");
    }
    AnimatorBuilder.setDefaultTimingSource(null);
    new AnimatorBuilder().build();
  }

  @Test(expected = IllegalStateException.class)
  public void start1() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.start();
    a.start();
  }

  @Test(expected = IllegalStateException.class)
  public void start2() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.startReverse();
    a.start();
  }

  @Test(expected = IllegalStateException.class)
  public void startReverse1() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.startReverse();
    a.startReverse();
  }

  @Test(expected = IllegalStateException.class)
  public void startReverse2() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.start();
    a.startReverse();
  }

  @Test(expected = IllegalStateException.class)
  public void reverseNow() {
    Animator a = new AnimatorBuilder(new ManualTimingSource()).build();
    a.reverseNow();
  }
}
