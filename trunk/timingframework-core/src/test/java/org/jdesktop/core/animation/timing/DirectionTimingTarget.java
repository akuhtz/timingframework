package org.jdesktop.core.animation.timing;

public class DirectionTimingTarget implements TimingTarget {

  private Animator.Direction f_direction;

  private boolean f_directionOkay = true;

  private String f_directionMsg = null;

  public boolean isDirectionOkay() {
    return f_directionOkay;
  }

  public String getDirectionMsg() {
    final String msg = f_directionMsg;
    return msg != null ? msg : "none";
  }

  private boolean f_allTimingEventsOkay = true;

  public boolean allTimingEventsOkay() {
    return f_allTimingEventsOkay;
  }

  public String allTimingEventsMsg() {
    return "timingEvent(Animator, double) called with the wrong direction";
  }

  public DirectionTimingTarget(Animator.Direction expectedAtBegin) {
    f_direction = expectedAtBegin;
  }

  @Override
  public void begin(Animator source) {
    if (source.getCurrentDirection() != f_direction) {
      f_directionOkay = false;
      f_directionMsg = "begin(Animator) called with wrong direction: going " + source.getCurrentDirection() + " but " + f_direction
          + " was expected";
    }
  }

  @Override
  public void end(Animator source) {
    if (source.getCurrentDirection() != f_direction) {
      f_directionOkay = false;
      f_directionMsg = "end(Animator) called with wrong direction: going " + source.getCurrentDirection() + " but " + f_direction
          + " was expected";
    }
  }

  @Override
  public void repeat(Animator source) {
    if (source.getRepeatBehavior() == Animator.RepeatBehavior.REVERSE)
      f_direction = f_direction.getOppositeDirection();
    if (source.getCurrentDirection() != f_direction) {
      f_directionOkay = false;
      f_directionMsg = "repeat(Animator) called with wrong direction: going " + source.getCurrentDirection() + " but "
          + f_direction + " was expected";
    }
  }

  @Override
  public void reverse(Animator source) {
    f_direction = f_direction.getOppositeDirection();
    if (source.getCurrentDirection() != f_direction) {
      f_directionOkay = false;
      f_directionMsg = "reverse(Animator) called with wrong direction: going " + source.getCurrentDirection() + " but "
          + f_direction + " was expected";
    }
  }

  @Override
  public void timingEvent(Animator source, double fraction) {
    if (source.getCurrentDirection() != f_direction) {
      f_allTimingEventsOkay = false;
    }
  }
}
