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
 * This class defines an Object and a {@link PropertyRange} that
 * define how a specific property on that Object should be
 * modified over time.  An instance of this class can be supplied as a
 * {@link TimingTarget} to {@link TimingController} to run an animation
 * that modifies the object's property over the timing period defined
 * by that TimingController.
 *
 * ObjectModifier can be subclassed if applications need to perform
 * more operations during the begin, end, or timingEvent methods 
 * than the simple interpolation done here.
 *
 * @author Chet
 */
public class ObjectModifier implements TimingTarget {
    
    private PropertyRange propertyRange;
    private Object object;
    private Method propertySetter;
            
    /** 
     * Creates a new instance of ObjectModifer.  Subclasses should call
     * this constructor if they want this class to handle the usual
     * setup of the property-setting method.
     * @throws IllegalArgumentException if there is no method on the
     * object with the appropriate name.  Method names are derived
     * by standard JavaBean naming conventions.  For example, a
     * property name of "location" (in the PropertyRange object) would
     * become a method name of "setLocation".  If this exception is
     * thrown, then either the property name was wrong or there is
     * no JavaBean-compliant set method that can be used with that
     * property name.
     * @throws SecurityException if the application does not have
     * appropriate permissions to request access to the Method
     */
    public ObjectModifier(Object object, PropertyRange propertyRange) {
        this.object = object;
        this.propertyRange = propertyRange;
        try {
            setupMethodInfo();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Bad property name (" +
                    propertyRange.getPropertyName() +"): could not find " +
                    "an appropriate setter method for that property");
        }
    }

    /**
     * Translates the property name used in the PropertyRange object into
     * the appropriate Method in the Object to be modified.  This uses
     * standard JavaBean naming convention (e.g., propertyName would
     * become setPropertyName).
     * @throws NoSuchMethodException if there is no method on the
     * object with the appropriate name
     * @throws SecurityException if the application does not have
     * appropriate permissions to request access to the Method
     */
    private void setupMethodInfo() throws NoSuchMethodException {
        // create JavaBeans setter method name from property name
        String propertyName = propertyRange.getPropertyName();
        String firstChar = propertyName.substring(0, 1);
        String remainder = propertyName.substring(1);
        String propertySetterName = "set" + firstChar.toUpperCase() + remainder;
        // Now get the Method from the object
        Class propertyType = propertyRange.getType();
        propertySetter = object.getClass().getMethod(propertySetterName,
                propertyType);
    }
    
    //
    // TimingTarget interface implementations
    //
    
    /**
     * Called by TimingController to signal that the timer is about to start.
     * This method does nothing in ObjectModifier; subclasses may want to
     * override it if they have any tasks to perform at this time.
     */
    public void begin() {}

    /**
     * Called by TimingController to signal that the timer has ended.
     * This method does nothing in ObjectModifier; subclasses may want to
     * override it if they have any tasks to perform at this time.
     */
    public void end() {}
    
    /**
     * Called from TimingController to signal another timing event.  This
     * causes ObjectModifier to invoke the property-setting method (as 
     * specified by the property name in the PropertyRange object) with the
     * appropriate value of the property given the range of values in the
     * PropertyRange object and the fraction of the timing cycle that has
     * elapsed.
     */
    public void timingEvent(long cycleElapsedTime,
			    long totalElapsedTime, 
			    float fraction) {
        try {
            propertyRange.setValue(object, propertySetter, fraction);
        } catch (Exception e) {
            System.out.println("Problem in ObjectModifier.timingEvent: " + e);
        }
    }
    
}
