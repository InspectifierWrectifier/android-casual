/*CASUALInteraction is the user interface class
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
package CASUAL;

import CASUAL.instrumentation.Instrumentation;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * CASUALInteraction is the place where all User Interactions are initiated.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALMessageObject implements Serializable {

    private static final long serialVersionUID = 1029319L;


    /**
     * The message sent into the CASUALMessageObject. Original message includes
     * Title and Message delimited by the first comma or &gt;&gt;&gt;.
     */
    public String originalMessage = ""; //for use with translations

    /**
     * Return values expected (for use as a guide only). The expected returns
     * for all are 0 for acknowledged, ok, yes or any other positive. 1 is for
     * cancel or no.
     */
    public String expectedReturn = "";

    /**
     * The title of the message.
     */
    public String title;

    /**
     * The text of the message.
     */
    public String messageText;

    /**
     * category of this message. The category should be used instead of the old
     * final static variables.
     */
    public iCASUALUI.MessageCategory category;
    /**
     * Type of message commanded by this MessageObject
     * iCASUALInteraction.INTERACTION_TIME_OUT=0;
     * iCASUALInteraction.INTERACTION_USER_CANCEL_OPTION=1;
     * iCASUALInteraction.INTERACTION_ACTION_REUIRED=2;
     * iCASUALInteraction.INTERACTION_USER_NOTIFICATION=3;
     * iCASUALInteraction.INTERACTION_SHOW_INFORMATION=4;
     * iCASUALInteraction.INTERACTION_SHOW_ERROR=5;
     * iCASUALInteraction.INTERACTION_SHOW_YES_NO=6;
     * iCASUALInteraction.INTERACTION_INPUT_DIALOG=7;
     * iCASUALInteraction.INTERACTION_COMMAND_NOTIFICATION=8;    /**
     * Type of message commanded by this MessageObject
     * iCASUALInteraction.INTERACTION_TIME_OUT=0;
     * iCASUALInteraction.INTERACTION_USER_CANCEL_OPTION=1;
     * iCASUALInteraction.INTERACTION_ACTION_REUIRED=2;
     * iCASUALInteraction.INTERACTION_USER_NOTIFICATION=3;
     * iCASUALInteraction.INTERACTION_SHOW_INFORMATION=4;
     * iCASUALInteraction.INTERACTION_SHOW_ERROR=5;
     * iCASUALInteraction.INTERACTION_SHOW_YES_NO=6;
     * iCASUALInteraction.INTERACTION_INPUT_DIALOG=7;
     * iCASUALInteraction.INTERACTION_COMMAND_NOTIFICATION=8;
     */

    /**
     * Used by jOptionPane for TIMEOUTMessages only. Made available for use
     * under other APIs so that it may be changed if needed.
     *
     * @see javax.swing.JOptionPane
     */
    public int timeoutOptionType;

    /**
     * Used by TIMEOUTMessages only. Specifies the default value for timeout
     * upon timeout.
     */
    public Object timeoutInitialValue;

    /**
     * Used by TIMEOUTMessages only. Specifies the options for the
     * TIMEOUTMessage
     */
    public Object[] timeoutOptions;

    /**
     * Used by TIMEOUTMessages only. Specifies the amount of time the message
     * should be displayed before timing out and returning the default value
     */
    public int timeoutPresetTime;

    /**
     * Used by jOptionPane for TIMEOUTMessages only. Made available for use
     * under other APIs so that it may be changed if needed.
     *
     * @see javax.swing.JOptionPane
     */
    public int timeoutMessageType;

    /**
     * instantiates an interaction
     *
     * @param messageInput can be title,message or title&gt;&gt;&gt;message, or
     * just message and title will be automatically chosen
     */
    public CASUALMessageObject(String messageInput) {
        iCASUALUI gui=CASUALSessionData.getGUI();
        if (messageInput.startsWith("@")) {
            String translation = Translations.get(messageInput);
            if (translation.contains(">>>")) {
                originalMessage = messageInput;
                String[] s = translation.split(">>>", 2);
                //messageText=s[1].replace("\n","\\n");
                title = s[0];
                messageText = s[1];
            } else {
                title = null;
                messageText = translation;
            }
        } else {
            if (messageInput.contains(">>>")) {
                originalMessage = messageInput;
                String[] s = messageInput.split(">>>", 2);
                //messageText=s[1].replace("\n","\\n");
                title = s[0];
                messageText = s[1];
            } else {
                this.title = null;
                this.messageText = messageInput;
            }
        }
        Instrumentation.updateStatus("-New Message:" + title + " " + messageText);
    }

    /**
     * instantiates a CASUALInteraction
     *
     * @param title title to display on interaction
     * @param messageInput message to display on interaction
     */
    public CASUALMessageObject(String title, String messageInput) {
        this.title = title;
        this.messageText = messageInput;
    }

    /**
     * CASUALInteraction input device
     */
    /**
     * shows a TIMEOUTDialog
     *
     * @param PRESET_TIME time to show message
     * @param parentComponent where to hover over
     * @param optionType jOptionPane.OPTION_
     * @param timeOutMessageType jOptionPane.MESSAGETYPE
     * @param options array of options
     * @param initialValue value to choose if none other are chosen
     * @return value chosen 0 for first, 1 for second...
     */
    synchronized public int showTimeoutDialog(final int PRESET_TIME, Component parentComponent, int optionType, int timeOutMessageType, Object[] options, final Object initialValue) {
        this.timeoutOptionType = optionType;
        this.timeoutMessageType = timeOutMessageType;
        this.timeoutOptions = options;
        this.timeoutInitialValue = initialValue;
        this.timeoutPresetTime = PRESET_TIME;
        setType(iCASUALUI.MessageCategory.TIMEOUT);
        expectedReturn = "Type any whole number as a response and Press Enter " + Arrays.asList(options).toString();
        if (CASUALSessionData.getGUI() == null) {
            return 0;
        }
        return Integer.parseInt(CASUALSessionData.getGUI().displayMessage(this));
    }

    /**
     * shows an input dialog
     *
     * @return value from user input
     * @throws HeadlessException when no GUI is present
     */
    public String inputDialog() throws HeadlessException {
        setType(iCASUALUI.MessageCategory.TEXTINPUT);
        expectedReturn = "Type your response and Press Enter";
        return CASUALSessionData.getGUI().displayMessage(this);
    }

    /**
     * shows action required dialog
     *
     * @return 1 if user didn't do it, or 0 if user did it.
     * @throws HeadlessException when no GUI is present
     */
    public int showActionRequiredDialog() throws HeadlessException {
        setType(iCASUALUI.MessageCategory.ACTIONREQUIRED);
        expectedReturn = "String 0-continue, 1-stop";
        return Integer.parseInt(CASUALSessionData.getGUI().displayMessage(this));
    }

    /**
     * displays user cancel option
     *
     * @return 1 if cancel was requested
     */
    public int showUserCancelOption() {
        setType(iCASUALUI.MessageCategory.USERCANCELOPTION);
        expectedReturn = "String 0-continue, 1-stop";
        return Integer.parseInt(CASUALSessionData.getGUI().displayMessage(this));
    }

    /**
     * displays command notification
     *
     * @throws HeadlessException when no GUI is present
     */
    public void showCommandNotification() throws HeadlessException {
        setType(iCASUALUI.MessageCategory.COMMANDNOTIFICATION);
        expectedReturn = "Press Enter";
       CASUALSessionData.getGUI().displayMessage(this);
    }

    /**
     * displays user notification
     *
     * @throws HeadlessException when no GUI is present
     */
    public void showUserNotification() throws HeadlessException {
        setType(iCASUALUI.MessageCategory.USERNOTIFICATION);
        expectedReturn = "Press Enter";
        CASUALSessionData.getGUI().displayMessage(this);
    }

    /**
     * displays information message
     *
     * @throws HeadlessException when no GUI is present
     */
    public void showInformationMessage() throws HeadlessException {
        setType(iCASUALUI.MessageCategory.SHOWINFORMATION);
        expectedReturn = "Press Enter";
        Log.level3Verbose("showing information message object");
        CASUALSessionData.getGUI().displayMessage(this);
        Log.level3Verbose("Done with message object");
    }

    /**
     * displays error message
     *
     * @throws HeadlessException when no GUI is present
     */
    public void showErrorDialog() throws HeadlessException {
        setType(iCASUALUI.MessageCategory.SHOWERROR);
        expectedReturn = "Press Enter";
        iCASUALUI ui = CASUALSessionData.getGUI();
        CASUALSessionData.getGUI().displayMessage(this);

    }

    /**
     * displays a Yes/No dialog
     *
     * @return true if yes, false if no
     */
    public boolean showYesNoOption() {
        setType(iCASUALUI.MessageCategory.SHOWYESNO);
        Boolean retval = CASUALSessionData.getGUI().displayMessage(this).equals("0");
        expectedReturn = "Press 0-yes, 1-no then Press Enter";
        return retval;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String n = "\n";
        sb.append("[").append(this.category).append("] Title:").append(this.title).append(n);
        sb.append("Message:").append(this.messageText.replace("\n", "\\\\n")).append(n);
        sb.append("press: ").append(this.expectedReturn).append(n);
        return sb.toString();
    }

    private int setType(iCASUALUI.MessageCategory cat) {
        this.category = cat;
        return getMessageType();
    }

    /**
     * gets message type
     *
     * @return the message type from MessageCategory.TIMEOUT
     */
    public int getMessageType() {
        return category.compareTo(iCASUALUI.MessageCategory.TIMEOUT);
    }
}
