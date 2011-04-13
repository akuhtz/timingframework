package org.jdesktop.core.animation.timing.triggers;

import org.jdesktop.core.animation.timing.TriggerEvent;

import com.surelogic.Immutable;

/**
 * Mouse Enter/Exit/Press/Release/Click events.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public enum MouseTriggerEvent implements TriggerEvent {

  /**
   * Event fired when mouse enters.
   */
  ENTER {
    @Override
    public MouseTriggerEvent getOppositeEvent() {
      return EXIT;
    }
  },

  /**
   * Event fired when mouse exits.
   */
  EXIT {
    @Override
    public MouseTriggerEvent getOppositeEvent() {
      return ENTER;
    }
  },

  /**
   * Event fired when mouse button is pressed.
   */
  PRESS {
    @Override
    public MouseTriggerEvent getOppositeEvent() {
      return RELEASE;
    }
  },

  /**
   * Event fired when mouse button is released.
   */
  RELEASE {
    @Override
    public MouseTriggerEvent getOppositeEvent() {
      return PRESS;
    }
  },

  /**
   * Event fired when mouse is clicked.
   */
  CLICK;

  @Override
  public MouseTriggerEvent getOppositeEvent() {
    return this;
  };
}
