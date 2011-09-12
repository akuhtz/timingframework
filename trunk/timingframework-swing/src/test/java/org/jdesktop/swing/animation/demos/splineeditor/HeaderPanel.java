package org.jdesktop.swing.animation.demos.splineeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

class HeaderPanel extends JPanel {

  private ImageIcon icon;

  HeaderPanel(ImageIcon icon, String title, String help1, String help2) {
    super(new BorderLayout());

    this.icon = icon;

    JPanel titlesPanel = new JPanel(new GridLayout(3, 1));
    titlesPanel.setOpaque(false);
    titlesPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

    JLabel headerTitle = new JLabel(title);
    Font police = headerTitle.getFont().deriveFont(Font.BOLD);
    headerTitle.setFont(police);
    headerTitle.setBorder(new EmptyBorder(0, 12, 0, 0));
    titlesPanel.add(headerTitle);

    JLabel message;

    titlesPanel.add(message = new JLabel(help1));
    police = headerTitle.getFont().deriveFont(Font.PLAIN);
    message.setFont(police);
    message.setBorder(new EmptyBorder(0, 24, 0, 0));

    titlesPanel.add(message = new JLabel(help2));
    police = headerTitle.getFont().deriveFont(Font.PLAIN);
    message.setFont(police);
    message.setBorder(new EmptyBorder(0, 24, 0, 0));

    message = new JLabel(this.icon);
    message.setBorder(new EmptyBorder(0, 0, 0, 12));

    add(BorderLayout.WEST, titlesPanel);
    add(BorderLayout.EAST, message);
    add(BorderLayout.SOUTH, new JSeparator(JSeparator.HORIZONTAL));

    setPreferredSize(new Dimension(500, this.icon.getIconHeight() + 24));
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (!isOpaque()) {
      return;
    }

    Rectangle bounds = g.getClipBounds();

    Color control = UIManager.getColor("control");
    int width = getWidth();

    Graphics2D g2 = (Graphics2D) g;
    Paint storedPaint = g2.getPaint();
    g2.setPaint(new GradientPaint(this.icon.getIconWidth(), 0, Color.white, width, 0, control));
    g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    g2.setPaint(storedPaint);
  }

  private static final long serialVersionUID = -3238096725182573034L;
}
