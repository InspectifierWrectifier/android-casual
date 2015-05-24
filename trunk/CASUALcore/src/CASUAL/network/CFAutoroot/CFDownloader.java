/*Obtains Odin flashable files from CFAutoRoot
 *Copyright (C) 2015  Adam Outler <adamoutler@gmail.com>
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

package CASUAL.network.CFAutoroot;

import CASUAL.CASUALMessageObject;
import CASUAL.CASUALSessionData;
import CASUAL.archiving.Unzip;
import CASUAL.network.CASUALUpdates;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;




/**
 * Provides a process to download from CFAutoRoot and reports to the CASUALMessageObject. 
 * @author adamoutler
 */
public class CFDownloader {
    static final File[] empty = new File[]{};
    
    final CASUALSessionData sd;
            
    
    final String localCFRoot;

    public CFDownloader(CASUALSessionData sd){
        this.localCFRoot = sd.getTempFolder()+"cfautoroot.zip";
        this.sd=sd;
    }

    /**
     * returns a list of files downloaded and unzipped from CFAutoroot
     * @return a list of uncompressed files.
     * @throws NullPointerException if CASUALMessageObject does not have UI to work with.
     */

    public File[] getTarFromCFAutoRoot() {
        //get CFAutoRoot file location from deviec
        String url = getUrlFromCfAutoRoot();
        if (url.isEmpty()){
            return empty;
        }
        
        //clear out a space for the download
        File zip=new File(sd.getTempFolder()+localCFRoot);
        if (!deleteFile(zip)){
            new CASUALMessageObject("Insufficient permissions>>>We don't have permissions to remove or delete the file "+localCFRoot).showErrorDialog();
            return empty;
        }
        
        //download file from CFAutoRoot
        if (!new CASUALUpdates(sd).downloadFileFromInternet(url,localCFRoot, "Downloading CFAutoRoot")){
            return empty;
        }
        
        //verify the file exists
        if (!new File(localCFRoot).exists()){
            new CASUALMessageObject("There was a problem downloading>>We didn't receive the file. Please try again later. ").showErrorDialog();
            return empty;
        }
        ArrayList<File>odinFiles=new ArrayList<File>();
        try {
            odinFiles=new ArrayList<File>(Arrays.asList(new Unzip(localCFRoot).unzipFile(url)));
        } catch (IOException ex) {
            new CASUALMessageObject("Corrupt Zip File was received>>>A malformed zip was received from CFAutoRoot.\n\nPlease try again later.").showErrorDialog();
        }
        for (File f:odinFiles){
            if (f.getName().endsWith(".tar")||f.getName().endsWith(".gz")||f.getName().endsWith(".md5")){
                odinFiles.add(f);
            }
        }
        
        return odinFiles.toArray(new File[odinFiles.size()]);
    }

    /**
     * deletes a file. 
     * @param zip file to be deleted
     * @return true if space is available. 
     */
    private boolean deleteFile(File zip) {
        if (zip.exists()) {
            zip.delete();
        } else {
            return false;
        }
        return true;
    }

    private String getUrlFromCfAutoRoot() throws HeadlessException {
        String url="";
        try {
            url=new CASUAL.network.CFAutoroot.CFAutoRootDb(new CASUAL.communicationstools.adb.BuildProp()).returnForMyDevice();
        } catch (MalformedURLException ex) {
            new CASUALMessageObject("Invalid Link>>>A malformed URL was received from CFAutoRoot.").showErrorDialog();
        } catch (URISyntaxException ex) {
            new CASUALMessageObject("Invalid Link>>>CFAutoRoot is down, or you have specified an invalid link.").showErrorDialog();
        } catch (CFAutorootTableException ex) {
            new CASUALMessageObject("CFAutoRoot has updated>>>Unfortunately this means that the application cannot continue.\n\nPlease ask the developer for an update.  You can find contact information in the \'about\' or \'legal\' section of this app").showErrorDialog();
        } catch (IOException ex) {
            new CASUALMessageObject("Invaid file>>>The file specified was invalid or corrupt").showErrorDialog();
        }
        return url;
    }
    
   
    
}
