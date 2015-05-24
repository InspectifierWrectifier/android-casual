/*HeimdallTools provides tools for use with Heimdall
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
package CASUAL.communicationstools.heimdall;

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.Shell;
import CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol;
import CASUAL.communicationstools.heimdall.drivers.DriverInstall;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides tools to work with Heimdall in CASUAL.
 *
 * @author Adam Outler adamoutler@gmail.com
 * @author Jeremy Loper jrloper@gmail.com
 */
public class HeimdallTools extends AbstractDeviceCommunicationsProtocol {

    //locations to resources within CASUAL. 
    static final String[] windowsLocation = new String[]{"/CASUAL/communicationstools/heimdall/resources/heimdall.exe", "/CASUAL/communicationstools/heimdall/resources/libusb-1.0.dll", "/CASUAL/communicationstools/heimdall/resources/msvcr110.dll", "/CASUAL/communicationstools/heimdall/resources/msvcp110.dll"};
    static final String[] macLocation = new String[]{"/CASUAL/communicationstools/heimdall/resources/heimdall-mac.dmg"};
    static final String[] linux32Location = new String[]{"/CASUAL/communicationstools/heimdall/resources/heimdall_i386.deb"};
    static final String[] linux64Location = new String[]{"/CASUAL/communicationstools/heimdall/resources/heimdall_amd64.deb"};
    static final String[] linuxArmv6Location = new String[]{"/CASUAL/communicationstools/heimdall/resources/heimdall_armv6.deb"};
    private static String binaryLocation = "";

    Shell shell = new Shell();
    int heimdallRetries = 0;


    int errorCycles = 0;

    /**
     * {@inheritDoc} Heimdall will only return one device or 0.
     *
     * @return {@inheritDoc}
     */
    @Override
    public int numberOfDevicesConnected() {
        String detectCommand[] = new String[]{"detect"};
        String connectedString = "Device detected";
        String shellReturn = run(detectCommand, 4000, true);
        if (shellReturn.contains(connectedString)) {
            Log.level3Verbose("Heimdall Device detected!");
            errorCycles = 100; //this removes the potential annoying error;
            return 1;
        }
        errorCycles++;
        if (errorCycles == 60) {
            new CASUALMessageObject("@interactionUnableToDetectDownloadMode").showInformationMessage();
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * obtains a CommandDisposition by analyzing the return value. Takes action
     * based on disposition.
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkErrorMessage(String[] commandRun, String returnValue) {
        //exit if this is a detection call
        if (commandRun[1].equals("detect")) {
            return true;
        }
        CommandDisposition retval = new HeimdallErrorHandler().doErrorCheck(commandRun, returnValue);
        errorCycles++;
        boolean errored;
        switch (retval) {
            case NOACTIONREQUIRED:
                errorCycles = 0;
                errored = false;
                break;
            case RUNAGAIN:
                errored = checkErrorMessage(commandRun, this.doElevatedHeimdallShellCommand(commandRun));
                if (errorCycles > 4) {
                    errored = true;
                }
                break;
            case ELEVATIONREQUIRED:
                errored = checkErrorMessage(commandRun, this.doElevatedHeimdallShellCommand(getBinaryCommandInArray(commandRun)));
                if (errorCycles > 4) {
                    errored = true;
                }
                break;
            case INSTALLDRIVERS:
                errorCycles = 0;
                this.installDriver();
                errored = checkErrorMessage(commandRun, this.doHeimdallShellCommand(commandRun));
                break;
            case HALTSCRIPT:
                errored = true;
                break;
            case MAXIMUMRETRIES:
                Log.level0Error("Heimdall has encountered an error we did not forsee");
                errored = true;
                break;
            default:
                errored = true;
        }
        return !errored;

    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean installDriver() {
        if (OSTools.isWindows()) {
            return new DriverInstall(0).installKnownDrivers();
        }
        if (OSTools.isMac() || OSTools.isLinux()) {
            return !deployBinary(CASUALMain.getSession().getTempFolder()).isEmpty();
        }

        return false;
    }

    /**
     * {@inheritDoc} Heimdall will only return one device or 0.
     *
     * @return {@inheritDoc}
     */
    @Override
    public String deployBinary(String tempFolder) {
        HeimdallInstall hinstall = new HeimdallInstall();
        try {
            if (OSTools.isLinux()) {
                binaryLocation = hinstall.installLinux(tempFolder);
            } else if (OSTools.isMac()) {

                hinstall.installMac(macLocation, tempFolder);

            } else if (OSTools.isWindows()) {
                binaryLocation = hinstall.installWindows(windowsLocation, tempFolder);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(HeimdallTools.class.getName()).log(Level.SEVERE, "Error while trying to install heimdall", ex);
        } catch (IOException ex) {
            Logger.getLogger(HeimdallTools.class.getName()).log(Level.SEVERE, "Error while trying to install heimdall", ex);
        }
        return binaryLocation;

    }

    @Override
    public void restartConnection() {
        //TODO: jrloper examine possibility of resetting USB subsystem 
        reset();
    }

    @Override
    public void reset() {
        heimdallRetries = 0;
        binaryLocation = "";
    }

    @Override
    public void shutdown() {
        reset();
    }

    @Override
    public String getConnectionMethodName() {
        return "Heimdall";
    }

    @Override
    public synchronized String getBinaryLocation() {
        //return located heimdall if available
        //here we make new File twice, but this is because binaryLocation may be null.  It's easier to read this way 
        if (binaryLocation != null && !binaryLocation.isEmpty() && new File(binaryLocation).isFile() && new File(binaryLocation).exists()) {
            return binaryLocation;
        }

        //locate heimdall in path or filesystem. Will be blank if not found.
        binaryLocation = locateNativeHeimdall();
        if (!binaryLocation.isEmpty()) {
            return binaryLocation;
        }

        //install heimdall
        binaryLocation = deployBinary(CASUALMain.getSession().getTempFolder());
        return binaryLocation;
    }

    private String locateNativeHeimdall() {
        String notFound = "CritERROR!!!";

        //for windows we try running "heimdall".
        //note this wont work unless user sets path variable. 
        if (OSTools.isWindows()) {
            String heimdall = "heimdall";
            String[] cmd = new String[]{heimdall};
            String retval = shell.silentShellCommand(cmd);
            if (retval.contains(notFound) || retval.isEmpty()) {
                return "";
            } else {
                return heimdall;
            }
        }
        
        //for all unix/linux systems we try common paths for heimdall. 
        String cmd = "/usr/local/bin/heimdall";
        String check = shell.silentShellCommand(new String[]{cmd});
        //we got the file
        Log.level4Debug("native search /usr/local/bin/heimdall:" + (check.contains("CritERROR!!!") ? "false" : "true"));
        if (check.equals(notFound)) {
            cmd = "/usr/bin/heimdall";
            check = shell.silentShellCommand(new String[]{cmd});
            Log.level4Debug("native search /usr/bin/heimdall:" + (check.contains("CritERROR!!!") ? "false" : "true"));
            //try different things
            if (check.equals(notFound)) {
                cmd = "/bin/heimdall";
                check = shell.silentShellCommand(new String[]{cmd});
                Log.level4Debug("native search /bin/heimdall:" + (check.contains("CritERROR!!!") ? "false" : "true"));
                if (check.equals(notFound)) {
                    cmd = "heimdall";
                    check = shell.silentShellCommand(new String[]{cmd});
                    Log.level4Debug("native search heimdall:" + (check.contains("CritERROR!!!") ? "false" : "true"));
                    if (check.equals(notFound)) {
                        cmd = "";
                    }
                }
            }
        }
        return cmd;
    }

    /**
     * performs an elevated heimdall command
     *
     * @param command heimdall command to be executed
     * @return result from heimdall
     */
    private String doElevatedHeimdallShellCommand(String[] command) {

        Log.level4Debug("Executing ELEVATED Heimdall Command:  " + new HeimdallErrorHandler().displayArray(command));
        String returnval = shell.elevateSimpleCommandWithMessage(command, "CASUAL uses root to work around Heimdall permissions.  Hit cancel if you know root is not required to access your device.");
        return returnval;
    }

    private String[] getBinaryCommandInArray(String[] command) {
        if (command[0].equals(getBinaryLocation())) {
            return command;
        }

        String[] cmd = new String[command.length + 1];
        cmd[0] = getBinaryLocation();
        System.arraycopy(command, 0, cmd, 1, command.length);
        return cmd;
    }

    /**
     * performs a heimdall command
     *
     * @param command value from heimdall command
     * @return return from shell command
     */
    public String doHeimdallShellCommand(String[] command) {
        HeimdallErrorHandler heh = new HeimdallErrorHandler();
        Log.level4Debug("Executing " + heh.displayArray(command));
        command = getBinaryCommandInArray(command);
        int timesRun = 0;
        String shellRead; //value from command 
        String returnValue = ""; //concatinated shellRead values from repeats
        shellRead = shell.liveShellCommand(command, true);
        this.checkErrorMessage(command, shellRead);
        returnValue += shellRead;
        timesRun++;
        return returnValue;

    }

    /**
     * returns the Instance of Linux's ADB binary
     *
     * @return gets the proper name of the ADB binary as a resource.
     */
    private String[] getLinuxADBResource() {
        String arch = OSTools.checkLinuxArch();
        if (arch.equals("x86_64")) {
            return linux64Location;
        }
        if (arch.equals("ARMv6")) {
            return linuxArmv6Location;
        }
        return linux32Location;
    }

    /**
     * Status for decision making based on return value from heimdall.
     *
     * SUCCESS: Heimdall executed sucessfully
     *
     * HALTED: Heimdall encountered a failure which cannot be recovered.
     *
     * CONTINUE: Heimdall did not execute sucessfully, but we can try again.
     *
     * MAXIMUMRETRIESEXCEEDED: We've tried to continue four times now.
     *
     */
    public enum CommandDisposition {
        
        /**
         * Result was good.
         */
        NOACTIONREQUIRED, /**
         * Result requires retry.
         */
        RUNAGAIN, /**
         * Result indicates that permission problems were encountered.
         */
        ELEVATIONREQUIRED, /**
         * Result indicates drivers are required.
         */
        INSTALLDRIVERS, /**
         * Result indicates that an unrecoverable error was encountered.
         */
        HALTSCRIPT, /**
         * This command has been run too many times and a favorable result is
         * not likely.
         */
        MAXIMUMRETRIES
    }

}
