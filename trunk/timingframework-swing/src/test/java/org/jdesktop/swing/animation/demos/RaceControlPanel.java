package org.jdesktop.swing.animation.demos;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Go/Stop buttons to control the animation
 * 
 * @author Chet Haase
 */
public class RaceControlPanel extends JPanel {

  JButton goButton = new JButton("Go/Restart");
  JButton reverseButton = new JButton("Reverse Direction");
  JButton pauseResumeButton = new JButton("Pause/Resume");
  JButton stopButton = new JButton("Stop");

  /**
   * Creates a new instance of RaceControlPanel
   */
  public RaceControlPanel() {
    add(goButton);
    add(reverseButton);
    add(pauseResumeButton);
    add(stopButton);
  }

  public JButton getGoButton() {
    return goButton;
  }

  public JButton getReverseButton() {
    return reverseButton;
  }

  public JButton getPauseResumeButton() {
    return pauseResumeButton;
  }

  public JButton getStopButton() {
    return stopButton;
  }

  private static final long serialVersionUID = -1737346780545607168L;
}
