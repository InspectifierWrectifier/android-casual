/*UI Controller for CASCADE
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

import CASPACkager.PackagerMain;
import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.CASUALSessionData;
import CASUAL.CASUALSettings.MonitorMode;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import CASUAL.crypto.AES128Handler;
import com.casual_dev.assistant_ui.CASUALAssistantUI;
import com.casual_dev.assistant_ui.casual_ui.AutomaticUI;
import com.casual_dev.assistant_ui.casual_ui.MessageHandler;
import com.casual_dev.drag_event.DragEventHandler;
import com.casual_dev.file_ops.CASPACFileSelection;
import com.casual_dev.caspaccreator2.CASPACcreator2;
import com.casual_dev.caspaccreator2.exception.MissingParameterException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author adamoutler
 */
public class CASCADEUIController extends AutomaticUI implements Initializable {

    //Scripting and Overview 
    @FXML
    Accordion scripting;

    //Overview Tab
    @FXML
    Button newScript;
    @FXML
    TextField scriptName;
    @FXML
    TextField devName;
    @FXML
    TextField scriptRevision;
    @FXML
    TextField supportURL;
    @FXML
    TextField donateTo;
    @FXML
    TextField donateLink;
    @FXML
    TextField applicationTitle;
    @FXML
    TextArea scriptDescription;
    @FXML
    TextField startButtonText;
    @FXML
    TextField bannerText;

    //Scripting panel
    @FXML
    TextArea scriptingArea;
    @FXML
    ListView<File> zipFiles;
    @FXML
    TitledPane commandAssistant;

    //CASPAC FIle area
    @FXML
    TitledPane caspacFile;
    @FXML
    Button selectCaspac;
    @FXML
    TextField pathToCaspac;
    Button reloadCASPAC;
    @FXML
    Button saveCASPAC;
    @FXML
    CheckBox encrypt;

    //CASPACOutputArea 
    @FXML
    TitledPane caspacOutput;
    @FXML
    TextField caspacOutputFolder;
    @FXML
    Button editScriptName;
    @FXML
    CheckBox useTag;
    @FXML
    Button chooseFolder;
    @FXML
    TextField tagAppend;
    @FXML
    Button saveCASUAL;
    @FXML
    Button runCASUAL;

    @FXML
    RadioButton adb;
    @FXML
    RadioButton fastboot;
    @FXML
    RadioButton heimdall;
    @FXML
    RadioButton always;

    @FXML
    TitledPane overview;
    @FXML
    TitledPane scriptingpanel;
    
    private TextInputControl[] textControls;
    private static CASCADEUIController uiController;

    public CASCADEUIController getInstance() {
        return uiController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new Thread(() -> {
            CASUALSessionData.setGUI(this);
        }).start();
        Platform.runLater(() -> {
            uiController = CASCADEUIController.this;
            textControls = new TextInputControl[]{scriptRevision, scriptName, supportURL, devName, donateTo, donateLink, applicationTitle, startButtonText, bannerText, scriptDescription, caspacOutputFolder, caspacOutputFolder, scriptingArea};
            overview.setExpanded(true);
        });
        final TextArea sa = scriptingArea;
        final TitledPane pane = this.commandAssistant;
        new Thread(() -> {
            final TreeView<Label> commandTreeView = new CASUALAssistantUI().getCasualLanguageTreeView(sa);

            Platform.runLater(() -> {
                pane.setContent(commandTreeView);

            });

        }).start();
    }

    private void insertTextIntoScriptingAreaAtCursor(String replacement) {
        final IndexRange selection = scriptingArea.getSelection();
        if (selection.getLength() == 0) {
            scriptingArea.insertText(selection.getStart(), replacement);
        } else {
            scriptingArea.replaceText(selection.getStart(), selection.getEnd(), replacement);
        }
    }

    public void disableControls(boolean e) {
        scripting.setDisable(e);
        caspacFile.setDisable(e);
        caspacOutput.setDisable(e);
    }



    @FXML
    private void newButtonClicked() {
        Platform.runLater(() -> {
            setTextAreasBlank(textControls);
            this.adb.setSelected(true);
            caspacOutput.setDisable(true);
        });

    }

    @FXML
    private void selectCaspac() {
        pathToCaspac.setText(new CASPACFileSelection().showFileChooser(CASCADE2.getStage(), pathToCaspac.getText()));
        if (!pathToCaspac.getText().isEmpty()) {
            reloadClicked();
        }
    }

    @FXML
    private void chooseFolder() {
        caspacOutputFolder.setText(new CASPACFileSelection().showFolderChooser(CASCADE2.getStage(), caspacOutputFolder.getText()));
    }

    private void setTextAreasBlank(TextInputControl[] fields) {
        for (TextInputControl field : fields) {
            field.setText("");
        }
        zipFiles.getItems().clear();
    }

    @FXML
    private void reloadClicked() {
        final File file = new File(pathToCaspac.getText());
        if (!file.exists()) {
            return;
        }

        new Thread(() -> {
            Caspac cp;
            newButtonClicked();
            try {
                if (AES128Handler.getCASPACHeaderLength(file) > 20) {
                    cp = new Caspac(CASUALMain.getSession(),file,CASUALMain.getSession().getTempFolder(), 0, getPassword());
                } else {
                    cp = new Caspac(CASUALMain.getSession(),file, CASUALMain.getSession().getTempFolder(), 0);
                }
                setIDEInfoFromCASPAC(cp);
            } catch (IOException ex) {
                if (new File(pathToCaspac.getText()).exists()) {
                    new CASUALMessageObject("There was a permissions problem reading the file", " Could not read the file:\"" + pathToCaspac.getText() + "\"").showErrorDialog();
                } else {
                    new CASUALMessageObject("File not found", "The file named:\n   \"" + pathToCaspac.getText() + "\"\ndoes not exist").showErrorDialog();
                }
                Log.errorHandler(ex);
            } catch (Exception ex) {
                new CASUALMessageObject("Exception while reading CASPAC", "please see log for more details").showErrorDialog();
                Log.errorHandler(ex);
            }
        }).start();
    }

    private void setIDEInfoFromCASPAC(final Caspac cp) {
        try {
            cp.load();
            cp.waitForUnzip();

        } catch (IOException ex) {
            new CASUALMessageObject("There was a problem reading the caspac", "Problem reading the CASPAC while unzipping.  Please see log for more details.").showErrorDialog();
            Log.errorHandler(ex);

            return;
        }
        Platform.runLater(() -> {
            Script script = cp.getScripts().get(0);
            this.scriptRevision.setText(script.getMetaData().getScriptRevision());
            this.supportURL.setText(script.getMetaData().getSupportURL());
            this.scriptName.setText(script.getName());
            this.scriptDescription.setText(script.getDiscription());
            this.scriptingArea.setText(script.getScriptContentsString());
            this.bannerText.setText(cp.getOverview());
            devName.setText(cp.getBuild().getDeveloperName());
            donateLink.setText(cp.getBuild().getDonateLink());
            donateTo.setText(cp.getBuild().getDeveloperDonateButtonText());
            applicationTitle.setText(cp.getBuild().getWindowTitle());
            startButtonText.setText(cp.getBuild().getExecuteButtonText());
            MonitorMode mode = script.getMetaData().getMonitorMode();
            switch (mode) {
                case ADB:
                    this.adb.setSelected(true);
                    break;
                case FASTBOOT:
                    this.adb.setSelected(true);
                    break;
                case HEIMDALL:
                    this.adb.setSelected(true);
                    break;
                case NONE:
                    this.always.setSelected(true);
                    break;
                default:
                    this.adb.setSelected(true);
            }
            zipFiles.getItems().addAll(script.getIndividualFiles());

            /*
             listModel.removeAllElements();
             for (File f : scriptList.getElementAt(this.scriptListJList.getSelectedIndex()).individualFiles) {
             String file = f.toString();
             listModel.addElement(file.replace(file.substring(0, file.lastIndexOf(CASUALSessionData.slash) + 1), "$ZIPFILE"));
             //listModel.addElement(f);
             }*/
        });

    }

    private String verifyNotEmpty(String value,String description) throws MissingParameterException{
        if (value.isEmpty()){
            new CASUALMessageObject("Form not complete!"," We checked and the "+description+ " field is empty! you've got to fill it out.").showErrorDialog();
            throw new MissingParameterException(description+ " is empty");
        }
        return value;
    }
    
    private String ifBlankUseDefault(String value, String defaultValue){
        if (value.isEmpty()){
            return defaultValue;
        }
        return value;
    }
    
    
    @FXML
    private File saveClicked() throws IOException {
        List<String> argsArray = new ArrayList<>();
        try{
        this.disableControls(true);
        argsArray.add("--output=" + verifyNotEmpty(this.pathToCaspac.getText(),"CASPAC Path"));
        argsArray.add("--scriptname=" +  verifyNotEmpty(scriptName.getText(),"Script Name"));
        argsArray.add("--scriptdescription=" +  verifyNotEmpty(this.scriptDescription.getText(),"Script Description"));
        argsArray.add("--scriptcode=" +  verifyNotEmpty(this.scriptingArea.getText(),"Script Contents"));
        zipFiles.getItems().stream().forEach((file) -> {
            argsArray.add("--zipfile=" + file.getAbsolutePath());
        });
        argsArray.add("--overview=" +  verifyNotEmpty(this.scriptDescription.getText(),"Script Description"));
        argsArray.add("--devname=" +  verifyNotEmpty(this.devName.getText(),"Developer Name"));

        //monitormode
        argsArray.add("--monitormode" + (adb.isSelected() ? "ADB" : //adb mode
                fastboot.isSelected() ? "FASTBOOT" : //fastboot mode
                        heimdall.isSelected() ? "HEIMDALL" : //heimdall mode
                                always.isSelected() ? "NONE" : //always enable controls
                                        "ADB"));  //default
        argsArray.add("--bannertext=" + verifyNotEmpty( this.bannerText.getText(),"Brief Overview"));
        argsArray.add("--donatebuttontext=" +  ifBlankUseDefault(this.donateTo.getText(),"casual-dev"));
        argsArray.add("--donatelink=" +  ifBlankUseDefault(this.donateLink.getText(),"https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=MYEHK66JLBVXY"));
        argsArray.add("--startbutton=" + ifBlankUseDefault(this.startButtonText.getText(),"Do It!"));
        argsArray.add("--windowtitle=" + ifBlankUseDefault(this.applicationTitle.getText(),"Generated by CASCADE"));
        argsArray.add("--scriptrevision=" +  ifBlankUseDefault(this.scriptRevision.getText(),"1"));
        argsArray.add("--supporturl=" +  ifBlankUseDefault(this.supportURL.getText(),"http://xda-developers.com"));
        } catch (MissingParameterException ex){
            this.disableControls(false);
            return null;
        }
        CASPACcreator2 cpc = new CASPACcreator2(argsArray.toArray(new String[argsArray.size()]));
        Caspac caspac;
        try {
            caspac = cpc.createNewCaspac();
        } catch (MissingParameterException ex) {
            //message here
                    this.disableControls(false);

            new CASUALMessageObject("missing parameters", " you have not filled out all parameters properly.").showErrorDialog();
            Log.errorHandler(ex);
            return null;
        }

        if (this.encrypt.isSelected()) {
            File temp = new File(caspac.getCASPAC().getAbsolutePath() + ".tmp");
            if (new AES128Handler(caspac.getCASPAC()).encrypt(temp.getAbsolutePath(), getPassword())) {
                caspac.getCASPAC().delete();
                new FileOperations().moveFile(temp, caspac.getCASPAC());
            }
        }
        disableControls(false);
        if (caspac == null) {
            new CASUALMessageObject("Could not write CASPAC", "Errors were detected while writing out the CASPAC.\n" + cpc.toString()).showErrorDialog();
            return null;
        }
        return caspac.getCASPAC();
    }

    private char[] getPassword() {
        if (Platform.isFxApplicationThread()) {
            return showPasswordDialog().getText().toCharArray();
        }
        class Temp {

            String password = "";
        }
        final Temp temp = new Temp();
        Platform.runLater(() -> {
            synchronized (temp) {
                temp.password = showPasswordDialog().getText();
                temp.notifyAll();
            }
        });

        synchronized (temp) {
            try {
                temp.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(CASCADEUIController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return temp.password.toCharArray();
    }

    private PasswordField showPasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("CASPAC Encryption");
        dialog.setHeaderText("Enter your password");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY);
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        dialog.getDialogPane().setContent(password);
        dialog.showAndWait();
        return password;
    }

    @FXML
    private File saveCASUALClicked() throws IOException {
        File myCaspacFile = saveClicked();
        this.disableControls(false);
        File casualFile = PackagerMain.doPackaging(new String[]{"--CASPAC", myCaspacFile.getAbsolutePath(), "--output", this.caspacOutputFolder.getText()});
        return casualFile;
    }

    @FXML
    private void runCASUALClicked() throws IOException{
        File casual = saveCASUALClicked();
        String exe = "";
        if (OSTools.isWindows()) {
            exe = ".exe";
        }
        final String executable = exe;
        final String outputFile = casual.getAbsolutePath();
        Runnable r = () -> {
            //CASUAL.JavaSystem.restart(new String[]{outputFile+CASUALSessionData.slash+file});
            ProcessBuilder pb;
            if (OSTools.isWindows()) {
                System.out.println("executing " + "cmd.exe /c start  " + System.getProperty("java.home") + CASUALSessionData.slash + "bin" + CASUALSessionData.slash + "java" + executable + " -jar " + outputFile);
                new CASUAL.Shell().liveShellCommand(new String[]{"cmd.exe", "/C", "\"" + outputFile + "\""}, true);
            } else {
                System.out.println("Executing" + System.getProperty("java.home") + CASUALSessionData.slash + "bin" + CASUALSessionData.slash + "java" + executable + " -jar " + outputFile);

                new CASUAL.Shell().liveShellCommand(new String[]{System.getProperty("java.home") + CASUALSessionData.slash + "bin" + CASUALSessionData.slash + "java" + executable, "-jar", outputFile}, true);

            }
            // pb.directory(new File(new File( "." ).getCanonicalPath()));
            //log.level3Verbose("Launching CASUAL \""+pb.command().get(0)+" "+pb.command().get(1)+" "+pb.command().get(2));
            //Process p = pb.start();

            //new CASUAL.Shell().sendShellCommand(new String[]{System.getProperty("java.home") + CASUALSessionData.slash + "bin" + CASUALSessionData.slash + "java" + executable,"-jar",outputFile+CASUALSessionData.slash+file});
        };
        Thread t = new Thread(r);

        t.start();
    }

    @FXML
    private void zipFileResourcesClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {

                String replacement = "\"$ZIPFILE" + new File(zipFiles.getSelectionModel().getSelectedItem().toString()).getName() + "\"";
                insertTextIntoScriptingAreaAtCursor(replacement);

            }
        }
        if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            zipFiles.getItems().remove(zipFiles.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    protected void zipFileDragOver(DragEvent event) {
        new DragEventHandler().setzipFileEventList(event);
        event.consume();
    }

    @FXML
    protected void zipFileDragExited(DragEvent event) {
        new DragEventHandler().markTimeOfDrop();

    }

    @FXML
    protected void zipFileMouseEnter() {

        List<File> fileList = new DragEventHandler().ifTimerInRangeSetFileList();
        fileList.stream().forEach((f) -> {
            if (f.isFile() && f.exists() && !zipFiles.getItems().contains(f)) {
                zipFiles.getItems().add(f);
            } else {
                Log.level4Debug("Invalid drop event detected:" + f);
            }
        });
    }

    @Override
    public String displayMessage(CASUALMessageObject mo) {
        return new MessageHandler().sendMessage(mo, CASCADE2.getScene().getRoot());
    }
}
