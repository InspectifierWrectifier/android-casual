/*Provides input and output from the main User Interface
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
package com.casual_dev.zodui;

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.CASUALScriptParser;
import CASUAL.Log;
import static CASUAL.Log.level4Debug;
import CASUAL.CASUALSessionData;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
import com.casual_dev.zodui.Downloader.ZodDownloader;
import com.casual_dev.zodui.Log.ZodLog;
import com.casual_dev.zodui.about.AboutController;
import com.casual_dev.zodui.contentpanel.ZodPanelContent;
import com.casual_dev.zodui.contentpanel.ZodPanelController;
import com.casual_dev.zodui.messagepanel.MessagePanelContent;
import com.casual_dev.zodui.messagepanel.MessagePanelController;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import static javafx.util.Duration.millis;


//TODO: if this window gets closed, we need to send a result to MessageHandler!

/**
 * Provides input and output from the main User Interface
 *
 * @author adamoutler
 */
public class CASUALZodMainUI
        implements CASUAL.iCASUALUI {

    @FXML
    GridPane grid;
    @FXML
    WebView ad;
    @FXML
    Button startButton;

    @FXML
    AnchorPane frontPage;
    BorderPane newPanel;

    Script activeScript;

    private ZodDownloader downloader;
    /**
     * The panel content currently displayed
     */
    public ZodPanelController panel;
    MessagePanelController message;

    /**
     * true if running in testing mode. //todo: delete this later This is from
     * debugging.
     */
    public static boolean testmode = false;

    static boolean messageDisplayed = false;
    static boolean frontPageDisplayed = false;
    /**
     * Content used in in ZodPanel.
     */
    public static ZodPanelContent content = new ZodPanelContent();
    final Object messageCreationLock = new Object();
    int x = 0;
    //double movement = grid.getRowConstraints().get(0).getMaxHeight()+grid.getRowConstraints().get(1).getMaxHeight();
    boolean clicked = false;
    boolean adOpened = false;
    public static AtomicBoolean CASUALready = new AtomicBoolean(true);

    /**
     * creates a new message for display
     *
     * @param msg a messagePanelContent object is required
     * @return 0, 1, 2 or text depending on what the user pressed.
     */
    public synchronized String createNewMessage(final MessagePanelContent msg) {
        messageDisplayed = true;
        organizeFrontPageAndZodPanel();
        //Start the message panel on a new thread and wait for it to create
        Platform.runLater(() -> {
            AnchorPane b = createMessagePanel(msg);
            b.setPrefHeight(panel.getHeight() - 50);
            b.setMaxHeight(panel.getHeight() - 50);

            grid.add(b, 0, 0, 1, 1);
            notifyMessageCreationCompleted();
        });

        try {
            //wait for message to be created and added so we don't run into collision.
            waitForMessageCreation();
            organizeFrontPageAndZodPanel();
        } catch (InterruptedException ex) {
            Log.errorHandler(ex);
        }

        //Get return value from user
        String messageButtonValue = "0";
        try {
            messageButtonValue = message.getReturn();
        } catch (InterruptedException ex) {
            Log.errorHandler(ex);
        }
        //get rid of the pane
        level4Debug("User clicked Button:\"" + messageButtonValue + "\" after " + message.getCompletionTime() + "ms");
        message.disposeMessagePanel(this.grid);
        messageDisplayed = false;
        organizeFrontPageAndZodPanel();
        return messageButtonValue;
    }

    private void organizeFrontPageAndZodPanel() {
        if (messageDisplayed) {
            displayFrontPage(false);
        } else if (frontPageDisplayed) {
            displayFrontPage(true);
        } else {
            displayFrontPage(false);

        }
        if (TestClass.hideMainScreen) {
            displayFrontPage(false);
        }
        if (TestClass.testEmoticons) {
            new Thread(() -> {
                new TestClass().testGUIGraphics();
            }).start();
        }

    }

    void displayFrontPage(boolean x) {
        if (frontPage.isVisible() != x) {
            Platform.runLater(() -> {
                frontPage.setVisible(x);
                if (x) {
                    frontPage.toFront();
                } else {
                    frontPage.toBack();
                }
            });
        }
        if (this.getControlStatus() != x) {
            Platform.runLater(() -> {
                this.setControlStatus(x,1,"heimdall");
            });
        }
    }

    private void notifyMessageCreationCompleted() {
        //synchronize
        synchronized (messageCreationLock) {
            messageCreationLock.notifyAll();
        }
    }

    private void waitForMessageCreation() throws InterruptedException {
        //wait for message to be created and added.
        synchronized (messageCreationLock) {
            messageCreationLock.wait();
        }
    }

    AnchorPane createMessagePanel(MessagePanelContent mpc) {
        try {

            URL location = this.getClass().getResource("/com/casual_dev/zodui/messagepanel/MessagePanel.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent root = (Parent) fxmlLoader.load(location.openStream());
            this.message = (MessagePanelController) fxmlLoader.getController();
            message.createMessage(mpc);

            return (AnchorPane) root;
        } catch (IOException ex) {
            Log.errorHandler(ex);
            return null;
        }
    }

    /**
     * Creates a new Zod Panel from Zod Panel Content.
     *
     * @param zpc content used to create new panel.
     */
    public synchronized void createNewZod(ZodPanelContent zpc) {

        /**
         * anonymous inner class to ensure there is no way to access this except
         * here.
         */
        class creator {

            CASUALZodMainUI ui;

            creator(CASUALZodMainUI ui) {
                this.ui = ui;
            }

            private BorderPane generateZodPanel(ZodPanelContent zpc) {

                try {
                    URL location = this.getClass().getResource("/com/casual_dev/zodui/contentpanel/ZodPanel.fxml");
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(location);
                    fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
                    Parent root = (Parent) fxmlLoader.load(location.openStream());
                    panel = (ZodPanelController) fxmlLoader.getController();
                    panel.setParentObject(ui);
                    panel.setZodPanelContent(zpc);

                    //imageArea.getChildren().add(zpc.getModeText(
                    return (BorderPane) root;
                } catch (IOException ex) {
                    Log.errorHandler(ex);
                    return null;
                }
            }
        }
        if (this.panel != null) {
            this.panel.disposeZod(this.grid);
        }
        BorderPane b = new creator(this).generateZodPanel(zpc);
        b.setCacheHint(CacheHint.SPEED);
        b.setCache(true);
        TranslateTransition tt = new TranslateTransition(millis((double) 500.0), (Node) b);
        tt.setFromX(-600.0);
        tt.setToX(0.0);
        Platform.runLater(() -> {

            this.grid.add(b, 0, 0);
            tt.play();
            tt.setOnFinished((ActionEvent actionEvent) -> {
                b.setCache(false);
            });
        });
        Log.level4Debug("new panel created: " + ++x);
        //this.panel.setZodPanelContent(zpc);
    }

    @FXML
    private void showLog() {
        new ZodLog().showLog(new Stage());
    }

    private void webViewScaler(boolean expansionRequested) {

    }

    @FXML
    private void webViewClicked() {

    }

    void initializeAd() {
        final String adURL = "https://builds.casual-dev.com/ad.php";
        Platform.runLater(() -> {
            final WebEngine webEngine = CASUALZodMainUI.this.ad.getEngine();
            webEngine.setJavaScriptEnabled(true);
            webEngine.load(adURL);
            webEngine.locationProperty().addListener((ChangeListener<String>) new ChangeListener<String>() {
                boolean isclicked = false;

                @Override
                public void changed(ObservableValue<? extends String> observable, String browserClaimedValue, String actualLink) {
                    System.out.println(browserClaimedValue);
                    if (!actualLink.contains(adURL) && !isclicked) {
                        isclicked = true;
                        CASUAL.network.LinkLauncher ll = new CASUAL.network.LinkLauncher(actualLink);
                        ll.launch();
                        initializeAd();
                    }
                }
            });
        });
    }

    /**
     * returns the current ZodPanelContent
     *
     * @return content
     */
    public static ZodPanelContent getZodPanelContent() {
        return content;
    }

    /**
     * returns a list of nodes representing the children of the panel
     *
     * @return list of nodes
     */
    public ObservableList<Node> getChildren() {
        return this.panel.getChildren();
    }

    @Override
    public boolean isReady() {
        return CASUALready.get();
    }

    @Override
    public void setReady(boolean bln) {
        CASUALready.set(bln);
        Platform.runLater(() -> {
            startButton.setDisable(false);
            frontPageDisplayed = true;
            organizeFrontPageAndZodPanel();
        });
        this.displayStatusOnFrontPage(bln);

    }

    @Override
    public boolean isDummyGUI() {
        return false;
    }

    @Override
    public String displayMessage(CASUALMessageObject cslm) {
        messageDisplayed = true;
        String retval = createNewMessage(new MessagePanelContent(cslm));
        return retval;

    }

    @Override
    public void dispose() {
        System.exit(0);
    }

    @Override
    @FXML
    public void StartButtonActionPerformed() {
        frontPageDisplayed = false;
        organizeFrontPageAndZodPanel();

        startButton.setDisable(true);
        //execute
        if (CASUALMain.getSession().CASPAC.getActiveScript().extractionMethod != 2) { //not on filesystem
            Log.level4Debug("Loading internal resource: " + activeScript);
            CASUALMain.getSession().CASPAC.getActiveScript().setScriptContinue(true);
            new CASUALScriptParser().executeSelectedScript(activeCASPAC, true,CASUALMain.getSession());
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
        Platform.runLater(() -> {
            frontPageDisplayed = status;
            displayStatusOnFrontPage(status);
            if (TestClass.hideMainScreen) {
                displayFrontPage(false);
            }
        });
        return true;
    }

    public boolean getControlStatus() {
        return !startButton.disableProperty().get();
    }

    Caspac activeCASPAC;

    @Override
    public void setCASPAC(Caspac caspac) {
        activeCASPAC = caspac;
    }

    @Override
    public void setInformationScrollBorderText(String string) {
        Platform.runLater(() -> {
            panel.appendToLog(string);
        });
        ZodPanelContent zpc = new ZodPanelContent(content);
        zpc.setSubtitle(string);
        this.createNewZod(zpc);
    }

    @Override
    public void setProgressBar(int i) {
        ZodPanelContent.setProgress(i);
        panel.updateProgress();
    }

    @Override
    public void setProgressBarMax(int i) {
        ZodPanelContent.setProgressMax(x);
    }

    @Override
    public void setScript(Script script) {
        this.activeScript = script;
        content.setMainTitle(script.getName());
        content.setSubtitle(script.getDiscription());
        this.createNewZod(content);
    }

    @Override
    public void setStartButtonText(String string) {
        Platform.runLater(() -> {
            startButton.setText(string);
        });

    }


    @Override
    public void setWindowBannerText(String string) {
        Platform.runLater(() -> {
            bannertext.setText(string);
        });
    }

    @Override
    public void setVisible(boolean bln) {

    }

    public void deviceConnected(String string) {
        Platform.runLater(() -> {
            deviceStatus.setText("Device Detected");
        });
    }

    public void deviceDisconnected() {
        Platform.runLater(() -> {
            deviceStatus.setText("Not Connected");
        });

    }

    public void deviceMultipleConnected(int i) {
        Platform.runLater(() -> {
            deviceStatus.setText("Please disconnect " + (i - 1) + " devices");
        });
    }

    @Override
    public void setBlocksUnzipped(String string) {
        Log.progress("Unzipping:" + string);
    }

    @Override
    public void sendString(final String string) {
        Platform.runLater(() -> {
            panel.appendToLog(string);
            CASUALZodMainUI.content.setStatus(string);
        });
        //todo: send to log
    }

    @Override
    public void sendProgress(final String string) {
            if (ZodDownloader.isDownloading()) {
                try {
                    ZodDownloader d = getDownloader();
                    String s=string.replace("kb ", "");
                    int i=Integer.valueOf(s);
                    
                    int max = d.getExpectedBytes();
                    ZodPanelContent c = this.panel.getZodPanelContent();
                    ZodPanelContent.setProgressMax(max);
                    ZodPanelContent.setProgress(i);
                    panel.setStatus("Downloading " + d.getTitle() + ":" + string + " of " + d.getExpectedBytes() + "kb");
                    Platform.runLater(() -> {
                        panel.updateProgress();
                    });
                } catch (NumberFormatException | NullPointerException ex) {
                    Platform.runLater(() -> {
                        panel.setStatus(string);
                    });

                }
            }
        
    }



    @FXML
    private void showAbout() throws Exception {
        new AboutController().show();
    }

    /**
     * @return the downloader
     */
    public ZodDownloader getDownloader() {
        return downloader;
    }

    /**
     * @param download the downloader to set
     */
    public void setDownloader(ZodDownloader download) {
        this.downloader = download;
    }

    @FXML
    Label bannertext;
    @FXML
    Label title;
    @FXML
    Label donateTo;
    @FXML
    Label donateLink;
    @FXML
    Label developer;
    @FXML
    Label deviceStatus;
    @FXML
    Label casualStatus;

    @FXML
    private void launchDonationLink() {
        CASUAL.network.LinkLauncher ll = new CASUAL.network.LinkLauncher(donateLink.getText());
        ll.launch();
    }

    public void updateFrontPageProperties(Properties p) {
        Platform.runLater(() -> {
            bannertext.setText(p.getProperty("Window.BannerText", "CASUAL Script"));
            title.setText(p.getProperty("Window.Title", "CASUAL"));
            developer.setText(p.getProperty("Developer.Name", "CASUAL Developer"));
            donateTo.setText(p.getProperty("Developer.DonateToButtonText", "Project CASUAL"));
            String link = p.getProperty("Developer.DonateLink", "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ZYM99W5RHRY3Y");
            if (link.isEmpty()) {
                link = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ZYM99W5RHRY3Y";
            }
            donateLink.setText(link);
        });
    }

    public void displayStatusOnFrontPage(boolean ready) {
        Platform.runLater(() -> {
            if (ZodDownloader.isDownloading()) {
                casualStatus.setText("Downloading");
            } else if (!CASUALready.get() || !ready) {
                casualStatus.setText("Preparing");
            } else if (deviceStatus.getText().equals("Device Detected")) {
                casualStatus.setText("Waiting for device");
            } else {
                casualStatus.setText("ready");
            }
        });
    }



    @Override
    public void setUserMainMessage(String string) {
        panel.appendToLog(string);
        content.setMainTitle(string);    }

    @Override
    public void setUserSubMessage(String string) {
        this.panel.getZodPanelContent().setSubtitle(string);
    }

}
