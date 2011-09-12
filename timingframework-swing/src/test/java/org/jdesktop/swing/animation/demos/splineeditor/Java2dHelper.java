package org.jdesktop.swing.animation.demos.splineeditor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Java2dHelper {

  public static BufferedImage createCompatibleImage(int width, int height) {
    GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice screenDevice = environment.getDefaultScreenDevice();
    GraphicsConfiguration configuration = screenDevice.getDefaultConfiguration();
    return configuration.createCompatibleImage(width, height);
  }

  public static BufferedImage loadCompatibleImage(URL resource) throws IOException {
    BufferedImage image = ImageIO.read(resource);
    BufferedImage compatibleImage = createCompatibleImage(image.getWidth(), image.getHeight());
    Graphics g = compatibleImage.getGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
    image = null;
    return compatibleImage;
  }

  public static BufferedImage createThumbnail(BufferedImage image, int requestedThumbSize) {
    float ratio = (float) image.getWidth() / (float) image.getHeight();
    int width = image.getWidth();
    BufferedImage thumb = image;

    do {
      width /= 2;
      if (width < requestedThumbSize) {
        width = requestedThumbSize;
      }

      BufferedImage temp = new BufferedImage(width, (int) (width / ratio), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = temp.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(thumb, 0, 0, temp.getWidth(), temp.getHeight(), null);
      g2.dispose();

      thumb = temp;
    } while (width != requestedThumbSize);

    return thumb;
  }
}
