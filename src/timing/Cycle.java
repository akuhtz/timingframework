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
