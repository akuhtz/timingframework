package org.jdesktop.swing.animation.demos;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * The GUI used by all of the different race demos. It contains a control panel
 * (for the Go/Pause/Stop buttons) and a TrackView (where the race is rendered)
 * 
 * @author Chet Haase
 */
public class RaceGUI {

  private RaceTrackView track;
  private RaceControlPanel controlPanel;

  public RaceGUI(String appName) {
    JFrame f = new JFrame(appName);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLayout(new BorderLayout());

    // Add Track view
    track = new RaceTrackView();
    f.add(track, BorderLayout.CENTER);

    // Add control panel
    controlPanel = new RaceControlPanel();
    f.add(controlPanel, BorderLayout.SOUTH);

    f.pack();
    f.setVisible(true);
  }

  public RaceTrackView getTrack() {
    return track;
  }

  public RaceControlPanel getControlPanel() {
    return controlPanel;
  }
}
