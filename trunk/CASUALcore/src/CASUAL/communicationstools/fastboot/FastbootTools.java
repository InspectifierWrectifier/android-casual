/*FastbootTools is a set of tools for use with fastboot
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
package CASUAL.communicationstools.fastboot;

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.ResourceDeployer;
import CASUAL.Shell;
import CASUAL.ShellTools;
import CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol;
import CASUAL.misc.StringOperations;
import java.io.File;
import java.util.ArrayList;

/**
 * Provides tools required to use Fastboot.
 * @author Adam Outler adamoutler@gmail.com
 */
public class FastbootTools extends AbstractDeviceCommunicationsProtocol {

    //last known deployed location for fastboot
    private static String binaryLocation = ""; //deployed fastboot

    //binary locations for Fastboot in resources
    private static final String fastbootLinux32 = "/CASUAL/communicationstools/fastboot/resources/fastboot-linux32";
    private static final String fastbootMac = "/CASUAL/communicationstools/fastboot/resources/fastboot-mac";
    private static final String fastbootWindows = "/CASUAL/communicationstools/fastboot/resources/fastboot.exe";
    private static final String fastbootLinuxARMv6 = "/CASUAL/communicationstools/fastboot/resources/fastboot-linuxARMv6";
    private static final String fastbootLinux64 = "/CASUAL/communicationstools/fastboot/resources/fastboot-linux64";

    /**
     * gets the resource for Fastboot
     *
     * @return path to resource
     */
    private static String getFastbootLinuxResource() {
        String arch = OSTools.checkLinuxArch();

        if (arch.equals("x86_64")) {
            Log.level3Verbose("found x86-64 bit arch");
            return fastbootLinux64;
        }
        if (arch.equals("ARMv6")) {
            Log.level3Verbose("found ARMv6 arch");
            return fastbootLinuxARMv6;
        }
        Log.level3Verbose("found x86-32 bit arch");
        return fastbootLinux32;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public synchronized String getBinaryLocation() {
        
        File loc = new File(binaryLocation);
        if (!loc.isFile() || !loc.exists()) {
            return deployBinary(CASUALMain.getSession().getTempFolder());
        } else {
            return binaryLocation;
        }

    }

    /**
     * executes fastboot
     *
     * @param line params for fastboot
     * @return value from fastboot command
     */
    public String doFastbootShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList<String>();
        ShellCommand.add(binaryLocation);
        ShellCommand.addAll(new ShellTools().parseCommandLine(line));
        String StringCommand[] = StringOperations.convertArrayListToStringArray(ShellCommand);
        Log.level3Verbose("Performing standard fastboot command" + line);
        return Shell.liveShellCommand(StringCommand, true);
    }

    /**
     * performs elevated fastboot command
     *
     * @param line params for fastboot
     * @return value from fastboot command
     */
    public String doElevatedFastbootShellCommand(String line) {
        line = StringOperations.removeLeadingSpaces(line);
        Shell Shell = new Shell();
        ArrayList<String> ShellCommand = new ArrayList<String>();
        ShellCommand.add(binaryLocation);
        ShellCommand.addAll(new ShellTools().parseCommandLine(line));
        String StringCommand[] = StringOperations.convertArrayListToStringArray(ShellCommand);
        Log.level3Verbose("Performing elevated Fastboot command" + line);
        String returnval = Shell.elevateSimpleCommandWithMessage(StringCommand, "CASUAL uses root to work around fastboot permissions.  Hit cancel if you have setup your UDEV rules.");
        return returnval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        //nothing required for Fastboot. fastboot has no daemon. 
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int numberOfDevicesConnected() {
        String[] devices = run(new String[]{"devices"}, 4000, true).trim().split("\n");
        int devcount = 0;
        for (String device : devices) { //enumerate devices
            if (device.trim().endsWith("fastboot")) { //validate devices
                devcount++; //increment counter
            }
        }
        return devcount; //return results

    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkErrorMessage(String[] commandRun, String returnValue) {
        //TODO Fastboot error message checking.  Error messages should be checked, actions taken and drivers installed if needed. 
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean installDriver() {
        //TODO Fastboot Drivers.  This should be called if error messages dictate need for driver installation. 
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String deployBinary(String tempFolder) {

        if (binaryLocation.isEmpty()) {
            String fastbootResource;
            binaryLocation = CASUALMain.getSession().getTempFolder() + "fastboot";
            if (OSTools.isWindows()) {
                binaryLocation += ".exe";
                new CASUALMessageObject("@interactionInstallFastbootDrivers").showInformationMessage();
                fastbootResource = fastbootWindows;
            } else if (OSTools.isMac()) {
                fastbootResource = fastbootMac;
            } else {
                fastbootResource = getFastbootLinuxResource();
            }
            Log.level2Information("@deployingFastboot");
            Log.level3Verbose("Deploying Fastboot from " + fastbootResource + " to " + binaryLocation);
            binaryLocation=new ResourceDeployer().deployResourceTo(fastbootResource, tempFolder);
            if (OSTools.isLinux() || OSTools.isMac()) {
                
                new File(binaryLocation).setExecutable(true);

            }
            Log.level2Information("@fastbootDeployed");
        }
        File loc = new File(binaryLocation);
        if (loc.exists()) {  //if the file exists after deployment procedure
            return binaryLocation;
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restartConnection() {
        this.reset();
        this.getBinaryLocation();
    }

    @Override
    public void reset() {
        if (!binaryLocation.isEmpty()) {
            this.shutdown();
        }
        binaryLocation = "";
    }

    @Override
    public String getConnectionMethodName() {
        return "fastboot";
    }
}
