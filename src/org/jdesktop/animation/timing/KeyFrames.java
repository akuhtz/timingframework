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

package org.jdesktop.animation.timing;

import java.lang.reflect.Method;

/**
 *
 * KeyFrames holds information about the times at which values are sampled
 * (KeyTimes) and the values at those times (KeyValues).  It also holds
 * information about how to interpolate between these values for
 * times that lie between the sampling points.
 *
 * @author Chet
 */
public class KeyFrames {
    
    /**
     * Type of interpolation used between each value in KeyValues.
     */
    public enum InterpolationType {
        /**
         * Interpolate linearly between each value
         */
        LINEAR,
        /**
         * Jump from value to value based on whether the timing fraction
         * has equalled or surpassed the new value.
         */
        DISCRETE,
        /**
         * Interpolate using the spline control points defined for each
         * key frame.
         */
        NONLINEAR
    };
    
    private KeyValues keyValues;
    private KeyTimes keyTimes;
    private KeySplines keySplines;
    private InterpolationType interpolationType;
    
    /** 
     * Simplest variation; determine keyTimes based on even division of
     * 0-1 range based on number of keyValues.  This constructor
     * assumes LINEAR interpolation.
     * @param keyValues values that will be assumed at each time in keyTimes
     */
    public KeyFrames(KeyValues keyValues) {
        init(keyValues, null, null, InterpolationType.LINEAR);
    }
    
    /**
     * Simple division of time into equal divisions according to the
     * number of values in keyValues, but this constructor also takes
     * an InterpolationType parameter to tell KeyFrames how to interpolate
     * between each value.
     */
    public KeyFrames(KeyValues keyValues, InterpolationType interpolationType) {
        init(keyValues, null, null, interpolationType);
    }
    
    /**
     * This variant takes both keyValues (values at each
     * point in time) and keyTimes (times at which values are sampled), in
     * addition to InterpolationType.
     * @param keyValues values that the animation will assume at each of the
     * corresponding times in keyTimes
     * @param keyTimes times at which the animation will assume the
     * corresponding values in keyValues
     * @param interpolationType how the animation will interpolate between the
     * values in keyValues.  This can be only DISCRETE or LINEAR in this 
     * constructor since NONLINEAR requires keySplines
     * @throws IllegalArgumentException A request for NONLINEAR interpolation 
     * will cause this exception because that interpolation type requires
     * keySplines.  
     * @throws IllegalArgumentException keyTimes and keySizes must have the
     * same number of elements since these structures are meant to have
     * corresponding entries; an exception is thrown otherwise.
     */
    public KeyFrames(KeyValues keyValues, KeyTimes keyTimes, 
            InterpolationType interpolationType) {
        init(keyValues, null, keyTimes, interpolationType);
    }
    
    /**
     * Full constructor: caller provides
     * an instance of all key* structures which will be used to calculate
     * between all times in the keyTimes list.  A null keySplines parameter
     * is equivalent to calling {@link KeyFrames#KeyFrames(KeyValues, KeyTimes,
     * InterpolationType) KeyFrames(KeyValues, KeyTimes, InterpolationType)},
     * where IntepolationType should not be NONLINEAR.  An interpolationType
     * value of anything except NONLINEAR will cause the keySplines parameter
     * to be ignored.
     * @param keyValues values that the animation will assume at each of the
     * corresponding times in keyTimes
     * @param keyTimes times at which the animation will assume the
     * corresponding values in keyValues
     * @param keySplines collection of Splines that control the interpolation
     * of the animation between the values in keyValues during the time
     * intervals between times in keyTimes.  The size of keySplines must
     * be equal to one less than the size of keyTimes/keyValues.
     * @param interpolationType how the animation will interpolate between the
     * values in keyValues.  This can only be NONLINEAR if keySplines is
     * not null
     * @throws IllegalArgumentException A request for NONLINEAR interpolation 
     * in the event of a null keySplines is not allowed
     * @throws IllegalArgumentException keyTimes and keySizes must have the
     * same number of elements since these structures are meant to have
     * corresponding entries; an exception is thrown otherwise.
     * @throws IllegalArgumentException keySplines must have a size equal to
     * one less than the size of keyTimes and keyValues since the splines must
     * have the right number of elements to perform interpolation in 
     * (keyTimes - 1) time intervals.
     */
    public KeyFrames(KeyValues keyValues, 
            KeySplines keySplines,
            KeyTimes keyTimes, 
            InterpolationType interpolationType) {
        init(keyValues, keySplines, keyTimes, interpolationType);
    }

    /**
     * Utility constructor that assumes even division of times according to
     * size of keyValues and NONLINEAR interpolation if keySplines is not null.
     * In the event of a null keySplines parameter, LINEAR interpolation is
     * assumed.
     * @param keyValues values that the animation will assume at each of the
     * corresponding times in keyTimes
     * @param keySplines collection of Splines that control the interpolation
     * of the animation between the values in keyValues during the time
     * intervals between times in keyTimes.  The size of keySplines must
     * be equal to one less than the size of keyTimes/keyValues.
     * @throws IllegalArgumentException keySplines must have a size equal to
     * one less than the size of keyValues since the splines must
     * have the right number of elements to perform interpolation in 
     * (sizeof(keyValues) - 1) time intervals.
     */
    public KeyFrames(KeyValues keyValues, KeySplines keySplines) {
        if (keySplines != null) {
            init(keyValues, keySplines, null, InterpolationType.NONLINEAR);
        } else {
            init(keyValues, keySplines, null, InterpolationType.LINEAR);
        }
    }

    /**
     * Utility function called by constructors to perform common
     * initialization
     */
    private void init(KeyValues keyValues,
            KeySplines keySplines,
            KeyTimes keyTimes, 
            InterpolationType interpolationType) {
        // If keyTimes null, create our own
        if (keyTimes == null) {
            int numKeyTimes = keyValues.getSize();
            float keyTimesArray[] = new float[numKeyTimes];
            float timeVal = 0.0f;
            keyTimesArray[0] = timeVal;
            for (int i = 1; i < (numKeyTimes - 1); ++i) {
                timeVal += (1.0f / (numKeyTimes - 1));
                keyTimesArray[i] = timeVal;
            }
            keyTimesArray[numKeyTimes - 1] = 1.0f;
            this.keyTimes = new KeyTimes(keyTimesArray);
        } else {
            this.keyTimes = keyTimes;
        }
        this.keyValues = keyValues;
        this.keySplines = keySplines;
        this.interpolationType = interpolationType;
        if (interpolationType == InterpolationType.NONLINEAR &&
                keySplines == null) {
            throw new IllegalArgumentException("NONLINEAR interpolation " +
                    "requires KeySplines");
        }
        if (keyValues.getSize() != this.keyTimes.getSize()) {
            throw new IllegalArgumentException("keyValues and keyTimes" +
                    " must be of equal size");
        }
        if (keySplines != null && 
                (keySplines.getSize() != (this.keyTimes.getSize() - 1))) {
            throw new IllegalArgumentException("keySplines must have" +
                    " a size equal to the one less than the size of" +
                    " keyValues");
        }
    }
        
    public Class getType() {
        return keyValues.getType();
    }
    
    public void setValue(Object object, Method method, float fraction) {
        // First, figure out the real fraction to use, given the
        // interpolation type and keyTimes
        int interval = keyTimes.getInterval(fraction);
        float t0 = keyTimes.getTime(interval);
        if (interpolationType == InterpolationType.DISCRETE) {
            if (fraction < 1.0f) {
                keyValues.setValue(object, method, interval);
            } else {
                keyValues.setValue(object, method, keyTimes.getSize() -1 );
            }
        } else { // LINEAR or NONLINEAR
            float t1 = keyTimes.getTime(interval + 1);
            float t = (fraction - t0) / (t1 - t0);
            if (interpolationType == InterpolationType.NONLINEAR) {
                //System.out.println("Interpolation: time t, interpT = " +
                //        t + ", " + keySplines.interpolate(i0, t));
                t = keySplines.interpolate(interval, t);
            }
            keyValues.setValue(object, method, interval, (interval + 1), t);
        }
    }
}
