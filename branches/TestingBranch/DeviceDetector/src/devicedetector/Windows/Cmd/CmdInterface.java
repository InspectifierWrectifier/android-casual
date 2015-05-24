/*
 * Copyright (C) 2013 Logan Ludington loglud@casual-dev.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package devicedetector.Windows.Cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Logan Ludington
 */
public class CmdInterface {
    private InputStreamReader isr = null;
    private InputStream is = null;
    private BufferedReader br = null;
    private String command;
    private Process p = null;
    private boolean admin = false;
    private File outLog = null;
    
    final private String elevationBatchFile = "Set objShell = CreateObject(\"Shell.Application\")\n"
                        + "args = \"%s\" \n"
                        + "objShell.ShellExecute \"%s\", args, \"\", \"runas\"";
    
    
    /**
     *
     * @param commandAndArgs A full string of the executable and the Args
     * <p>
     * Constructor that takes in the full command line.
     */
    public CmdInterface(String commandAndArgs) {
        this.command = commandAndArgs;
    }
    
    /**
     *
     * @param exe The executable to be run.
     * @param args List of arguments as a array of strings
     * <p>
     * Constructor that takes in the exe and the args and then constructs the 
     * full command line.
     */
    public CmdInterface(String exe, String[] args) {
        this.command = exe;
        for (String s : args) {
            this.command = this.command + " " + s;
        } 
    }
    
    /**
     *
     * @param commandAndArgs A full string of the executable and the Args
     * @param logfile The log file where the output of the command will be 
     * written too. Will overwrite any existing file.
     * <p>
     * Constructor that takes in the full command line, along with a log.
     */
    public CmdInterface(String commandAndArgs, String logfile) {
        this.command = commandAndArgs;
        this.outLog = new File(logfile);
    }
    
    /**
     *
     * @param exe The executable to be run.
     * @param args List of arguments as a array of strings 
     * @param logfile The log file where the output of the command will be 
     * written too. Will overwrite any existing file. 
     * <p>
     * Constructor that takes in the exe and the args and then constructs the 
     * full command line, and the log locations.
     */
    public CmdInterface(String exe, String[] args, String logfile) {
        this.command = exe;
        for (String s : args) {
            this.command = this.command + " " + s;
        }
        this.outLog = new File(logfile);
    }
    
    /**
     * Will run the command as a standard user, or the user where the 
     * JVM is being run.
     */
    public void run() {
        if (command == null) {
            System.out.println("CmdInterface: A command is required. \n"
                    + "\tPlease create a new cmdInterface and run.\n");
            return;
        }
        if (!admin && (outLog != null)) {
            command = command + " > " + outLog.toString() + " 2>&1";
        }
        try { 
            p = Runtime.getRuntime().exec(command);
            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(CmdInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(CmdInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Will run the command as an admin user, or the user where the 
     * JVM is being run.
     */
    public void runAsAdmin() { 
        File outFile = null;
        admin = true;
        try {
            if (outLog == null) {
                outLog = File.createTempFile("elevate", ".log");
                outLog.deleteOnExit();
            }
            outFile = File.createTempFile("sudo", ".vbs");
            outFile.deleteOnExit();
            FileWriter fw = new FileWriter(outFile);
            fw.write(String.format(elevationBatchFile, "/C " + command + " > " + outLog.toString() + " 2>&1", "cmd.exe" ));
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(CmdInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (command == null) {
            System.out.println("CmdInterface: A command is required. \n"
                    + "\tPlease create a new cmdInterface and run.\n");
            return;
        }
        if (outFile == null) {
            System.out.println("CmdInterface: Elevation Script could not be written \n"
                    + "\tPlease create a new cmdInterface and run.\n");
            return;
        }
        command = "cscript "+ outFile.toString();
        run();

        
    }

    /**
     *
     * @return Input stream reader for the output from the command that was run.
     * <p>
     * <b>Note:</b> This only works on standard users.
     */
    public InputStreamReader getInputStreamReader() {
        if (is == null)
            getInputStream();
        if (is == null) {
            return null;
        }
        isr = new InputStreamReader(is);
        return isr;
    }

    /**
     *
     * @return Input stream for the output from the command that was run.
     * <p>
     * <b>Note:</b> This only works on standard users.
     */
    public InputStream getInputStream() {
        if ( p==null) {
            System.out.println("CmdInterface: Process has not been run. \n"
                    + "\tPlease use the run() method before this one.\n");
            return null;
        }
        is = p.getInputStream();
        return is;
    }

    /**
     *
     * @return Buffered reader for the output from the command that was run.
     * <p>
     * <b>Note:</b> This only works on standard users.
     */
    public BufferedReader getBufferedReader() {
        if (isr==null)
            getInputStreamReader();
        if (isr == null) {
            return null;
        }
        if (admin) {
            
        }
        br = new BufferedReader(isr);
        return br;
    }
    
    /**
     *Prints the output of the command to the console.
     * @see System.out.println
     */
    public void printOuput() {
        if(admin) {
             try {
                BufferedReader br = new BufferedReader( new FileReader(outLog));
                String line;
                while((line = br.readLine())!=null) {
                    System.out.println(line);
                }
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CmdInterface.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CmdInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
           if (br == null)
                getBufferedReader();
            if (br == null) {
                return;
            }
            String line;
            try {
                while (( line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(CmdInterface.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
    }
    
    /**
     *
     * @return Output string of all lines of the output thrown together.
     * <p>
     * 
     */
    public String getOuputString() {
        String outputString = "";
        if (br == null)
            getBufferedReader();
        if (br == null) {
            return null;
        }
        String line;
        try {
            while (( line = br.readLine()) != null) {
                outputString = outputString + line + "\n";
            }
             br.close();
        } catch (IOException ex) {
            Logger.getLogger(CmdInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputString;
    }
    
}
