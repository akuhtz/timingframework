/*
 * KeyValuesDimensionTest.java
 * JUnit based test
 *
 * Created on June 12, 2006, 5:57 AM
 */

package org.jdesktop.animation.timing.interpolation;

import junit.framework.*;
import java.awt.Dimension;
import java.lang.reflect.Method;
import org.jdesktop.animation.timing.*;

/**
 *
 * @author Chet
 */
public class KeyValuesDimensionTest extends TestCase {
    
    public KeyValuesDimensionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(KeyValuesDimensionTest.class);
        
        return suite;
    }

    /**
     * Test of getType method, of class org.jdesktop.animation.timing.interpolation.KeyValuesDimension.
     */
    public void testGetType() {
        System.out.println("getType");
        
        KeyValuesDimension instance = null;
        
        Class<Object> expResult = null;
        Class<Object> result = instance.getType();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setValue method, of class org.jdesktop.animation.timing.interpolation.KeyValuesDimension.
     */
    public void testSetValue() {
        System.out.println("setValue");
        
        Object object = null;
        Method method = null;
        int i0 = 0;
        int i1 = 0;
        float fraction = 0.0F;
        KeyValuesDimension instance = null;
        
        instance.setValue(object, method, i0, i1, fraction);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
