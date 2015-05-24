/*
 * To change ttestings license header, choose License Headers in Project Properties.
 * To change ttestings template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL.language;

import CASUAL.CASUALScriptParser;
import CASUAL.CASUALSessionData;
import CASUAL.OSTools;
import CASUAL.communicationstools.adb.ADBTools;
import CASUAL.communicationstools.heimdall.HeimdallTools;
import CASUAL.misc.math.CASUALMathOperationException;
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
 * @author adamoutler
 */
public class CASUALLanguageTest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    CASUALScriptParser casualScriptParser = new CASUALScriptParser();

    public CASUALLanguageTest() {
    }
    @Before
    public void setUp() {
        CASUALSessionData.setGUI(new GUI.testing.automatic());
    }
    @After
    public void tearDown() {
    }
 

    /**
     * Test of commandHandler method, of class CASUALLanguage.
     * @throws java.lang.Exception
     */
    @Test
    public void testOS() throws Exception {
        
        if (OSTools.isLinux()){
            System.out.println("Testing Linux commands");
            assertEquals("testing",casualScriptParser.executeOneShotCommand("$LINUX $ECHO testing"));
            assertEquals("testing",casualScriptParser.executeOneShotCommand("$LINUXMAC $ECHO testing"));
            assertEquals("testing",casualScriptParser.executeOneShotCommand("$LINUXWINDOWS $ECHO testing"));
            assertEquals("",casualScriptParser.executeOneShotCommand("$WINDOWS $ECHO testing"));
            assertEquals("",casualScriptParser.executeOneShotCommand("$WINDOWSMAC $ECHO testing"));
            assertEquals("",casualScriptParser.executeOneShotCommand("$MAC $ECHO testing"));

        }
        if (OSTools.isMac()){
            System.out.println("Testing Mac commands");
            assertEquals("testing",casualScriptParser.executeOneShotCommand("$WINDOWSMAC $ECHO testing"));
            assertEquals("testing",casualScriptParser.executeOneShotCommand("$LINUXMAC $ECHO testing"));
            assertEquals("testing",casualScriptParser.executeOneShotCommand("$MAC $ECHO testing"));            
            assertEquals("",casualScriptParser.executeOneShotCommand("$WINDOWS $ECHO testing"));
            assertEquals("",casualScriptParser.executeOneShotCommand("$LINUXMAC $ECHO testing"));
            assertEquals("",casualScriptParser.executeOneShotCommand("$LINUX $ECHO testing"));
        }
        if (OSTools.isWindows()){
            System.out.println("Testing Windows commands");
            assertEquals("testing",casualScriptParser.executeOneShotCommand("$WINDOWS $ECHO testing"));
            assertEquals("testing",casualScriptParser.executeOneShotCommand("$WINDOWSMAC $ECHO testing"));
            assertEquals("testing",casualScriptParser.executeOneShotCommand("$LINUXWINDOWS $ECHO testing"));
            assertEquals("",casualScriptParser.executeOneShotCommand("$LINUX $ECHO testing"));
            assertEquals("",casualScriptParser.executeOneShotCommand("$LINUXMAC $ECHO testing"));
            assertEquals("",casualScriptParser.executeOneShotCommand("$MAC $ECHO testing"));        }
 
    }
    
    @Test
    public void testEcho() throws Exception {
        System.out.println("$ECHO");
        String expResult = "testing";
        String result = casualScriptParser.executeOneShotCommand("$ECHO testing");
        assertEquals(expResult, result);
    }
    @Test
    public void testHalt() throws Exception {
        System.out.println("$HALT");
        String expResult = "testing\ntesting\n";
        String haltResult="";
        String result = casualScriptParser.executeOneShotCommand("$HALT $ECHO testing;;; $ECHO testing");
        System.out.println(result);
        String result2 = casualScriptParser.executeOneShotCommand("$HALT");
        assertEquals(expResult, result);
        assertEquals(haltResult,result2);
    }
    @Test
    public void testcomment() throws Exception {
        System.out.println("Comment");
        String expResult = "";
        String result = casualScriptParser.executeOneShotCommand("#$ECHO testing");
        assertEquals(expResult, result);
    }
    @Test
    public void testBlankLines() throws Exception {
        System.out.println("Testing blank lines");
        String expResult = "";
        String result = casualScriptParser.executeOneShotCommand("");
        assertEquals(expResult, result);
    }
    @Test
    public void testIfContains() throws Exception {
        System.out.println("$IFCONTAINS true");
        String expResult = "testing";
        String result = casualScriptParser.executeOneShotCommand("$IFCONTAINS woot $INCOMMAND $ECHO woot $DO $ECHO testing");
        assertEquals(expResult, result);
        System.out.println("$IFCONTAINS false");
        result = casualScriptParser.executeOneShotCommand("$IFCONTAINS toow $INCOMMAND $ECHO woot $DO $ECHO testing");
        assertEquals("", result);
    }
    
    @Test
    public void testIfNotContains() throws Exception {
        System.out.println("$IFNOTCONTAINS true");
        String expResult = "testing";
        String result = casualScriptParser.executeOneShotCommand("$IFNOTCONTAINS toow $INCOMMAND $ECHO woot $DO $ECHO testing");
        assertEquals(expResult, result);
        System.out.println("$IFNOTCONTAINS false");
        result = casualScriptParser.executeOneShotCommand("$IFNOTCONTAINS woot $INCOMMAND $ECHO woot $DO $ECHO testing");
        assertEquals("", result);       
    }
    @Test
    public void testSleep() throws Exception {
        System.out.println("Testing blank lines");
        long time=System.currentTimeMillis();
        casualScriptParser.executeOneShotCommand("$SLEEP 1");
        assert(System.currentTimeMillis()>=time+1000);
    }    
     @Test
     public void testSleepMillis() throws Exception {
         System.out.println("Testing blank lines");
         long time=System.currentTimeMillis();
         casualScriptParser.executeOneShotCommand("$SLEEPMILLIS 1000");
         assert(System.currentTimeMillis()>=time+1000);
    }   
    @Test
    public void testBusybox() throws Exception {
        
        System.out.println("$BUSYBOX");
        //this will fail if no device is connected.
        if (new ADBTools().isConnected()){
            String expResult = "/data/local/tmp/busybox";
            String result = casualScriptParser.executeOneShotCommand("$ECHO $BUSYBOX");
            assertEquals(expResult, result);
        }
    }
    @Test
    public void testSlash() throws Exception {
        System.out.println("$SLASH");
        String expResult = System.getProperty("file.separator"); 
        expResult += expResult; //get two in there just to verify for literal purposes
        String result = casualScriptParser.executeOneShotCommand("$ECHO $SLASH$SLASH");
        assertEquals(expResult, result);
    }
    @Test
    public void testZipfile() throws Exception {
        System.out.println("$ZIPFILE");
        String result = casualScriptParser.executeOneShotCommand("$ECHO $ZIPFILE");
        System.out.println(result);
        assert(new File(result).exists() && new File(result).isDirectory());
    }   
    
    @Test
    public void testListDir() throws IOException, Exception{
        System.out.println("$LISTDIR");
        String expResult = "test.txt"; 
        File f=new File(expResult);
        f.createNewFile();
        String result = casualScriptParser.executeOneShotCommand("$LISTDIR .");
        f.delete();
        System.out.println("$LISTDIR result:\n"+result);
        String[] retvalsplit=result.split("\n");
        boolean test=false;
        for (String res:retvalsplit){
            if(res.endsWith(expResult)){
                test=true;
            }
        }
        assert(test);
    }
        @Test
        public void testMAKEREMOVEDIR() throws IOException, Exception{
            System.out.println("$MAKEDIR/$REMOVEDIR");
            String expResult = "testfolder";
            File f=new File(expResult);
            f.createNewFile();
            String result = casualScriptParser.executeOneShotCommand("$MAKEDIR "+expResult);
            assert(result.contains(expResult));
            
            result = casualScriptParser.executeOneShotCommand("$LISTDIR .");
            
            String[] retvalsplit=result.split("\n");
            boolean test=false;
            for (String res:retvalsplit){
                if(res.endsWith(expResult)){
                    test=true;
                }
            }
            assert(test);
            System.out.println("$REMOVEDIR");
            assert(new File(expResult).exists());
            assert(casualScriptParser.executeOneShotCommand("$REMOVEDIR "+expResult).contains(expResult));
            assert(! new File(expResult).exists());
        }
        @Test
        public void testDownload() throws Exception{
            System.out.println("$Download");
            String result = casualScriptParser.executeOneShotCommand("$DOWNLOAD https://android-casual.googlecode.com/svn/trunk/README , $ZIPFILEreadme, CASUAL SVN readme file");
            
            String sha256sum=CASUAL.crypto.SHA256sum.getLinuxSum(new File(result));
            assertEquals (sha256sum, "b2db2359cb7ea18bec6189b26e06775abf253f36ffb00402a9cf4faa1a2b6982  readme");
            
            new File(result).delete();
            
        }
        
        @Test
        public void testADB() throws Exception{
            System.out.println("adb test");
            String result = casualScriptParser.executeOneShotCommand("$ADB devices");
            assert result.contains("List of devices attached");
            result = casualScriptParser.executeOneShotCommand("adb devices");
            assert result.contains("List of devices attached");
            System.out.println("adb language test completed");
        }
        
        @Test
        public void testFastboot() throws Exception{
            System.out.println("fastboot test");
            String result = casualScriptParser.executeOneShotCommand("$FASTBOOT --help");
            
            System.out.println("fastboot language test completed");
        }
        
        @Test
        public void testMath() throws Exception {
            System.out.println("$MATH test");
            String result = casualScriptParser.executeOneShotCommand("$MATH 3*3");
            assert result.equals("9");
            result = casualScriptParser.executeOneShotCommand("$MATH 4+4/2");
            assert result.equals("6");
            result = casualScriptParser.executeOneShotCommand("$MATH -1+1");
            assert result.equals("0");
            result = casualScriptParser.executeOneShotCommand("$MATH x=2; x+1;");
            assert result.equals("3");
            result = casualScriptParser.executeOneShotCommand("$MATH function myFunction(p1, p2) {return p1 * p2;} myFunction(300,43.13412);" );
            assert result.equals("12940.236");
            try {
                casualScriptParser.executeOneShotCommand("$MATH 323421+22asdf");
            } catch (CASUALMathOperationException ex){
                assert(ex.getMessage().endsWith("could not be evaluated"));
                System.out.println("$MATH test completed");
            }
        }
        
        @Test
        public void testVariablesWithMath() throws Exception {
            System.out.println("Variable test");
            String result = casualScriptParser.executeOneShotCommand("mvar=5;;;$MATH mvar+4").trim();
            String expectedResult="9";
            assertEquals( expectedResult,result);
            System.out.println("$MATH test completed");  
        }
        
        @Test
        public void testHeimdall() throws Exception {
            System.out.println("heimdall test");
            
            String result;
            if (new HeimdallTools().isConnected()){
                result=casualScriptParser.executeOneShotCommand("$HEIMDALL detect");
                assert result.contains("download");
                result = casualScriptParser.executeOneShotCommand("heimdall detect");
                assert result.contains("download");
        } 
        System.out.println("heimdall language test completed");  
    }
}
