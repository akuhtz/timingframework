/**
 * Envelope
 * 
 * This object holds the values that describe the overall TimingController
 * sequence.  The Cycle class holds the values that determine what 
 * individual timing cycles are like; this class determines how those
 * cycles are put together with other values for the entire sequence.
 */

package timing;

public class Envelope {

    /**
     * EndBehavior determines what happens at the end of the Envelope.
     * HOLD means the timing sequence should maintain its final value
     * at the end, RESET means the timing sequence should reset to the
     * initial value at the end.
     */
    public enum EndBehavior {
	HOLD,
	RESET,
    };

    /**
     * RepeatBehavior determines how each successive cycle will flow.
     * A cycle can either go FORWARD, in which case each cycle starts
     * at the beginning and goes to the end, or it can go REVERSE,
     * in which case each cycle starts at the final value of the last
     * cycle and flows in the opposite direction.
     */
    public enum RepeatBehavior {
	FORWARD,
	REVERSE,
    };
    
    // Private variables to hold the internal values
    private double numCycles;
    private int startDelay;
    private RepeatBehavior repeatBehavior;
    private EndBehavior endBehavior;


    /**
     * Constructs the Envelope object for use in creating a 
     * TimingController object.
     * @param numCycles fractional value representing the number of
     * cycles that should be run before this Envelope ends.  Value
     * can be any positive double value or TimingController.INIFINITE
     * to representing an unending Envelope.
     * @param startDelay number of milliseconds to delay before starting
     * the first cycle.
     * @param repeatBehavior RepeatBehavior of each successive
     * cycle.
     * @param endBehavior EndBehavior at the conclusion of this
     * Envelope.
     * @see TimingController
     * @see TimingController#INFINITE
     * @see RepeatBehavior
     * @see EndBehavior
     * @exception IllegalArgumentException if any parameters have invalid
     * values
     */
    public Envelope(double numCycles, int startDelay, 
		    RepeatBehavior repeatBehavior,
		    EndBehavior endBehavior)
    {
	// First, check for bad parameters
	if ((startDelay < 0) ||
	    (endBehavior == null) ||
	    ((numCycles != TimingController.INFINITE && numCycles <= 0)))
	{
	    String errorMessage = "Errors: ";
	    if (startDelay < 0) {
		errorMessage += "startDelay " + startDelay + 
				" cannot be negative\n";
	    }
	    if (endBehavior == null) {
		errorMessage += "endBehavior cannot be null\n";
	    }
	    if ((numCycles != TimingController.INFINITE && numCycles <= 0))
	    {
		errorMessage += "cannot have zero or negative value of " +
				"numCycles (" + numCycles + ")\n";
	    }
            throw new IllegalArgumentException(errorMessage);
	}
	this.numCycles = numCycles;
	this.startDelay = startDelay;
	this.repeatBehavior = repeatBehavior;
	this.endBehavior = endBehavior;
    }

    public double getNumCycles() {
	return numCycles;
    }

    public int getStartDelay() {
	return startDelay;
    }

    public RepeatBehavior getRepeatBehavior() {
	return repeatBehavior;
    }

    public EndBehavior getEndBehavior() {
	return endBehavior;
    }
}
