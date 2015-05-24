/*
 * Copyright (C) 2014 adam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/ .
 */
package CASUAL.communicationstools.adb.busybox;

import CASUAL.CASUALMain;
import CASUAL.CASUALTools;
import CASUAL.Log;
import CASUAL.Shell;
import CASUAL.communicationstools.adb.ADBTools;
import static CASUAL.communicationstools.adb.busybox.CASUALDataBridge.ip;
import CASUAL.misc.MandatoryThread;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adam
 */
/**
 * This is a placeholder class to represent the device-server portion of the
 * DataBridge.
 */
class DeviceSideDataBridge {


    final static String USBDISCONNECTED = "USB Disconnected";
    final static String DEVICEDISCONNECTED = "error: device not found";
    final static String PERMISSIONERROR = "/system/bin/sh: can't open";

    /**
     * reads a stream and returns a string
     *
     * @param is stream to read
     * @return stream converted to string
     */
    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    final ADBTools adb;
    String deviceSideMessage;

    DeviceSideDataBridge(ADBTools adb) {
        this.adb = adb;
    }

    MandatoryThread startDeviceSideServer(String remoteFileName, boolean forWrite) {
        MandatoryThread t = new MandatoryThread(openLinkForReadOrWrite(remoteFileName, forWrite));
        t.setName("Device Write Server");
        t.start();
        Log.level3Verbose("Device-Side thread Started");
        return t;
    }

    /**
     * This returns a runnable server object ready to deploy on any device.
     *
     * @param remoteFileName filename on the device
     * @param forWrite true if writing, false if reading
     * @return server object ready to be started
     */
    Runnable openLinkForReadOrWrite(final String remoteFileName, final boolean forWrite) {
        Log.level4Debug("DEVICE: openLinkForReadOrWrite()" + (forWrite ? "for write to " : "for read from ") + remoteFileName);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Log.level3Verbose("Device Side Opening DataBrdige Read/Write Link");
                    String[] cmd;
                    //deploy and get busybox location
                    String busybox = BusyboxTools.getBusyboxLocation();
                    String donestring = "operation complete";
                    //the command executed on the device should end with a keyword.  in this case the keyword is "done" which shows us it has exited properly.
                    //this command is used if forWrite is true (flash)--  basically netcat>desired file
                    String sendcommand = busybox + " stty raw;" + busybox + " nc -l " + ip + ":" + CASUALDataBridge.port + " |" + busybox + " dd of='" + remoteFileName + "';sync;echo " + donestring;
                    //this command is used if forWrite is false (pull)--  basically netcat<desired file with a sync at the end
                    String receiveCommand = busybox + " stty raw;" + busybox + " dd if='" + remoteFileName + "'|" + busybox + " nc -l " + ip + ":" + CASUALDataBridge.port + ";echo " + donestring;
                    
                    //build the command to send or receive with root or without. 
                    //TODO test rootAccessCommand needed by using "busybox 'test -w remoteFileName && echo RootNotRequired||echo root Required';
                    if (forWrite) {
                        Log.level3Verbose("Device DataBridge Write-Mode active");
                        if (!CASUALTools.rootAccessCommand().isEmpty()) {
                            cmd = new String[]{adb.getBinaryLocation(), "shell", sendcommand};
                        } else {
                            cmd = new String[]{adb.getBinaryLocation(), "shell", CASUALTools.rootAccessCommand() + " \"" + sendcommand + ";\""};
                        }
                    } else {
                        Log.level3Verbose("Device DataBridge Read-Mode active");
                        if (CASUALTools.rootAccessCommand().isEmpty()) {
                            cmd = new String[]{adb.getBinaryLocation(), "shell", receiveCommand};
                        } else {
                            cmd = new String[]{adb.getBinaryLocation(), "shell", CASUALTools.rootAccessCommand() + " \'" + receiveCommand + "\'"};
                        }
                    }
                    Log.level4Debug("Device **TARGET SET ON REMOTE DEVICE:" + remoteFileName);
                    //launch the process
                    ProcessBuilder p = new ProcessBuilder(cmd);
                    p.redirectErrorStream(true);
                    Process proc = p.start();
                    
                    //read a byte from the inputstream from the process so it does not halt. 
                    BufferedInputStream is = new BufferedInputStream(proc.getInputStream());
                    is.read(new byte[is.available()]);
                    //wait for the connection to be ready then send the device ready signal
                    Log.level3Verbose("Device DataBridge Process Active");

                    waitForDeviceSideConnection(is);

                    //todo remove this test
                    Log.level4Debug("port list" + new Shell().sendShellCommand(new String[]{"adb", "forward", "--list"}));
                    //device is ready for transfer
                    CASUALMain.getSession().setStatus("device ready");
                    CASUALDataBridge.deviceReadyForReceive = true;
                    synchronized (CASUALDataBridge.deviceSideReady) {
                        CASUALDataBridge.deviceSideReady.notifyAll();
                    }
                    deviceSideMessage = "";

                    //transfer is complete because host closed connection and device-side process exited
                    CASUALMain.getSession().setStatus("device-side server closed");
                    deviceSideMessage += convertStreamToString(is);
                    Log.level4Debug("FinalMessage" + deviceSideMessage);

                    //check for errors.  if any errors were present they would have come before the donestring
                    //an error on this line means the server stalled and running this can fix it. 
                    // adb shell "echo woot|/data/local/tmp/busybox nc 127.0.0.1:27825 "
                    if (null == deviceSideMessage || !deviceSideMessage.contains(donestring)) {

                        Log.level0Error("Failed to send file. Message:" + deviceSideMessage);
                        Log.level0Error("Improper Exit of DataBridge");
                        if (deviceSideMessage.isEmpty()) {
                            deviceSideMessage = USBDISCONNECTED;
                        }
                        CASUALDataBridge.shutdownBecauseOfError = true;
                        CASUALDataBridge.deviceReadyForReceive = false;
                        throw new RuntimeException("Server exited improperly- received");
                    } else {
                        Log.level4Debug("DEVICE REPORTED: deviceSideMessage");
                        Log.level4Debug("device reported sucessful shutdown");
                    }

                    //signal that the device is done before this thread dies.
                    CASUALDataBridge.deviceReadyForReceive = false;

                } catch (IOException ex) {
                    Log.level0Error("Error in DeviceSideDataBridge");
                    Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            void waitForDeviceSideConnection(BufferedInputStream is) throws IOException {
                String[] cmd;
                cmd = new String[]{adb.getBinaryLocation(), "shell", "/data/local/tmp/busybox netstat -tul"};
                boolean ready = false;
                String received = "";
                CASUALMain.getSession().setStatus("monitoring ports on device");
                Log.level3Verbose("Device Waiting for Server connection for DataBridge");
                while (!ready && !CASUALDataBridge.getInstance().commandedShutdown) {
                    //monitor server status and detect errors
                    while (is.available() > 0) {
                        received += (char) is.read();
                        Log.level4Debug(received);
                        if (received.contains("read-only file system")
                                || received.contains("cannot open")
                                || received.contains("No such file or directory")
                                || received.contains(DEVICEDISCONNECTED)
                                || received.contains(USBDISCONNECTED)
                                || received.contains(PERMISSIONERROR)
                                || received.contains("no closing quote")
                                || received.contains("error: more than one device and emulator")) {
                            Log.level4Debug("Device Server Error+" + received);
                            shutdownServer(received);
                        }

                    }
                    String returnval = new Shell().silentShellCommand(cmd);
                    if (returnval.contains(":" + CASUALDataBridge.port)) {
                        ready = true;
                    }
                }
            }

            private void shutdownServer(String message) {
                Log.level3Verbose("Shutdown Commanded-- error:" + CASUALDataBridge.shutdownBecauseOfError + " -message:" + message + " -ready:" + CASUALDataBridge.deviceReadyForReceive);
                CASUALDataBridge.shutdownBecauseOfError = true;
                deviceSideMessage = message;
                CASUALDataBridge.deviceReadyForReceive = true;
                Log.level0Error(message);
            }
        };
    }
}
