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

import CASUAL.CASUALMain;
import CASUAL.CASUALSessionData;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.Shell;
import CASUAL.archiving.Unzip;
import CASUAL.misc.StringOperations;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jeremy R. Loper &lt;jrloper@gmail.com&gt;
 */
public class DriverOperations {


    /**
     * driverExtracted this static member is toggled true upon a successful
     * driver package decompression.
     *
     */
    private static volatile boolean driverExtracted = false;

    /**
     * CADI Windows Driver for Windows Vista and higher.
     */
    private final static String cadiDrivers = "/CASUAL/communicationstools/heimdall/drivers/resources/CADI.zip";
    /**
     * pathToCADI contains the full path to the root folder of where driver
     * package(s) are (or will be). This Member is populated on Class Object
     * creation.
     */
    private final String pathToCADI;

    public DriverOperations() {
        this.pathToCADI =CASUALMain.getSession().getTempFolder() + "CADI" + CASUALSessionData.slash;
        if (!driverExtracted) {
            try {
                driverExtract(pathToCADI);
            } catch (FileNotFoundException ex) {
                Log.errorHandler(ex);
                return;
            } catch (IOException ex) {
                Log.errorHandler(ex);
                return;
            }
            driverExtracted = true;
        }
    }

    /**
     * driverExtract extracts the contents of CADI.zip from CASUAL's resources
     *
     * @param pathToExtract the desired destination folders full path.
     *
     * @throws FileNotFoundException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     *
     * @return true if successful, false otherwise
     */
    private boolean driverExtract(String pathToExtract) throws FileNotFoundException, IOException {
        if(new FileOperations().makeFolder(pathToCADI)) {
            Log.level4Debug("driverExtract() Unzipping CADI");
            Unzip.unZipResource(CASUALMain.getSession(),cadiDrivers, pathToExtract);
            return true;
        }
        return false;
    }

    /**
     * getDeviceList parses installer output for connected USB devices of the
     * specified VID; Any matching devices are stored for return in a String
     * Array.
     *
     * @param VID a String containing a four character USB vendor ID code in
     * hexadecimal
     * @return is a String Array of matching connected devices, null otherwise
     */
    public String[] getDeviceList(String VID) {
        if (VID.isEmpty()) {
            Log.level0Error("getDeviceList() no VID specified");
            return null;
        }
        String rawDeviceList = find("*USB\\VID_" + VID + "*");

        if (rawDeviceList == null) {
            Log.level0Error("getDeviceList() installer returned null!");
            return null;
        }
        Pattern pattern = regexPattern(PatternChoice.MATCHINGDEVICES);
        if (pattern == null) {
            Log.level0Error("getDeviceList() getRegExPattern() returned null!");
            return null;
        }
        pattern = regexPattern(PatternChoice.ALLDEVICES);

        if (pattern == null) {
            Log.level0Error("getDeviceList() getRegExPattern() returned null!");
            return null;
        }
        pattern = regexPattern(PatternChoice.ALLDEVICES);
        Matcher matcher = pattern.matcher(rawDeviceList);
        ArrayList<String> al = new ArrayList<String>();
        while (matcher.find()) {
            String replacedQuote = StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", ""));
            al.add(replacedQuote);
        }
        String[] retval = al.toArray(new String[al.size()]);
        if (retval.length == 0) {
            retval = null;
        }
        return retval;
    }

    /**
     * getDeviceList parses installer output for devices specified Any matching
     * devices are stored for return in a String Array.
     *
     * @param onlyConnected boolean for presently connected devices only
     * @param onlyUSB boolean for USB devices only
     * @return is a String Array of matching devices, null otherwise
     */
    public String[] getDeviceList(boolean onlyConnected, boolean onlyUSB) {
        String rawDeviceList;
        if (onlyConnected && onlyUSB) {
            rawDeviceList = find("USB*"); //All present USB devices
        } else if (onlyConnected && !onlyUSB) {
            rawDeviceList = find("*"); //All present devices
        } else if (!onlyConnected && onlyUSB) {
            rawDeviceList = findall("USB*"); //All installed USB devices
        } else {
            rawDeviceList = findall("*"); //All installed devices
        }
        if (rawDeviceList == null) {
            Log.level0Error("getDeviceList() installer returned null!");
            return null;
        }
        Pattern pattern = regexPattern(PatternChoice.MATCHINGDEVICES);
        if (pattern == null) {
            Log.level0Error("getDeviceList() getRegExPattern() returned null!");
            return null;
        }
        pattern = regexPattern(PatternChoice.ALLDEVICES);
        Matcher matcher = pattern.matcher(rawDeviceList);
        ArrayList<String> al = new ArrayList<String>();
        while (matcher.find()) {
            String replacedQuote = StringOperations.removeLeadingAndTrailingSpaces(matcher.group(0).replace("\"", ""));
            al.add(replacedQuote);
        }
        String[] retval = al.toArray(new String[al.size()]);
        if (retval.length == 0) {
            retval = null;
        }
        return retval;
    }

    /**
     * regexPattern returns a Pattern Object of the requested REGEX pattern.
     *
     * @param whatPattern a predefined String name for a REGEX pattern.
     * @return a compiled REGEX Pattern if requested pattern exists, otherwise
     * null.
     */
    public Pattern regexPattern(PatternChoice whatPattern) {
        switch (whatPattern) {
            case ORPHANS:
                return Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}.*(?=:\\s[CASUAL's|Samsung]+\\s[Android\\sDevice])");
            case CASUALDRIVER:
                return Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}.*(?=:\\s[CASUAL's|Samsung]+\\s[Android\\sDevice])");
            case INF:
                return Pattern.compile("[o|Oe|Em|M]{3}[0-9]{1,4}\\.inf(?=\\s*Provider:\\slibusbK\\s*Class:\\s*libusbK USB Devices)");
            case INSTALL:
                return Pattern.compile("USB.?VID_[0-9a-fA-F]{4}&PID_[0-9a-fA-F]{4}(?=.*:)");
            case MATCHINGDEVICES:
                return Pattern.compile("(?<=\\s)[0-9]{1,3}?(?=[\\smatching\\sdevice\\(s\\)\\sfound])");
            case ALLDEVICES:
                return Pattern.compile("\\S+(?=\\s*:\\s)");
            default:
                Log.level0Error("getRegExPattern() no known pattern requested");
                return null;
        }
    }

    /**
     * getCASUALDriverCount parses installer output for all CASUAL driver
     * installations and returns an integer count
     *
     * @return integer count of CASUAL driver installs
     */
    public int getCASUALDriverCount() {
        int devCount = 0;
        String outputBuffer = findall("USB*");
        if (outputBuffer == null) {
            Log.level0Error("removeOrphanedDevices() installer returned null!");
            return 0;
        }
        Pattern pattern = regexPattern(PatternChoice.CASUALDRIVER);
        if (pattern == null) {
            Log.level0Error("removeOrphanedDevices() getRegExPattern() returned null!");
            return 0;
        }
        Matcher matcher = pattern.matcher(outputBuffer);
        while (matcher.find()) {
            devCount++;
        }
        return devCount;
    }

    public String update(String HWID) {
        if (!HWID.isEmpty()) {
            return sendCommand("update " + pathToCADI + "cadi.inf " + "\"" + HWID);
        } else {
            return null;
        }
    }

    public String remove(String HWID) {
        if (!HWID.isEmpty()) {
            return sendCommand("remove " + HWID);
        } else {
            return null;
        }
    }

    public String delete(String infName) {
        if (!infName.isEmpty()) {
            return sendCommand("-f dp_delete " + infName);
        } else {
            return null;
        }
    }

    public String find(String searchString) {
        if (!searchString.isEmpty()) {
            return sendCommand("find " + searchString);
        } else {
            return null;
        }
    }

    public String findall(String searchString) {
        if (!searchString.isEmpty()) {
            return sendCommand("findall " + searchString);
        } else {
            return null;
        }
    }

    public String enumerate() {
        return sendCommand("dp_enum");
    }

    public boolean rescan() {
        return (sendCommand("rescan").contains("Scanning for new hardware"));
    }

    private String sendCommand(String cmd) {
        String retval, exec = pathToCADI + (OSTools.is64bitSystem() ? "driver_x64.exe " : "driver_x86.exe ") + cmd;
        retval = new Shell().timeoutShellCommand(new String[]{"cmd.exe", "/C", "\"" + exec + "\""}, 90000); //1000 milliseconds — one second
        if (retval.contains(" failed")) {
            exec = pathToCADI + (OSTools.is64bitSystem() ? "driver_x64_elevate.exe " : "driver_x86_elevate.exe ") + cmd;
            retval = new Shell().timeoutShellCommand(new String[]{"cmd.exe", "/C", "\"" + exec + "\""}, 90000); //1000 milliseconds — one second
        }
        Log.level2Information(retval);
        return retval;
    }

    public enum PatternChoice {

        ORPHANS, CASUALDRIVER, INF, INSTALL, MATCHINGDEVICES, ALLDEVICES
    }
}
