package org.jdesktop.swt.animation.demos;

import java.io.IOException;
import java.net.URL;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.jdesktop.core.animation.demos.DemoResources;

public final class DemoImages {

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
      final URL url = DemoResources.getResource(name);
      final Image image = new Image(device, url.openStream());
      return image;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load image: " + name, e);
    }
  }
}
