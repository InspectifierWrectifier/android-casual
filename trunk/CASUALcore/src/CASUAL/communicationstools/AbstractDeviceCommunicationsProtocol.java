/*interface DeviceConnection provides a unified manner of accessing tools which access devices.
 * Copyright (C) 2013 adamoutler
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
 * along with this program.  If not, see https://www.gnu.org/licenses/ .
 */
package CASUAL.communicationstools;

import CASUAL.Log;
import CASUAL.Shell;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract DeviceCommunicationsProtocol provides a set of methods which allow
 * universal access to various device firmware loading binaries. This class was
 * written with ADB, Fastboot, and Heimdall in mind but is broad enough to
 * encompass many others as well.
 *
 * @author adamoutler
 */
public abstract class AbstractDeviceCommunicationsProtocol {

    /**
     * Last known location of the binary should be overridden and implemented as
     * private static. Refers to the location of the binary to be operated by
     * this class eg.. /tmp/Adam2123/adb.exe or C:\Users\Adam\local...
     *
     */
    String binaryLocation = "";

    /**
     * Windows binary location should be overridden and implemented as private
     * static final.
     */
    String[] windowsLocation = new String[]{"The Windows platform is not yet supported. Please override this with an appropriate binary."};
    /**
     * Linux32 binary location should be overridden and implemented as private
     * static final.
     */
    String[] linux32Location = new String[]{"The Linux32 platform is not yet supported. Please override this with an appropriate binary."};
    /**
     * Linux64 binary location should be overridden and implemented as private
     * static final.
     */
    String[] linux64Location = new String[]{"The Linux64 platform is not yet supported. Please override this with an appropriate binary."};
    /**
     * LinuxArmv6 binary location should be overridden and implemented as
     * private static final.
     */
    String[] linuxArmv6Location = new String[]{"The Linux ARMv6 platform is not yet supported. Please override this with an appropriate binary."};
    /**
     * Mac binary location should be overridden and implemented as private
     * static final.
     */
    String[] macLocation = new String[]{"The Mac platform is not yet supported. Please override this with an appropriate binary."};

    /**
     * returns and integer representing the number of devices connected. This
     * may or may not be possible depending upon the tool used. This method will
     * use the getBinaryLocation method.
     *
     * @return results from the wait command.
     */
    abstract public int numberOfDevicesConnected();

    /**
     * Examines the return values of other commands and determines if action
     * should be taken, then takes it. This method should be called frequently
     * so as to ensure that errors are caught and corrected quickly. The catches
     * may be as simple as notifying the operator to plug in the device or
     * automatically installing drivers should the return value detect that it
     * is required.
     *
     * @param commandRun Command which was run including parameters.
     * @param returnValue string to check for errors
     * @return true if no error
     */
    abstract public boolean checkErrorMessage(String[] commandRun, String returnValue);

    /**
     * This method is used by checkErrorMessage to install drivers when
     * required. There should never be a reason to call this independently.
     *
     * @return true if drivers were installed. false indicates a problem.
     */
    abstract public boolean installDriver();

    /**
     * Called by the getBinaryLocation method to deploy the binary used by the
     * application. This method is called when there is no known location for
     * the binary. It is in charge of determining which platform's binary to
     * deploy and deploying associated resources.
     *
     * @param tempFolder Location to deploy binary.
     * @return location to binary.
     */
    abstract public String deployBinary(String tempFolder);

    /**
     * Restarts the connection to the device. This may be a simple call or a
     * complex one. This call is intended to fix problems detected by
     * checkErrorMessage. Depending on the situation, it may be beneficial to
     * keep a counter and try various troubleshooting steps for various
     * operating systems here.
     */
    abstract public void restartConnection();

    /**
     * reset is used to clear the binary location from outside the package and
     * stop the service if required. This is useful for when the temp folder is
     * changed or when the system is shutting down. This should also trigger the
     * getBinaryLocation() to create a new binary upon the next call. This is a
     * method to destroy the private static location of the binary in memory.
     * This will reset the binaryLocation and call the shudown() method;
     */
    abstract public void reset();

    /**
     * Commands used to shutdown the application as a part of reset. This may be
     * called at any time so it should account for various operating conditions.
     */
    abstract public void shutdown();

    /**
     * Deploys the binary and returns its location. This method should check the
     * binaryLocation, and if the called location is null, it should deploy the
     * binary using the deployBinary(TempFolder) method. This is the primary
     * method used by this class.
     *
     * @return location to binary being called.
     */
    abstract public String getBinaryLocation();

    /**
     * returns true if 1 device is connected. Will return false if more than one
     * or less than one is connected. This method should use the
     * numberOfDevicesConnected() method to get the number of devices connected
     * and determine if it is a single device.
     *
     * @return true if connected.
     */
    public boolean isConnected() {
        return numberOfDevicesConnected() == 1;
    }

    /**
     * Waits for isConnected() to return true. If the device is not connected,
     * this method will continue blocking. The purpose of this method is to halt
     * progress until a device is connected and usable. waitForDevice() may use
     * any tools it can to determine the ready status of the device.
     *
     */
    public void waitForDevice() {
        Log.level4Debug("Waiting for device");
        while (!isConnected()) {
            sleep200ms();
        }
        Log.level4Debug("Device Connected!!!");
    }

    /**
     * sleeps for 200 ms and then returns
     */
    private void sleep200ms() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(AbstractDeviceCommunicationsProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * provides a safe method to run the binary with parameters. this method
     * should execute a Shell.timeOutShell command and allow for a method of
     * calling the binary with a timeout so as to never allow the method to
     * hang.
     *
     * @param parameters parameters used to operate the binary. Eg.. adb DETECT,
     * or heimdall FLASH. The binary is to be specified in the run() and only
     * the parameters are supplied.
     * @param timeout time in ms before timeout will occur and the command will
     * return;
     * @param silent true will cause this method to keep information out of the
     * logs so as not to clutter with every-second-pings of a device or the
     * such.
     * @return value from command. if result begins with "Timeout!!! " the
     * command has exceeded the set timeout value.
     */
    public String run(String[] parameters, int timeout, boolean silent) {
        Shell shell = new Shell();
        //expand array by one
        String[] runcmd = new String[parameters.length + 1];
        runcmd[0] = getBinaryLocation(); //insert binary as [0] 
        String runstring = runcmd[0];
        for (int i = 1; i < runcmd.length; i++) {
            runcmd[i] = parameters[i - 1]; //insert the rest of the parameters
            runstring = runstring + " " + runcmd[i];
        }

        if (silent) {
            String retval = shell.silentTimeoutShellCommand(runcmd, timeout);
            this.checkErrorMessage(runcmd, retval);
            return retval;
        } else {
            Log.level4Debug("Run " + this.getConnectionMethodName() + " from DeviceCommunicationProtocol:" + runstring);
            Log.insertChars(",");
            return shell.liveShellCommand(runcmd, true);
        }
    }

    /**
     * Verfies file was deployed. 
     * @param binaryLocation location to file
     * @return true if file is present. 
     */
    public boolean fileIsDeployedProperly(String binaryLocation) {
        File f = new File(binaryLocation);
        return (binaryLocation != null
                && !binaryLocation.isEmpty()
                && f.exists()
                && f.isFile()
                && f.canExecute());

    }

    /**
     * returns a mode of operation. This allows the toString method to display
     * more information about the current operation. eg. ADB, Heimdall,
     * Fastboot.
     *
     * @return Name of connection method, eg. ADB, Heimdall or Fastboot
     */
    abstract public String getConnectionMethodName();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String n = "\n";
        sb.append("mode:").append(this.getConnectionMethodName()).append(n);
        sb.append("Connected:").append(this.isConnected()).append(n);
        return sb.toString();
    }
}
