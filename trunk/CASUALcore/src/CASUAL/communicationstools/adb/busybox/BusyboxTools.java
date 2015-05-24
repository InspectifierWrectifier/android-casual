/*BusyboxTools deploys and gives an on-device reference to busybox.
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
package CASUAL.communicationstools.adb.busybox;

import CASUAL.CASUALMain;
import CASUAL.Log;
import CASUAL.ResourceDeployer;
import CASUAL.Shell;
import CASUAL.communicationstools.adb.ADBTools;

/**
 * BusyboxTools deploys and gives an on-device reference to busybox.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class BusyboxTools {

    /**
     * Busybox for Linux ARMv4tl is the most compatible with all ARM according
     * to Busybox site. This is intended for the device, not the host.
     */
    public static final String busyboxARM = "/CASUAL/communicationstools/adb/busybox/resources/busybox-armv4tl";
    /**
     * Busybox for Linux x86. This is intended for the device, not the host.
     */
    public static final String busyboxX86 = "/CASUAL/communicationstools/adb/busybox/resources/busybox-i686";
    //Windows permissions elevator

    /**
     * Deploys busybox to device and returns the location of busybox on device.
     *
     * @return string path to busybox on device
     */
    public static String getBusyboxLocation() {
        BusyboxTools bbtools = new BusyboxTools();
        if (bbtools.busyboxIsInstalled()) {
            return bbtools.busyboxLocation;
        } else {
            Log.level4Debug("deploying busybox");
            return bbtools.deployBusybox();
        }
    }

    /**
     * reset method.  unused currently, it is here for the future.
     */
    public static void reset() {
    }

    final String busyboxLocation = "/data/local/tmp/busybox";
    ADBTools adb = new ADBTools();
    Shell shell = new Shell();

    private String getDeviceArch() {
        if (CASUALMain.getSession().CASPAC != null) {
            if (!CASUALMain.getSession().CASPAC.getActiveScript().getDeviceArch().isEmpty()) {
                return CASUALMain.getSession().CASPAC.getActiveScript().getDeviceArch();
            }
        }
        String cpuinfo = shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "shell", "cat /proc/cpuinfo"});
        String[] lines = cpuinfo.split("\n");
        for (String line : lines) {
            if (line.contains("Processor") && line.contains("ARM")) {
                return "ARM";

            }
        }
        return "X86";
    }

    private boolean busyboxIsInstalled() {

        String temp = shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "shell", "chmod 711 " + busyboxLocation + " 2&1>dev/null ;ls " + busyboxLocation});

        return !temp.contains("No such") && !temp.contains("found");
    }

    private String deployBusybox() {
        ResourceDeployer rd = new ResourceDeployer();
        String busyboxOnHost = CASUALMain.getSession().getTempFolder() + "busybox";
        String busyboxResource;
        if (getDeviceArch().equals("ARM")) {
            busyboxResource = busyboxARM;
        } else {
            busyboxResource = busyboxX86;
        }
        rd.copyFromResourceToFile(busyboxResource, busyboxOnHost);
        String[] installCmd = {adb.getBinaryLocation(), "push", busyboxOnHost, busyboxLocation};
        new Shell().silentShellCommand(installCmd);
        String[] checkCmd = new String[]{adb.getBinaryLocation(), "shell", "chmod 711 /data/local/tmp/busybox;ls /data/local/tmp"};
        String check = new Shell().sendShellCommand(checkCmd);
        if (check.contains("busybox")) {
            return this.busyboxLocation;
        } else {
            return null;
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Location:").append(busyboxLocation);
        sb.append("\nDeployed:").append(busyboxIsInstalled());
        return busyboxLocation;
    }

}
