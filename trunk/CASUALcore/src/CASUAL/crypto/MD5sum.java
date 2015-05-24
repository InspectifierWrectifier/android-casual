/*MD5sum provides MD5 tools for use in CASUAL
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
package CASUAL.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/*
 * Inspired by R.J. Lorimer http://www.javalobby.org/java/forums/t84420.html
 */
/**
 *MD5sum provides MD5 tools for use in CASUAL
 * @author Adam Outler adamoutler@gmail.com
 */
public class MD5sum {

    /**
     * compares an MD5 to a file
     *
     * @param f file to be compared
     * @param MD5 expected MD5
     * @return true if matches
     */
    public boolean compareFileToMD5(File f, String MD5) {
        return md5sum(f).equals(MD5.toLowerCase());
    }

    /**
     * gets MD5 of input stream
     *
     * @param is stream to be MD5d
     * @return md5 of stream
     */
    public String md5sum(InputStream is) {
        return md5sumStream(is);
    }

    /**
     * MD5s a file
     *
     * @param f file to MD5
     * @return md5 of file
     */
    public String md5sum(File f) {
        InputStream is;
        try {
            is = new FileInputStream(f);
            return md5sumStream(is);
        } catch (FileNotFoundException ex) {
            return "ERROR0FileNotFoundException00000";
        }
    }

    /**
     * md5s an input stream
     *
     * @param is stream to MD5
     * @return md5 of stream
     */
    public String md5sumStream(InputStream is) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[8192];
            int read;
            try {
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                BigInteger bigInt = new BigInteger(1, md5sum);
                String output = bigInt.toString(16);
                while (output.length() != 32) {
                    output = "0" + output;
                }
                return output;

            } finally {
                if (is != null) {
                    is.close();
                }
            }

        } catch (NoSuchAlgorithmException ex) {
            return "ERROR0NoSuchAlgorythemException0";

        } catch (IOException ex) {
            return "ERROR00IOException00000000000000";
        }

    }

    /**
     * compares files to MD5 to verify at least one matches
     *
     * @param LinuxFormat MD5 to compare
     * @param MD5Filenames filenames to be checked
     * @return true if all MD5s were matched to files
     */
    public boolean compareMD5StringsFromLinuxFormatToFilenames(String[] LinuxFormat, String[] MD5Filenames) {
        String[][] FilenamesAndMD5 = splitFilenamesAndMD5(LinuxFormat);
        boolean[] matches = new boolean[MD5Filenames.length];
        for (int n = 0; n < MD5Filenames.length; n++) { //loop through files
            matches[n] = false; //set match as false by default
            String md5 = md5sum(new File(MD5Filenames[n]));// get MD5 for current file
            for (int nn = 0; nn < FilenamesAndMD5.length; nn++) { //find MD5 in lookup table
                if (md5.length() != 32) { //if md5 is found while looping through lookup table set match true
                    matches[n] = true;
                } else if (md5.equals(FilenamesAndMD5[nn][0])) { //or if it is not an actual MD5 set as true;
                    matches[n] = true;
                }

            }


        }
        for (int n = 0; n < matches.length; n++) { //loop through all values
            if (matches[n] == false) {
                return false; //if all values don't match, return false
            }
        }

        return true;
    }

    private String[][] splitFilenamesAndMD5(String[] idStrings) {
        final int ROWS = idStrings.length;
        int COLUMNS = 2;
        final String[][] NameMD5 = new String[ROWS][COLUMNS];
        for (int n = 0; n < ROWS; n++) {
            if (idStrings[n].contains("  ")) {
                String[] splitID = idStrings[n].split("  ");
                if (splitID.length == 2) {
                    if (splitID[0] != null && splitID[1] != null) {
                        NameMD5[n][0] = splitID[0];
                        NameMD5[n][1] = splitID[1];
                        //this is a valid MD5 split
                    } else {
                        //spoof empty string
                        NameMD5[n][0] = "";
                        NameMD5[n][1] = "";
                    }

                } else {
                    //spoof empty string;
                    NameMD5[n][0] = "";
                    NameMD5[n][1] = "";
                }
            }
        }
        return NameMD5;
    }

    /**
     * MD5Sums a file
     * @param string representation of path to file on hard disk. 
     * @return MD5Sum of file. 
     */
    public String md5sum(String string) {
        return md5sum(new File(string));

    }

    /**
     * splits a Linux MD5sum value into a md5 and a filename
     *
     * @param md5 linux md5 sum
     * @return md5,filename
     */
    public String[] splitMD5String(String md5) {
        String[] retval = md5.split("  ");
        return retval;
    }

    /**
     * returns MD5 from a linux md5sum
     *
     * @param md5 linux MD5sum
     * @return md5
     */
    public String getMD5fromLinuxMD5String(String md5) {
        return splitMD5String(md5)[0];
    }

    /**
     * Returns the standard md5sum found on Linux as though the file were in the
     * same directory
     *
     * @param file to be MD5sum'd
     * @return 32digitMd5Sum+" "+file.Name
     */
    public String getLinuxMD5Sum(File file) {
        String md5 = this.md5sum(file);
        String filename = file.getName();
        return convertMD5andFiletoLinuxMD5Sum(md5, filename);


    }

    /**
     * Returns the standard md5sum found on Linux as though the file were in the
     * same directory
     *
     * @param stream to be MD5sum'd
     * @param filename to append to the stream
     * @return 32digitMd5Sum+" "+file.Name
     */
    public String getLinuxMD5Sum(InputStream stream, String filename) {
        String md5 = this.md5sum(stream);
        return convertMD5andFiletoLinuxMD5Sum(md5, filename);



    }

    /**
     * gets filename from linux md5sum
     *
     * @param md5 linux md5sum
     * @return filename of md5sum input
     */
    public String getFileNamefromLinuxMD5String(String md5) {
        String[] s = splitMD5String(md5);
        if (s.length > 0) {
            return s[1];
        }
        return s[0];
    }

    /**
     * returns a standard md5sum
     *
     * @param md5 md5 to be inserted
     * @param filename filename to be appended
     * @return linux md5sum
     */
    public String convertMD5andFiletoLinuxMD5Sum(String md5, String filename) {
        return md5 + "  " + filename;
    }

    /**
     * tests to see if a line matches an md5sum
     *
     * @param testLine line in question
     * @return true if this is contains a 32 byte hex string
     */
    public boolean lineContainsMD5(String testLine) {
        boolean x = testLine.matches("([0-9a-f]{32}([\\s\\S]*))");
        return x;
    }

    /**
     * picks the new MD5 if available
     *
     * @param newMd5List new MD5s
     * @param OldMD5 old md5s
     * @return new md5s if available
     */
    public String pickNewMD5fromArrayList(ArrayList<String> newMd5List, String OldMD5) {
        String[] md5FileSplit = OldMD5.split("  ");
        for (Object item : newMd5List.toArray()) {
            if (((String) item).endsWith(md5FileSplit[1])) {
                return (String) item;
            }

        }
        return OldMD5;
    }
}
