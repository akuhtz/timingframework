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

/**
 * TimingTarget
 * <p>
 * This interface provides a single method, timingEvent(), which
 * is called by TimingController for all timing events.  Applications
 * that wish to receive timing events will either create a subclass
 * of TimingController and override timingEvent() or they can create
 * an implementation of TimingTarget and pass that into the constructor
 * of TimingController.
 */
public interface TimingTarget {
    /**
     * Implementors will override this method and pass the TimingTarget
     * into the TimingController constructor in order to receive timing events.
     * @param cycleElapsedTime the total time in milliseconds elapsed in
     * the current Cycle
     * @param totalElapsedTime the total time in milliseconds elapsed
     * since the start of the first cycle
     * @param fraction the fraction of completion between the start and
     * end of the current cycle.  Note that on reversing cycles
     * (<code>Envelope.RepeatBehavior.REVERSE</code>) the fraction decreases
     * from 1.0 to 0 on backwards-running cycles.
     * @see Envelope.RepeatBehavior
     * @see TimingController#timingEvent
     */
    public void timingEvent(long cycleElapsedTime,
			    long totalElapsedTime, 
			    float fraction);
}
