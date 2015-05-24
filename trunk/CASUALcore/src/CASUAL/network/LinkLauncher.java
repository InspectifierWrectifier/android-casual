/*LinkLauncher launches URLs on various platforms. 
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
package CASUAL.network;

import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.Shell;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Launches URLs on various platforms.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class LinkLauncher {

    final String link;

    final private Runnable launcher = new Runnable() {
        @Override
        public void run() {
            //use java to autolaunch if possible
            if (Desktop.isDesktopSupported()) {
                Desktop desktop;
                desktop = Desktop.getDesktop();
                URI uri;
                try {
                    uri = new URI(link);
                    desktop.browse(uri);
                } catch (IOException ioe) {
                    Log.level4Debug("Attempted to open" + link + " Failed with IO error");
                } catch (URISyntaxException use) {
                    Log.level4Debug("Attempted to open" + link + " Failed with URI Syntax error");

                }
            } else {
                Shell Shell = new Shell();
                if (OSTools.isMac()) {
                    //separate mac because open is used on different platforms
                    Shell.sendShellCommand(new String[]{"open", link});
                } else {
                    //launch link with firefox
                    String retval = Shell.sendShellCommand(new String[]{"firefox", link});
                    if (retval.contains("CritERROR!!!")) {
                        //launch link with Chrome
                        Shell.sendShellCommand(new String[]{"chrome", link});
                        if (retval.contains("CritERROR!!!")) {
                            //launch link with explorer
                            Shell.sendShellCommand(new String[]{"explorer", link});
                        }
                    }
                }

            }
        }
    };

    /**
     * launches a browser with a link
     *
     * @param link link to launch
     */
    public LinkLauncher(String link) {
        this.link = link;
    }

    /**
     * launches the link commanded in constructor
     */
    public void launch() {
        Thread thread = new Thread(launcher);
        thread.setName("Link Launcher Thread");
        thread.start();
    }
}
