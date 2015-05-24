/*DriverInstall.java
 * **************************************************************************
 *Copyright (C) 2013  Jeremy Loper
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
 ***************************************************************************/
package CASUAL.communicationstools.heimdall.drivers;

import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import java.util.ArrayList;

/**
 * ***************************************************************************
 * DriverInstall a.k.a. CADI(v2) or (CASUALS Automated Driver Installer) is a
 * CASUALcore dependant class which attempts to automate CASUAL process on
 * Windows (Vista - Win8) A generic driver is required for USB IO via CASUAL. This
 * driver must temporarily take the place of the default OEM driver of targeted
 * device (which must be currently connected). While many OEMs use WinUSB (or
 * compatible alternative) as a device interface, CASUAL is not able communicate
 * with the target because of proprietary (undocumented) driver service API.
 * However once the generic driver is installed CASUAL using reverse engineered
 * open-source tools such as Heimdall - http://goo.gl/bqeulW is able to interact
 * with the target device directly.
 *
 * This class is heavily dependant upon REGEX and a modified version of Devcon
 * (MS-LPL). CADI uses libusbK, which is a generic WinUSB compatible driver for
 * libusbx communication via Heimdall. The drivers which are used (containing 
 * both an x86/x64 variant)are built with WDK 8.1 (for Windows 8.1 support). All driver
 * components are built &amp; digitally signed by Jeremy Loper.
 *
 * WARNING: Modifications to this class can result in system-wide crash of
 * Windows. (I know, I've seen it :-D ) So plan out all modifications prior, and
 * always ensure a null value is never passed to the installer.
 *
 * @author Jeremy Loper jrloper@gmail.com
 * @author Adam Outler adamoutler@gmail.com
 * ************************************************************************
 */
public class DriverInstall {


    /**
     * removeDriverOnCompletion is a primarily user set variable, relating to
     * driver package un-installation. Should driver be removed on script
     * completion? 0 - Unset (will prompt user) 1 - Do not remove driver on
     * completion 2 - Remove driver on script completion This Member is
     * populated on Class Object creation.
     */
    public static volatile int removeDriverOnCompletion;
    /**
     * windowsDriverBlanket is a static Array of targeted USB VID (VendorID
     * numbers) in hexadecimal form. IDs are stored as strings because Java
     * doesn't have a native storage class for hexadecimal (base 16) without
     * conversion to decimal (base 10) This Member is populated on Class Object
     * creation.
     */
    private final String[] windowsDriverBlanket;

    /**
     * WindowsDrivers instantiates the windows driver class.
     *
     * @param promptInit initializes removeDriverOnCompletion member and
     * subsequent prompting action. 0 - Unset (will prompt user) (default) 1 -
     * Do not remove driver on completion 2 - Remove driver on script completion
     */
    public DriverInstall(int promptInit) {
        removeDriverOnCompletion = promptInit;
        Log.level4Debug("WindowsDrivers() Initializing");
        this.windowsDriverBlanket = new String[]{"04E8", "0B05", "0BB4", "22B8", "054C", "2080", "18D1"};
        if (removeDriverOnCompletion == 0) {
            removeDriverOnCompletion = new CASUALMessageObject("@interactionInstallingCADI").showYesNoOption() ? 2 : 1; //set value as 2 if true and 1 if false
        }
    }

    public boolean installKnownDrivers() {
        String[] deviceList = new DriverOperations().getDeviceList(true, true);
        int retVal = 0;
        ArrayList<String> qualifiedDevices = new ArrayList<String>();//get list of devices to be instaled
        for (String device : deviceList) {
            addDeviceToInstallationQueueIfInList(qualifiedDevices, device);
        }
        ArrayList<String[]> uidVid = new ArrayList<String[]>();//get vidUID list
        parseUidVidFromQualifiedDevices(qualifiedDevices, uidVid);
        DriverOperations driver = new DriverOperations();
        for (String[] uv : uidVid) {
            String usbVidString = "USB\\VID_" + uv[0] + "&PID_" + uv[1];
            if (driver.update(usbVidString).contains(" successfully")) {//install each driver
                retVal++;
            }
        }
        return retVal > 0;
    }

    public ArrayList<String[]> parseUidVidFromQualifiedDevices(ArrayList<String> qualifiedDevices, ArrayList<String[]> uidVid) {
        for (String device : qualifiedDevices) {

            if (!device.startsWith("USB\\VID_")) {
                continue;
            }
            device = device.replace("USB\\VID_", "");
            String vid = device.substring(0, 4);

            if (!device.startsWith(vid + "&PID_")) {
                continue;
            }
            device = device.replace(vid + "&PID_", "");
            String uid = device.substring(0, 4);
            uidVid.add(new String[]{vid, uid});
        }
        return uidVid;
    }

    public ArrayList<String> addDeviceToInstallationQueueIfInList(ArrayList<String> installqueue, String device) {
        for (String vid : windowsDriverBlanket) {
            if (device.startsWith("USB\\VID_" + vid)) {
                installqueue.add(device);
            }
        }
        return installqueue;
    }
}
