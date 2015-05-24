package casualpreloader;
/*CasualPreloader is used for loading CASUAL related items. 
 *Copyright (C) 2015  Adam Outler
 *Heavily Modified from Oracle JavaFX Ensemble Demo. 
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
 * 
 * 
 */

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 * Modified from Oracle JavaFX Ensemble Demo. This is a splash
 */
public class CasualPreloader extends Preloader {

    //variables for storing initial position of the stage at the beginning of drag
    final int FADEOUTTIME = 7000;
    ProgressBar bar;
    Text text;
    FadeTransition finalFadeOut;
    boolean prepairingShowed=false;
    @Override
    public void start(Stage primaryStage) throws Exception {
        //create stage which has set stage style transparent
        primaryStage = new Stage(StageStyle.TRANSPARENT);
        this.init(primaryStage);

    }

    private void init(final Stage primaryStage) {

        //create root node of scene, i.e. group
        Group rootGroup = new Group();
        //create scene with set width, height and color
        Scene scene = new Scene(rootGroup, 300, 300, Color.TRANSPARENT);
        //set scene to stage
        primaryStage.setScene(scene);
        //center stage on screen
        primaryStage.centerOnScreen();

        ProgressIndicator pi = new ProgressIndicator();
        pi.setProgress(-.1);
        pi.setMinWidth(240);
        pi.setMinHeight(240);
        pi.setStyle("-fx-progress-color: darkgray");
        pi.setOpacity(.1);
        bar = new ProgressBar();
        bar.setStyle("-fx-accent: green;");
        bar.setScaleY(2);
        // CREATE SIMPLE TEXT NODE
        text = new Text("CASUAL"); //20, 110,
        text.setFill(Color.WHITESMOKE);
        text.setEffect(new Lighting());
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setFont(Font.font(Font.getDefault().getFamily(), 30));

        // USE A LAYOUT VBOX FOR EASIER POSITIONING OF THE VISUAL NODES ON SCENE
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(90, 0, 0, 60));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(text, bar);

        rootGroup.getChildren().addAll(vBox, pi);
        //add all nodes to main root group
        //show the stage
        primaryStage.show();
        this.finalFadeOut = new FadeTransition(Duration.millis((double) 4000.0), primaryStage.getScene().getRoot());
        this.finalFadeOut.setFromValue(1.0);
        this.finalFadeOut.setToValue(0.0);
        EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                primaryStage.hide();
                primaryStage.close();
            }
        };
        finalFadeOut.setOnFinished(eh);

    }

    @Override
    public void handleStateChangeNotification(Preloader.StateChangeNotification scn) {
        if (scn.getType() != Preloader.StateChangeNotification.Type.BEFORE_START) {
            return;
        }
        this.finalFadeOut.play();
    }

    
    @Override
    public void handleProgressNotification(Preloader.ProgressNotification pn) {

        CasualPreloader.this.bar.setProgress(pn.getProgress());
        if (pn.getProgress() <= 0.98) {
            return;
        }
        if (! prepairingShowed){
            prepairingShowed=true;
            CasualPreloader.this.text.setText(CasualPreloader.this.text.getText() + "\npreparing");
            
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
