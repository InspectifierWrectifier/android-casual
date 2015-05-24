/*OSTools provides a way to determine which platform is being used and various other tools. 
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
package CASUAL;

/**
 * Provides a set of tools designed to identify the operating system and
 * archetecture.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class OSTools {

    /**
     * Operating System is Mac.
     *
     * @return true if mac
     */
    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac");
    }
    //Check for Linux

    /**
     * Operating System is 64 bit as reported by operating system.
     *
     * @return true if 64 bit.
     */
    public static boolean is64bitSystem() {
        if (isWindows()) {
            return isWindows64Arch();
        } else {
            return isMacLinux64Arch();
        }
    }

    /**
     * Returns the arch from Linux
     *
     * @return arch command results or "Linux" on an unsupported machine.
     * Currently Debian based are supported.
     */
    public static String checkLinuxArch() {
        Shell shell = new Shell();
        String[] Command = {"dpkg", "--help"};
        String dpkgResults = shell.silentShellCommand(Command);
        if (dpkgResults.contains("aptitude") || dpkgResults.contains("debian") || dpkgResults.contains("deb")) {
            String[] CommandArch = {"arch"};
            String rawArch = shell.silentShellCommand(CommandArch);
            if (rawArch.contains("armv6")) {
                return "armv6";
            } else if (rawArch.contains("i686")) {
                return "i686";
            } else if (rawArch.contains("x86_64")) {
                return "x86_64";
            } else {
                return "Linux";
            }
        } else {
            return "Linux";
        }
    }

    /**
     * Checks if this is Windows64
     *
     * @return true if Windows 64.
     */
    public static boolean isWindows64Arch() {
        return System.getenv("ProgramFiles(x86)") != null;
    }

    /**
     * gets OS name
     *
     * @return string representing OSName
     */
    public static String OSName() {
        return System.getProperty("os.name");
    }

    /**
     * Checks if system is Linux.
     *
     * @return true if Linux
     */
    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nux");
    }

    /**
     * Checks if system is Windows.
     *
     * @return True if Windows
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    /**
     * Checks if Mac/Linux 64 bit
     *
     * @return true if 64 bit mac/linux.
     */
    public static boolean isMacLinux64Arch() {
        String[] CommandArch = {"arch"};
        return new Shell().silentShellCommand(CommandArch).contains("64");
    }

}
