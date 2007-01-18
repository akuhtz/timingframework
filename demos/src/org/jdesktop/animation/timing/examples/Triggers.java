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
 *     copyright notice, this list of conditions and ttihe following
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

import java.awt.*;
import javax.swing.*;
import org.jdesktop.animation.timing.*;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.animation.timing.Animator.EndBehavior;
import org.jdesktop.animation.timing.triggers.FocusTriggerEvent;
import org.jdesktop.animation.timing.triggers.ActionTrigger;
import org.jdesktop.animation.timing.triggers.FocusTrigger;
import org.jdesktop.animation.timing.triggers.MouseTrigger;
import org.jdesktop.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.animation.timing.triggers.TimingTrigger;
import org.jdesktop.animation.timing.triggers.TimingTriggerEvent;

/**
 * Demonstrates simple animation effects triggered by events.  Events
 * trigger animated painting of some objects in the window (not the
 * buttons themselves, just to simplify the example code).
 * 
 * @author Chet
 */
public class Triggers extends JComponent {
    
    private int animatingValue = 0;
    
    /** Creates a new instance of Triggers */
    public Triggers() {
    }
    
    protected void paintComponent(Graphics g) {
        g.drawString(Integer.toString(animatingValue), 20, 100);
    }
    
    public void setAnimatingValue(int animatingValue) {
        this.animatingValue = animatingValue;
        repaint();
    }
    
    private static void createAndShowGUI() {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        JFrame f = new JFrame("Timing Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());
        
        // Create panel with buttons
        f.setSize(300, 300);
        Triggers panel = new Triggers();
        panel.setLayout(null);
        panel.setupButtons(panel);
        
        f.add(panel, BorderLayout.CENTER);
        f.setVisible(true);
    }
    
    private void setupButtons(JComponent panel) {
        Color inactiveColor = Color.lightGray;
        Color activeColor = Color.yellow;
        // Create and add them to the panel
        JButton button = new JButton("Trigger Button");
        panel.add(button);
        button.setBounds(0, 0, 150, 30);
        JButton otherButton = new JButton("Other Button");
        panel.add(otherButton);
        otherButton.setBounds(0, 40, 150, 30);
        
        // Create the labels where the effects will animate
        JLabel enter = new JLabel("Enter");
        enter.setBounds(200, 0, 100, 50);
        enter.setOpaque(true);
        enter.setBackground(inactiveColor);
        JLabel focus = new JLabel("Focus");
        focus.setBounds(200, 50, 100, 50);
        focus.setOpaque(true);
        focus.setBackground(inactiveColor);
        JLabel action = new JLabel("Action");
        action.setBounds(200, 100, 100, 50);
        action.setOpaque(true);
        action.setBackground(inactiveColor);
        JLabel press = new JLabel("Press");
        press.setBounds(200, 150, 100, 50);
        press.setOpaque(true);
        press.setBackground(inactiveColor);
        panel.add(enter);
        panel.add(focus);
        panel.add(action);
        panel.add(press);
        
        // Create a hover effect for button1
        Animator animator = PropertySetter.createAnimator(1000, enter, 
                "background", inactiveColor, activeColor);
        MouseTrigger mouseTrigger = MouseTrigger.addTrigger(button, animator,
                MouseTriggerEvent.ENTER, true);

        // Create a click effect
        animator = PropertySetter.createAnimator(1000, action, "background", 
                activeColor);
        animator.setEndBehavior(EndBehavior.RESET);
        ActionTrigger actionTrigger = ActionTrigger.addTrigger(button, animator);

        // Create a focus effect
        animator = PropertySetter.createAnimator(1000, focus, "background", 
                inactiveColor, activeColor);
        FocusTrigger focusTrigger = FocusTrigger.addTrigger(button, animator, 
                FocusTriggerEvent.IN, true);

        // Create a pressed effect
        animator = PropertySetter.createAnimator(1000, press, "background", 
                inactiveColor, activeColor);
        mouseTrigger = MouseTrigger.addTrigger(button, animator, 
                MouseTriggerEvent.PRESS, true);
        
        // Create a TimingTrigger sequence to animate animatingValue
        Animator animatePositive = PropertySetter.createAnimator(1000,
                this, "animatingValue", 0, 100);
        Animator animateNegative = PropertySetter.createAnimator(1000,
                this, "animatingValue", 0, -100);
        TimingTrigger trigger = TimingTrigger.addTrigger(animatePositive,
                animateNegative, TimingTriggerEvent.STOP);
        actionTrigger = ActionTrigger.addTrigger(button, animatePositive);
    }
    
    public static void main(String[] args) {
        // Need to do GUI stuff like making the JFrame visible on the
        // Event Dispatch Thread; do this via invokeLater()
        
        Runnable doCreateAndShowGUI = new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        };
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
    
}
