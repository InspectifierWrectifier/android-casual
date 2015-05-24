/* Mode tracker logs the modes used in CASUAL
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

package CASUAL.instrumentation;

/**
 * Used for mode logging, can be overridden for use in other projects
 * @author adamoutler
 */
public interface ModeTrackerInterface {

    /**
     * Modes used by setMode().
     */
    public static enum Mode {
        ADB, 
        ADBsearching, //CASUAL mode for ADB search
        ADBpush,  //adb push
        ADBpull,  //adb pull
        ADBsideload,//adb install
        ADBwaitForDevice,//adb wait-for-device
        ADBreboot,// adb reboot
        ADBshell,// adb shell
        
        Fastboot,
        FastbootSearching, //CASUAL mode for ADB search
        FastbootBooting, //fastboot boot
        FastbootFlashing, //fastboot flash
        
        Heimdall,
        HeimdalSearching, //CASUAL mode for ADB search
        HeimdallFlash, //heimdall flash
        HeimdallPullPartitionTable, //heimdall print-pit/ heimdall download-pit
        HeimdallExaminingOdin3Package, //CASUAL Mode for examining odin package
        
        CASUAL,
        CASUALDownload, //CASUAL is downloading something from the internet
        CASUALExecuting, //A CASUAL Script or other generic execution activity is processing
        CASUALDataBridgeFlash, //CASUAL Data Bridge firmware flash
        CASUALDataBridgePull,  //CASUAL Data Bridge firmware pull
        CASUALFinishedSucess,  //CASUAL finished sucessfully
        CASUALFinishedFailure,  //CASUAL finished with a failure
        CASUALFinished  //CASUAL just finished. Nothing special here.
        
    };
    
    /**
     * logs the current CASUAL mode.
     * Override this for things like images
     * @param mode mode desired
     */
    public void setMode(Mode mode);

}
