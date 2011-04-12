package org.jdesktop.core.animation.timing.triggers;

/**
 * Animation start/stop/repeat/reverse events.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public enum TimingTriggerEvent implements TriggerEvent {

  /**
   * Event fired when an animation starts.
   */
  START {
    @Override
    public TimingTriggerEvent getOppositeEvent() {
      return STOP;
    }
  },

  /**
   * Event fired when an animation stops.
   */
  STOP {
    @Override
    public TimingTriggerEvent getOppositeEvent() {
      return START;
    }
  },

  /**
   * Event fired when an animation finishes one cycle and starts another.
   */
  REPEAT,

  /**
   * Event fired with an animation is reversed mid-cycle.
   */
  REVERSE;

  @Override
  public TimingTriggerEvent getOppositeEvent() {
    return this;
  };
}
