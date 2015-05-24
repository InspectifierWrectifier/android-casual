/*CASUALAudioSystem provides audio output for CASUAL. 
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

import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * CASUALAudioSystem handles Sounds
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class AudioHandler {

    /**
     * True if sound is to be used.
     */
    public static volatile boolean useSound = false;

    /**
     * playSound plays sounds
     *
     * @param url path to sound
     */
    public static synchronized void playSound(final String url) {
        Thread t = new Thread(new Runnable() { // the wrapper thread is unnecessary, unless it blocks on the Clip finishing
            @Override
            public void run() {
                if (useSound) {
                    AudioInputStream is;
                    try {
                        byte[] buffer = new byte[4096];
                        is = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(url)));
                        AudioFormat Format = is.getFormat();
                        SourceDataLine line;
                        line = AudioSystem.getSourceDataLine(Format);
                        line.open(Format);
                        line.start();
                        while (is.available() > 0) {
                            int Len = is.read(buffer);
                            line.write(buffer, 0, Len);
                        }
                        line.drain();
                        line.close();
                        is.close();
                        //Don't worry about autio exceptions.  Just turn off audio
                        } catch (IOException error) {
                            Log.level4Debug("Audio File not found");
                            Log.level3Verbose("File Not Found");
                        } catch (UnsupportedAudioFileException ex) {
                            Log.level4Debug("Audio Unsupported Format Exception throw");
                            useSound = false;
                        } catch (LineUnavailableException ex) {
                            Log.level4Debug("Audio Line Unavailable Exception thrown");
                            useSound = false;
                        } catch (java.lang.IllegalArgumentException ex) {
                            Log.level4Debug("Audio Illegal Arguement Exception thrown");
                            useSound = false;
                        }
                }
            }
        });
        t.setName("Audio");
        t.start();
    }

    /**
     * plays multiple sounds
     *
     * @param urls array of paths to sound
     */
    public static synchronized void playMultipleInputStreams(final String[] urls) {
        Thread t;
        t = new Thread(new Runnable() {
// the wrapper thread is unnecessary, unless it blocks on the Clip finishing
            @Override
            public void run() {
                if (useSound) {

                    for (String url : urls) {

                        try {
                            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(url));

                            AudioFormat format = audioIn.getFormat();
                            DataLine.Info info = new DataLine.Info(Clip.class, format);
                            Clip clip = (Clip) AudioSystem.getLine(info);

                            clip.open(audioIn);
                            clip.start();
                            sleepTillEndOfClip(clip);
                            clip.drain();
                        } catch (IOException error) {
                            Log.level4Debug("Audio File not found");
                            Log.level3Verbose("File Not Found");
                        } catch (UnsupportedAudioFileException ex) {
                            Log.level4Debug("Audio Unsupported Format Exception throw");
                            useSound = false;
                        } catch (LineUnavailableException ex) {
                            Log.level4Debug("Audio Line Unavailable Exception thrown");
                            useSound = false;
                        } catch (java.lang.IllegalArgumentException ex) {
                            Log.level4Debug("Audio Illegal Arguement Exception thrown");
                            useSound = false;
                        } catch (InterruptedException ex) {
                            Log.level4Debug("Audio Interrupted Exception thrown");
                            useSound = false;
                        }
                    }
                }

            }

            private void sleepTillEndOfClip(Clip clip) throws InterruptedException {
                Thread.sleep(clip.getMicrosecondLength()/5000);
            }
        });
        t.setName("AudioStream");
        
        t.start();

    }
}
