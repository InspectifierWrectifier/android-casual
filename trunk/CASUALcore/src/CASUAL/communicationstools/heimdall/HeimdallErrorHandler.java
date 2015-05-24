/*Handles errors from heimdall. 
 * Copyright (C) 2013 adamoutler
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

package CASUAL.communicationstools.heimdall;

import CASUAL.CASUALMessageObject;
import CASUAL.CASUALScriptParser;
import CASUAL.Log;
import CASUAL.OSTools;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used for detection and reaction to errors in heimdall. 
 * @author Adam Outler adamoutler@gmail.com
 */
public class HeimdallErrorHandler {
    static final String[] errFail = {"Failed to end phone file transfer sequence!", "Failed to end modem file transfer sequence!", "Failed to confirm end of file transfer sequence!", "Failed to request dump!", "Failed to receive dump size!", "Failed to request dump part ", "Failed to receive dump part ", "Failed to send request to end dump transfer!", "Failed to receive end dump transfer verification!", "Failed to initialise file transfer!", "Failed to begin file transfer sequence!", "Failed to confirm beginning of file transfer sequence!", "Failed to send file part packet!", "Failed to request device info packet!", "Failed to initialise PIT file transfer!", "Failed to confirm transfer initialisation!", "Failed to send PIT file part information!", "Failed to confirm sending of PIT file part information!", "Failed to send file part packet!", "Failed to receive PIT file part response!", "Failed to send end PIT file transfer packet!", "Failed to confirm end of PIT file transfer!", "Failed to request receival of PIT file!", "Failed to receive PIT file size!", "Failed to request PIT file part ", "Failed to receive PIT file part ", "Failed to send request to end PIT file transfer!", "Failed to receive end PIT file transfer verification!", "Failed to download PIT file!", "Failed to send end session packet!", "Failed to receive session end confirmation!", "Failed to send reboot device packet!", "Failed to receive reboot confirmation!", "Failed to begin session!", "Failed to send file part size packet!", "Failed to complete sending of data: ", "Failed to complete sending of data!", "Failed to unpack device's PIT file!", "Failed to retrieve device description", "Failed to retrieve config descriptor", "Failed to find correct interface configuration", "Failed to read PIT file.", "Failed to open output file ", "Failed to write PIT data to output file.", "Failed to open file ", "Failed to send total bytes device info packet!", "Failed to receive device info response!", "Expected file part index: ", "Expected file part index: ", "No partition with identifier ", "Could not identify the PIT partition within the specified PIT file.", "Unexpected file part size response!", "Unexpected device info response!", "Attempted to send file to unknown destination!", "The modem file does not have an identifier!", "Incorrect packet size received - expected size = ", "does not exist in the specified PIT.", "Partition name for ", "Failed to send data: ", "Failed to send data!", "Failed to receive file part response!", "Failed to unpack received packet.", "Unexpected handshake response!", "Failed to receive handshake response."};
    static final String[] epicFailures = {"ERROR: No partition with identifier"};
    static final String[] nonErrors={"ERROR: Failed to detect compatible download-mode device."};
    
    /**
     * Checks for errors from Heimdall. 
     * @param command command which was run. 
     * @param result result from command. 
     * @return disposition of command. 
     */
    public HeimdallTools.CommandDisposition doErrorCheck(String[] command, String result){
        return errorCheckHeimdallOutput(result);
    }
        
    private void doErrorReport(String[] command, String result, HeimdallTools heimdallTools) {
        Log.level0Error("@heimdallErrorReport");
        Log.level0Error(displayArray(command));
        Log.level0Error("@heimdallErrorReport");
        Log.level0Error(result);
        Log.level0Error("@heimdallErrorReport");
        CASUALScriptParser cLang = new CASUALScriptParser();
        try {
            cLang.executeOneShotCommand("$HALT $SENDLOG");
        } catch (Exception ex) {
            Log.errorHandler(ex);
        }
    }

    /**
     * errorCheckHeimdallOutput parses console log output of Heimdall, checking 
     * for key error strings
     *
     * @param heimdallOutput CASUAL (console) log output of a Heimdall execution
     * @return is an integer representation of a CommandDisposition        
     *      0 HALTSCRIPT
     *      1 NOACTIONREQUIRED
     *      2 RUNAGAIN
     *      3 ELEVATIONREQUIRED
     *      4 INSTALLDRIVERS
     *
     * @author Jeremy Loper jrloper@gmail.com
     */
    private HeimdallTools.CommandDisposition errorCheckHeimdallOutput(String heimdallOutput) {
        if (heimdallOutput.startsWith("Usage:"))  {
            Log.level0Error("Did you intend to run a blank heimdall command without valid parameters?");
            return HeimdallTools.CommandDisposition.NOACTIONREQUIRED;
        }
        
        for (String code : HeimdallErrorHandler.epicFailures) {
            if (heimdallOutput.contains(code)) {
                Log.level0Error("ANALYSIS DETECTED ERROR:"+ code);
                return HeimdallTools.CommandDisposition.HALTSCRIPT;
            }
        }
        
        for (String code : HeimdallErrorHandler.errFail){
            if (heimdallOutput.contains(code)) {
                Log.level0Error("ANALYSIS DETECTED ERROR:"+ code);
                return HeimdallTools.CommandDisposition.HALTSCRIPT;
            }
        }
        
        if (heimdallOutput.contains("Failed to detect compatible download-mode device")) {
            Log.level0Error("ANALYSIS DETECTED ERROR: Device not in download mode or cable is borked.");
            return HeimdallTools.CommandDisposition.NOACTIONREQUIRED;
        }
        
        if (heimdallOutput.contains(" failed!")) {
            Log.level0Error("Heimdall Failure Detected");
            if (heimdallOutput.contains("Claiming interface failed!")) {
                Log.level0Error("Claiming Interface failure");
                new CASUALMessageObject(null, "@interactionRestartDownloadMode").showActionRequiredDialog();
                return HeimdallTools.CommandDisposition.RUNAGAIN;
            }
            
            if (heimdallOutput.contains("Setting up interface failed!")){
                Log.level0Error("Setting up interface failure");
                return HeimdallTools.CommandDisposition.RUNAGAIN;
            }
            
            if (heimdallOutput.contains("Protocol initialisation failed!")) {
                CASUALScriptParser cLang = new CASUALScriptParser();
                try {
                    cLang.executeOneShotCommand("$HALT $ECHO A random error occurred while attempting initial communications with the device.\nYou will need disconnect USB and pull your battery out to restart your device.\nDo the same for CASUAL.");
                } catch (Exception ex) {
                    Logger.getLogger(HeimdallErrorHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                Log.level0Error("Protocol Init failure");
                return HeimdallTools.CommandDisposition.RUNAGAIN;
            }
            if (heimdallOutput.contains("upload failed!")) {
                Log.level0Error("Upload Failure");
                return HeimdallTools.CommandDisposition.RUNAGAIN;
            }
        }
        if (heimdallOutput.contains("Flash aborted!")){
            Log.level0Error("Flash Aborted");
            return HeimdallTools.CommandDisposition.RUNAGAIN;
        }
        
        if (heimdallOutput.contains("libusb error")) {
            int startIndex = heimdallOutput.lastIndexOf("libusb error");
            if (heimdallOutput.charAt(startIndex + 1) == ':') startIndex = +3;
            while (heimdallOutput.charAt(startIndex) != '\n') {
                if (heimdallOutput.charAt(startIndex) == '-') {
                    String retVal = examineLibusbError(heimdallOutput, startIndex);
                    if(retVal.contains("LIBUSB_ERROR_NOT_SUPPORTED") && OSTools.isWindows()){
                        Log.level0Error("LIBUSB error not supported.  Installing drivers. ");
                        return HeimdallTools.CommandDisposition.INSTALLDRIVERS;
                    }//Install driver
                    else if(retVal.contains("LIBUSB_ERROR_ACCESS") && OSTools.isLinux()){
                        Log.level0Error("permissions elevation required");
                        return HeimdallTools.CommandDisposition.ELEVATIONREQUIRED;
                    }//Elevate Heimdall Command
                    else if(retVal.contains("LIBUSB_ERROR_OTHER")) {
                        Log.level0Error("Random LIBUSB error detected. ");
                        return HeimdallTools.CommandDisposition.RUNAGAIN;
                    }//Other libUSB Error, Halt
                    else return HeimdallTools.CommandDisposition.RUNAGAIN;//Hit me baby, one more time
                }
                startIndex++;
            }
        }
        return HeimdallTools.CommandDisposition.NOACTIONREQUIRED;
    }

    /**
     * examineLibusbError parses console log output of Heimdall, checking 
     * for key error strings
     * 
     * @param heimdallOutput CASUAL (console) log output of a Heimdall execution
     * @param startIndex Integer representing a String index position of a libUSB
     *                   error number
     * @return is a String representation of the libUSB error   
     */
    private String examineLibusbError(String heimdallOutput, int startIndex) {
        switch (heimdallOutput.charAt(startIndex + 1)) {
            case '1':
                switch (heimdallOutput.charAt(startIndex + 2)) {
                    case '0': return "LIBUSB_ERROR_INTERRUPTED";// -10
                    case '1': return "LIBUSB_ERROR_NO_MEM";// -11
                    case '2': return "LIBUSB_ERROR_NOT_SUPPORTED";// -12
                    default:  return "LIBUSB_ERROR_IO";// -1
                }
            case '2': return "LIBUSB_ERROR_INVALID_PARAM";// -2
            case '3': return "LIBUSB_ERROR_ACCESS";// -3
            case '4': return "LIBUSB_ERROR_NO_DEVICE";// -4
            case '5': return "LIBUSB_ERROR_NOT_FOUND";// -5
            case '6': return "LIBUSB_ERROR_BUSY";// -6
            case '7': return "LIBUSB_ERROR_TIMEOUT";// -7
            case '8': return "LIBUSB_ERROR_OVERFLOW";// -8
            case '9': if (heimdallOutput.charAt(startIndex + 2) == 9){
                          return "LIBUSB_ERROR_OTHER";
                      }// -99
                      else return "LIBUSB_ERROR_PIPE";//-9
            default:  return "LIBUSB_ERROR_OTHER";//??
        }
    }

    String displayArray(String[] command) {
        StringBuilder sb = new StringBuilder();
        for (String cmd : command) {
            sb.append("\"").append(cmd).append("\" ");
        }
        return sb.toString();
    }
    
}
