package org.jdesktop.swt.animation.timing.demos.ch15;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.jdesktop.swt.animation.demos.DemoResources;

/**
 * This class does the work of rendering the current view of the racetrack. It
 * holds the car position and rotation and displays the car accordingly. The
 * track itself is merely a background image that is copied the same on every
 * repaint. Note that carPosition and carRotation are both JavaBean properties,
 * which is exploited in the SetterRace and MultiStepRace variations.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class TrackView extends Canvas {

  Image car;
  Image track;
  Point carPosition;
  float carRotation = 0;
  int trackW, trackH;
  int carW, carH, carWHalf, carHHalf;

  /** Hard-coded positions of interest on the track */
  static final Point START_POS = new Point(450, 70);
  static final Point FIRST_TURN_START = new Point(130, 70);
  static final Point FIRST_TURN_END = new Point(76, 127);
  static final Point SECOND_TURN_START = new Point(76, 404);
  static final Point SECOND_TURN_END = new Point(130, 461);
  static final Point THIRD_TURN_START = new Point(450, 461);
  static final Point THIRD_TURN_END = new Point(504, 404);
  static final Point FOURTH_TURN_START = new Point(504, 127);

  public TrackView(Composite parent, int style) {
    super(parent, style);
    try {
      car = DemoResources.getImage(DemoResources.BEETLE_RED, parent.getDisplay());
      track = DemoResources.getImage(DemoResources.TRACK, parent.getDisplay());
    } catch (Exception e) {
      System.out.println("Problem loading track/car images: " + e);
    }
    carPosition = new Point(START_POS.x, START_POS.y);
    carW = car.getBounds().width;
    carH = car.getBounds().height;
    carWHalf = carW / 2;
    carHHalf = carH / 2;
    trackW = track.getBounds().width;
    trackH = track.getBounds().height;

    addPaintListener(new PaintListener() {

      @Override
      public void paintControl(PaintEvent e) {
        final GC gc = e.gc;
        paintComponent(gc);
      }
    });
  }

  @Override
  public Point computeSize(int wHint, int hHint, boolean changed) {
    final Point result = new Point(trackW, trackH);
    return result;
  }

  /**
   * Render the track and car.
   */
  public void paintComponent(GC gc) {
    gc.setAdvanced(true);

    /*
     * First draw the race track.
     */
    gc.drawImage(track, 0, 0);

    /*
     * Now draw the car. The translate/rotate/translate settings account for any
     * nonzero carRotation values.
     */
    Transform transform = new Transform(gc.getDevice());
    transform.translate(carPosition.x, carPosition.y);
    transform.rotate(carRotation);
    transform.translate(-(carPosition.x), -(carPosition.y));
    gc.setTransform(transform);
    /*
     * Now the graphics has been set up appropriately; draw the car in position
     */
    gc.drawImage(car, carPosition.x - carWHalf, carPosition.y - carHHalf);
    transform.dispose();
  }

  /**
   * Set the new position and schedule a repaint.
   */
  public void setCarPosition(Point newPosition) {
    carPosition.x = newPosition.x;
    carPosition.y = newPosition.y;
    redraw();
  }

  /**
   * Set the new rotation and schedule a repaint.
   */
  public void setCarRotation(float newDegrees) {
    carRotation = newDegrees;
    // repaint area accounts for larger rectangular are because rotate
    // car will exceed normal rectangular bounds
    redraw();
  }

  /**
   * Gets the car's rotation.
   */
  public double getCarRotation() {
    return carRotation;
  }

  /**
   * Reverses the car's rotation.
   */
  public void reverseCarRotation() {
    float result = carRotation + 180;
    if (result > 360)
      result = result - 360;
    carRotation = result;
  }
}
