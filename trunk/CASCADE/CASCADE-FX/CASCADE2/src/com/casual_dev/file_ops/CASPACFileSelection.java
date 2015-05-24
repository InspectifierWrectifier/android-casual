/*Provides a file chooser for CASPACs
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
package com.casual_dev.file_ops;

import CASUAL.Log;
import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author adamoutler
 */
public class CASPACFileSelection {

    public String showFileChooser(Stage stage, String initial) {
        FileChooser chooser = new FileChooser();
         chooser.setTitle("Select CASPAC file");
         if (!new File(initial).isDirectory()){
             initial=new File(initial).getParent();
         }
         
        chooser.setInitialDirectory(ifInitialEmptyUseHome(initial));
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");
        FileChooser.ExtensionFilter cpFilter = new FileChooser.ExtensionFilter("CASPAC files (*.CASPAC)", "*.CASPAC","*.caspac");
       
        chooser.getExtensionFilters().addAll(allFilter,cpFilter);
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            return file.getAbsolutePath();
        }
        return "";
    }
    
    public String showFolderChooser(Stage stage, String initial){
         DirectoryChooser chooser = new DirectoryChooser();
         chooser.setTitle("Select Folder");

         
        chooser.setInitialDirectory( ifInitialEmptyUseHome(initial));      
        File dir = chooser.showDialog(stage);

        if (dir != null) {
            return dir.getAbsolutePath();
        }
        return "";
    }
    
    private File ifInitialEmptyUseHome(String initial){
        if (initial==null||initial.isEmpty()||!new File(initial).exists()){
             initial=System.getProperty("user.home");
         }
        Log.level3Verbose("choosing "+initial+" as directory");
        return new File(initial);
    }
}
