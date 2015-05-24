/*Log provides logging tools 
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs stuff and things
 *
 * @author Adam Outler adamoutler@gmail.com
 *
 */
public class Log {

    private static final StringBuilder sb=new StringBuilder();
    private static File logFile;

    private static iCASUALUI gui;
    

    
    
    
    /**
     * increase or decrease the logging level. 0 is error only, 4 is debug
     */
     public static LogLevel[] outputGUIVerbosity = {LogLevel.ERROR, LogLevel.INFORMATION}; //userdata is output to console

    /**
     * increase or decrease the log logFile output. 0 is error only, 4 is debug
     */
    public static LogLevel[] outputLogVerbosity = {LogLevel.ERROR, LogLevel.INTERACTION, LogLevel.INFORMATION, LogLevel.VERBOSE, LogLevel.DEBUG}; //all logs are output to logFile
     /**
     * increase or decrease the terminal output. 0 is error only, 4 is debug
     */
    public static LogLevel[] consoleLogVerbosity={LogLevel.ERROR, LogLevel.INTERACTION, LogLevel.INFORMATION, LogLevel.VERBOSE, LogLevel.DEBUG};
    /**
     * output device
     */
    public static PrintStream out = new PrintStream(System.out);
    private static final String progressBuffer = "";
    static int lastNewLine = 100;

    /**
     * @return the logFile
     */
    public static File getLogFile() {
        if (logFile==null){
            try {
                logFile=File.createTempFile("LOG","txt");
            } catch (IOException ex) {
                sb.append("could not create log file");
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return logFile;
    }

    /**
     * @param aFile the logFile to set
     */
    public static void setLogFile(File aFile) {
        logFile = aFile;
    }
    public static String getPreProgress() {
        return sb.toString();
    }

    public static void setUI(iCASUALUI ui) {
        gui=ui;
    }

    private static void sendToGUI(String data) {
        if (gui == null) {
            sb.append("\n").append(data);
        } else if (!data.equals("\n") || !data.isEmpty()) {
            gui.sendString(data + "\n");
        }
    }

    /**
     * level 0 is used for errors.. basically silent. Use level 1 for for most
     * tasks
     *
     * @param data is data to be written to log
     */
    public static void level0Error(String data) {
        data = performTranslation(data);
        routeLogsToGUIAndFile(LogLevel.ERROR, data);
    }

    /**
     * level 2 if for debugging data
     *
     * @param data is data to be written to log
     */
    // level 2 is for info-type data
    public static void level2Information(String data) {
        data = performTranslation(data);
        routeLogsToGUIAndFile(LogLevel.INFORMATION, data);
    }

    private static String performTranslation(String data) {
        if (data.startsWith("@")) {
            data = Translations.get(data);
        }
        return data;
    }

    /**
     * level 1 is used for interactive tasks.
     *
     * @param data is data to be written to log
     */
    public static void Level1Interaction(String data) {
        data = performTranslation(data);
        routeLogsToGUIAndFile(LogLevel.INTERACTION, data);

    }

    /**
     * level 3 is for verbose data
     *
     * @param data is data to be written to log
     */
    public static void level3Verbose(String data) {
        routeLogsToGUIAndFile(LogLevel.VERBOSE, data);
    }

    /**
     *
     * @param data is data to be written to log
     */
    public static void level4Debug(String data) {
        routeLogsToGUIAndFile(LogLevel.DEBUG, data);
    }

    private static void routeLogsToGUIAndFile(LogLevel ll, String data) {
        if (Arrays.asList(outputLogVerbosity).contains(ll)) {
            writeOutToLog("e/" + getCaller() + " - " + data);
        }
        if (Arrays.asList(outputGUIVerbosity).contains(ll)) {
            sendToGUI(data);
        }
        if (Arrays.asList(consoleLogVerbosity).contains(ll)) {
            out.println("[" + ll.name() + "]" + data);
        }
    }

    public static void insertChars(String data) {
        writeOutToLog(data);
        out.print(data);
    }

    /**
     *
     * @param data to be written to log logFile
     */
    public static void writeToLogFile(String data) {
        writeOutToLog(data);
    }

    private static synchronized void writeOutToLog(String data) {
        FileWriter WriteFile;
        try {
            WriteFile = new FileWriter(getLogFile(), true);
            PrintWriter output = new PrintWriter(WriteFile);
            output.write(data + "\n");
            WriteFile.close();
            output.close();
        } catch (IOException ex) {
            out.println("Attempted to write to log but could not.");
        }

    }

    /**
     *
     * @param data data to be written to progress on screen
     */
    public static void progress(String data) {
        if (gui == null) {
            System.out.print(data);
        } else {
            gui.sendProgress(data);
        }

    }

    /**
     *
     * @param data data to be written to screen in real time
     */
    public static void LiveUpdate(String data) {
        out.print(data);
        if (gui!= null) {
            gui.sendProgress(data);
        }

    }

    /**
     * begins a new line
     */
    public static void beginLine() {
        out.println();
        if (gui!=null) {
            progress("\n");
        }
    }

    /**
     *
     * @param e is any Throwable.
     */
    public static void errorHandler(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        level0Error("[CRITICAL]" + e.getLocalizedMessage() + "\n" + e.getMessage() + "\n" + e.toString() + "\n" + "\n" + writer.toString());
        level0Error("@criticalError");
    }

    static void initialize() {
        out = new PrintStream(System.out);
    }

    private static String getCaller() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        return caller.getFileName().replace("java", "") + caller.getMethodName() + "()";
    }

    public static enum LogLevel {
        
        ERROR, INTERACTION, INFORMATION, VERBOSE, DEBUG
    }
}
