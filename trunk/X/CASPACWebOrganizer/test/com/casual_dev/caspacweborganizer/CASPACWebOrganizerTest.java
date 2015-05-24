/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casual_dev.caspacweborganizer;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class CASPACWebOrganizerTest {

    public CASPACWebOrganizerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class CASPACWebOrganizer.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = new String[]{"../../CASPAC"};
        CASPACWebOrganizer.main(args);

        CASPACWebOrganizer cpo = new CASPACWebOrganizer();
        String[] files = cpo.getListOfFiles();
        for (String file : files) {
            if (file.toLowerCase().endsWith(".meta") || file.toLowerCase().endsWith(".properties")) {
                System.out.println("cleaning " + file);
                //new File(file).delete();
            }
        }
    }

}
