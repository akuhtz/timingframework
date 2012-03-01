package org.jdesktop.swing.animation.demos;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.jdesktop.core.animation.demos.DemoResources;


/**
 * This class does the work of rendering the current view of the racetrack. It
 * holds the car position and rotation and displays the car accordingly. The
 * track itself is merely a background image that is copied the same on every
 * repaint. Note that carPosition and carRotation are both JavaBean properties,
 * which is exploited in the SetterRace and MultiStepRace variations.
 * 
 * @author Chet Haase
 */
public class RaceTrackView extends JComponent {

  private BufferedImage car;
  private BufferedImage track;
  private Point carPosition;
  private double carRotation = 0;
  private boolean carReverse = false;
  private int trackW, trackH;
  private int carW, carH, carWHalf, carHHalf;

  /*
   * Hard-coded positions of interest on the track
   */

  static final Point START_POS = new Point(450, 70);
  static final Point FIRST_TURN_START = new Point(130, 70);
  static final Point FIRST_TURN_END = new Point(76, 127);
  static final Point SECOND_TURN_START = new Point(76, 404);
  static final Point SECOND_TURN_END = new Point(130, 461);
  static final Point THIRD_TURN_START = new Point(450, 461);
  static final Point THIRD_TURN_END = new Point(504, 404);
  static final Point FOURTH_TURN_START = new Point(504, 127);

  public RaceTrackView() {
    try {
      car = ImageIO.read(DemoResources.getResource(DemoResources.BEETLE_RED));
      track = ImageIO.read(DemoResources.getResource(DemoResources.TRACK));
    } catch (Exception e) {
      System.out.println("Problem loading track/car images: " + e);
    }
    carPosition = new Point(START_POS.x, START_POS.y);
    carW = car.getWidth();
    carH = car.getHeight();
    carWHalf = carW / 2;
    carHHalf = carH / 2;
    trackW = track.getWidth();
    trackH = track.getHeight();
  }

  public Dimension getPreferredSize() {
    return new Dimension(trackW, trackH);
  }

  public void paintComponent(Graphics g) {
    /*
     * First draw the race track.
     */
    g.drawImage(track, 0, 0, null);

    /*
     * Now draw the car. The translate/rotate/translate settings account for any
     * nonzero carRotation values.
     */
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.translate(carPosition.x, carPosition.y);
    final double drawCarRotation;
    if (carReverse) {
      double result = carRotation + 180;
      if (result > 360)
        result = result - 360;
      drawCarRotation = result;
    } else {
      drawCarRotation = carRotation;
    }
    g2d.rotate(Math.toRadians(drawCarRotation));
    g2d.translate(-(carPosition.x), -(carPosition.y));

    /*
     * Now the graphics has been set up appropriately; draw the car in position
     */
    g2d.drawImage(car, carPosition.x - carWHalf, carPosition.y - carHHalf, null);
  }

  public void setCarPosition(Point newPosition) {
    repaint(0, carPosition.x - carWHalf, carPosition.y - carHHalf, carW, carH);
    carPosition.x = newPosition.x;
    carPosition.y = newPosition.y;
    repaint(0, carPosition.x - carWHalf, carPosition.y - carHHalf, carW, carH);
  }

  public void setCarRotation(double newDegrees) {
    carRotation = newDegrees;
    // repaint area accounts for larger rectangular are because rotate
    // car will exceed normal rectangular bounds
    repaint(0, carPosition.x - carW, carPosition.y - carH, 2 * carW, 2 * carH);
  }

  public double getCarRotation() {
    return carRotation;
  }

  public void setCarReverse(boolean value) {
    carReverse = value;
  }

  public void toggleCarReverse() {
    carReverse = !carReverse;
  }

  public boolean getCarReverse() {
    return carReverse;
  }

  private static final long serialVersionUID = -7514622314816386572L;
}
