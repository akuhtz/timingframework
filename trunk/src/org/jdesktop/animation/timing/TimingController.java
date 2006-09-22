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


package org.jdesktop.animation.timing;

import javax.swing.Timer;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * This class is a controls the timing sequence for animations.
 * Its begin(), end(), and timingEvent() methods are called during
 * the course of any animation; the default action in these methods
 * is to call all TimingTargets appropriately.
 */
public class TimingController implements TimingTarget {

    private Timer timer;    // Currently uses Swing timer.  This could change
			    // in the future to use a more general mechanism
			    // (and one of better timing resolution)

    private ArrayList targets = new ArrayList();
    private long startTime;	    // Tracks original cycle start time
    private long currentStartTime;  // Tracks current cycle start time
    private int currentCycle = 0;   // Tracks number of cycles so far
    private Direction direction = Direction.FORWARD;	// Tracks current direction
    private boolean intRepeatCount;
    private ArrayList listeners = new ArrayList();

    private Envelope envelope;
    private Cycle cycle;

    private float acceleration = 0;
    private float deceleration = 0.0f;
    
    // Used internally to track current animation direction
    private enum Direction {
	FORWARD,
	BACKWARD};

    /**
     * Used to specify indefinite Cycle duration or Envelope repeatCount
     * @see Cycle
     * @see Envelope
     * */
    public static final int INFINITE = -1;

    /**
     * Constructor: sets up all necessary information
     * @param cycle encapsulates the parameters that
     * each timing cycle will use.
     * @param envelope encapsulates the parameters that
     * each envelope will use.
     * @see Cycle
     * @see Envelope
     */
    public TimingController(Cycle cycle, Envelope envelope) {
	this(cycle, envelope, null);
    }
    
    /**
     * Constructor: sets up all necessary information
     * @param cycle encapsulates the parameters that
     * each timing cycle will use.
     * @param envelope encapsulates the parameters that
     * each envelope will use.
     * @param target TimingTarget object that will be called with
     * all timing events.  Additional targets may be added by calling
     * the {@link TimingController#addTarget addTarget} method.
     * @see Cycle
     * @see Envelope
     * @see #timingEvent(long, long, float)
     */
    public TimingController(Cycle cycle, Envelope envelope, 
			    TimingTarget target) {
	
	// Set class variables
	this.cycle = cycle;
	this.envelope = envelope;
        if (target != null) {
            targets.add(target);
        }

	// Set convenience variable: do we have an integer number of cycles?
	intRepeatCount = 
	    (Math.rint(envelope.getRepeatCount()) == envelope.getRepeatCount());

	// Create internal Timer object
	TimerTarget timerTarget = new TimerTarget();
	timer = new Timer(cycle.getResolution(), timerTarget);
	timer.setInitialDelay(envelope.getBegin());

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
     * Constructor: this is a utility method that sets up reasonable defaults
     * for a simple timing sequence that will have no delay, will not repeat,
     * will hold the end value when it ends, will use a timer resolution 
     * of 10 milliseconds, and will run for the length of time specified
     * in the duration parameter.
     * @param duration The length of time that this will run, in milliseconds.
     * @param target TimingTarget object that will be called with
     * all timing events.
     */
    public TimingController(int duration, TimingTarget target) {
	this(new Cycle(duration, 10), 
             new Envelope(1, 0, Envelope.RepeatBehavior.FORWARD, 
                          Envelope.EndBehavior.HOLD), 
             target);
    }
    
    /**
     * Sets the fraction of the timing cycle that will be spent accelerating
     * at the beginning. The default acceleration value is 0 (no acceleration).
     * @param acceleration value from 0 to 1
     * @throws IllegalArgumentException acceleration value must be between 0 and
     * 1, inclusive. 
     * @throws IllegalArgumentException acceleration cannot be greater than
     * (1 - deceleration)
     * @see #setDeceleration(float)
     */
    public void setAcceleration(float acceleration) {
        if (acceleration < 0 || acceleration > 1.0f) {
            throw new IllegalArgumentException("Acceleration value cannot lie" +
                    " outside [0,1] range");
        }
        if (acceleration > (1.0f - deceleration)) {
            throw new IllegalArgumentException("Acceleration value cannot be" +
                    " greater than (1 - deceleration)");
        }
        this.acceleration = acceleration;
    }
    
    /**
     * Sets the fraction of the timing cycle that will be spent decelerating
     * at the end. The default deceleration value is 0 (no deceleration).
     * @param deceleration value from 0 to 1
     * @throws IllegalArgumentException deceleration value must be between 0 and
     * 1, inclusive. 
     * @throws IllegalArgumentException deceleration cannot be greater than
     * (1 - acceleration)
     * @see #setAcceleration(float)
     */
    public void setDeceleration(float deceleration) {
        if (deceleration < 0 || deceleration > 1.0f) {
            throw new IllegalArgumentException("Deceleration value cannot lie" +
                    " outside [0,1] range");
        }
        if (deceleration > (1.0f - acceleration)) {
            throw new IllegalArgumentException("Deceleration value cannot be" +
                    " greater than (1 - acceleration)");
        }
        this.deceleration = deceleration;
    }
    
    public float getAcceleration() {
        return acceleration;
    }
    
    public float getDeceleration() {
        return deceleration;
    }
    
    /**
     * Adds a TimingTarget to the list of targets that get notified of each
     * timingEvent.  This can be done at any time before, during, or after the
     * timing envelope has started or completed; the new target will begin
     * having its TimingTarget implementations called as soon as it is added
     * to the list.
     */
    public void addTarget(TimingTarget target) {
        if (target != null) {
            synchronized (targets) {
                targets.add(target);
            }
        }
    }
    
    public void addTimingListener(TimingListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    public Cycle getCycle() {
        return cycle;
    }
    
    public Envelope getEnvelope() {
        return envelope;
    }
    
    public void setCycle(Cycle cycle) {
        this.cycle = cycle;
        timer.setDelay(cycle.getResolution());
    }
    
    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }
    
    /**
     * The constructor sets up everything, but start must be called to 
     * actually start the timer.
     */
    public void start() {
        begin();
	// Initialize start time variables to current time
	startTime = (System.nanoTime() / 1000000) + envelope.getBegin();
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
     * duration and repeatCount in the constructor.  But if the application 
     * wants to stop the timer mid-stream, this is the method to call.
     */
    public void stop() {
	timer.stop();
        end();
    }


    /**
     * There are two ways to receive timing events: either override
     * this method in a subclass of TimingController or create
     * a TimingController instance directly with a TimingTarget
     * object.
     * This default implementation calls the TimingTarget's timingEvent()
     * method if the target is non-null.
     * @param cycleElapsedTime the total time in milliseconds elapsed in
     * the current Cycle
     * @param totalElapsedTime the total time in milliseconds elapsed
     * since the start of the first cycle
     * @param fraction the fraction of completion between the start and
     * end of the current cycle.  Note that on reversing cycles
     * (<code>Envelope.RepeatBehavior.REVERSE</code>) the fraction decreases
     * from 1.0 to 0 on backwards-running cycles.
     * @see Envelope.RepeatBehavior
     * @see TimingTarget
     * @see #TimingController(Cycle, Envelope, TimingTarget)
     */
    public void timingEvent(long cycleElapsedTime,
			    long totalElapsedTime, 
			    float fraction)
    {
        synchronized (targets) {
            for (int i = 0; i < targets.size(); ++i) {
                TimingTarget target = (TimingTarget)targets.get(i);
                target.timingEvent(cycleElapsedTime, totalElapsedTime, fraction);
            }
        }
    }
    
    /**
     * Implementation of <code>TimingTarget.begin</code>.  Subclasses of
     * TimingController may want to do applicable custom setup here.
     */
    public void begin() {
        synchronized (targets) {
            for (int i = 0; i < targets.size(); ++i) {
                TimingTarget target = (TimingTarget)targets.get(i);
                target.begin();
            }
	}
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); ++i) {
                TimingListener listener = (TimingListener)listeners.get(i);
                listener.timerStarted(new TimingEvent(this));
            }
	}
    }
    
    /**
     * Implementation of <code>TimingTarget.end</code>.  Sublcasses of
     * TimingController may want to do applicable custom cleanup here.
     */
    public void end() {
        synchronized (targets) {
            for (int i = 0; i < targets.size(); ++i) {
                TimingTarget target = (TimingTarget)targets.get(i);
                target.end();
            }
	}
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); ++i) {
                TimingListener listener = (TimingListener)listeners.get(i);
                listener.timerStopped(new TimingEvent(this));
            }
	}
    }
    
    /**
     * Notify listeners that timer is about to repeat
     */
    protected void repeat() {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); ++i) {
                TimingListener listener = (TimingListener)listeners.get(i);
                listener.timerRepeated(new TimingEvent(this));
            }
	}
    }
    
    /**
     * This method calculates a new fraction value based on the
     * acceleration and deceleration settings of TimingController.
     * It then calls the real timingEvent() method with this
     * new value.
     */
    private void timingEventPreprocessor(TimingData timingData)
    {
        if (acceleration != 0 || deceleration != 0.0f) {
            // See the SMIL 2.0 specification for details on this
            // calculation
            float fraction = timingData.getFraction();
            float oldFraction = fraction;
            float runRate = 1.0f / (1.0f - acceleration/2.0f - 
                    deceleration/2.0f);
            if (fraction < acceleration) {
                float averageRunRate = runRate * (fraction / acceleration) / 2;
                fraction *= averageRunRate;
            } else if (fraction > (1.0f - deceleration)) {
                // time spent in deceleration portion
                float tdec = fraction - (1.0f - deceleration);
                // proportion of tdec to total deceleration time
                float pdec  = tdec / deceleration;
                fraction = runRate * (1.0f - ( acceleration / 2) -
                        deceleration + tdec * (2 - pdec) / 2);
            } else {
                fraction = runRate * (fraction - (acceleration / 2));
            }
            // clamp fraction to [0,1] since above calculations may
            // cause rounding errors
            if (fraction < 0) {
                fraction = 0;
            } else if (fraction > 1.0f) {
                fraction = 1.0f;
            }
            timingData.setFraction(fraction);
        }
    }
    
    /**
     * This method calculates and returns the TimingData information based
     * on the current time
     */
    public TimingData getTimingData() {
        TimingData timingData;
        long currentTime = System.nanoTime() / 1000000;
        // Calculate values that are passed into TimingController.timingEvent()
        long cycleElapsedTime = currentTime - currentStartTime;
        long totalElapsedTime = currentTime - startTime;
        double currentCycle = (double)totalElapsedTime / cycle.getDuration();
        float fraction;

        if ((envelope.getRepeatCount() != INFINITE) &&
            currentCycle >= envelope.getRepeatCount())
        {
            // Envelope done: stop based on end behavior
            switch (envelope.getEndBehavior()) {
            case HOLD:
                // Make sure we send a final end value
                if (intRepeatCount) {
                    // If supposed to run integer number of cycles, hold
                    // on integer boundary
                    if (direction == Direction.BACKWARD) {
                        // If we were traveling backward, hold on 0
                        fraction = 0.0f;
                    } else {
                        fraction = 1.0f;
                    }
                } else {
                    // hold on final value instead
                    fraction = Math.min(1.0f, 
                        ((float)cycleElapsedTime / cycle.getDuration()));
                }
                break;
            case RESET:
                // RESET requires setting the final value to the start value
                fraction = 0.0f;
                break;
            default:
                fraction = 0.0f;
                // should not reach here
                break;
            }
            stop();
        } else if ((cycle.getDuration() != INFINITE) && 
                   (cycleElapsedTime > cycle.getDuration()))
        {
            // Cycle end: Time to stop or change the behavior of the timer
            long actualCycleTime = cycleElapsedTime % cycle.getDuration();
            fraction = (float)actualCycleTime / cycle.getDuration();
            // Set new start time for this cycle
            currentStartTime = currentTime - actualCycleTime;

            if (envelope.getRepeatBehavior() == 
                Envelope.RepeatBehavior.REVERSE)
            {
                boolean oddCycles = 
                        ((int)(cycleElapsedTime / cycle.getDuration()) % 2)
                        > 0;
                if (oddCycles) {
                    // reverse the direction
                    direction = (direction == Direction.FORWARD) ? 
                            Direction.BACKWARD :
                            Direction.FORWARD;
                }
                if (direction == Direction.BACKWARD) {
                    fraction = 1.0f - fraction;
                }
            }
            repeat();
        } else {
            // mid-stream: calculate fraction of animation between
            // start and end times and send fraction to target
            fraction = 0.0f;
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
        }
        timingData = new TimingData(cycleElapsedTime, totalElapsedTime, 
                fraction);
        timingEventPreprocessor(timingData);
        return timingData;
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
            TimingData timingData = getTimingData();
            timingEvent(timingData.getCycleElapsedTime(),
                    timingData.getTotalElapsedTime(),
                    timingData.getFraction());
	}
    }

}
