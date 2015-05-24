/*JOdinController provides methods for JOdin interoperability with CASUALcore. 
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

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.CASUALSessionData;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import CASUAL.communicationstools.heimdall.HeimdallTools;
import CASUAL.communicationstools.heimdall.odin.CorruptOdinFileException;
import CASUAL.communicationstools.heimdall.odin.Odin;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author adam
 */
public class JOdinController implements Initializable, CASUAL.iCASUALUI {

    @FXML
    Button testButton;

    @FXML
    private AnchorPane loading;
    @FXML
    private Label passFailLabel;
    @FXML
    private AnchorPane displaySurface;
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Label messageTitle;
    @FXML
    private TextArea messageDisplay;

    @FXML
    private AnchorPane mainSurface;

    private Stage stage;
    @FXML
    private Rectangle passFailBox;
    @FXML
    private ProgressBar progress;
    @FXML
    private TextField connectedIndicator;

    @FXML
    private CheckBox autoReboot;
    @FXML
    private CheckBox repartition;

    @FXML
    private Button pitSelection;
    @FXML
    private TextField pitLocation;

    @FXML
    private CheckBox bootloaderFlash;
    @FXML
    private Button bootloaderSelection;
    @FXML
    private TextField bootloaderLocation;

    @FXML
    private CheckBox pdaFlash;
    @FXML
    private Button pdaSelection;
    @FXML
    private TextField pdaLocation;

    @FXML
    private CheckBox phoneFlash;
    @FXML
    private Button phoneSelection;
    @FXML
    private TextField phoneLocation;

    @FXML
    private CheckBox cscFlash;
    @FXML
    private Button cscSelection;
    @FXML
    private TextField cscLocation;

    @FXML
    private TextArea messageBox;

    @FXML
    private Button start;
    @FXML
    private Button reset;
    @FXML
    private Button exit;

    @FXML
    Button reportProblem;

    @FXML
    TextField inputText;
    @FXML
    Button donate;
    boolean ready = false;

    @FXML
    Button dismissLegal;
    @FXML
    AnchorPane legal;
    @FXML
    Button showLegal;

    @FXML
    WebView ad;
    @FXML
    Label browserMode;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CASUAL.CASUALSessionData.setGUI(this);
        ready = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                displaySurface.setVisible(false);
                Log.level4Debug("Diagnostics " + CASUAL.Diagnostics.getDiagnosticReportOneLine(CASUALMain.getSession()));
                resetPassFail("");
                deviceDisconnected();
                checkFilesCheckboxes();
                inputText.setPromptText("Enter Text Here");
                new CASUAL.CASUALConnectionStatusMonitor().start(CASUALMain.getSession(),new CASUAL.communicationstools.heimdall.HeimdallTools());
                loading.setVisible(false);
                hideDisplaySurface();
            }
        });
        t.start();
        initializeAd();
    }

    @FXML
    private void installDriverButton() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                boolean x = new HeimdallTools().installDriver();
                new CASUALMessageObject("All Done>>>All done.\n\nReport: " + (x == true ? "Sucessful!" : "No Changes") + "\n\nIf you continue to have problems," + (OSTools.isMac() ? " ensure you have removed Samsung Kies from your computer.  You should also" : "") + " reboot the device and the computer. ").showInformationMessage();
            }
        });
        t.start();

    }

    File initialDir = new File(System.getProperty("user.dir"));

    private String showFileChooser(String title) {
        String s;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(initialDir);
        File f = fileChooser.showOpenDialog(JOdinMain.stage);

        if (f == null) {
            return "";
        } else {
            initialDir = f.getParentFile();
            return f.getAbsolutePath();
        }
    }

    @FXML
    private void pitPressed() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                pitLocation.setText(showFileChooser("Select PIT"));
                checkFilesCheckboxes();
            }
        });
    }

    @FXML
    private void bootloaderPressed() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bootloaderLocation.setText(showFileChooser("Select Bootloader"));
                checkFilesCheckboxes();
                if (!bootloaderLocation.getText().equals("")) {
                    bootloaderFlash.setSelected(true);
                }
            }
        });
    }

    @FXML
    private void pdaPressed() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                pdaLocation.setText(showFileChooser("Select PDA"));
                checkFilesCheckboxes();
                if (!pdaLocation.getText().equals("")) {
                    pdaFlash.setSelected(true);
                }
            }
        });
    }

    @FXML
    private void phonePressed() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                phoneLocation.setText(showFileChooser("Select Phone"));
                checkFilesCheckboxes();
                if (!phoneLocation.getText().equals("")) {
                    phoneFlash.setSelected(true);
                }
            }
        });
    }

    @FXML
    private void cscPressed() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cscLocation.setText(showFileChooser("Select CSC"));
                checkFilesCheckboxes();
                if (!cscLocation.getText().equals("")) {
                    cscFlash.setSelected(true);
                }
            }
        });
    }

    public void completePass() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                passFailLabel.setText("PASS");
                passFailBox.setFill(Paint.valueOf("green"));
                disableControls(false);
            }
        });
    }

    public void resetPassFail(final String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                passFailLabel.setText(message);
                passFailBox.setFill(Paint.valueOf("#dfdfdf"));
                disableControls(false);
            }
        });
    }

    public void completeFail() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                passFailLabel.setText("FAIL");
                passFailBox.setFill(Paint.valueOf("red"));
            }
        });
    }

    private void checkFilesCheckboxes() {
        if (bootloaderLocation.getText().equals("")) {
            bootloaderFlash.setSelected(false);
            bootloaderFlash.setDisable(true);
            bootloaderLocation.setDisable(true);
        } else {
            bootloaderFlash.setDisable(false);
            bootloaderLocation.setDisable(false);
        }
        if (pdaLocation.getText().equals("")) {
            pdaFlash.setSelected(false);
            pdaFlash.setDisable(true);
            pdaLocation.setDisable(true);
        } else {
            pdaFlash.setDisable(false);
            pdaLocation.setDisable(false);
        }
        if (phoneLocation.getText().equals("")) {
            phoneFlash.setSelected(false);
            phoneFlash.setDisable(true);
            phoneLocation.setDisable(true);
        } else {
            phoneFlash.setDisable(false);
            phoneLocation.setDisable(false);
        }
        if (cscLocation.getText().equals("")) {
            cscFlash.setSelected(false);
            cscFlash.setDisable(true);
            cscLocation.setDisable(true);
        } else {
            cscFlash.setDisable(false);
            cscLocation.setDisable(false);
        }
        if (pitLocation.getText().equals("")) {
            pitLocation.setDisable(true);
        } else {
            pitLocation.setDisable(false);
        }
    }

    private void exit() {
        System.exit(0);
        this.setVisible(false);
    }

    @FXML
    private void reset() {
        this.pitLocation.setText("");
        this.bootloaderLocation.setText("");
        this.pdaLocation.setText("");
        this.phoneLocation.setText("");
        this.cscLocation.setText("");
        this.autoReboot.setSelected(true);
        this.repartition.setSelected(false);
        this.messageBox.setText("");
        checkFilesCheckboxes();
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    boolean dummyGUI = false;

    @Override
    public boolean isDummyGUI() {
        return false;
    }

    @FXML
    private void displaySurface(final String[] message, final String[] buttonText, final boolean textbox) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainSurface.setEffect(new GaussianBlur());
                if (textbox) {
                    inputText.setVisible(true);
                } else {
                    inputText.setVisible(false);
                }
                returnValue = null;
                button1.setVisible(true);
                if (buttonText.length > 0) {
                    button1.setText(buttonText[0]);
                } else {
                    button1.setVisible(false);
                }
                if (buttonText.length >= 2) {
                    button2.setText(buttonText[1]);
                    button2.setVisible(true);
                } else {
                    button2.setVisible(false);
                }
                if (buttonText.length >= 3) {
                    button3.setText(buttonText[2]);
                    button3.setVisible(true);
                } else {
                    button3.setVisible(false);
                }

                messageTitle.setText(message[0]);
                messageDisplay.setText(message[1]);
                displaySurface.setOpacity(0);
                displaySurface.setVisible(true);
                FadeTransition ft = new FadeTransition(Duration.millis(500), displaySurface);
                ft.setFromValue(0);
                ft.setToValue(100);
                ft.setCycleCount(1);
                ft.setAutoReverse(true);
                ft.play();
            }
        });
    }
    static int i;

    private void hideDisplaySurface() {
        mainSurface.setEffect(null);
        synchronized (lock) {
            lock.notify();
        }
        inputText.setVisible(false);
        this.displaySurface.setVisible(false);
        loading.setVisible(false);

    }

    @FXML
    void button1ClickAction() {
        if (inputText.getText().equals("")) {
            returnValue = "0";
        } else {
            returnValue = inputText.getText();
            inputText.setText("");
        }

        this.hideDisplaySurface();
    }

    @FXML
    private void button2ClickAction() {
        returnValue = "1";
        this.hideDisplaySurface();
    }

    @FXML
    private void button3ClickAction() {
        returnValue = "2";
        this.hideDisplaySurface();
    }
    String returnValue;
    final Object lock = new Object();

    @Override
    public String displayMessage(CASUALMessageObject messageObject) {
        Log.Level1Interaction(messageObject.toString());
        String[] message = new String[]{messageObject.title, messageObject.messageText};
        returnValue = null;
        switch (messageObject.category) {
            case ACTIONREQUIRED:
                displaySurface(message, new String[]{"I did it", "I didn' do it"}, false);
                break;
            case COMMANDNOTIFICATION:
                displaySurface(message, new String[]{"OK"}, false);
                break;
            case TEXTINPUT:
                displaySurface(message, new String[]{"OK", "Cancel"}, true);
                break;
            case SHOWERROR:
                displaySurface(message, new String[]{"OK"}, false);
                break;
            case SHOWINFORMATION:
                displaySurface(message, new String[]{"OK"}, false);
                break;
            case SHOWYESNO:
                displaySurface(message, new String[]{"Yes", "no"}, false);
                break;
            case TIMEOUT:
                displaySurface(message, new String[]{"Ok"}, false);
                break;
            case USERCANCELOPTION:
                displaySurface(message, new String[]{"OK", "Cancel"}, false);
                break;
            case USERNOTIFICATION:
                displaySurface(message, new String[]{"OK"}, false);
                break;

        }
        try {
            while (returnValue == null) {
                synchronized (lock) {
                    lock.wait();
                }
            }
            return returnValue;
        } catch (InterruptedException ex) {
            return returnValue;
        }

    }

    @FXML
    @Override
    public void dispose() {
        CASUALSessionData.setGUI(null);
        CASUALMain.shutdown(i);
        this.exit();
    }

    private void disableControls(final boolean b) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pitSelection.setDisable(b);
                bootloaderSelection.setDisable(b);
                pdaSelection.setDisable(b);
                phoneSelection.setDisable(b);
                cscSelection.setDisable(b);
                start.setDisable(b);
                reset.setDisable(b);
                exit.setDisable(b);
            }
        });
    }

    @FXML
    @Override
    public void StartButtonActionPerformed() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                resetPassFail("running");
                disableControls(true);
                //obtain pit file
                if (pitLocation.getText().isEmpty()) {
                    String pitMessage = "Do you want me to obtain a PIT for you?>>>This application requires what is known as a` 'PIT file'.  The PIT file tells the application where to place files on your device.  If you don't have a PIT file, we can obtain one from your device";
                    boolean obtainPit = new CASUALMessageObject(pitMessage).showYesNoOption();
                    if (obtainPit) {
                        //if approved to get the pit file, get it.
                        getPitFromDevice();
                        return;

                    } else {
                        //otherwise inform user that we cannot continue without a pit. 
                        new CASUALMessageObject("We can't continue without a PIT>>>We cannot continue without a PIT file.  Please select one or let CASUAL do it for you.").showInformationMessage();
                        disableControls(false);
                        resetPassFail("halted");
                        return;
                    }
                }
                //prepare device flashing
                ArrayList<File> list = new ArrayList<>();
                getOdinPacakages(list);
                try {
                    //get the packages mapped
                    String[] runCommand = getHeimdallCommandFromOdinPackageList(list);
                    //stop the monitor
                    CASUAL.CASUALConnectionStatusMonitor.stop();

                    //verify device is connected one last time
                    HeimdallTools ht = new HeimdallTools();
                    if (!ht.isConnected()) {
                        messageBox.appendText("\nWaiting for device");
                    }

                    Log.level3Verbose("running command");
                    String s = ht.run(runCommand, 9999999, false);
                    if (ht.checkErrorMessage(runCommand, s)) {
                        completePass();
                    } else {
                        completeFail();
                    }
                    CASUAL.CASUALConnectionStatusMonitor.resumeAfterStop();

                } catch (FileNotFoundException ex) {
                    new CASUALMessageObject("We can't continue without a PIT>>>The PIT file was corrupt. We cannot continue without a PIT file.  Please select one or let CASUAL do it for you.").showInformationMessage();
                    pitLocation.setText("");
                    disableControls(false);

                    return;
                } catch (CorruptOdinFileException ex) {
                    new CASUALMessageObject("Corrupt File>>> A corrupted file was detected.  In order to continue, you must select a valid Odin File").showInformationMessage();
                    bootloaderLocation.setText("");
                    pdaLocation.setText("");
                    phoneLocation.setText("");
                    cscLocation.setText("");
                    disableControls(false);

                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JOdinController.class.getName()).log(Level.SEVERE, null, ex);
                }
                disableControls(false);
            }
        });
        t.start();

    }

    private String[] getHeimdallCommandFromOdinPackageList(ArrayList<File> list) throws FileNotFoundException, CorruptOdinFileException {
        Odin odin = new Odin(new File(pitLocation.getText()));
        String[] flashList = odin.getHeimdallFileParametersFromOdinFile(CASUALMain.getSession().getTempFolder(), list.toArray(new File[list.size()]));
        ArrayList<String> flashCommand = new ArrayList<>();
        flashCommand.add("flash");
        flashCommand.add("--PIT");
        flashCommand.add(pitLocation.getText());
        if (repartition.isSelected()) {
            flashCommand.add("--repartition");
        }
        flashCommand.addAll(Arrays.asList(flashList));
        if (!autoReboot.isSelected()) {
            flashCommand.add("--no-reboot");
        }
        messageBox.appendText(new HeimdallTools().getBinaryLocation());
        String[] runCommand = flashCommand.toArray(new String[flashCommand.size()]);
        System.out.println("heimdall Command:");
        System.out.println(new HeimdallTools().getBinaryLocation());
        return runCommand;
    }

    private void getPitFromDevice() throws HeadlessException {
        Log.level3Verbose("obtaining pit");
        new CASUALMessageObject("You will need to restart your device in Download Mode.>>>In order to obtain a PIT file, the device will be rebooted.  Once it reboots, you will need to put it back into download mode.\n\nPro-Tip: hold the download mode combination and press OK to dismiss this dislog to reboot into download mode immediately").showInformationMessage();
        HeimdallTools ht = new HeimdallTools();
        String newPit = CASUALMain.getSession().getTempFolder() + "part.pit";
        ht.run(new String[]{"download-pit", "--output", newPit}, 10000, true);
        File f = new File(newPit);
        if (f.exists() && f.length() > 1) {
            Log.level3Verbose("found pit");
            pitLocation.setText(f.getAbsolutePath());
            disableControls(false);
            resetPassFail("waiting");
            new CASUALMessageObject("Got it!>>>We obtained the PIT file and everything is ready to flash\n\n Click the start button again when you're ready. ").showInformationMessage();
        } else {
            Log.level3Verbose("Did not find pit");
            new CASUALMessageObject("Could not obtain pit.>>>We could not obtain the pit file. We tried, but it didn't work. ").showErrorDialog();
            disableControls(false);
            resetPassFail("halted");
        }
    }

    private void getOdinPacakages(ArrayList<File> list) {
        if (bootloaderFlash.isSelected()) {
            String location = bootloaderLocation.getText();
            list.add(new File(location));
            Log.level3Verbose("Added bootloader to list " + list);
        }
        if (pdaFlash.isSelected()) {
            String location = pdaLocation.getText();
            list.add(new File(location));
            Log.level3Verbose("Added PDA to list " + list);
        }
        if (phoneFlash.isSelected()) {
            String location = phoneLocation.getText();
            list.add(new File(location));
            Log.level3Verbose("Added Phone to list " + list);
        }
        if (cscFlash.isSelected()) {
            String location = cscLocation.getText();
            list.add(new File(location));
            Log.level3Verbose("Added bootloader to list " + list);
        }
    }

        @Override
    public boolean setControlStatus(boolean status,int number, String mode) {
        switch (number){
            case 0: this.deviceDisconnected();
                break;
            case 1: this.deviceConnected(mode);
                break;
            default: this.deviceMultipleConnected(number);
                break;
        }
        this.start.setDisable(!status);
        return !start.isDisabled();
    }


    Caspac caspac;

    @Override
    public void setCASPAC(Caspac caspac) {
        this.caspac = caspac;
    }

    @Override
    public void setInformationScrollBorderText(final String title) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageBox.appendText(title + "\n");
            }
        });

    }

    @Override
    public void setProgressBar(final int value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progress.setProgress(value / max);

            }
        });
    }

    int max = 100;

    @Override
    public void setProgressBarMax(int value) {
        max = value;
    }

    @Override
    public void setScript(Script s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStartButtonText(String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



    @Override
    public void setWindowBannerText(String text) {
    }

    @Override
    public void setVisible(boolean b) {

    }

    public void deviceConnected(final String mode) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                connectedIndicator.setText("Connected");
                connectedIndicator.setStyle("-fx-background-color: lime;");
            }
        });

    }

    public void deviceDisconnected() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                connectedIndicator.setText("Disconnected");
                connectedIndicator.setStyle("-fx-background-color: red;");
            }
        });

    }

    public void deviceMultipleConnected(int numberOfDevicesConnected) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                connectedIndicator.setText("Disconnected");
                connectedIndicator.setStyle("-fx-background-color: red;");
            }
        });

    }

    public void setBlocksUnzipped(String i) {
    }

    @FXML
    private void sendReport() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    new CASUAL.network.Pastebin().doPosting();
                } catch (MalformedURLException ex) {
                    new CASUALMessageObject("Could not post ").showErrorDialog();
                } catch (IOException | URISyntaxException ex) {
                    Logger.getLogger(JOdinController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.setName("reporting error");
        t.start();
    }

    @FXML
    private void donatePressed() {
        CASUAL.network.LinkLauncher ll = new CASUAL.network.LinkLauncher("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=WHZEN3FV6SKAA");
        ll.launch();
    }

    @Override
    public void sendString(String string) {

    }

    @Override
    public void sendProgress(String data) {
        sendProgress(data, messageBox);
    }

    public void sendProgress(final String data, final TextArea messageBox) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                char[] dataArray = data.toCharArray();
                for (int c : dataArray) {
                    switch (c) {
                        case 8: //backspace
                            String doc = messageBox.getText();
                            doc = doc.substring(0, doc.length() - 1);
                            messageBox.setText(doc);
                            break;

                        default:
                            messageBox.appendText(data);
                    }
                }
                String[] test = messageBox.getText().split("\n");
                String lastline = test[test.length - 1];
                if (lastline.startsWith("Uploading")) {
                    lastline = lastline.replace("Uploading ", "");
                    passFailLabel.setText(lastline);
                }

            }
        });
    }

    @FXML
    private void showLegal() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainSurface.setEffect(new GaussianBlur());
                legal.setVisible(true);
            }
        });
    }

    @FXML
    private void dismissLegal() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainSurface.setEffect(null);
                legal.setVisible(false);
            }
        });
    }

    @FXML
    private void webviewHover() {
        if (!clicked) {
            return;
        }
        ad.setMaxHeight(mainSurface.getHeight());
        ad.setMinHeight(mainSurface.getHeight());
        ad.setTranslateY((-mainSurface.getHeight()) + 90);
        browserMode.setVisible(true);
    }

    @FXML
    private void webViewLeave() {
        ad.setTranslateY(0);
        ad.setMaxHeight(90);
        ad.setMinHeight(90);
        browserMode.setVisible(false);
    }

    private boolean clicked = false;

    @FXML
    private void webViewClicked() {
        clicked = true;
        WebEngine webEngine = ad.getEngine();
        webEngine.setJavaScriptEnabled(false); //javascript runs slowly and causes problems. no scripts allowed. 

        webviewHover();
    }

    private void initializeAd() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebEngine webEngine = ad.getEngine();
                webEngine.setJavaScriptEnabled(true);
                webEngine.load("https://builds.casual-dev.com/ad.php");
            }
        });

    }


    @Override
    public void setUserMainMessage(String string) {
    }

    @Override
    public void setUserSubMessage(String string) {
    }

}
