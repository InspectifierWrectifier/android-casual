/* ModeContent enumerates messages about the current mode and changes them into human readable forms. 
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
package com.casual_dev.zodui.contentpanel;

import CASUAL.instrumentation.ModeTrackerInterface;
import com.casual_dev.zodui.fonts.FontLoader;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author adamoutler
 */
public class ModeContent {

    private final static Image iADB = new Image("/com/casual_dev/zodui/images/CASUAL-adb.png");
    private final static Image iFASTBOOT = new Image("/com/casual_dev/zodui/images/CASUAL-fastboot.png");
    private final static Image iCASUAL = new Image("/com/casual_dev/zodui/images/CASUAL-casual.png");
    private final static Image iHEIMDALL = new Image("/com/casual_dev/zodui/images/CASUAL-heimdall.png");

   
    private final static javafx.scene.text.Font openEmoji = FontLoader.getOpenEmoji();
    static Text x;
    public enum emote{    
    tWAITFORDEVICE(){@Override public Text get(){return setText("💻");}},
    tFLASH(){@Override public Text get(){return setText("⚠");}},
    tADBINSTALL(){@Override public Text get(){return setText("📲");}},
    tADBPUSH(){@Override public Text get(){return setText("📥");}},
    tADBPULL(){@Override public Text get(){return setText("📤");}},
    tADBREBOOT(){@Override public Text get(){return setText("🔃");}},
    tFASTBOOTBOOTING(){@Override public Text get(){return setText("🔐");}},
    tHEIMDALLGETPARTITIONTABLE(){@Override public Text get(){return setText("💾");}},
    tHEIMDALLEXAMINEODINFILE(){@Override public Text get(){return setText("📂");}},
    tCASUALDOWNLOAD(){@Override public Text get(){return setText("📥");}},
    tCASUALEXECUTE(){@Override public Text get(){return setText("💻");}},
    tCASUALDATABRIDGEFLASH(){@Override public Text get(){return setText("⛗");}},
    tCASUALDATABRIDGEPULL(){@Override public Text get(){return setText("⛗");}},
    tCASUALFINISHEDSUCESS(){@Override public Text get(){return setText("☑");}},
    tCASUALFINISHEDFAILURE(){@Override public Text get(){return setText("☒");}},
    blank(){@Override public Text get(){return new Text("");}};

    public abstract Text get();
   
}

    final static Text t=new Text();
    private static Text setText(String value) {
        t.idProperty().set("emoticon");
        //t.setFont(openEmoji);
        t.setFont(Font.font("Droid Sans Fallback",120));
        t.setText(value);
        return t;
    }

    static Image getImage(ModeTrackerInterface.Mode mode) {
        switch (mode) {
            case ADB:
            case ADBsearching:
            case ADBpush:
            case ADBpull:
            case ADBsideload:
            case ADBwaitForDevice:
            case ADBreboot:
            case ADBshell: {
                return iADB;
            }
            case Fastboot:
            case FastbootSearching:
            case FastbootBooting:
            case FastbootFlashing: {
                return iFASTBOOT;
            }
            case Heimdall:
            case HeimdalSearching:
            case HeimdallFlash:
            case HeimdallPullPartitionTable:
            case HeimdallExaminingOdin3Package: {
                return iHEIMDALL;
            }
            default: {
                return iCASUAL;
            }
        }
    }

    static Text getText(ModeTrackerInterface.Mode mode) {
        switch (mode) {
            case ADB:
            case Fastboot:
            case CASUAL:
            case Heimdall:
                return emote.blank.get();

            case ADBsearching:
                return emote.tWAITFORDEVICE.get();
            case ADBpush:
                return emote.tADBPUSH.get();
            case ADBpull:
                return emote.tADBPULL.get();
            case ADBsideload:
                return emote.tADBINSTALL.get();
            case ADBwaitForDevice:
                return emote.tWAITFORDEVICE.get();
            case ADBreboot:
                return emote.tADBREBOOT.get();
            case ADBshell:
                return emote.tCASUALEXECUTE.get();
            case FastbootSearching:
                return emote.tWAITFORDEVICE.get();
            case FastbootBooting:
                return emote.tFASTBOOTBOOTING.get();
            case FastbootFlashing:
                return emote.tFLASH.get();
            case HeimdalSearching:
                return emote.tWAITFORDEVICE.get();
            case HeimdallFlash:
                return emote.tFLASH.get();
            case HeimdallPullPartitionTable:
                return emote.tHEIMDALLGETPARTITIONTABLE.get();
            case HeimdallExaminingOdin3Package:
                return emote.tHEIMDALLEXAMINEODINFILE.get();
            case CASUALDownload:
                return emote.tCASUALDOWNLOAD.get();
            case CASUALExecuting:
                return emote.tCASUALEXECUTE.get();
            case CASUALDataBridgeFlash:
                return emote.tCASUALDATABRIDGEFLASH.get();
            case CASUALDataBridgePull:
                return emote.tCASUALDATABRIDGEPULL.get();
            case CASUALFinishedSucess:
                return emote.tCASUALFINISHEDSUCESS.get();
            case CASUALFinishedFailure:
                return emote.tCASUALFINISHEDFAILURE.get();
            case CASUALFinished:
                return emote.tCASUALFINISHEDSUCESS.get();
            default:
                return emote.blank.get();
        }
    }
}
