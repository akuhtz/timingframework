/**
 * Copyright (c) 2005, Sun Microsystems, Inc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following 
 *     disclaimer in the documentation and/or other materials provided 
 *     with the distribution.
 *   * Neither the name of the TimingFramework project nor the names of its
 *     contributors may be used to endorse or promote products derived 
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * SimpleAnimation
 * 
 * This class demonstrates the functionality of the TimingController
 * utility.  It uses a simple GUI that allows the user to enter
 * different values and then start the animation.  The animation is
 * simply duke moving in a diagonal line from the upper-left corner
 * to the lower-right corner; the timing attributes in the GUI
 * control the behavior and speed of that animation.
 */

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.net.URL;
import java.text.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import timing.*;

/**
 * Main class that starts it off.  This creates the view where
 * the animation is drawn (AnimationView) and the GUI that 
 * controls the aimation (ControlPanel).
 */
public class SimpleAnimation {

    /**
     * Creates the window and sub-panels and makes it all visible
     */
    private static void createAndShowGUI() {
	UIManager.put("swing.boldMetal", Boolean.FALSE);
	JFrame f = new JFrame("Timing Demo");
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	f.setLayout(new BorderLayout());
	f.setSize(500, 500);
	AnimationView animationView = new AnimationView();
	ControlPanel controlPanel = new ControlPanel(animationView);
	f.add(controlPanel, BorderLayout.CENTER);
	f.setVisible(true);
    }

    public static void main(String[] args) {
	// Need to do GUI stuff like making the JFrame visible on the
	// Event Dispatch Thread; do this via invokeLater()

	Runnable doCreateAndShowGUI = new Runnable() {
	    public void run() {
		createAndShowGUI();
	    }
	};
	SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
}

/**
 * The canvas where the animation is drawn.
 */
class AnimationView extends JComponent {

    Image duke = null;
    int xStart = 0, yStart = 0;	 // upper-left of image
    int xEnd = 300, yEnd = 300;  // set later based on component size
    int xCurrent = 0, yCurrent = 0; // current position of duke
    int prevW = -1, prevH = -1;  // used to track view resizes

    public AnimationView() {
	try {
	    URL url = getClass().getClassLoader().getResource("images/duke.gif");
	    duke = ImageIO.read(url);
	} catch (Exception e) {
	    System.out.println("Problem loading duke.gif file: " + e);
	}
    }

    /**
     * Utility method to set current duke position and schedule repaints
     * based on old and new positions.
     */
    private synchronized void setDukePosition(float fraction) {
	Rectangle oldRect = new Rectangle(xCurrent, yCurrent, 
		duke.getWidth(null), duke.getHeight(null));
	xCurrent = (int)(xStart + ((xEnd - xStart) * fraction) + .5f);
	yCurrent = (int)(yStart + ((yEnd - yStart) * fraction) + .5f);
	Rectangle newRect = new Rectangle(xCurrent, yCurrent, 
		duke.getWidth(null), duke.getHeight(null));
	newRect.add(oldRect);
	repaint(newRect);
    }
    
    /**
     * Called by TimingController subclass (below).  This sets the
     * fraction for the image animation and then causes a repaint().
     * Note that repaint() causes a repaint of the entire view; a more
     * optimal implementation would calculate and repaint only those
     * areas that needed to be repainted (erase the area where the image
     * used to be, draw the image in the new location).
     */
    public void setAnimationFraction(float fraction) {
	setDukePosition(fraction);
   }

    /**
     * Draws the image in the proper location according to where it
     * is in the animation cycle (given by currentFraction).
     */
    public void paintComponent(Graphics g) {
	if (isOpaque()) {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	}
	if (prevW != getWidth() || prevH != getHeight()) {
	    // Handle window resizing
	    prevW = getWidth();
	    prevH = getHeight();
	    xEnd = getWidth() - duke.getWidth(this);
	    yEnd = getHeight() - duke.getHeight(this);
	}
	g.drawImage(duke, xCurrent, yCurrent, null);
    }
}


/**
 * GUI controls for setting the attributes for the animation and
 * starting the animation running.
 */
class ControlPanel extends JPanel implements ActionListener {
    JFormattedTextField beginField;
    JFormattedTextField durationField;
    JFormattedTextField resolutionField;
    JFormattedTextField repeatCountField;
    JRadioButton repeatButton, reverseButton;
    JRadioButton holdButton, resetButton;
    TimingController animation = null;
    AnimationView animationView; // We will pass this into Animation
				 // so that the animation fraction can
				 // can be passed into AnimationView
				 // during the animation.

    /**
     * Sets up the GUI.  We will have simple labels/text-fields for
     * the number values and radio button groups for the behaviors.
     */
    public ControlPanel(AnimationView animationView) {
	this.animationView = animationView;

	setLayout(new GridBagLayout());
	NumberFormat intFormat = NumberFormat.getNumberInstance();
	intFormat.setParseIntegerOnly(true);
	NumberFormat doubleFormat = NumberFormat.getNumberInstance();

	// begin
	add(new JLabel("Time to Begin (ms):"),
	    new GridBagConstraints(0, 0, 1, 1, 0, 0,
				   GridBagConstraints.EAST, 
				   GridBagConstraints.NONE,
				   new Insets(5, 5, 0, 0), 0, 0));
	beginField = new JFormattedTextField(intFormat);
	beginField.setValue(0);
	add(beginField, 
	    new GridBagConstraints(1, 0, 1, 1, .5, 0,
				   GridBagConstraints.CENTER, 
				   GridBagConstraints.HORIZONTAL,
				   new Insets(5, 5, 0, 0), 0, 0));

	// duration
	add(new JLabel("Cycle Duration (ms):"),
	    new GridBagConstraints(2, 0, 1, 1, 0, 0,
				   GridBagConstraints.EAST, 
				   GridBagConstraints.NONE,
				   new Insets(5, 5, 0, 0), 0, 0));
	durationField = new JFormattedTextField(intFormat);
	durationField.setValue(1000);
	add(durationField, 
	    new GridBagConstraints(3, 0, 1, 1, .5, 0,
				   GridBagConstraints.CENTER, 
				   GridBagConstraints.HORIZONTAL,
				   new Insets(5, 5, 0, 5), 0, 0));

	// resolution
	add(new JLabel("Resolution (ms):"),
	    new GridBagConstraints(0, 1, 1, 1, 0, 0,
				   GridBagConstraints.EAST, 
				   GridBagConstraints.NONE,
				   new Insets(5, 5, 0, 0), 0, 0));
	resolutionField = new JFormattedTextField(intFormat);
	resolutionField.setValue(30);
	add(resolutionField, 
	    new GridBagConstraints(1, 1, 1, 1, .5, 0,
				   GridBagConstraints.CENTER, 
				   GridBagConstraints.HORIZONTAL,
				   new Insets(5, 5, 0, 0), 0, 0));
	
	// repeatCount: Note that this is a non-integer field
	add(new JLabel("RepeatCount:"),
	    new GridBagConstraints(2, 1, 1, 1, 0, 0,
				   GridBagConstraints.EAST, 
				   GridBagConstraints.NONE,
				   new Insets(5, 5, 0, 0), 0, 0));
	repeatCountField = new JFormattedTextField(doubleFormat);
	repeatCountField.setValue(2);
	add(repeatCountField, 
	    new GridBagConstraints(3, 1, 1, 1, .5, 0,
				   GridBagConstraints.CENTER, 
				   GridBagConstraints.HORIZONTAL,
				   new Insets(5, 5, 0, 5), 0, 0));

	// RepeatBehavior radio buttons
	repeatButton = new JRadioButton("Repeat", true);
	reverseButton = new JRadioButton("Reverse");
	ButtonGroup group = new ButtonGroup();
	group.add(repeatButton);
	group.add(reverseButton);

	JPanel buttonPanel = new JPanel();
	buttonPanel.setBorder(new TitledBorder("Repeat Behavior"));
	buttonPanel.add(repeatButton);
	buttonPanel.add(reverseButton);
	add(buttonPanel, 
	    new GridBagConstraints(0, 2, 2, 1, .5, 0,
				   GridBagConstraints.CENTER, 
				   GridBagConstraints.HORIZONTAL,
				   new Insets(5, 5, 0, 0), 0, 0));

	// EndBehavior radio buttons
	holdButton = new JRadioButton("Hold", true);
	resetButton = new JRadioButton("Reset");
	group = new ButtonGroup();
	group.add(holdButton);
	group.add(resetButton);

	buttonPanel = new JPanel();
	buttonPanel.setBorder(new TitledBorder("End Behavior"));
	buttonPanel.add(holdButton);
	buttonPanel.add(resetButton);
	add(buttonPanel, 
	    new GridBagConstraints(2, 2, 2, 1, .5, 0,
				   GridBagConstraints.CENTER, 
				   GridBagConstraints.HORIZONTAL,
				   new Insets(5, 5, 0, 5), 0, 0));

	// Go button to start the animation
	JButton button;
	button = new JButton("GO");
	add(button, 
	    new GridBagConstraints(0, 3, 4, 1, 0, 0,
				   GridBagConstraints.EAST, 
				   GridBagConstraints.NONE,
				   new Insets(5, 5, 0, 5), 0, 0));
	button.addActionListener(this);
	
	JPanel panel = new JPanel(new BorderLayout());
	panel.add(animationView);
	TitledBorder animationBorder = new TitledBorder("Animation");
	animationBorder.setTitleFont(panel.getFont().deriveFont(18f));
	animationBorder.setTitleJustification(TitledBorder.CENTER);
	panel.setBorder(animationBorder);
	add(panel, 
	    new GridBagConstraints(0, 4, 4, 1, 1, 1,
				   GridBagConstraints.CENTER, 
				   GridBagConstraints.BOTH,
				   new Insets(5, 5, 5, 5), 0, 0));
	
    }

    /**
     * Utility method to retrieve textfield value as integer
     */
    private int getFieldValueAsInt(JTextField field) {
	String text = field.getText();
	int returnValue = 0;
	try {
	    returnValue = NumberFormat.getNumberInstance().parse(text).intValue();
	} catch (Exception e) {
	    System.out.println("Number formation exception: " + e);
	}
	return returnValue;
    }

    /**
     * Utility method to retrieve textfield value as double
     */
    private double getFieldValueAsDouble(JTextField field) {
	String text = field.getText();
	double returnValue = 0;
	try {
	    returnValue = NumberFormat.getNumberInstance().parse(text).doubleValue();
	} catch (Exception e) {
	    System.out.println("Number formation exception: " + e);
	}
	return returnValue;
    }

    /**
     * "Go" button pressed: get the values from the controls and
     * create/start the animation
     */
    public void actionPerformed(ActionEvent ae) {
	// If we are already running an animation, stop it pending
	// a restart with the new control values
	if (animation != null && animation.isRunning()) {
	    animation.stop();
	}
	int begin = getFieldValueAsInt(beginField);
	int duration = getFieldValueAsInt(durationField);
	int resolution = getFieldValueAsInt(resolutionField);
	double repeatCount = getFieldValueAsDouble(repeatCountField);
	Envelope.RepeatBehavior repeatBehavior = 
	    reverseButton.isSelected() ? Envelope.RepeatBehavior.REVERSE : 
					 Envelope.RepeatBehavior.FORWARD;
	Envelope.EndBehavior behavior = (holdButton.isSelected()) ? 
	    Envelope.EndBehavior.HOLD : Envelope.EndBehavior.RESET;

	/*
	 * Here is the core functionality in this entire class:
	 * Get the various values we want to use for the animation,
	 * create the Cycle and Envelope objects, and then create
	 * the TimingController object (Animation is a subclass
	 * of TimingController) with those objects.
	 */
	Cycle cycle = new Cycle(duration, resolution);
	Envelope envelope = new Envelope(repeatCount, begin, 
					 repeatBehavior, behavior);
	// Note the extra parameter to Animation (above what TimingController
	// requires); we will pass in the animation fraction to 
	// animationView during the animation
	AnimationTarget animationTarget = new AnimationTarget(animationView);
	animation = new TimingController(cycle, envelope, animationTarget);
	animation.start();
    }
}

/**
 * Animation implements TimingTarget in order to receive
 * the calls to timingEvent().  It passes the cycle fraction along
 * to the AnimationView object.
 */
class AnimationTarget implements TimingTarget {
    AnimationView animationView;

    public AnimationTarget(AnimationView animationView)
    {
	this.animationView = animationView;
    }

    /**
     * Don't care about the times here, only the cycle fraction.
     */
    public void timingEvent(long cycleElapsedTime,
			       long totalElapsedTime, 
			       float fraction)
    {
	animationView.setAnimationFraction(fraction);
    }
}
