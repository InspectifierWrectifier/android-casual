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

package CASUAL;

/**
 *
 * @author adamoutler
 */
public class Diagnostics {
    
    public static String getDiagnosticReportOneLine(CASUALSessionData sd){
        return diagnosticReport(sd).replace("\n", " | ");
    }
    
    public static String diagnosticReport(CASUALSessionData sd){
        StringBuilder sb=new StringBuilder();
        sb.append("OS:").append(System.getProperty("os.name")).append(" Version:").append(System.getProperty("os.version")).append(" Architecture:").append(System.getProperty("os.arch")).append(" Separator:").append(System.getProperty("file.separator"));
        sb.append("\n").append("Java:").append(System.getProperty("java.vendor")).append(" Version:").append(System.getProperty("java.version")).append(" Website:").append(System.getProperty("java.vendor.url"));
        sb.append("\n").append("WorkingDir: ").append(System.getProperty("user.dir")).append(" CASUALDir:").append(sd.getTempFolder());
        sb.append("\n").append("CASUAL is handling this system as:");
        if (OSTools.isLinux()){
            if (OSTools.is64bitSystem()){
                sb.append("Linux 64 bit, ").append(OSTools.checkLinuxArch());
            } else {
                sb.append("Linux 32 bit, ").append(OSTools.checkLinuxArch());
            }
        } else if (OSTools.isMac()){
            if (OSTools.is64bitSystem()){
                sb.append("Mac 64 bit");
            } else {
                sb.append("Mac 64 bit");
            }
        } else if (OSTools.isWindows()){
            if (OSTools.is64bitSystem()){
                sb.append("Windows 64 bit");
            } else {
                sb.append("Windows 32 bit");
            }
        } else {
            if (OSTools.is64bitSystem()){
                sb.append("Unsupported 64 bit").append(OSTools.OSName());
            } else {
                sb.append("Unsupported 32 bit").append(OSTools.OSName());
            }
        }
        sb.append(" -- End Diag");
        
        return sb.toString();
    }
}
