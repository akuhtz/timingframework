package org.jdesktop.core.animation.timing.triggers;

/**
 * Focus In/Out events.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public enum FocusTriggerEvent implements TriggerEvent {

  /**
   * Event fired when Component receives focus.
   */
  IN {
    @Override
    public FocusTriggerEvent getOppositeEvent() {
      return OUT;
    }
  },

  /**
   * Event fired when Component loses focus.
   */
  OUT {
    @Override
    public FocusTriggerEvent getOppositeEvent() {
      return IN;
    }
  };

  @Override
  public abstract FocusTriggerEvent getOppositeEvent();
}
