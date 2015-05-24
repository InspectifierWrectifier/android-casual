/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.misc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class BooleanOperationsTest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    public BooleanOperationsTest() {
    }

    /**
     * Test of containsTrue method, of class BooleanOperations.
     */
    @Test
    public void testContainsTrue() {
        System.out.println("containsTrue");
        boolean[] containstrue = new boolean[]{false,false,false,false,true,false,false};
        boolean[] notcontainstrue = new boolean[]{false,false,false,false,false,false,false};
        assert(BooleanOperations.containsTrue(containstrue));
        assert(!BooleanOperations.containsTrue(notcontainstrue));
    }

    /**
     * Test of containsFalse method, of class BooleanOperations.
     */
    @Test
    public void testContainsFalse() {
        System.out.println("containsFalse");
        boolean[] notcontainsfalse = new boolean[]{true,true,true,true,true,true,true,true};
        boolean[] containsfalse = new boolean[]{true,true,true,true,false,true,true,true};
        assert(BooleanOperations.containsFalse(containsfalse));
        assert(!BooleanOperations.containsFalse(notcontainsfalse));
        
    }

    /**
     * Test of containsAllTrue method, of class BooleanOperations.
     */
    @Test
    public void testContainsAllTrue() {
        System.out.println("containsAllTrue");
        boolean[] notcontainsfalse = new boolean[]{true,true,true,true,true,true,true,true};
        boolean[] containsfalse = new boolean[]{true,true,true,true,false,true,true,true};
        assert(!BooleanOperations.containsFalse(notcontainsfalse));
        assert(BooleanOperations.containsFalse(containsfalse)); 
       
    }

    /**
     * Test of containsAllFalse method, of class BooleanOperations.
     */
    @Test
    public void testContainsAllFalse() {
        System.out.println("containsAllFalse");
        boolean[] containstrue = new boolean[]{false,false,false,false,true,false,false};
        boolean[] notcontainstrue = new boolean[]{false,false,false,false,false,false,false};
        assert(BooleanOperations.containsAllFalse(notcontainstrue));
        assert(!BooleanOperations.containsAllFalse(containstrue));
    }
}