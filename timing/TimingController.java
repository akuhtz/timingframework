/**
 * Copyright (c) 2004, Sun Microsystems, Inc
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
 *   * Neither the name of the Ping demo project nor the names of its
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


package timing;

import javax.swing.Timer;
import java.awt.event.*;


/**
 * TimingController
 * <p>
 * This class is a utility for performing animations.  It layers
 * more functionality on existing timing utilities in Java.
 * It is a simple class whose functionality is mainly
 * in the constructor and in its later calls to the 
 * <code>timingEvent()</code>
 * method that subclasses shoudl override.
 */
public class TimingController {

    private Timer timer;    // Currently uses Swing timer.  This could change
			    // in the future to use a more general mechanism
			    // (and one of better timing resolution)

    private long startTime;	    // Tracks original cycle start time
    private long currentStartTime;  // Tracks current cycle start time
    private int currentCycle = 0;   // Tracks number of cycles so far
    private Direction direction = Direction.FORWARD;	// Tracks current direction
    private boolean intNumCycles;

    private Envelope envelope;
    private Cycle cycle;

    // Used internally to track current animation direction
    private enum Direction {
        FORWARD,
        BACKWARD};

    /**
     * Used to specify indefinite Cycle duration or Envelope numCycles
     * @see Cycle
     * @see Envelope
     * */
    public static final int INFINITE = -1;

    /**
     * Constructor: sets up all necessary information
     * @param cycle encapsulates the parameters that
     * each timing cycle will use.
     * @param envelope encapsulates the parameters that
     * each envelope will use
     * @see Cycle
     * @see Envelope
     */
    public TimingController(Cycle cycle, Envelope envelope) {

	// Set class variables
	this.cycle = cycle;
	this.envelope = envelope;

	// Set convenience variable: do we have an integer number of cycles?
	intNumCycles = 
	    (Math.rint(envelope.getNumCycles()) == envelope.getNumCycles());

	// Create internal Timer object
	TimerTarget timerTarget = new TimerTarget();
	timer = new Timer(cycle.getResolution(), timerTarget);
	timer.setInitialDelay(envelope.getStartDelay());

	/**
	 * hack workaround for starting the Toolkit thread before any Timer stuff
	 * javax.swing.Timer uses the Event Dispatch Thread, which is not
	 * created until the Toolkit thread starts up.  Using the Swing
	 * Timer before starting this stuff starts up may get unexpected
	 * results (such as taking a long time before the first timer
	 * event).
	 * */
	java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();	
    }

    /**
     * The constructor sets up everything, but start must be called to 
     * actually start the timer.
     */
    public void start() {
	// Initialize start time variables to current time
	startTime = (System.nanoTime() / 1000000) + envelope.getStartDelay();
	currentStartTime = startTime;
	timer.start();
    }

    /**
     * Returns whether this TimingController object is currently running
     */
    public boolean isRunning() {
	return timer.isRunning();
    }

    /**
     * This method is optional; animations will always stop on their own
     * if TimingController is provided with appropriate values for
     * duration and numCycles in the constructor.  But if the application 
     * wants to stop the timer mid-stream, this is the method to call.
     */
    public void stop() {
	timer.stop();
    }


    /**
     * Subclasses should override this method to receive timing events.
     * This default implementation does nothing.
     * @param cycleElapsedTime the total time in milliseconds elapsed in
     * the current Cycle
     * @param totalElapsedTime the total time in milliseconds elapsed
     * since the start of the first cycle
     * @param fraction the fraction of completion between the start and
     * end of the current cycle.  Note that on reversing cycles
     * (<code>Envelope.RepeatBehavior.REVERSE</code>) the fraction decreases
     * from 1.0 to 0 on backwards-running cycles.
     * @see Envelope.RepeatBehavior
     */
    protected void timingEvent(long cycleElapsedTime,
			       long totalElapsedTime, 
			       float fraction)
    {
	// Default action does nothing
    }
    
    /**
     * Internal implementation detail: we happen to use javax.swing.Timer
     * currently, which sends its timing events to an ActionListener.
     * This internal private class is our ActionListener that traps
     * these calls and forwards them to the TimingController.timingEvent()
     * method.
     */
    private class TimerTarget implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    long currentTime = System.nanoTime() / 1000000;
	    // Calculate values that are passed into TimingController.timingEvent()
	    long cycleElapsedTime = currentTime - currentStartTime;
	    long totalElapsedTime = currentTime - startTime;
	    double currentCycle = (double)totalElapsedTime / cycle.getDuration();

	    if ((envelope.getNumCycles() != INFINITE) &&
		currentCycle >= envelope.getNumCycles())
	    {
		// Envelope done: stop based on end behavior
		timer.stop();
		switch (envelope.getEndBehavior()) {
		case HOLD:
		    // Make sure we send a final end value
		    float endFraction;
		    if (intNumCycles) {
			// If supposed to run integer number of cycles, hold
			// on integer boundary
			if (direction == Direction.BACKWARD)
			{
			    // If we were traveling backward, hold on 0
			    endFraction = 0.0f;
			} else {
			    endFraction = 1.0f;
			}
		    } else {
			// hold on final value instead
			endFraction = Math.min(1.0f, 
			    ((float)cycleElapsedTime / cycle.getDuration()));
		    }
		    timingEvent(cycleElapsedTime, totalElapsedTime, endFraction);
		    break;

		case RESET:
		    // RESET requires setting the final value to the start value
		    timingEvent(cycleElapsedTime, totalElapsedTime, 0.0f);
		    break;
		default:
		    // should not reach here
		    break;
		}
	    } else if ((cycle.getDuration() != INFINITE) && 
		       (cycleElapsedTime > cycle.getDuration()))
	    {
		// Cycle end: Time to stop or change the behavior of the timer

		if (envelope.getRepeatBehavior() == 
		    Envelope.RepeatBehavior.REVERSE)
		{
		    // reverse the direction
		    if (direction == Direction.FORWARD) {
			// We were going forward: send an event with the final 
			// value and switch direction
			timingEvent(cycleElapsedTime, totalElapsedTime, 1.0f);
			direction = Direction.BACKWARD;
		    } else {
			// We were going backward: send an event with the initial 
			// value and switch direction
			timingEvent(cycleElapsedTime, totalElapsedTime, 0.0f);
			direction = Direction.FORWARD;
		    }
		    // Set new start time for this cycle
		    currentStartTime = currentTime;
		} else {
		    // Like REVERSE, only don't have to switch directions

		    // set initial value
		    timingEvent(cycleElapsedTime, totalElapsedTime, 0.0f);
		    // Set new start time for this cycle
		    currentStartTime = currentTime;
		}
	    } else {
		// mid-stream: calculate fraction of animation between
		// start and end times and send fraction to target
		float fraction = 0.0f;
		if (cycle.getDuration() != INFINITE) {
		    // Only limited duration animations need a fraction
		    fraction = (float)cycleElapsedTime / cycle.getDuration();
		    if (direction == Direction.BACKWARD) {
			// If this is a reversing cycle, want to know inverse
			// fraction; how much from start to finish, not 
			// finish to start
			fraction = (1.0f - fraction);
		    }
		    // Clamp fraction in case timing mechanism caused out of 
		    // bounds value
		    fraction = Math.min(fraction, 1.0f);
		    fraction = Math.max(fraction, 0.0f);
		}
		timingEvent(cycleElapsedTime, totalElapsedTime, fraction);
	    }
	}
    }

}
