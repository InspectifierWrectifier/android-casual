/*JOdinMain is the launcher for JOdin3
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
package com.casual_dev.jodin;


//NOTE: Runtime Error == Java is out of date. 
import java.util.Map;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author adam
 */
public class JOdinMain extends Application {

    public static Stage stage;
    public static Map<String,String> paramList;
    static String fileToBeDownloaded;
    @Override
    public void start(final Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("JOdin.fxml"));
        JOdinMain.stage = stage;
        Scene scene = new Scene(root);
        fileToBeDownloaded = this.getParameters().getNamed().get("file");
         //app.getParameters().getNamed("zip");
        stage.setScene(scene);
        stage.getIcons().add(new Image("odinicon.png"));
        stage.show();
        

        stage.setTitle("JODIN3      powered by Heimdall and CASUAL");
        stage.setResizable(false);
        CASUAL.network.CASUALDevIntegration.CasualDevCounter.doIncrementCounter("JODIN3CASUALwoot");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stage.close();

                CASUAL.CASUALMain.shutdown(0);
            }
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
