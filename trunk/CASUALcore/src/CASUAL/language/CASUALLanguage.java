/*CASUALLanguage is where the CASUALLanguage is interperated
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
package CASUAL.language;

import CASUAL.CASUALMessageObject;
import CASUAL.CASUALScriptParser;
import CASUAL.CASUALSessionData;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.Shell;
import CASUAL.ShellTools;
import CASUAL.caspac.Caspac;
import CASUAL.communicationstools.adb.ADBTools;
import CASUAL.communicationstools.adb.busybox.BusyboxTools;
import CASUAL.communicationstools.adb.busybox.CASUALDataBridge;
import CASUAL.communicationstools.fastboot.FastbootTools;
import CASUAL.communicationstools.heimdall.HeimdallTools;
import CASUAL.communicationstools.heimdall.drivers.DriverInstall;
import CASUAL.communicationstools.heimdall.drivers.DriverRemove;
import CASUAL.crypto.MD5sum;
import CASUAL.instrumentation.Track;
import CASUAL.language.commands.ControlCommands;
import CASUAL.language.commands.MathCommands;
import CASUAL.language.commands.Variables;
import CASUAL.misc.StringOperations;
import CASUAL.network.CASUALUpdates;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CASUALLanguage is where the CASUALLanguage is interperated
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALLanguage {
    public static String GOTO = "";

    /**
     * resets the script
     */
    public static void reset() {
        GOTO="";
    }

    final private CASUALSessionData sd;
    final private String ScriptTempFolder;
    final String CASUALHOME = System.getProperty("user.home") + System.getProperty("file.separator") + ".CASUAL" + System.getProperty("file.separator");
    final Caspac CASPAC;
    private String deviceBuildPropStorage;
    
    int currentLine = 1;

    
    /**
     * instantiates CASUALLanguage with script
     *
     * @param caspac the CASPAC used for the script
     * @param ScriptTempFolder temp folder to use for script
     */
    public CASUALLanguage(Caspac caspac, String ScriptTempFolder) {
        this.sd=caspac.getSd();
        this.ScriptTempFolder = ScriptTempFolder;
        this.CASPAC = caspac;
    }

    /**
     * Constructor for CASUALLanguage
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param ScriptName Name of script to be executed
     * @param ScriptTempFolder Folder in which script is executing.
     */
    public CASUALLanguage(CASUALSessionData sd, String ScriptName, String ScriptTempFolder){
        this.sd=sd;
        this.ScriptTempFolder = ScriptTempFolder;
        this.CASPAC = null;
    }
    
    public Caspac getCaspac(){
        return CASPAC;
    }

    public CASUALSessionData getSessionData() {
        return sd;
    }

    /**
     * starts the scripting handler spooler and handles flow control
     *
     * @param dataIn CASUALScript .scr file
     */
    public void beginScriptingHandler(DataInputStream dataIn) {
        String strLine = "";
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn),200*1024);

            bReader.mark(200*1024);
            while ((strLine = bReader.readLine()) != null)  {

                //verify continue
                if (sd.CASPAC.getActiveScript().isScriptContinue() == false) {
                    return;
                }
                
                //set progress
                currentLine++;
                if (CASUALSessionData.isGUIIsAvailable()) {
                    CASUALSessionData.getGUI().setProgressBar(currentLine);
                }
                
                //check GOTO commands
                if (!GOTO.isEmpty()) {
                    strLine = doGotoRoutine(bReader, strLine);
                }
                if (strLine.contains(";;;")) {
                    String[] lineArray = strLine.split(";;;");
                    for (String line : lineArray) {
                        commandHandler(line);
                    }
                } else {
                    commandHandler(strLine);
                }
            }
            //Close the input stream
            dataIn.close();
            uninstallDriverMaybe();
            Log.level2Information("@done");
            CASUALSessionData.getGUI().sendProgress("@done");
            CASUALSessionData.getGUI().setUserMainMessage("@done");
            CASUALSessionData.getGUI().setReady(true);
            //yeah yeah, overly broad chatch.  read below. 
        } catch (Exception e) {
            /*
            *  Java reports this as an overly broad catch.  Thats fine.  this is
            *  supposed to be broad.  It is the handler for all errors during
            *  execution of CASUAL script.  Script commands are tested for quality
            *  and errors found here will be syntax or oher scripting errors.
            *
            *  CASUAL will take the blame for the end user and the developer will
            *  see that it was a problem with their script
            */
            Log.level0Error("@problemParsingScript");
            Log.level0Error(strLine);
            Log.errorHandler(new RuntimeException("CASUAL scripting error\n   " + strLine, e));
            Log.level0Error("@problemParsingScript");
            Log.level0Error(strLine);

        }

    }

    private void uninstallDriverMaybe() {
        if (DriverInstall.removeDriverOnCompletion == 2) {//2 for remove driver 1 for do not remove
            Log.level2Information("Removing generic USB driver as requested");
            new DriverRemove().deleteOemInf();
        }
    }

    private String doGotoRoutine(BufferedReader bReader, String strLine) throws IOException{
        bReader.reset();
        //use Label method
        while (bReader.ready() &&  !strLine.replaceAll("\\s", "").startsWith("$LABEL" + GOTO) ){
            strLine = bReader.readLine();
        }
        
        //use old compatibility method
        if (strLine==null || !strLine.replaceAll("\\s", "").startsWith("$LABEL" + GOTO) ){
            bReader.reset();
            while (bReader.ready() && !strLine.startsWith(GOTO)) {  //burn blanks at the beginning
                strLine = bReader.readLine();
            }
        }
        if (strLine==null){
            strLine="$ECHO parsed to end of script and could not $GOTO:"+GOTO;
        }
        GOTO = "";
        return strLine;
    }
    
    /**
     * Process a line of CASUAL script.
     *
     * @param line CASUAL line to process
     * @return value returned from CASUAL command
     * @throws java.io.IOException When permissions problem exists
     */
    public String commandHandler(String line) throws Exception {
        
        
        return commandHandler(new Command(line));
    }
    
    /**
     * Process a CASUAL Command.
     *
     * @param cmd CASUAL command to process.
     * @return value returned from CASUAL command
     * @throws java.io.IOException When permissions problem exists
     */
    public String commandHandler(Command cmd) throws Exception {
        
        Log.level3Verbose("COMMAND HANDLER:"+cmd.toString().replace("\n",""));
        
        if (cmd.get().trim().isEmpty()||cmd.get().trim().startsWith("#")||cmd.get().trim().isEmpty()||cmd.get().trim().startsWith("$LABEL")) {
            //Log.level4Debug("received comment");  
            return "";
        }

        //Process Language commands $WINDOWS $MAC $LINUXWINDOWS $LINUXMAC $LINUX
        if (new CASUAL.language.commands.OSCommands().operatingSystemCommands(cmd)){
            Log.level4Debug(cmd+"\n Command not applicable to this OS");
            return cmd.getReturn();
        }
        
        /*
        * VARIABLES
        * check for = in the first part of the command...   VAR=Value
        */
        if (Variables.parseVariablesInCommandString(cmd)) return cmd.getReturn();
        
        /*
        *DEBUG COMMANDS  $SENDLOG
        */
        if (ControlCommands.checkSendLog(sd,cmd)) return cmd.getReturn();

        /*
        * CONTROL COMMANDS
        */
        //SETRETURN  "string"  sets return value
        if (ControlCommands.launchCaspac(sd,cmd)) return cmd.getReturn();
        
        if (ControlCommands.setReturn(cmd)) return cmd.getReturn();

        //$HALT "$CASUAL command" halts and executes the remainder of the line
        ControlCommands.checkHalt(sd,cmd);
        //$GOTO "#comment" goes to a commented line
        if (ControlCommands.checkGoto(cmd)) return cmd.getReturn();
        //$ON  
        if (ControlCommands.checkOn(sd,cmd)) return cmd.getReturn();
        //$CASPAC
        if (ControlCommands.launchCaspac(sd, cmd)) return cmd.getReturn();
        //$CLEARON
        if (ControlCommands.checkClearOn(sd,cmd)) return cmd.getReturn();
        //# comments
        if (ControlCommands.checkComments(cmd)) return cmd.getReturn();
        //
        if (ControlCommands.checkBlankLine(cmd)) return cmd.getReturn();
        // $IFCONTAINS "value" $INCOMMAND "casual command" $DO "casual command"
        if (ControlCommands.checkIfContains(cmd)) return ControlCommands.doIfContainsReturnResults(cmd.get(), true);
        // $IFNOTCONTAINS "value" $INCOMMAND "casual command" $DO "casual command"
        if (ControlCommands.checkIfNotContains(cmd)) return ControlCommands.doIfContainsReturnResults(cmd.get(), false);
        //SLEEP  1
        //SLEEPMILLIS 1000
        if (checkSleep(cmd)) return cmd.getReturn();
        
        if (MathCommands.doMath(cmd)) return cmd.getReturn();
        /*
        * Environmental variables
        */
//$SLASH will replace with "\" for windows or "/" for linux and mac
        if (cmd.get().contains("$BUSYBOX")) {
            cmd.set( cmd.get().replace("$BUSYBOX", BusyboxTools.getBusyboxLocation()));
            Log.level4Debug("Expanded $BUSYBOX: " + cmd.get());
        }

//$SLASH will replace with "\" for windows or "/" for linux and mac
        if (cmd.get().contains("$SLASH")) {
            cmd.set(cmd.get().replace("$SLASH", CASUALSessionData.slash));
            Log.level4Debug("Expanded $SLASH: " + cmd.get());
        }
//$ZIPFILE is a reference to the Script's .zip file
        if (cmd.get().contains("$ZIPFILE")) {

            if (!verifyZIPFILEReferencesExist(cmd.get())) {
                return "";
            }

            cmd.set( cmd.get().replace("$ZIPFILE", ScriptTempFolder));
            Log.level4Debug("Expanded $ZIPFILE: " + cmd.get());
        }

        if (cmd.get().contains("\\n") && (cmd.get().startsWith("$USERNOTIFICATION") || cmd.get().startsWith("$USERNOTIFICATION") || cmd.get().startsWith("$USERCANCELOPTION"))) {
            cmd.set( cmd.get().replace("\\n", "\n"));
        }
//$HOMEFOLDER will reference the user's home folder on the system        
        if (cmd.get().contains("$HOMEFOLDER")) {
            if (!new FileOperations().verifyExists(CASUALHOME)) {
                new FileOperations().makeFolder(CASUALHOME);
            }
            cmd.set( cmd.get().replace("$HOMEFOLDER", CASUALHOME));
            Log.level4Debug("Expanded $HOMEFOLDER" + cmd.get());
        }

        /*
        * GENERAL PURPOSE COMMANDS
        */
//$ECHO command will display text in the main window
        if (cmd.get().startsWith("$ECHO")) {
            Log.level4Debug("Received ECHO command" + cmd.get());
            cmd.setReturn(true,cmd.get().replace("$ECHO", "").trim());
            Log.level2Information(cmd.get());
            CASUALSessionData.getGUI().setUserSubMessage(cmd.getReturn());
            return cmd.getReturn();
        
            //TODO: should this be updated automatically by monitoring or by this new command?
            //I think automatic is the way.. triggered on ADB push, pull, heimdall, and others.
//$TITLE command is used to inform the user that a new portion of the process has started
        } else if (cmd.get().startsWith("$TITLE")){
            Log.level4Debug("Received ECHO command" + cmd.get());
            cmd.setReturn(true,cmd.get().replace("$ECHO", "").trim());
            CASUALSessionData.getGUI().setUserMainMessage(cmd.get());
            return cmd.getReturn();
        
//$LISTDIR will a folder on the host machine  Useful with $ON COMMAND
        } else if (cmd.get().startsWith("$LISTDIR")) {
            cmd.set( cmd.get().replace("$LISTDIR", "").trim());
            if (OSTools.isLinux() || OSTools.isMac()) {
            } else {
                cmd.set(cmd.get().replace("/", CASUALSessionData.slash));
            }
            File[] files = new File(cmd.get()).listFiles();
            String retval = "";
            if (files != null && files.length > 0 && new ADBTools().isConnected()) {
                for (File file : files) {
                    retval = retval + file.getAbsolutePath() + "\n";
                    try {
                        commandHandler("adb shell \"echo " + file.getCanonicalPath() + "\"");
                    } catch (IOException ex) {
                        Log.errorHandler(ex);
                    }
                }
            } else if (files !=null && files.length>0){
                for (File file:files){
                    retval=retval+file+"\n";   
                }
            }
            return retval;

// $MAKEDIR will make a folder
        } else if (cmd.get().startsWith("$MAKEDIR")) {
            cmd.set( cmd.get().replace("$MAKEDIR", "").trim());
            Log.level4Debug("Creating Folder: " + cmd.get());
            new File(cmd.get()).mkdirs();
            return cmd.get();
// $REMOVEDIR will make a folder
        } else if (cmd.get().startsWith("$REMOVEDIR")) {
            cmd.set( cmd.get().replace("$REMOVEDIR", "").trim());
            Log.level4Debug("Creating Folder: " + cmd.get());
            new FileOperations().recursiveDelete(cmd.get());
            return cmd.get();
            
// Takes a value from a command and returns to text box        
        } else if (cmd.get().startsWith("$COMMANDNOTIFICATION")) {
            cmd.set( cmd.get().replace("$COMMANDNOTIFICATION", "").trim());
            String title = "Return Value";
            String retval = commandHandler(cmd.get());
            new CASUALMessageObject(title + ">>>" + retval).showCommandNotification();
            return retval;
            
//$USERNOTIFICATION will stop processing and force the user to
            // press OK to continueNotification 
        } else if (cmd.get().startsWith("$USERNOTIFICATION")) {
            cmd.set( cmd.get().replace("$USERNOTIFICATION", "").trim());
            new CASUALMessageObject(cmd.get().replaceFirst(",", ">>>")).showUserNotification();
            return "";

// $USERCANCELOPTION will give the user the option to halt the script
            //USE: $USERCANCELOPTION Message
            //USE: $USERCANCELOPTION Title, Message
        } else if (cmd.get().startsWith("$USERCANCELOPTION")) {
            //CASUALAudioSystem CAS = new CASUALAudioSystem();
            int n;
            cmd.set( cmd.get().replace("$USERCANCELOPTION", "").trim());
            n = new CASUALMessageObject(cmd.get().replaceFirst(",", ">>>")).showUserCancelOption();
            if (n == 1) {
                Log.level0Error(this.CASPAC.getActiveScript().getName());
                Log.level0Error("@canceledAtUserRequest");
                sd.CASPAC.getActiveScript().setScriptContinue(false);
                return "";
            }
            return "";
            
//$ACTIONREQUIRED Message            
        } else if (cmd.get().startsWith("$ACTIONREQUIRED")) {
            cmd.set( cmd.get().replace("$ACTIONREQUIRED", "").trim());
            int n = new CASUALMessageObject(cmd.get().replaceFirst(",", ">>>")).showActionRequiredDialog();
            if (n == 1) {
                Log.level0Error(this.CASPAC.getActiveScript().getName());
                Log.level0Error("@haltedPerformActions");
                sd.CASPAC.getActiveScript().setScriptContinue(false);
                return "";
            }
            return "";

//$USERINPUTBOX will accept a String to be injected into ADB
            //Any text will be injected into the $USERINPUT variable    
            //USE: $USERINPUTBOX Title, Message, command $USERINPUT
        } else if (cmd.get().startsWith("$USERINPUTBOX")) {
            //cmd.set( line.replace("\\n", "\n");
            String[] Message = cmd.get().replace("$USERINPUTBOX", "").split(",", 3);
            String inputBoxText = new CASUALMessageObject(Message[0] + ">>>" + Message[1]).inputDialog();
            if (inputBoxText == null) {
                inputBoxText = "";
            }
            inputBoxText = returnSafeCharacters(inputBoxText);

            Log.level4Debug(inputBoxText);

            String command = Message[2].replace("$USERINPUT", inputBoxText);
            this.commandHandler(command);

            return "";
//$DOWNLOAD from, to, friendly download name,  Optional standard LINUX MD5 command ouptut.

        } else if (cmd.get().startsWith("$DOWNLOAD")) {
            cmd.set( cmd.get().replace("$DOWNLOAD", "").trim());
            String[] downloadCommand = cmd.get().split(",");
            for (int i = 0; i < downloadCommand.length; i++) {
                downloadCommand[i] = downloadCommand[i].trim();
            }
            FileOperations fo = new FileOperations();
            Log.level4Debug("Downloading " + downloadCommand[2]);
            Log.level4Debug("From " + downloadCommand[0]);
            Log.level4Debug("to " + downloadCommand[1]);
            if (!new File(downloadCommand[1]).getParentFile().exists()) {
                new File(downloadCommand[1]).getParentFile().mkdirs();
            }

            if (downloadCommand.length == 3) {
                new CASUALUpdates(sd).downloadFileFromInternet(downloadCommand[0], downloadCommand[1], downloadCommand[2]);
                return downloadCommand[1];
            } else if (downloadCommand.length == 4) {
                new CASUALUpdates(sd).downloadFileFromInternet(downloadCommand[0], downloadCommand[1], downloadCommand[2]);
                if (!new MD5sum().compareMD5StringsFromLinuxFormatToFilenames(new String[]{downloadCommand[3]}, new String[]{downloadCommand[1]})) {
                    new CASUALScriptParser().executeOneShotCommand("$HALT HALTING Downloaded md5sum did not check out");
                }
                return "";
            } else {
                Log.level0Error("Invalid download command");
                return "Invalid Download Command";
            }

//$EXECUTE will blindly execute commands into the shell.  Usefull only with $LINUX $WINDOWS or $MAC commands.
        } else if (cmd.get().startsWith("$EXECUTE")) {
            cmd.set( cmd.get().replace("$EXECUTE", "").trim());
            ArrayList<String> command = new ShellTools().parseCommandLine(cmd.get());
            String[] commandArray = Arrays.copyOf(command.toArray(), command.size(), String[].class);
            return new Shell().sendShellCommand(commandArray);

//$BUILDPROP will silently grab the build.prop from the device
        } else if (cmd.get().startsWith("$BUILDPROP")) {
            if (deviceBuildPropStorage != null && deviceBuildPropStorage.contains("ro.")) {
                return deviceBuildPropStorage;
            } else {
                String[] com = {new ADBTools().getBinaryLocation(), "shell", "cat /system/build.prop"};
                deviceBuildPropStorage = new Shell().timeoutShellCommand(com, 5000);
                return deviceBuildPropStorage;
            }
//$FLASH will push a file to the specified block eg $FLASH $ZIPFILEmyFile, /dev/block/mmcblk0p5
        } else if (cmd.get().startsWith("$FLASH")) {
            Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALDataBridgeFlash);
            cmd.set( cmd.get().replace("$FLASH", "").trim());
            if (!cmd.get().contains(",")) {
                Log.level0Error("Missing Comma in CASUAL Data Bridge $FLASH command");
                throw new RuntimeException("no comma to split and specify destination");
            }
            String[] split = cmd.get().split(",");
            File f = new File(split[0].replace("\"", "").trim());
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(CASUALLanguage.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                long x = new CASUALDataBridge().sendFile(f, split[1].trim());
                if (x != f.length()) {
                    new CASUALMessageObject("@interactionUltimateFlashFailure").showErrorDialog();
                }
                return "Pushed " + x + " bytes";
            } catch (FileNotFoundException ex) {
                Log.level0Error("@fileNotFound");
                throw new RuntimeException("File not found");
            } catch (Exception ex) {
                Log.level0Error("@failedToWriteFile");
                throw new RuntimeException("Failed to write file");
            }
//$PULL will push a file to the specified block eg $PULL  /dev/block/mmcblk0p5 , $ZIPFILEmyFile
        } else if (cmd.get().startsWith("$PULL")) {
            Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALDataBridgePull);
            cmd.set( cmd.get().replace("$PULL", "").trim());
            if (!cmd.get().contains(",")) {
                Log.level0Error("Missing Comma in $PULL command");
                throw new RuntimeException("no comma to split and specify destination");
            }
            String[] split = cmd.get().split(",");
            File f = new File(split[1].replace("\"", "").trim());

            new File(f.getParent()).mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ex) {
            }
            return new CASUALDataBridge().integralGetFile(split[0].trim(), f);

            /*
            * SUPPORTED SHELLS
            */
            // if Heimdall, Send to Heimdall shell command
        } else if (cmd.get().startsWith("$HEIMDALL")||cmd.get().startsWith("heimdall")) {
            Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.Heimdall);
            if (cmd.get().startsWith("heimdall")){
                cmd.set(cmd.get().replaceFirst("heimdall",""));
            } else if (cmd.get().startsWith("$HEIMDALL")){
                cmd.set(cmd.get().replaceFirst("$HEIMDALL", ""));
            }
            
            cmd.set(cmd.get().replace("$HEIMDALL", ""));
            Log.level4Debug("Received Command: " + cmd.get());
            Log.level4Debug("CASUALLanguage- verifying Heimdall deployment.");
           
            Track.setMode(LanguageTracker.heimdall(cmd));
            if (!new HeimdallTools().run(new String[]{"detect"},5000, true).contains("CritERROR!!!")) {
                ArrayList<String> intermediateCommand=new ShellTools().parseCommandLine(cmd.get());
                String[] command=intermediateCommand.toArray(new String[intermediateCommand.size()]);
                Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.HeimdalSearching);
                if (!command[0].equals("detect")){
                    new HeimdallTools().waitForDevice();
                    Track.setMode(LanguageTracker.heimdall(cmd));
                    
                }
                
                /* if (sd.isLinux()) {   //Is this needed?
                doElevatedHeimdallShellCommand(line);
                }*/
                Log.level2Information("@executingHeimdall");
                
                
                return new HeimdallTools().doHeimdallShellCommand(command);
                
            } else {
                return new CASUALScriptParser().executeOneShotCommand("$HALT $ECHO You must install Heimdall!");
            }
// if Fastboot, Send to fastboot shell command
        } else if (cmd.get().startsWith("$FASTBOOT") ||(cmd.get().startsWith("fastboot"))) {
            Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.Fastboot);
            if (cmd.get().startsWith("fastboot")){
                cmd.set(cmd.get().replaceFirst("fastboot", ""));
            } else if (cmd.get().startsWith("$FASTBOOT")){
                cmd.set(cmd.get().replaceFirst("\\$FASTBOOT",""));
            }
            Log.level4Debug("received fastbot command.");
            new FastbootTools().getBinaryLocation();
            Log.level2Information("@waitingForDownloadModeDevice");
            Track.setMode(LanguageTracker.fastboot(cmd));
            if (OSTools.isLinux() && !cmd.get().isEmpty() && !cmd.get().equals("--help")) {
                Log.level2Information("@linuxPermissionsElevation");

                String returnValue = new FastbootTools().doElevatedFastbootShellCommand(cmd.get().replaceAll("\"", "\\\""));
                if (!returnValue.contentEquals("\n")) {
                    return returnValue;
                }
                //}

            } else {
                return new FastbootTools().doFastbootShellCommand(cmd.get());
            }

            // if Fastboot, Send to fastboot shell command
        } else if (cmd.get().startsWith("$ADB") || cmd.get().startsWith("adb")) {
            Track.setMode(LanguageTracker.adb(cmd));
            if (cmd.get().startsWith("adb")){
                cmd.set(cmd.get().replaceFirst("adb",""));
            } else if (cmd.get().startsWith("$ADB")){
                cmd.set(cmd.get().replaceFirst("\\$ADB",""));
            }
            String retVal = doShellCommand(cmd.get(), null, null);
            Log.level4Debug("return from ADB:" + retVal);
            return retVal;
                
// if no prefix, then send command directly to ADB.
        } else {
            Track.setMode(CASUAL.instrumentation.ModeTrackerInterface.Mode.CASUALFinishedFailure);
            StringBuilder sb=new StringBuilder();
            if (cmd.getReturnPassedOrFailed()){
                return cmd.getReturn();
            }
            sb.append("ERROR!!!!  Invalid Command:\"").append(cmd.get()).append("\" is not recognized as a valid command");
            throw new IOException(sb.toString());
        }
        return "";
    }
//END OF SCRIPT PARSER

    
    
    private boolean checkSleep(Command cmd) throws RuntimeException {
        if (cmd.get().startsWith("$SLEEP")) {
            Log.level3Verbose("detected sleep command: " + cmd.get());
            int sleeptime;
            cmd.set( cmd.get().replace("$SLEEP", "").trim());
            if (cmd.get().startsWith("MILLIS")) {
                cmd.set( cmd.get().replace("MILLIS", "").trim());
                sleeptime = Integer.parseInt(cmd.get());
            } else {
                sleeptime = Integer.parseInt(cmd.get()) * 1000;
            }
            if (!(Integer.parseInt(cmd.get()) >= 0)) {
                throw new RuntimeException();
            }
            try {
                Log.level2Information("sleeping for " + sleeptime / 1000 + " seconds");
                Thread.sleep(sleeptime);
            } catch (InterruptedException ex) {
            }
            return true;
        }
        return false;
    }


    private String returnSafeCharacters(String Str) {
        Str = Str.replace("\\", "\\\\");
        Str = Str.replace("\"", "\\\"");
        Str = Str.replace("\'", "\\\'");

        return Str;
    }

    private String doShellCommand(String Line, String ReplaceThis, String WithThis) {
        return executeADBCommand(Line, ReplaceThis, WithThis, true);
    }

    /*
    * doShellCommand is the point where the shell is activated ReplaceThis
    * WithThis allows for a last-minute insertion of commands by default
    * ReplaceThis should be null.
    */
    private String executeADBCommand(String Line, String ReplaceThis, String WithThis, boolean parseError) {
        Line = StringOperations.removeLeadingSpaces(Line);

        if (Line.startsWith("wait-for")) {
            Log.level2Information("@waitingForDeviceToBeDetected");
        }

        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList<String>();
        ShellCommand.add(new ADBTools().getBinaryLocation());
        ShellCommand.addAll(new ShellTools().parseCommandLine(Line));
        String StringCommand[] = StringOperations.convertArrayListToStringArray(ShellCommand);
        if (ReplaceThis != null) {
            for (int i = 0; i < StringCommand.length; i++) {
                StringCommand[i] = StringCommand[i].replace(ReplaceThis, WithThis);
            }
        }
        Log.level4Debug("sending");
        if (parseError) {
            return Shell.liveShellCommand(StringCommand, true);
        } else {
            return Shell.sendShellCommandIgnoreError(StringCommand);
        }

    }

    private boolean verifyFileExists(String testFileString) {
        if (new FileOperations().verifyExists(testFileString)) {
            //exists
            Log.level3Verbose("verified " + testFileString + " exists");
        } else {
            testFileString = testFileString.replace(",", "");
            if (new FileOperations().verifyExists(testFileString)) {
                //exists
                Log.level3Verbose("verified " + testFileString + " exists");
                return true;
            }
            return false;
        }
        return true;
    }

    private void fileNotFound() {
        int n = new CASUALMessageObject("@interactionMissingFileVirusScanner").showUserCancelOption();
        if (n == 1) {
            Log.level0Error(this.CASPAC.getActiveScript().getName());
            Log.level0Error("@canceledDueToMissingFiles");
            sd.CASPAC.getActiveScript().setScriptContinue(false);
        }
    }

    private boolean verifyZIPFILEReferencesExist(String line) {
        //break commandline into an array of arguments
        //verify zipfile reference exists
        //allow echo of zipfile
        if (!line.startsWith("$USERINPUTBOX") && !line.startsWith("$MAKEDIR") && !line.startsWith("$PULL") && !line.startsWith("$DOWNLOAD") && !line.startsWith("$ECHO") && !line.startsWith("$REMOVEDIR") && !line.startsWith("$COMMANDNOTIFICATION") && !line.startsWith("$MAKEDIR") && !line.contains(" shell echo ") && !line.startsWith("$USERNOTIFICATION") && !line.contains(" pull ")) {
            String[] lineArray = line.split(" ");
            //loop through line, locate positions of $ZIPFILE and test
            int pos = 0;
            while (pos < lineArray.length) {
                if (lineArray[pos].contains("$ZIPFILE")) {
                    String zipRef = lineArray[pos].replace("$ZIPFILE", ScriptTempFolder).replace("\"", "");
                    //if $ZIPFILE is a folder reference verify and continue
                    if (lineArray[pos].endsWith("$ZIPFILE\"") || lineArray[pos].equals("$ZIPFILE")) {
                        if (!verifyFileExists(zipRef)) {
                            fileNotFound();
                            return false;
                        }
                        pos++;
                        continue;
                    } else {
                        //$ZIPFILE is not a folder reference
                        //if we are at the last arg
                        if (verifyFileExists(zipRef)) {
                            pos++;
                            continue; //zipfile at the end of the line
                        }

                        boolean fileFound = false;
                        //test to end of string or until next ZIPFILE reference
                        String instanceString = zipRef;
                        for (int instancePos = pos + 1; instancePos < lineArray.length; instancePos++) {
                            if (lineArray[instancePos].contains("$ZIPFILE")) {
                                break;//new zipfile ref without previous found.
                            }
                            instanceString = instanceString + " " + lineArray[instancePos];
                            if (verifyFileExists(instanceString)) {
                                fileFound = true;
                                pos++;
                                break;
                            }
                        }
                        if (!fileFound) {
                            fileNotFound();
                        }
                    }
                }
                pos++;
            }
        }
        return true;
    }
}
