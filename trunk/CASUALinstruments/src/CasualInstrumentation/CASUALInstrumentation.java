/*CASUALInstrumentation provides an overview method for CASUAL debugging
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
package CasualInstrumentation;

import CASUAL.instrumentation.Instrumentation;
import CASUAL.misc.MandatoryThread;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author adamoutler
 */
public class CASUALInstrumentation extends Application implements CASUAL.instrumentation.InstrumentationInterface {

    static CASUALInstrumentationFXMLController doc;

    @SuppressWarnings("unchecked")
    public CASUALInstrumentation() {
        this.list = new ArrayList<>();
        observableList = FXCollections.observableList(list);

    }

    @Override
    @SuppressWarnings("unchecked") // method uses the MandatoryThread.toString() method This is handled by javafx. 
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("CASUALInstrumentationFXML.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

        Instrumentation.instrumentation = this;
        doc.running.<MandatoryThread>setItems(observableList);
        Thread.sleep(1000);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                CASUAL.CASUALMain.main(new String[]{});
            }
        });
        t.start();
        new CASUALInstrumentationTimer().start();

        //new CASUALInstrumentationTimer();
    }
    List<MandatoryThread> list;
    ObservableList<MandatoryThread> observableList;

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

    @Override
    public void updateStatus(final String status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                doc.ta.appendText(status + "\n");
            }
        });

    }

    @Override
    public void trackThread(final MandatoryThread thread) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                observableList.add(thread);

                thread.waitFor();
                //observableList.remove(thread);//javaFX operations should go here
            }
        });

    }


 

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALInstrumentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
