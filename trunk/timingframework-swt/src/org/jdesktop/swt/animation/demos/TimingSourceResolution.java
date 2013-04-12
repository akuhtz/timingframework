package org.jdesktop.swt.animation.demos;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdesktop.core.animation.demos.ScheduledExecutorFactory;
import org.jdesktop.core.animation.demos.TimingSourceFactory;
import org.jdesktop.core.animation.demos.TimingSourceResolutionThread;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

/**
 * A SWT application that outputs benchmarks of the available
 * {@link TimingSource} implementations.
 * <p>
 * This is based upon the TimingResolution demo discussed in Chapter 12 on pages
 * 288&ndash;300 (the section on <i>Resolution</i>) of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008), however it used the timers
 * via their Timing Framework {@link TimingSource} implementations rather than
 * directly.
 * <p>
 * Two timing source configurations are benchmarked:
 * <ol>
 * <li>{@link SWTTimingSource} (within SWT UI thread) &ndash; As discussed in
 * the book, this timer has the advantage that all calls made from it are within
 * the EDT.</li>
 * <li>{@link ScheduledExecutorTimingSource} (within timer thread) &ndash; This
 * timing source is provided by a <tt>util.concurrent</tt> and calls from it are
 * within its tread context.</li>
 * </ol>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class TimingSourceResolution implements TimingSourceResolutionThread.Depository {

  public static void main(String args[]) {
    final Display display = Display.getDefault();
    final Shell f_shell = new Shell(display);
    f_shell.setText("SWT TimingSource Resolution Benchmark");
    f_shell.setLayout(new FillLayout());
    final Text benchmarkOutput = new Text(f_shell, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
    benchmarkOutput.setEditable(false);
    Font fixed = new Font(display, "Courier", 11, SWT.NONE);
    benchmarkOutput.setFont(fixed);
    benchmarkOutput.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    benchmarkOutput.setForeground(display.getSystemColor(SWT.COLOR_GREEN));

    final TimingSourceResolution demo = new TimingSourceResolution(display, benchmarkOutput);

    f_shell.setSize(450, 600);
    f_shell.open();

    /*
     * Run the benchmarks in a thread outside the SWT UI thread.
     */
    demo.f_benchmarkThread = new TimingSourceResolutionThread(demo, new ScheduledExecutorFactory(), new SWTTimerFactory(display));
    demo.f_benchmarkThread.start();

    while (!f_shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
    System.exit(0);
  }

  private TimingSourceResolution(Display display, Text benchmarkOutput) {
    f_display = display;
    f_benchmarkOutput = benchmarkOutput;
  }

  final Display f_display;

  static Text f_benchmarkOutput;

  TimingSourceResolutionThread f_benchmarkThread = null;

  /**
   * This method outputs the string to the GUI {@link #f_benchmarkOutput}.
   * 
   * @param s
   *          a string to append to the output.
   */
  public void out(final String s) {
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

  static class SWTTimerFactory implements TimingSourceFactory {

    private final Display f_display;

    public SWTTimerFactory(Display display) {
      f_display = display;
    }

    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new SWTTimingSource(periodMillis, MILLISECONDS, f_display);
    }

    @Override
    public String toString() {
      return "SWTTimingSource (Calls in SWT UI thread)";
    }
  }
}
