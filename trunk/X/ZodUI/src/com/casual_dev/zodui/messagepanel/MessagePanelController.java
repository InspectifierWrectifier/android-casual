/*Handles Modal Messages in CASUAL ZodUI.
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


import com.casual_dev.zodui.CASUALZodMainUI;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 *Handles Modal Messages in CASUAL ZodUI.
 * @author adamoutler
 */
public class MessagePanelController
        implements Initializable {


    @FXML
    Button button0;
    @FXML
    Button button1;
    @FXML
    Button button2;
    @FXML
    TextArea messageText;
    @FXML
    TextArea titleText;
    @FXML
    TextField textInput;
    @FXML
    ImageView picture;
    CASUALZodMainUI mainPanel;
    final Object buttonWaitLock = new Object();
    int buttonReturn = -1;
    @FXML
    AnchorPane topLevel;
    long creationTime = 0;
    long finishTime = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        creationTime = new Date().getTime();
    }

    /**
     * Creates a message from a PessagePanelContent
     * @param mpc 
     */
    public void createMessage(MessagePanelContent mpc) {
        createMessage(new String[]{mpc.getTitle(), mpc.getMessage()}, mpc.getActionOptions(), mpc.getTextInputRequired());
    }

    /**
     * Creates a message from bare essentials
     * @param message [0]=title [1]=message
     * @param buttonText an array of text to be displayed on buttons
     * @param useTextBox true if message requires return of text and not just 0,1,2 of strings
     */
    @FXML
    public void createMessage(String[] message, String[] buttonText, boolean useTextBox) {
        Platform.runLater(() -> {
            if (useTextBox) {
                this.textInput.setVisible(true);
            } else {
                this.textInput.setVisible(false);
            }
            this.buttonReturn = -1;
            this.button0.setVisible(true);
            button1.setVisible(false);
            button2.setVisible(false);
            if (buttonText.length > 0) {
                    this.button0.setText(buttonText[0]);
            }
            if (buttonText.length >= 2) {
                this.button1.setText(buttonText[1]);
                this.button1.setVisible(true);
            }
            if (buttonText.length >= 3) {
                this.button2.setText(buttonText[2]);
                this.button2.setVisible(true);
            } 
            this.titleText.setText(message[0]);
            this.messageText.setText(message[1]);
        }
        );
    }

    @FXML
    private void button0Clicked() {
        this.buttonReturn = 0;
        this.releaseLock();
    }

    @FXML
    private void button1Clicked() {
        this.buttonReturn = 1;
        this.releaseLock();
    }

    @FXML
    private void button2Clicked() {
        this.buttonReturn = 2;
        this.releaseLock();
    }

    /**
     * Waits for the user to click a button and then receives the selected value
     * @return 0-button 1 was clicked, 1-button 2, 2-button 3
     * @throws InterruptedException
     */
    public String getReturn() throws InterruptedException {
        if (finishTime == 0) {
            synchronized (buttonWaitLock) {
                this.buttonWaitLock.wait();
            }
        }
        finishTime = new Date().getTime();
        String text = this.textInput.getText();
        if (!text.isEmpty()) {
            return text;
        }
        CASUALZodMainUI.content.setImage("");
        return Integer.toString(this.buttonReturn);
    }

    private void releaseLock() {
        Platform.runLater(() -> {
            synchronized (buttonWaitLock) {
                buttonWaitLock.notify();
            }
        });
    }

    /**
     * when the controller needs to talk to its parent
     * @param mainUI parent CASUALZodMainUI
     */
    public void setParent(CASUALZodMainUI mainUI) {
        this.mainPanel = mainUI;
    }

    /**
     * This should be called at the end of the ZOD panel life
     * @param parentPane the MainUI is a grid pane. 
     */
    public void disposeMessagePanel(GridPane parentPane) {
        Platform.runLater((Runnable) new Runnable() {
            final AnchorPane me = topLevel;

            @Override
            public void run() {

                topLevel.setCache(true);
                topLevel.setCacheHint(CacheHint.SCALE_AND_ROTATE);
                FadeTransition ft = new FadeTransition(Duration.millis((double) 2000.0), topLevel);
                ScaleTransition st = new ScaleTransition(Duration.millis((double) 2000.0), topLevel);
                ft.setFromValue(10.0);
                ft.setToValue(0.0);
                ft.setCycleCount(1);
                ft.setAutoReverse(false);
                st.setFromX(1.0);
                st.setFromY(1.0);
                st.setToX(10.0);
                st.setToY(10.0);
                ft.play();
                st.play();
                EventHandler<ActionEvent> eh = (ActionEvent t) -> {
                    parentPane.getChildren().remove(me);
                };
                st.setOnFinished(eh);
                topLevel.setMouseTransparent(true);
            }

        });
    }

    /**
     * Time taken to press a button after Message was presented
     * @return time in millis to press a button
     */
    public String getCompletionTime() {
        if (finishTime == 0) {
            return "not finished";
        }
        return Long.toString(finishTime - creationTime);
    }

   
}
