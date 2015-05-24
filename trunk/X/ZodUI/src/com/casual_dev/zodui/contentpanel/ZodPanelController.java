/*Zod Panel FXML Controller provides logic for the panels 
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

package com.casual_dev.zodui.contentpanel;

import com.casual_dev.zodui.CASUALZodMainUI;
import com.casual_dev.zodui.Log.ZodLog;
import com.casual_dev.zodui.fonts.FontLoader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Zod Panel FXML Controller provides logic for the panels 
 * @author adamoutler
 */
public class ZodPanelController
extends BorderPane
implements Initializable, CASUAL.instrumentation.ModeTrackerInterface {
    @FXML
    BorderPane topLevel;
    @FXML
    Label title;
    @FXML
    Label message;
    @FXML
    ImageView image;
    @FXML
    ProgressBar progressBar;
    @FXML
    public ProgressIndicator progressIndicator;
    @FXML
    TextArea logArea;
    @FXML
    AnchorPane imageArea;
    @FXML 
    Label actionArea;
    @FXML
    Label status;
    CASUALZodMainUI parentPanel;
    final Duration transitiontime=Duration.millis((double)10000.0);
    private static Mode mode=Mode.CASUAL;

    /**
     *Sets the parent object for communication.
     * @param mainUI CASUAL Main UI. 
     */
    public void setParentObject(CASUALZodMainUI mainUI) {
        this.parentPanel = mainUI;

    }

    public void appendToLog(String text){
        Platform.runLater(()->{
            logArea.appendText(text);
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    CASUAL.instrumentation.Track.setTrackerImpl(this);
        //ObjectProperty p=actionArea.fontProperty();
        //System.out.println(p);
        
    }

    /**
     * sets zod panel content from a ZodPanelContent.
     * useful for creating a new object
     * @param zpc zod panel content.
     */
    public void setZodPanelContent(ZodPanelContent zpc) {

        AnchorPane a=imageArea;

        Platform.runLater(()->{
        title.setText(zpc.getMainTitle());
        message.setText(zpc.getSubtitle());
        progressBar.setProgress(ZodPanelContent.getProgress()/ZodPanelContent.getProgressMax());
        status.setText(zpc.getStatus());
        progressIndicator.setProgress(ZodPanelContent.getProgress()/ZodPanelContent.getProgressMax());
        image.setImage(getModeImage());
        imageArea.getChildren().add(getModeText());
        System.out.println("woot");
        });
    }

    /**
     * returns a new ZodPanelContent with the same values as the current object.
     * @return new ZodPanelContent
     */
    public ZodPanelContent getZodPanelContent() {
        
        ZodPanelContent zpc = new ZodPanelContent();
        zpc.setMainTitle(this.title.getText());
        zpc.setSubtitle(this.title.getText());
        zpc.setStatus(status.getText());
        zpc.setLog(this.logArea.getText());
        return zpc;
    }


    /**
     * Performs calculations and updates the progress/status bar.
     * should be called after every percentage update. 
     */
    public void updateProgress(){
        final double progress=ZodPanelContent.getProgress()/ZodPanelContent.getProgressMax();
        Platform.runLater(()->{
            progressBar.setProgress(progress);
            progressIndicator.setProgress(progress);
        });
    }

    /**
     *Removes this Zod Panel from the parent.
     * @param parent parent to have this object removed from 
     */
    public void disposeZod(GridPane parent) {
        Platform.runLater(() -> {
            WritableImage snapshot = topLevel.snapshot(new SnapshotParameters(), null);
            topLevel.setCacheHint(CacheHint.SCALE_AND_ROTATE);
            topLevel.setCache(true);
            ImageView i=new ImageView(snapshot);
            topLevel.setBottom(null);
            topLevel.setTop(null);
            topLevel.setRight(null);
            topLevel.setLeft(null);
            topLevel.setCenter(i);
            //final BorderPane me=topLevel;
            ZodPanelController.this.topLevel.setCache(true);
            ZodPanelController.this.topLevel.setCacheHint(CacheHint.SCALE_AND_ROTATE);
            FadeTransition ft = new FadeTransition(Duration.millis((double)200.0), (Node)ZodPanelController.this.topLevel);
            ScaleTransition st = new ScaleTransition(ZodPanelController.this.transitiontime, (Node)ZodPanelController.this.topLevel);
            RotateTransition rt = new RotateTransition(ZodPanelController.this.transitiontime, (Node)ZodPanelController.this.topLevel);
            TranslateTransition tt = new TranslateTransition(ZodPanelController.this.transitiontime, (Node)ZodPanelController.this.topLevel);
            ft.setFromValue(30.0);
            ft.setToValue(5.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(Math.random() * 0.05);
            st.setToY(Math.random() * 0.05);
            rt.setCycleCount(1);
            rt.setByAngle(Math.random() * 720.0 + 350.0);
            tt.setFromY(0.0);
            tt.setFromX(0.0);
            tt.setToX(- ZodPanelController.this.topLevel.getBoundsInParent().getWidth() / 2.0);
            tt.setToY((double)(-500 + (int)(Math.random() * 1700.0)));
            tt.play();
            rt.play();
            ft.play();
            st.play();
            rt.setOnFinished((ActionEvent actionEvent) -> {
                parent.getChildren().remove(topLevel);
            });
        });
    }

    @FXML
    private void showLog(){
        new ZodLog().showLog(new Stage());
    }
    
    /**
     * Sets the status.  Status is shown in a highly visible area,
     * and may be updated frequently. 
     * @param string value to be set as status
     */
    @FXML
    public void setStatus(String string){
        Platform.runLater(() -> {
            status.setText(string);
        });
        
    }

          @Override
    public void setMode(Mode setMode) {
        Platform.runLater(()->{
            mode = setMode;
            this.image.setImage(this.getModeImage());
            if (imageArea.getChildren().size()>1){
                imageArea.getChildren().remove(1);
            }
            this.imageArea.getChildren().add(this.getModeText());
        });
    }
    private Image getModeImage(){
        return ModeContent.getImage(mode);
    }
    
    private Text getModeText(){
       
        Text t=ModeContent.getText(mode);
        
        t.setX(150);
        t.setY(250);
        return t;
    }
    
    

}

