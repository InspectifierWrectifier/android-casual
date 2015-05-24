/*
 * Copyright (C) 2013 Logan Ludington loglud@casual-dev.com
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package devicedetector.Misc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Logan Ludington loglud@casual-dev.com
 */
public class OperatingSystem {
    
    public enum OSArch {
        ARMV6,
        X86,
        X64
    }
    
    public static final OSArch osArch = getOSArch();
    
    public enum OSName {
        MACOSX,
        WINDOWS,
        LINUX  
    }
    
    public static final OSName osName = getOSName();
    
    public static final String Slash = System.getProperty("file.separator");
    
    public static final String tempFolder = getTempFolder();
    
    public static void main(String[] args) {
        System.out.println(OperatingSystem.tempFolder);
        System.out.println(OperatingSystem.osName);
        System.out.println(OperatingSystem.osArch);
        System.out.println(OperatingSystem.Slash);
        System.out.println(OperatingSystem.tempFolder);
    }

    
    private static OSName getOSName() {
        switch (System.getProperty("os.name").toLowerCase().charAt(0)) {
            case 'l' :
                return OSName.LINUX;
            case 'w' :
                return OSName.WINDOWS;
            case 'm' :
                return OSName.MACOSX;
            default:
                return null;
        }
    }
    
    private static OSArch getOSArch() {
        if (System.getProperty("os.arch").toLowerCase().contains("armv6")) 
            return OSArch.ARMV6;
        else if (System.getProperty("os.arch").toLowerCase().contains("64"))
            return OSArch.X64;
        else if (System.getProperty("os.arch").toLowerCase().contains("86"))
            return OSArch.X86;
        else
            return null;
    }
    
    private static String getTempFolder() {
        String user = System.getProperty("user.name");
        String tmp = System.getProperty("java.io.tmpdir");
        tmp = tmp.endsWith(Slash) ? tmp : tmp + Slash;
        SimpleDateFormat sdf = new SimpleDateFormat("-yyyy-MM-dd-HH.mm.ss");
        tmp = tmp + "PRELOADER" + user + sdf.format(new Date()).toString() + Slash;
        if (new File(tmp).mkdir())
        {
            System.out.println("OPERATINGSYSTEM.GETTEMPFOLDER: SUCCESS \nCreated Temp Folder"
                    + " at: \n" + tmp);
            FileOperations.deleteFileOnExit(new File(tmp));
            return tmp;
        }
        else
        {
            System.out.println("OPERATINGSYSTEM.GETTEMPFOLDER: FAILED \nCould not create Temp Folder"
                    + " at: \n" + tmp);
            return null;
        }
        
    }
    
    
}
