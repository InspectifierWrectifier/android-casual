/*
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

package CASUAL.language.commands;

import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.language.Command;

/**
 *
 * @author adamoutler
 */
public class OSCommands {

    public boolean operatingSystemCommands(Command cmd) {
        if (cmd.get().startsWith("$LINUXMAC")) {
            if (OSTools.isLinux() || OSTools.isMac()) {
                String removeCommand = "$LINUXMAC";
                cmd.set(removeIdentifiedString(removeCommand, cmd.get()));
                Log.progress("Linux Or Mac Detected: ");
                Log.level4Debug("OS IS LINUX or MAC! remaining commands:" + cmd.get());
            } else {
                return true;
            }
        }
        if (cmd.get().startsWith("$LINUXWINDOWS")) {
            if (OSTools.isLinux() || OSTools.isWindows()) {
                String removeCommand = "$LINUXWINDOWS";
                cmd.set(removeIdentifiedString(removeCommand, cmd.get()));
                Log.progress("Windows or Linux Detected: ");
                Log.level4Debug("OS IS WINDOWS OR LINUX! remaining commands:" + cmd.get());
            } else {
                return true;
            }
        }
        if (cmd.get().startsWith("$WINDOWSMAC")) {
            if (OSTools.isWindows() || OSTools.isMac()) {
                String removeCommand = "$WINDOWSMAC";
                cmd.set(removeIdentifiedString(removeCommand, cmd.get()));
                Log.progress("Mac or Windows Detected: ");
                Log.level4Debug("OS IS Windows or Mac! remaining commands:" + cmd.get());
            } else {
                return true;
            }
        }
        if (cmd.get().startsWith("$LINUX")) {
            if (OSTools.isLinux()) {
                String removeCommand = "$LINUX";
                cmd.set(removeIdentifiedString(removeCommand, cmd.get()));
                Log.progress("Linux Detected: ");
                Log.level4Debug("OS IS LINUX! remaining commands:" + cmd.get());
            } else {
                return true;
            }
        }
        if (cmd.get().startsWith("$WINDOWS")) {
            if (OSTools.isWindows()) {
                Log.progress("Windows Detected: ");
                String removeCommand = "$WINDOWS";
                cmd.set(removeIdentifiedString(removeCommand, cmd.get()));
                Log.level4Debug("OS IS WINDOWS! remaining commands:" + cmd.get());
            } else {
                return true;
            }
        }
        if (cmd.get().startsWith("$MAC")) {
            if (OSTools.isMac()) {
                Log.progress("Mac Detected: ");
                String removeCommand = "$MAC";
                cmd.set(removeIdentifiedString(removeCommand, cmd.get()));
                Log.level4Debug("OS IS MAC! remaining commands:" + cmd.get());
            } else {
                return true;
            }
        }
        return false;
    }
        public String removeIdentifiedString(String identified, String line) {
        line = line.replace(identified, "").trim();
        Log.level4Debug("Processing " + identified);
        
        return line;
    }

}
