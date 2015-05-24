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
package devicedetector.Windows.Msiexec;

import devicedetector.Windows.Cmd.CmdInterface;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Useful for installing MSI files such as drivers quietly.
 * 
 * @author loganludington
 * 
 * 
 */
public class MsiexecInterface {

    
    //Operation varibles
    private final Msiexec msi;
    private String commandLine;
    private boolean run;
    
    /**
     *
     * @param msi An Msiexec object
     * <p>
     * Constructor used to create an interface between an MSI File and a cmd 
     * prompt.
     * <p>
     * 
     */
    public MsiexecInterface(Msiexec msi) {
        
        this.msi= msi;
        CreateCommandLine();
        CheckLogging();
        CheckQuiet();
    }

    private void CreateCommandLine() {
        try {
            if(!(new File(msi.msiFile)).exists())
                throw new RuntimeException();
        } catch (RuntimeException e) {
            System.err.print("MsiexecInterface: MSI file does not exist. \n");
            System.exit(1);
        }
        try {
            if(!(msi.msiFile.endsWith(".msi")))
                throw new RuntimeException();
        } catch (RuntimeException e) {
            System.err.print("MsiexecInterface: File does not seem to be a valid MSI file. \n");
            System.exit(1);
        }
        commandLine = "msiexec.exe";
        if (msi.operation == Msiexec.Operation.INSTALL)
            InstallCommandLine();
        else if (msi.operation == Msiexec.Operation.REPAIR)
            RepairCommandLine();
        else if (msi.operation == Msiexec.Operation.UNINSTALL)
            UninstallCommandLine();
    }

    private void InstallCommandLine() {
        commandLine = commandLine + " /i " + msi.msiFile;
    }

    private void RepairCommandLine() {
        commandLine = commandLine + " /f";
        RepairOptions();
        commandLine = commandLine + " " + msi.msiFile;
    }

    private void UninstallCommandLine() {
        commandLine = commandLine + " /x " + msi.msiFile;
    }

    private void CheckLogging() {
        if(!msi.loggingEnabled)
            return;
        try {
            if (!isFilenameValid(msi.logFile.toString()))
                throw new RuntimeException();
        } catch(RuntimeException e) {
            System.err.print("MsiexecInterface: Logfile not valid location. \n");
            System.exit(1);
        }
        if(msi.getLoggingOptions().isEmpty())
            msi.setLoggingOptions(Msiexec.LoggingOption.ALL);
        commandLine = commandLine+ " /l";
        for (Msiexec.LoggingOption lo : msi.getLoggingOptions()) {
            switch (lo) {
                case STATUS_MESSAGES: 
                    commandLine = commandLine + "i";
                    break;
                case NONFATAL_WARNINGS:
                    commandLine = commandLine + "w";
                    break;
                case ALL_ERROR_MESSAGES:
                    commandLine = commandLine + "e";
                    break;
                case STARTUP_OF_ACTIONS:
                    commandLine = commandLine + "a";
                    break;
                case ACTION_SPECIFIC_RECORDS:
                    commandLine = commandLine + "r";
                    break;
                case USER_REQUESTS:
                    commandLine = commandLine +  "u";
                    break;
                case INITIAL_USER_INTERFACE_PARAMS:
                    commandLine = commandLine + "c";
                    break;
                case OUT_OF_MEMORY:
                    commandLine = commandLine + "mo";
                    break;
                case TERMINAL_PROPS:
                    commandLine = commandLine + "p";
                    break;
                case VERBOSE:
                    commandLine = commandLine + "v";
                    break;
                case APPEND:
                    commandLine = commandLine + "+";
                    break;
                case FLUSH:
                    commandLine = commandLine + "!";
                    break;
                case ALL:
                    commandLine = commandLine + "*";
                    break;
            }
        }
        commandLine = commandLine + " " + msi.logFile.toString();
        
        
    }
    
    private static boolean isFilenameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void RepairOptions() {
        switch (msi.repairOptions) {
            case ONLY_IF_MISSING:
                commandLine = commandLine + "p";
                break;
            case OLDER_VERSION:
                commandLine = commandLine + "o";
                break;
            case OLDER_OR_EQUAL_VERSION:
                commandLine = commandLine + "e";
                break;
            case DIFFERENT_VERSION:
                commandLine = commandLine + "d";
                break;
            case CHECKSUM_MISMATCH:
                commandLine = commandLine + "c";
                break;
            case ALL_FILES:
                commandLine = commandLine + "a";
                break;
        }
    }

    private void CheckQuiet() {
        if (!msi.quite)
            return;
        commandLine = commandLine + " /quiet";
    }

    private void PrintCommand() {
        System.out.println(commandLine);
    }
    
    /**
     *Executes a msiexec at an elevated cmd level with all the proper flags
     */
    public void execute() {
        CmdInterface cmd = new CmdInterface(commandLine);
        cmd.runAsAdmin();
        run = true;
    }
    
    /**
     *
     * If a log file is created and then executed this function will write out
     * the file to screen.
     * 
     * @see System.out.println
     */
    public void PrintLog(){
        if (!run) {
            System.out.println("Error: Must execute MSI first.");
            return;
        }
        if (!msi.loggingEnabled) {
            System.out.println("Error: Must have logging enabled.");
            return;
        }
        long PrevSize = 0;
        while (msi.logFile.length() != PrevSize) {
            PrevSize = msi.logFile.length();
            System.out.println("File Still Open");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MsiexecInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(msi.logFile)));
            String line;
            while ((line = br.readLine())!=null)
                System.out.println(line);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MsiexecInterface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MsiexecInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
