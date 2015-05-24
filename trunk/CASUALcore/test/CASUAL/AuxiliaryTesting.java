package CASUAL;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class AuxiliaryTesting {

    @BeforeClass
    public static void setUpClass() {
        CASUAL.CASUALMain.shutdown(0);
    }

    @AfterClass
    public static void tearDownClass() {
    }
CASUALSessionData sd=CASUALSessionData.newInstance();

@Test
    public void testCasualAuxilliaryFunctions() throws Exception {

        //run CASUAL to set environmental values
        CASUAL.CASUALMain.main(new String[]{"-e", "$ADB devices"});
        //Testing ADB reboot download
        CASUALSessionData.getGUI().setReady(true);
        if (new CASUAL.CASUALMessageObject("Testing Heimdall", "Connect an ODIN capable device in ADB mode").showUserCancelOption() == 0) {
            CASUALSessionData.getGUI().setReady(false);
            String returnval = new CASUAL.CASUALScriptParser().executeOneShotCommand("$ADB reboot download");
            assert returnval.isEmpty() || returnval.equals("\n ");
            CASUAL.CASUALMain.shutdown(0);
        }

        //Testing Heimdall close-pc-screen
        CASUALSessionData.getGUI().setReady(true);
        if (new CASUAL.CASUALMessageObject("Testing Heimdall", "Connect a device in ODIN mode").showUserCancelOption() == 0) {
            CASUALSessionData.getGUI().setReady(false);
            setContinue();
            setContinue();
            setContinue();
            setContinue();
            new CASUAL.CASUALScriptParser().executeOneShotCommand( "$HEIMDALL close-pc-screen");
            CASUAL.CASUALMain.shutdown(0);
        }

        //testing ADB reboot bootloader
        CASUALSessionData.getGUI().setReady(true);
        if (new CASUAL.CASUALMessageObject("Testing Fastboot", "Connect a FASTBOOT capable device in ADB mode").showUserCancelOption() ==0 ) {
            CASUALSessionData.getGUI().setReady(false);
            CASUAL.CASUALMain.main(new String[]{"-e", "$ADB reboot bootloader"});
            CASUAL.CASUALMain.shutdown(0);
        }

        //testing Fastboot reboot
        CASUALSessionData.getGUI().setReady(true);
        if (new CASUAL.CASUALMessageObject("Testing Fastboot", "Connect a device in FASTBOOT mode").showUserCancelOption() == 0) {
            CASUALSessionData.getGUI().setReady(false);
            String returnval = new CASUAL.CASUALScriptParser().executeOneShotCommand("$FASTBOOT reboot");
            CASUAL.CASUALMain.shutdown(0);
            assert returnval.contains("rebooting...");
        }

        CASUALSessionData.getGUI().setReady(true);
        if (new CASUAL.CASUALMessageObject("Overall Test", "Connect a device in ADB mode").showUserCancelOption() == 0) {
            CASUALSessionData.getGUI().setReady(false);
            String[] casualParams = new String[]{"--execute", "$ECHO hi"};
            String[] badValues = new String[]{"holy mother of god, i just saw a dog."};
            String[] goodValues = new String[]{"hi"};
            assertEquals(true, new CASUAL.CASUALTest(sd,casualParams, goodValues, badValues).checkTestPoints());
            CASUAL.CASUALMain.shutdown(0);
        }

    }
    
    public void setContinue() {
        String string = "\n";
        InputStream stringStream = new java.io.ByteArrayInputStream(string.getBytes());
        sd.in = new BufferedReader(new InputStreamReader(stringStream));
    }
}