/*
 * Copyright (C) 2015 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/ .
 */
package CASUAL.network;

import CASUAL.CASUALScriptParser;
import CASUAL.CASUALSessionData;
import CASUAL.caspac.Caspac;
import CASUAL.crypto.SHA256sum;
import CASUAL.network.CASUALDevIntegration.CASUALPackage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author adamoutler
 */
public class RemoteCASPACHandler {

    final static String repository = "https://builds.casual-dev.com/files/CASPAC/";

    public String executeCaspac(CASUALSessionData sd, String urlPath) throws FileNotFoundException, IOException {
        File  downloaded= downloadFromRemoteRepository(sd.getTempFolder(),urlPath);
        Caspac caspac=new Caspac(sd,downloaded);
        sd.CASPAC=caspac;
        caspac.loadFirstScriptFromCASPAC();
        new CASUALScriptParser().loadFileAndExecute(caspac, false);
        return "";
    }
    
    
    
    
    
    
    
    public File downloadFromRemoteRepository(String tempFolder, String urlPath) throws MalformedURLException, FileNotFoundException, IOException {
        URL caspacURL = new URL(repository + urlPath);
        String localCaspac=tempFolder+caspacURL.getFile();
        CASUALPackage cp=new CASUALPackage(repository + urlPath);
        System.out.println(cp.toString());
        downloadFileFromURLToFolder(caspacURL,localCaspac);
        String downloadedSha;
        try {
              SHA256sum sha256= new SHA256sum(new File(localCaspac));
          
            if (!sha256.getSha256().equals(cp.getCaspacSHA256sum())){
                return null;
            }
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
          return new File(localCaspac);
    }

    private void downloadFileFromURLToFolder(URL url, String tempFile) throws FileNotFoundException, IOException {
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        File f=new File(tempFile);
        f.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(f);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    private String getPropertiesURL(String input) {
            input=input.substring(0, input.lastIndexOf("."))+"properties";
            return input;
    }

}
