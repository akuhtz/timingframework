package org.jdesktop.swing.animation.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import static java.util.concurrent.TimeUnit.SECONDS;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.core.animation.demos.DemoResources;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Evaluator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.evaluators.EvaluatorPoint2D;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * This demonstration uses property setters to create a "to" animation using the
 * Timing Framework.
 * <p>
 * A "to" animation uses the getter on the property to set the starting point of
 * the animation. Further, it tests that the {@link EvaluatorPoint2D} class is
 * automatically chosen as the {@link Evaluator} for a {@link Point} (
 * {@link Point} is an implementation of {@link Point2D}).
 * 
 * @author Tim Halloran
 */
public final class ClickAndGo extends JPanel {

  public static void main(String[] args) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        setupGUI();
      }
    });
  }

  public static class Ball {
    Point location;
    Rectangle rect;
    Color rectColor;
    BufferedImage image;
    Animator animator;

    public void setLocation(Point value) {
      location = value;
    }

    public Point getLocation() {
      return location;
    }

    public void setRect(Rectangle value) {
      rect = value;
    }

    public Rectangle getRect() {
      return rect;
    }

    public void setRectColor(Color value) {
      rectColor = value;
    }

    public Color getRectColor() {
      return rectColor;
    }
  }

  static final Ball f_ball = new Ball();
  static final Random f_die = new Random();
  @SuppressWarnings("rawtypes")
  static final Map f_desktopHints = (Map) java.awt.Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");

  public static void setupGUI() {
    try {
      f_ball.image = ImageIO.read(DemoResources.getResource(DemoResources.BLUE_SPHERE));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    f_ball.setLocation(new Point(50, 90));
    final int rectSize = f_ball.image.getWidth();
    f_ball.setRect(new Rectangle(50, 90, rectSize, rectSize));
    f_ball.setRectColor(new Color(100, 100, 100));

    final JFrame frame = new JFrame("Swing Click and Go!");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());

    final JPanel panel = new ClickAndGo();
    frame.add(panel, BorderLayout.CENTER);

    frame.setMinimumSize(new Dimension(800, 600));
    frame.validate();
    frame.setVisible(true);
  }

  public ClickAndGo() {
    setOpaque(true);
    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if (f_ball.animator != null)
          f_ball.animator.stop();

        SwingTimerTimingSource animationTimer = new SwingTimerTimingSource();
        animationTimer.init();
        animationTimer.addPostTickListener(new PostTickListener() {
          @Override
          public void timingSourcePostTick(TimingSource source, long nanoTime) {
            ClickAndGo.this.repaint();
          }
        });

        f_ball.animator = new Animator.Builder(animationTimer).setDuration(2, SECONDS).setDisposeTimingSource(true).build();

        final Point clickPoint = new Point(e.getX(), e.getY());
        f_ball.animator.addTarget(PropertySetter
            .getTargetTo(f_ball, "location", new AccelerationInterpolator(0.5, 0.5), clickPoint));

        final int rectSize = f_ball.image.getWidth();
        final Rectangle clickRect = new Rectangle(e.getX(), e.getY(), rectSize * (f_die.nextInt(4) + 1), rectSize
            * (f_die.nextInt(4) + 1));
        f_ball.animator.addTarget(PropertySetter.getTargetTo(f_ball, "rect", clickRect));

        final Color rectColor = new Color(f_die.nextInt(255), f_die.nextInt(255), f_die.nextInt(255));
        f_ball.animator.addTarget(PropertySetter.getTargetTo(f_ball, "rectColor", rectColor));

        f_ball.animator.start();
      }
    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    g2d.setBackground(Color.white);
    g2d.clearRect(0, 0, getWidth(), getHeight());

    if (f_desktopHints != null)
      g2d.addRenderingHints(f_desktopHints);
    g2d.drawString("Click on the screen and the ball and the colored rectangle will move to that point in two seconds.", 5, 20);
    g2d.drawString("The ball and the colored rectangle don't use the same interpolator, so they will move at different rates.", 5,
        35);
    g2d.drawString("Feel free to change the destination at any time.", 5, 50);

    final Rectangle r = f_ball.rect;
    g2d.setColor(f_ball.getRectColor());
    g2d.fillRect(r.x, r.y, r.width, r.height);
    g2d.setColor(Color.black);
    g2d.drawRect(r.x, r.y, r.width, r.height);
    g2d.drawImage(f_ball.image, f_ball.location.x, f_ball.location.y, null);
  }

  private static final long serialVersionUID = -2013857521308055898L;
}
