/*CASUALConnectionStatus provides ADB connection status monitoring for CASUAL
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
package CASUAL;

import CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol;

/**
 * CASUALConnectionStatus provides ADB connection status monitoring for CASUAL
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALConnectionStatusMonitor {
    private static CASUALSessionData sd;
    private static int LastState = 0;  //last state detected
    private static CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol monitor;
    private static CASUAL.communicationstools.AbstractDeviceCommunicationsProtocol monitorLastState;

    /**
     * number of sucessive times ADB has halted. If ADB pauses for more than 4
     * seconds, it is considered locked up. If ADB locks up 10 times, monitoring
     * is stopped.
     */
    final static int TIMERINTERVAL = 2000;
    static boolean paused = false;
    static int connectedDevices = 0;

    /**
     * stops monitoring and nulls the monitor out. Stores the monitor to be
     * resumed at a later time. Monitor may be started again by using the
     * start(new monitor) or resumeAfterStop to continue the monitoring.
     */
    public static void stop() {
        monitorLastState = monitor;
        monitor = null;
        paused = true;
    }

    /**
     *Provides a way to restart the monitor after stop is called. 
     */
    public static void resumeAfterStop() {
        paused = false;
        if (monitorLastState == null) {
            Log.level3Verbose("A call to resume monitor occurred, but monitor was not reset first.  No action is occuring");
        } else {
            new CASUALConnectionStatusMonitor().start(sd,monitorLastState);
        }
    }

    /**
     * Static method to access toString().
     *
     * @return value of toString()
     */
    public static String getStatus() {
        return new CASUALConnectionStatusMonitor().toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String n = "\n";
        sb.append("Status:");
        if (monitor == null) {
            sb.append("offline").append(n).append("Mode:not monitoring").append(n);
        } else {
            sb.append("online").append(n).append(monitor.toString());
        }
        return sb.toString();
    }
    
    
    private String getConnectionMethodName(){
        if (null==monitor){
            return "not monitoring";
        } else {
            return monitor.getConnectionMethodName();
        }
        
    }

    /**
     * Starts and stops the ADB timer reference with
 CASUALSessionData.casualConnectionStatusMonitor.DeviceCheck ONLY;
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param mode sets the monitoring mode
     */
    public void start(CASUALSessionData sd, AbstractDeviceCommunicationsProtocol mode) {
        CASUALConnectionStatusMonitor.sd=sd;
        stop();
        paused=false;
        stateSwitcher(0);
        monitor = mode;
        Log.level3Verbose("Starting: " + mode);
        //lock controls if not available yet.
        if (CASUALSessionData.isGUIIsAvailable() && (CASUALStartupTasks.lockGUIformPrep || CASUALStartupTasks.lockGUIunzip)) {
            CASUALSessionData.getGUI().setControlStatus(false,0,getConnectionMethodName());
            LastState = 0;
        }
        doMonitoring();
    }

    private void doMonitoring() {

        final AbstractDeviceCommunicationsProtocol stateMonitor = monitor;
        //check device for state changes
        //loop on new thread while the monitor is the same monitor
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                while (CASUALConnectionStatusMonitor.monitor != null && CASUALConnectionStatusMonitor.monitor.equals(stateMonitor)) {
                    sleepForInterval();
                    if (paused) {
                        continue;
                    }
                    doDeviceCheck();
                }
            }

        });
        t.setDaemon(true);
        t.setName("Connection Status");
        t.start();

    }

    private void doDeviceCheck() {
        try {
            connectedDevices = monitor.numberOfDevicesConnected();
        } catch (NullPointerException ex) {
            connectedDevices = 0;
        }

        //Multiple devices detected
        if (connectedDevices > 1) {

            stateSwitcher(connectedDevices);
            //No devices detected
        } else if (connectedDevices == 0) {
            stateSwitcher(0);
            //One device detected
        } else if (connectedDevices == 1) {
            stateSwitcher(1);

        }
    }

    void stateSwitcher(int state) {
        if (LastState != state) {
            Log.level4Debug("Attempting state change to " + state + " devices connected");
            boolean switched;
            switch (state) {
                case 0:
                    Log.level4Debug("Device disconnected commanded");
                    sd.setStatus("Device Removed");
                    switched =CASUALSessionData.getGUI().setControlStatus(false,0,getConnectionMethodName());

                    break;
                case 1:
                    sd.setStatus("Device Connected");
                    Log.level4Debug("@stateConnected");
                    switched =CASUALSessionData.getGUI().setControlStatus(true,1,getConnectionMethodName());
                    break;
                default:
                    sd.setStatus("Multiple Devices Detected");
                    if (state == 2) {
                        Log.level0Error("@stateMultipleDevices");
                        Log.level0Error("Remove " + (state - 1) + " device to continue.");
                    }

                    switched =! CASUALSessionData.getGUI().setControlStatus(false,state,getConnectionMethodName());
                    Log.level4Debug("State Multiple Devices Number of devices" + state);

                    break;

            }
            if (switched){ //only set last state if controls were enabled/disabled when requested
                LastState = state;
            } else {
                Log.level4Debug("UI did not respond to state change, retrying.");

            }
        }
    }

    private void sleepForInterval() {
        try {
            Thread.sleep(TIMERINTERVAL);
        } catch (InterruptedException ex) {
            Log.errorHandler(ex);
        }
    }

}
