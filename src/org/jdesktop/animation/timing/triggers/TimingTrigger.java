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
 * TimingTrigger handles timing events and starts the animator
 * when those events occur. This class can be useful in sequencing different
 * Animators.  For example, one Animator can be set to start when another
 * ends using this Trigger.  For example, to have anim2 start when anim1 ends,
 * one might write the following:
 * <pre>
 *     TimingTrigger trigger = 
 *         TimingTrigger.createTrigger(anim2, TimingTriggerEvent.STOP);
 *     anim2.addTarget(trigger);
 * </pre>
 * 
 * 
 * 
 * @author Chet
 */
public class TimingTrigger extends Trigger implements TimingTarget {

    private Animator source;
    private TimingTriggerEvent event;
    
    /**
     * Creates a non-auto-reversing TimingTrigger, which should be added
     * to an Animator which will generate the events sent to the
     * trigger. The specified event will cause <code>targetAnimator</code> 
     * to start.
     * 
     * 
     * @param animator the Animator that will start when the event occurs
     * @param event the TimingTriggerEvent that will cause targetAnimator
     * to start
     * @return TimingTrigger the resulting trigger
     * @see org.jdesktop.animation.timing.Animator#addTarget(TimingTarget)
     */
    public static TimingTrigger createTrigger(Animator animator, 
            TimingTriggerEvent event) {
        return new TimingTrigger(animator, event, false);
    }

    /**
     * Creates a TimingTrigger, which should be added
     * to an Animator which will generate the events sent to the
     * trigger. The specified event will cause <code>targetAnimator</code> 
     * to start.
     * 
     * 
     * @param animator the Animator that will start when the event occurs
     * @param event the TimingTriggerEvent that will cause targetAnimator
     * to start
     * @param autoReverse flag to determine whether the animator should
     * stop and reverse based on opposite triggerEvents.
     * @return TimingTrigger the resulting trigger
     * @see org.jdesktop.animation.timing.Animator#addTarget(TimingTarget)
     */
    public static TimingTrigger createTrigger(Animator animator, 
            TimingTriggerEvent event, boolean autoReverse) {
        return new TimingTrigger(animator, event, autoReverse);
    }
    
    /**
     * Private constructor that does the work of the factory methods
     */
    private TimingTrigger(Animator animator,
            TimingTriggerEvent event, boolean autoReverse) {
        super(animator, event, autoReverse);
    }
    
    // 
    // TimingTarget implementation methods
    //
    
    /**
     * Implementation of TimingTarget method; this method does nothing
     * in this implementation since the events of TimingTrigger are limited
     * to START, STOP, and REPEAT
     */
    public void timingEvent(float fraction) {}
    
    /**
     * Called by Animator when starting. Sends the TimingTriggerEvent.START
     * event to the Trigger.
     */
    public void begin() {
        fire(TimingTriggerEvent.START);
    }
    
    /**
     * Called by Animator when ending. Sends the TimingTriggerEvent.STOP
     * event to the Trigger.
     */
    public void end() {
        fire(TimingTriggerEvent.STOP);
    }

    /**
     * Called by Animator when repeating. Sends the TimingTriggerEvent.REPEAT
     * event to the Trigger.
     */
    public void repeat() {
        fire(TimingTriggerEvent.REPEAT);
    }
}
