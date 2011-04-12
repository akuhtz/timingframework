package org.jdesktop.core.animation.timing;

/**
 * A timing target that counts the number of times each type of callback is
 * invoked. Used for testing the Timing Framework.
 */
public class CountingTimingTarget implements TimingTarget {

  private int f_beginCount = 0;
  private int f_endCount = 0;
  private int f_reverseCount = 0;
  private int f_repeatCount = 0;
  private int f_timingEventCount = 0;

  @Override
  public void begin(Animator source) {
    f_beginCount++;
  }

  public int getBeginCount() {
    return f_beginCount;
  }

  @Override
  public void end(Animator source) {
    f_endCount++;
  }

  public int getEndCount() {
    return f_endCount;
  }

  @Override
  public void repeat(Animator source) {
    f_repeatCount++;
  }

  public int getRepeatCount() {
    return f_repeatCount;
  }

  @Override
  public void reverse(Animator source) {
    f_reverseCount++;
  }

  public int getReverseCount() {
    return f_reverseCount;
  }

  @Override
  public void timingEvent(Animator source, double fraction) {
    f_timingEventCount++;
  }

  public int getTimingEventCount() {
    return f_timingEventCount;
  }
}
