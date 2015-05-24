/*
 * Copyright (c) 2012 Adam Outler
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package CASUAL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALConnectionStatusMonitor {
    
    CASUALAudioSystem CAS = new CASUALAudioSystem();
    private static int LastState = 0;
    Log Log = new Log();
    Shell Shell = new Shell();
    public final static int ONE_SECOND = 1000;
    Timer DeviceCheck = new Timer(ONE_SECOND, new ActionListener() {

        public void actionPerformed(ActionEvent evt) {

            //execute adb devices and filter
            String DeviceCommand[] = {Statics.AdbDeployed, "devices"};
            String DeviceList = Shell.silentShellCommand(DeviceCommand).replace("List of devices attached \n", "").replace("\n", "").replace("\t", "");
            Statics.DeviceTracker = DeviceList.split("device");

            //Multiple devices detected
            if (Statics.DeviceTracker.length > 1) {
                stateSwitcher(Statics.DeviceTracker.length);

                //No devices detected
            } else if (Statics.DeviceTracker[0].isEmpty()) {
                stateSwitcher(0);
                
                if (Statics.HeadlessEnabled) {
                    Statics.resetTimer--;
                    
                    //total reset
                    if ((Statics.resetTimer <= 0 ) && (Statics.armed == false) )  {
                        Statics.complete=true;
                        Statics.armed=true;
                        Statics.LED(2, true);
                        Log.level1("Reset.  Ready for device");
                    }
                }

                //One device detected
            } else if (!Statics.DeviceTracker[0].isEmpty()) {
                stateSwitcher(1);
                
                //start headless mode
                if (Statics.HeadlessEnabled && Statics.complete && Statics.armed) {
                    Log.level1("Armed and Ready turned off");
                    Statics.complete = false;
                    Statics.armed = false;
                    Statics.LED(2, false);
                    Statics.LED(0, true);
                    Statics.LED(4, false);
                    Statics.LED(3, false);
                    
                    Statics.GUI.startAutoScript();
                }
            }

            //Check and handle abnormalities
            //insufficient permissions
            if (DeviceList.contains("????????????") || DeviceList.contains("failed to start daemon")) {
                DeviceCheck.stop();
                Log.level0("Insufficient permissions on server detected.");
                String cmd[] = {Statics.AdbDeployed, "kill-server"}; //kill the server
                Log.level1("killing server and requesting elevated permissions.");
                Shell.sendShellCommand(cmd); //send the command
                //notify user that permissions will be requested and what they are used for
                TimeOutOptionPane TimeOutOptionPane = new TimeOutOptionPane();
                String[] ok = {"ok"};
                if (!Statics.HeadlessEnabled) {
                    CASUALAudioSystem.playSound("/CASUAL/resources/sounds/PermissionEscillation.wav");
                }
                TimeOutOptionPane.showTimeoutDialog(60, null, "It would appear that this computer\n"
                        + "is not set up properly to communicate\n"
                        + "with the device.  As a work-around we\n"
                        + "will attempt to elevate permissions \n"
                        + "to access the device properly.", "Insufficient Permissions", TimeOutOptionPane.OK_OPTION, 2, ok, 0);
                DeviceList = Shell.elevateSimpleCommand(DeviceCommand);
                // if permissions elevation was sucessful
                if ((!DeviceList.contains("????????????")) && (!DeviceList.contains("failed to start daemon"))) {
                    Log.level3(DeviceList);
                    Log.level1("Permissions problem corrected");
                    DeviceCheck.start();
                    //devices still not properly recognized.  Log it.
                } else {
                    Log.level0("Unrecognized device detected");
                    Log.level0("");
                    Log.level0("Application halted. Please restart the application.");
                    Log.level0("If you continue to experience problems, please report this issue ");
                }
                
            }            
        }        
    });
    
    private void stateSwitcher(int State) {
        if (LastState != State) {
            Log.level3("State Change Detected, The new state is: " + State);
            switch (State) {
                case 0:
                    Log.level3("State Disconnected");
                    Statics.GUI.enableControls(false);
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceDisconnected.png", "Device Not Detected");                    
                    
                    if (!Statics.HeadlessEnabled) {
                        CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Disconnected.wav");
                    } else {
                        if ( Statics.complete){
                            Log.level1("20 second countdown");
                            Statics.resetTimer=20;
                        } else {
                            Log.level1("120 second countdown");
                            Statics.resetTimer=120;
                        }
                        Statics.LED(1, false);
                        
                    }
                    break;
                    
                case 1:
                    Log.level3("State Connected");
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/DeviceConnected.png", "Device Connected");
                    if (!Statics.HeadlessEnabled) {
                        CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Connected-SystemReady.wav");
                    } else {
                        Statics.LED(1, true);
                        Statics.GUI.enableControls(true);
                        Statics.GUI.setStatusMessageLabel("Target Acquired");
                        
                    }
                    break;
                default:
                    
                    if (State == 2) {
                        Log.level0("Multiple devices detected. Remove " + (State - 1) + " device to continue.");
                    } else {
                        Log.level0("Remove " + (State - 1) + " devices to continue.");
                    }
                    
                    Log.level3("State Multiple Devices Number of devices" + State);
                    Statics.GUI.enableControls(false);
                    Statics.GUI.setStatusLabelIcon("/CASUAL/resources/icons/TooManyDevices.png", "Target Acquired");
                    String[] URLs = {"/CASUAL/resources/sounds/" + String.valueOf(State) + ".wav", "/CASUAL/resources/sounds/DevicesDetected.wav"};
                    if (!Statics.HeadlessEnabled) {
                        CASUALAudioSystem.playMultipleInputStreams(URLs);
                    } 
                    break;
                
            }
            LastState = State;
        }
    }
}
