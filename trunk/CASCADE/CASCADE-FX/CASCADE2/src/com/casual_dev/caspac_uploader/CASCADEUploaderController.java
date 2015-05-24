/*Upload Controller uploads CASPACs to Jenkins
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
package com.casual_dev.caspac_uploader;

import CASUAL.network.LinkLauncher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author adamoutler
 */
public class CASCADEUploaderController extends Application {

    @FXML
    private TextField caspacToUpload;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextArea whitelist;
    @FXML
    private TextArea blacklist;
    @FXML
    private Button upload;

    private static CASCADEUploaderController instance;

    public static CASCADEUploaderController getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CASUAL Commands");

        BorderPane root = getControl();

        primaryStage.setScene(new Scene(root));

        primaryStage.show();
    }

    public BorderPane getControl() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            InputStream is = getClass().getResource("/cascade2/caspac_uploader/CASCADEUploader.fxml").openStream();
            BorderPane p = fxmlLoader.load(is);
            CASCADEUploaderController controller = (CASCADEUploaderController) fxmlLoader.getController();
            p.getStylesheets().add("/cascade2/caspac_uploader/cascadeuploader.css");
            instance = controller;

            return p;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private void problemButtonClicked() {
        new LinkLauncher("https://jenkins.casual-dev.com").launch();

    }

    @FXML
    private void uploadButtonClicked() throws IOException {
        final String user = username.getText();
        final String pass = password.getText();
        final String white = whitelist.getText();
        final String black = blacklist.getText();
        if (caspacToUpload.getText().isEmpty()) {
            caspacToUpload.setText("/home/adamoutler/code/android-casual/trunk/CASPAC/testpak.zip");
        }
        final String caspac = this.caspacToUpload.getText();

        try {
            String USER_AGENT = "Mozilla/5.0";
            String charset = "UTF-8";
            String postURL = "jenkins.casual-dev.com/job/test/build";
            MultipartUtility multipart = new MultipartUtility(postURL, charset);
            multipart.addHeaderField("Content-Type","multipart/form-data");
            multipart.addHeaderField("token","CASPACSUBMISSION");
            
            multipart.addFormField("Submit", "Build");
            multipart.addFormField("file", new File(caspac).getAbsolutePath());
            multipart.addFormField("name", "./CASPAC.CASPAC");
            multipart.addFormField("statusCode", "303");
            List<String> l = multipart.finish();
            for (String s : l) {
                System.out.println(s);
            }
            /*
             Submit: Build
             file0:  file.caspac.jpg
             json: {"parameter": {"name": "./CASPAC.CASPAC", "file": "file0"}, "statusCode": "303", "redirectTo": "."}
             name: caspac.caspac
             redirectTo: .
             statusCode: 303
             */
        } catch (MalformedURLException ex) {
            Logger.getLogger(CASCADEUploaderController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
