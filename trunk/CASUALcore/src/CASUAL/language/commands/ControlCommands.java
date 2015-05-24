/*
 * Copyright (C) 2014 adamoutler
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
package CASUAL.language.commands;

import CASUAL.CASUALScriptParser;
import CASUAL.CASUALSessionData;
import CASUAL.Log;
import CASUAL.language.CASUALLanguage;
import CASUAL.language.Command;
import CASUAL.misc.StringOperations;
import CASUAL.network.Pastebin;
import CASUAL.network.RemoteCASPACHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 *
 * @author adamoutler
 */
public class ControlCommands {

    public static boolean checkComments(Command cmd) {
        if (cmd.get().startsWith("#")) {
            Log.level4Debug("Ignoring commented line" + cmd.get());
            return true;
        }
        return false;
    }

    public static boolean setReturn(Command cmd) {
        if (cmd.get().startsWith("$RETURNVALUE")) {
            cmd.set(cmd.get().replaceFirst("$RETURNVALUE", "").trim());
            CASUALScriptParser.setReturnValue(cmd.get());
            cmd.setReturn(true, cmd.get());
            return true;
        }
        return false;
    }

    public static boolean checkIfContains(Command cmd) {
        if (cmd.get().startsWith("$IFCONTAINS ")) {
            cmd.set(StringOperations.removeLeadingSpaces(cmd.get().replaceFirst("$IFCONTAINS ", "").trim()));
            return true;
        }
        return false;
    }

    public static void checkHalt(CASUALSessionData sd, Command cmd) {
        if (cmd.get().startsWith("$HALT")) {
            if (sd.CASPAC != null) {
                sd.CASPAC.getActiveScript().setScriptContinue(false);
            }
            cmd.set(cmd.get().replace("$HALT", "").trim());
            Log.level4Debug("HALT RECEIVED");
            Log.level4Debug("Finishing remaining commands:" + cmd.get());
        }
    }

    public static boolean checkClearOn(CASUALSessionData sd, Command cmd) {
        if (cmd.get().startsWith("$CLEARON")) {
            sd.ActionEvents = new ArrayList<String>();
            sd.ReactionEvents = new ArrayList<String>();
            Log.level4Debug("***$CLEARON RECEIVED. CLEARING ALL LOGGING EVENTS.***");
            return true;
        }
        return false;
    }

    public static boolean checkBlankLine(Command cmd) {
        if (cmd.get().isEmpty()) {
            return true;
        }
        Log.level4Debug("SCRIPT COMMAND:" + cmd);
        return false;
    }

    public static boolean checkGoto(Command cmd) {
        if (cmd.get().startsWith("$GOTO")) {
            cmd.set(cmd.get().replace("$GOTO", "").trim());
            CASUALLanguage.GOTO = cmd.get();
            return true;
        }
        return false;
    }

    public static boolean checkIfNotContains(Command cmd) {
        if (cmd.get().startsWith("$IFNOTCONTAINS ")) {
            cmd.set(cmd.get().replaceFirst("$IFCONTAINS ", "").trim());
            return true;
        }
        return false;
    }

    public static boolean checkOn(CASUALSessionData sd, Command cmd) {
        if (cmd.get().startsWith("$ON")) {
            cmd.set(cmd.get().replace("$ON", "").trim());
            String[] Event = cmd.get().split(",");
            try {
                sd.ActionEvents.add(Event[0]);
                Log.level4Debug("***NEW EVENT ADDED***");
                Log.level4Debug("ON EVENT: " + Event[0]);
                sd.ReactionEvents.add(Event[1]);
                Log.level4Debug("PERFORM ACTION: " + Event[1]);
            } catch (Exception e) {
                Log.errorHandler(e);
            }
            return true;
        }
        return false;
    }

    public static boolean checkSendLog(CASUALSessionData sd, Command cmd) {
        if (cmd.get().startsWith("$SENDLOG")) {
            cmd.set(cmd.get().replace("$SENDLOG", "").trim());
            if (StringOperations.removeLeadingAndTrailingSpaces(cmd.get()).isEmpty()) {
                Log.level4Debug("Sendlog Command Issued!\nNo remaining commands");
            } else {
                Log.level4Debug("Sendlog Command Issued!\nFinishing remaining commands:" + cmd.get());
            }
            try {
                new Pastebin().doPosting();
            } catch (IOException ex) {
            } catch (URISyntaxException ex) {
            }
            return true;
        }
        return false;
    }

    //split the string from $IFCONTAINS "string string" $INCOMMAND "$ADB command to execute" $DO "CASUAL COMMAND"
    public static String doIfContainsReturnResults(String line, boolean ifContains) throws Exception {
        if (line.startsWith("$IFCONTAINS")) {
            line = StringOperations.removeLeadingSpaces(line.replaceFirst("\\$IFCONTAINS", "").trim());
        } else if (line.startsWith("$IFNOTCONTAINS")) {
            line = StringOperations.removeLeadingSpaces(line.replaceFirst("\\$IFNOTCONTAINS", "").trim());
        }
        String[] checkValueSplit = line.split("\\$INCOMMAND", 2);
        String checkValue = StringOperations.removeLeadingAndTrailingSpaces(checkValueSplit[0].replace("\\$INCOMMAND", line).trim());
        String[] commandSplit = checkValueSplit[1].split("\\$DO", 2);
        String command = StringOperations.removeLeadingAndTrailingSpaces(commandSplit[0]);
        String casualCommand = StringOperations.removeLeadingAndTrailingSpaces(commandSplit[1]);
        Log.level4Debug("checking for results to be " + ifContains);
        Log.level4Debug("requesting " + command);
        String returnValue = new CASUALScriptParser().executeOneShotCommand(command);
        Log.level4Debug("got " + returnValue);
        String retValue = "";
        if (returnValue.contains(checkValue) == ifContains) {
            if (casualCommand.contains("&&&")) {
                String[] lineSplit = casualCommand.split("&&&");
                for (String cmd : lineSplit) {
                    retValue += new CASUALScriptParser().executeOneShotCommand(StringOperations.removeLeadingAndTrailingSpaces(cmd).trim());
                }
            } else {
                retValue += new CASUALScriptParser().executeOneShotCommand(StringOperations.removeLeadingAndTrailingSpaces(casualCommand).trim());
            }
        }
        return retValue;
    }

    public static boolean launchCaspac(CASUALSessionData sd, Command cmd) throws IOException {
        if (cmd.get().startsWith("$CASPAC")) {
            cmd.set(cmd.get().replace("$CASPAC", "").trim());
            new RemoteCASPACHandler().executeCaspac(CASUALSessionData.newInstance(), cmd.get());
            return true;
        }
        return false;
    }

}
