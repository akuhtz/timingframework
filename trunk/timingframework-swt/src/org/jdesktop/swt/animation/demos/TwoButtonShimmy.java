package org.jdesktop.swt.animation.demos;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;
import org.jdesktop.swt.animation.timing.triggers.TriggerUtility;

/**
 * A demonstration of moving buttons using the Timing Framework.
 * <p>
 * This demonstration uses property setters and triggers together to shimmy two
 * buttons back and forth across the window when they are clicked. "Infinite"
 * bounces back and forth until it is clicked again. "Once" goes back and forth
 * once and then stops. A mouse hover on either button causes the button to
 * slowly change to red. It slowly goes back to black when the mouse is moved
 * away.
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
    final Color black = shell.getDisplay().getSystemColor(SWT.COLOR_BLACK);
    final Color red = shell.getDisplay().getSystemColor(SWT.COLOR_RED);
    final Color background = new Color(shell.getDisplay(), 225, 227, 185);
    shell.getDisplay().disposeExec(new Runnable() {
      public void run() {
        background.dispose();
      }
    });

    shell.setLayout(null);
    shell.setBackground(background);
    shell.setSize(400, 200);

    /*
     * Creating Button with a infinite number of back and forth shimmy
     * repetitions
     */
    final Button btnInfinite = new Button(shell, SWT.PUSH);
    btnInfinite.setText("Infinite");
    btnInfinite.setBounds(10, 10, 130, 30);
    // Movement on click
    TimingTarget ttInfinite = PropertySetter.getTarget(btnInfinite, "location", new Point(10, 10), new Point(250, 10));
    Animator animatorInfinite = new Animator.Builder().setRepeatCount(Animator.INFINITE).setDuration(3, SECONDS)
        .addTarget(ttInfinite).build();
    TriggerUtility.addEventTrigger(btnInfinite, SWT.Selection, animatorInfinite);
    // Red text color on mouse hover
    TimingTarget ttInfinite2 = PropertySetter.getTarget(btnInfinite, "foreground", black, red);
    Animator animatorInfinite2 = new Animator.Builder().setDuration(2, SECONDS).addTarget(ttInfinite2).build();
    TriggerUtility.addMouseTrigger(btnInfinite, animatorInfinite2, MouseTriggerEvent.ENTER, true);

    /*
     * Creating Button with one back and forth shimmy
     */
    final Button btnFinite = new Button(shell, SWT.PUSH);
    btnFinite.setText("Once");
    btnFinite.setBounds(10, 50, 130, 30);
    // Movement on click
    TimingTarget ttFinite = PropertySetter.getTarget(btnFinite, "location", new Point(10, 50), new Point(250, 50),
        new Point(10, 50));
    Animator animatorFinite = new Animator.Builder().setDuration(6, SECONDS).addTarget(ttFinite).build();
    TriggerUtility.addEventTrigger(btnFinite, SWT.Selection, animatorFinite);
    // Red text color on mouse hover
    TimingTarget ttFinite2 = PropertySetter.getTarget(btnFinite, "foreground", black, red);
    Animator animatorFinite2 = new Animator.Builder().setDuration(2, SECONDS).addTarget(ttFinite2).build();
    TriggerUtility.addMouseTrigger(btnFinite, animatorFinite2, MouseTriggerEvent.ENTER, true);
  }
}
