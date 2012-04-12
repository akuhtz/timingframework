package org.jdesktop.core.animation.timing;

/**
 * A timing target that checks that each type of callback is invoked in the
 * right order. Used for testing the Timing Framework.
 * <p>
 * This implementation is not thread safe.
 */
public class ProtocolTimingTarget extends TimingTargetAdapter {

  private enum State {
    NEW, RUNNING, DEAD
  };

  private State f_inState = State.NEW;

  private boolean f_protocolOkay = true;

  private String f_protocolMsg = null;

  public boolean isProtocolOkay() {
    return f_protocolOkay;
  }

  public String getProtocolMsg() {
    final String msg = f_protocolMsg;
    return msg != null ? msg : "none";
  }

  @Override
  public void begin(Animator source) {
    if (f_inState != State.NEW) {
      f_protocolOkay = false;
      f_protocolMsg = "begin(Animator) called on " + f_inState + " target (should have been called on " + State.NEW + " target)";
    }
    f_inState = State.RUNNING;
  }

  @Override
  public void end(Animator source) {
    if (f_inState != State.RUNNING) {
      f_protocolOkay = false;
      f_protocolMsg = "end(Animator) called on " + f_inState + " target (should have been called on " + State.RUNNING + " target)";
    }
    f_inState = State.DEAD;
  }

  @Override
  public void repeat(Animator source) {
    if (f_inState != State.RUNNING) {
      f_protocolOkay = false;
      f_protocolMsg = "repeat(Animator) called on " + f_inState + " target (should have been called on " + State.RUNNING
          + " target)";
    }
  }

  @Override
  public void reverse(Animator source) {
    if (f_inState != State.RUNNING) {
      f_protocolOkay = false;
      f_protocolMsg = "reverse(Animator) called on " + f_inState + " target (should have been called on " + State.RUNNING
          + " target)";
    }
  }

  @Override
  public void timingEvent(Animator source, double fraction) {
    if (f_inState != State.RUNNING) {
      f_protocolOkay = false;
      f_protocolMsg = "timingEvent(Animator,double) called on " + f_inState + " target (should have been called on "
          + State.RUNNING + " target)";
    }
  }
}
