/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.communicationstools.adb.busybox;

import CASUAL.CASUALTools;
import CASUAL.Shell;
import CASUAL.communicationstools.adb.ADBTools;
import CASUAL.crypto.SHA256sum;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CASUALDataBridgeTest {

    Shell shell = new Shell();

    @Before
    public void setUp() {
        assumeTrue(new ADBTools().isConnected());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSendString() {
        try {
            
            shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm /sdcard/woot"});
            System.out.println("sendString");
            String send = "wooaoas";
            CASUALDataBridge instance = new CASUALDataBridge();
            instance.sendString(send, "/sdcard/woot");
            String result =new ADBTools().run(new String[]{"shell", BusyboxTools.getBusyboxLocation() + " cat /sdcard/woot;"},5000, true);
            System.out.println(result);
            //shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm /sdcard/woot"});
            assert (result.equals( send +"\n"));
        } catch (UnknownHostException ex) {
            Logger.getLogger(CASUALDataBridgeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALDataBridgeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(CASUALDataBridgeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridgeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testSendFile() throws Exception {

        System.out.println("sendFile");
        File f = new File("../../CASPAC/QualityControl/echoTest.zip");
        String remoteFileName = "/sdcard/testpak";
        CASUALDataBridge instance = new CASUALDataBridge();
        long retval = instance.sendFile(f, remoteFileName);
        assert (retval == f.length());
        shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "pull", "/sdcard/testpak", "test"});
        String originalSHA256 = new CASUAL.crypto.SHA256sum(f).toString();
        File test = new File("test");
        String testSHA256 = new CASUAL.crypto.SHA256sum(test).toString();
        System.out.println("original sha256sum:" + originalSHA256);
        System.out.println("received sha256sum:" + testSHA256);
        test.delete();
        //shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm /sdcard/testpak"});
        assert (testSHA256.equals(originalSHA256));
    }
    
    
    
        @Test
        public void testGetFile() throws Exception {
            System.out.println("getFile");
            File original = new File("../../CASPAC/testpak.zip");
            String remoteFileName = "/sdcard/testpak.zip";
            File test = new File("./test");
            test.delete();
            shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "push", original.getAbsolutePath(), remoteFileName});
            CASUALDataBridge instance = new CASUALDataBridge();
            instance.getFile(remoteFileName, test);
            String originalSHA256 = new CASUAL.crypto.SHA256sum(original).toString();
            String testSHA256 = new CASUAL.crypto.SHA256sum(test).toString();
            System.out.println(test.getAbsolutePath());
            System.out.println("Original sha256:"+originalSHA256);
            System.out.println("verified sha256:"+testSHA256);
            shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm " + remoteFileName});
            test.delete();
            assert (testSHA256.equals(originalSHA256));
        }
        
        @Test
        public void testSendStream() throws Exception {
            System.out.println("sendStream");
            String expResult = "omfg \n cool! 123456789";
            InputStream input = new ByteArrayInputStream(expResult.getBytes());
            String remoteFileName = "/sdcard/sendstreamtest";
            CASUALDataBridge instance = new CASUALDataBridge();
            
            long test = instance.sendStream(input, remoteFileName);
            String result = new ADBTools().run(new String[]{"shell","cat "+remoteFileName},5000, true);
            shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm " + remoteFileName});
            assert (test == expResult.length());
            assertEquals(expResult+"\n", result);
        }
        
        @Test
        public void testPullBlock() throws Exception {
            System.out.println("getBlock");
            assertTrue(!new ADBTools().run(new String[]{"shell","ls /dev/block/platform/*/by-name/recovery"}, 5000, true).contains("file or"));
            assertTrue(CASUALTools.rootAccessPossible());
            String remoteFileName = new ADBTools().run(new String[]{"shell","ls /dev/block/platform/*/by-name/recovery"}, 5000, true).split("\n")[0];
            File test = new File("./test");
            test.delete();
            String originalSHA256=SHA256sum.getSum(new ADBTools().run(new String[]{"shell" ,"su -C '"+BusyboxTools.getBusyboxLocation()+" sha256sum "+remoteFileName+"'"}, 30000, true));
            
            CASUALDataBridge instance = new CASUALDataBridge();
            instance.getFile(remoteFileName, test);
            String testSHA256 = new CASUAL.crypto.SHA256sum(test).toString();
            System.out.println(test.getAbsolutePath());
            System.out.println("expected sha256:"+originalSHA256);
            System.out.println("verified sha256:"+testSHA256);
            
            shell.sendShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "rm " + remoteFileName});
            //test=test.getAbsoluteFile();
            long length=test.length();
        System.out.println(test.length());
        test.delete();
        assertEquals(testSHA256,originalSHA256);
    }
}
