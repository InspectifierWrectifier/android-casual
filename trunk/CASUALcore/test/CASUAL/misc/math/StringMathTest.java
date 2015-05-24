/*
 * Copyright (C) 2014 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/ .
 */

package CASUAL.misc.math;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class StringMathTest {
    private static final double DELTA = 1e-15;
    public StringMathTest() {
    }
    
    @Before
    public void setUp() {
    }

    /**
     * Test of performRoundedMathOperation method, of class StringMath.
     */
    @Test
    public void testPerformOperation() {
        
        System.out.println("performOperation");
        String mathProblem = "1+1";
        StringMath instance = new StringMath();
        String expResult = "2";
        Object result="";
        try {
            result = instance.performRoundedMathOperation(mathProblem);
        } catch (CASUALMathOperationException ex) {
            Logger.getLogger(StringMathTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(expResult, result);

        mathProblem = "2-2";
        expResult = "0";
        try {
            result = instance.performRoundedMathOperation(mathProblem);
        } catch (CASUALMathOperationException ex) {
            Logger.getLogger(StringMathTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(expResult, result);
        
        mathProblem = "4-3";
        expResult = "1";
        try {
            result = instance.performRoundedMathOperation(mathProblem);
        } catch (CASUALMathOperationException ex) {
            Logger.getLogger(StringMathTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(expResult, result);

        mathProblem = "4/2+2";
        expResult = "4";
        try {
            result = instance.performRoundedMathOperation(mathProblem);
        } catch (CASUALMathOperationException ex) {
            Logger.getLogger(StringMathTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(expResult, result);        
        
        assertEquals(expResult, result);
        mathProblem = "3*(34/23+23)+22";
        expResult = "95.43478260869566";
        try {
            result = instance.performRoundedMathOperation(mathProblem);
        } catch (CASUALMathOperationException ex) {
            Logger.getLogger(StringMathTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        mathProblem = "3*(34/23+23)+22+Sdfa";
        expResult = "95.43478260869566";
        try {
            result = instance.performRoundedMathOperation(mathProblem);
        } catch (CASUALMathOperationException ex) {
            Logger.getLogger(StringMathTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(expResult, result);
                mathProblem = "3asdf";
        expResult = "95.43478260869566";
        try {
            result = instance.performRoundedMathOperation(mathProblem);
        } catch (CASUALMathOperationException ex) {
            Logger.getLogger(StringMathTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
