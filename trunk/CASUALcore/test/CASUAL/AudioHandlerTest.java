/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class AudioHandlerTest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    public AudioHandlerTest() {
        System.out.println("Working Directory = " +System.getProperty("user.dir"));
        
        try {
            System.out.println(new File(".").getCanonicalPath());
        } catch (IOException ex) {
            Logger.getLogger(AudioHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of playSound method, of class AudioHandler.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testPlaySound() throws InterruptedException {
        AudioHandler.useSound=true;
        System.out.println("playSound");
        String URL = "/GUI/development/resources/sounds/CASUAL.wav";
        AudioHandler.playSound(URL);
        Thread.sleep(2000);
    }

    /**
     * Test of playMultipleInputStreams method, of class AudioHandler.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testPlayMultipleInputStreams() throws InterruptedException {
        AudioHandler.useSound=true;
        System.out.println("playMultipleInputStreams");
        String[] URLs = {"/GUI/development/resources/sounds/2.wav","/GUI/development/resources/sounds/PermissionEscillation.wav","/GUI/development/resources/sounds/3.wav","/GUI/development/resources/sounds/4.wav","/GUI/development/resources/sounds/5.wav","/GUI/development/resources/sounds/6.wav","/GUI/development/resources/sounds/7.wav","/GUI/development/resources/sounds/8.wav","/GUI/development/resources/sounds/9OrMore.wav","/GUI/development/resources/sounds/DevicesDetected.wav","/GUI/development/resources/sounds/Disconnected.wav","/GUI/development/resources/sounds/InputRequested.wav","/GUI/development/resources/sounds/Notification.wav","/GUI/development/resources/sounds/RequestToContinue.wav"};
        AudioHandler.playMultipleInputStreams(URLs);
        Thread.sleep(15000);

    }
        @Test
    public void testPermissionSound() throws InterruptedException {
        AudioHandler.useSound=true;
        System.out.println("playSound");
        String URL = "/GUI/development/resources/sounds/PermissionEscillation.wav";
        AudioHandler.playSound(URL);
        Thread.sleep(2000);
    }
}