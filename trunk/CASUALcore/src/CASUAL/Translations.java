/*Translations provides tools for translating strings for CASUAL
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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides translations for the CASUAL project
 * @author Adam Outler adamoutler@gmail.com
 */
public class Translations {

    //language resource bundle
    static ResourceBundle translation;

    /**
     * Returns translated string from translation Resource Bundle. Checks to
     * make sure there is a valid resource file. Default locale is loaded if
     * required. The input String is split by " " and "\n". If the split values
     * start with the (at) character, a translation is attempted.
     *
     * @param line string to be translated
     * @return translated line
     */
    public static String get(String line) {
        if (translation == null) {
            Translations.setDefaultLanguage();
        }

        Log.level4Debug("[TRANSLATION]["+Locale.getDefault().getDisplayLanguage()+ "]" + line);
        //get translation
        String[] splitRef = line.split("( )|(\n)");
        String retVal = "";
        for (String ref : splitRef) {
            if (translation != null && !ref.isEmpty() && ref.startsWith("@")) {
                try {
                    retVal = line.replace(ref, translation.getString(ref));
                } catch (java.util.MissingResourceException ex) {
                    Log.level3Verbose("*****MISSING TRANSLATION VALUE***** for "+ref+" ");
                }
            }
        }
        return retVal;
    }

    /**
     * Sets language by Locale. If the translation is missing, the default is
     * CASUAL/resources/Translations/English.properties.
     */
    private static void setDefaultLanguage() {
        String lang = Locale.getDefault().getDisplayLanguage();
        try {
            
            translation = ResourceBundle.getBundle("CASUAL/resources/Translations/"+lang, Locale.getDefault());
        } catch (Exception e) {
            translation = ResourceBundle.getBundle("CASUAL/resources/Translations/English");
            Log.level3Verbose("Language " + lang + " was not found in CASUAL/resources/Translations/" + lang + ".properties.  CASUAL will accept translations.  Defaulting to english. ");
        }
    }

    /**
     * Sets up a translation language for testing CASUAL. If the translation is
     * missing, the default is CASUAL/resources/Translations/English.properties.
     *
     * @param lang attempts to load specified language.
     */
    public void setLanguage(String lang) {
        try {
            translation = ResourceBundle.getBundle("CASUAL/resources/Translations/" + lang);
        } catch (Exception e) {
            translation = ResourceBundle.getBundle("CASUAL/resources/Translations/English");
            Log.level3Verbose("Language " + lang + " was not found in CASUAL/resources/Translations/" + lang + ".properties.  CASUAL will accept translations.  Defaulting to english. ");
        }
    }
}
