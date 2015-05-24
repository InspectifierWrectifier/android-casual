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

package CASUAL.language;
import CASUAL.instrumentation.ModeTrackerInterface.Mode;
/**
 *
 * @author adamoutler
 */
public class LanguageTracker {
    public static Mode adb (Command command){
        String cmd=getControlCommand("adb", command.command);
        if (cmd.equals("push")) return Mode.ADBpush;
        if (cmd.equals("wait-for-device")) return Mode.ADBsearching;
        if (cmd.equals("pull"))return Mode.ADBpull;
        if (cmd.equals("sideload"))return Mode.ADBsideload;
        if (cmd.equals("reboot"))return Mode.ADBreboot;
        if (cmd.equals("shell"))return Mode.ADBshell;
        return Mode.ADB;
    }
    
    public static Mode fastboot (Command command){
        String cmd=getControlCommand("fastboot", command.command);
        if (cmd.equals("boot")) return Mode.FastbootBooting;
        if (cmd.equals("flash")) return Mode.FastbootFlashing;
        return Mode.Fastboot;
    }
    
    public static Mode heimdall (Command command){
        String cmd=getControlCommand("heimdall", command.command);
        if (cmd.equals("flash")) return Mode.HeimdallFlash;
        if (cmd.equals("print-pit")) return Mode.HeimdallPullPartitionTable;
        if (cmd.equals("download-pit")) return Mode.HeimdallPullPartitionTable;
        return Mode.Heimdall;
    }
    
    
    
    
    private static String getControlCommand(String tool, String command){
        String cmd=command.toLowerCase().replaceFirst("$"+tool, "").trim();
        cmd=cmd.toLowerCase().replaceFirst(tool, "").trim();
        return cmd.split(" ")[0];
    }
    
    
    
}
