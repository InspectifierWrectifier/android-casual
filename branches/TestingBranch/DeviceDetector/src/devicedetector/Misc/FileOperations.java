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

/**
 *
 * @author Logan Ludington loglud@casual-dev.com
 */
public class FileOperations {
    
     /**
     * recursively deletes a file path
     *
     * @param path
     * @return Delete Successful
     */
    public static boolean recursiveDelete(File path) {
        File[] c = path.listFiles();
        if (path.exists()) {
            if (c != null && c.length > 0) {
                for (File file : c) {
                    if (file.isDirectory()) {
                        recursiveDelete(file);
                        file.delete();
                    } else {
                        file.delete();
                    }
                }
            }
            return path.delete();
        }
        return false;
    }
    
    public static void deleteFileOnExit(final File path) {
        
        
        if ((!path.isFile()) && (!path.isDirectory()))
            return;
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (path.isFile())
                {
                    if(path.delete())
                        System.out.println("FILEOPERATION.DELETEFILEONEXIT: SUCCESS \nDeleted"
                                + " file at: \n" + path.toString());
                    else
                        System.out.println("FILEOPERATION.DELETEFILEONEXIT: FAILED \nCould not delete"
                                + " file at: \n" + path.toString());
                    
                }
                else {
                    if(recursiveDelete(path))
                        System.out.println("FILEOPERATION.DELETEFILEONEXIT: SUCCESS \nDeleted" +
                            " folder at: \n" + path.toString());
                    else
                        System.out.println("FILEOPERATION.DELETEFILEONEXIT: FAILED \nCould not delete"
                                + " file at: \n" + path.toString());
                }
                    
            }
        });
            
        
    }
    
}
