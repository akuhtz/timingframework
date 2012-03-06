package org.jdesktop.swing.animation.rendering;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.WrappedRunnable;
import org.jdesktop.core.animation.timing.sources.ManualTimingSource;

/**
 * Manages two-thread active rendering on a Swing {@link JRendererPanel}.
 * <p>
 * To use this renderer a client constructs a {@link JRendererPanel} and passes
 * it to the constructor with a flag indicating if the passed component will
 * have children that need to be rendered and a {@link JRendererTarget}
 * implementation. A typical sequence would be
 * 
 * <pre>
 * JFrame frame = new JFrame(&quot;Renderer Demonstration&quot;);
 * final JRendererPanel on = new JRendererPanel();
 * frame.setContentPane(on);
 * final JRendererTarget&lt;GraphicsConfiguration, Graphics2D&gt; target = this;
 * JRenderer renderer = new JActiveRenderer(on, target, true);
 * </pre>
 * 
 * In the above snippet <tt>on</tt> will be rendered to. Swing children will be
 * added to <tt>on</tt> by the client code (indicated by passing <tt>true</tt>
 * as the second argument) and should be shown. The enclosing instance,
 * <tt>this</tt>, implements {@link JRendererTarget} and will be called to
 * customize what is displayed on-screen.
 * <p>
 * The {@link JRendererTarget} implementation is called according to the
 * following protocol.
 * <ul>
 * <li>{@link JRendererTarget#renderSetup(Object)} is called once when
 * <tt>on</tt> is made visible. It allows the client to perform any necessary
 * setup.</li>
 * <li>{@link JRendererTarget#renderUpdate()} is called at the start of each
 * rendering cycle to allow the client to update its state prior to rendering.</li>
 * <li>{@link JRendererTarget#render(Object, int, int)} is called after
 * <tt>renderUpdate()</tt> during each rendering cycle to allow the client to
 * control what is displayed on-screen.</li>
 * <li>{@link JRendererTarget#renderShutdown()} is called once after
 * {@link JActiveRenderer#shutdown()} to allow the implementation to perform any
 * necessary cleanup.</li>
 * </ul>
 * The <i>rendering cycle</i> occurs as fast as the hardware can support. So
 * <tt>renderUpdate()</tt> and <tt>render()</tt> will be called many times per
 * second.
 * <p>
 * There are two threads involved in active rendering: (1) a rendering thread
 * and (2) the Swing Event Dispatch Thread (EDT). When the passed
 * {@link JComponent} is made visible this is detected in the EDT and two tasks
 * are submitted to be executed in the rendering thread: (1) The
 * {@link JRendererTarget#renderSetup(Object)} method is called to let the
 * client perform its setup. (2) The rendering loop is started.
 * <p>
 * The rendering thread runs the following sequence as fast as it can:
 * <ul>
 * <li>A call is made to {@link JRendererTarget#renderUpdate()} to allow the
 * client to update its state.</li>
 * <li>Internally, {@link CountDownLatch#await()} is called on the latch that is
 * waiting for the EDT thread to finish painting (skipped during the first
 * iteration).</li>
 * <li>A call is made to {@link JRendererTarget#render(Object, int, int)} to
 * allow the client to render onto an off-screen image.</li>
 * <li>A call to {@link SwingUtilities#invokeLater(Runnable)} asks the EDT
 * thread to paint the off-screen image on the screen.</li>
 * </ul>
 * <p>
 * The EDT thread, in addition to its normal Swing processing, performs the
 * following:
 * <ul>
 * <li>Paints the off-screen image to the screen.</li>
 * <li>Invokes {@link CountDownLatch#countDown()} to "pass" the off-screen image
 * back to the animator thread.</li>
 * </ul>
 * <p>
 * This two-thread active rendering approach has several advantages. First, it
 * <b>never</b> blocks the EDT. Second, it allows the
 * {@link JRendererTarget#renderUpdate()} to execute concurrently with painting
 * to the screen. Third, while the render thread can block, this only occurs if
 * painting to the screen takes longer than invoking
 * {@link JRendererTarget#renderUpdate()}.
 * <p>
 * The implementation depends upon the safe sharing of the {@link BufferedImage}
 * used as the off-screen image between the rendering thread and the EDT. The
 * use of {@link SwingUtilities#invokeLater(Runnable)} safely "passes" the image
 * from the rendering thread to the EDT and the use of the
 * {@link CountDownLatch} safely "passes" from the EDT back to the rendering
 * thread.
 * <p>
 * Clients can execute code in the rendering thread by calling
 * {@link #invokeLater(Runnable)} (in a manner similar to
 * {@link SwingUtilities#invokeLater(Runnable)}). This call is essential to
 * safely notify the rendering thread about events that occur in the EDT or
 * other program threads.
 * <p>
 * Several statistics are tracked which can be queried to understand active
 * rendering performance.
 * <ul>
 * <li>{@link #getFPS()} provides "frames per second" or how many times per
 * second the screen is painted. This is the best measure of overall performance
 * and is often displayed on-screen.</li>
 * <li>{@link #getAverageCycleTimeNanos()} provides how many nanoseconds, on
 * average, it tools to execute one complete rendering cycle.</li>
 * <li>{@link #getAveragePaintTimeNanos()} provides how many nanoseconds, on
 * average, it took the EDT to paint the off-screen image to the screen.</li>
 * <li>{@link #getAverageRenderTimeNanos()} provides how many nanoseconds, on
 * average, it took the rendering thread to render to the off-screen image (by
 * calling {@link JRendererTarget#render(Object, int, int)}).</li>
 * <li>{@link #getAveragePaintWaitTimeNanos()} provides how many nanoseconds, on
 * average, the rendering thread "blocked" waiting for the EDT to finish
 * painting to the screen. Because the rendering thread invokes
 * {@link JRendererTarget#renderUpdate()} before it has to "block" and wait for
 * the EDT to finish painting, this time can be significantly smaller than
 * {@link #getAveragePaintTimeNanos()}. <i>If this time is not smaller than
 * {@link #getAveragePaintTimeNanos()} then this two-thread active rendering
 * approach should probably not be used, i.e., a simple loop in a single thread
 * will be more efficient (i.e., provide more FPS).</i></li>
 * </ul>
 * <p>
 * Adding Swing components as children of <tt>on</tt> is supported. Ensure that
 * <tt>true</tt> is passed as the third argument to
 * {@link #JActiveRenderer(JRendererPanel, JRendererTarget, boolean)}. The
 * children are drawn in the EDT and never accessed in the rendering thread.
 * <p>
 * Use of the Timing Framework is supported via a {@link ManualTimingSource}
 * that can be obtained via {@link #getTimingSource()}. This timing source is
 * "ticked" once per rendering cycle. Client code should consider setting the
 * timing source as the default for animations, similar to the snippet below.
 * 
 * <pre>
 * AnimatorBuilder.setDefaultTimingSource(renderer.getTimingSource());
 * </pre>
 * 
 * Then the timing source's <tt>tick()</tt> method is invoked within the
 * rendering thread just prior to the call to <tt>renderUpdate()</tt>.
 * 
 * @author Tim Halloran
 * 
 * @see JRendererTarget
 * @see ManualTimingSource
 */
public final class JActiveRenderer implements JRenderer {

  /*
   * Shared state
   */
  private final ManualTimingSource f_ts = new ManualTimingSource();
  private final ExecutorService f_executor = Executors.newSingleThreadExecutor();
  private final AtomicBoolean f_renderingStarted = new AtomicBoolean(false);
  private final AtomicReference<CountDownLatch> f_edtPaintLatch = new AtomicReference<CountDownLatch>();
  private final AtomicReference<BufferedImage> f_renderingBuffer = new AtomicReference<BufferedImage>();
  private final AtomicReference<BufferedImage> f_replacementBuffer = new AtomicReference<BufferedImage>();
  private final AtomicBoolean f_shutdownRendering = new AtomicBoolean(false);

  /*
   * Statistics counters (shared)
   */
  private final AtomicLong f_totalRenderTime = new AtomicLong(0);
  private final AtomicLong f_renderCount = new AtomicLong(0);
  private final AtomicLong f_paintingRequestedNanos = new AtomicLong(0);
  private final AtomicLong f_totalPaintWaitTime = new AtomicLong(0);
  private final AtomicLong f_paintWaitCount = new AtomicLong(0);
  private final AtomicLong f_totalPaintTime = new AtomicLong();
  private final AtomicLong f_paintCount = new AtomicLong(0);

  /*
   * Thread-confined to the renderer thread (f_executor)
   */
  private final JRendererTarget<GraphicsConfiguration, Graphics2D> f_target;

  /*
   * Thread-confined to the EDT thread
   */
  private final JRendererPanel f_on;
  private final boolean f_hasChildren;

  /**
   * Constructs a new active renderer.
   * <p>
   * Should only be invoked from the Swing EDT.
   * 
   * @param on
   *          the Swing component to render on.
   * @param target
   *          to be called to control what is rendered.
   * @param hasChildren
   *          {@code true} if <tt>on</tt> has child components that need to be
   *          painted. If <tt>on</tt> has no child components passing
   *          {@code false} can improve rendering performance.
   * 
   * @throws IllegalArgumentException
   *           if either <tt>on</tt> or <tt>target</tt> are {@code null}.
   * @throws IllegalStateException
   *           if invoked outside of the Swing EDT.
   */
  public JActiveRenderer(JRendererPanel on, JRendererTarget<GraphicsConfiguration, Graphics2D> target, boolean hasChildren) {
    if (!SwingUtilities.isEventDispatchThread())
      throw new IllegalStateException(I18N.err(100));

    if (on == null)
      throw new IllegalArgumentException(I18N.err(1, "on"));
    f_on = on;

    if (target == null)
      throw new IllegalArgumentException(I18N.err(1, "target"));
    f_target = target;

    f_hasChildren = hasChildren;

    /*
     * Create and setup an on-screen panel to paint onto.
     */
    f_on.setDoubleBuffered(false);
    f_on.setOpaque(true);
    f_on.setIgnoreRepaint(true);

    f_on.addComponentListener(new ComponentAdapter() {

      /**
       * Used to detect resize notifications that don't really resize the Swing
       * component we are rendering on.
       */
      int f_width, f_height;

      @Override
      public void componentResized(ComponentEvent e) {
        if (f_on.getWidth() < 1 || f_on.getHeight() < 1)
          return;
        if (f_on.getWidth() == f_width && f_on.getHeight() == f_height)
          return;
        f_width = f_on.getWidth();
        f_height = f_on.getHeight();

        final Insets insets = f_on.getInsets();

        final GraphicsConfiguration gc = f_on.getGraphicsConfiguration();
        if (gc != null) {
          final BufferedImage buffer = gc.createCompatibleImage(f_width - insets.right - insets.left, f_height - insets.top
              - insets.bottom);
          f_replacementBuffer.set(buffer);
          if (f_renderingStarted.compareAndSet(false, true)) {
            /*
             * The first time we have an on-screen panel and an off-screen
             * buffer we are ready to begin rendering.
             */
            invokeLater(new Runnable() {
              @Override
              public void run() {
                f_target.renderSetup(gc);
              }
            });
            invokeLater(f_renderTask);
          }
        }
      }
    });
  }

  public void shutdown() {
    f_shutdownRendering.set(true);
    f_executor.shutdown();
    f_target.renderShutdown();
  }

  public long getFPS() {
    final long avgCycleTime = getAverageCycleTimeNanos();
    if (avgCycleTime != 0) {
      return TimeUnit.SECONDS.toNanos(1) / avgCycleTime;
    } else
      return 0;
  }

  public long getAverageCycleTimeNanos() {
    final long renderCount = f_renderCount.get();
    final long totalRenderTime = f_totalRenderTime.get();
    final long totalPaintWaitTime = f_totalPaintWaitTime.get();

    if (renderCount != 0) {
      return (totalRenderTime + totalPaintWaitTime) / renderCount;
    } else
      return 0;
  }

  /**
   * Calculates the average time spent rendering in the rendering thread. This
   * is the time spent in the call to
   * {@link JRendererTarget#render(Object, int, int)}.
   * <p>
   * Safe to be called at any time within any thread.
   * 
   * @return average time in nanoseconds.
   */
  public long getAverageRenderTimeNanos() {
    final long totalRenderTime = f_totalRenderTime.get();
    final long renderCount = f_renderCount.get();

    if (renderCount > 0)
      return totalRenderTime / renderCount;
    else
      return totalRenderTime;
  }

  /**
   * Gets the average amount of time spent waiting in the animator thread for
   * the EDT thread to complete painting to the screen.
   * <p>
   * Safe to be called at any time within any thread.
   * 
   * @return average time in nanoseconds.
   */
  public long getAveragePaintWaitTimeNanos() {
    final long totalPaintWaitTime = f_totalPaintWaitTime.get();
    final long paintWaitCount = f_paintWaitCount.get();

    if (paintWaitCount > 0)
      return totalPaintWaitTime / paintWaitCount;
    else
      return totalPaintWaitTime;
  }

  /**
   * The time spent within the EDT thread painting to the screen.
   * <p>
   * Safe to be called at any time within any thread.
   * 
   * @return average time in nanoseconds.
   */
  public long getAveragePaintTimeNanos() {
    final long totalPaintTime = f_totalPaintTime.get();
    final long paintCount = f_paintCount.get();

    if (paintCount > 0)
      return totalPaintTime / paintCount;
    else
      return totalPaintTime;
  }

  @Override
  public TimingSource getTimingSource() {
    return f_ts;
  }

  public void invokeLater(final Runnable task) {
    /*
     * Although the executor recovers from unhandled exceptions, it doesn't log
     * them or give any indication whatsoever that a problem occurred. So we
     * wrap the passed task to log any exception that is thrown during its
     * execution.
     */
    if (!f_executor.isShutdown())
      f_executor.submit(new WrappedRunnable(task));
  }

  /**
   * One cycle of the rendering loop. This task is always executed in the
   * rendering thread.
   */
  private final Runnable f_renderTask = new Runnable() {
    @Override
    public void run() {

      /*
       * We tick any animations and then update the game state while the EDT is
       * painting.
       */
      f_ts.tick();
      f_target.renderUpdate();

      /*
       * Wait for the EDT to finish painting.
       */
      final CountDownLatch edtPaintLatch = f_edtPaintLatch.get();
      if (edtPaintLatch != null) {
        try {
          edtPaintLatch.await();
        } catch (InterruptedException e) {
          Logger.getAnonymousLogger().log(Level.WARNING, I18N.err(101), e);
        }
      }

      if (f_shutdownRendering.get())
        return;

      /*
       * We will render onto an off-screen buffer image. This image has to be
       * replaced if the window is resized.
       */
      final BufferedImage replacementBuffer = f_replacementBuffer.getAndSet(null);
      final BufferedImage buffer;
      if (replacementBuffer != null) {
        final BufferedImage oldBuffer = f_renderingBuffer.getAndSet(replacementBuffer);
        if (oldBuffer != null)
          oldBuffer.flush();
        buffer = replacementBuffer;
        f_totalRenderTime.set(0);
        f_renderCount.set(0);
        f_totalPaintWaitTime.set(0);
        f_paintWaitCount.set(0);
      } else {
        buffer = f_renderingBuffer.get();
      }
      if (buffer != null) {
        /*
         * Render onto the off-screen image.
         */
        long t1 = System.nanoTime();
        final long paintingRequestedNanos = f_paintingRequestedNanos.get();
        if (paintingRequestedNanos != 0) {
          f_totalPaintWaitTime.getAndAdd(t1 - paintingRequestedNanos);
          f_paintWaitCount.incrementAndGet();
        }

        Graphics2D g2d = buffer.createGraphics();
        f_target.render(g2d, buffer.getWidth(), buffer.getHeight());
        g2d.dispose();

        final long now = System.nanoTime();
        f_paintingRequestedNanos.set(now);
        f_totalRenderTime.getAndAdd(now - t1);
        f_renderCount.incrementAndGet();

        /*
         * Send the off-screen image to the EDT to be painted onto the screen.
         */
        f_edtPaintLatch.set(new CountDownLatch(1));
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            paintOn(f_edtPaintLatch.get());
          }
        });
      }
      if (!f_shutdownRendering.get())
        invokeLater(this);
    }
  };

  /**
   * When this method is called in the EDT it has access to the rendering buffer
   * until it invokes {@link CountDownLatch#countDown()} which informs the
   * rendering thread that painting to the on-screen panel is complete.
   * 
   * @param paintingCompleted
   *          signals that painting to the screen is compete when
   *          {@link CountDownLatch#countDown()} is invoked.
   */
  private void paintOn(CountDownLatch paintingCompleted) {
    final long t1 = System.nanoTime();
    final Graphics g = f_on.getGraphics();
    final BufferedImage buffer = f_renderingBuffer.get();
    if (g != null && buffer != null) { // probably not visible
      /*
       * Paint the Swing children of this component, if necessary.
       */
      if (f_hasChildren) {
        final Graphics2D g2d = buffer.createGraphics();
        f_on.renderChildren(g2d);
        g2d.dispose();
      }
      g.drawImage(buffer, 0, 0, null);
    }
    f_totalPaintTime.getAndAdd(System.nanoTime() - t1);
    f_paintCount.incrementAndGet();
    paintingCompleted.countDown();
  }
}
