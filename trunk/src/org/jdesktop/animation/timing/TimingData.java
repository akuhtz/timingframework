/**
 * Copyright (c) 2006, Sun Microsystems, Inc
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

/**
 * Simple class to hold data for timing events (elapsed time and fraction)
 *
 * @author Chet
 */
public class TimingData {
    
    private long cycleElapsedTime;
    private long totalElapsedTime;
    private float fraction;
    
    /** Creates a new instance of TimingData */
    public TimingData(long cycleElapsedTime,
			    long totalElapsedTime, 
			    float fraction) {
        this.cycleElapsedTime = cycleElapsedTime;
        this.totalElapsedTime = totalElapsedTime;
        this.fraction = fraction;
    }
    
    
    public void setCycleElapsedTime(long cycleElapsedTime) {
        this.cycleElapsedTime = cycleElapsedTime;
    }
    
    public long getCycleElapsedTime() {
        return cycleElapsedTime;
    }
    
    public void setTotalElapsedTime(long totalElapsedTime) {
        this.totalElapsedTime = totalElapsedTime;
    }
    
    public long getTotalElapsedTime() {
        return totalElapsedTime;
    }
    
    public void setFraction(float fraction) {
        this.fraction = fraction;
    }
    
    public float getFraction() {
        return fraction;
    }
}
