/**
 * *****************************************************************************
 * This file is part of CADI a library of CASUAL.
 * 
* Copyright (C) 2014 Jeremy R. Loper &lt;jrloper@gmail.com&gt;
 *
 * CADI is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
* CADI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
* You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/ .
 * 
******************************************************************************
 */
package CASUAL.communicationstools.heimdall.drivers;

import CASUAL.Log;
import CASUAL.communicationstools.heimdall.drivers.DriverOperations.PatternChoice;
import CASUAL.misc.StringOperations;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jeremy R. Loper &lt;jrloper@gmail.com&gt;
 */
public class DriverRemove {

    /**
     * windowsDriverBlanket is a static Array of targeted USB VID (VendorID
     * numbers) in hexadecimal form. IDs are stored as strings because Java
     * doesn't have a native storage class for hexadecimal (base 16) without
     * conversion to decimal (base 10) This Member is populated on Class Object
     * creation.
     */
    private final String[] windowsDriverBlanket;

    public DriverRemove() {
        this.windowsDriverBlanket = new String[]{"04E8", "0B05", "0BB4", "22B8", "054C", "2080", "18D1"};
        Log.level2Information("uninstallCADI() Initializing");
        Log.level2Information("uninstallCADI() Scanning for CADI driver package(s)");

    }

    public boolean removeDriver() {
        deleteOemInf();
        Log.level2Information("uninstallCADI() Scanning for orphaned devices");
        boolean driverRemoved = true;
        for (String vid : windowsDriverBlanket) {
            driverRemoved = removeOrphanedDevices(vid);
        }

        Log.level2Information("removeDriver() Windows will now scan for hardware changes");
        if (!new DriverOperations().rescan()) {
            Log.level0Error("removeDriver() rescan() failed!");
        }
        return driverRemoved;
    }

    /**
     * deleteOemInf parses output from devconCommand via regex to extract the
     * name of the *.inf file from Windows driver store. Extraction of the file
     * name is determined by setup classes &amp; provider names.
     *
     * @return a String Array of *.inf files matching the search criteria.
     */
    public boolean deleteOemInf() {
        DriverOperations driver = new DriverOperations();
        Log.level2Information("deleteOemInf() Enumerating installed driver packages");
        int resultSum = 0;
        Pattern pattern = driver.regexPattern(PatternChoice.INF);
        String outputBuffer = driver.enumerate();
        if (outputBuffer == null) {
            Log.level0Error("deleteOemInf() installer returned null!");
            return false;
        }
        Matcher matcher = pattern.matcher(outputBuffer);
        while (matcher.find()) {
            Log.level2Information("removeDriver() Forcing removal of driver package" + matcher.group(0));
            String result = driver.delete(matcher.group(0));
            if (result == null || result.contains("Driver package")) {
                Log.level0Error("removeDriver() installer returned null!");
            }
            resultSum++;
        }
        return resultSum > 0;
    }

    /**
     * removeOrphanedDevices parses installer output of any current or
     * previously installed USB device drivers for the specified VID. Any
     * matching device drivers are uninstalled
     *
     * @param VID a String containing a four character USB vendor ID code in
     * hexadecimal
     * @return a String Array of installer output from attempted uninstalls of
     * drivers
     */
    public boolean removeOrphanedDevices(String VID) {
        int i = 0;
        int resultSum = 0;
        String result;
        DriverOperations driver = new DriverOperations();
        if (VID.isEmpty()) {
            Log.level0Error("removeOrphanedDevices() no VID specified");
            return false;
        }
        Pattern pattern;
        pattern = driver.regexPattern(PatternChoice.MATCHINGDEVICES);
        if (pattern == null) {
            Log.level0Error("removeOrphanedDevices() getRegExPattern() returned null!");
            return false;
        }
        String outputBuffer = driver.findall("*USB\\VID_" + VID + "*");
        if (outputBuffer == null) {
            Log.level0Error("removeOrphanedDevices() installer returned null!");
            return false;
        }
        pattern = driver.regexPattern(PatternChoice.ORPHANS);
        if (pattern == null) {
            Log.level0Error("removeOrphanedDevices() getRegExPattern() returned null!");
            return false;
        }
        Matcher matcher = pattern.matcher(outputBuffer);
        while (matcher.find()) {
            Log.level2Information("removeOrphanedDevices() Removing orphaned device " + "\"@" + StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", "")) + "\"");
            result = driver.remove("\"@" + StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", "")) + "\"");
            if (result.isEmpty()) {
            } else if (result.contains("device(s) are ready to be removed. To remove the devices, reboot the system.")) {
                resultSum++;
            } else {
                Log.level0Error("removeOrphanedDevices() installer returned null!");
            }
            i++;
        }
        return resultSum > 0;
    }

}
