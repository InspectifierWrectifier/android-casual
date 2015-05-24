/*CASPACCreator2 creates CASPACs for CASUAL. 
 *Copyright (C) 2015  Adam Outler
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see https://www.gnu.org/licenses/ .
 */
package com.casual_dev.caspaccreator2;

import CASUAL.CASUALMain;
import CASUAL.Log;
import CASUAL.CASUALSessionData;
import CASUAL.CASUALSettings.MonitorMode;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Build;
import CASUAL.caspac.Script;
import CASUAL.caspac.ScriptMeta;
import CASUAL.iCASUALUI;
import CASUAL.misc.StringOperations;
import com.casual_dev.caspaccreator2.exception.MissingParameterException;
import com.sun.javafx.application.ParametersImpl;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adamoutler
 */
public class CASPACcreator2 extends ParametersImpl {

    String returnValue = "Starting";
    private Caspac caspac;
    private boolean loadedFromExisting = false;

    public CASPACcreator2(String[] args) {
        super(args);
    }

    public Caspac getCaspac() {
        return caspac;
    }

    private String doPackaging() throws IOException, MissingParameterException {

        if (getNamed().containsKey("caspac")) {
            loadCaspac();
        }
        setCaspacOutputLocation();
        setCASPACProperties();
        setGeneralCaspacInfo();
        setCaspacBuildInfo();
        setScriptFiles();
        caspac.getCASPACLocation().delete();
        caspac.write();

        return returnValue;
    }

    private void loadCaspac() throws IOException {
        caspac = new Caspac(CASUALMain.getSession(),new File(getNamed().get("caspac")), CASUALMain.getSession().getTempFolder(), 0);
        caspac.loadFirstScriptFromCASPAC();
        caspac.waitForUnzip();
        loadedFromExisting = true;
    }

    private void setCASPACProperties() throws MissingParameterException {

    }

    private void setGeneralCaspacInfo() throws MissingParameterException {
        caspac.setLogo(null);
        caspac.setOverview(loadOptionalString("overview", caspac.getOverview(), "overview unspecified"));
    }

    private void setCaspacBuildInfo() throws MissingParameterException {
        Build b;
        b = (loadedFromExisting ? caspac.getBuild() : new Build(caspac));
        b.setAudioEnabled(false);
        b.
                setUsePictureForBanner(false).
                setBannerPic("").
                setDeveloperName(loadOptionalString("devname", b.getDeveloperName(), "Anonymous")).
                setAlwaysEnableControls(loadOptionalString("enablecontrols", Boolean.toString(b.isAlwaysEnableControls()), "false").equals("true")).
                setBannerText(loadOptionalString("bannertext", b.getBannerText(), "bannertext unspecified")).
                setDeveloperDonateButtonText(loadOptionalString("donatebuttontext", b.getDeveloperDonateButtonText(), "donatebuttontext unspecified")).
                setDonateLink(loadOptionalString("donatelink", b.getDonateLink(), "http://casual-dev.com")).
                setExecuteButtonText(loadOptionalString("startbutton", b.getExecuteButtonText(), "Do It!")).
                setWindowTitle(loadOptionalString("windowtitle", b.getWindowTitle(), "title unspecified"));
        caspac.setBuild(b);
    }

    private void setScriptFiles() throws MissingParameterException {
        List<File> includeFiles = new ArrayList<>();
        this.getRaw().stream().filter((arg) -> (arg.startsWith("--zipfile="))).forEach((arg) -> {
            includeFiles.add(new File(arg.split("=")[1]));
        });
        Script script = caspac.getFirstScript();
        script = (loadedFromExisting
                ? loadNewValuesToExistingScript(script, includeFiles)
                : createNewScriptFromScratch(includeFiles));
        setScriptMeta(script);
        verifyScript(script);
        caspac.removeAllScripts();
        caspac.addScript(script);
    }

    private void verifyScript(Script script) throws MissingParameterException {
        if (!script.verifyScript()) {
            throw new MissingParameterException("  UNKNOWN PARAMETER MISSING! THIS SHOULD NOT HAPPEN --");
        }
    }

    private Script createNewScriptFromScratch(List<File> includeFiles) throws MissingParameterException {
        Script script;
        String[] params = new String[]{"scriptname", "scriptdescription", "scriptcode"};
        for (String param : params) {
            if (null == getNamed().get(param)) {
                throw new MissingParameterException(param);
            }

        }
        script = new Script(CASUALMain.getSession(),
                getNamed().get("scriptname"),
                getNamed().get("scriptcode"),
                getNamed().get("scriptdescription"),
                includeFiles,
                CASUALMain.getSession().getTempFolder() + getNamed().get("scriptname"));
        return script;
    }

    private Script loadNewValuesToExistingScript(Script script, List<File> includeFiles) throws MissingParameterException {
        script = caspac.getScripts().get(0);
        if (includeFiles.size() > 0) {
            script.setIndividualFiles(includeFiles);
        }
        script.setName(loadString("scriptname", script.getName())).
                setScriptContents(loadString("scriptcode", script.getScriptContentsString())).
                setDiscription(loadString("description", script.getDiscription()));
        return script;
    }

    private String loadString(String key, String existing) throws MissingParameterException {
        if (null != getNamed().get(key)) {
            return (getNamed().get(key));   //try to return the requested value.
        } else {
            if (!loadedFromExisting || null == existing || existing.isEmpty()) {
                throw new MissingParameterException(key);  //throw an error if this was not loaded from a caspac
            }
            return existing;  //return the existing CASPAC value
        }

    }

    private String loadOptionalString(String key, String existing, String defaultValue) throws MissingParameterException {
        if (loadedFromExisting) {  //if this was a previously existing caspac, use key or existing
            return getNamed().getOrDefault(key, existing);
        } else {  //if this is a new caspac, use key or default value (since existing is missing)
            return getNamed().getOrDefault(key, defaultValue);
        }
    }

    private void setScriptMeta(Script script) throws MissingParameterException {

        ScriptMeta meta = script.getMetaData();
        meta.setKillSwitchMessage(loadOptionalString("killswitchmessage", meta.getKillSwitchMessage(), "This Script was deemed unsafe and is deactivated")).
                setScript(script).setScriptRevision(loadOptionalString("scriptrevision", meta.getScriptRevision(), "1")).
                setSupportURL(loadOptionalString("supporturl", meta.getSupportURL(), "http://forums.xda-developers.com")).
                setUniqueIdentifier(loadOptionalString("uniqueid", meta.getUniqueIdentifier(), StringOperations.generateRandomHexString(10) + script.getName())).
                setUpdateMessage(loadOptionalString("updatemessage", meta.getUpdateMessage(), "Script updated to new version")).
                setMinSVNversion(Integer.toString(CASUAL.CASUALTools.getSVNVersion())).
                setMonitorMode(MonitorMode.valueOf(loadOptionalString("monitormode",meta.getMonitorMode().name(),MonitorMode.valueOf("ADB").name()).toUpperCase()));
          
        script.setMetaData(meta);

        /*
         //        public Script(String name, String script, String discription, List<File> includeFiles, String tempDir) {
         Script script = new Script(
        
         );
        
         caspac.getScripts().get(0).name.equals("echoTest");
         String x = test.scripts.get(0).tempDir;
         caspac.getScripts().get(0).tempDir.contains(CASUALSessionData.getInstance().getTempFolder() + test.scripts.get(0).name);
         caspac.getScripts().get(0).scriptContents.equals("$ECHO test");
         caspac.scripts.get(0).individualFiles.size() = 0;
         caspac.getScripts().get(0).metaData.minSVNversion.equals("0");
         caspac.getScripts().get(0).metaData.scriptRevision.equals("0");
         caspac.scripts.get(0).metaData.uniqueIdentifier = "test";
         caspac.scripts.get(0).metaData.supportURL = "test";
         caspac.scripts.get(0).metaData.updateMessage = "test";
         caspac.scripts.get(0).metaData.killSwitchMessage = "test";
         caspac.getScripts().get(0).metaData.md5s.contains("c9aa2a1d8bce6a47bc7599d62c475658  echoTest.scr");
         caspac.getScripts().get(0).metaData.md5s.contains("58eba1c6a6b700f8b42b143f82942176  echoTest.txt");
         caspac.getScripts().get(0).metaData.md5s.contains("76cdb2bad9582d23c1f6f4d868218d6c  echoTest.zip");
         caspac.getScripts().get(0).discription.equals("Describe your script here");
         caspac.scripts.get(0).scriptContinue = false;*/
    }

    private static void helpStatement() {
        String n = System.getProperty("line.separator");
        String helpstatement = "CASUAL's CASPACCreator" + n
                + "mandatory:" + n
                + " specify output location" + n
                + "  --output=path/to/newCaspac" + n
                + " if loading a caspac, all other options are not mandatory" + n
                + " Load CASPAC from existing:" + n
                + "   --caspac=path/to/existingCaspac" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n
                + "" + n;
        System.out.println(helpstatement);
    }

    private void setCaspacOutputLocation() throws IOException, MissingParameterException {
        if (null == getNamed().get("output")) {
            throw new MissingParameterException("output");
        }
        File cplocation = new File(getNamed().get("output"));
        if (null == caspac) {
            
            caspac = new Caspac(CASUALMain.getSession(),cplocation, CASUALMain.getSession().getTempFolder(), 0);
        }
        caspac.setCASPACLocation(cplocation);

    }

    public Caspac createNewCaspac() throws IOException, MissingParameterException {
        iCASUALUI oldGUI = CASUALSessionData.getGUI();
        CASUALSessionData.setGUI(new GUI.testing.automatic());

        doPackaging();

        CASUALSessionData.setGUI(oldGUI);
        Log.Level1Interaction("new CASPAC located at " + caspac.getCASPACLocation().getAbsolutePath());
        return caspac;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new CASPACcreator2(args).createNewCaspac();
        } catch (NullPointerException | IOException | MissingParameterException ex) {
            helpStatement();
            Log.errorHandler(ex);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (loadedFromExisting) {
            sb.append("Loaded from file: ").append(this.caspac.getCASPACLocation()).append("\n");
        }
        sb.append("status:").append(returnValue);
        return sb.toString();
    }

}
