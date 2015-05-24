/*Displays logging information from CASUAL
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
package com.casual_dev.zodui.Log;

import CASUAL.CASUALMain;
import CASUAL.Diagnostics;
import CASUAL.FileOperations;
import CASUAL.Log;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays logging information from CASUAL
 * @author adamoutler
 */
public class ZodLog extends Application {

    Button submit = new Button("Submit to Pastebin");
    Button refresh = new Button("refresh");
    TextArea logArea = new TextArea();

    int x = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        showLog(primaryStage);
    }

    /**
     * sets the log on the stage
     * @param stage stage to use
     */
    public void showLog(Stage stage) {

        //create a vbox with a log and hbox with two buttons
        HBox hbox=new HBox(submit, refresh);
        hbox.alignmentProperty().set(Pos.CENTER_RIGHT);
        VBox vbox = new VBox(logArea, hbox);

        //set the properties so the log can take up the whole screen
        VBox.setVgrow(logArea, Priority.ALWAYS);
        VBox.setVgrow(vbox, Priority.ALWAYS);
        logArea.setMaxHeight(Double.MAX_VALUE);
        logArea.setMaxWidth(Double.MAX_VALUE);
        logArea.setPrefRowCount(30);
        logArea.setPrefColumnCount(50);

        
        //set the scene with CSS stylesheet
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        scene.getStylesheets().add("/com/casual_dev/zodui/zodanchor.css");
        stage.show();


        logArea.skinProperty().addListener(new ChangeListener<Skin<?>>() {

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
        //set click listeners
        setRefreshListener();
        setSubmitListener();
        //add content to the log
        stage.setTitle("CASUAL Log");
        logArea.setText(new FileOperations().readFile(Log.getLogFile().getAbsolutePath()));

    }

    private void setRefreshListener() {
        refresh.setOnAction((ActionEvent event) -> {
            logArea.setText("");
            logArea.setText(new FileOperations().readFile(Log.getLogFile().getAbsolutePath()) +"\n" + CASUALMain.getSession().getTempFolder() +"Log.txt refreshed "+ ++x+" times.");
        });

    }

    private void setSubmitListener() {
        submit.setOnAction((ActionEvent event) -> {
            Thread t = new Thread(() -> {
                try {
                    new CASUAL.network.Pastebin().doPosting();
                } catch (IOException | URISyntaxException ex) {
                    Log.errorHandler(ex);
                }
            });
            t.start();
        });
    }

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        Log.level4Debug("OMFG");
        Log.level4Debug(Diagnostics.diagnosticReport(CASUALMain.getSession()));
        Platform.runLater(() -> {
            try {

                new ZodLog().start(new Stage());

            } catch (Exception ex) {
                Log.errorHandler(ex);

            }
        });
    }
    private static final Logger LOG = Logger.getLogger(ZodLog.class.getName());

}
