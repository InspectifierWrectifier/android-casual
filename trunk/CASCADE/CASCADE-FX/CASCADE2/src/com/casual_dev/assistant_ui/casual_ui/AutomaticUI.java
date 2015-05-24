package com.casual_dev.assistant_ui.casual_ui;

/*
 * Copyright (C) 2013 adam
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



import CASUAL.CASUALMessageObject;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
/**
 *
 * @author adam
 */
public class AutomaticUI implements CASUAL.iCASUALUI {

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReady(boolean ready) {
        
    }

    @Override
    public boolean isDummyGUI() {
        return true;
    }



    @Override
    public String displayMessage(CASUALMessageObject mo) {
      return new MessageHandler().sendMessage(mo, null);
        
        
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void StartButtonActionPerformed() {
        
    }




    @Override
    public void setCASPAC(Caspac caspac) {
        
    }

    @Override
    public void setInformationScrollBorderText(String title) {
        
    }

    @Override
    public void setProgressBar(int value) {
        
    }

    @Override
    public void setProgressBarMax(int value) {
        
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
        
    }

    public void deviceDisconnected() {
        
    }

    public void deviceMultipleConnected(int numberOfDevicesConnected) {
        
    }

   
    @Override
    public void setBlocksUnzipped(String i) {
        
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
        
        return true;
    }
}
