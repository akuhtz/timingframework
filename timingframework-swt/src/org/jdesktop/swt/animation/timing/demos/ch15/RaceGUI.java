package org.jdesktop.swt.animation.timing.demos.ch15;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

/**
 * The GUI used by all of the different race demos. It contains a control panel
 * (for the Go/Pause/Stop buttons) and a TrackView (where the race is rendered)
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class RaceGUI {

  private RaceTrackView track;
  private RaceControlPanel controlPanel;

  public RaceGUI(Shell shell, String appName) {
    shell.setText(appName);
    shell.setLayout(new GridLayout());

    // Add Track view
    track = new RaceTrackView(shell, SWT.DOUBLE_BUFFERED);
    track.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // Add control panel
    controlPanel = new RaceControlPanel(shell, SWT.NONE);
    controlPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
  }

  public RaceTrackView getTrack() {
    return track;
  }

  public RaceControlPanel getControlPanel() {
    return controlPanel;
  }
}
