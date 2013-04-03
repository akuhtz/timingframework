package org.jdesktop.swt.animation.demos;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.DiscreteInterpolator;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;

/**
 * An SWT application that demonstrates use of a {@link DiscreteInterpolator}
 * using {@link KeyFrames} within a {@link PropertySetter} animation.
 * <p>
 * This demo is discussed in Chapter 15 on page 410 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class DiscreteInterpolationTest extends TimingTargetAdapter {

  public static void main(String args[]) {
    f_display = Display.getDefault();

    TimingSource ts = new SWTTimingSource(100, TimeUnit.MILLISECONDS, f_display);
    Animator.setDefaultTimingSource(ts);
    ts.init();

    final Shell f_shell = new Shell(f_display);
    f_shell.setText("SWT DiscreteInterpolation Test");
    f_shell.setLayout(new FillLayout());
    f_benchmarkOutput = new Text(f_shell, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
    f_benchmarkOutput.setEditable(false);
    Font fixed = new Font(f_display, "Courier", 11, SWT.NONE);
    f_benchmarkOutput.setFont(fixed);
    f_benchmarkOutput.setBackground(f_display.getSystemColor(SWT.COLOR_BLACK));
    f_benchmarkOutput.setForeground(f_display.getSystemColor(SWT.COLOR_GREEN));

    f_shell.setSize(650, 500);
    f_shell.open();

    DiscreteInterpolationTest object = new DiscreteInterpolationTest();

    final KeyFrames<Integer> keyFrames = new KeyFrames.Builder<Integer>().addFrames(2, 6, 3, 5, 4)
        .setInterpolator(DiscreteInterpolator.getInstance()).build();
    out("Constructed Key Frames");
    out("----------------------");
    int i = 0;
    for (KeyFrames.Frame<Integer> keyFrame : keyFrames) {
      final String s = keyFrame.getInterpolator() == null ? "null" : keyFrame.getInterpolator().getClass().getSimpleName();
      out(String.format("Frame %d: value=%d timeFraction=%f interpolator=%s", i++, keyFrame.getValue(), keyFrame.getTimeFraction(),
          s));
    }
    final Animator animator = new Animator.Builder().setDuration(3, TimeUnit.SECONDS)
        .addTarget(PropertySetter.getTarget(object, "intValue", keyFrames)).addTarget(object).build();
    out("");
    out("Animation of intValue");
    out("---------------------");
    animator.start();

    while (!f_shell.isDisposed()) {
      if (!f_display.readAndDispatch())
        f_display.sleep();
    }
    ts.dispose();
    f_display.dispose();
    System.exit(0);
  }

  static Display f_display;
  static Text f_benchmarkOutput;

  int f_intValue;

  public void setIntValue(int intValue) {
    f_intValue = intValue;
    out("intValue = " + f_intValue);
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
}
