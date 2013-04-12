package org.jdesktop.core.animation.rendering;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;

import com.surelogic.NotThreadSafe;

/**
 * Manages the location of a viewport into a larger image. The viewport may be
 * resized, moved directly, or moved with an animation.
 * <p>
 * The size of the image the viewport is into is not allowed to be changed. If
 * the image size changes a new viewport must be constructed.
 * 
 * @author Tim Halloran
 */
@NotThreadSafe
public final class Viewport {

  /**
   * Directions about how to animate the viewport.
   */
  public static enum Go {
    LEFT, RIGHT, UP, DOWN, STOP
  }

  /**
   * A source for the rate of animated viewport movement.
   */
  public interface MovementRateSource {

    /**
     * Gets the rate of animated viewport movement.
     * 
     * @return the rate of animated viewport movement in nanoseconds per pixel.
     */
    long getMovementRateNanosPerPixel();
  }

  /**
   * A default source to obtain the rate of animated viewport movement in
   * nanoseconds per pixels.
   */
  static final class DefaultMovementRateSource implements MovementRateSource {

    /**
     * the rate of animated viewport movement in nanoseconds per pixel.
     */
    private final long f_nanosPerPixel;

    /**
     * Constructs an instance with the passed rate.
     * 
     * @param nanosPerPixel
     *          the rate of animated viewport movement in nanoseconds per pixel.
     */
    public DefaultMovementRateSource(long nanosPerPixel) {
      f_nanosPerPixel = nanosPerPixel;
    }

    /**
     * Constructs an instance with a rate of 700 microseconds per pixel.
     */
    public DefaultMovementRateSource() {
      this(MICROSECONDS.toNanos(700));
    }

    public long getMovementRateNanosPerPixel() {
      return f_nanosPerPixel;
    }
  }

  /**
   * Width of the overall image being viewed into.
   */
  final int f_intoWidth;

  /**
   * Height of the overall image being viewed into.
   */
  final int f_intoHeight;

  /**
   * X coordinate of the upper-left of this viewport.
   */
  int f_x = 0;

  /**
   * Y coordinate of the upper-left of this viewport.
   */
  int f_y = 0;

  /**
   * Width of this viewport.
   */
  int f_width;

  /**
   * Height of this viewport.
   */
  int f_height;

  /**
   * An interpolator to provide a slight acceleration when animated movement is
   * started.
   */
  final Interpolator f_moveI = new AccelerationInterpolator(0.2, 0);

  /**
   * The source to obtain the rate of animated viewport movement in nanoseconds
   * per pixel.
   */
  MovementRateSource f_rate = new DefaultMovementRateSource();

  /**
   * An animation to move the viewport horizontally.
   */
  Animator f_moveHorizontalAnimator = null;

  /**
   * An animation to move the viewport vertically.
   */
  Animator f_moveVerticalAnimator = null;

  public Viewport(int intoWidth, int intoHeight) {
    f_intoWidth = intoWidth;
    f_intoHeight = intoHeight;
  }

  /**
   * A timing target for horizontal and vertical animated movement.
   */
  class Mover extends TimingTargetAdapter {

    final int f_pixelsToMove;
    final boolean f_horizontal;
    final int f_startViewXY;
    final boolean f_add;

    Mover(int pixelsToMove, boolean horizontal, int startViewXY, boolean add) {
      f_pixelsToMove = pixelsToMove;
      f_horizontal = horizontal;
      f_startViewXY = startViewXY;
      f_add = add;
    }

    @Override
    public void timingEvent(Animator source, double fraction) {
      final int pixelDelta = (int) Math.round(f_pixelsToMove * fraction);
      int proposedViewXY = f_horizontal ? f_x : f_y;
      if (f_add)
        proposedViewXY = f_startViewXY + pixelDelta;
      else
        proposedViewXY = f_startViewXY - pixelDelta;
      if (f_horizontal) {
        if (proposedViewXY != f_x) {
          f_x = proposedViewXY;
        }
      } else {
        if (proposedViewXY != f_y) {
          f_y = proposedViewXY;
        }
      }
    }
  }

  /**
   * Starts or stops animated horizontal movement of this viewport. The method
   * sets up the animation and returns immediately.
   * 
   * @param toward
   *          the direction of the animation, either {@link Go#LEFT} or
   *          {@link Go#RIGHT}. A value of {@link Go#STOP} stops animated
   *          horizontal movement, if any is in progress.
   * @throws IllegalArgumentException
   *           if toward is not one of {@link Go#LEFT}, {@link Go#RIGHT}, or
   *           {@link Go#STOP}.
   * @throws IllegalStateException
   *           if the viewport width is less than one.
   */
  public void animateHorizontalMovement(Go toward) {
    if (toward == Go.STOP) {
      if (f_moveHorizontalAnimator != null) {
        f_moveHorizontalAnimator.stop();
        f_moveHorizontalAnimator = null;
      }
      return;
    }
    if (!(toward == Go.LEFT || toward == Go.RIGHT)) {
      throw new IllegalArgumentException(String.format("Animated horizontal viewport movement not allowed toward %s.", toward));
    }
    if (f_width < 1) {
      throw new IllegalStateException("Viewport width has not been set.");
    }

    int pixelsToMove = 0;
    if (toward == Go.LEFT) {
      pixelsToMove = f_x;
    } else if (toward == Go.RIGHT) {
      pixelsToMove = f_intoWidth - f_x - f_width;
    } else
      throw new IllegalStateException(I18N.err(3, "Horizontal direction of LEFT or RIGHT is " + toward));

    if (pixelsToMove > 0) {
      f_moveHorizontalAnimator = new Animator.Builder()
          .setDuration(f_rate.getMovementRateNanosPerPixel() * pixelsToMove, NANOSECONDS).setInterpolator(f_moveI)
          .addTarget(new Mover(pixelsToMove, true, f_x, toward == Go.RIGHT)).build();
      f_moveHorizontalAnimator.start();
    }
  }

  /**
   * Starts or stops animated vertical movement of this viewport. The method
   * sets up the animation and returns immediately.
   * 
   * @param toward
   *          the direction of the animation, either {@link Go#UP} or
   *          {@link Go#DOWN}. A value of {@link Go#STOP} stops animated
   *          horizontal movement, if any is in progress.
   * @throws IllegalArgumentException
   *           if toward is not one of {@link Go#UP}, {@link Go#DOWN}, or
   *           {@link Go#STOP}.
   * @throws IllegalStateException
   *           if the viewport height is less than one.
   */
  public void animateVerticalMovement(Go toward) {
    if (toward == Go.STOP) {
      if (f_moveVerticalAnimator != null) {
        f_moveVerticalAnimator.stop();
        f_moveVerticalAnimator = null;
      }
      return;
    }
    if (!(toward == Go.UP || toward == Go.DOWN)) {
      throw new IllegalArgumentException(String.format("Animated vertical viewport movement not allowed toward %s.", toward));
    }
    if (f_height < 1) {
      throw new IllegalStateException("Viewport height has not been set.");
    }

    int pixelsToMove = 0;
    if (toward == Go.UP) {
      pixelsToMove = f_y;
    } else if (toward == Go.DOWN) {
      pixelsToMove = f_intoHeight - f_y - f_height;
    } else
      throw new IllegalStateException(I18N.err(3, "Vertical direction of UP or DOWN is " + toward));

    if (pixelsToMove > 0) {
      f_moveVerticalAnimator = new Animator.Builder()
          .setDuration(f_rate.getMovementRateNanosPerPixel() * pixelsToMove, NANOSECONDS).setInterpolator(f_moveI)
          .addTarget(new Mover(pixelsToMove, false, f_y, toward == Go.DOWN)).build();
      f_moveVerticalAnimator.start();
    }
  }

  /**
   * Moves the viewport position relative to its current position. If either of
   * the two movements will move the viewport outside the bounds of the image
   * being viewed it is adjusted to stay within the bounds.
   * 
   * @param deltaX
   *          the number of pixels to move the viewport horizontally. A negative
   *          value moves the viewport to the left. A positive value moves the
   *          viewport to the right. A value of zero leaves the horizontal
   *          position of the viewport unchanged.
   * @param deltaY
   *          the number of pixels to move the viewport vertically. A negative
   *          value moves the viewport up. A positive value moves the viewport
   *          down. A value of zero leaves the vertical position of the viewport
   *          unchanged.
   * @throws IllegalStateException
   *           if either the viewport width or height is less than one.
   */
  public void setPositionDelta(int deltaX, int deltaY) {
    if (f_width < 1) {
      throw new IllegalStateException("Viewport width has not been set.");
    }
    if (f_height < 1) {
      throw new IllegalStateException("Viewport height has not been set.");
    }
    int proposedViewX = f_x + deltaX;
    setX(proposedViewX);

    int proposedViewY = f_y + deltaY;
    setY(proposedViewY);
  }

  /**
   * Sets the coordinate of the upper-left of this viewport.
   * 
   * @param x
   *          the X coordinate of the upper-left of this viewport in pixels.
   * @param y
   *          the Y coordinate of the upper-left of this viewport in pixels.
   * 
   * @throws IllegalStateException
   *           if either the viewport width or height is less than one.
   */
  public void setPosition(int x, int y) {
    setX(x);
    setY(y);
  }

  /**
   * Gets the X coordinate of the upper-left of this viewport.
   * 
   * @return the X coordinate of the upper-left of this viewport in pixels.
   */
  public int getX() {
    return f_x;
  }

  /**
   * Sets the X coordinate of the upper-left of this viewport.
   * 
   * @param value
   *          the X coordinate of the upper-left of this viewport in pixels.
   * @throws IllegalStateException
   *           if the viewport width is less than one.
   */
  public void setX(int value) {
    if (f_width < 1) {
      throw new IllegalStateException("Viewport width has not been set.");
    }
    if (value < 0)
      value = 0;
    if (value > f_intoWidth - f_width)
      value = f_intoWidth - f_width;
    if (value != f_x)
      f_x = value;
  }

  /**
   * Gets the Y coordinate of the upper-left of this viewport.
   * 
   * @return the Y coordinate of the upper-left of this viewport in pixels.
   */
  public int getY() {
    return f_y;
  }

  /**
   * Sets the Y coordinate of the upper-left of this viewport.
   * 
   * @param value
   *          the Y coordinate of the upper-left of this viewport in pixels.
   * @throws IllegalStateException
   *           if the viewport height is less than one.
   */
  public void setY(int value) {
    if (f_height < 1) {
      throw new IllegalStateException("Viewport height has not been set.");
    }
    if (value < 0)
      value = 0;
    if (value > f_intoHeight - f_height)
      value = f_intoHeight - f_height;
    if (value != f_x)
      f_y = value;
  }

  /**
   * Sets the size of this viewport.
   * 
   * @param width
   *          the width of this viewport in pixels.
   * @param height
   *          the height of this viewport in pixels.
   */
  public void setSize(int width, int height) {
    setWidth(width);
    setHeight(height);
  }

  /**
   * Gets the width of this viewport.
   * 
   * @return the width of this viewport in pixels.
   */
  public int getWidth() {
    return f_width;
  }

  /**
   * Sets the width of this viewport.
   * 
   * @param value
   *          the width of this viewport in pixels.
   */
  public void setWidth(int value) {
    if (f_width != value)
      f_width = value;
  }

  /**
   * Gets the height of this viewport.
   * 
   * @return the height of this viewport in pixels.
   */
  public int getHeight() {
    return f_height;
  }

  /**
   * Sets the height of this viewport.
   * 
   * @param value
   *          the height of this viewport in pixels.
   */
  public void setHeight(int value) {
    if (f_height != value)
      f_height = value;
  }

  /**
   * Sets the rate of animated viewport movement. A value less than one resets
   * the movement rate to its default value.
   * 
   * @param nanosPerPixel
   *          the rate of animated viewport movement in nanoseconds per pixel or
   *          value less than one to reset the movement rate to its default
   *          value.
   */
  public void setMovementRate(final long nanosPerPixel) {
    if (nanosPerPixel < 1) {
      f_rate = new DefaultMovementRateSource();
    } else {
      f_rate = new DefaultMovementRateSource(nanosPerPixel);
    }
  }

  /**
   * Sets a source for the rate of animated viewport movement. A value of
   * {@code null} resets the movement rate to its default value.
   * 
   * @param source
   *          a source for the rate of animated viewport movement or
   *          {@code null} to reset the movement rate to its default value.
   */
  public void setMovementRateSource(MovementRateSource source) {
    if (source == null)
      f_rate = new DefaultMovementRateSource();
    else
      f_rate = source;
  }

  /**
   * Gets the rate of animated viewport movement.
   * 
   * @return the rate of animated viewport movement in nanoseconds per pixel.
   */
  public long getMovementRate() {
    return f_rate.getMovementRateNanosPerPixel();
  }

  /**
   * Disposes this viewport by ensuring that any running animations are stopped.
   */
  public void dispose() {
    if (f_moveHorizontalAnimator != null)
      f_moveHorizontalAnimator.stop();
    if (f_moveVerticalAnimator != null)
      f_moveVerticalAnimator.stop();
  }
}
