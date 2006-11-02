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
import java.util.EventListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.animation.timing.*;

/**
 *
 * @author Chet
 */
public class ButtonStateTrigger extends Trigger {
    
    /**
     * Creates a new instance of ButtonStateTrigger
     * 
     * @param timer the Animator that will perform the action
     * when the event occurs
     * @param source the AbstractButton that will be listened to for 
     * change events
     * @param action the TriggerAction that will be fired on timer when
     * the event occurs
     * @param event the ButtonStateEvent that will cause the action to fire
     */
    public ButtonStateTrigger(Animator timer, AbstractButton source, 
            TriggerAction action, ButtonStateEvent event) {
        setupListener(timer, source, action, event);
    }

    /**
     * Utility constructor that assumes: TriggerAction.START is the
     * action to fire upon the ButtonStateEvent, TriggerAction.STOP
     * will be fired upon opposite ButtonStateEvent, non-null
     * stopTimer will set up that animation to start and stop on
     * opposite events.
     */
    public ButtonStateTrigger(Animator startTimer, AbstractButton source, 
            ButtonStateEvent event, Animator stopTimer) {
        super(startTimer, source, event, stopTimer);
    }
    
    protected void setupListener(Animator timer, Object source, 
            TriggerAction action, TriggerEvent event) {
        try {
            ButtonModel model = ((AbstractButton)source).getModel();
            listener = new ButtonStateListener(timer, action, 
                    (ButtonStateEvent)event, model);
            setupListener(model, listener, "addChangeListener", 
                    ChangeListener.class);
        } catch (Exception e) {
            System.out.println("Exception creating " +
                "change listener for object " + source + ": " + e);
        }
    }

    class ButtonStateListener extends TriggerListener 
            implements ChangeListener {
        boolean armed;
        boolean rollover;
        ButtonStateEvent event;
        protected ButtonStateListener(Animator timer, 
                TriggerAction action, ButtonStateEvent event,
                ButtonModel model) {
            super(timer, action);
            this.event = event;
            armed = model.isArmed();
            rollover = model.isRollover();
        }
        
        public void stateChanged(ChangeEvent ae) {
            ButtonModel model = (ButtonModel)ae.getSource();
            boolean armedChange = (armed != model.isArmed());
            boolean rolloverChange = (rollover != model.isRollover());
            if (armedChange) {
                if ((event == ButtonStateEvent.ARMED && model.isArmed()) ||
                        (event == ButtonStateEvent.DISARMED && !model.isArmed())) {
                    pullTrigger();
                }
                armed = model.isArmed();
            }
            if (rolloverChange) {
                if ((event == ButtonStateEvent.ROLLOVER && model.isRollover()) ||
                        (event == ButtonStateEvent.ROLLOFF && !model.isRollover())) {
                    pullTrigger();
                }
                rollover = model.isRollover();
            }
        }
    }
    
}
