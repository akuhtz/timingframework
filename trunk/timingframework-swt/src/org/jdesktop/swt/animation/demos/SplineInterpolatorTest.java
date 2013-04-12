package org.jdesktop.swt.animation.demos;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

/**
 * A SWT application that compares the elapsed fraction, in real time, to the
 * elapsed fraction when using a sample {@link SplineInterpolator}.
 * <p>
 * This is based upon the TimingResolution demo discussed in Chapter 14 on pages
 * 372&ndash;375 (the section on <i>Resolution</i>) of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008), however it has been
 * changed in several ways. First, it outputs to a SWT window. Second, it uses a
 * global timer (as advocated by Haase in his JavaOne 2008 talk) which causes
 * the "ticks" to be slightly off from what is shown in the book
 * (sometimes&mdash;you might get lucky).
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class SplineInterpolatorTest extends TimingTargetAdapter {

  static Display f_display;
  static Text f_benchmarkOutput;

  public static void main(String args[]) {
    f_display = Display.getDefault();

    TimingSource ts = new SWTTimingSource(DURATION / 10, MILLISECONDS, f_display);
    Animator.setDefaultTimingSource(ts);
    ts.init();

    final Shell f_shell = new Shell(f_display);
    f_shell.setText("SWT SplineInterpolator Test");
    f_shell.setLayout(new FillLayout());
    f_benchmarkOutput = new Text(f_shell, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
    f_benchmarkOutput.setEditable(false);
    Font fixed = new Font(f_display, "Courier", 11, SWT.NONE);
    f_benchmarkOutput.setFont(fixed);
    f_benchmarkOutput.setBackground(f_display.getSystemColor(SWT.COLOR_BLACK));
    f_benchmarkOutput.setForeground(f_display.getSystemColor(SWT.COLOR_GREEN));

    f_shell.setSize(350, 500);
    f_shell.open();

    SplineInterpolator si = new SplineInterpolator(1, 0, 0, 1);
    Animator animator = new Animator.Builder().setDuration(DURATION, MILLISECONDS).setInterpolator(si)
        .addTarget(new SplineInterpolatorTest()).build();
    animator.start();

    while (!f_shell.isDisposed()) {
      if (!f_display.readAndDispatch())
        f_display.sleep();
    }
    ts.dispose();
    f_display.dispose();
    System.exit(0);
  }

  /**
   * This method outputs the string to the GUI {@link #f_benchmarkOutput}.
   * 
   * @param s
   *          a string to append to the output.
   */
  private static void out(final String s) {
    final Runnable addToTextArea = new Runnable() {
      @Override
      public void run() {
        final StringBuffer b = new StringBuffer(f_benchmarkOutput.getText());
        b.append(s);
        b.append(Text.DELIMITER);
        f_benchmarkOutput.setText(b.toString());
        f_benchmarkOutput.setSelection(b.length());
      }
    };
    if (f_display.getThread().equals(Thread.currentThread())) {
      addToTextArea.run();
    } else {
      f_display.asyncExec(addToTextArea);
    }
  }

  private long startTime;
  private final static int DURATION = 5000; // milliseconds

  @Override
  public void begin(Animator source) {
    startTime = System.nanoTime() / 1000000;
    out("real  interpolated");
    out("----  ------------");
  }

  /**
   * Calculate the real fraction elapsed and output that along with the fraction
   * parameter, which has been non-linearly interpolated.
   */
  @Override
  public void timingEvent(Animator source, double fraction) {
    long currentTime = System.nanoTime() / 1000000;
    long elapsedTime = currentTime - startTime;
    double realFraction = (double) elapsedTime / DURATION;
    out(String.format("%.2f          %.2f", realFraction, fraction));
  }

  @Override
  public void end(Animator source) {
    out("");
    out("\"real\" fraction may exceed");
    out("1.00 due to callback delays");
    out("(interpolated never should)");
  }
}
