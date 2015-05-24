/* Handles installation of Heimdall
 * Copyright (c) 2011 Adam Outler
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package CASUAL.communicationstools.heimdall;

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.ResourceDeployer;
import CASUAL.Shell;
import CASUAL.communicationstools.heimdall.drivers.DriverInstall;
import CASUAL.network.CASUALUpdates;
import java.io.File;
import java.io.IOException;

/**
 * Heimdall Install provides methods to install Heimdall under the CASUAL
 * environment
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class HeimdallInstall {

    /**
     * Heimdall version.
     */
    public static final String heimdallVersion = "140"; //primary version string

    final String[] WindowsDriverBlanket = {"18D1", "04E8", "0B05", "0BB4", "22B8", "054C", "2080"};
    /**
     * Vendor ID detected
     */
    public String VID = "";
    /**
     * Device ID detected
     */
    public String PID = "";

    FileOperations FileOperations = new FileOperations();
    Shell shell = new Shell();

    String installLinux(String tempFolder) {
        
        FileOperations fo = new FileOperations();
        String arch = OSTools.checkLinuxArch();
        String resource[];
        String heimdall;
        if (arch.contains("armv6")) {
            resource = HeimdallTools.linuxArmv6Location;
        } else if (arch.contains("i686")) {
            resource = HeimdallTools.linux32Location;
        } else if (arch.contains("x86_64")) {
            resource = HeimdallTools.linux64Location;
        } else {
            //unsupported; go get heimdall yourself;
            Log.level0Error("@incompatibleWithHeimdal");
            resource = new String[]{};
        }
        ResourceDeployer rd = new ResourceDeployer();
        for (String heimdallResource : resource) {
            String debDeployed = rd.deployResourceTo(heimdallResource, tempFolder);
            shell.elevateSimpleCommandWithMessage(new String[]{"dpkg", "-i", debDeployed}, "Permissions escillation required to install Heimdall");
        }
        String heimdallDeployed = "heimdall";

        if (checkHeimdallVersion(heimdallDeployed)) {
            return heimdallDeployed;
        } else {
            return "";
        }
//Windows

    }

    /**
     * Installs windows drivers
     *
     * @return true if drivers installed properly
     */
    public boolean installWindowsDrivers() {

        return new DriverInstall(0).installKnownDrivers();

    }

    /**
     * displays a message to the user that Windows permissions were not
     * obtainable
     */
    public void displayWindowsPermissionsMessageAndExit() {
        if (OSTools.isWindows()) {
            new CASUALMessageObject("@interactionwindowsRunAsMessage" + getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).showErrorDialog();
        }
        CASUALMain.shutdown(0);
    }

    void runWinHeimdallInstallationProcedure() {
        installWindowsDrivers();
    }

    /**
     * checks the heimdall version against version expected from Statics
     *
     * @param binaryLocation lication to Heimdall binary
     * @return true if version is good
     */
    public boolean checkHeimdallVersion(String binaryLocation) {
        String[] command = {binaryLocation, "version"};
        String Version = new Shell().silentShellCommand(command);
        if (!Version.contains("CritERROR!!!")) {
            Version = Version.replaceAll("\n", "").replaceAll("v", "");
            if (Version.contains(" ")) {
                Version = Version.split(" ")[0];
            }
            Version = Version.replaceAll("\\.", "");
            if (Version.length() == 2) {
                Version += 0;
            }
        } else {
            return false;
        }
        char[] digits = Version.toCharArray();
        int commandLineVersion = Integer.parseInt(new String(digits));
        int resourceVersion = Integer.parseInt(heimdallVersion);
        return commandLineVersion >= resourceVersion;
    }

    /**
     * deploys heimdal
     *
     * @return true if deployed
     */
    String installWindows(String[] windowsLocation, String tempFolder) {
        HeimdallTools ht = new HeimdallTools();
        String expectedLocation = tempFolder + "heimdall.exe";
        if (ht.fileIsDeployedProperly(expectedLocation)) {
            return expectedLocation;
        }

        ResourceDeployer rt = new ResourceDeployer();
        for (String res : HeimdallTools.windowsLocation) {
            String name = tempFolder + new File(res).getName();
            rt.copyFromResourceToFile(res, name);
        }
        Log.level4Debug("deployHeimdallForWindows- verifying Heimdall deployment");
        if (ht.fileIsDeployedProperly(expectedLocation)) { //try with redist files
            Log.level4Debug("heimdall install sucessful");
            return expectedLocation;
        }
        return null;

    }

    String installMac(String[] resourceLocation, String tempFolder) throws InterruptedException, IOException {
        ResourceDeployer rd = new ResourceDeployer();
        String exec;
        if ((exec = getFile(rd.deployResourceTo(resourceLocation, tempFolder), "")).isEmpty()) {
            exec = new CASUALUpdates(CASUALMain.getSession()).CASUALRepoDownload("https://android-casual.googlecode.com/svn/trunk/repo/heimdall.properties");
        }
        new Shell().liveShellCommand(new String[]{"open", "-W", exec}, true);
        new CASUALMessageObject("@interactionUnplugItAndPlugItBackIn").showErrorDialog();
        return "heimdall";
    }

    private String getFile(String[] fullyQualifiedPaths, String filename) {
        if (fullyQualifiedPaths.length == 1) {
            return fullyQualifiedPaths[0];
        }
        String retval = "";
        for (String value : fullyQualifiedPaths) {
            if (value.endsWith(filename)) {
                retval = filename;
                break;
            }
        }
        return retval;
    }

}
