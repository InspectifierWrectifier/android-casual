/*StringOperations provides string tools 
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
package CASUAL.misc;

import CASUAL.Log;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class StringOperations {

    /**
     * replaces the last instance of a string
     *
     * @param string original string
     * @param toReplace value to replace
     * @param replacement replace with this
     * @return string with last value replaced
     */
    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    /**
     * removes leading spaces from line
     *
     * @param line string to remove spaces from
     * @return line without any leading spaces
     */
    public static String removeLeadingSpaces(String line) {
        while (line.startsWith(" ")) {
            line = line.replaceFirst(" ", "");
        }
        return line;
    }

    /**
     * removes leading and trailing spaces
     *
     * @param line original value
     * @return original value without leading or trailing spaces
     */
    public static String removeLeadingAndTrailingSpaces(String line) {
        while (line.startsWith(" ")) {
            line = line.replaceFirst(" ", "");
        }
        while (line.endsWith(" ")) {
            StringBuilder b = new StringBuilder(line);
            b.replace(line.lastIndexOf(" "), line.lastIndexOf(" ") + 1, "");
            line = b.toString();
        }
        return line;
    }

    /**
     * remove trailing spaces
     *
     * @param line original value
     * @return original value without trailing spaces
     */
    public static String removeTrailingSpaces(String line) {
        while (line.endsWith(" ")) {
            StringBuilder b = new StringBuilder(line);
            b.replace(line.lastIndexOf(" "), line.lastIndexOf(" ") + 1, "");
            line = b.toString();
        }
        return line;
    }

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

    /**
     * converts a String to an InputStream
     * @param input string to turn into an InputStream
     * @return InputStream representation of the input string. 
     */
    public static InputStream convertStringToStream(String input) {
        InputStream bas = new ByteArrayInputStream(input.getBytes());
        return bas;


    }

    /**
     * takes a array list and converts to string array
     *
     * @param list input array list
     * @return output string array
     */
    public static String[] convertArrayListToStringArray(List<String> list) {
        String[] StringArray = new String[list.size()];
        for (int i = 0; i <= list.size() - 1; i++) {
            StringArray[i] = list.get(i);
        }
        return StringArray;
    }

    /**
     * Returns an array of Strings from a source String.
     *
     * @param inputString contains comma delimited collection of strings each
     * surrounded by quotations.
     * @return string array result of breaking on commas
     * @author Jeremy Loper jrloper@gmail.com
     */
    public static String[] convertStringToArray(String inputString) {
        StringOperations.removeLeadingAndTrailingSpaces(inputString);
        String[] outputArray = {};
        int currentQuotePosition = 0;
        int lastQuotePosition = 0;

        for (int i = 0; i <= inputString.length(); i++, currentQuotePosition = inputString.indexOf("\",", currentQuotePosition)) {
            if (inputString.length() != currentQuotePosition) {
                outputArray[i] = inputString.substring(lastQuotePosition, currentQuotePosition - 1);
                lastQuotePosition = currentQuotePosition++;
            } else {
                outputArray[i] = inputString.substring(lastQuotePosition, currentQuotePosition);
                break;
            }
        }
        return outputArray;
    }

    /**
     * gets a random hexadecimal string
     *
     * @param len length of string to return
     * @return random hex string of specified length
     */
    public static String generateRandomHexString(int len) {
        final char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        String random = "";
        for (int i = 0; i < len; i++) {
            random += chars[new Random().nextInt(chars.length)];
        }
        return random;
    }

    /**
     * Converts an array to a string delimited by a space.
     * @param stringarray string array to merge into single string
     * @return string representaition of an array.
     */
    public static String arrayToString(String[] stringarray) {
        String str = " ";
        for (String stringarray1 : stringarray) {
            str = str + " " + stringarray1;
        }
        Log.level4Debug("arrayToString " + Arrays.toString(stringarray) + " expanded to: " + str);
        return str;
    }
}
