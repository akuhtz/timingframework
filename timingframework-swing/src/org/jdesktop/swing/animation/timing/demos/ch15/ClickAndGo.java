package org.jdesktop.swing.animation.timing.demos.ch15;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.Evaluator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.demos.DemoResources;
import org.jdesktop.swing.animation.timing.evaluators.EvaluatorPoint2D;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * This demonstration tests two aspects of using {@link KeyFrames} and a
 * {@link PropertySetter}. First, it ensures that "to" animations work properly.
 * A "to" animation uses the getter on the property to set the starting point of
 * the animation. Second, it tests that the {@link EvaluatorPoint2D} class is
 * automatically chosen as the {@link Evaluator} for a {@link Point} (
 * {@link Point} is an implementation of {@link Point2D}).
 * 
 * @author Tim Halloran
 */
public final class ClickAndGo extends JPanel {

  public static void main(String[] args) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    AnimatorBuilder.setDefaultTimingSource(f_animationTimer);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        setupGUI();
      }
    });
  }

  /**
   * Used for ball animations.
   */
  private static final SwingTimerTimingSource f_animationTimer = new SwingTimerTimingSource(15, TimeUnit.MILLISECONDS);

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

  private static final Ball f_ball = new Ball();
  private static final Random f_die = new Random();
  @SuppressWarnings("rawtypes")
  private static final Map f_desktopHints = (Map) java.awt.Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");

  public static void setupGUI() {
    try {
      f_ball.image = ImageIO.read(DemoResources.getResource(DemoResources.BLUE_SPHERE));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    f_ball.setLocation(new Point(50, 50));
    final int rectSize = f_ball.image.getWidth();
    f_ball.setRect(new Rectangle(50, 50, rectSize, rectSize));
    f_ball.setRectColor(new Color(100, 100, 100));

    final JFrame frame = new JFrame("Click and Go!");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
        f_animationTimer.dispose();
      }
    });
    frame.setLayout(new BorderLayout());

    final JPanel panel = new ClickAndGo();
    frame.add(panel, BorderLayout.CENTER);

    f_animationTimer.init();
    f_animationTimer.addPostTickListener(new PostTickListener() {
      @Override
      public void timingSourcePostTick(TimingSource source, long nanoTime) {
        panel.repaint();
      }
    });

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

        f_ball.animator = new AnimatorBuilder().setDuration(2, TimeUnit.SECONDS).build();

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
    g2d.drawString("Click on the screen an the ball will move to that point in 2 seconds.", 5, 20);
    g2d.drawString("Feel free to change the ball's destination at any time.", 5, 35);

    final Rectangle r = f_ball.rect;
    g2d.setColor(f_ball.getRectColor());
    g2d.fillRect(r.x, r.y, r.width, r.height);
    g2d.setColor(Color.black);
    g2d.drawRect(r.x, r.y, r.width, r.height);
    g2d.drawImage(f_ball.image, f_ball.location.x, f_ball.location.y, null);
  }

  private static final long serialVersionUID = -2013857521308055898L;
}
