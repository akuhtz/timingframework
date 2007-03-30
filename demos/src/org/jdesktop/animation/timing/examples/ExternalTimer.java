/**
 * Copyright (c) 2007, Sun Microsystems, Inc
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

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingSource;
import org.jdesktop.animation.timing.TimingTarget;

/**
 *
 * @author Chet
 */
public class ExternalTimer extends JComponent implements TimingTarget {
    
    int numEvents = 0;
    final static int DURATION = 1000;
    
    public ExternalTimer() {
        Animator defaultAnim = new Animator(DURATION, this);
        defaultAnim.setStartDelay(1000);
        defaultAnim.start();
        
        Animator externalTimerAnim = new Animator(DURATION, this);
        externalTimerAnim.setTimer(new MyTimingSource());
        externalTimerAnim.setStartDelay(3000);
        externalTimerAnim.setResolution(0);
        externalTimerAnim.start();

        externalTimerAnim = new Animator(DURATION, this);
        externalTimerAnim.setTimer(new MyTimingSource());
        externalTimerAnim.setStartDelay(6000);
        externalTimerAnim.setResolution(20);
        externalTimerAnim.start();
    }
    
    private static void createAndShowGUI() {
        JFrame f = new JFrame("Timing Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new ExternalTimer());
        f.setSize(300, 300);
        f.setVisible(true);
        
    }

    public void begin() {
        numEvents = 0;
    }
    public void end() {
        System.out.println("resolution = " + ((float)DURATION / numEvents));
    }
    
    public void repeat() {}
    
    public void timingEvent(float fraction) {
        //System.out.println("Thread = " + Thread.currentThread());
        numEvents++;
        repaint();
    }
    
    class MyTimingSource extends TimingSource implements Runnable {
        Thread t = null;
        int delay;
        int resolution;
        boolean stopped = false;
        
        MyTimingSource() {
            t = new Thread(this);
        }
        public void setStartDelay(int delay) {
            this.delay = delay;
        }
        public void start() {
            t.start();
        }

        public void stop() {
            stopped = true;
        }

        public void setResolution(int resolution) {
            this.resolution = resolution;
        }
        
        private void sleep(int duration) {
            try {
                Thread.sleep(duration);
            } catch (Exception e) {}
        }
        
        public void run() {
            sleep(delay);
            while (!stopped) {
                sleep(resolution);
                timingEvent();
            }
        }
        
    }
    
    public void paintComponent(Graphics g) {
        g.drawString("numEvents = " + numEvents, 20, 20);
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
