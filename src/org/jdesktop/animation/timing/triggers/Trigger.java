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

import java.lang.reflect.Method;
import java.util.EventListener;
import org.jdesktop.animation.timing.*;

/**
 * This abstract class should be overridden by any class wanting to
 * implement a new Trigger.  The subclass will define the events to trigger
 * off of and the listener to handle those events, and will implement
 * the abstract setupListener() method to handle the listener-specific
 * details.
 *
 * @author Chet
 */
public abstract class Trigger {
  
    /**
     * A Trigger can be set up to either start or stop a given TimingController
     */
    public enum TriggerAction {
        START,
        STOP
    };
    

    public Trigger() {}
    
    /**
     * This constructor sets up animations to auto-start/auto-stop
     * based on the start/stop events.  The first set of parameters
     * declare the timer to start based on the source/event, the
     * second set or parameters declare the timer to start based on
     * the second source/event.  The first timer will be auto-stopped
     * when the second source/event occurs, and vice versa.
     */
    public Trigger(TimingController startTimer, Object source, 
            TriggerEvent event, TimingController stopTimer) {
        setupListener(startTimer, source, TriggerAction.START, event);
        setupListener(startTimer, source, TriggerAction.STOP, 
                event.getOppositeEvent());
        if (stopTimer != null) {
            setupListener(stopTimer, source, TriggerAction.START, 
                    event.getOppositeEvent());
            setupListener(stopTimer, source, TriggerAction.STOP, event);
        }
    }
    
    /**
     * This method must be implemented by subclasses, which will create
     * the appropriate EventListener object and call Trigger.setupListener()
     * to do the rest.
     */
    abstract protected void setupListener(TimingController timer, 
                Object source, TriggerAction action, TriggerEvent event);
    
    /**
     * Utility method called by subclass' setupListener() implementation;
     * after creating the appropriate EventListener object, the
     * subclass calls this method with that listener object to create the
     * Method reference and add the listener.
     */
    protected void setupListener(Object object, EventListener listener, 
            String methodName, Class<? extends EventListener> listenerClass) 
            throws NoSuchMethodException, IllegalAccessException, 
            java.lang.reflect.InvocationTargetException
    {
        Method addListenerMethod = object.getClass().getMethod(methodName,
                listenerClass);
        addListenerMethod.invoke(object, listener);
    }    
}
