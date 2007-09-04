/*
 * Canceler.java
 *
 * Created on April 14, 2007, 8:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.animation.timing.examples;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.Animator.RepeatBehavior;
import org.jdesktop.animation.timing.TimingTarget;

/**
 *
 * @author Chet
 */
public class Canceler extends JComponent implements MouseListener, TimingTarget {
    
    float animFraction = 0f;
    static Animator animator = null;
    /** Creates a new instance of Canceler */
    public Canceler() {
        animator = new Animator(5000, Animator.INFINITE, 
            RepeatBehavior.LOOP, this);
    }

    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawString("fraction: " + animFraction, 50, 50);
    }
    
    private static void createAndShowGUI() {
        JFrame f = new JFrame("Canceler");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        f.setSize(200, 100);
        Canceler canceler = new Canceler();
        canceler.addMouseListener(canceler);
        f.add(canceler);
        f.setVisible(true);
    }

    public void timingEvent(float fraction) {
        animFraction = fraction;
        repaint();
    }
    public void begin() {}
    public void end() {}
    public void repeat() {}
    
    public void mouseClicked(MouseEvent me) {
        animator.cancel();
        animator.start();
    }
    
    public void mousePressed(MouseEvent me) {}
    public void mouseReleased(MouseEvent me) {}
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}
    
    public static void main(String[] args) {
        // Need to do GUI stuff like making the JFrame visible on the
        // Event Dispatch Thread; do this via invokeLater()
        
        Runnable doCreateAndShowGUI = new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        };
        SwingUtilities.invokeLater(doCreateAndShowGUI);
        
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
            animator.cancel();
            animator.start();
        }
    }

}
