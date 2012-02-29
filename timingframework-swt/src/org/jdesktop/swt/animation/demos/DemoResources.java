package org.jdesktop.swt.animation.demos;

import java.io.IOException;
import java.net.URL;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

/**
 * Manages all the resources used by demonstration programs. This avoids
 * duplication of resources if they are used by more than one demonstration
 * program.
 * 
 * @author Tim Halloran
 */
public final class DemoResources {

  public static final String BEETLE_RED = "beetle-red.gif";
  public static final String BLUE_SPHERE = "blue-sphere.png";
  public static final String DRIFT = "drift.wav";
  public static final String GRAY_SPHERE = "gray-sphere.png";
  public static final String GREEN_SPHERE = "green-sphere.png";
  public static final String RED_SPHERE = "red-sphere.png";
  public static final String TRACK = "track.jpg";
  public static final String VROOM = "vroom.wav";
  public static final String YELLOW_SPHERE = "yellow-sphere.png";

  public static final String[] SPHERES = { BLUE_SPHERE, GRAY_SPHERE, GREEN_SPHERE, RED_SPHERE, YELLOW_SPHERE };

  private static final String PREFIX = "org/jdesktop/swt/animation/demos/";

  /**
   * Gets the passed resource in the classpath.
   * 
   * @param name
   *          the resource name.
   * @return a reference to the resource that can be used to load it.
   */
  public static URL getResource(String name) {
    final URL result = Thread.currentThread().getContextClassLoader().getResource(PREFIX + name);
    if (result == null)
      throw new IllegalStateException("Unable to load resource: " + name);
    else
      return result;
  }

  /**
   * Returns an SWT image loaded from the resource name.
   * 
   * @param name
   *          the resource name.
   * @return an SWT image.
   * @throws IllegalStateException
   *           if something goes wrong.
   */
  public static Image getImage(String name, Device device) {
    try {
      final URL url = getResource(name);
      final Image image = new Image(device, url.openStream());
      return image;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load image: " + name, e);
    }
  }
}
