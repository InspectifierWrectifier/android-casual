/*Provides storage of content for ZodPanels
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
package com.casual_dev.zodui.contentpanel;

import CASUAL.instrumentation.ModeTrackerInterface.Mode;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.text.Text;

/**
 * Provides storage of content for ZodPanels
 *
 * @author adamoutler
 */
public class ZodPanelContent{

    private String mainTitle = "Lorem ipsum dolor sit amet adfasfd asfdasfdasfdas";
    private String subTitle = "Lorem ipsum dolor sit amet";
    private boolean logClosed = true;

    private static double progressIndicatorMax = 100.0;
    private static double progressIndicator = 12.0;
    private String image = "@images/DoingSomething.png";

    private String[] buttonActions = new String[]{"OK"};
    private boolean inputRequired = false;
    private String status = "ready";
    private String  log="";
    private static Text logText=new Text("");

    public ZodPanelContent(){
        
    }

    /**
     * constructor for use with existing zod panel content object
     *
     * @param zpc
     */
    public ZodPanelContent(ZodPanelContent zpc) {
        mainTitle = zpc.mainTitle;
        subTitle = zpc.subTitle;
        logClosed = zpc.logClosed;
        image = zpc.image;
        buttonActions = zpc.buttonActions;
        inputRequired = zpc.inputRequired;
        status = zpc.status;
        log=zpc.log;
    }

  public String toString(){
      StringBuilder sb=new StringBuilder();
      sb.append("Title:").append(mainTitle);
      sb.append("subtitle:").append(subTitle).append("\n");
      sb.append("log Closed:").append(logClosed).append("\n");
      sb.append("image:").append(image).append("\n");
      sb.append("button actions:").append(buttonActions).append("\n");
      sb.append("inputRequired:").append(inputRequired).append("\n");
      sb.append("status:").append(status).append("\n");
      sb.append("logClosed:").append(logClosed).append("\n");
      
      return sb.toString();
  }
    /**
     * gets main title
     *
     * @return main title
     */
    public String getMainTitle() {
        return this.mainTitle;
    }

    /**
     * sets main title
     *
     * @param title
     */
    public void setMainTitle(String title) {
        this.mainTitle = title;
    }

    /**
     * gets subtitle
     *
     * @return subtitle
     */
    public String getSubtitle() {
        return this.subTitle;
    }

    /**
     * sets subtitle
     *
     * @param message
     */
    public void setSubtitle(String message) {
        this.subTitle = message;
    }

    /**
     * gets the progress indicator
     *
     * @return the current progress
     */
    public static double getProgress() {
        return progressIndicator;
    }

    /**
     *
     * @return  the max progress on the bar
     */
    public static double getProgressMax() {
        return progressIndicatorMax;
    }

    /**
     *
     * @param progress
     */
    public static void setProgress(double progress) {
        progressIndicator = progress;
    }

    /**
     *
     * @param max
     */
    public static void setProgressMax(double max) {
        progressIndicatorMax = max;
    }

    /**
     *
     * @return resource location
     */
    public String getImageString() {
        
        return this.image;
    }
    
    /**
     *
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     *
     * @param Options
     */
    public void setActionOptions(String[] Options) {
        buttonActions = Options;
    }

    /**
     *
     * @return  titles of each button
     */
    public String[] getActionOptions() {
        return buttonActions;
    }

    /**
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return current status
     */
    public String getStatus() {
        return status;
    }
    

    /**
     * @return the logArea
     */
    public Text getLogText() {
        return logText;
    }

    /**
     * @param logArea the logArea to set
     */
    public void appendLogAreaText(String logArea) {
        StringProperty sp=logText.textProperty();
        sp.setValue(sp.get()+logArea);
    }    
    
    public Text getLogAreaText(){
        return logText;
    }

    /**
     * @return the log
     */
    public String getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(String log) {
        this.log = log;
    }


    

 
}
