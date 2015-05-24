/*Messagehandler handles messages for CASCADE2..
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
package com.casual_dev.assistant_ui.casual_ui;

import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import CASUAL.CASUALSessionData;
import CASUAL.iCASUALUI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author adamoutler
 */
public class MessageHandler extends Application {

    final private static String title = "CASCADE";
    final Object waitLock = new Object();

    public String sendMessage(CASUALMessageObject mo, Parent p) {
        class Temp {

            public String returnValue;
        }

        final Temp retval = new Temp();

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> {
                synchronized (waitLock) {
                    retval.returnValue = displayMessage(mo, p);
                    waitLock.notifyAll();
                }
            });
            try {
                synchronized (waitLock){
                    waitLock.wait();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return retval.returnValue;
        } else {
            return retval.returnValue = displayMessage(mo, p);
        }

    }

    public static void main(String args[]) {
        launch(args);

    }

    synchronized public static String displayMessage(CASUALMessageObject messageObject, Parent p) {
        Log.Level1Interaction(messageObject.toString());

        String returnValue = "";
        switch (messageObject.category) {
            case ACTIONREQUIRED:
                return displaySurface(messageObject, new String[]{"I did it", "I didn' do it"}, AlertType.CONFIRMATION, p);

            case COMMANDNOTIFICATION:
                return displaySurface(messageObject, new String[]{"OK"}, AlertType.INFORMATION, p);

            case TEXTINPUT:
                return displaySurface(messageObject, new String[]{"OK", "Cancel"}, AlertType.CONFIRMATION, p);

            case SHOWERROR:
                return displaySurface(messageObject, new String[]{"OK"}, AlertType.ERROR, p);

            case SHOWINFORMATION:
                return displaySurface(messageObject, new String[]{"OK"}, AlertType.INFORMATION, p);

            case SHOWYESNO:
                return displaySurface(messageObject, new String[]{"Yes", "no"}, AlertType.CONFIRMATION, p);

            case TIMEOUT:
                return displaySurface(messageObject, new String[]{"Ok"}, AlertType.NONE, p);

            case USERCANCELOPTION:
                return displaySurface(messageObject, new String[]{"OK", "Cancel"}, AlertType.WARNING, p);

            case USERNOTIFICATION:
                return displaySurface(messageObject, new String[]{"OK"}, AlertType.INFORMATION, p);

        }

        return returnValue;

    }

    static SimpleIntegerProperty count = new SimpleIntegerProperty(40);

    @FXML
    private static String displaySurface(CASUALMessageObject cmo, final String[] buttonText, AlertType type, Parent p) {
        Alert dialogBox = new Alert(type);
        dialogBox.setTitle(title);
        ButtonType[] buttonTypes = convertButtonTextToButtonTypes(buttonText);
        dialogBox.getButtonTypes().setAll(buttonTypes);
        dialogBox.setHeaderText(cmo.title);
        dialogBox.setContentText(cmo.messageText);

        TextArea userMessage = new TextArea(cmo.messageText);
        userMessage.skinProperty().addListener(new ChangeListener<Skin<?>>() {

            @Override
            public void changed(
                    ObservableValue<? extends Skin<?>> ov, Skin<?> t, Skin<?> t1) {
                if (t1 != null && t1.getNode() instanceof Region) {
                    Region r = (Region) t1.getNode();
                    r.setBackground(Background.EMPTY);

                    r.getChildrenUnmodifiable().stream().
                            filter(n -> n instanceof Region).
                            map(n -> (Region) n).
                            forEach(n -> n.setBackground(Background.EMPTY));

                    r.getChildrenUnmodifiable().stream().
                            filter(n -> n instanceof Control).
                            map(n -> (Control) n).
                            forEach(c -> c.skinProperty().addListener(this)); // *
                }
            }
        });
        userMessage.setText(cmo.messageText);
        userMessage.setWrapText(true);
        userMessage.setEditable(false);
        final Text text = new Text(cmo.messageText);
        userMessage.setPrefWidth(600);
        text.setWrappingWidth(600);
        userMessage.setPrefHeight(text.getLayoutBounds().getHeight() + 50);

        dialogBox.getDialogPane().setContent(new VBox(userMessage));
        userMessage.autosize();
        dialogBox.getDialogPane().autosize();

        dialogBox.getDialogPane().getStylesheets().add("cascade2/assistant_ui/casual_ui/MessageHandler.css");
        if (cmo.category.equals(iCASUALUI.MessageCategory.TEXTINPUT)) {
            TextField userInput = new TextField();
            userMessage.setPrefHeight(userMessage.getPrefHeight());
            dialogBox.getDialogPane().setContent(new VBox(userMessage, userInput));
            return showTextInputDialog(dialogBox, userInput, p);
        } else {
            Optional<ButtonType> clickResult = showDialogWithCustomButtons(dialogBox);
            String result = null;
            result = getButtonNumberPressedFromOptional(buttonTypes, clickResult, result);
            return result;
        }
    }

    private static Optional<ButtonType> showDialogWithCustomButtons(Alert dialogBox) {

        return dialogBox.showAndWait();
    }

    private static String showTextInputDialog(Alert dialogBox, TextField userInput, Parent p) {
        ButtonType[] buttonText = new ButtonType[]{new ButtonType("Submit")};

        if (null != p) {
            if (null != p.getScene()) {
                dialogBox.initOwner(p.getScene().getWindow());
            }
        }

//        dialogBox.getDialogPane().setPrefWidth(400);
        dialogBox.getDialogPane().getButtonTypes().setAll(buttonText);
        Optional<ButtonType> o = dialogBox.showAndWait();
        return userInput.getText();
    }

    private static String getButtonNumberPressedFromOptional(ButtonType[] buttonTypes, Optional<ButtonType> clickResult, String result) {
        for (int i = 0; i < buttonTypes.length; i++) {
            if (buttonTypes[i].equals(clickResult.get())) {
                System.out.println( clickResult.get().getButtonData());
                result = Integer.toString(i);
            }
        }
        return result;
    }

    private static ButtonType[] convertButtonTextToButtonTypes(final String[] buttonText) {
        ButtonType[] buttonTypes = new ButtonType[buttonText.length];
        for (int i = 0; i < buttonTypes.length; i++) {
            buttonTypes[i] = new ButtonType(buttonText[i]);
        }
        return buttonTypes;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        CASUALMessageObject cmo = new CASUALMessageObject("This is a title", "Here is a message.  This message is intended to give lots of information about what is supposed to happen here.  If it gets too long we neeed to handle that. ");
        CASUALSessionData.setGUI(new AutomaticUI());
        try {
            /* cmo.title = "show yes no option";
             System.out.println(cmo.showYesNoOption());
             cmo.title = "action Required";
             System.out.println(cmo.showActionRequiredDialog());
             cmo.title = "error";
             cmo.showErrorDialog();
             cmo.title = "information message";
             cmo.showInformationMessage();
             cmo.title = "user cancel";
             System.out.println(cmo.showUserCancelOption());
             cmo.title = "user notification";
             cmo.showUserNotification();*/
            while (true) {
                cmo.title = "show yes no";
                System.out.println(cmo.showYesNoOption());
                sleep(100);
                cmo.title = "return string";
                System.out.println(cmo.inputDialog());
                sleep(100);
            }

        } catch (NullPointerException ex) {
            Log.errorHandler(ex);
        }

    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
