/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CASUALLanguagejUnitTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        
    }

    @Test
    public void testCASUALLanguage() throws Exception {
        CASUAL.CASUALMain.main(new String[]{"-e", "$ECHO hi"});
        CASUALSessionData.setGUI(new GUI.testing.automatic());

        String x = new CASUAL.CASUALScriptParser().executeOneShotCommand("$IFNOTCONTAINS d2cafdan $INCOMMAND adb shell \"cat /system/build.prop\" $DO $IFNOTCONTAINS d2asdfgtt $INCOMMAND $ADB shell \"cat /system/build.prop\" $DO $ECHO hi");
        assert x.contains("hi");
    }

}
