package org.jdesktop.swt.animation.timing.demos.ch15;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * Go/Stop buttons to control the animation
 * 
 * @author Chet Hasse
 * @author Tim Halloran
 */
public class RaceControlPanel extends Composite {

	final Button goButton;
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
		pauseResumeButton = new Button(this, SWT.PUSH);
		pauseResumeButton.setText("Pause/Resume");
		stopButton = new Button(this, SWT.PUSH);
		stopButton.setText("Stop");
	}

	public Button getGoButton() {
		return goButton;
	}

	public Button getPauseResumeButton() {
		return pauseResumeButton;
	}

	public Button getStopButton() {
		return stopButton;
	}

	public void addListener(Listener listener) {
		goButton.addListener(SWT.Selection, listener);
		pauseResumeButton.addListener(SWT.Selection, listener);
		stopButton.addListener(SWT.Selection, listener);
	}
}
