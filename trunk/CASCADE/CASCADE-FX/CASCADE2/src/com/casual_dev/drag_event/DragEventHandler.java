/*Handles drag events in CASCADE2. 
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
package com.casual_dev.drag_event;

import CASUAL.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;

/**
 *
 * @author adamoutler
 */
public class DragEventHandler {
    
    static long time = 0;
    static List<File> fileListForDropEvent;

    @FXML

   public void setzipFileEventList(DragEvent event) {
        if (null == event) {
            return;
        }
        fileListForDropEvent = event.getDragboard().getFiles();
        Log.level4Debug("Dropped files released to list"+fileListForDropEvent);
    }
    
    public void markTimeOfDrop(){
          time = System.currentTimeMillis() + 100; 
    }
    
    public List<File>  ifTimerInRangeSetFileList(){
         if (time >= System.currentTimeMillis()) {
             return fileListForDropEvent;   
        }
    return new ArrayList<>();
    }
    
}