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

package org.jdesktop.animation.timing.examples.editor;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jdesktop.animation.timing.interpolation.KeyFrames;
import org.jdesktop.animation.timing.interpolation.KeySplines;
import org.jdesktop.animation.timing.interpolation.KeyTimes;
import org.jdesktop.animation.timing.interpolation.KeyValues;
import org.jdesktop.animation.timing.interpolation.PropertyRange;
import org.jdesktop.animation.timing.interpolation.Spline;
import org.jdesktop.animation.timing.interpolation.KeyFrames.InterpolationType;

class PropertyRangePoint2D {
    public static PropertyRange create(String propertyName, Point2D... values) {
        KeyValues keyValues = createKeyValuesPoint2D(values);
        KeyTimes times = new KeyTimes(0.0f, 1.0f);
        KeySplines splines = new KeySplines(new Spline(1.0f, 0.0f, 0.0f, 1.0f));
        KeyFrames keyFrames = new KeyFrames(keyValues, splines, times, InterpolationType.NONLINEAR);
        return new PropertyRange(propertyName, keyFrames);
    }

    public static KeyValues createKeyValuesPoint2D(Point2D... values) {
        return new KeyValuesPoint2D(values);
    }
    
    private static class KeyValuesPoint2D extends KeyValues<Point2D> { 
        public KeyValuesPoint2D(Point2D... values) {
            for (Point2D value: values) {
                this.values.add(value);
            }
        }

        @Override
        public void setValue(Object object, Method method, int index) {
            try {
                method.invoke(object, new Object[] { values.get(index) });
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }

        @Override
        public void setValue(Object object, Method method, int start, int end, float fraction) {
            Point2D value = (Point2D) ((Point2D) values.get(start)).clone();

            if (start != end) {
                Point2D v0 = (Point2D) values.get(start);
                Point2D v1 = (Point2D) values.get(end);
                double x = value.getX(); 
                x += (v1.getX() - v0.getX()) * fraction;
                double y = value.getY();
                y += (v1.getY() - v0.getY()) * fraction;
                value.setLocation(x, y);
            }
            try {
                method.invoke(object, new Object[] { value });
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }

        }

        @SuppressWarnings("unchecked")
        @Override
        public Class getType() {
            return Point2D.class;
        }
    }
}
