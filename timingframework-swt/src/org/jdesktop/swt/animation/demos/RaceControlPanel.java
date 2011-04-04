package org.jdesktop.swt.animation.demos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Go/Stop buttons to control the animation
 * 
 * @author Chet Hasse
 * @author Tim Halloran
 */
public class RaceControlPanel extends Composite {

  final Button goButton;
  final Button reverseButton;
  final Button pauseResumeButton;
  final Button stopButton;

  /**
   * Creates a new instance of RaceControlPanel
   */
  public RaceControlPanel(Composite parent, int style) {
    super(parent, style);
    setLayout(new FillLayout());
    goButton = new Button(this, SWT.PUSH);
    goButton.setText("Go");
    reverseButton = new Button(this, SWT.PUSH);
    reverseButton.setText("Reverse");
    pauseResumeButton = new Button(this, SWT.PUSH);
    pauseResumeButton.setText("Pause/Resume");
    stopButton = new Button(this, SWT.PUSH);
    stopButton.setText("Stop");
  }

  public Button getGoButton() {
    return goButton;
  }

  public Button getReverseButton() {
    return reverseButton;
  }

  public Button getPauseResumeButton() {
    return pauseResumeButton;
  }

  public Button getStopButton() {
    return stopButton;
  }
}
