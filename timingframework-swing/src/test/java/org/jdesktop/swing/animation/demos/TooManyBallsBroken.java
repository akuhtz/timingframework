package org.jdesktop.swing.animation.demos;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.core.animation.demos.DemoResources;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * This demonstration is a variant of the demonstration by Chet Haase at JavaOne
 * 2008. Chet discussed the problem in the original Timing Framework where, by
 * default, each animation used its own {@code javax.swing.Timer}. This did not
 * scale and, as balls were added to the demonstration, the multiple timers
 * caused noticeable performance problems.
 * <p>
 * This version of the "Too Many Balls!" demonstration mimics the problem Chet
 * demonstrated by explicitly creating a Swing Timer for each animation.
 * <p>
 * This version of the Timing Framework allows setting a default timer on the
 * {@link AnimatorBuilder}, thus making it easy for client code to avoid this
 * problem. This design was inspired by the JavaOne 2008 talk.
 * 
 * @author Tim Halloran
 */
public class TooManyBallsBroken {

  public static void main(String[] args) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new TooManyBallsBroken();
      }
    });
  }

  static final TimingSource f_repaintTimer = new SwingTimerTimingSource();
  /**
   * Used to update the FPS display once a second.
   */
  static final TimingSource f_infoTimer = new SwingTimerTimingSource(1, SECONDS);

  static final Interpolator ACCEL_4_4 = new AccelerationInterpolator(0.4, 0.4);
  static final Interpolator SPLINE_0_1_1_0 = new SplineInterpolator(0.00, 1.00, 1.00, 1.00);
  static final Interpolator SPLINE_1_0_1_1 = new SplineInterpolator(1.00, 0.00, 1.00, 1.00);

  final JFrame f_frame;
  final JLabel f_infoLabel;
  final BallField f_field;
  final Random f_die = new Random();
  BufferedImage[] f_ballImages;

  public class Ball {
    int x, y;
    int imageIndex;
    Animator animator;
    TimingSource ts;

    public void setX(int x) {
      this.x = x;
    }

    public void setY(int y) {
      this.y = y;
    }
  }

  final List<Ball> f_balls = new ArrayList<Ball>();

  public TooManyBallsBroken() {
    f_frame = new JFrame("Swing Too Many Balls! - Too Many Timers!");
    f_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f_frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
        f_repaintTimer.dispose();
        f_infoTimer.dispose();
        for (Ball ball : f_balls) {
          ball.ts.dispose();
          ball.animator.stop();
        }
      }
    });
    f_frame.setLayout(new BorderLayout());
    JPanel topPanel = new JPanel();
    f_frame.add(topPanel, BorderLayout.NORTH);
    topPanel.setLayout(new BorderLayout());
    JPanel buttonPanel = new JPanel();
    topPanel.add(buttonPanel, BorderLayout.WEST);
    buttonPanel.setLayout(new FlowLayout());
    final JButton addBall = new JButton("Add Ball");
    addBall.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        addBall();
        updateBallCount();
      }
    });
    final JButton add10Balls = new JButton("Add 10 Balls");
    add10Balls.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        addBall();
        updateBallCount();
      }
    });
    final JButton removeBall = new JButton("Remove Ball");
    removeBall.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        removeBall();
        updateBallCount();
      }
    });
    final JButton remove10Balls = new JButton("Remove 10 Balls");
    remove10Balls.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        removeBall();
        updateBallCount();
      }
    });
    buttonPanel.add(addBall);
    buttonPanel.add(add10Balls);
    buttonPanel.add(removeBall);
    buttonPanel.add(remove10Balls);
    f_infoLabel = new JLabel();
    topPanel.add(f_infoLabel, BorderLayout.EAST);

    f_field = new BallField();
    f_frame.add(f_field, BorderLayout.CENTER);
    f_field.setBackground(Color.white);

    f_infoTimer.addTickListener(new TickListener() {
      @Override
      public void timingSourceTick(TimingSource source, long nanoTime) {
        updateBallCount();
      }
    });

    f_infoTimer.init();
    f_repaintTimer.init();
    f_repaintTimer.addPostTickListener(new PostTickListener() {
      @Override
      public void timingSourcePostTick(TimingSource source, long nanoTime) {
        f_field.repaint();
      }
    });

    f_ballImages = new BufferedImage[DemoResources.SPHERES.length];
    int index = 0;
    for (String resourceName : DemoResources.SPHERES) {
      try {
        f_ballImages[index++] = ImageIO.read(DemoResources.getResource(resourceName));
      } catch (IOException e) {
        throw new IllegalStateException("Unable to load image: " + resourceName, e);
      }
    }

    f_frame.setPreferredSize(new Dimension(800, 600));
    f_frame.pack();
    f_frame.setVisible(true);
  }

  void updateBallCount() {
    f_infoLabel.setText("Balls: " + f_balls.size() + "    FPS: " + getFPS());
    f_frame.validate();
  }

  /*
   * Renderer thread methods and state
   */

  void addBall() {
    final Ball ball = new Ball();
    ball.imageIndex = f_die.nextInt(5);
    BufferedImage ballImage = f_ballImages[ball.imageIndex];

    ball.x = f_die.nextInt(f_field.getWidth() - ballImage.getWidth());
    ball.y = f_die.nextInt(f_field.getHeight() - ballImage.getHeight());

    final int duration = 4 + f_die.nextInt(10);

    /*
     * Create a circular movement.
     */
    int radiusX = f_die.nextInt(400);
    if (f_die.nextBoolean())
      radiusX = -radiusX;
    int radiusY = f_die.nextInt(300);
    if (f_die.nextBoolean())
      radiusY = -radiusY;
    KeyFrames.Builder<Integer> builder = new KeyFrames.Builder<Integer>(ball.x);
    builder.addFrame(ball.x + radiusX, SPLINE_0_1_1_0);
    builder.addFrame(ball.x, SPLINE_1_0_1_1);
    builder.addFrame(ball.x - radiusX, SPLINE_0_1_1_0);
    builder.addFrame(ball.x, SPLINE_1_0_1_1);
    final KeyFrames<Integer> framesX = builder.build();

    builder = new KeyFrames.Builder<Integer>(ball.y);
    builder.addFrame(ball.y + radiusY, SPLINE_1_0_1_1);
    builder.addFrame(ball.y + (2 * radiusY), SPLINE_0_1_1_0);
    builder.addFrame(ball.y + radiusY, SPLINE_1_0_1_1);
    builder.addFrame(ball.y, SPLINE_0_1_1_0);
    final KeyFrames<Integer> framesY = builder.build();

    final TimingTarget circularMovement = new TimingTargetAdapter() {
      @Override
      public void timingEvent(Animator source, double fraction) {
        ball.x = framesX.getInterpolatedValueAt(fraction);
        ball.y = framesY.getInterpolatedValueAt(fraction);
      }
    };
    /*
     * Sometimes go at a constant rate, sometimes accelerate and decelerate.
     */
    final Interpolator i = f_die.nextBoolean() ? ACCEL_4_4 : null;
    ball.ts = new SwingTimerTimingSource();
    ball.ts.init();
    ball.animator = new Animator.Builder(ball.ts).setDuration(duration, SECONDS).addTarget(circularMovement)
        .setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.LOOP).setInterpolator(i).build();
    ball.animator.start();

    f_balls.add(ball);
  }

  void removeBall() {
    if (f_balls.isEmpty())
      return;

    Ball ball = f_balls.remove(0);
    if (ball != null) {
      ball.ts.dispose();
      ball.animator.stop();
    }
  }

  long f_paintCount = 0;
  long f_lastPaintNanos = 0;
  long f_totalPaintTimeNanos = 0;

  private long getFPS() {
    if (f_paintCount < 1)
      return 0;
    final long avgCycleTime = f_totalPaintTimeNanos / f_paintCount;
    if (avgCycleTime != 0) {
      return SECONDS.toNanos(1) / avgCycleTime;
    } else
      return 0;
  }

  /**
   * A simple {@link JPanel} that draws paints the balls on a white background.
   */
  private final class BallField extends JPanel {

    private static final long serialVersionUID = -3109606027190086843L;

    public BallField() {
      setOpaque(true);
    }

    @Override
    public void paintComponent(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;

      g2d.setBackground(Color.white);
      g2d.clearRect(0, 0, f_field.getWidth(), f_field.getHeight());

      for (Ball ball : f_balls) {
        g2d.drawImage(f_ballImages[ball.imageIndex], ball.x, ball.y, null);
      }

      // Statistics
      final long now = System.nanoTime();
      if (f_lastPaintNanos == 0) {
        f_lastPaintNanos = now;
      } else {
        f_paintCount++;
        f_totalPaintTimeNanos += now - f_lastPaintNanos;
        f_lastPaintNanos = now;
      }
    }
  }
}
