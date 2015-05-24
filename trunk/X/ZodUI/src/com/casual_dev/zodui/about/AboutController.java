/*Provides ZodUI About Window
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
package com.casual_dev.zodui.about;


import CASUAL.network.LinkLauncher;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author adamoutler
 */
public class AboutController extends Application {

    @FXML
    private WebView webview;


    
    static Stage stage;

    @FXML
    Accordion accordion;
    WebEngine webEngine;


    @FXML
    private void cdevClicked() {
        LinkLauncher ll = new LinkLauncher("http://casual-dev.com");
    }

    /**
     * main method for testing about controller.  
     * @param args none
     */
    public static void main(String[] args) {
        try {
            new AboutController().show();
        } catch (Exception ex) {
            Logger.getLogger(AboutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * shows the about controller
     */
    public void show() {
        Platform.runLater(() -> {
            try {
                start(new Stage(StageStyle.TRANSPARENT));
            } catch (Exception ex) {
                Logger.getLogger(AboutController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    /**
     * closes this window. 
     */
    @FXML
    public void close() {
        stage.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
       stage = primaryStage;
        FXMLLoader fxmlLoader = getFXMLLoader(this.getClass().getResource("/com/casual_dev/zodui/about/About.fxml"));
        
        
        Scene scene = new Scene((Parent) fxmlLoader.load(fxmlLoader.getLocation().openStream()), Color.TRANSPARENT);

        fxmlLoader.setRoot(scene);

        scene.setCamera(new PerspectiveCamera());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("CASUAL About Window");
        ((AboutController) fxmlLoader.getController()).webview.getEngine().load("http://casual-dev.com");
        ((AboutController) fxmlLoader.getController()).accordion.setExpandedPane((TitledPane) ((AboutController) fxmlLoader.getController()).accordion.getChildrenUnmodifiable().get(0));
    }

    private FXMLLoader getFXMLLoader(URL location) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        return fxmlLoader;
    }
}
