
package timing;

public class Cycle {
    private int duration;
    private int resolution;

    /**
     * Constructs the Cycle object for use in creating a 
     * TimingController object.
     * @param duration number of milliseconds that the cycle
     * should last.  This can be any positive integer value or
     * TimingController.INFINITE to represent an unending cycle.
     * @param resolution number of milliseconds between calls to
     * TimingController.timingEvent().
     * @see TimingController
     * @see TimingController#INFINITE
     * @see TimingController#timingEvent
     * @exception IllegalArgumentException if any parameters have invalid
     * values
     */
    public Cycle(int duration, int resolution) {
	// First, check for bad parameters
	if ((duration != TimingController.INFINITE && duration < 0) ||
	    (resolution < 0))
	{
	    String errorMessage = "Errors: ";
	    if (duration != TimingController.INFINITE && duration < 0) {
		errorMessage += "duration " + duration + 
				" cannot be negative\n";
	    }
	    if (resolution < 0) {
		errorMessage += "resolution " + resolution + 
				" cannot be negative\n";
	    }
            throw new IllegalArgumentException(errorMessage);
	}
	this.duration = duration;
	this.resolution = resolution;
    }

    public int getResolution() {
	return resolution;
    }

    public int getDuration() {
	return duration;
    }
}
