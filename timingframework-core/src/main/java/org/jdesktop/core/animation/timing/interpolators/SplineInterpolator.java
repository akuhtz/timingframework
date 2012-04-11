package org.jdesktop.core.animation.timing.interpolators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdesktop.core.animation.timing.Interpolator;

import com.surelogic.Borrowed;
import com.surelogic.Immutable;
import com.surelogic.RegionEffects;
import com.surelogic.Unique;
import com.surelogic.Vouch;

/**
 * This class interpolates fractional values using Bezier splines to animate
 * with a variety of non-linear movement behaviors. The anchor points for the
 * spline are assumed to be (0, 0) and (1, 1). Control points should all be in
 * the range [0, 1].
 * <p>
 * For more information on how splines are used to interpolate, refer to the
 * SMIL specification at <a href="http://w3c.org">http://w3c.org</a>.
 * <p>
 * Instances of this class contain no mutable state and can be safely shared. In
 * fact, due to the non-trivial setup and memory use of this implementation,
 * instance sharing is recommended. This class is thread-safe.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
public final class SplineInterpolator implements Interpolator {

  private final double f_x1, f_y1, f_x2, f_y2;

  @Vouch(value = "Immutable", reason = "Instance wrapped via Collections.unmodifiableList(List)")
  @Unique
  private final List<LengthItem> f_lengths;

  /**
   * Creates a new instance of SplineInterpolator with the control points
   * defined by (x1,y1) and (x2,y2). The anchor points are implicitly defined as
   * (0,0) and (1,1).
   * 
   * @param x1
   *          the x value of the first control point.
   * @param y1
   *          the y value of the first control point.
   * @param x2
   *          the x value of the second control point.
   * @param y2
   *          the y value of the second control point.
   * 
   * @throws IllegalArgumentException
   *           This exception is thrown when values beyond the allowed [0,1]
   *           range are passed in.
   */
  @Unique("return")
  public SplineInterpolator(double x1, double y1, double x2, double y2) {
    if (x1 < 0 || x1 > 1 || y1 < 0 || y1 > 1.0f || x2 < 0 || x2 > 1 || y2 < 0 || y2 > 1) {
      throw new IllegalArgumentException("Control points must be in the range [0,1].");
    }

    f_x1 = x1;
    f_y1 = y1;
    f_x2 = x2;
    f_y2 = y2;

    /*
     * Now construct the array of all lengths to t in [0,1] at 0.01 increments.
     */
    final ArrayList<LengthItemBase> baseLengths = new ArrayList<LengthItemBase>();
    double prevX = 0;
    double prevY = 0;
    double cumulativeLength = 0;
    for (double t = 0; t <= 1; t += 0.01) {
      Point2D xy = getXY(t);
      double length = cumulativeLength + Math.sqrt((xy.x - prevX) * (xy.x - prevX) + (xy.y - prevY) * (xy.y - prevY));
      final LengthItemBase lengthItem = new LengthItemBase(length, t);
      baseLengths.add(lengthItem);
      cumulativeLength = length;
      prevX = xy.x;
      prevY = xy.y;
    }
    /*
     * Now calculate the fractions so that we can access the lengths array with
     * values in [0,1]. cumulativeLength now holds the total length of the
     * spline.
     */
    final ArrayList<LengthItem> resultLengths = new ArrayList<LengthItem>();
    for (LengthItemBase length : baseLengths) {
      resultLengths.add(new LengthItem(length.getLength(), length.getT(), cumulativeLength));
    }
    f_lengths = Collections.unmodifiableList(resultLengths);
  }

  /**
   * Calculates the XY point for a given <i>t</i> value.
   * 
   * The general spline equation is
   * <p>
   * x = b0*x0 + b1*x1 + b2*x2 + b3*x3 y = b0*y0 + b1*y1 + b2*y2 + b3*y3
   * <p>
   * where
   * <p>
   * b0 = (1-t)^3 b1 = 3 * t * (1-t)^2 b2 = 3 * t^2 * (1-t) b3 = t^3.
   * <p>
   * We know that (x0,y0) == (0,0) and (x1,y1) == (1,1) for our splines, so this
   * simplifies to
   * <p>
   * x = b1*x1 + b2*x2 + b3 y = b1*x1 + b2*x2 + b3
   * 
   * @param t
   *          parametric value for spline calculation.
   */
  @Borrowed("this")
  @RegionEffects("reads Instance")
  private Point2D getXY(double t) {
    final double invT = 1 - t;
    final double b1 = 3 * t * invT * invT;
    final double b2 = 3 * t * t * invT;
    final double b3 = t * t * t;
    final Point2D xy = new Point2D((b1 * f_x1) + (b2 * f_x2) + b3, (b1 * f_y1) + (b2 * f_y2) + b3);
    return xy;
  }

  /**
   * Utility function: When we are evaluating the spline, we only care about the
   * Y values. See {@link #getXY} for the details.
   */
  @Borrowed("this")
  @RegionEffects("reads Instance")
  private double getY(double t) {
    final double invT = 1 - t;
    final double b1 = 3 * t * invT * invT;
    final double b2 = 3 * t * t * invT;
    final double b3 = t * t * t;
    return (b1 * f_y1) + (b2 * f_y2) + b3;
  }

  /**
   * Given a fraction of time along the spline (which we can interpret as the
   * length along a spline), return the interpolated value of the spline.
   * <p>
   * We first calculate the <i>t</i> value for the length by doing a lookup in
   * our array of previously calculated values and then linearly interpolating
   * between the nearest values. Then we calculate the Y value for this
   * <i>t</i>.
   * 
   * @param fraction
   *          a value between 0 and 1, representing the elapsed fraction of a
   *          time interval.
   * @return an interpolated fraction between 0 and 1.
   */
  @RegionEffects("reads All")
  public double interpolate(double fraction) {
    int low = 1;
    int high = f_lengths.size() - 1;
    int mid = 0;
    while (low <= high) {
      mid = (low + high) / 2;

      if (fraction > f_lengths.get(mid).getFraction())
        low = mid + 1;
      else if (mid > 0 && fraction < f_lengths.get(mid - 1).getFraction())
        high = mid - 1;
      else {
        break;
      }
    }
    /*
     * The answer lies between the "mid" item and its predecessor.
     */
    final LengthItem prevItem = f_lengths.get(mid - 1);
    final double prevFraction = prevItem.getFraction();
    final double prevT = prevItem.getT();

    final LengthItem item = f_lengths.get(mid);
    final double proportion = (fraction - prevFraction) / (item.getFraction() - prevFraction);
    final double interpolatedT = prevT + (proportion * (item.getT() - prevT));
    return getY(interpolatedT);
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(SplineInterpolator.class.getSimpleName());
    b.append("(x1=").append(f_x1);
    b.append(", y1=").append(f_y1);
    b.append(", x1=").append(f_x2);
    b.append(", y2=").append(f_y2);
    b.append(')');
    return b.toString();
  }

  /**
   * Base class used to store information about length values. This base class
   * allows the construction of full {@link LengthItem} instances that are
   * immutable.
   * 
   * @see LengthItem
   */
  @Immutable
  private static class LengthItemBase {
    private final double f_length;
    private final double f_t;

    @RegionEffects("none")
    LengthItemBase(double length, double t) {
      f_length = length;
      f_t = t;
    }

    @RegionEffects("reads Instance")
    public double getLength() {
      return f_length;
    }

    @RegionEffects("reads Instance")
    public double getT() {
      return f_t;
    }
  }

  /**
   * Used to store information about length values. Specifically, each item
   * stores the "length" (which can be thought of as the time elapsed along the
   * spline path), the <i>t</i> value at this length (used to calculate the
   * (x,y) point along the spline), and the <i>fraction</i> which is equal to
   * the length divided by the total absolute length of the spline.
   * <p>
   * After we calculate all LengthItems for a given spline, we have a list of
   * entries which can return the <i>t</i> values for fractional lengths from 0
   * to 1.
   */
  @Immutable
  private static final class LengthItem extends LengthItemBase {
    private final double fraction;

    @RegionEffects("none")
    LengthItem(double length, double t, double totalLength) {
      super(length, t);
      fraction = length / totalLength;
    }

    @RegionEffects("reads Instance")
    public double getFraction() {
      return fraction;
    }
  }

  /**
   * Stores a simple tuple containing an X and Y value.
   */
  @Immutable
  private static final class Point2D {
    private final double x;
    private final double y;

    @RegionEffects("none")
    Point2D(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }
}
