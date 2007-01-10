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

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.animation.timing.*;

/**
 * StateTrigger allows callers to start an animation based on
 * changes in the armed and rollover states of a specified
 * button.
 * For example, to have anim start when a button receives a 
 * ROLLOVER event, one might write the following:
 * <pre>
 *     StateTrigger trigger = 
 *         StateTrigger.createTrigger(anim, button, StateTriggerEvent.ROLLOVER);
 *     button.addChangeListener(trigger);
 * </pre>
 * 
 * 
 * @author Chet
 */
public class StateTrigger extends Trigger implements ChangeListener {
    
    private boolean armed;
    private boolean rollover;
    
    /**
     * Creates a non-auto-reversing StateTrigger, which should be added to
     * a button that will generate the ChangeEvents listened to by this trigger.
     * 
     * @param animator the Animator that will start when the event occurs
     * @param source the AbstractButton that holds the current state of the
     * button that will be listened to. This button will be used to record
     * the current state so that future changes to that state will be
     * correctly identified.
     * @param event the StateTriggerEvent that will cause the action to fire
     * @return StateTrigger the resulting trigger
     */
    public static StateTrigger createTrigger(Animator animator, 
            AbstractButton source, StateTriggerEvent event) {
        return new StateTrigger(animator, source, event, false);
    }
    
    /**
     * Creates a StateTrigger, which should be added to
     * a button that will generate the ChangeEvents listened to by this trigger.
     * 
     * @param animator the Animator that will start when the event occurs
     * @param source the AbstractButton that holds the current state of the
     * button that will be listened to. This button will be used to record
     * the current state so that future changes to that state will be
     * correctly identified.
     * @param event the StateTriggerEvent that will cause the action to fire
     * @param autoReverse flag to determine whether the animator should
     * stop and reverse based on opposite triggerEvents
     * @return StateTrigger the resulting trigger
     */
    public static StateTrigger createTrigger(Animator animator, 
            AbstractButton source,  StateTriggerEvent event, 
            boolean autoReverse) {
        return new StateTrigger(animator, source, event, autoReverse);
    }

    /**
     * Private constructor that does the work of the factory methods
     */
    private StateTrigger(Animator animator, AbstractButton source, 
            StateTriggerEvent event, boolean autoReverse) {
        super(animator, event, autoReverse);
        ButtonModel model = source.getModel();
        armed = model.isArmed();
        rollover = model.isRollover();
    }
    
    /**
     * Called by button that added this Trigger as a ChangeListener.
     * Starts animation based on whether the relevant StateTriggerEvent
     * event was received.
     */
    public void stateChanged(ChangeEvent ae) {
        ButtonModel model = ((AbstractButton)ae.getSource()).getModel();
        boolean armedChange = (armed != model.isArmed());
        boolean rolloverChange = (rollover != model.isRollover());
        if (armedChange) {
            if (model.isArmed()) {
                fire(StateTriggerEvent.ARMED);
            } else {
                fire(StateTriggerEvent.DISARMED);
            }
            armed = model.isArmed();
        }
        if (rolloverChange) {
            if (model.isRollover()) {
                fire(StateTriggerEvent.ROLLOVER);
            } else {
                fire(StateTriggerEvent.ROLLOFF);
            }
            rollover = model.isRollover();
        }
    }
    
}
