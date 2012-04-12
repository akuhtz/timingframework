package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
import java.util.List;

/**
 * A timing target that checks that calls are made to a series of
 * {@link #timingEvent(Animator, double)}s in the correct order. Used for
 * testing the Timing Framework.
 * <p>
 * Test codes gets the set using {@link #getSomeTargets()}. The set is reset
 * after each tick via {@link #resetCallOrder()}. Test code should iterate
 * through the set of timing targets and call {@link #invokedInOrder()} and
 * {@link #getFailureMessage()} on each item to see if it was called in the
 * proper order and if not what went wrong.
 * <p>
 * This implementation is not thread safe.
 */
public class OrderedTimingTarget extends ProtocolTimingTarget {

  private static int SOME = 10;

  /**
   * Provides the list of ordered timing targets for testing.
   */
  public static List<OrderedTimingTarget> getSomeTargets() {
    final List<OrderedTimingTarget> result = new ArrayList<OrderedTimingTarget>();
    for (int i = 0; i < SOME; i++) {
      result.add(new OrderedTimingTarget(i));
    }
    return result;
  }

  private static int f_callOrder = 0;

  /**
   * Reset after ever tick.
   */
  public static void resetCallOrder() {
    f_callOrder = 0;
  }

  private final int f_index;

  private String f_failed = null;

  private OrderedTimingTarget(int index) {
    f_index = index;
  }

  public int getIndex() {
    return f_index;
  }

  public boolean invokedInOrder() {
    return f_failed != null;
  }

  String getFailureMessage() {
    return f_failed;
  }

  @Override
  public void timingEvent(Animator source, double fraction) {
    super.timingEvent(source, fraction);

    /*
     * Reset
     */
    f_failed = null;
    /*
     * Signal a failure if we were not called in the right order.
     */
    if (f_index != f_callOrder)
      f_failed = "Was #" + f_callOrder + " target called but should have been #" + f_index + " target called";
    f_callOrder++;
  }
}
