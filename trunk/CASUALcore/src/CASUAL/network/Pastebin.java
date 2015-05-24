/*Pastebin provides automated pastebin submisson
 * 
 *  Copyright (C) 2015  Adam Outler
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

import CASUAL.CASUALMain;
import CASUAL.CASUALMessageObject;
import CASUAL.CASUALSessionData;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.OSTools;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jeremy Loper jrloper@gmail.com

 Modified From: https://code.google.com/p/pastebin-click/ Originally released
 under the MIT license (http://www.opensource.org/licenses/mit-license.php)
 */
public class Pastebin {

    /**
     * Notice: The following users should be considered a threat to data logging
     * integrity because they do really stupid things like changing variables
     * and introducing errors in the code on a regular basis so they are hereby
     * banned from submitting any further reports to the Pastebin.
     */
    final private String[] incompetentUsers = {"adamoutler", "adam", "Jeremy", "loganludington"};
    //Pastebin User DEV API Key
    final private String devKey = "027c63663a6023d774b5392f380e5923";
    final private String user = "CASUAL-Automated";
    final private String passwd = "2J2y7SK172p46m1";
    final private String format = "text";

    /**
     * Automatically prompts the user for their XDA username and submits a
     * pasting to Pastebin
     *
     * @throws IOException when Permission problem exists
     * @throws URISyntaxException if invalid URI 
     */
    public void doPosting() throws IOException, URISyntaxException {
        if (CASUALMain.getSession().debugMode) {
            return;
        }
        String xdaUsername = new CASUALMessageObject("@interactionPastebinError").inputDialog();
        if (xdaUsername != null && !xdaUsername.equals("1")) {//CANCEL_OPTION will rerturn a null String
            API paste = new API(devKey);
            
            if (!(user.isEmpty()) && !(passwd.isEmpty())) {
                String lResult = paste.login(user, passwd);
                if (lResult.equals("false")) {
                    Log.level4Debug("Pastebin Login Failed");
                } else {
                    paste.setToken(lResult);
                    Log.level4Debug("Pastebin Login Successful");
                }
                String pasteData = new FileOperations().readFile(CASUALMain.getSession().getTempFolder() + "Log.txt");

                String output = paste.makePaste(pasteData, "CASUAL r" + java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision") + "-" + xdaUsername, format);
                if (output.substring(0, 4).equals("http")) {
                    new LinkLauncher(output).launch();
                    StringSelection stringSelection = new StringSelection(output);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    new CASUALMessageObject("@interactionThankyouForPastebin").showInformationMessage();
                    Log.level4Debug(output);
                } else {
                    Log.level4Debug(output);
                }
            }
        }
    }

    /**
     * strips user info and pastes an anonymous log to pastebin
     *
     * @throws MalformedURLException when URL cannot be reached
     */
    public void pasteAnonymousLog() throws MalformedURLException {
        Pattern svnRev = Pattern.compile("(?=[setViewedRevision]?.{2})[0-9]{3,4}");
        FileOperations fO = new FileOperations();
        if (!fO.verifyExists(CASUALMain.getSession().getTempFolder() + "Log.txt")) {
            return;
        }
        String casualLog = fO.readFile(CASUALMain.getSession().getTempFolder() + "Log.txt");
        Matcher matcher;
        try {
            matcher = svnRev.matcher(new API().getPage("http://code.google.com/p/android-casual/source/browse/"));
        } catch (NullPointerException ex) {
            return;
        }
        int SVNrev = Integer.parseInt(matcher.find() ? matcher.group(0) : "5");
        int CASRev = Integer.parseInt(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision"));
        if ((SVNrev - 5) >= CASRev && casualLog.contains("failed") || casualLog.contains("FAILED") || casualLog.contains("ERROR")) { //build.prop contains the word error on some devices so error is not a good word to track. 
            String slashrep = OSTools.isWindows() ? "\\" : "//";
            String userhome = System.getProperty("user.home");
            casualLog = casualLog.replace(userhome, slashrep + "USERHOME" + (userhome.endsWith(CASUALSessionData.slash) ? slashrep : ""));
            String username = System.getProperty("user.name");
            if (username == null || username.isEmpty()) {
                username = System.getenv("USERNAME");
            }
            if (username != null && casualLog.contains(username)) {
                casualLog = casualLog.replace(username, "USER");
            }
            if (CASUALMain.getSession().debugMode || Arrays.asList(incompetentUsers).contains(username)) {
                return; //only log results from non-devs :)
            }

            try {
                API paste = new API(devKey);
                String lResult = paste.login(user, passwd);
                if (lResult.equals("false")) {
                    return;
                } else {
                    paste.setToken(lResult);
                }
                if (casualLog.isEmpty()) {
                    return;
                }
                paste.makePaste(casualLog, "CASUAL r" + java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision") + "-Anonymous", format);
            } catch (IOException ex) {
                Log.errorHandler(ex);
            }
        }
    }

    /**
     * This is the API for Pastebin
     *
     */
    private class API {

        private String token; //used for instance
        private String devkey; //used for our program
        final private String loginURL;
        final private String pasteURL;

        private API() {
            this.pasteURL = "http://www.pastebin.com/api/api_post.php";
            this.loginURL = "http://www.pastebin.com/api/api_login.php";
        }

        private API(String devkey) {
            this.pasteURL = "http://www.pastebin.com/api/api_post.php";
            this.loginURL = "http://www.pastebin.com/api/api_login.php";
            this.devkey = devkey;
        }

        private boolean checkResponse(String response) {
            return !response.substring(0, 15).equals("Bad API request") || response.substring(17) == null;
        }

        public String login(String username, String password) throws UnsupportedEncodingException, MalformedURLException {
            String api_user_name = URLEncoder.encode(username, "UTF-8");
            String api_user_password = URLEncoder.encode(password, "UTF-8");
            String data = "api_dev_key=" + this.devkey + "&api_user_name=" + api_user_name + "&api_user_password=" + api_user_password;
            String response = this.page(this.loginURL, data);
            if (!this.checkResponse(response)) {
                return "false";
            }

            this.token = response;
            return response;
        }

        public String makePaste(String code, String name, String format) throws UnsupportedEncodingException, MalformedURLException {
            String content = URLEncoder.encode(code, "UTF-8");
            String title = URLEncoder.encode(name, "UTF-8");
            String data = "api_option=paste&api_user_key=" + this.token + "&api_paste_private=0&api_paste_name=" + title + "&api_paste_expire_date=N&api_paste_format=" + format + "&api_dev_key=" + this.devkey + "&api_paste_code=" + content;
            String response = this.page(this.pasteURL, data);
            if (!this.checkResponse(response)) {
                return response.substring(17);
            }
            return response;
        }

        public String page(String uri, String urlParameters) throws MalformedURLException {
            try {
                // Create connection
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();

                // Get Response
                InputStream is = connection.getInputStream();
                StringBuilder response;
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } catch (IOException ex) {
                Log.errorHandler(ex);
            }
            return null;
        }

        private String getPage(String uri) throws MalformedURLException {
            try {
                // Create connection
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(5000);
                connection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                connection.addRequestProperty("User-Agent", "Mozilla");
                StringBuffer html;
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                html = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    html.append(inputLine);
                }

                return html.toString();
            } catch (IOException ex) {
                Log.errorHandler(ex);
            }
            return null;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
