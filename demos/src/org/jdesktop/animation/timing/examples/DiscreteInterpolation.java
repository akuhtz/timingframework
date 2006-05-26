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

package org.jdesktop.animation.timing.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.animation.timing.TimingController;
import org.jdesktop.animation.timing.interpolation.KeyFrames;
import org.jdesktop.animation.timing.interpolation.KeyTimes;
import org.jdesktop.animation.timing.interpolation.KeyValues;
import org.jdesktop.animation.timing.interpolation.ObjectModifier;
import org.jdesktop.animation.timing.interpolation.PropertyRange;
import org.jdesktop.animation.timing.triggers.ActionTrigger;
import org.jdesktop.animation.timing.triggers.Trigger.TriggerAction;

/**
 * This is a simple demonstration of discrete interpolation, where the
 * animation simple switches between several strings to display in the
 * window.  A ActionTrigger is used to start the animation based on
 * the user clicking the Go button.
 *
 * @author Chet
 */
public class DiscreteInterpolation extends JComponent {
    
    String strings[] = {"Zero", "One", "Two", "Three", "Four", "Five", "Six"};
    private int stringIndex = 0;
    private float segmentSeconds[] = {.5f, 2.0f, 1.0f, .2f, 2.0f, 1.0f};
    
    /** Creates a new instance of DiscreteInterpolation */
    public DiscreteInterpolation() {
        JFrame f = new JFrame();
        f.setLayout(new BorderLayout());
        f.setSize(500, 500);
        JPanel panel = new JPanel();
        f.add(panel, BorderLayout.NORTH);
        JButton button = new JButton("Go");
        setupAnimation(button);
        panel.add(button);
        f.add(this, BorderLayout.CENTER);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void setupAnimation(JButton button){
        // Total up the segment times
        float totalTime = 0.0f;
        for (int i = 0; i < segmentSeconds.length; ++i) {
            totalTime += segmentSeconds[i];
        }
        
        // Set up KeyTimes according to cumulative proportion of total
        float times[] = new float[strings.length];
        times[0] = 0.0f;
        for (int i = 1; i < times.length; ++i) {
            times[i] = segmentSeconds[i-1] / totalTime + times[i-1];
        }
        KeyTimes keyTimes = new KeyTimes(times);
        
        // keyValues are just progressive index values
        int vals[] = {0, 1, 2, 3, 4, 5, 6};
        KeyValues keyValues = KeyValues.createKeyValues(vals);
        
        // Create KeyFrames with times, values, and DISCRETE interpolation
        KeyFrames keyFrames = new KeyFrames(keyValues, keyTimes,
                KeyFrames.InterpolationType.DISCRETE);
        
        // Property setter will use stringIndex property of this class
        PropertyRange range = new PropertyRange("stringIndex", keyFrames);
        ObjectModifier modifier = new ObjectModifier(this, range);
        
        // Run the timer for totalTime (in ms), with modifier as the
        // TimingTarget
        TimingController timer = new TimingController(
                (int)(totalTime * 1000), modifier);
        
        // Now set up Trigger to start animation
        new ActionTrigger(timer, button, TriggerAction.START);
    }
    
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.drawString(strings[stringIndex], 100, 100);
    }
    
    public void setStringIndex(int i) {
        stringIndex = i;
        repaint();
    }
    
    private static void createAndShowGUI() {
        new DiscreteInterpolation();
    }
    
    public static void main(String args[]) {
        Runnable doCreateAndShowGUI = new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        };
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
    
}
