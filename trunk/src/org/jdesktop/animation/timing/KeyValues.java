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
import java.util.ArrayList;

/**
 *
 * Stores a list of values that correspond to the times in a {@link
 * KeyTimes} object.  These structures are then used to create a
 * {@link KeyFrames} object, which is then used to create a {@link
 * PropertyRange} and {@link ObjectModifier} object, for the purposes
 * of modifying an object's property over time.
 *
 * At each of the times in {@link KeyTimes}, the property will take
 * on the corresponding value in the KeyValues object.  Between these
 * times, the property will take on a value based on the interpolation
 * information stored in the KeyFrames object.
 *
 * @author Chet
 */
public abstract class KeyValues<T> {
    
    protected ArrayList<T> values = new ArrayList<T>();
    
    /**
     * Callers should create KeyValues structures from the factory methods
     * which create KeyValues subclasses based on the types of values.
     */
    protected KeyValues() {
        // default constructor does nothing
    }
    
    /**
     * Returns the number of values stored in this object
     */
    public int getSize() {
        return values.size();
    }
    
    //
    // Factory methods for creating type-specific subclasses
    //
    
    /**
     * Create KeyValues object with int values
     */
    public static KeyValues createKeyValues(int... values) {
        return new KeyValuesInt(values);
    }
    
    /**
     * Create KeyValues object with float values
     */
    public static KeyValues createKeyValues(float... values) {
        return new KeyValuesFloat(values);
    }
    
    /**
     * Create KeyValues object with Point values
     */
    public static KeyValues createKeyValues(Point... values) {
        return new KeyValuesPoint(values);
    }
    
    /**
     * Create KeyValues object with Dimension values
     */
    public static KeyValues createKeyValues(Dimension... values) {
        return new KeyValuesDimension(values);
    }
    
    /**
     * Create KeyValues object with Rectangle values
     */
    public static KeyValues createKeyValues(Rectangle... values) {
        return new KeyValuesRectangle(values);
    }
    
    /**
     * Create KeyValues object with Color values
     */
    public static KeyValues createKeyValues(Color... values) {
        return new KeyValuesColor(values);
    }
    
    /**
     * Subclasses will override this to return the type associated
     * with that subclass.  This is used in ObjectModifier to set
     * up the property-setting method with the appropriate type.
     */
    public abstract Class<?> getType();
    
    /**
     * Sets the value of the property
     * with a linear interpolation of the given fraction between
     * the values at i0 and i1.  Subclasses need to override this
     * method to calculate the value according to their type.
     */
    public abstract void setValue(Object object, Method method, int i0,
            int i1, float fraction);
    
    /**
     * Sets the value of the property to be the value at index.
     * Subclasses need to override this
     * method to calculate the value according to their type.
     */
    public abstract void setValue(Object object, Method method, int index);
}
