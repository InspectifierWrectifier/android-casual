/*
 * Copyright (C) 2014 adamoutler
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author adamoutler
 */
public class HttpPost {


    
    
    public static String postString(String s, String url) throws IOException {
        return postStream(convertStringToStream(s),url);
    }
    
    public static String postFile(File f, String url) throws FileNotFoundException, IOException {
        FileInputStream fis=new FileInputStream (f);
        

        return postStream(fis,url);
    }

    private static String postStream( InputStream fis,String url) throws IOException, MalformedURLException {
        HttpPost post = new HttpPost();
        URL u = new URL(url);
        
        //open the connection
        URLConnection uc = u.openConnection();
        uc.setDoOutput(true);
        uc.setDoInput(true);
        uc.setAllowUserInteraction(false);
        DataOutputStream dis = new DataOutputStream(uc.getOutputStream());
        
        // Flood data into the connection
        byte[] buffer=new byte[1024];
        while (fis.available()>1){
            fis.read(buffer);
            dis.write(buffer);
        }
        dis.write(buffer);
        dis.close();
        
        // Read Response
        
        BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
        String buf = convertStreamToString(in);
        in.close();
        return buf;
    }
    
        /**
     * converts a String to an InputStream
     * @param input string to turn into an InputStream
     * @return InputStream representation of the input string. 
     */
    public static InputStream convertStringToStream(String input) {
        InputStream bas = new ByteArrayInputStream(input.getBytes());
        return bas;


    }
        /**
     * reads a stream and returns a string
     *
     * @param is stream to read
     * @return stream converted to string
     */
    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
