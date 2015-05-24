/*FileOperations provides a group of tools which relate to files.
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
package CASUAL.misc;

import java.io.File;

/**
 *
 * @author Jeremy
 */
public class CASUALScrFilter extends javax.swing.filechooser.FileFilter {

    /**
     * chooses only valid script files
     *
     * @param file file to test
     * @return true if file is a script
     */
    @Override
    public boolean accept(File file) {
        // Allow only directories, or files with ".txt" extension
        return file.isDirectory() || file.getAbsolutePath().endsWith(".scr");
    }

    /**
     * files to be listed
     *
     * @return casual scripts only
     */
    @Override
    public String getDescription() {
        // This description will be displayed in the dialog,
        // hard-coded = ugly, should be done via I18N
        return "CASUAL Script (*.scr)";
    }
}
