package org.jdesktop.core.animation.timing;

import com.surelogic.Immutable;

/**
 * This interface provides the base interface for all trigger event
 * enumerations. Defines the ability to get the opposite event from a particular
 * trigger event.
 * 
 * @author Tim Halloran
 */
@Immutable
public interface TriggerEvent {

  /**
   * This method returns the 'opposite' event from itself. This is used by
   * {@link Trigger} in running an auto-reversing animation, to determine
   * whether an opposite event has occurred (and whether to stop/reverse the
   * animation). Note that some events may have no opposite. Default behavior
   * returns same event; subclasses with multiple/opposite events must override
   * to do the right thing here.
   */
  TriggerEvent getOppositeEvent();
}
