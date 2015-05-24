/*ShellTools is tools which can be used to assist with shells
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

import java.util.ArrayList;

/**
 * Provides a set of shell tools designed to assist with Java to shell on all systems. 
 * @author Adam Outler adamoutler@gmail.com
 */
public class ShellTools {

    
    
    
    
    /**
     * CASUAL's command line parser. Will return an arraylist of each part of
     * the command line. Checks for literal "\", double and single quotes to
     * determine if the command should be quoted or not. This will process only
     * the outermost layer of any single or double quote.
     *
     * @param Line Commandline to be processsed.
     * @return ArrayList representing individual portions of commandline.
     */
    public ArrayList<String> parseCommandLine(String Line) {
        ArrayList<String> List = new ArrayList<String>();
        Boolean SingleQuoteOn = false;
        Boolean DoubleQuoteOn = false;
        String Word = "";
        char LastChar = 0;
        char[] TestChars = {
            "\'".toCharArray()[0], //'
            "\"".toCharArray()[0], //"
            " ".toCharArray()[0], // 
            "\\".toCharArray()[0], //\
        };
        char[] CharLine = Line.toCharArray();
        for (int I = 0; I < CharLine.length; I++) {
            //If we are not double quoted, act on singe quotes
            if (!DoubleQuoteOn && CharLine[I] == TestChars[0] && LastChar != TestChars[3]) {
                //If we are single quoted and we see the last ' character;
                if (SingleQuoteOn) {
                    SingleQuoteOn = false;
                    //start single quote
                } else if (!SingleQuoteOn) {
                    SingleQuoteOn = true;
                }
                //if we are not single quoted, act on double quotes
            } else if (!SingleQuoteOn && CharLine[I] == TestChars[1] && LastChar != TestChars[3]) {
                DoubleQuoteOn = !DoubleQuoteOn;
            } else if (!SingleQuoteOn && !DoubleQuoteOn && CharLine[I] == TestChars[2] && LastChar != TestChars[3]) {
                List.add(Word);
                Word = "";
                //Otherwise add it to the string
            } else {
                Word += String.valueOf(CharLine[I]);
            }
            //Annotate last char for literal character checks "\".
            LastChar = CharLine[I];
        }
        //add the last word to the list if it's not blank.
        if (!Word.isEmpty()) {
            List.add(Word);
        }
        return List;
    }
}
