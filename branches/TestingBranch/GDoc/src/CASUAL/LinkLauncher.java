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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author adam
 */
public class LinkLauncher implements Runnable  {
    static String Link;
    /*
     * launches a browser with a link
     */
    public void launchLink(String link){
        Link = link;
        Runnable runnable = new LinkLauncher();
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void run() {
           
        Shell Shell = new Shell();
        String Cmd[]={"firefox", Link};
        String LaunchRes=Shell.sendShellCommand(Cmd);
        if (LaunchRes.contains("CritERROR!!!")){
            String MCmd[]={"open" , Link};
            String MLaunchRes=Shell.sendShellCommand(MCmd);
            if (MLaunchRes.contains("CritERROR!!!")){
                String WCmd[]={"explorer", Link};
                String WLaunchRes=Shell.sendShellCommand(WCmd);
                if (WLaunchRes.contains("CritERROR!!!")){
                     if (Desktop.isDesktopSupported()) {
                        Desktop desktop;
                        desktop = Desktop.getDesktop();
                        URI uri = null;
                        try {
                            uri = new URI(Link);
                             desktop.browse(uri);
                        } catch (IOException ioe) {
                            System.out.println("Attempted to open"+ Link+" Failed with IO error");
                        } catch (URISyntaxException use) {
                            System.out.println("Attempted to open"+ Link+" Failed with URI Syntax error");
                        }
                     }
                }
            }
        }
    }
}
    

