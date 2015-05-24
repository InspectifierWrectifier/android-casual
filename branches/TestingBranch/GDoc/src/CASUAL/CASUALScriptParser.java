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

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.util.ArrayList;

/**
 *
 * @author adam
 */
public class CASUALScriptParser {

    static boolean ScriptContinue = true;
    Log Log = new Log();
    int LinesInScript = 0;
    int CurrentLine;
    String ScriptTempFolder = "";
    String ScriptName = "";
    /*
     * Executes a selected script as a resource reports to Log class.
     */

    public void executeSelectedScriptResource(String Script) {
        Log.level3("Selected resource" + Script);
        ScriptName = Script;
        CountLines CountLines = new CountLines();
        LinesInScript = CountLines.countResourceLines(Script);
        Log.level3("Lines in Script " + LinesInScript);
        ScriptTempFolder = Statics.TempFolder + Script + Statics.Slash;

        InputStream ResourceAsStream = getClass().getResourceAsStream(Statics.ScriptLocation + Script + ".scr");
        DataInputStream DIS = new DataInputStream(ResourceAsStream);
        executeSelectedScript(DIS);
    }

    /*
     * executes a CASUAL script from a file Reports to Log
     *
     */
    public void executeSelectedScriptFile(String Script) {
        Log.level3("Selected file" + Script);
        CountLines CountLines = new CountLines();
        ScriptName = Script;
        ScriptTempFolder = Statics.TempFolder + (new File(Script).getName()) + Statics.Slash;
        LinesInScript = CountLines.countFileLines(Script + ".scr");
        Log.level3("Lines in Script " + LinesInScript);
        DataInputStream DIS;
        try {

            FileInputStream FileAsStream;
            FileAsStream = new FileInputStream(Script + ".scr");
            DIS = new DataInputStream(FileAsStream);
            executeSelectedScript(DIS);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*
     * executeOneShotCommand provides a way to insert a script line.
     * 
     */
    public void executeOneShotCommand (String Line){
        //$LINE is a reference to the last line received in the shell            
        if (Line.contains("$LINE")) {
            Line = Line.replace("$LINE", Statics.LastLineReceived);
            Log.level3("Executing Reaction - $LINE: " + Line);
        }
        commandHandler(Line);
    }

    /*
     * Script Handler contains all script commands and will execute commands
     */
    private void commandHandler(String Line) {


        //Remove leading spaces
        Line = removeLeadingSpaces(Line);
//$HALT will execute any commands after the $HALT command and stop the script.
        if (Line.startsWith("$HALT")){
            ScriptContinue=false;
            Line=Line.replaceFirst("$HALT","");
            Line = removeLeadingSpaces(Line);
        }
            
//# is a comment Disregard commented lines
        if (Line.startsWith("#")) {
            Log.level3("Ignoring commented line" + Line);
            return;
        }

        //Disregard blank lines
        if (Line.equals("")) {
            return;
        }
        Log.level3("SCRIPT COMMAND:" + Line);

//$ON will trigger on an event
        //PARAM1 = Textual input event
        //PARAM2 = Command to execute
        //,= separator
        // example $ON File Not Found, $HALT
        // example $ON Permission Denied, su -c !!
        if (Line.startsWith("$ON")){
            Line=Line.replace("$ON", "");
            Line=removeLeadingSpaces(Line);
            String Event[]=Line.split(",");
            try {
              Statics.ActionEvents.add(Event[0]);
              Statics.ReactionEvents.add(Event[1]);
            } catch (Exception e) {
                               Logger.getLogger(CASUALJFrame.class.getName()).log(Level.SEVERE, null, e);

            }
            return;

        }        

//$SLASH will replace with "\" for windows or "/" for linux and mac
        if (Line.contains("$SLASH")) {
            Line = Line.replace("$SLASH", Statics.Slash);
            Log.level3("Expanded $SLASH: " + Line);
        }

//$ZIPFILE is a reference to the Script's .zip file
        if (Line.contains("$ZIPFILE")) {
            Line = Line.replace("$ZIPFILE", ScriptTempFolder);
            Log.level3("Expanded $ZIPFILE: " + Line);
        }
        
        if ((Line.contains("\\n")) && ((Line.startsWith("$USERNOTIFICATION") || Line.startsWith("$USERNOTIFICATION")) || Line.startsWith("$USERCANCELOPTION"))){
            Line=Line.replace("\\n", "\n");
        }
//$HOMEFOLDER will reference the user's home folder on the system        
        if (Line.contains("$HOMEFOLDER")) {
           Line=Line.replace("$HOMEFOLDER", Statics.CASUALHome);
           Log.level3("Expanded $HOMEFOLDER" + Line); 
        }         
//$ECHO command will display text in the main window
        if (Line.startsWith("$ECHO")) {
            Log.level3("Received ECHO command" + Line);
            Line = Line.replace("$ECHO", "");
            Line = removeLeadingSpaces(Line);
            Log.level1(Line);
            return;

// $LISTDIR will a folder on the host machine
        } else if (Line.startsWith("$LISTDIR")){
            Line=Line.replace("$LISTDIR","");
            Line=removeLeadingSpaces(Line);
            File[] files= new File(Line).listFiles();
            for (int i=0; i<=files.length; i++){
                try {
                    commandHandler("shell \"echo "+ files[i].getCanonicalPath()+"\"");
                } catch (IOException ex) {
                    Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            return;
        
// $MAKEDIR will make a folder
        } else if (Line.startsWith("$MAKEDIR")){
            Line=Line.replaceFirst("$MAKEDIR","");
            Line=removeLeadingSpaces(Line);
            new File(Line).mkdirs();
            return;
        
// $CLEARON will remove all actions/reactions
        } else if (Line.startsWith("$CLEARON")){
            Statics.ActionEvents=new ArrayList();
            Statics.ReactionEvents=new ArrayList();
            
            
//$USERNOTIFICATION will stop processing and force the user to 
        // press OK to continueNotification 
        }else if (Line.startsWith("$USERNOTIFICATION")) {
            if (Statics.UseSound.contains("true")) {
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/Notification.wav");
            }
            Line = Line.replace("$USERNOTIFICATION", "");
            Line = removeLeadingSpaces(Line);
            if (Line.contains(",")) {
                String[] Message = Line.split(",");
                JOptionPane.showMessageDialog(Statics.GUI,
                        Message[1],
                        Message[0],
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(Statics.GUI,
                        Line,
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            return;

// $USERCANCELOPTION will give the user the option to halt the script
            //USE: $USERCANCELOPTION Message
            //USE: $USERCANCELOPTION Title, Message
        } else if (Line.startsWith("$USERCANCELOPTION")) {
            if (Statics.UseSound.contains("true")) {
                //CASUALAudioSystem CAS = new CASUALAudioSystem();
                CASUALAudioSystem.playSound("/CASUAL/resources/sounds/RequestToContinue.wav");
            }
            Line = Line.replace("$USERCANCELOPTION", "");
            if (Line.contains(",")) {
                String[] Message = Line.split(",");
                Object[] Options = {"Stop",
                    "Continue"};
                int n = JOptionPane.showOptionDialog(
                        Statics.GUI,
                        Message[1],
                        Message[0],
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Options,
                        Options[1]);
                if (n == JOptionPane.YES_OPTION) {
                    Log.level0(ScriptName + " canceled at user request");
                    ScriptContinue = false;
                    return;
                }
            } else {
                int n = JOptionPane.showConfirmDialog(
                        Statics.GUI,
                        Line,
                        "Do you wish to continue?",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    Log.level0(ScriptName + " canceled at user request");
                    ScriptContinue = false;
                    return;
                }
            }
//$USERINPUTBOX will accept a String to be injected into ADB
        //Any text will be injected into the $USERINPUT variable    
        //USE: $USERINPUTBOX Title, Message, command $USERINPUT
        } else if (Line.startsWith("$USERINPUTBOX")){
            CASUALAudioSystem.playSound("/CASUAL/resources/sounds/InputRequested.wav");
            Line.replace("\\n", "\n");
            String[] Message=Line.replace("$USERINPUTBOX", "").split(",");
            String InputBoxText=JOptionPane.showInputDialog(null, Message[1], Message[0], JOptionPane.QUESTION_MESSAGE);
            InputBoxText=returnSafeCharacters(InputBoxText);
            
            
            Log.level3(InputBoxText);
            //TODO Verify this
            doShellCommand(Message[2], "$USERINPUT", InputBoxText);
            return;
// if no prefix, then send command directly to ADB.
        } else {
            
            doShellCommand(Line, null, null);
        }
        //final line output for debugging purposes
        Log.level3("COMMAND processed - " + Statics.AdbDeployed + " " + Line);
    }

    DataInputStream DATAIN;
    private void executeSelectedScript(DataInputStream DIS) {
        Statics.ReactionEvents=new ArrayList();
        Statics.ActionEvents=new ArrayList();
        ScriptContinue=true;
        DATAIN=DIS;
        Log.level3("Executing Scripted Datastream" + DIS.toString());
        Runnable r = new Runnable() {
            public void run() {
                System.out.println("CASUAL has initiated a multithreaded execution environment");
                CurrentLine = 1;
                Statics.ProgressBar.setMaximum(LinesInScript);
                Log.level3("Reading datastream" + DATAIN);
                doRead(DATAIN);
            }
        };
        Thread ExecuteScript = new Thread(r);
        ExecuteScript.start();
    }
    
    private String[] convertArrayListToStringArray(ArrayList List){
        String[] StringArray=new String[List.size()] ;
        for (int i=0; i <= List.size()-1; i++){
            StringArray[i] = List.get(i).toString();
        }
        return StringArray;
    }

    private void doRead(DataInputStream dataIn) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dataIn));
            String strLine;

            while (((strLine = bReader.readLine()) != null) && (ScriptContinue)) {
                CurrentLine++;
                Statics.ProgressBar.setValue(CurrentLine);


                commandHandler(strLine);
            }
            //Close the input stream
            dataIn.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }

    private String removeLeadingSpaces(String Line) {
        while (Line.startsWith(" ")) {
            Log.level3("Removing leading space.");
            Line = Line.replaceFirst(" ", "");
        }
        return Line;
    }

    private ArrayList parseCommandLine(String Line) {
        ArrayList List = new ArrayList();
        Boolean SingleQuoteOn = false;
        Boolean DoubleQuoteOn = false;
        String Word ="";
        char LastChar=0;
        char[] TestChars = {
            "\'".toCharArray()[0], //'
            "\"".toCharArray()[0], //"
            " ".toCharArray()[0],  // 
            "\\".toCharArray()[0], //\
            
        };
        char[] CharLine = Line.toCharArray();
        for (int I = 0; I < CharLine.length; I++) {
            //If we are not double quoted, act on singe quotes
            if (!DoubleQuoteOn && CharLine[I] == TestChars[0]&& LastChar != TestChars[3]) {
                //If we are single quoted and we see the last ' character;
                if (SingleQuoteOn){
                    SingleQuoteOn=false;  
                //start single quote
                } else if (! SingleQuoteOn){
                    SingleQuoteOn=true;
                } 
            //if we are not single quoted, act on double quotes
            } else if (!SingleQuoteOn && CharLine[I] == TestChars[1]&& LastChar != TestChars[3]) {
                //if we are doulbe quoted already and see the last character;
                if (DoubleQuoteOn) {
                    //turn doublequote off
                    DoubleQuoteOn=false;
                //start doublequote
                } else {
                    DoubleQuoteOn=true;
                }
            //if space is detected and not single or double quoted
            }else if (!SingleQuoteOn && !DoubleQuoteOn && CharLine[I] == TestChars[2] && LastChar != TestChars[3]) {
                List.add(Word);
                Word="";
            //Otherwise add it to the string
            } else {
                Word=Word + String.valueOf(CharLine[I]);
            }
            //Annotate last char for literal character checks "\".
            LastChar=CharLine[I];
        }
        //add the last word to the list if it's not blank.
        if (!Word.equals("")){ 
            List.add(Word);
        }
        return List;
    }

    /*
     * doShellCommand is the point where the shell is activated
     * ReplaceThis WithThis allows for a last-minute insertion of commands
     * by default ReplaceThis should be null.
     */
    private void doShellCommand(String Line, String ReplaceThis, String WithThis) {
            Line=this.removeLeadingSpaces(Line);
        
            Shell Shell = new Shell();
            ArrayList ShellCommand=new ArrayList();
            ShellCommand.add(Statics.AdbDeployed);
            ShellCommand.addAll(this.parseCommandLine(Line));
            String StringCommand[]= (convertArrayListToStringArray(ShellCommand));
            if (ReplaceThis != null){
                for ( int i=0; i<StringCommand.length; i++){
                StringCommand[i]=StringCommand[i].replace(ReplaceThis, WithThis);
                }
            }
            Shell.liveShellCommand(StringCommand);
    }

    private String returnSafeCharacters(String Str) {
        Str=Str.replace("\\", "\\\\");
        Str=Str.replace("\"", "\\\"");
        Str=Str.replace("\'", "\\\'");

        return Str;
    }
}
