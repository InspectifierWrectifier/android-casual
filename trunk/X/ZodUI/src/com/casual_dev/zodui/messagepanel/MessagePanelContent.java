/*Interperates a CASUALMessageObject into a ZODUI message object for MessagePanel.
 *Copyright (C) 2014 CASUAL-Dev or Adam Outler
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

package com.casual_dev.zodui.messagepanel;

import CASUAL.CASUALMessageObject;
import CASUAL.iCASUALUI;
import javafx.scene.image.Image;

/**
 * Interperates a CASUALMessageObject into a ZODUI message object for MessagePanel.
 * @author adamoutler
 */
public class MessagePanelContent{
    
    CASUALMessageObject cmo;

    /**
     * sets the CASUALMessageObject used for this class
     * @param cmo
     */
    public MessagePanelContent(CASUALMessageObject cmo){
        this.cmo=cmo;
    }

    /**
     * gets the message requested
     * @return string containing message
     */
    public String getMessage(){
        return cmo.messageText.replace("\\n", "\n");
    }
    
    /**
     * gets the title of the message
     * @return string containing title
     */
    public String getTitle(){
        return cmo.title;
    }
    /**
     * if the message requires a text return
     * @return true if text box is needed
     */
    public boolean getTextInputRequired(){
        return cmo.category.equals(iCASUALUI.MessageCategory.TEXTINPUT);
    }
    

    /**
     * Image corresponding to messageCategory
     * @return image for display to user
     */
    public Image getImage(){
         switch (cmo.category) {
            case ACTIONREQUIRED: {
                return new Image("../images/CASUALDude.png");
            }
            case COMMANDNOTIFICATION: {
                return new Image("../images/CASUALDude.png");
            }
            case TEXTINPUT: {
                return new Image("../images/CASUALDude.png");
            }
            case SHOWERROR: {
                return new Image("../images/CASUALDude.png");
            }
            case SHOWINFORMATION: {
                return new Image("../images/CASUALDude.png");
            }
            case SHOWYESNO: {
                return new Image("../images/CASUALDude.png");
            }
            case TIMEOUT: {
                return new Image("../images/CASUALDude.png");
            }
            case USERCANCELOPTION: {
                return new Image("../images/CASUALDude.png");
            }
            case USERNOTIFICATION: {
                return new Image("../images/CASUALDude.png");
            }
            default:
                return new Image("../images/CASUALDude.png");
        }
    }
    
    /**
     * text for use in buttons
     * @return string array of button options
     */
    public String[] getActionOptions(){
        return getButtonOptions();
    }
    /**
     * number of buttons required
     * @return integer representing the number of buttons
     */
    public int getNumberOfButtons(){
    return getButtonOptions().length;
    }
    
    
    private String[] getButtonOptions(){
     switch (cmo.category) {
            case ACTIONREQUIRED: {
                return new String[]{"I did it", "I didn' do it"};

            }
            case COMMANDNOTIFICATION: {
                return new String[]{"OK"};
            }
            case TEXTINPUT: {
                return new String[]{"OK", "Cancel"};
            }
            case SHOWERROR: {
                return new String[]{"OK"};
            }
            case SHOWINFORMATION: {
                return new String[]{"OK"};
            }
            case SHOWYESNO: {
                return new String[]{"Yes", "no"};
            }
            case TIMEOUT: {
                return new String[]{"Ok"};
            }
            case USERCANCELOPTION: {
                return new String[]{"OK", "Stop"};
            }
            case USERNOTIFICATION: {
                return new String[]{"OK"};
            }
            default:
                return new String[]{"OK"};
        }
    }
    
    
    
}
