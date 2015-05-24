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

/*CASUALDevQuerier queries the file-system at casual-dev.com for items pertaining to the provided build.prop 
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
package CASUAL.network.CASUALDevIntegration;

import CASUAL.communicationstools.adb.BuildProp;
import CASUAL.network.HttpPost;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * queries the file-system at casual-dev.com for items pertaining to the
 * provided build.prop
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALDevQuerier2 {

    final static String buildsURL = "https://builds.casual-dev.com/availableCaspacs/CASUALComms.php";
    final static String rootPath = "https://builds.casual-dev.com";

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    final String buildProp;

    /**
     * accepts a build prop in CASUAL's BuildProp format
     *
     * @param bp BuildProp to be sent to server
     * @param additionalProps additional lines of properties to claim as valid
     */
    public CASUALDevQuerier2(BuildProp bp, String[] additionalProps) {
        buildProp = addAdditionalPropsToBuildProp(bp.toString(), additionalProps);
    }

    /**
     * accepts a build prop in file format
     *
     * @param bp BuildProp to be sent to server
     * @param additionalProps additional lines of properties to claim as valid
     * @throws FileNotFoundException when the build prop is not found
     */
    public CASUALDevQuerier2(File bp, String[] additionalProps) throws FileNotFoundException {
        buildProp = addAdditionalPropsToBuildProp(convertStreamToString(new FileInputStream(bp)), additionalProps);
    }

    /**
     * accepts a buildprop in string format
     *
     * @param bp BuildProp to be sent to server
     * @param additionalProps additional lines of properties to claim as valid
     */
    public CASUALDevQuerier2(String bp, String[] additionalProps) {
        addAdditionalPropsToBuildProp(bp, additionalProps);
        this.buildProp = bp;
    }

    private String addAdditionalPropsToBuildProp(String bp, String[] additionalProps) {
        for (String prop : additionalProps) {
            bp = bp + "\n" + prop;
        }
        return bp;
    }

    /**
     * gets an array of CASUALPackages available for this query on the build
     * prop
     *
     * @return casualpackages array
     */
    public CASUALPackage[] getPackages() {
        List<CASUALPackage> cp = getPackagesList();
        return cp.toArray(new CASUALPackage[cp.size()]);
    }

    /**
     * gets a list of URLs available for this query on the build prop
     *
     * @return String array of available URLs.
     */
    public String[] getPackagesString() {
        CASUALPackage[] packs = getPackages();
        ArrayList<String> stringPacks = new ArrayList<String>();
        for (CASUALPackage pack : packs) {
            stringPacks.add(pack.url);
        }
        return stringPacks.toArray(new String[stringPacks.size()]);
    }

    /**
     * gets a list of CASUALPackages available for this query on the build prop
     *
     * @return list of CASUALPackages
     */
    public List<CASUALPackage> getPackagesList() {
        ArrayList<CASUALPackage> cp = new ArrayList<CASUALPackage>();

        ArrayList<String> list = folderList();
        for (String pack : list) {
            CASUALPackage packageMeta = new CASUALPackage(pack);
            if (packageMeta.isValid()) {
                cp.add(packageMeta);
            }
        }
        System.out.println("Found " + cp.size() + " valid CASPACs");
        return cp;

    }

    /**
     * Performs the listing on the folder. This is performed after blacklist
     * Gets folders and files. Adds files to the availableURLs list
     *
     * @param remoteFolder folder to do work on
     * @param availableURLs reference to the master URL list
     * @return new work items to be addressed.
     * @throws MalformedURLException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    private ArrayList<String> folderList() {
        try {
            //get JSON from website
            BufferedReader in = new BufferedReader(new StringReader(HttpPost.postString(buildProp, buildsURL)));
            String json = extractJSONFromHTTPResponse(in);
            return parseCDevJSON(json);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CASUALDevQuerier2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CASUALDevQuerier2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScriptException ex) {
            Logger.getLogger(CASUALDevQuerier2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ArrayList<String>();
    }

    private ArrayList<String> parseCDevJSON(String json) throws ScriptException {
        ArrayList<String> returnList = new ArrayList<String>();
        // create a JavaScript engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        String parsed;
        int x = 0;
        //evaluate the json as a JavaScript String[].. String parsed=json[1];
        while (null != (parsed = (String) engine.eval(json + "[" + x++ + "]"))) {
            returnList.add(rootPath + parsed);
        }
        return returnList;
    }

    private String extractJSONFromHTTPResponse(BufferedReader in) throws IOException {
        String line;
        String json = "";
        //parse for JSON hint.
        while (!(line = in.readLine()).contains("-----END JSON OUTPUT-----")) {
            System.out.println(line);
            if (line.contains("<br>----START JSON OUTPUT----<br>")) {
                //read the JSON Array into json string value
                json = in.readLine();
            }
        }
        return json;
    }


}
