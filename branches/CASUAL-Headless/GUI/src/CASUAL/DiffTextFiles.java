/*
 * Copyright (c) 2012 Logan Ludington
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

/**
 *
 * @author LogansALIEN
 */
public class DiffTextFiles {

    /*
     * takes a resource and a string
     * outputs difference as a string
     */
    public String diffResourceVersusFile(String TestIStream, String OriginalFile) {

        String Difference = "";
        InputStream ResourceAsStream = getClass().getResourceAsStream(TestIStream);
        BufferedReader TestStream = new BufferedReader(new InputStreamReader(ResourceAsStream));
        File Original = new File(OriginalFile);
        String TestStreamLine = "";
        String OriginalFileLine;
        try {
            while ((TestStreamLine = TestStream.readLine()) != null) {
                boolean LineExists = false;
                BufferedReader OriginalReader = new BufferedReader(new FileReader(Original));
                while ((OriginalFileLine = OriginalReader.readLine()) != null) {
                    if (OriginalFileLine.equals(TestStreamLine)) {
                        LineExists = true;
                    }
                }
                if (!LineExists) {
                    Difference = Difference + "\n" + TestStreamLine;
                }
            }
        } catch (IOException ex) {

            Difference = TestStreamLine + "\n";
            try {
                while ((TestStreamLine = TestStream.readLine()) != null) {
                    Difference = Difference + TestStreamLine + "\n";
                }
            } catch (IOException ex1) {
                Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex1);
            }


        }
        if (Difference.startsWith("\n")) {
            Difference = Difference.replaceFirst("\n", "");
        }
        if (Difference.endsWith("\n")){
            Difference=new StringOperations().replaceLast(Difference, "\n", "");
        }
            
        return Difference;

    }

    /*
     * takes two files
     * returns the difference between the two
     */
    public String diffTextFiles(String Original, String TestForDiff) {
        String DifferenceFromFile1 = "";
        try {
            BufferedReader BRTestDiff = new BufferedReader(new FileReader(TestForDiff));
            try {

                String Line;
                String Line2;
                while ((Line = BRTestDiff.readLine()) != null) {
                    ;
                    BufferedReader BROriginal = new BufferedReader(new FileReader(Original));
                    try {
                        boolean LineExists = false;
                        while ((Line2 = BROriginal.readLine()) != null) {
                            if (Line2.equals(Line)) {
                                LineExists = true;
                            }
                        }
                        if (!LineExists) {
                            DifferenceFromFile1 = DifferenceFromFile1 + "\n" + Line;
                        }

                    } finally {
                        BROriginal.close();
                    }
                }
            } finally {
                BRTestDiff.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DifferenceFromFile1;
    }
    //Takes in the Diff from the constructor and writes it to the file that is 
    //iniFile.

    /*
     * appends text to a file
     */
    public void appendDiffToFile(String NameOfFileToBeModified, String Diff) {
        if (Diff.equals("")){
            return;
        }
        String currentString;
        FileOutputStream FileOut = null;
        File FileToModify = new File(NameOfFileToBeModified);
        if (!FileToModify.exists()) {
            try {
                FileToModify.mkdirs();
                if (FileToModify.isDirectory()){
                    FileToModify.delete();
                }
                FileToModify.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        FileReader FR;
        try {
            FR = new FileReader(FileToModify);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        BufferedReader OriginalFileBuffer = new BufferedReader(FR);
        try {
            FileOut = new FileOutputStream(NameOfFileToBeModified + "_new");
            while ((currentString = OriginalFileBuffer.readLine()) != null) {
                new PrintStream(FileOut).println(currentString);
            }
            new PrintStream(FileOut).println(Diff);
        } catch (IOException ex) {
            Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                OriginalFileBuffer.close();
                File OutputFile = FileToModify;
                OutputFile.delete();
                OutputFile = new File(NameOfFileToBeModified + "_new");
                OutputFile.renameTo(new File(NameOfFileToBeModified).getAbsoluteFile());
            } catch (IOException ex) {
                Logger.getLogger(DiffTextFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
