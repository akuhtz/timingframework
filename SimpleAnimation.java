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
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import timing.*;

/**
 * Main class that starts it off.  This creates the view where
 * the animation is drawn (AnimationView) and the GUI that 
 * controls the aimation (ControlPanel).
 */
public class SimpleAnimation {
    public static void main(String args[]) {
	JFrame f = new JFrame();
	f.setLayout(new BorderLayout());
	f.setSize(500, 500);
	AnimationView animationView = new AnimationView();
	ControlPanel controlPanel = new ControlPanel(animationView);
	f.add(controlPanel, BorderLayout.WEST);
	f.add(animationView, BorderLayout.CENTER);
	f.setVisible(true);
    }
}

/**
 * The canvas where the animation is drawn.
 */
class AnimationView extends JComponent {

    Image duke = new ImageIcon("duke.gif").getImage();
    int xStart = 0, yStart = 0;	 // upper-left of image
    int xEnd = 300, yEnd = 300;  // set later based on component size
    int prevW = -1, prevH = -1;  // used to track view resizes
    float currentFraction = 0.0f;// used to track where the image should
				 // be drawn

    /**
     * Called by TimingController subclass (below).  This sets the
     * fraction for the image animation and then causes a repaint().
     * Note that repaint() causes a repaint of the entire view; a more
     * optimal implementation would calculate and repaint only those
     * areas that needed to be repainted (erase the area where the image
     * used to be, draw the image in the new location).
     */
    public void setAnimationFraction(float fraction) {
	currentFraction = fraction;
	repaint();
    }

    /**
     * Draws the image in the proper location according to where it
     * is in the animation cycle (given by currentFraction).
     */
    public void paintComponent(Graphics g) {
	if (prevW != getWidth() || prevH != getHeight()) {
	    // Handle window resizing
	    prevW = getWidth();
	    prevH = getHeight();
	    xEnd = getWidth() - duke.getWidth(this);
	    yEnd = getHeight() - duke.getHeight(this);
	}
	int xCurrent = (int)(xStart + ((xEnd - xStart) * currentFraction) + .5f);
	int yCurrent = (int)(yStart + ((yEnd - yStart) * currentFraction) + .5f);
	g.drawImage(duke, xCurrent, yCurrent, null);
    }
}


/**
 * GUI controls for setting the attributes for the animation and
 * starting the animation running.
 */
class ControlPanel extends JPanel implements ActionListener {
    JFormattedTextField startDelayField;
    JFormattedTextField durationField;
    JFormattedTextField resolutionField;
    JFormattedTextField numCyclesField;
    JRadioButton repeatButton, reverseButton;
    JRadioButton holdButton, resetButton;
    Animation animation = null;
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

	JPanel textPanel;
	setLayout(new GridLayout(7, 1));
	NumberFormat intFormat = NumberFormat.getNumberInstance();
	intFormat.setParseIntegerOnly(true);
	NumberFormat doubleFormat = NumberFormat.getNumberInstance();

	// startDelay
	textPanel = new JPanel();
	textPanel.setLayout(new GridLayout(1, 2));
	textPanel.add(new JLabel("startDelay"));
	startDelayField = new JFormattedTextField(intFormat);
	startDelayField.setValue(0);
	textPanel.add(startDelayField);
	add(textPanel);

	// duration
	textPanel = new JPanel();
	textPanel.setLayout(new GridLayout(1, 2));
	textPanel.add(new JLabel("duration"));
	durationField = new JFormattedTextField(intFormat);
	durationField.setValue(1000);
	textPanel.add(durationField);
	add(textPanel);

	// resolution
	textPanel = new JPanel();
	textPanel.setLayout(new GridLayout(1, 2));
	textPanel.add(new JLabel("resolution"));
	resolutionField = new JFormattedTextField(intFormat);
	resolutionField.setValue(30);
	textPanel.add(resolutionField);
	add(textPanel);

	// numCycles: Note that this is a non-integer field
	textPanel = new JPanel();
	textPanel.setLayout(new GridLayout(1, 2));
	textPanel.add(new JLabel("numCycles"));
	numCyclesField = new JFormattedTextField(doubleFormat);
	numCyclesField.setValue(2);
	textPanel.add(numCyclesField);
	add(textPanel);

	// RepeatBehavior radio buttons
	repeatButton = new JRadioButton("Repeat", true);
	reverseButton = new JRadioButton("Reverse");
	ButtonGroup group = new ButtonGroup();
	group.add(repeatButton);
	group.add(reverseButton);
	JPanel buttonPanel = new JPanel();
	buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	buttonPanel.add(repeatButton);
	buttonPanel.add(reverseButton);
	add(buttonPanel);

	// EndBehavior radio buttons
	holdButton = new JRadioButton("Hold", true);
	resetButton = new JRadioButton("Reset");
	group = new ButtonGroup();
	group.add(holdButton);
	group.add(resetButton);
	buttonPanel = new JPanel();
	buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	buttonPanel.add(holdButton);
	buttonPanel.add(resetButton);
	add(buttonPanel);

	// Go button to start the animation
	JButton button;
	button = new JButton("GO");
	add(button);
	button.addActionListener(this);
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
	int startDelay = getFieldValueAsInt(startDelayField);
	int duration = getFieldValueAsInt(durationField);
	int resolution = getFieldValueAsInt(resolutionField);
	double numCycles = getFieldValueAsDouble(numCyclesField);
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
	Envelope envelope = new Envelope(numCycles, startDelay, 
					 repeatBehavior, behavior);
	// Note the extra parameter to Animation (above what TimingController
	// requires); we will pass in the animation fraction to 
	// animationView during the animation
	animation = new Animation(animationView, cycle, envelope);
	animation.start();
    }
}

/**
 * Animation is a subclass of TimingController which simply receives
 * the calls to timingEvent() and passes the cycle fraction along
 * to the AnimationView object.
 */
class Animation extends TimingController {
    AnimationView animationView;

    public Animation(AnimationView animationView, Cycle cycle, 
		     Envelope envelope)
    {
	super(cycle, envelope);
	this.animationView = animationView;
    }

    /**
     * Don't care about the times here, only the cycle fraction.
     */
    protected void timingEvent(long cycleElapsedTime,
			       long totalElapsedTime, 
			       float fraction)
    {
	animationView.setAnimationFraction(fraction);
    }
}
