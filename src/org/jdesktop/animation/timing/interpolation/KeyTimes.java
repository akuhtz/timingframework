/*
 * KeyTimes.java
 *
 * Created on February 8, 2006, 4:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.animation.timing.interpolation;

import java.util.ArrayList;
import org.jdesktop.animation.timing.*;

/**
 * Stores a list of times from 0 to 1 that are used in calculating interpolated
 * values for ObjectModifier given a matching set of KeyValues and
 * possibly KeySplines for those times.  In the simplest case, a
 * KeyFrame will consist of just two times in KeyTimes: 0 and 1.
 *
 * @author Chet
 */
public class KeyTimes {
    
    private ArrayList<Float> times = new ArrayList<Float>();
    
    /** 
     * Creates a new instance of KeyTimes.  Times should be in increasing
     * order and should all be in the range [0,1], with the first value
     * being zero and the last being 1
     * @throws IllegalArgumentException Time values must be ordered in
     * increasing value, the first value must be 0 and the last value
     * must be 1
     */
    public KeyTimes(float... times) {
        if (times[0] != 0) {
            throw new IllegalArgumentException("First time value must" +
                    " be zero");
        }
        if (times[times.length - 1] != 1.0f) {
            throw new IllegalArgumentException("Last time value must" +
                    " be one");
        }
        float prevTime = 0;
        for (float time : times) {
            if (time < prevTime) {
                throw new IllegalArgumentException("Time values must be" +
                        " in increasing order");
            }
            this.times.add(time);
            prevTime = time;
        }
    }
    
    ArrayList getTimes() {
        return times;
    }
    
    public int getSize() {
        return times.size();
    }

    /**
     * Returns time interval that contains this time fraction
     */
    public int getInterval(float fraction) {
        int prevIndex = 0;
        for (int i = 1; i < times.size(); ++i) {
            float time = times.get(i);
            if (time >= fraction) { 
                // inclusive of start time at next interval.  So fraction==1
                // will return the final interval (times.size() - 1)
                return prevIndex;
            }
            prevIndex = i;
        }
        return prevIndex;
    }

    public float getTime(int index) {
        return times.get(index);
    }
}