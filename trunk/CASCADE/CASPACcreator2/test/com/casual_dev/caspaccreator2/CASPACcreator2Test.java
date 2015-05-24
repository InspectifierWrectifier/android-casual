/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casual_dev.caspaccreator2;

import CASUAL.Log;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import CASUAL.misc.StringOperations;
import com.casual_dev.caspaccreator2.exception.MissingParameterException;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adamoutler
 */
public class CASPACcreator2Test {

    public CASPACcreator2Test() throws IOException {
        this.myTempDir = File.createTempFile("temp", null);
        this.myTempDir.delete();
        this.myTempDir.mkdirs();
        f2.deleteOnExit();
        f.deleteOnExit();
        f2=new File(myTempDir + "/test2.img");
        f= new File(myTempDir + "/test.img");
        f2.createNewFile();
        f.createNewFile();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
    }

    @After
    public void tearDown() {
    }

    File myTempDir;

    File f = new File(myTempDir + "test.img");
    File f2 = new File(myTempDir + "test2.img");
    String scriptdescription = "\"a cool script thingy\nwoot!\"";
    String scriptcontents = "#testing scriptinjection\nmoretesting\"";

    /**
     * Test of main method, of class CASPACcreator2.
     */
    @Test
    public void testLoadAndSave() {
        System.out.println("main");
        String[] argsl = new String[]{"--caspac=../../CASPAC/testpak.zip", "--output=" + myTempDir + "new.zip", "--scriptfiles=file 1", "--scriptfiles=file 2"};
        CASPACcreator2.main(argsl);

        // TODO review the generated test code and remove the default call to fail.
    }

    @Test(expected = MissingParameterException.class)
    public void testColdLoadWithErrorOutput() throws IOException, MissingParameterException {
        System.out.println("Cold Load output");
        String[] argsl = new String[]{};
        CASPACcreator2 cc = new CASPACcreator2(argsl);
        cc.createNewCaspac();
        fail("Script not set, not detected");
    }

    @Test(expected = MissingParameterException.class)
    public void testColdLoadWithErrorScriptName() throws IOException, MissingParameterException {
        System.out.println("cold load scriptname");
        String[] argsl = new String[]{
            "--output=" + myTempDir + "new.zip",};
        CASPACcreator2 cc = new CASPACcreator2(argsl);
        cc.createNewCaspac();
        fail("ScriptName not set, not detected");
    }

    @Test(expected = MissingParameterException.class)
    public void testColdLoadWithErrordescription() throws IOException, MissingParameterException {
        System.out.println("coldload description");
        String[] argsl = new String[]{
            "--output=" + myTempDir + "new.zip",
            "--scriptname=newscript"
        };
        CASPACcreator2 cc = new CASPACcreator2(argsl);
        cc.createNewCaspac();
        fail("Descript not set, not detected");
    }

    @Test(expected = MissingParameterException.class)
    public void testColdLoadWithErrorScriptCode() throws IOException, MissingParameterException {
        System.out.println("cold load scriptcode");
        String[] argsl = new String[]{
            "--output=" + myTempDir + "new.zip",
            "--scriptname=newscript",
            "--scriptdescription=\"a cool script thingy\nwoot!\""
        };
        CASPACcreator2 cc = new CASPACcreator2(argsl);
        cc.createNewCaspac();
        fail("script code not set, not detected");
    }

    @Test
    public void testMinimalLoad() throws IOException, MissingParameterException {
        System.out.println("cold load mimimal");
        String[] argsl = new String[]{
            "--output=" + myTempDir + "new.zip",
            "--scriptname=newscript",
            "--scriptdescription=\"a cool script thingy\nwoot!\"",
            "--scriptcode=#testing scriptinjection\nmoretesting\"'"
        };
        CASPACcreator2 cc = new CASPACcreator2(argsl);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            fail("Exception detected");
        }
    }

    private File getTemp() {
        File tempfile = new File("" + myTempDir + "new" + StringOperations.generateRandomHexString(10) + ".zip");
        tempfile.deleteOnExit();
        return tempfile;
    }

    @Test
    public void testMinimalLoadWithFiles() throws IOException, MissingParameterException {
        System.out.println("cold load mimimal");

        String[] argsl = new String[]{
            "--output=" + getTemp(),
            "--scriptname=newscript",
            "--scriptdescription=" + this.scriptdescription,
            "--scriptcode=" + this.scriptcontents,
            "--zipfile=" + this.f.getAbsolutePath(),
            "--zipfile=" + this.f2.getAbsolutePath(),
            "--overview=\"this is an overview\nand a good one\""
        };

        CASPACcreator2 cc = new CASPACcreator2(argsl);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=cc.getCaspac().getScripts().get(0);
        
        assert (s.getIndividualFiles().contains(f));
        assert (s.getIndividualFiles().contains(f2));
    }

    private void verifySetValueWorked(String check, int i) {
        System.out.print("**** Checking: "+args[i]+ "\nagainst "+check);
        if (null == check || check.isEmpty()) {
            fail();
        }
        assert (args[i].contains(check));
    }

    private String[] getArgsOfLength(int len) {
        String[] localargs = new String[len];
        System.arraycopy(this.args, 0, localargs, 0, localargs.length);
        return localargs;
    }

    @Test
    public void testColdLoadOverview() throws IOException, MissingParameterException {
        System.out.println("cold load overview");
        int i = 7;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        cc.getCaspac().loadFirstScriptFromCASPAC();
        String check = cc.getCaspac().getOverview();
        verifySetValueWorked(check, i-1);
    }
     @Test
    public void testColdLoaddevname() throws IOException, MissingParameterException {
        System.out.println("cold load devname");
        int i = 8;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =c.getBuild().getDeveloperName();
        verifySetValueWorked(check, i-1);
    }
         @Test
    public void testColdLoadenablecontrols() throws IOException, MissingParameterException {
        System.out.println("cold load enablecontrols");
        int i = 9;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =Boolean.toString(c.getBuild().isAlwaysEnableControls());
        verifySetValueWorked(check, i-1);
    }
    
             @Test
    public void testColdLoadBannerText() throws IOException, MissingParameterException {
        System.out.println("cold load bannertext");
        int i = 10;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =c.getBuild().getBannerText();
        verifySetValueWorked(check, i-1);
    }
    
    
     @Test
    public void testColdLoadDonateText() throws IOException, MissingParameterException {
        System.out.println("cold load donatetext");
        int i = 11;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =c.getBuild().getDeveloperDonateButtonText();
        verifySetValueWorked(check, i-1);
    }
    
    @Test
    public void testColdLoadDonateLink() throws IOException, MissingParameterException {
        System.out.println("cold load donatelink");
        int i = 12;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =c.getBuild().getDonateLink();
        verifySetValueWorked(check, i-1);
    }
    
    @Test
    public void testColdLoadStartButton() throws IOException, MissingParameterException {
        System.out.println("cold load start button");
        int i = 13;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =c.getBuild().getExecuteButtonText();
        verifySetValueWorked(check, i-1);
    }
    
        @Test
    public void testColdLoadWindowTitle() throws IOException, MissingParameterException {
        System.out.println("cold load window title");
        int i = 14;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =c.getBuild().getWindowTitle();
        verifySetValueWorked(check, i-1);
    }
    
    @Test
    public void testColdLoadKillswitch() throws IOException, MissingParameterException {
        System.out.println("cold load killswitch message");
        int i = 15;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =s.getMetaData().getKillSwitchMessage();
        verifySetValueWorked(check, i-1);
    }
    @Test
    public void testColdLoadScriptRevision() throws IOException, MissingParameterException {
        System.out.println("cold load Script Revision");
        int i = 16;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =s.getMetaData().getScriptRevision();
        verifySetValueWorked(check, i-1);
    }
@Test
    public void testColdLoadSupportUrl() throws IOException, MissingParameterException {
        System.out.println("cold load support url");
        int i = 17;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =s.getMetaData().getSupportURL();
        verifySetValueWorked(check, i-1);
    }
    
    @Test
    public void testColdLoadUniqueID() throws IOException, MissingParameterException {
        System.out.println("cold load UniqueID");
        int i = 18;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =s.getMetaData().getUniqueIdentifier();
        verifySetValueWorked(check, i-1);
    }
    
    @Test
    public void testColdLoadUpdateMessage() throws IOException, MissingParameterException {
        System.out.println("cold load update message");
        int i = 19;
        String[] myArgs = getArgsOfLength(i);
        CASPACcreator2 cc = new CASPACcreator2(myArgs);
        try {
            cc.createNewCaspac();
        } catch (IOException | MissingParameterException ex) {
            Log.errorHandler(ex);
            fail("exception detected");
        }
        Caspac c=cc.getCaspac();
        cc.getCaspac().loadFirstScriptFromCASPAC();
        Script s=c.getFirstScript();
        String check =s.getMetaData().getUpdateMessage();
        verifySetValueWorked(check, i-1);
    }
    String[] args = new String[]{
        "--output=" + getTemp(),
        "--scriptname=newscript",
        "--scriptdescription=" + this.scriptdescription,
        "--scriptcode=" + this.scriptcontents,
        "--zipfile=" + this.f.getAbsolutePath(),
        "--zipfile=" + this.f2.getAbsolutePath(),
        "--overview=\"this is an overview\nand a good one\"",
        "--devname=CASPAC2 test",
        "--enablecontrols=true",
        "--bannertext=CASPAC2 banner Test",
        "--donatebuttontext=\"caspac 2 dev button \"",
        "--donatelink=http://casual-dev.com/CASPAC2 Testdonateatest",
        "--startbutton=do it now!",
        "--windowtitle=CASPAC2Test",
        "--killswitchmessage= OMFG THE CASPAC2 killswitchmessage is working?!?",
        "--scriptrevision=77777",
        "--supporturl=http://casual-dev.com/caspac2supporturl",
        "--uniqueid=23482394asfjajdfa",
        "--updatemessage=omfg teh caspac2 updatemessage is working"
    };
}
