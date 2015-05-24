/*CASUALDataBridge provides a method of direct transfer to/from block/character/file objects on the device. 
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
package CASUAL.communicationstools.adb.busybox;

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import CASUAL.Shell;
import CASUAL.communicationstools.adb.ADBTools;
import CASUAL.instrumentation.ModeTrackerInterface;
import CASUAL.instrumentation.Track;
import CASUAL.misc.MandatoryThread;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * CASUALDataBridge is a unique way to flash information to a device. as opposed
 * to other methods, which require writing a string or a file, CASUALDataBridge
 * also handles Block and Character devices. This allows "flashing" and
 * "pulling" of entire partitions on a device without first transferring a file
 * to the SDCard. The technique used is to deploy a server, and monitor its
 * operations. A second thread is started which sends or receives information to
 * or from the device via TCP over USB. The end result is a verifiable and
 * error-checked method of data transfer from a computer to the Android device
 * from any file/block/char device available.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALDataBridge {

    final static String port = "27825";
    final static String ip = "127.0.0.1";
    final static Integer WATCHDOGINTERVAL = 2000;
    static boolean deviceReadyForReceive = false;
    static String deviceSideMessage = "";
    static boolean shutdownBecauseOfError = false;
    static long bytes = 0;
    static long lastbytes = -1;
    static String status = "";

    final static Object deviceSideReady = new Object();
    static Object casualSideReady;
    static Object transmissionInProgress;


   
   static CASUALDataBridge instance;
    /**
     * timeoutWatchdog checks every WATCHDOGINTERVAL millis to verify bytes have
     * increased. If bytes have not increased, it throws an error and shuts
     * things down. This is used to detect a broken connection.
     */
    public static Timer timeoutWatchdog = new Timer(WATCHDOGINTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (lastbytes == bytes) {
                try {
                    Log.level0Error("Failed to send file.  Timeout. Bytes:" + bytes + " Message:" + deviceSideMessage);
                    deviceReadyForReceive = false;
                    shutdownBecauseOfError = true;
                    timeoutWatchdog.stop();
                    throw new TimeoutException();
                } catch (TimeoutException ex) {
                    Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } else {
                lastbytes = bytes;
                CASUALMain.getSession().setStatus(status + " " + bytes);
            }
            
        }
    });

    public static CASUALDataBridge getInstance() {
        return instance;
    }
    
    public static void shutdown(){
        if (getInstance()==null|| getInstance().socket==null){
            return;
        }
        try {
            instance.shutdownCommunications(getInstance().socket,getInstance().worker);
        } catch (InterruptedException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    Shell shell = new Shell();
    ADBTools adb = new ADBTools();
    MandatoryThread worker = new MandatoryThread();
    Socket socket = new Socket();
    /**
     * used externally to command shutdown. If shutdown is commanded, all
     * operations must halt as soon as possible and return.
     */
    boolean commandedShutdown = false;
    
    /**
     * default constructor
     */
    public CASUALDataBridge(){
        if (instance!=null){
            instance.commandedShutdown=true;
            instance.worker.interrupt();
        }
        instance=this;
    }
    
    /**
     * gets a file from the device.
     *
     * @param remoteFileName path to remote file
     * @param f local file to write
     * @return string path to local file
     * @throws IOException when permission problem exists
     * @throws InterruptedException when interrupted
     */
    public synchronized String getFile(String remoteFileName, File f) throws IOException, InterruptedException {
        status = "received ";
        Log.level4Debug("CDB Starting getFile DataBridge");
        FileOutputStream fos = new FileOutputStream(f);
        //begin write

        getStream(fos, remoteFileName);

        if (shutdownBecauseOfError) {
            return deviceSideMessage;
        }
        return f.getCanonicalPath();

    }

    /**
     * Sends a string to a block/char/file on device
     *
     * @param send string to send
     * @param remoteFileName remote block/char/file on deviec
     * @return number of bytes sent
     * @throws InterruptedException when interrupted
     * @throws SocketException when socket cannot be created
     * @throws IOException when permission problem exists
     */
    public synchronized long sendString(String send, String remoteFileName) throws InterruptedException, SocketException, IOException {
        //make a duplicate of the array, with the \n and 0x3 key to end the file transfer
        Log.level3Verbose("Starting sendString DataBridge");
        send = send;
        ByteArrayInputStream bis = new ByteArrayInputStream(send.getBytes());
        long retval = sendStream(bis, remoteFileName);
        return retval;
    }

    /**
     * Sends a file to the device.
     *
     * @param f local file to send
     * @param remoteFileName path to remote file on device
     * @return number of bytes sent
     * @throws FileNotFoundException  when file is not present
     * @throws Exception when file cannot be sent
     */
    public synchronized long sendFile(File f, String remoteFileName) throws FileNotFoundException, Exception {
        Log.level2Information("sending " + f.getName() + " to device. size=" + f.length());
        FileInputStream fis = new FileInputStream(f);
        long retval = sendStream(fis, remoteFileName);
        try {
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!checkErrors()) {
            retval = 0;
        }
        if (f.length() != retval) {
            retval = 0;
        }
        return retval;

    }

    /**
     * Sends an inputstream to the device.
     *
     * @param input stream to be written to remote file
     * @param remoteFileName name of remote file to be written
     * @return number of bytes sent.
     * @throws InterruptedException when interrupted
     * @throws SocketException when socket cannot be created
     * @throws IOException when permissions problem exists
     */
    public synchronized long sendStream(final InputStream input, final String remoteFileName) throws InterruptedException, SocketException, IOException {
        resetCASUALConnection();
        Log.level4Debug("CDB Starting sendStream DataBridge");
        //start device-side receiver thread
        worker= new DeviceSideDataBridge(adb).startDeviceSideServer(remoteFileName, true);

               
        //Open the socket
        final Socket socket = setupPort();
        //grab the stream
        OutputStream os = socket.getOutputStream();
        //do the work

        copyStreamToDevice(input, os, true);
        //begin write
        shutdownCommunications(socket, worker);
        if (shutdownBecauseOfError) {
            return 0;
        }
        return bytes;
    }

    private long getStream(OutputStream output, String remoteFileName) throws IOException, InterruptedException {
        resetCASUALConnection();
        Log.level3Verbose("Starting getStream DataBridge");
        //start device-side sender
        MandatoryThread t = new DeviceSideDataBridge(adb).startDeviceSideServer(remoteFileName, false);

        //open the socket
        final Socket socket = setupPort();

        //begin write;
        copyStreamFromDevice(socket, output);

        shutdownCommunications(socket, t);
        return bytes;
    }

    private long copyStreamToDevice(InputStream input, OutputStream output, boolean toDevice) {

        try {
            //make a buffer to work with and setup start time
            long startTime = System.currentTimeMillis();
            byte[] buf = new byte[4096];
            Log.level4Debug("CDB Starting copyStreamToDevice DataBridge");
            BufferedOutputStream bos = new BufferedOutputStream(output);
            BufferedInputStream bis = new BufferedInputStream(input);
            int buflen = buf.length - 1;
            //pump in 4096 byte chunks at a time. from input to output
            Track.setMode(ModeTrackerInterface.Mode.CASUALDataBridgeFlash);
            while (bis.available() >= buflen && !commandedShutdown) {
                timeoutWatchdog.start();
                bis.read(buf);
                bos.write(buf);
                bytes += buf.length;
                timeoutWatchdog.stop();
            }

            //send final bits less than 4096
            if (bis.available() > 0) {
                buf = new byte[input.available()];
                bis.read(buf);
                bytes += buf.length;
                bos.write(buf);
            }
            bos.flush();

            CASUALMain.getSession().setStatus("Sent:" + bytes + "bytes");

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.000;
            double kb = bytes / duration / 1000;
            Log.level2Information("Sent:" + bytes / 1000 + "kb in " + duration + "s at " + kb + " KB/s");

        } catch (IOException ex) {
            timeoutWatchdog.stop();
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }

        return bytes;
    }

    private long copyStreamFromDevice(final Socket socket, OutputStream output) {
        try {
            CASUALMain.getSession().setStatus("Data transfer initiated, please wait");
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            long startTime = System.currentTimeMillis();
            byte[] buf;
            timeoutWatchdog.start();
            Track.setMode(ModeTrackerInterface.Mode.CASUALDataBridgePull);
            while (deviceReadyForReceive && !commandedShutdown) {

                while ((buf = new byte[bis.available()]).length > 0) {
                    bytes += buf.length;
                    bis.read(buf);
                    output.write(buf);

                }
            }
            timeoutWatchdog.stop();
            output.flush();
            CASUALMain.getSession().setStatus("Sent:" + bytes + "bytes");
            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.000;
            double kb = bytes / duration / 1000;
            String message;
            Log.level4Debug("CDB copyStreamFromDevice() complete");
            Log.level2Information("Sent:" + bytes / 1000 + "kb in " + duration + "s at " + kb + " KB/s");
        } catch (IOException ex) {
            Log.level4Debug("CDB copyStreamFromDevice() exception!!");
            timeoutWatchdog.stop();
            Logger.getLogger(CASUALDataBridge.class.getName()).log(Level.SEVERE, null, ex);
        }

        return bytes;
    }

    private void resetCASUALConnection() {
        deviceReadyForReceive = false;
        shutdownBecauseOfError = false;
        deviceSideMessage = "";
        bytes = 0;
        lastbytes = -1;
        Log.level4Debug("CDB RESET!!!!!! resetCASUALConnection() commanded");
        new ADBTools().restartConnection();
        shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "forward", "--remove-all"});
        try {
            Socket socket = new Socket(ip, Integer.parseInt(port));
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (ConnectException ex) {
        } catch (IOException ex) {
        }
        Log.level4Debug("CDB resetCASUALConnection() establishing ports");
        shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "shell", "killall busybox"});
        shell.silentShellCommand(new String[]{adb.getBinaryLocation(), "forward", "tcp:" + port, "tcp:" + port});
        Log.level3Verbose("CDB resetCASUALConnection() complete");
    }

    private void waitForReadySignal() {
        Log.level4Debug("CDB waitForReadySignal() Waiting for device side to be ready");
        if (deviceReadyForReceive) {
            Log.level4Debug("CDB waitForReadySignal() deviceReadyForReceive true");
            return;
        }
        Log.level4Debug("CDB waitForReadySignal() deviceReadyForReceive false");
        try {
            synchronized (deviceSideReady) {
                Log.level4Debug("CDB waitForReadySignal() Waiting for device side to be ready");
                deviceSideReady.wait();
            }
        } catch (InterruptedException ex) {
            Log.errorHandler(ex);
        }
        Log.level4Debug("CDB waitForReadySignal() Complete");

    }

    private Socket setupPort() throws SocketException, IOException, NumberFormatException {
        Log.level4Debug("CDB setupPort()");
        waitForReadySignal();
        int p = Integer.parseInt(port);
        final Socket socket = new Socket(ip, p);
        socket.setTrafficClass(0x04);

        return socket;
    }

    private void shutdownCommunications(Socket socket, MandatoryThread deviceSideServer) throws InterruptedException, IOException {
        if (!socket.isConnected()){
            return;
        }
        
        Log.level4Debug("CDB shutdownCommunications");
        deviceReadyForReceive = false;
        socket.getOutputStream().flush();
        Log.level4Debug("CDB shutdownCommunications() closing DataBridge ports");

        socket.close();

        //todo evaluate potential corruption issues, open socket, write closing character, close socket.
        Socket s = new Socket(ip, Integer.parseInt(port));
        s.getOutputStream().write(0x13);
        s.close();

        Log.level4Debug("CDB shutdownCommunications() waiting for device-side server to shutdown");
        if (!deviceSideServer.isComplete()) {
            deviceSideServer.join();
        }
    }


    private boolean checkErrors() {
        Log.level3Verbose("checking DataBridge errors.");
        return !deviceSideMessage.equals(DeviceSideDataBridge.USBDISCONNECTED) && !deviceSideMessage.equals(DeviceSideDataBridge.DEVICEDISCONNECTED) && !deviceSideMessage.startsWith(DeviceSideDataBridge.PERMISSIONERROR);
    }

    /**
     * Gets a file from the device.
     *
     * @param remoteFile remote file name
     * @param f local file name
     * @return path to local file.
     */
    public String integralGetFile(String remoteFile, File f) {

        String retval = "";
        try {
            Log.level3Verbose("Starting integralGetFile DataBridge");
            retval = getFile(remoteFile, f);
        } catch (IOException ex) {
            Log.errorHandler(ex);
        } catch (InterruptedException ex) {
            Log.errorHandler(ex);
        }
        while (shutdownBecauseOfError && !commandedShutdown) {
            deviceReadyForReceive = false;
            deviceSideMessage = "";
            shutdownBecauseOfError = false;
            bytes = 0;
            lastbytes = -1;
            status = "";
            new CASUALMessageObject("@interactionFailedToReceive").showInformationMessage();
            try {
                return getFile(remoteFile, f);
            } catch (IOException ex) {
                Log.errorHandler(ex);
            } catch (InterruptedException ex) {
                Log.errorHandler(ex);
            }

        }
        return retval;
    }

}
