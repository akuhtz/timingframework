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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import javax.swing.AbstractButton;
import org.jdesktop.animation.timing.*;

/**
 * ActionTrigger handles action events on a given Object and
 * fires the appropriate TriggerAction when actions occur.
 *
 * @author Chet
 */
public class ActionTrigger extends Trigger {

    /**
     * Creates a new instance of ActionTrigger 
     * 
     * @param timer the Animator that will perform the action
     * when the event occurs
     * @param source the Object that will be listened to for ActionEvents;
     * this must be an object that has an addActionListener() method on it
     * @param action the TriggerAction that will be fired on timer when
     * the event occurs
     */
    public ActionTrigger(Animator timer, Object source, 
            TriggerAction action) {
        setupListener(timer, source, action, null);
    }
    
    protected void setupListener(Animator timer, Object source, 
            TriggerAction action, TriggerEvent event) {
        try {
            listener = new ActionTriggerListener(timer, action);
            setupListener(source, listener, "addActionListener",
                    ActionListener.class);
        } catch (Exception e) {
            System.out.println("Exception creating " +
                "action listener for object " + source + ": " + e);
        }
    }

    class ActionTriggerListener extends TriggerListener 
            implements ActionListener {
        protected ActionTriggerListener(Animator timer, 
                TriggerAction action) {
            super(timer, action);
        }
        public void actionPerformed(ActionEvent ae) {
            pullTrigger();
        }
    }
    
}
