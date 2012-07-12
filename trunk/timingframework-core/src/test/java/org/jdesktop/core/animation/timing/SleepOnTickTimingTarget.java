package org.jdesktop.core.animation.timing;

public class SleepOnTickTimingTarget extends ProtocolTimingTarget {

  @Override
  public void timingEvent(Animator source, double fraction) {
    super.timingEvent(source, fraction);
    /*
     * Mimic a poorly implemented timing target
     */
    try {
      Thread.sleep(3000);
    } catch (InterruptedException ignore) {
    }
  }
}
