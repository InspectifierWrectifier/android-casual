/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.archiving.libpit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class PrintPitTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    public PrintPitTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class PrintPit.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = new String[]{"../test/CASUAL/archiving/resources/ekgc100part.pit"};
        PrintPit.main(args);

    }

}
