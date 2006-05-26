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

package org.jdesktop.animation.timing.triggers;

/**
 * @author Chet
 */
public class ButtonStateEvent extends TriggerEvent {
    /** Event fired when Button becomes Armed */
    public static final ButtonStateEvent ARMED = 
            new ButtonStateEvent("Armed");
    /** Event fired when Button becomes Disarmed */
    public static final ButtonStateEvent DISARMED = 
            new ButtonStateEvent("Disarmed");
    /** Event fired when Button gets rollover state */
    public static final ButtonStateEvent ROLLOVER = 
            new ButtonStateEvent("Rollover");
    /** Event fired when Button loses rollover state */
    public static final ButtonStateEvent ROLLOFF = 
            new ButtonStateEvent("Rolloff");

    protected ButtonStateEvent(String name) {
        super(name);
    }

    /**
     * This method finds the opposite of the current event.: ARMED ->
     * DISARMED, DISARMED -> ARMED, ROLLOVER -> ROLLOFF, ROLLOFF ->
     * ROLLOVER.
     */
    public TriggerEvent getOppositeEvent() {
        if (this.equals(ButtonStateEvent.ARMED)) {
            return ButtonStateEvent.DISARMED;
        } else if (this.equals(ButtonStateEvent.DISARMED)) {
            return ButtonStateEvent.ARMED;
        } else if (this.equals(ButtonStateEvent.ROLLOVER)) {
            return ButtonStateEvent.ROLLOFF;
        } else if (this.equals(ButtonStateEvent.ROLLOFF)) {
            return ButtonStateEvent.ROLLOVER;
        }
        // Should not reach here
        return this;
    }   
}
