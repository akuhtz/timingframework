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

package org.jdesktop.animation.timing.interpolation;

import java.util.ArrayList;
import org.jdesktop.animation.timing.*;

/**
 * 
 * KeySplines is used to hold the information about the splines
 * used in a {@link KeyFrames KeyFrames} object.  KeySplines is
 * only used when KeyFrames has a NONLINEAR {@link
 * KeyFrames.InterpolationType InterpolationType}.  There should
 * be one spline (e.g., one set of Spline) defined for 
 * each time segment defined in {@link KeyTimes KeyTimes}, or 
 * one less than the number of times defined in that object.
 * 
 * For more information on how splines are used, refer to the
 * SMIL specification at http://w3c.org.
 * 
 * 
 * @author Chet
 */
public class KeySplines {
    
    private ArrayList splines = new ArrayList();
    
    /**
     * This constructor takes a series of Spline to define a set
     * splines.  These splines will control the behavior for each time
     * interval in between the times defined in the {@link KeyTimes
     * KeyTimes} object and will determine how to interpolate between each
     * of the values in the {@link KeyValues KeyValues} object.
     * 
     * 
     * @param splines A series of Spline objects that define
     * each spline.  The number of Spline objects passed in to this
     * constructor should equal the number of splines you wish to define.
     */
    public KeySplines(Spline... splines) {
        for (Spline points : splines) {
            this.splines.add(points);
        }
    }
    
    public int getSize() {
        return splines.size();
    }

    /**
     * Return the interpolated value for t for the spline at the
     * given index.
     * @param t Fraction of time elapsed in the current time interval.
     * @param index Time segment for which we wish to interpolate.
     */
    float interpolate(int index, float t) {
        Spline spline = (Spline)splines.get(index);
        return spline.getInterpolatedValue(t);
    }
    
}
