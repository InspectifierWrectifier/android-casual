/*
 * Copyright (c) 2011 Adam Outler
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package CASUAL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
//define <output and input> to this abstract class
public class Shell implements Runnable {

    //for internal access
    public Shell() {
    }
    //for external access
    Log log = new Log();
    //Send a command to the shell

    public String elevateSimpleCommand(String[] cmd) {
        String NewCmd = "";
        FileOperations FileOperations = new FileOperations();
        Shell Shell = new Shell();
        String Result = "";


        String Command = "";
        for (int i = 0; i < cmd.length; i++) {
            Command = Command + cmd[i] + " ";
        }

        String[] newCmd;
        if (Statics.isLinux()) {
            String[] TestGKSudo = {"which", "gksudo"};
            String TestReturn = Shell.sendShellCommand(TestGKSudo);
            if ((TestReturn.contains("CritERROR!!!") || (TestReturn.equals("")))) {
                TimeOutOptionPane TO = new TimeOutOptionPane();
                TO.showTimeoutDialog(60, null, "Please install package 'gksudo'", "GKSUDO NOT FOUND", TO.OK_OPTION, TO.ERROR_MESSAGE, null, null);
            }


            String ScriptFile = Statics.TempFolder + "ElevateScript.sh";
            try {
                FileOperations.writeToFile(Command, ScriptFile);
            } catch (IOException ex) {
                Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
            }
            FileOperations.setExecutableBit(ScriptFile);
            String[] SendCommand = {"gksudo", "-k", "-D", "CASUAL", ScriptFile};
            Result = Shell.sendShellCommand(SendCommand);
        } else if (Statics.isMac()) {
            String ScriptFile = Statics.TempFolder + "ElevateScript.sh";
            try {
                FileOperations.writeToFile(""
                        + "#!/bin/sh \n"
                        + "export bar=\"" + Command + "\"\n"
                        + "for i in \"$@\"; do export bar=\"$bar '${i}'\";done"
                        + "osascript -e \"do shell script \"$bar\" with administrator privileges\""
                        + "", ScriptFile);

            } catch (IOException ex) {
                Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
            }
            FileOperations.setExecutableBit(ScriptFile);
            String[] MacCommand = {ScriptFile};
            Result = sendShellCommand(MacCommand);
        } else if (!Statics.OSName.equals("Windows XP")) {
            newCmd = new String[cmd.length + 2];
            newCmd[0] = Statics.WinElevatorInTempFolder;
            newCmd[1] = "-wait";
            for (int i = 2; i < cmd.length + 2; i++) {
                newCmd[i] = cmd[i - 2] + " ";
            }

            Result = sendShellCommand(newCmd);

        }

        return Result;
    }

    public String sendShellCommand(String[] cmd) {
        log.level3("\n###executing: " + cmd[0] + "###");
        String AllText = "";
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
            }
            while ((line = STDERR.readLine()) != null) {
                AllText = AllText + "\n" + line;
            }
            while ((line = STDOUT.readLine()) != null) {
                AllText = AllText + "\n" + line;
                while ((line = STDERR.readLine()) != null) {
                    AllText = AllText + "\n" + line;
                }
            }
            //log.level0(cmd[0]+"\":"+AllText);
            return AllText;
        } catch (IOException ex) {
            log.level2("Problem while executing" + arrayToString(cmd)
                    + " in Shell.sendShellCommand() Received " + AllText);
            return "CritERROR!!!";
        }

    }

    public String silentShellCommand(String[] cmd) {

        String AllText = "";
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = STDOUT.readLine()) != null) {
                AllText = AllText + "\n" + line;

            }
            return AllText;
        } catch (IOException ex) {
            return "error";
        }

    }

    public String arrayToString(String[] stringarray) {
        String str = " ";
        for (int i = 0; i < stringarray.length; i++) {
            str = str + " " + stringarray[i];
        }
        log.level3("arrayToString " + stringarray + " expanded to: " + str);
        return str;
    }

    private boolean testForException(Process process) {

        if (process.exitValue() >= 0) {

            return false;
        } else {
            return true;
        }


    }

    public void liveShellCommand(String[] params) {

        boolean LinkLaunched = false;
        try {
            Process process = new ProcessBuilder(params).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String LineRead="";
            String CharRead;
            String LogRead="";

            int c;
            while ((c = STDOUT.read()) > -1) {

                CharRead = Character.toString((char) c);
                LineRead = LineRead + CharRead;
                LogRead=LogRead+CharRead;
                log.progress(CharRead);
                
                if (Statics.ActionEvents != null && (LineRead.contains("\n") || LineRead.contains("\r"))){
                    for (int i = 0; i<= Statics.ActionEvents.size() -1 ;i++){
                        if (Statics.ActionEvents !=null && LineRead.contains((String)Statics.ActionEvents.get(i))){
                          
                          String LastLine=StringOperations.replaceLast(LineRead, "\n", "");  
                          LastLine=StringOperations.replaceLast(LastLine, "\r", "");
                          Statics.LastLineReceived=LastLine;
                          new CASUALScriptParser().executeOneShotCommand((String) Statics.ReactionEvents.get(i));
                        }
                    }
                    LineRead="";
                              
                }
            }
            while ((LineRead = STDERR.readLine()) != null) {
                log.progress(LineRead);
            }
            log.level3(LogRead);

        } catch (IOException ex) {
            String[] ArrayList = (String[]) Statics.LiveSendCommand.toArray();
            log.level2("Problem while executing" + ArrayList
                    + " in Shell.liveShellCommand()");
            Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void liveBackgroundShellCommand() {


        Runnable r = new Runnable() {

            public void run() {
                boolean LinkLaunched = false;
                try {
                    String[] params = (String[]) Statics.LiveSendCommand.toArray(new String[0]);
                    Process process = new ProcessBuilder(params).start();
                    BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String LineRead = null;
                    String CharRead = null;
                    String LogData="";
                    boolean ResetLine = false;
                    int c;
                    while ((c = STDOUT.read()) > -1) {
                        if (ResetLine) {
                            log.beginLine();
                            ResetLine = !ResetLine;
                        }
                        CharRead = Character.toString((char) c);
                        LineRead = LineRead + CharRead;
                        log.progress(CharRead);
                        LogData=LogData+CharRead.toString();
                    }
                    while ((LineRead = STDERR.readLine()) != null) {
                        log.progress(LineRead);
                    }
                    new Log().level2(LogData);

                } catch (IOException ex) {
                    String[] ArrayList = (String[]) Statics.LiveSendCommand.toArray();
                    log.level2("Problem while executing" + ArrayList
                            + " in Shell.liveShellCommand()");
                    Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}