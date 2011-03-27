package org.jdesktop.core.animation.timing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.surelogic.Immutable;

/**
 * A list of times from 0 to 1 representing the elapsed fraction of an animation
 * cycle. Instances of this class are immutable&mdash;they can be freely shared
 * and reused.
 * <p>
 * Instances are obtained using the {@link KeyTimes#build(double...)} method.
 * For example, the code below produces an instance with four time increments: 0
 * to 0.1, 0.1 to 0.2, 0.2 to 0.5, and 0.5 to 1.
 * 
 * <pre>
 * KeyTimes k = KeyTimes.build(0, 0.1, 0.2, 0.5, 1);
 * </pre>
 * 
 * In the simplest case, a {@link KeyFrames} will consist of just two times in
 * its {@link KeyTimes}: 0 and 1. The code below would return this instance.
 * 
 * <pre>
 * KeyFrames.build(0, 1);
 * </pre>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public class KeyTimes implements Iterable<Double> {

  private final List<Double> f_keyTimes;

  /**
   * Constructs a new instance of {@link KeyTimes}. Times should be in
   * increasing order and should all be in the range [0,1], with the first value
   * being zero and the last value being one.
   * 
   * @throws IllegalArgumentException
   *           Time values must be ordered in increasing value, the first value
   *           must be zero and the last value must be one.
   */
  public static KeyTimes build(double... times) {
    return new KeyTimes(times);
  }

  /**
   * Private constructor, called by the {@link #build(double...)} factory
   * method. This use makes this class consistent with how {@link KeyValues} and
   * {@link KeyFrames} are constructed.
   */
  private KeyTimes(double... times) {
    final List<Double> timesList = new ArrayList<Double>();
    if (times[0] != 0) {
      throw new IllegalArgumentException("First time value must be zero");
    }
    if (times[times.length - 1] != 1.0f) {
      throw new IllegalArgumentException("Last time value must be one");
    }
    double prevTime = 0;
    for (double time : times) {
      if (time < prevTime) {
        throw new IllegalArgumentException("Time values must be in increasing order");
      }
      timesList.add(time);
      prevTime = time;
    }
    f_keyTimes = Collections.unmodifiableList(timesList);
  }

  /**
   * Returns interval of time, 0 to {@link KeyTimes#size()} - 2, that contains
   * this time fraction. The interval returned is the index of the start key
   * time of the interval the passed fraction falls within.
   * <p>
   * The returned interval is <i>i</i> if <tt>fraction</tt> is within the range
   * (<tt>getKeyTime(</tt> <i>i</i><tt>)</tt>, <tt>getKeyTime(</tt> <i>i</i>
   * <tt>+1)</tt>] unless <i>i</i>=0 in which case <tt>fraction</tt> is within
   * the range [0, <tt>getKeyTime(</tt> <i>i</i><tt>+1)</tt>] (i.e., zero
   * inclusive).
   * <p>
   * For example, consider the following instance:
   * 
   * <pre>
   * KeyTimes k = KeyTimes.build(0, 0.1, 0.2, 0.5, 1);
   * </pre>
   * 
   * The table below shows the results that are obtained from this instance.
   * <table border="1">
   * <tr>
   * <th><i>f<i></th>
   * <th><i>i</i><tt>=k.getInterval(</tt><i>f</i><tt>)</tt></th>
   * <th><tt>k.getKeyTime(</tt><i>i</i><tt>)</tt></th>
   * <th><tt>k.getKeyTime(</tt><i>i</i><tt>+1)</tt></th>
   * </tr>
   * <tr>
   * <td align="right"><i>-1</i></td>
   * <td align="right">0</td>
   * <td align="right">0.0</td>
   * <td align="right">0.1</td>
   * </tr>
   * <tr>
   * <td align="right">0</td>
   * <td align="right">0</td>
   * <td align="right">0.0</td>
   * <td align="right">0.1</td>
   * </tr>
   * <tr>
   * <td align="right">0.1</td>
   * <td align="right">0</td>
   * <td align="right">0.0</td>
   * <td align="right">0.1</td>
   * </tr>
   * <tr>
   * <td align="right">0.11</td>
   * <td align="right">1</td>
   * <td align="right">0.1</td>
   * <td align="right">0.2</td>
   * </tr>
   * <tr>
   * <td align="right">0.2</td>
   * <td align="right">1</td>
   * <td align="right">0.1</td>
   * <td align="right">0.2</td>
   * </tr>
   * <tr>
   * <td align="right">0.34</td>
   * <td align="right">2</td>
   * <td align="right">0.2</td>
   * <td align="right">0.5</td>
   * </tr>
   * <tr>
   * <td align="right">0.5</td>
   * <td align="right">2</td>
   * <td align="right">0.2</td>
   * <td align="right">0.5</td>
   * </tr>
   * <tr>
   * <td align="right">0.6</td>
   * <td align="right">3</td>
   * <td align="right">0.5</td>
   * <td align="right">1.0</td>
   * </tr>
   * <tr>
   * <td align="right">1</td>
   * <td align="right">3</td>
   * <td align="right">0.5</td>
   * <td align="right">1.0</td>
   * </tr>
   * <tr>
   * <td align="right"><i>2</i></td>
   * <td align="right">3</td>
   * <td align="right">0.5</td>
   * <td align="right">1.0</td>
   * </tr>
   * </table>
   * 
   * The first and the last entries, -1 and 2, are outside the range [0,1],
   * however the implementation clamps them to [0,1] and returns a valid result.
   * 
   * @param fraction
   *          a time fraction in the range [0,1].
   * @return the index of the start key time of the interval the passed fraction
   *         falls within.
   */
  public int getInterval(double fraction) {
    for (int i = 1; i < f_keyTimes.size(); ++i) {
      final double keyTime = f_keyTimes.get(i);
      if (fraction <= keyTime)
        return i - 1;
    }
    return size() - 2;
  }

  /**
   * Returns the key time at the specified position in this list.
   * 
   * @param index
   *          index of the element to return
   * @return the element at the specified position in this list
   * @throws IndexOutOfBoundsException
   *           if the index is out of range (
   *           <tt>index &lt; 0 || index &gt;= size()</tt>)
   */
  public double get(int index) {
    return f_keyTimes.get(index);
  }

  /**
   * Returns the number of key times.
   * 
   * @return the number of key times.
   */
  public int size() {
    return f_keyTimes.size();
  }

  public Iterator<Double> iterator() {
    return f_keyTimes.iterator();
  }

  @Override
  public String toString() {
    return getClass().getName() + "@" + f_keyTimes.toString();
  }
}