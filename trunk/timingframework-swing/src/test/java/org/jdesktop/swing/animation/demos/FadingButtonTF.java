package org.jdesktop.swing.animation.demos;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Animator.Direction;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * A demonstration of a fading button using the Timing Framework.
 * <p>
 * This demo is discussed in Chapter 14 on pages 353&ndash;356 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * 
 * @author Chet Haase
 */
public class FadingButtonTF extends JButton implements ActionListener, TimingTarget {

  float f_alpha = 1.0f; // current opacity of button
  Animator f_animator; // for later start/stop actions
  BufferedImage f_buttonImage = null;

  public FadingButtonTF(String label) {
    super(label);
    setOpaque(false);
    f_animator = new Animator.Builder().setRepeatCount(Animator.INFINITE).setStartDirection(Direction.BACKWARD).addTarget(this)
        .build();
    addActionListener(this);
  }

  public void paint(Graphics g) {
    // Create an image for the button graphics if necessary
    if (f_buttonImage == null || f_buttonImage.getWidth() != getWidth() || f_buttonImage.getHeight() != getHeight()) {
      f_buttonImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight());
    }
    Graphics gButton = f_buttonImage.getGraphics();
    gButton.setClip(g.getClip());

    // Have the superclass render the button for us
    super.paint(gButton);

    // Make the graphics object sent to this paint() method translucent
    Graphics2D g2d = (Graphics2D) g;
    AlphaComposite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f_alpha);
    g2d.setComposite(newComposite);

    // Copy the button's image to the destination graphics, translucently
    g2d.drawImage(f_buttonImage, 0, 0, null);
  }

  /**
   * This method receives button click events, which we use to start and stop
   * the animation.
   */
  public void actionPerformed(ActionEvent ae) {
    if (!f_animator.isRunning()) {
      this.setText("Stop Animation");
      f_animator.start();
    } else {
      f_animator.stop();
      this.setText("Start Animation");
      // reset alpha to opaque
      f_alpha = 1.0f;
    }
  }

  @Override
  public void begin(Animator source) {
    // nothing to do
  }

  @Override
  public void end(Animator source) {
    // nothing to do
  }

  @Override
  public void repeat(Animator source) {
    // nothing to do
  }

  @Override
  public void reverse(Animator source) {
    // nothing to do
  }

  /**
   * This method sets the alpha of our button to be equal to the current elapsed
   * fraction of the animation
   */
  @Override
  public void timingEvent(Animator source, double fraction) {
    f_alpha = (float) fraction;
    // redisplay our button
    repaint();
  }

  static void createAndShowGUI() {
    final TimingSource ts = new SwingTimerTimingSource();
    Animator.setDefaultTimingSource(ts);
    ts.init();

    final JFrame frame = new JFrame("Swing Fading Button TF");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 300);
    JPanel checkerboard = new Checkerboard();
    checkerboard.add(new FadingButtonTF("Start Animation"));
    frame.add(checkerboard);
    frame.setVisible(true);
  }

  public static void main(String args[]) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    Runnable doCreateAndShowGUI = new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    };
    SwingUtilities.invokeLater(doCreateAndShowGUI);
  }

  static class Checkerboard extends JPanel {
    static final int CHECKER_SIZE = 60;

    public void paintComponent(Graphics g) {
      g.setColor(Color.black);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(Color.red);
      for (int stripeX = 0; stripeX < getWidth(); stripeX += CHECKER_SIZE) {
        for (int y = 0, row = 0; y < getHeight(); y += CHECKER_SIZE / 2, ++row) {
          int x = (row % 2 == 0) ? stripeX : (stripeX + CHECKER_SIZE / 2);
          g.fillRect(x, y, CHECKER_SIZE / 2, CHECKER_SIZE / 2);
        }
      }
    }

    private static final long serialVersionUID = 6865031008320340506L;
  }

  private static final long serialVersionUID = -892913895069393865L;
}
