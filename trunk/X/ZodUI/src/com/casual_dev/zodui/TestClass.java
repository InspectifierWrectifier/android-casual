/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casual_dev.zodui;

import CASUAL.instrumentation.ModeTrackerInterface.Mode;
import CASUAL.instrumentation.Track;
import java.lang.reflect.Array;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 *
 * @author adamoutler
 */
public class TestClass {

    public static boolean hideMainScreen = false;
    public static boolean testEmoticons = false;
    private static boolean hasRun = false;
    static Duration waitTime = Duration.seconds(1);

    Timeline modeTimer = new Timeline(new KeyFrame(waitTime, (ActionEvent event) -> {
        new Thread(() -> {
            Mode[] modes = Mode.values();
            for (Mode m : modes) {
                sleep1sec();
                Track.setMode(m);
            }
        }).start();
    }));

    public void testGUIGraphics() {
        if (hasRun) {
            return;
        }
        modeTimer.play();
        hasRun = true;

    }

    void sleep1sec() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
