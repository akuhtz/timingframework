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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Method;

/**
 * This class is used to hold a property name and
 * a range of values that represent the values that the property can 
 * attain over time.  The class is used primarily in creating instances of
 * ObjectModifier, which is a TimingTarget implementation
 * that varies the values of the property in the values range
 * over time.
 *
 * Users of this class will either call one of the utility factory methods,
 * to create a simple range whose values will be linearly
 * interpolated between during the animation, or the more detailed
 * constructor that uses KeyFrames for more control over the range.
 *
 * It is also possible to subclass PropertyRange to handle types that
 * have no existing definition.
 *
 * @author Chet
 */
public class PropertyRange {
    
    private String propertyName;
    private KeyFrames keyFrames;

    /**
     * This constructor stores the propertyName and KeyFrames for this
     * PropertyRange.
     */
    public PropertyRange(String propertyName, KeyFrames keyFrames) {
        this.propertyName = propertyName;
        this.keyFrames = keyFrames;
    }
    
    //
    // Utility factories; handle the simple lienar interpolation
    // cases here so that users don't have to learn about KeyFrames/etc.
    // if they only need the simple case.
    //
    
    /**
     * Returns PropertyRange that defines an int range.
     */
    public static PropertyRange createPropertyRangeInt(String propertyName,
            int... values) {
        KeyValues keyValues = KeyValues.createKeyValues(values);
        KeyFrames keyFrames = new KeyFrames(keyValues);
        return new PropertyRange(propertyName, keyFrames);
    }
    
    /**
     * Returns PropertyRange that defines a float range.
     */
    public static PropertyRange createPropertyRangeFloat(String propertyName,
            float... values) {
        KeyValues keyValues = KeyValues.createKeyValues(values);
        KeyFrames keyFrames = new KeyFrames(keyValues);
        return new PropertyRange(propertyName, keyFrames);
    }
    
    /**
     * Returns PropertyRange that defines a Point range.
     */
    public static PropertyRange createPropertyRangePoint(String propertyName,
            Point... values) {
        KeyValues keyValues = KeyValues.createKeyValues(values);
        KeyFrames keyFrames = new KeyFrames(keyValues);
        return new PropertyRange(propertyName, keyFrames);
    }
    
    /**
     * Returns PropertyRange that defines a Dimension range.
     */
    public static PropertyRange createPropertyRangeDimension(String propertyName,
            Dimension... values) {
        KeyValues keyValues = KeyValues.createKeyValues(values);
        KeyFrames keyFrames = new KeyFrames(keyValues);
        return new PropertyRange(propertyName, keyFrames);
    }
    
    /**
     * Returns PropertyRange that defines a Rectangle range.
     */
    public static PropertyRange createPropertyRangeRectangle(String propertyName,
            Rectangle... values) {
        KeyValues keyValues = KeyValues.createKeyValues(values);
        KeyFrames keyFrames = new KeyFrames(keyValues);
        return new PropertyRange(propertyName, keyFrames);
    }
    
    /**
     * Returns PropertyRange that defines a Color range.
     */
    public static PropertyRange createPropertyRangeColor(String propertyName,
            Color... values) {
        KeyValues keyValues = KeyValues.createKeyValues(values);
        KeyFrames keyFrames = new KeyFrames(keyValues);
        return new PropertyRange(propertyName, keyFrames);
    }
    
    String getPropertyName() {
        return propertyName;
    }
    
    /**
     * Sets the appropriate value on the property given the current fraction
     */
    void setValue(Object object, Method method, float fraction) {
        keyFrames.setValue(object, method, fraction);
    }
        
    /**
     * Returns the type used in this property range (defers to KeyFrames
     * for this information).
     */
    Class getType() {
        return keyFrames.getType();
    }
    
}
