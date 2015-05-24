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

package CASUAL.communicationstools.adb.twrprecovery;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class TwrpCommunicationsTest {

    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    TwrpCommunications tc = new TwrpCommunications();
        
    public TwrpCommunicationsTest() {
        assumeTrue(tc.isTwrpInstalled()||tc.isTwrpRunning());
        tc.rebootTWRP();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
        tc.exitRecovery();
    }

    /**
     * Test of rebootTWRP method, of class TwrpCommunications.
     */
    @Test
    public void testRebootTWRP() {
        tc.waitForDevice();
        System.out.println("rebootTWRP");
        TwrpCommunications instance = new TwrpCommunications();
        boolean expResult = true;
        boolean result = instance.rebootTWRP();
        assertEquals(expResult, result);
    }

    /**
     * Test of isTwrpInstalled method, of class TwrpCommunications.
     */
    @Test
    public void testIsTwrpInstalled() {
        tc.waitForDevice();
        tc.rebootTWRP();
        System.out.println("isTwrpInstalled");
        TwrpCommunications instance = new TwrpCommunications();
        boolean expResult = false;  //will be false because we are in twrp.
        boolean result = instance.isTwrpInstalled();
        assertEquals(expResult, result);
    }

    /**
     * Test of isTwrpRunning method, of class TwrpCommunications.
     */
    @Test
    public void testIsTwrpRunning() {
        tc.waitForDevice();
        tc.rebootTWRP();
        System.out.println("isTwrpRunning");
        TwrpCommunications instance = new TwrpCommunications();
        boolean expResult = true;
        boolean result = instance.isTwrpRunning();
        assertEquals(expResult, result);

    }

    /**
     * Test of runTwrpScript method, of class TwrpCommunications.
     * @throws java.lang.Exception
     */
    @Test
    public void testRunTwrpScript_OpenRecoveryScript() throws Exception {
        tc.waitForDevice();
        tc.rebootTWRP();
        System.out.println("runTwrpScript");
        
        TwrpCommunications instance = new TwrpCommunications();
        OpenRecoveryScript script = new OpenRecoveryScript();
        script.mountSystem();
        script.append("wipe cache");
        script.wipeCache();
        script.wipeDalvik();
                
        instance.runTwrpScript(script);

        instance.rebootTWRP();
        

    }

    /**
     * Test of runTwrpScript method, of class TwrpCommunications.
     * @throws java.lang.Exception
     */
    @Test
    public void testRunTwrpScript_String() throws Exception {
        tc.waitForDevice();
        tc.rebootTWRP();
        System.out.println("runTwrpScript");
        String script = "";
        TwrpCommunications instance = new TwrpCommunications();
        instance.runTwrpScript(script);
        instance.rebootTWRP();
    }

    
}
