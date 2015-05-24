/*
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

package GUI.CommandLine;

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.CASUALSessionData;
import CASUAL.Log;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import CASUAL.iCASUALUI;
import static CASUAL.iCASUALUI.INTERACTION_ACTION_REUIRED;
import static CASUAL.iCASUALUI.INTERACTION_COMMAND_NOTIFICATION;
import static CASUAL.iCASUALUI.INTERACTION_INPUT_DIALOG;
import static CASUAL.iCASUALUI.INTERACTION_SHOW_ERROR;
import static CASUAL.iCASUALUI.INTERACTION_SHOW_INFORMATION;
import static CASUAL.iCASUALUI.INTERACTION_SHOW_YES_NO;
import static CASUAL.iCASUALUI.INTERACTION_TIME_OUT;
import static CASUAL.iCASUALUI.INTERACTION_USER_CANCEL_OPTION;
import static CASUAL.iCASUALUI.INTERACTION_USER_NOTIFICATION;
import java.awt.HeadlessException;
import java.io.IOException;


/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CommandLineUI implements iCASUALUI {

    private int progressMax=0;
    private void msg(String msg){
        Log.level3Verbose("[UI]"+msg);
    }
    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReady(boolean ready) {
        msg("ready requested");
    }

    @Override
    public boolean isDummyGUI() {
        
        msg("is dummy gui requested");
        return false;
    }


   @Override
    public String displayMessage(CASUALMessageObject messageObject) {
        int messageType = messageObject.getMessageType();
        String title = messageObject.title;
        String messageText = messageObject.messageText;
        String retval = "";
        Log.Level1Interaction(messageObject.toString());
        switch (messageType) {
            case INTERACTION_TIME_OUT:
                return showTimeOutInteraction(messageObject, messageText, title);
            case INTERACTION_ACTION_REUIRED:
                return showActionRequiredInteraction(messageText, title);
            //break;// unreachable

            case INTERACTION_USER_CANCEL_OPTION:
                return showUserCancelOptionInteraction(title, messageText);               //break; unreachable

            case INTERACTION_USER_NOTIFICATION:
                showUserNotificationInteraction(title, messageText);
                break;

            case INTERACTION_SHOW_INFORMATION:
                showInformationInteraction(messageText, title);
                break;

            case INTERACTION_SHOW_ERROR:
                showErrorInteraction(messageText, title);
                break;

            case INTERACTION_SHOW_YES_NO:
                return showYesNoInteraction(title, messageText);
            //break; unreachable

            case INTERACTION_INPUT_DIALOG:
                return showInputDialog(title, messageText);
            //break; unreachable

            case INTERACTION_COMMAND_NOTIFICATION:
                showUserNotificationInteraction(title, messageText);
                return messageText;
        }
        return retval;
    }

    /**
     * grabs input from CASUALSessionData.getInstance().in (usually stdin).
     * @return string value containing user input truncated by enter key.
     */
    public String getCommandLineInput() {
        try {
            Log.out.flush();
            String s =CASUALMain.getSession().in.readLine();
            if (s == null) {
                while (s == null) {
                    s = CASUALMain.getSession().in.readLine();
                }
            }
            return s;
        } catch (IOException ex) {
            Log.errorHandler(ex);
            return "";
        }
    }

    private void waitForStandardInputBeforeContinuing() {
        getCommandLineInput();
    }

    private String showTimeOutInteraction(CASUALMessageObject messageObject, String messageText, String title) {
            String s = getCommandLineInput();
            if (s == null || s.isEmpty()) {
                return "0";
            }
            return "1";
    }

    private String showActionRequiredInteraction(String messageText, String title) throws HeadlessException {
        String retval;
        int n = 9999;

            while (n != 0 && n != 1) {
                retval = getCommandLineInput();
                if (!retval.equals("q") && !retval.equals("Q") && !retval.isEmpty()) {
                    n = new CASUALMessageObject(messageText).showActionRequiredDialog();
                } else if (retval.equals("Q") || retval.equals("q")) {
                    n = 1;
                } else {
                    n = 0;
                }
            }
        return Integer.toString(n);
        //break;// unreachable
    }

    private String showUserCancelOptionInteraction(String title, String messageText) throws HeadlessException {
        int cancelReturn;
            String s = this.getCommandLineInput();
            if (s.equals("q") || s.equals("Q")) {
                cancelReturn = 1;
            } else {
                cancelReturn = 0;
            }
        return Integer.toString(cancelReturn);
        //break; unreachable
    }

    private void showUserNotificationInteraction(String title, String messageText) throws HeadlessException {

            waitForStandardInputBeforeContinuing();
    }

    private void showInformationInteraction(String messageText, String title) throws HeadlessException {

            waitForStandardInputBeforeContinuing();
    }

    private void showErrorInteraction(String messageText, String title) throws HeadlessException {
            waitForStandardInputBeforeContinuing();

    }

    private String showYesNoInteraction(String title, String messageText) throws HeadlessException {

            //display the messageText
            String s = this.getCommandLineInput();
            if (s.equals("n") || s.equals("N")) {
                return "false";
            } else {
                return "true";
            }
     
    }

    private String showInputDialog(String title, String messageText) throws HeadlessException {
        messageText = "<html>" + messageText.replace("\\n", "\n");
        
            return getCommandLineInput();
      
        //break; unreachable
    }

    @Override
    public void dispose() {
       msg("Dispose Commanded");
    }

    @Override
    public void StartButtonActionPerformed() {
    }

 

    @Override
    public boolean setControlStatus(boolean status,int number, String mode) {
        switch (number){
            case 0: this.deviceDisconnected();
                break;
            case 1: this.deviceConnected(mode);
                break;
            default: this.deviceMultipleConnected(number);
                break;
        }
        
        msg("control status requested:"+status);
        return true;
    }

    public boolean getControlStatus() {
          return true;
    }

    @Override
    public void setCASPAC(Caspac caspac) {
        msg("Setting caspac"+caspac);
        CASUALMain.getSession().CASPAC=caspac;
    }

    @Override
    public void setInformationScrollBorderText(String title) {

        msg("boarder title change requested:"+title);
    }

    @Override
    public void setProgressBar(int value) {
        msg("Progress percent:"+value);
        
    }

    @Override
    public void setProgressBarMax(int value) {
       msg("Progress bar max"+value);
       progressMax=value;
    }
    
    @Override
    public void setScript(Script s) {
    }

    @Override
    public void setStartButtonText(String text) {
    }


    @Override
    public void setUserSubMessage(String text) {
    }


    @Override
    public void setWindowBannerText(String text) {
    }

    @Override
    public void setVisible(boolean b) {
    }

    public void deviceConnected(String mode) {
       msg ("Device connected");
      
    }

    public void deviceDisconnected() {
    }

    public void deviceMultipleConnected(int numberOfDevicesConnected) {
    }

   
    public void setThisAsGUI(){
        CASUALSessionData.setGUI(this);
    }

    @Override
    public void setBlocksUnzipped(String blocks) {
        msg("Progress percent:"+blocks);

    }

    @Override
    public void sendString(String string) {
    }

    @Override
    public void sendProgress(String data) {
    }

    @Override
    public void setUserMainMessage(String text) {
    }
}
