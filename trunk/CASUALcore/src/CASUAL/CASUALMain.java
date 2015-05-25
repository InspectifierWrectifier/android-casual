/*CASUALMain provides a place for the main thread to break out into different modes.
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
import CASUAL.misc.MandatoryThread;
import CASUAL.network.Pastebin;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 * provides a place for the main thread to break out into different modes.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public final class CASUALMain {

    private static CASUALSessionData sd;
    
    
    
    //TODO: convert android-casual to Maven so it works better cross-platform
    /**
     * the default package used for IDE mode or if no scripts are found
     */
    final public static String defaultPackage = "TestScript"; //note this will be used for IDE only.
    final private static boolean useOverrideArgs = false; // this will use overrideArguments.
    final private static String[] overrideArguments = new String[]{""};

    /**
     *
     * @return this CASUALSessionData
     */
    public static CASUALSessionData getSession() {
        if (sd==null){
            sd=CASUALSessionData.newInstance();
        }
        return sd;
    }

    /**
     * Main method launching the application.
     *
     * @param args command line args to send to casual
     */
    public static void main(String[] args) {
        //reset initial variables for everything. 
        CASUALSessionData sd= CASUALSessionData.newInstance();
        sd.initializeStatics();
        
        
        //Override args for test modes
        if (useOverrideArgs) {
            args = overrideArguments;
        }
        beginCASUAL(args,sd);
    }

    /**
     * Begins actual CASUAL modes this can be called as a stop for CASUAL
     * without losing state. This does not cause a stop.
     *
     * @param args command line args to send to casual
     * @param sd CASUALSessionData to be used
     */
    public static void beginCASUAL(String[] args, CASUALSessionData sd) {
        CASUALMain main = new CASUALMain();
        CASUALMain.sd=sd;
        String CASUALFileName = new File(main.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).toString();

        String CASUALBuildNumber = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.buildnumber");

        System.out.println("CASUAL Cross-platform Android Scripting and Unified Auxiliary Loader\nBuild:" + CASUALBuildNumber + "\n"
                + "    CASUAL  Copyright (C) 2015  Adam Outler\n"
                + "    This program comes with ABSOLUTELY NO WARRANTY.  This is free software,\n"
                + "    and you are welcome to redistribute it, under certain conditions; run\n"
                + "    '" + CASUALFileName + " --license'\n"
                + "    for details. http://android-casual.googlecode.com for source.\n");
        Log.level4Debug(Diagnostics.getDiagnosticReportOneLine(sd));
        main.arguments.checkArguments(args);
        main.startup(sd);
        System.gc();
    }

    /**
     * shuts down CASUAL
     *
     * @param i code to throw
     */
    public static void shutdown(int i) {
        Log.level4Debug("Shutting Down");
        AudioHandler.useSound = false;
        Log.out.flush();
        if (sd.CASPAC != null && sd.CASPAC.getActiveScript() != null) {
            sd.CASPAC.getActiveScript().setScriptContinue(false);
        }
        CASUALConnectionStatusMonitor.stop();
        
        //No logs if Developing, No GUI, or CASPAC.  Only if CASUAL distribution.
        if (!CASUALTools.IDEMode) {
            if (!CASUALSessionData.isGUIIsAvailable()) {
                try {
                    new Pastebin().pasteAnonymousLog();
                } catch (MalformedURLException ex) {
                    Log.errorHandler(ex);
                }
            }
        }
        
        if (CASUALSessionData.getGUI() != null) {
            CASUALSessionData.getGUI().dispose();
            CASUALSessionData.setGUI(null);
        }
        CASUALConnectionStatusMonitor.stop();
        
        sd.initializeStatics();

    }

    private static void doSleep() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    boolean exitWhenDone = false;
    CASUALSettings arguments = new CASUALSettings();
    /**
     * Scans /SCRIPTS/ Folder to locate scripts.
     */
    public Runnable setupCASUALCASPAC = new Runnable() {
        @Override
        public void run() {
            
            if (arguments.getCaspacLocation() != null && arguments.getCaspacLocation().exists()) {
                try {
                    Caspac cp;
                    if (!arguments.getPassword().isEmpty()) {
                        cp = new Caspac(sd,arguments.getCaspacLocation(), sd.getTempFolder(), 0, arguments.getPassword().toCharArray());
                        arguments.setPassword("");
                    } else {
                        cp = new Caspac(sd,arguments.getCaspacLocation(), sd.getTempFolder(), 0);
                        
                    }
                    cp.loadFirstScriptFromCASPAC();
                    sd.CASPAC = cp;
                } catch (IOException ex) {
                    Log.errorHandler(ex);
                } catch (Exception ex) {
                    Log.errorHandler(ex);
                }
                
            } else if (!arguments.isExecute()) {   //execute is for single commands
                //Build a CASPAC from the SCRIPTS folder
                CodeSource src = getClass().getProtectionDomain().getCodeSource();
                Caspac cp;
                try {
                    cp = new Caspac(sd, src, sd.getTempFolder(), 1);
                    
                    //cp.load();
                    sd.CASPAC = cp;
                } catch (ZipException ex) {
                    
                    Log.errorHandler(ex);
                } catch (IOException ex) {
                    Log.errorHandler(ex);
                }
            }
        }
    };

    /**
     * startup is where CASUAL starts its normal routines for both
* @param sd CASUALSessionData to be used
     */
    public void startup(CASUALSessionData sd) {
        //starts the scriptRunLock so that the lock will not be enabled when checked for the first time. 
        CASUALStartupTasks.scriptRunLock.start();
        //make the temp folder if not created
        new FileOperations().makeFolder(sd.getTempFolder());

        switch (arguments.getCASPACType()) {
            case CASUAL:
                Log.level4Debug("Loading CASUAL Type package");

                startGUI();
                commonCASUALCASPACStartupTasks();
                waitForGUI();
                sd.CASPAC.setActiveScript(sd.CASPAC.getScriptByName(sd.CASPAC.getScriptNames()[0]));
                CASUALSessionData.getGUI().setCASPAC(sd.CASPAC);
                CASUALStartupTasks.startADB.waitFor();
                startConnectionStatusMonitor();
                return;
            case CASPAC:
                Log.level4Debug("Loading CASPAC Type package");
                iCASUALUI gui=CASUALSessionData.getGUI();
                        
                if (CASUALSessionData.getGUI() == null) {
                    CASUALSessionData.setGUI(new GUI.CommandLine.CommandLineUI());
                }
                
                commonCASUALCASPACStartupTasks();
                sd.CASPAC.setActiveScript(sd.CASPAC.getScriptByName(sd.CASPAC.getScriptNames()[0]));
                try {
                    sd.CASPAC.loadActiveScript();
                } catch (IOException ex) {
                    Logger.getLogger(CASUALMain.class.getName()).log(Level.SEVERE, null, ex);
                }
                CASUALStartupTasks.caspacPrepLock.waitFor();
                sd.CASPAC.waitForUnzip();
                CASUALStartupTasks.startADB.waitFor();
                new CASUALScriptParser().executeActiveScript(sd.CASPAC);

                //caspacExecute();
                break;
            case EXECUTE:
                try {
                    CASUALTools.setiCASUALGUI(Class.forName("GUI.CommandLine.CommandLineUI"));
                } catch (ClassNotFoundException ex) {
                    Log.level0Error("Could not find Command Line class");
                } catch (InstantiationException ex) {
                    Log.level0Error("Could not instantiate Command Line class");
                } catch (IllegalAccessException ex) {
                    Log.level0Error("Could not access Command Line class");
                }
                this.doConsoleStartup(arguments.getExecuteCommand());
                break;
            case EXIT:
                shutdown(1);
                break;
            default:
                shutdown(1);
                break;
        }
        shutdown(0);

    }

    private void waitForGUI() {
        try {
            CASUALStartupTasks.startGUI.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void commonCASUALCASPACStartupTasks() {
        CASUALStartupTasks.startADB.setName("ADB Startup Task");
        CASUALStartupTasks.startADB.start();
        prepareCaspac();
        setDefaultCASPACScript();
        sd.setStatus("waiting for CASPAC");
        CASUALStartupTasks.caspacPrepLock.waitFor();

    }

    private void caspacExecute() {
        //Using command line mode

        //start the device monitor
        //wait for complete;
        CASUALConnectionStatusMonitor.stop();
        sd.CASPAC.startAndWaitForUnzip();

        new CASUALScriptParser().executeActiveScript(sd.CASPAC);

    }

    private void setDefaultCASPACScript() {
        if (sd.CASPAC != null && sd.CASPAC.getScripts() != null && sd.CASPAC.getScripts().size() >= 1) {
            Log.level4Debug("Finalizing active script up to be run");

            sd.CASPAC.setActiveScript(sd.CASPAC.getScripts().get(0));
            sd.CASPAC.getActiveScript().setScriptContinue(true);
        }
    }

    private void startGUI() {
        CASUALStartupTasks.startGUI = new MandatoryThread(new CASUALTools().GUI);
        CASUALStartupTasks.startGUI.setName("CASUAL GUI");
        CASUALStartupTasks.startGUI.start();//starts the GUI if required
    }

    private void doConsoleStartup(String cmd) {
        CASUALConnectionStatusMonitor.stop();
        CASUALScriptParser csp = new CASUALScriptParser();
        try {
            csp.executeOneShotCommand(cmd);
        } catch (Exception ex) {
            Log.errorHandler(ex);
        }
        Log.level2Information("@scriptComplete");

    }

    private void startConnectionStatusMonitor() {

        switch (arguments.getMonitorMode()) {
            case ADB:
                new CASUALConnectionStatusMonitor().start(sd,new CASUAL.communicationstools.adb.ADBTools());
                break;
            case FASTBOOT:
                new CASUALConnectionStatusMonitor().start(sd, new CASUAL.communicationstools.fastboot.FastbootTools());
                break;
            case HEIMDALL:
                new CASUALConnectionStatusMonitor().start(sd, new CASUAL.communicationstools.heimdall.HeimdallTools());
                break;
            default:
                new CASUALConnectionStatusMonitor().start(sd, new CASUAL.communicationstools.adb.ADBTools());
        }
    }

    /**
     * starts preparing the CASPAC provided.
     */
    public void prepareCaspac() {
        CASUALStartupTasks.caspacPrepLock = new MandatoryThread(setupCASUALCASPAC);
        CASUALStartupTasks.caspacPrepLock.setName("Preparing Scripts");
        CASUALStartupTasks.caspacPrepLock.start(); //scan self for embedded scripts
    }
}
