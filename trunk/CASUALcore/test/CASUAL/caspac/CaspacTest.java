/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.caspac;

import CASUAL.CASUALSessionData;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CaspacTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    Caspac test;
    CASUALSessionData sd=CASUALSessionData.newInstance();
    Caspac instance;

    public CaspacTest() {
        CASUALSessionData.setGUI(new GUI.testing.automatic());
        try {
            test=new Caspac(sd,new File("../../CASPAC/QualityControl/echoTest.zip"),sd.getTempFolder(),0);
            test.loadFirstScriptFromCASPAC();
            this.instance = new Caspac(sd,new File("../../CASPAC/testpak.zip"), sd.getTempFolder(), 0);
            System.out.println(instance.getCASPACLocation().getCanonicalPath());
        } catch (IOException ex) {
            Logger.getLogger(CaspacTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of removeScript method, of class Caspac.
     */
    @Test
    public void testRemoveScript() {
        try {
            System.out.println("removeScript");
            instance.load();
            Script s = new Script(sd,instance.getScripts().get(0));
            instance.getScriptByName("foobar");
            instance.removeScript(instance.getScriptByName("foobar"));
            assert (!instance.getScripts().contains("foobar"));
        } catch (ZipException ex) {
            Logger.getLogger(CaspacTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CaspacTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testSetActiveScript() {
        System.out.println("setActiveScript");
        Script s = instance.getScriptByName("foobar");
        instance.setActiveScript(s);
        assert (instance.getActiveScript().getName().equals("foobar"));

    }

    @Test
    public void testGetActiveScript() {
        System.out.println("getActiveScript");

        for (int i = 0; i < 10; i++) {
            instance.getScriptByName("script" + Integer.toString(i));
        }
        instance.setActiveScript(instance.getScripts().get(instance.getScripts().size() - 1));
        Script result = instance.getActiveScript();
        assert (result.getName().equals(instance.getScripts().get(instance.getScripts().size() - 1).getName()));
    }

    @Test
    public void testSetBuild() {
        System.out.println("setBuild");
        Properties p = test.getBuild().getBuildProp();
        p.setProperty("Developer.DonateLink", "OMFG");
        instance.setBuild(p);
        assert (instance.getBuild().getDonateLink().equals("OMFG"));
    }
    
    @Test
    public void testEchoTestVerification() throws Exception {
        System.out.println("write");
        assert test.getType()==0;
        assert test.getLogo()==null;
        assert test.getCASPACsrc()==null;
        assert test.getOverview().equals("test");
        assert test.getBuild().getDeveloperName().equals("test");
        assert test.getBuild().getDonateLink().equals("test");
        assert test.getBuild().getWindowTitle().equals("test");
        assert test.getBuild().getBannerPic().isEmpty();
        assert test.getBuild().getBannerText().equals("test");
        assert test.getBuild().getExecuteButtonText().equals("test");
        assert test.getBuild().isAudioEnabled()==true;
        assert test.getBuild().isUsePictureForBanner()==false;
        assert test.getBuild().isAlwaysEnableControls()==false;
        assert test.getTempFolder().equals(sd.getTempFolder());
        assert test.getBuild().getDeveloperDonateButtonText().equals("test");
        assert test.getBuild().getDeveloperDonateButtonText().equals("test");
        assert test.getCASPACLocation().getCanonicalFile().equals(new File("../../CASPAC/QualityControl/echoTest.zip").getCanonicalFile());
        assert test.getScripts().get(0).extractionMethod==0;
        assert test.getScripts().get(0).getName().equals("echoTest");
        String x=test.getScripts().get(0).getTempDir();
        assert test.getScripts().get(0).getTempDir().contains(sd.getTempFolder()+test.getScripts().get(0).getName());
        assert test.getScripts().get(0).getScriptContentsString().equals("$ECHO test");
        assert test.getScripts().get(0).getIndividualFiles().isEmpty();
        assert test.getScripts().get(0).getMetaData().getMinSVNversion().equals("0");
        assert test.getScripts().get(0).getMetaData().getScriptRevision().equals("0");
        assert test.getScripts().get(0).getMetaData().getUniqueIdentifier().equals("test");
        assert test.getScripts().get(0).getMetaData().getSupportURL().equals("test");
        assert test.getScripts().get(0).getMetaData().getUpdateMessage().equals("test");
        assert test.getScripts().get(0).getMetaData().getKillSwitchMessage().equals("test");
        assert test.getScripts().get(0).getMetaData().getMd5s().contains("c9aa2a1d8bce6a47bc7599d62c475658  echoTest.scr");
        assert test.getScripts().get(0).getMetaData().getMd5s().contains("58eba1c6a6b700f8b42b143f82942176  echoTest.txt");
        assert test.getScripts().get(0).getMetaData().getMd5s().contains("76cdb2bad9582d23c1f6f4d868218d6c  echoTest.zip");
        assert test.getScripts().get(0).getDiscription().equals("Describe your script here");
        assert test.getScripts().get(0).isScriptContinue()==false;
    }
}