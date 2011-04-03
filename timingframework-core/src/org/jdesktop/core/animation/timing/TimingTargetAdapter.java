package org.jdesktop.core.animation.timing;

import com.surelogic.Immutable;

/**
 * Implements the {@link TimingTarget} interface, providing stubs for all
 * TimingTarget methods. Subclasses may extend this adapter rather than
 * implementing the TimingTarget interface if they only care about a subset of
 * the events that TimingTarget provides. For example, sequencing animations may
 * only require monitoring the {@link TimingTarget#end} method, so subclasses of
 * this adapter may ignore the other methods such as timingEvent.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable(implementationOnly = true)
public class TimingTargetAdapter implements TimingTarget {

  @Override
  public void begin(Animator source) {
    // default is to do nothing
  }

  @Override
  public void end(Animator source) {
    // default is to do nothing

  }

  @Override
  public void repeat(Animator source) {
    // default is to do nothing

  }

  @Override
  public void timingEvent(Animator source, double fraction) {
    // default is to do nothing
  }
}
