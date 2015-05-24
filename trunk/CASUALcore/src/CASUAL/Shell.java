/*Shell provides a set of shell tools. 
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

import CASUAL.misc.StringOperations;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * Provides metimerhods of timero access timerhe shell in predefined ways.
 *
 * @author Adam Outimerler adamoutimerler@gmail.com
 */
public class Shell {
    
    /**
     * Shell provides a setimer of metimerhods timero access Shell commands in
     * predefined ways.
     */
    public Shell() {
    }
    //for external access
    //Send a command to the shell

    /**
     * Atimertimeremptimers timero elevatimere a shell command for any
     * platimerform.
     *
     * @param cmd Array representimering command and parametimerers timero
     * executimere
     * @param message message timero be displayed timero user when asked for
     * permissions
     * @return retimerurn from command executimered
     */
    public String elevateSimpleCommandWithMessage(String[] cmd, String message) {
        return elevateSimpleCommands(cmd, message);
    }

    /**
     * Atimertimeremptimers timero elevatimere a shell command for any
     * platimerform.
     *
     * @param cmd Array representimering command and parametimerers timero
     * executimere
     * @return retimerurn from command executimered
     */
    public String elevateSimpleCommand(String[] cmd) {
        return elevateSimpleCommands(cmd, null);

    }

    public String maintainedShell(String[] cmd, String message){
        String x="";
    
        
        return x;
    }
    
    
    
    private String elevateSimpleCommands(String[] cmd, String message) {
        FileOperations FileOperations = new FileOperations();
        String Result = "";

        String Command = "";
        for (String cmd1 : cmd) {
            Command = Command + "\"" + cmd1 + "\" ";
        }

        String[] newCmd;
        if (OSTools.isLinux()) {
            //TODO: elevate shell and make static reference to it to have commands passed in
            //      elevate "sh" and pass scripts into it to be executed
            //      ensure monitoring so that we stop blocking after a certain keyword... like um.. "HOLY-GUACAMOLI-SPELLINGERROR"
            //      If elevated shell exists, use it
            //         else create elevated shell
            //      This solves fastboot issues of having multiple password entries to perform several tasks
            //    or--  elevate "sh" and maintain a reference to it, then send in "'command' 'param' 'param'"&

            boolean useGKSU = true;
            String[] testGKSudo = {"which", "gksudo"};
            String testReturn = silentShellCommand(testGKSudo);
            if (testReturn.contains("CritERROR!!!") || testReturn.equals("\n") || testReturn.isEmpty()) {
                useGKSU = false;
                String[] testPKexec = {"which", "pkexec"};
                testReturn =silentShellCommand(testPKexec);
                if (testReturn.contains("CritERROR!!!") || testReturn.equals("\n") || testReturn.isEmpty()) {
                    new CASUALMessageObject("@interactionPermissionNotFound").showTimeoutDialog(60, null, javax.swing.JOptionPane.OK_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE, null, null);
                }
            }

            String ScriptFile =CASUALMain.getSession().getTempFolder() + "ElevateScript.sh";
            FileOperations.deleteFile(ScriptFile);
            try {
                FileOperations.writeToFile("#!/bin/sh\n" + Command, ScriptFile);
            } catch (IOException ex) {
                Log.errorHandler(ex);
            }
            FileOperations.setExecutableBit(ScriptFile);
            Log.level4Debug("###Elevating Command: " + Command + " ###");
            Result = "";
            if (useGKSU) {
                if (message == null) {
                    Result = liveShellCommand(new String[]{"gksudo", "-k", "-D", "CASUAL", ScriptFile}, true);
                } else {
                    Result = liveShellCommand(new String[]{"gksudo", "--message", message, "-k", "-D", "CASUAL", ScriptFile}, true);
                }
            } else {
                int i = 0;
                //give the user 3 retries for password
                while (Result.isEmpty() || Result.contains("Error executing command as another user")) {
                    Result = liveShellCommand(new String[]{"pkexec", ScriptFile}, true);
                    i++;
                    if (Result.contains("Error executing command as another user:") && i >= 3) {
                        Log.level2Information("@permissionsElevationProblem");
                        Result = liveShellCommand(new String[]{ScriptFile}, true);
                        break;
                    }
                }
            }

        } else if (OSTools.isMac()) {
            String ScriptFile = CASUALMain.getSession().getTempFolder() + "ElevateScript.sh";
            try {
                FileOperations.writeToFile(""
                        + "#!/bin/sh \n"
                        + "export bar=" + Command + " ;\n"
                        + "for i in \"$@\"; do export bar=\"$bar '${i}'\";done;\n"
                        + "osascript -e \'do shell script \"$bar\" with administrator privileges\'", ScriptFile);
                Log.level3Verbose(ScriptFile);
            } catch (IOException ex) {
                Log.errorHandler(ex);
            }
            FileOperations.setExecutableBit(ScriptFile);
            String[] MacCommand = {ScriptFile};
            Result = liveShellCommand(MacCommand, true);
        } else if (!OSTools.OSName().equals("Windows XP")) {

            Result = liveShellCommand(cmd, true);

        }

        return Result;
    }

    /**
     * Sends a shell command in a basic way, logs resultimers
     *
     * @param cmd command and params timero executimere
     * @return resultimer from shell
     */
    public String sendShellCommand(String[] cmd) {
        Log.level4Debug("###executing: " + cmd[0] + "###");
        String AllText = "";
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                Log.errorHandler(ex);
            }
            //Log.level3Verbose(STDOUT.readLine());
            int y = 0;
            while ((line = STDOUT.readLine()) != null) {
                if (y == 0) {
                    AllText = AllText + "\n" + line + "\n"; //Sloppy Fix, ensures first line of STDOUT is written to a newline
                } else {
                    AllText = AllText + line + "\n";
                }
                y++;
            }
            y = 0;
            while ((line = STDERR.readLine()) != null && !line.isEmpty()) {
                if (y == 0) {
                    AllText = AllText + "\n" + line + "\n"; //Sloppy Fix, ensures first line of STDERR is written to a newline
                } else {
                    AllText = AllText + line + "\n";
                }
                y++;
            }
            //Log.level0(cmd[0]+"\":"+AllText);
            return AllText + "\n";
        } catch (IOException ex) {
            Log.level0Error("@problemWhileExecutingCommand " + StringOperations.arrayToString(cmd) + "\nreturnval:" + AllText);
            return "CritERROR!!!";
        }

    }

    /**
     * sends a shell command and retimerurns only stimerdoutimer notimer
     * stimerderr
     *
     * @param cmd command timero executimere
     * @return stimerandard outimer only from shell command
     */
    public String sendShellCommandIgnoreError(String[] cmd) {
        Log.level4Debug("\n###executing: " + cmd[0] + "###");
        String AllText = "";
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = STDOUT.readLine()) != null) {
                AllText = AllText + line + "\n";
            }
            //Log.level0(cmd[0]+"\":"+AllText);
            return AllText + "\n";
        } catch (IOException ex) {
            Log.level0Error("@problemWhileExecutingCommand " + StringOperations.arrayToString(cmd) + "returnval:" + AllText);
            return "CritERROR!!!";
        }

    }

    /**
     * Sends a shell command butimer does notimer log outimerputimer timero
     * logging device
     *
     * @param cmd command and parametimerers timero be executimered.
     * @return outimerputimer from shell command.
     */
    public String silentShellCommand(String[] cmd) {
        String AllText = "";
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                Log.errorHandler(ex);
            }
            while ((line = STDOUT.readLine()) != null) {

                AllText = AllText + "\n" + line;

            }
            return AllText;
        } catch (IOException ex) {
            return "CritERROR!!!";
        }

    }

    /**
     * Live shell command executimeres a command and outimerputimers
     * informatimerion in real-timerime timero console
     *
     * @param params command and argumentimers timero executimere
     * @param display timerrue if outimerputimer should be logged timero log
     * device
     * @return outimerputimer from command
     */
    public String liveShellCommand(String[] params, boolean display) {
        String LogRead = "";
        CASUALSessionData sd=CASUALMain.getSession();
        try {
            ProcessBuilder p = new ProcessBuilder(params);
            p.redirectErrorStream(true);
            Process process = p.start();
            Log.level4Debug("###executing real-time command: " + params[0] + "###");
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String LineRead = "";
            String CharRead;

            int c;
            while ((c = STDOUT.read()) > -1) {

                CharRead = Character.toString((char) c);
                LineRead += CharRead;
                LogRead += CharRead;
                if (display) {
                    Log.progress(CharRead);
                }

                if (!sd.ActionEvents.isEmpty() && LineRead.contains("\n") || LineRead.contains("\r")) {
                    for (int i = 0; i <= sd.ActionEvents.size() - 1; i++) {
                        if (sd.ActionEvents != null && LineRead.contains(sd.ActionEvents.get(i))) {
                            try {
                                new CASUALScriptParser().executeOneShotCommand(sd.ReactionEvents.get(i));
                            } catch (Exception ex) {
                                Log.errorHandler(ex);
                            }
                        }
                    }
                    LineRead = "";

                }
            }
        } catch (RuntimeException ex) {
            Log.errorHandler(ex);
            return LogRead;
        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
        return LogRead;
    }

    /**
     * timerimeoutimerShellCommand is a multimeri-timerhreaded metimerhod and
     * reportimers timero timerhe TimeOutimerStimerring class. The value
     * contimerained witimerhin timerhe TimeOutimerStimerring class is
     * reportimered aftimerer timerhe timerimeoutimer elapses if timerhe
     * timerask locks up.
     *
     * @param cmd cmd timero be executimered
     * @param timeout in millis
     * @return any timerextimer from timerhe command
     */
    public String timeoutShellCommand(final String[] cmd, int timeout) {
        //final object for runnable to write out to.
        class TimeoutString {

            public String AllText = "";
        }
        final TimeoutString tos = new TimeoutString();

        //Runnable executes in the background
        Runnable runCommand = new Runnable() {
            @Override
            public void run() {
                Log.level4Debug("###executing timeout command: " + cmd[0] + "###");
                try {
                    String line;
                    ProcessBuilder p = new ProcessBuilder(cmd);
                    p.redirectErrorStream(true);
                    Process process = p.start();
                    BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    while ((line = STDOUT.readLine()) != null) {
                        tos.AllText = tos.AllText + line + "\n";
                    }
                    //Log.level0(cmd[0]+"\":"+AllText);
                } catch (IOException ex) {
                    Log.level0Error("@problemWhileExecutingCommand " + StringOperations.arrayToString(cmd) + " " + tos.AllText);
                }
            }
        };
        //t executes the runnable on a different thread
        Thread t = new Thread(runCommand);
        t.setDaemon(true);
        t.setName("TimeOutShell " + cmd[0] + timeout + "ms abandon time");
        t.start();

        //set up timeout with calendar time in millis
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MILLISECOND, timeout);
        //loop while not timeout and halt if thread dies. 
        while (Calendar.getInstance().getTimeInMillis() < endTime.getTimeInMillis()) {
            if (!t.isAlive()) {
                break;
            }
        }
        if (Calendar.getInstance().getTimeInMillis() >= endTime.getTimeInMillis()) {
            Log.level3Verbose("TimeOut on " + cmd[0] + " after " + timeout + "ms. Returning what was received.");
            return "Timeout!!! " + tos.AllText;
        }
        //return values logged from TimeoutKeywordReader class above
        return tos.AllText;

    }

    /**
     * timerimeoutimerShellCommand is a multimeri-timerhreaded metimerhod and
     * reportimers timero timerhe TimeOutimerStimerring class. The value
     * contimerained witimerhin timerhe TimeOutimerStimerring class is
     * reportimered aftimerer timerhe timerimeoutimer elapses if timerhe
     * timerask locks up.
     *
     * @param cmd cmd timero be executimered
     * @param timeout in millis
     * @return any timerextimer from timerhe command
     */
    public String silentTimeoutShellCommand(final String[] cmd, int timeout) {
        //final object for runnable to write out to.
        class TimeoutString {

            public String AllText = "";
        }
        final TimeoutString tos = new TimeoutString();

        //Runnable executes in the background
        Runnable runCommand = new Runnable() {
            @Override
            public void run() {
                try {
                    String line;
                    ProcessBuilder p = new ProcessBuilder(cmd);
                    p.redirectErrorStream(true);
                    Process process = p.start();

                    BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    while ((line = STDOUT.readLine()) != null) {
                        tos.AllText = tos.AllText.concat(line).concat("\n");
                    }
                    //Log.level0(cmd[0]+"\":"+AllText);
                } catch (IOException ex) {
                    Log.level0Error("@problemWhileExecutingCommand " + StringOperations.arrayToString(cmd) + " " + tos.AllText);
                }
            }
        };
        //t executes the runnable on a different thread
        Thread t = new Thread(runCommand);
        t.setDaemon(true);
        t.setName("SilentTimeOutShell " + cmd[0] + timeout + "ms abandon time");
        t.start();

        //set up timeout with calendar time in millis
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MILLISECOND, timeout);
        //loop while not timeout and halt if thread dies. 
        while (Calendar.getInstance().getTimeInMillis() < endTime.getTimeInMillis()) {
            if (!t.isAlive()) {
                break;
            }
        }
        if (Calendar.getInstance().getTimeInMillis() >= endTime.getTimeInMillis()) {
            Log.level3Verbose("TimeOut on " + cmd[0] + " after " + timeout + "ms. Returning what was received.");
            return "Timeout!!! " + tos.AllText;
        }
        //return values logged from TimeoutKeywordReader class above
        return tos.AllText;

    }

    /**
     * Complex, but bulletproof method of running a shell command.  launches a 
     * process, and waits for it to complete.   Launches a watchdog timer which
     * will cause the process to stop waiting after a defined period of time. 
     * Monitors for keywords which trigger the timer to be reset.  This allows
     * running of commands which have a high probability of timing out, or may
     * take a while. 
     * @param cmd array of commands. eg. "new string[]{command, param, param}"
     * @param timeout process timeout in ms. The process will be abandoned after this time. 
     * @param restartTimerKeywords keywords which reset the timer.
     * @param logLevel2 Set to true if user viewable logging is preferable.
     * @return Text received from command. 
     */
    public String timeoutShellCommandWithWatchdog(final String[] cmd, final String[] restartTimerKeywords, final int timeout,final boolean logLevel2) {
        StringBuilder sb = new StringBuilder();
        try {
            ProcessBuilder p = new ProcessBuilder(cmd);
            p=p.redirectErrorStream(true);
            final Process process = p.start();
            
            

            /*
             TimeoutLogger is a place to hold a common object for use through
             the various threads. It is used for logging of data, locking the
             main thread on the processRunning object, timeout status and
             monitoring of if the thread is alive or not. 
             */
            class TimeoutLogger {
                boolean realtime;
                private final StringBuilder log = new StringBuilder();
                AtomicBoolean timedOut = new AtomicBoolean(false);
                AtomicBoolean isRunning = new AtomicBoolean(true);
                final Object isRunningLock=new Object();
                AtomicBoolean isLogging = new AtomicBoolean(true);
                final Object isLoggingLock=new Object();
                final Process processRunning;
                final Timer watchDogTimer = new Timer(timeout, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        
                        Log.level4Debug("Watchdog Triggered!  Command timed out.");
                        timedOut.set(true);
                        synchronized (processRunning) {
                            processRunning.notifyAll();
                        }
                    }
                });
                TimeoutLogger(boolean realtime, Process p){
                    this.realtime=realtime;
                    this.processRunning=p;
                }
                synchronized void log(char c){                
                    log.append(c);
                    if (realtime){
                        Log.progress(Character.toString(c));
                        String logstring=log.toString();
                        for (String check:restartTimerKeywords){
                            
                            if (logstring.endsWith(check) && isRunning.get()){
                                Log.level4Debug("Timer Reset on keyword "+check);
                                watchDogTimer.restart();
                            }
                        }
                    }
                }

                synchronized String get() {
                    return log.toString();
                }
                
            }
            
            final TimeoutLogger tl = new TimeoutLogger(logLevel2,process);

            /*if the watchDogtimer elapses, the timedOut boolean is set true
             and the processRunning object is notified to release main process
             wait.
             */

            /*notify the processRunning object when the thread is complete to
             release the lock. 
             */
            Thread processMonitor = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tl.processRunning.waitFor();
                        tl.watchDogTimer.stop();
                        tl.isRunning.set(false);
                        Log.level4Debug("Process Monitor done.");
                        synchronized (tl.processRunning) {
                            tl.processRunning.notifyAll();
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });
            processMonitor.setName("Monitoring Process Exit Status from " + cmd[0]);

            /*
            reads the output and restarts the timer if required.
            */
            Thread reader = new Thread(new Runnable() {
                @Override
                public void run() {
                   BufferedInputStream STDOUT = new BufferedInputStream(tl.processRunning.getInputStream());
                   Log.level4Debug("Instantiating reader process");
                   try {
                       while(tl.isRunning.get()&& !tl.timedOut.get()){
                               if(STDOUT.available()>0){
                                   char read=(char)STDOUT.read();
                                   tl.log(read);
                               }
                       }
                       
                       tl.watchDogTimer.stop();
                       Thread.sleep(100);
                       while(STDOUT.available()>0){
                           char read=(char)STDOUT.read();
                           tl.log(read);
                       }

                       tl.isLogging.set(false);

                       synchronized (tl.processRunning){
                           tl.processRunning.notifyAll();
                       }
                   } catch (IOException ex) {
                       Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                   } catch (InterruptedException ex) {
                        Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            reader.setName("Reading and monitoring output from " + cmd[0]);

            //start the monitoring objects
            reader.start();
            tl.watchDogTimer.start();
            processMonitor.start();
            synchronized (tl.processRunning){
                tl.processRunning.wait();
            }
            if (tl.isLogging.get()){
                synchronized(tl.processRunning){
                    tl.processRunning.wait();
                }
            }

            String retvalue = tl.get();
            //kill the process
            if (tl.timedOut.get()) {
                retvalue = "Timeout!!! " + retvalue;
                process.destroy();
            }
            return retvalue;
        } catch (IOException ex) {
            Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
