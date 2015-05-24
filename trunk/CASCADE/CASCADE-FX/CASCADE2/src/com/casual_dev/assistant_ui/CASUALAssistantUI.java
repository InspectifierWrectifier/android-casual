/*AssistantUI provides assistance with CASUAL Language Commands
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
package com.casual_dev.assistant_ui;

import CASUAL.Log;
import com.casual_dev.CASCADE2;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.ToolTipManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author adamoutler
 */
public class CASUALAssistantUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CASUAL Commands");

        StackPane root = new StackPane();
        VBox vbox = new VBox();
        TextInputControl ta = new TextArea();
        TreeView<Label> tree = getCasualLanguageTreeView(ta);
        vbox.getChildren().addAll(tree, ta);

        primaryStage.setScene(new Scene(vbox, 300, 250));
        primaryStage.show();
    }

    public TreeView<Label> getCasualLanguageTreeView(TextInputControl ctl) {
  
        final TreeItem<Label> rootItem = new TreeItem<>(new Label("Commands-hover for description"));
        Elements sections=new Elements();
        TreeView<Label> tree=new TreeView();
        for (int i=0; i<3; i++){
        try {
          
            rootItem.setExpanded(true);
             tree=new TreeView<>(rootItem);
            tree.setEditable(false);
            Document casualCommandsAndVariables = Jsoup.connect("https://casual-dev.com/casual-commands-and-variables/").get();
            sections = casualCommandsAndVariables.select("section");

        } catch (IOException ex) {
           Log.level4Debug("Could not connect to server in a timely manner.  retrying.");
           continue;
        }
        Log.level4Debug("Preparing CASUALAssistantUI with "+ sections.size() + " sections.");
            sections.stream().map((e) -> {
                return e;
            }).map((Element e) -> {
                TreeItem<Label>  section = new TreeItem<>(new Label(e.attr("data-name")));
                Elements cmds = e.getElementsByTag("article");
                Log.level4Debug(e.attr("data-name") + " section contains "+ cmds.size() + " elements.");
                               
                cmds.stream().forEach((cmd) -> {
                    //get strings and then remove so whatever is left is the tooltip.
                    Elements commandName = cmd.getElementsByTag("li");
                    Elements commandCode = cmd.getElementsByTag("pre");
                    String name = commandName.text();
                    String code = commandCode.text();
                    commandName.remove();
                    commandCode.remove();
                    String tooltip = cmd.text();

                    //create a label to apply the tooltip.
                    Label label = new Label(name);
                    Platform.runLater(()->{
                            setTooltip(tooltip, label, code, ctl);
                    });
                    TreeItem<Label> l=new TreeItem<>(label);
                    section.getChildren().add(l);
                    section.setExpanded(false);
                });
                return section;
            }).forEach((section) -> {
                rootItem.getChildren().add(section);
            });
        return tree;
           
        }
        return new TreeView<>();
    }

    private final int defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();
    private void setTooltip(String tooltip, Label label, String code, TextInputControl ctl) {
        Tooltip tip=new Tooltip(tooltip);
        tip.setPrefWidth(300);
        tip.setWrapText(true);
        label.setOnMouseMoved((MouseEvent mouseEvent) -> {
            tip.show( CASCADE2.getScene().getWindow());
        });
        label.setOnMouseExited((MouseEvent mouseEvent) -> {
            tip.hide();
        });
        sendCodeToTextOnMouseClicked(label, code, ctl);
        
    }

    private void sendCodeToTextOnMouseClicked(Label label, String codeText, TextInputControl ctl) {
        label.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    ctl.replaceSelection(codeText);
                }
            }
        });
                
    }

}
