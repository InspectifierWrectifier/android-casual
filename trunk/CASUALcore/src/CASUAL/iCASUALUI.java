/*iCASUALUI provides an interface for a main GUI in CASUAL
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

import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;

/**
 * provides an interface for a main UI in CASUAL. Provides all User Interfaces
 * and methods which provide a means of handling Message objects and UI. The
 * intent is to abstract the CASUAL Messaging System using this class so a
 * developer may implement their own GUI using any means they like. This class
 * provides notifications which will halt the progress of the script and present
 * information to the user. This allows the user to interact with CASUAL.
 *
 * In order to change the Messaging API for CASUAL, there is a static reference
 * which is located in CASUAL.resources.CASUALApp.properties the property name
 * change required is Application.interactions. This should match the desired
 * class which can handle CASUALMessageObjects
 *
 * It is recommended that any class implementing iCASUALUI handle the following
 * items at a minimum: 1. CASUALMessageObject.messageText 2. *
 * CASUALMessageObject.title
 *
 * The return value for any CASUAL Message Object aside from those which execute
 * commmands or specifically state they return string results: 0- yes, ok,
 * continue 1- no, cancel, stop
 *
 *
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public interface iCASUALUI {

    /**
     * Categories of possible messages.
     */
    public enum MessageCategory {

        /**
         * TIMEOUT message is displayed to user.
         */
        TIMEOUT,
        /**
         * An important message to be displayed.
         */
        ACTIONREQUIRED,
        /**
         * User is given the option to continue or cancel.
         */
        USERCANCELOPTION,
        /**
         * Notification is displayed.
         */
        USERNOTIFICATION,
        /**
         * Information is displayed.
         */
        SHOWINFORMATION,
        /**
         * Error message is displayed.
         */
        SHOWERROR,
        /**
         * Information with yes/no buttons is displayed.
         */
        SHOWYESNO,
        /**
         * Information with a string return is displayed.
         */
        TEXTINPUT,
        /**
         * A command is run, and the return is displayed.
         */
        COMMANDNOTIFICATION
    }

    /**
     * returns true if the UI is ready.
     *
     * @return true if ui is ready
     */
    public boolean isReady();

    /**
     * provides a setter for UI. Normally unused, but this is for test purposes.
     *
     * @param ready GUI has all parts established and is ready for operations.
     */
    public void setReady(boolean ready);

    /**
     * returns true if the UI is a dummy UI. Dummy UI is used for testing and
     * for running CASPACs with a UI from the command line. This is useful for
     * situations when you want to run an automated UI on a loop.
     *
     * @return true if dummy ui.
     */
    public boolean isDummyGUI();


    /**
     * MessageType used by CASUALMessageObject and any class implementing this
     * interface. Type 0 is a non-critical time-based message object which will
     * time out and dismiss itself. returns 0
     */
    final int INTERACTION_TIME_OUT = 0;

    /**
     * MessageType used by CASUALMessageObject and any class implementing this
     * interface. Type 1 is a message object which mandates the user do
     * something in order to advance the process. User is assinged a task and
     * given two buttons "I did it" and "I didn't do it". returns 0 if user
     * completed task. 1 if the user failed to do it.
     */
    final int INTERACTION_ACTION_REUIRED = 1;

    /**
     * MessageType used by CASUALMessageObject and any class implementing this
     * interface. Type 2 requests the user's permission to continue and gives
     * the option to halt the active script returns 1 if user wishes to cancel
     */
    final int INTERACTION_USER_CANCEL_OPTION = 2;

    /**
     * MessageType used by CASUALMessageObject and any class implementing this
     * interface. Type 3 is a general purpose notification which displays
     * information and halts the flow of the script until dismissed. returns 0.
     */
    final int INTERACTION_USER_NOTIFICATION = 3;

    /**
     * MessageType used by CASUALMessageObject and any class implementing this
     * interface. Type 4 is similar to type 3 in that it displays a notification
     * and halts the flow, but a type 3 will change the user interaction type
     * from notification to information style. returns 0.
     */
    final int INTERACTION_SHOW_INFORMATION = 4;

    /**
     * MessageType used by CASUALMessageObject and any class implementing this
     * interface. Type 5 is intended to halt the flow of operation and show an
     * error notification. returns 0
     */
    final int INTERACTION_SHOW_ERROR = 5;

    /**
     * MessageType used by CASUALMessageObject and any class implementing this
     * interface. Halts all script operations and shows the user a Yes/No
     * diaLog. returns 0 if yes and 1 if no.
     */
    final int INTERACTION_SHOW_YES_NO = 6;

    /**
     * MessageType used by CASUALMessageObject and any class implementing this
     * interface. Requests input from the user in the form of text. The return
     * from this type of dialog will be a string value representing user-entered
     * text. returns 0.
     */
    final int INTERACTION_INPUT_DIALOG = 7;

    /**
     * MessageType used by CASUALMessageObject and any class implementing this
     * interface. A command notification will run a CASUAL command and return
     * the results in the CASUALMessageObject.messageText variable. The results
     * should be displayed to the user.
     *
     * returns 0.
     */
    final int INTERACTION_COMMAND_NOTIFICATION = 8;

    /**
     * Takes a message object and displays to user. To properly implement this
     * class the displayMessage should, at a minimum handle both
     * CASUALMessageObject.title and CASUALMessageObject.messageText.
     *
     * @param messageObject defined by CASUAL
     * @return string value which must be interpereted
     */
    String displayMessage(CASUALMessageObject messageObject);

    /**
     * disposes the current window. should be used to terminate application.
     * This method can be used to call CASUALMain.Shutdown for proper shutdown.
     * This should contain "Statics.GUI=null;" for faster shutdown. This method
     * can contain System.exit at the end.
     */
    void dispose();

    /**
     * the start button was pressed.
     */
    void StartButtonActionPerformed();

    /**
     * sets controls status
     *
     * @param status commanded value
     * @param numberOfDevicesConnected is the number of connected devices.
     * @param mode string value representing mode of checking for devices.
     * @return true if successful
     */
    boolean setControlStatus(boolean status, int numberOfDevicesConnected, String mode);

    /**
     * Sets a reference to the current CASPAC so information can be displayed
     *
     * @param caspac caspac to reference
     */
    void setCASPAC(Caspac caspac);

    /**
     * Sets the current status of the window.
     *
     * @param title current status
     */
    void setInformationScrollBorderText(String title);

    /**
     * sets the progress bar value.
     *
     * @param value value for progress bar
     */
    void setProgressBar(int value);

    /**
     * sets max value for progress bar
     *
     * @param value maximum
     */
    void setProgressBarMax(int value);

    /**
     * Sets the active script for the window
     *
     * @param s script which is now active
     */
    void setScript(Script s);

    /**
     * sets "do it!" button text
     *
     * @param text text for main execution button
     */
    void setStartButtonText(String text);

    /**
     * This message provides a topical overview of what is happening to the
     * user. It should be used sparingly... eg.. pushing files, flashing
     * devices...
     *
     * @param text title to set
     */
    void setUserMainMessage(String text);

    /**
     * sets the message label subtext. This message only provides detail to the
     * user as to the process which is happening and may be used at any time to
     * update sub-text in the UI.
     *
     * @param text label text
     */
    void setUserSubMessage(String text);

    /**
     * sets window banner image
     *
     * @param icon image to display
     * @param text text if image cannot be displayed
     */
    /**
     * sets the main window banner text if an image is not used
     *
     * @param text text to display as banner
     */
    void setWindowBannerText(String text);

    /**
     * sets the window visibility
     *
     * @param b true if visibility is commanded
     */
    public void setVisible(boolean b);

    /**
     * used during unzip as a progress indicator
     *
     * @param value number of blocks unzipped.
     */
    public void setBlocksUnzipped(String value);

    /**
     * sends a message to the user console
     *
     * @param string String to send
     */
    public void sendString(String string);

    /**
     * sends a bit of data at a time, such as a character from a terminal
     * command. b should backspace. \r should erase line.
     *
     * @param data data to be sent to UI
     */
    public void sendProgress(String data);

}
