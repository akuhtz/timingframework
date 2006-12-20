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

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import org.jdesktop.animation.timing.*;

/**
 * ComponentFocusTrigger handles focus events for a given Component
 * and performs the appropriate TriggerAction based on those events.
 *
 * @author Chet
 */
public class ComponentFocusTrigger extends Trigger {
    
    /**
     * Creates a new instance of ComponentFocusTrigger
     * 
     * @param timer the Animator that will perform the action
     * when the event occurs
     * @param source the Component that will be listened to for focus events
     * @param action the TriggerAction that will be fired on timer when
     * the event occurs
     * @param event the ComponentFocusEvent that will cause the action to fire
     */
    public ComponentFocusTrigger(Animator timer, Component source, 
            TriggerAction action, ComponentFocusEvent event) {
        setupListener(timer, source, action, event);
    }

    /**
     * Utility constructor that assumes: TriggerAction.START is the
     * action to fire upon the ComponentFocusEvent, TriggerAction.STOP
     * will be fired upon opposite the opposite ComponentFocusEvent, non-null
     * stopTimer will set up that animation to start and stop in
     * reverse order
     */
    public ComponentFocusTrigger(Animator startTimer, Component source, 
            ComponentFocusEvent event, Animator stopTimer) {
        super(startTimer, source, event, stopTimer);
    }
    
    protected void setupListener(Animator timer, Object source, 
            TriggerAction action, TriggerEvent event) {
        try {
            listener = new ComponentFocusListener(timer, action, 
                    (ComponentFocusEvent)event);
            setupListener(source, listener, "addFocusListener", 
                    FocusListener.class);
        } catch (Exception e) {
            System.out.println("Exception creating " +
                "focus listener for object " + source + ": " + e);
        }
    }

    class ComponentFocusListener extends TriggerListener implements FocusListener {
        ComponentFocusEvent event;
        protected ComponentFocusListener(Animator timer, 
                TriggerAction action, ComponentFocusEvent event) {
            super(timer, action);
            this.event = event;
        }
        public void focusGained(FocusEvent e) {
            if (event == ComponentFocusEvent.FOCUS_IN) {
                pullTrigger();
            }
        }
        public void focusLost(FocusEvent e) {
            if (event == ComponentFocusEvent.FOCUS_OUT) {
                pullTrigger();
            }
        }
    }
    
}