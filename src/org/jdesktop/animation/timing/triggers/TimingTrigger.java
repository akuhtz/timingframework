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

import org.jdesktop.animation.timing.*;

/**
 * TimingTrigger handles timing events and fires the appropriate
 * TriggerAction when those events occur.
 *
 * @author Chet
 */
public class TimingTrigger extends Trigger {
    /** Creates a new instance of TimingTrigger
     * @param timer the TimingController that will perform the action
     * when the event occurs
     * @param source the TimingController object that will be listened to
     * for timing events
     * @param action the TriggerAction that will be fired on timer when
     * the event occurs
     * @param event the TimingTriggerEvent that will cause the action
     * to be fired
     */
    public TimingTrigger(TimingController timer, TimingController source, 
            TriggerAction action, TimingTriggerEvent event) {
        setupListener(timer, source, action, event);
    }

    /**
     * Utility constructor that assumes: TriggerAction.START is the
     * action to fire upon the TimingTriggerEvent, TriggerAction.STOP
     * will be fired upon opposite the opposite TimingTriggerEvent, non-null
     * stopTimer will set up that animation to start and stop in
     * reverse order
     */
    public TimingTrigger(TimingController startTimer, TimingController source, 
            TimingTriggerEvent event, TimingController stopTimer) {
        super(startTimer, source, event, stopTimer);
    }
    
    protected void setupListener(TimingController timer, Object source, 
            TriggerAction action, TriggerEvent event) {
        try {
            TimingTriggerListener listener = new 
                TimingTriggerListener(timer, action, (TimingTriggerEvent)event);
            setupListener(source, listener, "addTimingListener", 
                    TimingListener.class);
        } catch (Exception e) {
            System.out.println("Exception creating " +
                "timing listener for object " + source + ": " + e);
        }
    }

    class TimingTriggerListener extends TriggerListener 
            implements TimingListener {
        TimingTriggerEvent event;
        protected TimingTriggerListener(TimingController timer, 
                TriggerAction action, TimingTriggerEvent event) {
            super(timer, action);
            this.event = event;
        }
        public void timerStarted(TimingEvent e) {
            if (event == TimingTriggerEvent.START) {
                pullTrigger();
            }
        }
        public void timerStopped(TimingEvent e) {
            if (event == TimingTriggerEvent.STOP) {
                pullTrigger();
            }
        }
        public void timerRepeated(TimingEvent e) {
            if (event == TimingTriggerEvent.REPEAT) {
                pullTrigger();
            }
        }
    }    
}
