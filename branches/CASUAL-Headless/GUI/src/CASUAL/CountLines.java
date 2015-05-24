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

/**
 *
 * @author adam
 */
public class CountLines {
     public int countFileLines(String Filename) {
        InputStream IS = null;
        int Lines = 0;
        try {
            IS = new BufferedInputStream(new FileInputStream(Filename));

            Lines = countISLines(IS);


        } catch (FileNotFoundException ex) {
            Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                IS.close();
            } catch (IOException ex) {
                Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return Lines;

    }

    public int countResourceLines(String ResourceName) {
        InputStream IS = getClass().getResourceAsStream(Statics.ScriptLocation + ResourceName + ".scr");
        int Lines = 0;
        try {
            Lines = countISLines(IS);
        } catch (IOException ex) {
            Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                IS.close();
            } catch (IOException ex) {
                Logger.getLogger(CASUALScriptParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Lines;
    }

    private int countISLines(InputStream IS) throws IOException {
        int count = 0;
        try {
            byte[] c = new byte[1024];
            int ReadChars = 0;
            while ((ReadChars = IS.read(c)) != -1) {
                for (int i = 0; i < ReadChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
        } finally {
            IS.close();
        }

        return count + 1;

    }
}
