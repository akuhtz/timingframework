package org.jdesktop.swt.animation.demos;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;
import org.jdesktop.swt.animation.timing.triggers.TriggerUtility;

/**
 * This demonstration uses property setters and triggers together to shimmy two
 * buttons back and forth across the window. "Infinite" bounces back and forth
 * until it is clicked again. "Once" goes back and forth once and then stops.
 * 
 * @author Jan Studeny
 * @author Tim Halloran
 */
public final class TwoButtonShimmy {

  public static void main(String[] args) {
    final Display display = Display.getDefault();
    final Shell shell = new Shell(display);
    shell.setText("SWT TwoButtonShimmy");

    final TimingSource ts = new SWTTimingSource(display);
    Animator.setDefaultTimingSource(ts);
    ts.init();

    createAndShowGUI(shell);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    ts.dispose();
    display.dispose();
  }

  private static void createAndShowGUI(Shell shell) {
    shell.setLayout(null);
    shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
    shell.setSize(400, 200);

    final Button btnInfinite = new Button(shell, SWT.PUSH);
    btnInfinite.setText("Infinite");
    btnInfinite.setBounds(10, 10, 130, 30);
    TimingTarget ttInfinite = PropertySetter.getTarget(btnInfinite, "location", new Point(10, 10), new Point(250, 10));
    Animator animatorInfinite = new Animator.Builder().setRepeatCount(Animator.INFINITE).setDuration(3, TimeUnit.SECONDS)
        .addTarget(ttInfinite).build();
    TriggerUtility.addEventTrigger(btnInfinite, SWT.Selection, animatorInfinite);

    final Button btnFinite = new Button(shell, SWT.PUSH);
    btnFinite.setText("Once");
    btnFinite.setBounds(10, 50, 130, 30);
    TimingTarget ttFinite = PropertySetter.getTarget(btnFinite, "location", new Point(10, 50), new Point(250, 50),
        new Point(10, 50));
    Animator animatorFinite = new Animator.Builder().setDuration(3, TimeUnit.SECONDS).addTarget(ttFinite).build();
    TriggerUtility.addEventTrigger(btnFinite, SWT.Selection, animatorFinite);
  }
}
