/*ADBInstall deploys ADB for CASUAL 
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
package CASUAL.communicationstools.adb;

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.CASUALSessionData;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.ResourceDeployer;
import CASUAL.Shell;
import CASUAL.misc.DiffTextFiles;
import java.awt.HeadlessException;
import java.io.File;

/**
 * Provides a set of tools for using ADB in CASUAL
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class ADBTools extends CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol {

    /**
     * path to ADB after deployment.
     */
    private static String binaryLocation = ""; //location of ADB after deployment
    // The following variables represent locations of ADB files
    private static final String[] linux64Location = new String[]{"/CASUAL/communicationstools/adb/resources/adb-linux64"};
    private static final String[] linux32Location = new String[]{"/CASUAL/communicationstools/adb/resources/adb-linux32"};
    private static final String[] windowsLocation = new String[]{"/CASUAL/communicationstools/adb/resources/adb.exe", "/CASUAL/communicationstools/adb/resources/AdbWinApi.dll", "/CASUAL/communicationstools/adb/resources/AdbWinUsbApi.dll"};
    private static final String[] macLocation = new String[]{"/CASUAL/communicationstools/adb/resources/adb-mac"};
    private static final String[] linuxArmv6Location = new String[]{"/CASUAL/communicationstools/adb/resources/adb-linuxARMv6"};
    private static final String adbIniResource = "/CASUAL/communicationstools/adb/resources/adb_usb.ini";

    /**
     * ADBTools default constructor.
     */
    public ADBTools() {
        
    }

    private String getAdbIniLocation() {
        return System.getProperty("user.home") + CASUALSessionData.slash + ".android" + CASUALSessionData.slash + "adb_usb.ini";
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
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int numberOfDevicesConnected() {
        String[] devices = getIndividualDevices();
        int connected = 0;
        for (String device : devices) {
            if (device.trim().endsWith("device") || device.trim().endsWith("recovery")) {
                connected++;
            }
        }
        return connected;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void restartConnection() {
        Log.level3Verbose("Restarting ADB slowly for compatibility");
        Shell shell = new Shell();
        shell.timeoutShellCommand(getKillServerCmd(), 1000);
        String retval = shell.timeoutShellCommand(getDevicesCmd(), 6000);
        new ADBTools().checkErrorMessage(getDevicesCmd(), retval);
    }

    /**
     * {@inheritDoc}
     *
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkErrorMessage(String[] commandRun, String returnValue) throws HeadlessException {

        /**
         * This error was received on Linux when permissions elevation was
         * required. daemon not running. starting it now on port 5037 * cannot
         * bind 'local:5037' ADB server didn't ACK failed to start daemon *
         * error: cannot connect to daemon
         */
        if (OSTools.isLinux() && returnValue.contains("ERROR-3")) { //Don't know how to handle this yet
            Shell shell = new Shell();
            Log.level0Error("@permissionsElevationRequired");
            shell.silentShellCommand(getKillServerCmd());
            shell.elevateSimpleCommandWithMessage(getDevicesCmd(), "Device permissions problem detected");
            return false;
        }

        if (returnValue.contains("ELFCLASS64") && returnValue.contains("wrong ELF")) {
            new CASUALMessageObject("@interactionELFCLASS64Error").showInformationMessage();
            return false;
        }

        if (returnValue.contains("List of devices attached ")) {
            if (returnValue.contains("unauthorized") || returnValue.contains("Please check the confirmation dialog on your device.")) {
                new CASUALMessageObject("@interactionPairingRequired").showActionRequiredDialog();
                return false;
            }

            if (returnValue.contains("offline")) {
                String[] ok = {"All set and done!"};
                new CASUALMessageObject("@interactionOfflineNotification").showTimeoutDialog(120, null, javax.swing.JOptionPane.OK_OPTION, 2, ok, 0);
                Log.level0Error("@disconnectAndReconnect");
                return false;
            }
            if (returnValue.contains("????????????") || returnValue.contains("**************") || returnValue.contains("error: cannot connect to daemon")) {
                Log.level0Error("@unrecognizedDeviceDetected");
                Log.level4Debug("Restarting ADB slowly");
                restartConnection();
                returnValue = new Shell().silentShellCommand(getDevicesCmd()).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
                if (!OSTools.isWindows() && returnValue.contains("????????????") || returnValue.contains("**************") || returnValue.contains("error: cannot connect to daemon")) {
                    String[] ok = {"ok"};
                    new CASUALMessageObject("@interactionInsufficientPermissionsWorkaround").showTimeoutDialog(60, null, javax.swing.JOptionPane.OK_OPTION, 2, ok, 0);
                    shutdown();
                    elevateADBserver();
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return numberOfDevicesConnected() == 1;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void reset() {
        if (!binaryLocation.isEmpty()) {
            this.shutdown();
        }
        binaryLocation = "";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean installDriver() {
        //TODO install drivers for ADB
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public synchronized String deployBinary(String tempFolder) {
        Log.level4Debug("Deploying ADB");

        String tempBinaryLocation = tempFolder + "adb";
        String[] resourceLocation;
        if (OSTools.isLinux()) {
            Log.level4Debug("Found Linux Computer for ADB deployment");
            resourceLocation = this.getLinuxADBResource();
        } else if (OSTools.isMac()) {
            Log.level4Debug("Found Mac Computer for ADB deployment");
            resourceLocation = macLocation;
        } else if (OSTools.isWindows()) {
            Log.level4Debug("Found Windows Computer for ADB deployment");
            resourceLocation = windowsLocation;
        } else {
            new CASUALMessageObject("@interactionsystemNotNativelySupported").showInformationMessage();
            resourceLocation = new String[]{};
        }

        ResourceDeployer rd = new ResourceDeployer();
        File defaultLocation = new File(getDefaultBinaryName(tempFolder));
        for (String res : resourceLocation) {
            String deployedName = rd.deployResourceTo(res, tempFolder);
            if (deployedName.contains("adb-") || deployedName.endsWith("adb.exe")) {

                new File(deployedName).renameTo(defaultLocation);
                defaultLocation.setExecutable(true);
                tempBinaryLocation = defaultLocation.getAbsolutePath();
            }
        }

        updateADBini();
        String[] devicesCommand = new String[]{tempBinaryLocation, "devices"};
        String[] sendcmd = devicesCommand;
        String deviceList = new Shell().silentTimeoutShellCommand(sendcmd, 5000);
        if (checkErrorMessage(devicesCommand, deviceList) || deviceList.contains("\toffline\n")) {
            binaryLocation = tempBinaryLocation;
        }
        return binaryLocation;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void shutdown() {
        super.run(new String[]{"kill-server"}, 4000, false);
    }

    private void updateADBini() {
        ResourceDeployer rd = new ResourceDeployer();
        String adbini = getAdbIniLocation();
        File adbIni = new File(adbini);
        if (!adbIni.isFile() && !adbIni.exists()) {
            rd.copyFromResourceToFile(adbIniResource, adbini);
        } else {
            DiffTextFiles DTF = new DiffTextFiles();
            DiffTextFiles.appendDiffToFile(adbini, DTF.diffResourceVersusFile(adbIniResource, adbini));
        }
    }

    /**
     * executes the getDevices command
     *
     * @return individual devices listed as strings
     */
    public String[] getIndividualDevices() {
        String devReturn = run(new String[]{"devices"}, 5000, true);
        checkErrorMessage(getDevicesCmd(), devReturn);
        if (devReturn.equals("List of devices attached \n\n")) {
            return new String[]{};
        } else {
            String[] retval;
            try {
                retval = devReturn.split("List of devices attached ")[1].trim().split("\n");
            } catch (ArrayIndexOutOfBoundsException ex) {
                retval = new String[]{};
            }
            return retval;
        }

    }

    private String getDefaultBinaryName(String tempFolder) {
        if (OSTools.isWindows()) {
            return tempFolder + "adb.exe";
        }
        return tempFolder + "adb";
    }

    /**
     * method to get the wait-for-device command for ADB use
     *
     * @return path_to_adb, wait-for-device
     */
    private String[] getWaitForDeviceCmd() {
        return new String[]{getBinaryLocation(), "wait-for-device"};
    }

    /**
     * method to get the devices command for ADB use
     *
     * @return path_to_adb, devices
     */
    private String[] getDevicesCmd() {
        return new String[]{getBinaryLocation(), "devices"};
    }

    /**
     * value to start the server
     *
     * @return value from adb
     */
    private String[] getStartServerCmd() {
        return new String[]{getBinaryLocation(), "start-server"};
    }

    /**
     * return the value to kill the ADB server
     *
     * @return value from ADB command
     */
    private String[] getKillServerCmd() {
        return new String[]{getBinaryLocation(), "kill-server"};
    }

    /**
     * executes the getDevices command
     *
     * @return value from adb getDevices
     */
    public String getDevices() {
        Shell shell = new Shell();
        String devReturn = shell.silentTimeoutShellCommand(getDevicesCmd(), 5000);
        //TODO implement error checking here and install drivers if needed EXPAND this!
        return devReturn;
    }

    /**
     * executes the start server command
     *
     * @return value from adb start server
     */
    public String startServer() {
        Shell shell = new Shell();
        String retval = shell.timeoutShellCommand(getStartServerCmd(), 5000);
        return retval;
    }

    /**
     * starts an elevated ADB server.
     */
    public void elevateADBserver() {
        Log.level3Verbose("Elevating ADB server!");
        Shell shell = new Shell();
        shell.silentShellCommand(getKillServerCmd());
        shell.elevateSimpleCommand(getDevicesCmd());
    }

    @Override
    public synchronized String getBinaryLocation() {
        if (binaryLocation.isEmpty() || !new File(binaryLocation).exists()) {
            deployBinary(CASUALMain.getSession().getTempFolder());
        }
        return binaryLocation;
    }

    @Override
    public String getConnectionMethodName() {
        return "ADB";
    }

}
