/*CASUALScriptParser handles all script operations and language usage in CASUAL.
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
package CASUAL;

import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import CASUAL.instrumentation.Track;
import CASUAL.language.CASUALLanguage;
import CASUAL.misc.CountLines;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Parses and prepares CASUAL Script for CASUAL Language interperater.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALScriptParser {

    static Caspac oneShotCaspac;
    static String scriptReturnValue = "";
    public static final String NEWLINE = ";;;";

    public static void setReturnValue(String value) {
        scriptReturnValue=value;
    }

    public static String getReturnValue() {
        return scriptReturnValue;
    }

    /**
     * If true, script will continue. False to shutdown.
     */
    public int LinesInScript = 0;
    String scriptTempFolder = "";
    String scriptName = "";

    /*
     * Script Handler contains all script commands and will execute commands
     */
    public DataInputStream scriptInput;

    /**
     * executes a CASUAL script from a file
     *
     * @param caspac Caspac used for the script
     * @param multiThreaded false executes on main thread
     */
    public void loadFileAndExecute(Caspac caspac, boolean multiThreaded) {
        Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALExecuting);
        caspac.getSd().setStatus("Loading from file");
        executeSelectedScript(caspac, multiThreaded ,caspac.getSd());
    }

    /**
     * executes a CASUAL script from a file Reports to Log
     *
     * @param script path to file
     */
    private DataInputStream getDataStreamFromFile(Caspac caspac) {

        try {
            Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALExecuting);
            Log.level4Debug("Selected file" + caspac.getActiveScript().getName());

            scriptName = caspac.getActiveScript().getName();
            scriptTempFolder = caspac.getActiveScript().getTempDir();
            LinesInScript = new CountLines().countISLines(caspac.getActiveScript().getScriptContents());
            Log.level4Debug("Lines in Script " + LinesInScript);
            return new DataInputStream(caspac.getActiveScript().getScriptContents());

        } catch (FileNotFoundException ex) {
            Log.errorHandler(ex);
            return null;

        } catch (IOException ex) {
            Log.errorHandler(ex);
            return null;
        }

    }

    /**
     * provides a way to insert a line of CASUAL script.
     *
     * @param Line line to execute
     * @return from CASUAL language
     * @throws java.lang.Exception on any problem
     */
    public String executeOneShotCommand(String Line) throws Exception {
        CASUALSessionData sd=CASUALSessionData.newInstance();
        Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALExecuting);
        sd.setStatus("Executing");
        String retvalue = "";
        if (sd.CASPAC == null) {
            scriptName = "oneShot";
            scriptTempFolder = sd.getTempFolder();
        }
        if (Line.contains(NEWLINE)) {
            String[] lineArray = Line.split(NEWLINE);
            for (String linesplit : lineArray) {
                retvalue = retvalue + new CASUALLanguage(sd,scriptName, scriptTempFolder).commandHandler(linesplit) + "\n";
            }
        } else {
            retvalue = new CASUALLanguage(sd,scriptName, scriptTempFolder).commandHandler(Line);

            }

        return retvalue;
    }
    /**
     * executes the Active Script in the provided CASPAC
     *
     * @param caspac CASPAC to have script executed
     * @param startThreaded true if it is to be started on a new thread.
     * @param data CASUALSessionData to be used for this execution
     */
    public void executeSelectedScript(final Caspac caspac, boolean startThreaded, final CASUALSessionData data){
        Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALExecuting);
        data.ReactionEvents = new ArrayList<String>();
        data.ActionEvents = new ArrayList<String>();
        data.CASPAC.getActiveScript().setScriptContinue(true);
        scriptInput = new DataInputStream(caspac.getActiveScript().getScriptContents());
        Log.level4Debug("Executing Scripted Datastream" + scriptInput.toString());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //int updateStatus;
                Log.level4Debug("CASUAL has initiated a multithreaded execution environment");

                if (CASUALSessionData.isGUIIsAvailable()) {
                    CASUALSessionData.getGUI().setProgressBarMax(LinesInScript);
                }
                Log.level4Debug("Reading datastream" + scriptInput);
                new CASUALLanguage(caspac, caspac.getActiveScript().getTempDir()).beginScriptingHandler(scriptInput);

                if (CASUALSessionData.isGUIIsAvailable()) {
                    //return to normal.
                    CASUALConnectionStatusMonitor.resumeAfterStop();
                } else {
                    //just in case something started the device monitor
                    CASUALConnectionStatusMonitor.stop();
                }
                try {
                    scriptInput.close();
                } catch (IOException ex) {
                    Log.errorHandler(ex);
                }
                data.CASPAC.getActiveScript().setDeviceArch("");
                data.setStatus("done");
                Log.level2Information("@scriptComplete");
                CASUALSessionData.getGUI().setReady(true);

            }
        };
        if (startThreaded) {
            CASUALStartupTasks.scriptRunLock = new CASUAL.misc.MandatoryThread(r);
            caspac.getSd().setStatus("Executing");
            CASUALStartupTasks.scriptRunLock.setName("CASUAL Script Executor");
            CASUALStartupTasks.scriptRunLock.start();
        } else {
            r.run();

        }
    }
    void executeActiveScript(Caspac caspac){
        Log.level3Verbose("Exection of active script in CASPAC Commensing");
        Script s = caspac.getActiveScript();
        caspac.getSd().CASPAC.getActiveScript().setScriptContinue(true);

        Log.level2Information(s.getDiscription());
        int casualSVN = Integer.parseInt(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision"));
        int scriptSVN = Integer.parseInt(s.getMetaData().getMinSVNversion());
        if (casualSVN < scriptSVN) {
            Log.level0Error("@improperCASUALversion");
            return;
        }
        
        DataInputStream dis = new DataInputStream(s.getScriptContents());
        caspac.setActiveScript(s);
        new CASUALLanguage(caspac, s.getTempDir()).beginScriptingHandler(dis);
        
    }

    void executeFirstScriptInCASPAC(Caspac caspac) {
        String name = caspac.getScriptNames()[0];
        Script s = caspac.getScriptByName(name);
        caspac.setActiveScript(s);
        executeActiveScript(caspac);
    }
}
