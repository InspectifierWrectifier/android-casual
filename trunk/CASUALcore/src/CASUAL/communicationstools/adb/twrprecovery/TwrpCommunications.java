/*TWRPCommunications provides methods of accessing TWRP basic information
 * Copyright (C) 2014 adamoutler
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
package CASUAL.communicationstools.adb.twrprecovery;

import CASUAL.Log;
import CASUAL.communicationstools.adb.ADBTools;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author adamoutler
 */
public class TwrpCommunications {

    ADBTools adb = new ADBTools();
    int timeout = 4000;

    /**
     * Puts the device into TWRP recovery mode.
     *
     * @return true if TWRP is detected and device is in recovery.
     */
    public boolean rebootTWRP() {
        Log.level2Information("Checking TWRP installation");
        if (isTwrpRunning()) {
            Log.level4Debug("TWRP is running.");
            waitForDevice();
            return true;
        } else if (isTwrpInstalled()) {
            //we need to reboot recovery
            Log.level2Information("Rebooting device into TWRP");
            adb.run(new String[]{"reboot", "recovery"}, timeout, false);
            //sleep away ADB if it's still running
            adb.run(new String[]{"shell", "sleep 99"}, 99, false);
            waitForDevice();
            //verify device is in twrp
            return isTwrpRunning();
        }
        Log.level0Error("TWRP is not installed.");
        return false;
    }

    /**
     * verifies TWRP is installed by checking for "\nStarting TWRP " in
     * /cache/recovery/last_log.
     *
     * @return true if TWRP logs are detected in cache
     */
    public boolean isTwrpInstalled() {
        Log.level4Debug("Checking if TWRP is installed.");
        String twrpval = adb.run(new String[]{"shell", "su -c 'cat /cache/recovery/last_log'"}, timeout, false);
        return twrpval.contains("\nStarting TWRP ");
    }

    /**
     * verifies TWRP is running by checking for for "\nStarting TWRP " in
     * /tmp/recovery.log.
     *
     * @return true if TWRP is detected
     */
    public boolean isTwrpRunning() {
        Log.level4Debug("Checking if TWRP is running.");
        String twrpval = adb.run(new String[]{"shell", "cat /tmp/recovery.log"}, timeout, false);
        return twrpval.contains("\nStarting TWRP ");
    }

    /**
     * exits from TWRP.
     */
    public void exitRecovery() {
        adb.run(new String[]{"reboot"}, timeout, false);
    }

    /**
     * Waits for the device to be ready. 
     */
    public void waitForDevice() {
        adb.waitForDevice();
    }

    /**
     * deposits the script file in /cache/recovery/openrecoveryscript and runs.
     * Running is performed by killing the recovery binary, running in memory
     * then letting TWRP perform the actions in the script.
     *
     * @param script script to run
     * @throws java.io.IOException  {@inheritDoc}
     */
    public void runTwrpScript(OpenRecoveryScript script) throws IOException {
        runTwrpScript(script.toString());
    }

    /**
     * deposits the script file in /cache/recovery/openrecoveryscript and runs.
     * Running is performed by killing the recovery binary, running in memory
     * then letting TWRP perform the actions in the script.
     *
     * @param script script to run 
     * @throws java.io.IOException {@inheritDoc}
     */
    public void runTwrpScript(String script) throws IOException {
        Log.level2Information("verifying TWRP mode");
        rebootTWRP();  //reboot twrp
        Log.level4Debug("writing recovery script to temp location");
        File f=File.createTempFile("openrecovery", "");
        f.delete();
                
        String diskLocation = f.getAbsolutePath();
        writeToFile(script, diskLocation); //create a local file
        pushRecoveryScript(diskLocation); //push it to the device
        restartTWRP();
    }

    /**
     * killall the "recovery" process on the device. This forces TWRP to reboot
     * and run the openrecoveryscript
     */
    public void restartTWRP() {
        Log.level3Verbose("killing TWRP recovery (likely to run script)");
        adb.run(new String[]{"shell", "killall recovery"}, timeout, false);
    }

    private void pushRecoveryScript(String diskLocation) {
        adb.run(new String[]{"push", diskLocation, "/cache/recovery/openrecoveryscript"}, 8000, false);
    }

    private void writeToFile(String Text, String File) throws IOException {
        Log.level4Debug("Writing script to file");
        BufferedWriter bw;
        FileWriter fw = new FileWriter(File, true);
        bw = new BufferedWriter(fw);
        bw.write(Text);
        bw.flush();
        fw.close();
        Log.level4Debug("Write Finished");
    }
}
