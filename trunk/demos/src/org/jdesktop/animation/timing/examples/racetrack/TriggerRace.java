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

package org.jdesktop.animation.timing.examples.racetrack;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.jdesktop.animation.timing.TimingController;
import org.jdesktop.animation.timing.interpolation.ObjectModifier;
import org.jdesktop.animation.timing.interpolation.PropertyRange;
import org.jdesktop.animation.timing.triggers.ActionTrigger;
import org.jdesktop.animation.timing.triggers.Trigger;
import org.jdesktop.animation.timing.triggers.Trigger.TriggerAction;

/**
 * Exactly like SetterRace, only this version uses Triggers to
 * start/stop the animation automatically based on the user
 * clicking the Go/Stop buttons (no need for an ActionListener here)
 *
 * @author Chet
 */
public class TriggerRace {
    
    public static final int RACE_TIME = 2000;

    /** Creates a new instance of TriggerRace */
    public TriggerRace(String appName) {
        RaceGUI basicGUI = new RaceGUI(appName);
        
        // Now set up an animation that will automatically
        // run itself with ObjectModifier
        
        PropertyRange range = PropertyRange.
                createPropertyRangePoint("carPosition", 
                TrackView.START_POS, TrackView.FIRST_TURN_START);
        ObjectModifier modifier = new ObjectModifier(basicGUI.getTrack(), range);
        TimingController timer = new TimingController(RACE_TIME, modifier);
        JButton goButton = basicGUI.getControlPanel().getGoButton();
        JButton stopButton = basicGUI.getControlPanel().getStopButton();
        
        // Instead of manually tracking the events, have the framework do
        // the work by setting up a trigger
        Trigger trigger = new ActionTrigger(timer, goButton, TriggerAction.START);
        trigger = new ActionTrigger(timer, stopButton, TriggerAction.STOP);
    }
    
    public static void main(String args[]) {
        Runnable doCreateAndShowGUI = new Runnable() {
            public void run() {
                TriggerRace race = new TriggerRace("Trigger Race");
            }
        };
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
}
