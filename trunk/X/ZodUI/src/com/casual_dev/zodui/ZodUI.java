/*Launches the CASUAL Zod User Interface
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
package com.casual_dev.zodui;

import CASUAL.CASUALMain;
import CASUAL.Diagnostics;
import CASUAL.Log;
import CASUAL.CASUALSessionData;
import com.casual_dev.zodui.Downloader.ZodDownloader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author adamoutler
 */
public class ZodUI extends Application {

    final String CASPAClocation = "https://builds.casual-dev.com/files/";
    String title = "CASPAC";
    static URL downloadCaspac;
    String[] args = new String[]{};

    /**
     * the main panel used by ZODUI
     */
    public CASUALZodMainUI zui;

    /**
     * main method used to launch Zod
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * starts the zodUI.
     * @param stage stage to be used
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {

        parseArgs();
        //Initialize an 800x600 stage, with 3D camera and show it.
        setStageSize(800, 600, stage);

        FXMLLoader fxmlLoader = getFXMLLoader(this.getClass().getResource("/com/casual_dev/zodui/CASUALZodMainUI.fxml"));

        Scene scene = new Scene((Parent) fxmlLoader.load(fxmlLoader.getLocation().openStream()));
        this.zui = (CASUALZodMainUI) fxmlLoader.getController();
        CASUALSessionData.setGUI(zui);

        //initialize the 3D Camera
        //Windows doesn't handle perspectives without 3D
        scene.setCamera(new PerspectiveCamera());
        stage.setScene(scene);
        stage.setOnCloseRequest((final WindowEvent event) -> {
            Platform.exit();
        });
        stage.show();
        

        //get the download operations off the main thread
        ZodDownloader downloader = new ZodDownloader(downloadCaspac, title);
        zui.setDownloader(downloader);
        //launch the first display panel
        displayInitialZod();
        
        new Thread( ()->{
            new CASUAL.CASUALConnectionStatusMonitor().start(CASUALMain.getSession(),new CASUAL.communicationstools.adb.ADBTools());
        }).start();

        new Thread(()->{
            downloader.downloadCaspac(zui);
        }).start();

    }

    private void parseArgs() {
        //WHAT IS THIS BLACK MAGIC?  JavaFX Auto-parses command line?
        Map<String, String> map = this.getParameters().getNamed();
        //Java 8 Delta forEach key, execute this function.

        map.keySet().stream().forEach((String key) -> {
            switch (key) {
                case "downloadCASPAC":
                    try {
                        downloadCaspac = new URL(CASPAClocation + map.get(key));
                    } catch (MalformedURLException ex) {
                        Log.errorHandler(ex);
                    }
                break;
                case "testStartup":
                    TestClass.hideMainScreen=true;
                    TestClass.testEmoticons=true;
                    break;
            }
        });
        Log.level2Information("  Copyright (C) 2013  CASUAL-Dev / Adam Outler\n"
                + "This program is free software: you can redistribute it and/or modify\n"
                + "it under the terms of the GNU General Public License as published by\n"
                + "the Free Software Foundation, either version 3 of the License, or\n"
                + "(at your option) any later version.\n"
                + "\n"
                + "This program is distributed in the hope that it will be useful,\n"
                + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                + "GNU General Public License for more details.\n"
                + "\n"
                + "You should have received a copy of the GNU General Public License\n"
                + "along with this program.  If not, see https://www.gnu.org/licenses/ .");
        Log.level4Debug(Diagnostics.diagnosticReport(CASUALMain.getSession()));
        Log.level3Verbose("Download Target:" + downloadCaspac);
    }

    private FXMLLoader getFXMLLoader(URL location) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        return fxmlLoader;
    }

    private void setStageSize(int width, int height, Stage stage) {
        stage.setMinHeight(height);
        stage.setMinWidth(width);
        stage.setResizable(true);
    }

    private void displayInitialZod() {
        zui.initializeAd();
        zui.createNewZod(CASUALZodMainUI.content);
        if (TestClass.hideMainScreen){
            zui.displayFrontPage(false);
        }
    }
    private static final Logger LOG = Logger.getLogger(ZodUI.class.getName());
}
