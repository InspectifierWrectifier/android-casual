 /*CASCADE2 is CASUAL's Automated Scripting Action Development Environment GUI 2
 *Copyright (C) 2015  Adam Outler & Logan Ludington
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
package com.casual_dev;

import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author adamoutler
 */
public class CASCADE2 extends Application {

    private static Stage myStage;
    private static Scene scene;

    public static Stage getStage() {
        return myStage;
    }

    public static Scene getScene() {
        return scene;
    }

    @Override
    public void start(Stage stage) throws Exception {
        myStage = stage;
        Parent  root = FXMLLoader.load(getClass().getResource("CASCADEUI.fxml"));

        System.out.println(getClass().getClassLoader().getResource("cascade2/CASCADEUI.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(400);
        stage.setMaxWidth(1024);
        stage.setMaxHeight(600);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
    }
}
