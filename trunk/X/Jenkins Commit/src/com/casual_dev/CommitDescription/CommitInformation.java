/*CommitInformation creates a Google Plus post formatted output based on information from Jenkins
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
package com.casual_dev.CommitDescription;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author adamoutler
 */
public class CommitInformation {

    final static String jenkinsXMLXpath = "https://jenkins.casual-dev.com/job/Project%20CASUAL//lastSuccessfulBuild/api/xml/?xpath=";
    //final static String jenkinsXMLXpath="https://jenkins.casual-dev.com/job/Project%20CASUAL/549/api/xml/?xpath=";

    Pattern xmlClosingAngleBracket = Pattern.compile(">");

    /**
     * runs a set of checks against last Jenkins job and outputs results. will
     * stop and output nothing if last job was manually built or not triggered
     * by CASUAL source code modification
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CommitInformation jc = new CommitInformation();
        CommitDetails cd = jc.getJenkinsInformation();
        System.out.println(cd.toString());

    }

    private CommitDetails getJenkinsInformation() {
        CommitDetails cd = new CommitDetails();
        try {
            if (!isNewRevision()) {
                System.out.println("This is a rebuild, no message sent");
                System.exit(1);
            }
            cd.culprit = getName();
            cd.revision = getRev();
            cd.files = getFiles();
            cd.message = getMessage();
            cd.isTrunk = isTrunk(cd.files);
            cd.isBranch = isBranch(cd.files);
            cd.modBranch = cd.isBranch;
            cd.isDocumentation = isDocumentation(cd.message);
            cd.isMaintenance = isMaintenance(cd.message);
            cd.modCASCADE = isCASCADE(cd.files);
            cd.modCASPACkager = isCASPACkager(cd.files);
            cd.modCASUALCore = isCASUALcore(cd.files);
            cd.modInstrumentation=isInstrumentation(cd.files);
            cd.modJodin3=isJodin3(cd.files);
        } catch (MalformedURLException ex) {
            
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("IOException");
            System.exit(1);
        }
        return cd;
    }

    private String getName() throws MalformedURLException, IOException {
        String rev = getXpath("/*/changeSet/item/author/fullName");
        return rev;
    }

    private String getRev() throws MalformedURLException, IOException {
        String rev = getXpath("/*/changeSet/item/commitId");
        return rev;
    }

    private String getXpath(String xPath) throws MalformedURLException, IOException {
        URL url = new URL(jenkinsXMLXpath + xPath);
        String page = getUrlAsString(url);
        Matcher matcher = xmlClosingAngleBracket.matcher(page);
        matcher.find();
        int x = matcher.start();
        page = page.substring(matcher.start() + 1, page.lastIndexOf("<"));

        return page;
    }

    private String getUrlAsString(URL url) throws IOException {
        InputStream urlStream = url.openStream();
        BufferedReader is = new BufferedReader(new InputStreamReader(urlStream));
        String page = "";
        String line;
        while ((line = is.readLine()) != null) {
            page = page + line + "\n";
        }

        return page;
    }

    private String[] getFiles() throws IOException {
        //"/*/changeSet[*]/item/path/file&wrapper=changes"
        String fileString = getXpath("/*/changeSet[*]/item/path/file&wrapper=changes");
        String[] fileSplit = fileString.replace("</file>", "").replaceFirst("<file>", "").split("<file>");
        return fileSplit;
    }

    private boolean isNewRevision() throws IOException {
       boolean x =getXpath("/*/action/cause/shortDescription").contains("Commanded by Revision"); 
       return x;
    }

    private boolean isTrunk(String[] files) {
        return checkArrayStartsWith(files, "/trunk/");

    }

    private boolean isBranch(String[] files) {
        return checkArrayStartsWith(files, "/branch/");
    }

    private boolean isMaintenance(String message) {
        message = message.toLowerCase();
        return message.contains("fixing") || message.contains("correcting") || message.contains("corrected") || message.contains("fixed") || message.contains("cleaning") || message.contains("revising") || message.contains("revised");
    }

    private boolean isDocumentation(String message) {
        message = message.toLowerCase();
        return message.contains("documented") || message.contains("documentation") || message.contains("javadoc");
    }

    private String getMessage() throws IOException {
        String rev = getXpath("/*/changeSet/item/msg") + ".\n";
        //remove Google Plus formatting
        rev = rev.replace("*", " ").replace("_", " ").replace("-", " ");
        //add proper punctuation because AdamOutler sucks at it
        rev = rev.replace("\n", ". ").replace("..", ".");
        return rev;
    }

    private boolean isCASCADE(String[] files) {
        String checkValue = "/trunk/CASCADE";
        return checkArrayStartsWith(files, checkValue);

    }

    private boolean checkArrayStartsWith(String[] files, String checkValue) {
        for (String file : files) {
            if (file.startsWith(checkValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCASPACkager(String[] files) {
        String checkValue = "/trunk/CASPACkager";
        return checkArrayStartsWith(files, checkValue);
    }

    private boolean isCASUALcore(String[] files) {
        String checkValue = "/trunk/CASUALcore";
        return checkArrayStartsWith(files, checkValue);
    }

    private boolean isInstrumentation(String[] files) {
                String checkValue = "/trunk/CASUALinstruments";
        return checkArrayStartsWith(files, checkValue);
    }

    private boolean isJodin3(String[] files) {
                String checkValue = "/trunk/X/JOdin3";
        return checkArrayStartsWith(files, checkValue);
    }

}
