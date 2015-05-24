/*CASUALUpdates provides a way to check and update CASUAL
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

import CASUAL.CASUALMessageObject;
import CASUAL.CASUALSessionData;
import CASUAL.CASUALTools;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.OSTools;
import CASUAL.caspac.Script;
import CASUAL.crypto.MD5sum;
import CASUAL.misc.StringOperations;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;
import java.util.zip.ZipException;

/**
 * provides a way to check and update CASUAL
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALUpdates {
    /*
     * String Properties File
     * Returns location of first downloaded file
     */
    static String arch = "";
    static String system = "";

    final CASUALSessionData sd;
    
    
    /**
     * location to CASUAL online repository
     */
    public final String CASUALRepo = "http://android-casual.googlecode.com/svn/trunk/CASUALcore/src";

    public CASUALUpdates( CASUALSessionData sd) {
        this.sd=sd;
    }

    /*
     * checks for updates returns: 0=no updates found 1=random error 2=Script
     * Update Required 3=CASUAL update required- cannot continue. 4=download
     * failed
     */
    /**
     * downloads a file
     *
     * @param URL web location to download
     * @param outputFile the local file to output
     * @param friendlyName name displayed to user
     * @return true if downloaded
     */
    public boolean downloadFileFromInternet(String URL, String outputFile, String friendlyName) {
        try {
            downloadFileFromInternet(stringToFormattedURL(URL), outputFile, friendlyName);
        } catch (MalformedURLException ex) {
            Log.errorHandler(ex);
        } catch (URISyntaxException ex) {
            Log.errorHandler(ex);
        }
        return true;
    }

    /**
     *
     * downloads a file
     *
     * @param url web location to download
     * @param outputFile the local file to output
     * @param friendlyName name displayed to user
     * @return true if downloaded
     */
    public boolean downloadFileFromInternet(URL url, String outputFile, String friendlyName) {
        Log.progress("Downloading ");
        Log.level4Debug("Downloading " + url);
        Log.level4Debug("To: " + outputFile);
        InputStream input;
        try {

            input = url.openStream();
            byte[] buffer = new byte[4096];
            File f = new File(outputFile);
            f.getParentFile().mkdirs();
            OutputStream output = new FileOutputStream(f);
            long bytes = 0;
            Log.progress(friendlyName.replace("/SCRIPTS/", ""));
            int lastlength = 0;
            long kilobytes;
            int offset = 1;

            try {
                int bytesRead;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                    bytes += bytesRead;
                    kilobytes = bytes / 1024;
                    int length = String.valueOf(kilobytes).length() + 2;
                    for (int i = 0; i < length; i++) {
                        Log.progress("\b");  //backspace over the old value
                    }
                    Log.progress(Long.toString(kilobytes) + "kb "); //write new

                }
            } finally {
                output.flush();
                output.close();

            }
        } catch (IOException ex) {
            Log.level4Debug("Error Downloading " + ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * displays data from a split meta file
     *
     * @param CASUALString meta information to be displayed 0-id 1-revsion
     * 2-minimum svn 3-support URL 4-update message
     */
    public void displayCASUALString(String[] CASUALString) {
        //SVN Revision, Script Revision, Script Identification, support URL, message to user
        Log.level4Debug("Identification: " + CASUALString[0]);
        Log.level4Debug("ScriptRevision: " + CASUALString[1]);
        Log.level4Debug("CASUALRevision: " + CASUALString[2]);
        Log.level4Debug("URL: " + CASUALString[3]);
        Log.level4Debug("Server Message: " + CASUALString[4]);
    }

    /**
     * converts a string to a URL
     *
     * @param stringURL raw URL in string format
     * @return URL formatted properly
     * @throws MalformedURLException if URL is corrupt
     * @throws URISyntaxException if URI is corrupt
     */
    public URL stringToFormattedURL(String stringURL) throws MalformedURLException, URISyntaxException {
        URL url = new URL(stringURL);
        url = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef()).toURL();
        return url;
    }

    /**
     * gets the data from the online script
     *
     * @param script Script to pull online metadata for
     * @return String representation of the metadata for the script.
     * @throws MalformedURLException url is corrupt
     * @throws IOException permission problem
     * @throws URISyntaxException uri is corrupt
     */
    public String getWebData(String script) throws MalformedURLException, IOException, URISyntaxException {
        URL url = stringToFormattedURL(script);
        String webData;
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        ByteBuffer buf = ByteBuffer.allocateDirect(10);
        webData = "";
        int numRead = 0;
        while (numRead >= 0) {
            buf.rewind();
            numRead = rbc.read(buf);
            buf.rewind();
            for (int i = 0; i < numRead; i++) {
                byte b = buf.get();
                webData += new String(new byte[]{b});
            }
        }

        return webData;
    }

    /**
     * downloads metadata for the current script from the repository.
     *
     * @param s Script for information to be pulled
     * @return InputStream representing a properties file with metadata about
     * online version of the script.
     * @throws MalformedURLException URL/URI is corrupt
     * @throws URISyntaxException URL/URI is corrupt
     * @throws IOException permission problem
     */
    public InputStream downloadMetaFromRepoForScript(Script s) throws MalformedURLException, URISyntaxException, IOException {
        URL url;
        String parentFolder = new File(s.getTempDir()).getParent() + "/";
        String meta = s.getName() + ".meta";
        if (CASUALTools.IDEMode) {
            url = stringToFormattedURL(CASUALRepo + "/SCRIPTS/" + meta);
        } else {
            Log.level3Verbose(CASUALRepo + meta);
            url = stringToFormattedURL(CASUALRepo + meta);
            System.out.println(url.toString());
        }
        Log.level3Verbose("opening download stream");

        URLConnection con = url.openConnection();
        con.setConnectTimeout(300);
        con.setReadTimeout(300);

        return con.getInputStream();

    }

    /**
     * Opens a file from a URL as a stream
     *
     * @param link String representation of URL
     * @return InputStream object to file.
     * @throws MalformedURLException URL/URI is corrupt
     * @throws URISyntaxException URL/URI is corrupt
     * @throws IOException permission problem
     */
    public InputStream streamFileFromNet(String link) throws MalformedURLException, URISyntaxException, IOException {
        URL url = new URL(link);
        return url.openStream();
    }

    /**
     * downloads proper file if available in repository
     *
     * @param propertiesFileInCASUALOnlineRepo requested file to be downloaded
     * ie -"heimdall" will be translated to web url:heimdallWin32.zip,
     * downloaded and the path to the downloaded file is returned.
     * @return file downloaded for system/arch
     * @throws FileNotFoundException file not found
     * @throws IOException permission problem 
     * @throws InterruptedException thread interrupted
     */
    public String CASUALRepoDownload(String propertiesFileInCASUALOnlineRepo) throws FileNotFoundException, IOException, InterruptedException {
        arch = OSTools.is64bitSystem() ? "64" : "32";
        system = OSTools.isWindows() ? "win" : system;
        system = OSTools.isLinux() ? "linux" : system;
        system = OSTools.isMac() ? "mac" : system;
        Log.level3Verbose("Found " + system + " " + arch + "computer");
        String basename = new File(propertiesFileInCASUALOnlineRepo).getName();
        //download location, md5, and version information
        downloadFileFromInternet(propertiesFileInCASUALOnlineRepo, sd.getTempFolder() + basename, "locating files");
        Log.level3Verbose("downloaded" + propertiesFileInCASUALOnlineRepo);
        //Set properties file
        Properties prop = new Properties();
        prop.load(new FileInputStream(sd.getTempFolder() + basename));
        // get information from properties file
        int counter = 1;
        String filenumber = "";

        /*
        * This loop uses the filenumber as a blank the first time through
        * after that filenumber turns to "2", so it will look for
        * eg. "win32" property then "win32-2" property
        *
        * It will download the applicable files in the properties file. then
        * MD5sum against the value in the properties file.
        */
        while (prop.getProperty(system + arch + filenumber) != null) {
            String downloadURL = prop.getProperty(system + arch + filenumber);
            Log.level3Verbose("based on information, we need to download: " + downloadURL);

            String downloadBasename = downloadURL.substring(downloadURL.lastIndexOf('/') + 1, downloadURL.length());
            String availableVersion = prop.getProperty(system + arch + filenumber + "Version");
            String downloadedFile = sd.getTempFolder() + downloadBasename;
            //download update based on information available.

            downloadFileFromInternet(downloadURL, downloadedFile, downloadBasename + " ver" + availableVersion);

            //get expected MD5
            String expectedMD5 = new MD5sum().getMD5fromLinuxMD5String(prop.getProperty(system + arch + "md5"));
            //verify  we have an MD5
            //if MD5 does not match
            if (expectedMD5.length() >= 31 && !new MD5sum().compareFileToMD5(new File(downloadedFile), expectedMD5)) {
                //show message and exit
                new CASUALMessageObject("@interactionBadDownload").showErrorDialog();
                CASUAL.CASUALMain.shutdown(0);
            }
            counter++;
            filenumber = "-" + Integer.toString(counter);
        }
        String downloadURL = prop.getProperty(system + arch);
        String downloadBasename = new File(downloadURL).getName();
        return sd.getTempFolder() + downloadBasename;

    }

    /**
     * Updates a script. Uses online repository specified in sd.
     *
     * @param script CASUAL.caspac.Script object to be updated
     * @param tempFolder Temp Folder to use for updating.
     * @return New, updated script.
     * @throws ZipException zip corrupted
     * @throws IOException permission problem 
     * @throws MalformedURLException  url/uri malformed
     * @throws URISyntaxException url/uri malformed
     */
    public Script updateScript(Script script, String tempFolder) throws ZipException, IOException, MalformedURLException, URISyntaxException {
        MD5sum md5sum = new MD5sum();

        for (String md5 : script.getMetaData().getMd5s()) {
            FileOperations fo = new FileOperations();
            String targetFilename = md5sum.getFileNamefromLinuxMD5String(md5);
            URL url;
            url = stringToFormattedURL(CASUALRepo + "/SCRIPTS/" + targetFilename);
            System.out.println(url);

            String localFilename = tempFolder + targetFilename;

            if (targetFilename.endsWith(".scr")) {
                script.setScriptContents(StringOperations.convertStreamToString(url.openStream()));
                script.getActualMD5s().add(md5sum.getLinuxMD5Sum(script.getScriptContents(), targetFilename));
            } else if (targetFilename.endsWith(".txt")) {
                script.setDiscription(StringOperations.convertStreamToString(url.openStream()));
                script.getActualMD5s().add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(script.getDiscription()), targetFilename));
            } else if (targetFilename.endsWith(".zip")) {
                this.downloadFileFromInternet(url, localFilename, targetFilename);
                script.scriptZipFile = localFilename;
                //MD5 is performed during unzip and checked at that time. 
            }

        }
        return script;
    }

    public int tryGetFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            System.out.println(conn.getReadTimeout());
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.level0Error("@problemDownlaodingFile");
                    new CASUALMessageObject("@interactionBadDownload").showErrorDialog();
                }
            });
            return -1;
        } finally {
            if (null!=conn){
                conn.disconnect();
            }
        }
    }
}
