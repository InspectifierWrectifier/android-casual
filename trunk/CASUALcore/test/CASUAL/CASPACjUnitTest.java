/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import CASUAL.communicationstools.adb.ADBTools;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CASPACjUnitTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    CASUALSessionData sd=CASUALSessionData.newInstance();
    @Test
    public void testTest() {
        
    }

    //@Test
    public void testCASPACOperations() {
    
        if (!new ADBTools().isConnected()) {
            return;
        }
        CASUAL.CASUALMain.shutdown(0);
        String[] casualParams = new String[]{"--CASPAC", "../../CASPAC/testpak.zip"};
        String[] badValues = new String[]{"ERROR"};
        String[] goodValues = new String[]{"echo [PASS]"};
        CASUALTest ct = new CASUALTest(sd,casualParams, goodValues, badValues);
        assertEquals(true, ct.checkTestPoints());
        CASUAL.CASUALMain.shutdown(0);

        System.out.println("TESTING SECOND ROUND");
        System.out.println("TESTING SECOND ROUND");
        System.out.println("TESTING SECOND ROUND");
        System.out.println("TESTING SECOND ROUND");
        casualParams = new String[]{"--CASPAC", "../../CASPAC/testpak.zip"};
        badValues = new String[]{"ERROR"};
        goodValues = new String[]{"echo [PASS]", "[PASS] IFNOTCONTAINS"};
        assertEquals(true, new CASUAL.CASUALTest(sd,casualParams, goodValues, badValues).checkTestPoints());
    }
              
    
}
