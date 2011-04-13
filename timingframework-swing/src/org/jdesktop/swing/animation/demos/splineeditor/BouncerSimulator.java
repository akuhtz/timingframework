package org.jdesktop.swing.animation.demos.splineeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class BouncerSimulator extends AbstractSimulator {

  private static final Color COLOR_BACKGROUND = Color.WHITE;

  private BufferedImage image;

  public BouncerSimulator() {
    try {
      image = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource(SplineEditor.PREFIX + "item.png"));
    } catch (Exception e) {
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (!isVisible()) {
      return;
    }

    Graphics2D g2 = (Graphics2D) g;

    setupGraphics(g2);
    drawBackground(g2);
    drawItem(g2);
  }

  private void drawItem(Graphics2D g2) {
    double position = f_time;
    double xPos = position * getWidth() / 2;

    int width = getWidth() * 2 / 3;
    int x = (getWidth() - width) / 2;
    x += xPos;
    int y = getHeight() / 2;
    y -= image.getHeight() / 2;

    g2.drawImage(image, null, x, y);
  }

  private void setupGraphics(Graphics2D g2) {
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }

  private void drawBackground(Graphics2D g2) {
    g2.setColor(COLOR_BACKGROUND);
    g2.fill(g2.getClipBounds());
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(150, 100);
  }

  private static final long serialVersionUID = -3963863642907761767L;
}
