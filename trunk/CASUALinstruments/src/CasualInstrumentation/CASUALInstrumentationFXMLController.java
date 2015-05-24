/*CASUALInstrumentationFXMLController controls the CASUALInstrumentation in JavaFX
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

import CASUAL.CASUALConnectionStatusMonitor;
import CASUAL.CASUALMain;
import CASUAL.misc.MandatoryThread;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


/**
 *
 * @author adamoutler
 */
public class CASUALInstrumentationFXMLController implements Initializable {
    
    @FXML
    TextArea monitorStatus;
    
    @FXML 
    ListView<String> messages;
    
    @FXML
    ListView<MandatoryThread> running;
    @FXML
    Button startAdbButton;
    
    @FXML 
    Button startFastboot;
    @FXML
    TextArea ta;
    
    @FXML
    Button pastebinButton;
            
  /*  @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    */


    public void updateStatus(String status){
       ta.appendText("\n"+status);
    }
    
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CASUALInstrumentation.doc=this;
    }    
    
    @FXML public void startADB(){
             new CASUALConnectionStatusMonitor().start(CASUALMain.getSession(),new CASUAL.communicationstools.adb.ADBTools());
    }
    @FXML public void startFastboot(){
             new CASUALConnectionStatusMonitor().start(CASUALMain.getSession(),new CASUAL.communicationstools.fastboot.FastbootTools());
    }
    @FXML public void startHeimdall(){
             new CASUALConnectionStatusMonitor().start(CASUALMain.getSession(),new CASUAL.communicationstools.heimdall.HeimdallTools());
    }
    
    @FXML public void resetConnection(){
        CASUALConnectionStatusMonitor.stop();
    }
    
    @FXML private void pastebin(){
        MandatoryThread t=new MandatoryThread( new Runnable (){
            @Override
            public void run (){
                try {
                    new CASUAL.network.Pastebin().pasteAnonymousLog();
                    new CASUAL.network.Pastebin().doPosting();
                } catch (MalformedURLException ex) {
                    Logger.getLogger(CASUALInstrumentationFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CASUALInstrumentationFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (URISyntaxException ex) {
                    Logger.getLogger(CASUALInstrumentationFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
        }});
        t.setName("Pastebin From Instrumentation");
        t.start();
    }
}
