package org.jdesktop.swt.animation.demos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Animator.Direction;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

/**
 * A demonstration of a fading checkerboard using the Timing Framework.
 * <p>
 * This demo is discussed in Chapter 14 on pages 353&ndash;356 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008). However, because SWT
 * doesn't support translucent controls (at all) this demonstration fades the
 * red squares on the checkerboard in and out of view instead.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public final class FadingButtonTF {

  private static Color f_squareColor;

  public static void main(String[] args) {
    final Display display = Display.getDefault();
    final Shell shell = new Shell(display);
    shell.setText("SWT Fading Button TF");
    shell.setLayout(new FillLayout());

    final TimingSource animationTimer = new SWTTimingSource(display);
    Animator.setDefaultTimingSource(animationTimer);

    f_squareColor = display.getSystemColor(SWT.COLOR_RED);

    final Canvas checkerboard = new Canvas(shell, SWT.DOUBLE_BUFFERED);
    checkerboard.setLayout(new GridLayout());
    checkerboard.addPaintListener(new PaintListener() {

      private static final int CHECKER_SIZE = 60;

      @Override
      public void paintControl(PaintEvent e) {
        final GC gc = e.gc;
        final int width = checkerboard.getSize().x;
        final int height = checkerboard.getSize().y;
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.fillRectangle(0, 0, width, height);
        gc.setBackground(f_squareColor);
        for (int stripeX = 0; stripeX < width; stripeX += CHECKER_SIZE) {
          for (int y = 0, row = 0; y < height; y += CHECKER_SIZE / 2, ++row) {
            int x = (row % 2 == 0) ? stripeX : (stripeX + CHECKER_SIZE / 2);
            gc.fillRectangle(x, y, CHECKER_SIZE / 2, CHECKER_SIZE / 2);
          }
        }
      }
    });

    final TimingTarget target = new TimingTargetAdapter() {

      Color c;

      @Override
      public void timingEvent(Animator source, double fraction) {
        if (c != null)
          c.dispose();
        // fade the red color
        int red = (int) (fraction * 255);
        Color c = new Color(display, new RGB(red, 0, 0));
        f_squareColor = c;
        checkerboard.redraw();
      }
    };
    final Animator animator = new Animator.Builder().setRepeatCount(Animator.INFINITE).setStartDirection(Direction.BACKWARD)
        .addTarget(target).build();
    final Button push = new Button(checkerboard, SWT.PUSH);
    push.setText("Start Animation");
    push.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        if (!animator.isRunning()) {
          push.setText("Stop Animation");
          animator.start();
        } else {
          animator.stop();
          push.setText("Start Animation");
          // reset square color to red
          f_squareColor = display.getSystemColor(SWT.COLOR_RED);
          checkerboard.redraw();
        }
      }
    });
    push.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

    animationTimer.init();

    shell.setSize(500, 300);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    animationTimer.dispose();
    display.dispose();
  }
}
