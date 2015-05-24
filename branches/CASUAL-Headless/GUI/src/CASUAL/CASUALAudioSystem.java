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


import java.io.BufferedInputStream;
import javax.sound.sampled.*;

/**
 * CASUALAudioSystem handles Sounds
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALAudioSystem {

    /*
     * playSound plays sounds
     */
    public static synchronized void playSound(final String URL) {
        new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing, see comments

            public void run() {
                if (Statics.UseSound.contains("true")||Statics.UseSound.contains("True")){
                try {
                    byte[] buffer = new byte[4096];
                    AudioInputStream IS = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(URL)));
                    AudioFormat Format = IS.getFormat();
                    SourceDataLine Line = AudioSystem.getSourceDataLine(Format);
                    Line.open(Format);
                    Line.start();
                    while (IS.available() > 0) {
                        int Len = IS.read(buffer);
                        Line.write(buffer, 0, Len);
                    }
                    Line.drain();
                    Line.close();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
            }
        }).start();
    }
    /*
     * plays multiple sounds
     */

    public static synchronized void playMultipleInputStreams(final String[] URLs) {
        new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing, see comments

            public void run() {
                if (Statics.UseSound.contains("true")||Statics.UseSound.contains("True")){
                byte[] buffer = new byte[4096];
                int URLEndPosition=URLs.length - 1;
                int CurrentURL=0;
                SourceDataLine Line=null;
                for (String URL : URLs) {
                    try {
                        AudioInputStream IS = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(URL)));
                        AudioFormat Format = IS.getFormat();
                        Line= AudioSystem.getSourceDataLine(Format);
                        
                        Line.open(Format);
                        Line.start();
                        Line.drain();
                        while (IS.available() > 0) {
                            int Len = IS.read(buffer);
                            Line.write(buffer, 0, Len);
                        };
                        if (CurrentURL==URLEndPosition) Line.drain(); // wait for the buffer to empty before closing the line
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                  CurrentURL=CurrentURL++;  
                }
                Line.close();

            }
            }
        }).start();
    }
}
