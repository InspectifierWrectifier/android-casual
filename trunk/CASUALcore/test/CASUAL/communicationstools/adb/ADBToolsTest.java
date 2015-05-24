/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.communicationstools.adb;

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.CASUALSessionData;
import CASUAL.OSTools;
import CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class ADBToolsTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    CASUALSessionData sd=CASUALMain.getSession();
    AbstractDeviceCommunicationsProtocol instance;

    public ADBToolsTest() throws IOException {
        instance = new ADBTools();
        System.out.println(new File(".").getCanonicalPath());
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of getADBCommand method, of class ADBTools.
     */
    @Test
    public void testGetADBCommand() {
        System.out.println("getADBCommand");
        String expResult = "adb";
        String result = new ADBTools().getBinaryLocation();
        assert (result.contains(expResult));
        assert (result.contains(sd.getTempFolder()));
        assert (new File(result).exists());
    }

    /**
     * Test of waitForDevice method, of class ADBTools.
     */
    @Test
    public void testWaitForDevice() {
        if (!instance.isConnected()) {
            return;
        }
        
        int x = new CASUALMessageObject("Disconnect>>>Disconnect Your Device, wait 5 seconds and then reconnect.\n").showTimeoutDialog(10, null, 1, 1, new Object[]{"ok", "cancel"}, "ok");
        if (x == 0) {
            System.out.println("waitForDevice");
            String expResult = "";
            instance.waitForDevice();
            
        }
    }

    /**
     * Test of getDevices method, of class ADBTools.
     */
    @Test
    public void testGetDevices() {
        System.out.println("getDevices");
        String result = new ADBTools().getDevices();
        System.out.println(result);
    }

    /**
     * Test of startServer method, of class ADBTools.
     */
    @Test
    public void testStartServer() {
        System.out.println("startServer");
        new ADBTools().startServer();

    }

    /**
     * Test of restartADBserver method, of class ADBTools.
     */
    @Test
    public void testRestartADBserver() {
        System.out.println("restartADBserver");
        instance.restartConnection();
    }

    /**
     * Test of elevateADBserver method, of class ADBTools.
     */
    @Test
    public void testElevateADBserver() {
        if (!new ADBTools().isConnected()) {
            return;
        }
        System.out.println("elevateADBserver");
        if (!OSTools.isWindows()) {
            new ADBTools().elevateADBserver();
        }

    }

    /**
     * Test of shutdown method, of class ADBTools.
     */
    @Test
    public void testKillADBserver() {
        System.out.println("killADBserver");
        new ADBTools().shutdown();
    }

    /**
     * Test of checkErrorMessage method, of class ADBTools.
     */
    @Test
    public void testCheckADBerrorMessages() {
        System.out.println("checkADBerrorMessages");
        assert (instance.checkErrorMessage(new String[]{}, "woot"));

    }

    @Test
    public void testNumberOfDevicesConnected() {
        System.out.println("numberOfDevicesConnected");
        int expResult = 0;
        int result = new ADBTools().numberOfDevicesConnected();
        System.out.println(result + " devices connected");
    }

    /**
     * Test of isConnected method, of class ADBTools.
     */
    @Test
    public void testIsConnected() {
        System.out.println("isConnected");
        boolean result = new ADBTools().isConnected();

    }

    /**
     * Test of getBinaryLocation method, of class ADBTools.
     */
    @Test
    public void testGetBinaryLocation() {
        System.out.println("getBinaryLocation");
        ADBTools instance = new ADBTools();
        String expResult = "adb";
        String result = instance.getBinaryLocation();
        assert result.contains(expResult);
        assert result.contains(sd.getTempFolder());
    }

    /**
     * Test of restartConnection method, of class ADBTools.
     */
    @Test
    public void testRestartConnection() {
        System.out.println("restartConnection");
        ADBTools instance = new ADBTools();
        instance.restartConnection();

    }

    /**
     * Test of checkErrorMessage method, of class ADBTools.
     */
    @Test
    public void testCheckErrorMessage() {
        System.out.println("checkErrorMessage");
        String[] CommandRun = null;
        String returnValue = "";
        ADBTools instance = new ADBTools();
        boolean expResult = true;
        boolean result = instance.checkErrorMessage(CommandRun, returnValue);
        assertEquals(expResult, result);

    }

    /**
     * Test of reset method, of class ADBTools.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        ADBTools instance = new ADBTools();
        instance.reset();
    }

    /**
     * Test of deployBinary method, of class ADBTools.
     */
    @Test
    public void testDeployBinary() {
        System.out.println("deployBinary");
        String TempFolder = sd.getTempFolder();
        ADBTools instance = new ADBTools();
        String expResult = sd.getTempFolder()+ (OSTools.isWindows()?"adb.exe":"adb");
        //use old binary if it exists
        String result = instance.deployBinary(TempFolder);
        assertEquals(expResult, result);
        //reset to ensure that the new binary is used this time
        instance.reset();
        result = instance.deployBinary(TempFolder);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getIndividualDevices method, of class ADBTools.
     */
    @Test
    public void testGetIndividualDevices() {
        System.out.println("getIndividualDevices");
        ADBTools instance = new ADBTools();
        instance.getIndividualDevices();
    }




}
