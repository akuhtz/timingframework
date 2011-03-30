package org.jdesktop.swing.animation.timing.demos.ch15;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Go/Stop buttons to control the animation
 * 
 * @author Chet Haase
 */
public class RaceControlPanel extends JPanel {

  JButton goButton = new JButton("Go");
  JButton pauseResumeButton = new JButton("Pause/Resume");
  JButton stopButton = new JButton("Stop");

  /**
   * Creates a new instance of RaceControlPanel
   */
  public RaceControlPanel() {
    add(goButton);
    add(pauseResumeButton);
    add(stopButton);
  }

  public JButton getGoButton() {
    return goButton;
  }

  public JButton getPauseResumeButton() {
    return pauseResumeButton;
  }

  public JButton getStopButton() {
    return stopButton;
  }

  public void addListener(ActionListener listener) {
    goButton.addActionListener(listener);
    pauseResumeButton.addActionListener(listener);
    stopButton.addActionListener(listener);
  }

  private static final long serialVersionUID = -4156188778768009687L;
}
