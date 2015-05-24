/*CFAutoRootDB Pulls file locations from CFAutoRoot 
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

import CASUAL.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Pulls file locations from CFAutoRoot
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class CFAutoRootDb {

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

    final Properties BUILDPROP = new Properties();
    final private ArrayList<Device> deviceList;


    String defaultValue = "Value was not obtainable.  Cannot find value";

    /**
     * Constructor for CFAutoRootDB parses the buildprop and CFAutoroot site
     * into a usable format for the class
     *
     * @param BuildProp string representation of the /system/build.prop from the
     * device
     * @throws URISyntaxException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @throws MalformedURLException {@inheritDoc}
     * @throws CFAutorootTableException if Chainfire changes his tables
     */
    CFAutoRootDb(String BuildProp) throws URISyntaxException, IOException, MalformedURLException, CFAutorootTableException {
        this.deviceList = new ArrayList<Device>();
        BUILDPROP.load(new StringReader(BuildProp));
        grabTable();
    }

    public CFAutoRootDb(CASUAL.communicationstools.adb.BuildProp bp) throws URISyntaxException, IOException, MalformedURLException, CFAutorootTableException {
        this(bp.toString());
    }

    /**
     * checks device's build.prop against values found on
     * http://autoroot.chainfire.eu
     *
     * @return Download link for CFAutoRoot
     */
    public String returnForMyDevice() {
        //search each device
        for (Device device : deviceList) {
            //verify each device property against build.prop.
            if (device.oem.equals(buildProp("ro.product.manufacturer"))) {
                if (device.model.equals(buildProp("ro.product.model"))) {
                    if (device.name.equals(buildProp("ro.product.name"))) {
                        if (device.device.equals(buildProp("ro.product.device"))) {
                            if (device.board.equals(buildProp("ro.product.board"))) {
                                if (BUILDPROP.getProperty("ro.board.platform").equals(device.platform)) {
                                    System.out.println("located a " + device.oem + ", model:" + device.model + ", device:" + device.device);
                                    System.out.println(device.download);
                                    return device.download + "?retrieve_file=1";
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.level2Information("Found nothing available for your device on http://autoroot.chainfire.eu/");
        return "";
    }

    /**
     * gets the property from the build.prop
     *
     * @param propName property name to search
     * @return property value
     */
    private String buildProp(String propName) {
        return BUILDPROP.getProperty(propName, defaultValue);
    }

    /**
     * grabs the table and parses from CFAutoRoot
     */
    private void grabTable() throws MalformedURLException, URISyntaxException, IOException, CFAutorootTableException {
        //get url
        URI uri = new URI("http", "autoroot.chainfire.eu", "/" + "", "", null);
        URL url = new URL(uri.toASCIIString());
        String page = convertStreamToString(url.openStream());
        //remove all before <table>
        page = page.substring(page.indexOf("<table>"), page.length());
        //remove all after </table> and add a blank line after.
        page = page.substring(0, page.indexOf("</table>") + 8) + "\n";
        //convert to a buffered reader
        BufferedReader br = new BufferedReader(new StringReader(page));

        /*
        * This is not the proper way to parse HTML
        * This method is much faster for parsing than org.w3c.Document
        */
        //burn the first two TR entries as they are headers only.
        int trcount = 0;
        while (trcount < 2) {
            if (br.readLine().contains("<tr>")) {
                //assertations to verify table has not changed
                while (trcount == 1) {
                    //assert that the table is usable or throw a CFAutorootTableException.
                    //If the table is not the way we expect it, then we will not proceed. 
                    if (!br.readLine().contains("OEM")) {
                        throw new CFAutorootTableException("OEM tables On autoroot.chainfire.eu changed");
                    }
                    if (!br.readLine().contains("Model")) {
                        throw new CFAutorootTableException("Model tables On autoroot.chainfire.eu changed");
                    }
                    if (!br.readLine().contains("Name")) {
                        throw new CFAutorootTableException("Name tables On autoroot.chainfire.eu changed");
                    }
                    if (!br.readLine().contains("Device")) {
                        throw new CFAutorootTableException("Device tables On autoroot.chainfire.eu changed");
                    }
                    if (!br.readLine().contains("Board")) {
                        throw new CFAutorootTableException("Board tables On autoroot.chainfire.eu changed");
                    }
                    if (!br.readLine().contains("Platform")) {
                        throw new CFAutorootTableException("Platform tables On autoroot.chainfire.eu changed");
                    }
                    br.readLine();
                    br.readLine();
                    br.readLine();
                    if (!br.readLine().contains("Link")) {
                        throw new CFAutorootTableException("Link tables On autoroot.chainfire.eu changed");
                    }
                    trcount++;
                }
                trcount++;
            }
        }

        //Start the main reading action
        while (br.ready()) {
            //get a line
            String line = "";
            //ensure we are reading a full line
            while (!line.endsWith("\n")) {
                line += (char) br.read();
                if (line.endsWith("\uffff")||line.endsWith("-1")) {
                    return;  //return on end of stream
                }
            }
            //begin parsing tables
            if (line.contains("<tr>")) {
                //our instance device
                Device device = new Device();
                int tdEntry = 0;
                //while table is not at end of row
                while (!line.contains("</tr>")) {
                    //strip table TD tags and remove leading/trailing whitespace
                    line = br.readLine().replace("<td>", "").replace("</td>", "").trim();
                    switch (tdEntry) {
                        case 0:
                            device.oem = line;
                            break;
                        case 1:
                            device.model = line;
                            break;
                        case 2:
                            device.name = line;
                            break;
                        case 3:
                            device.device = line;
                            break;
                        case 4:
                            device.board = line;
                            break;
                        case 5:
                            device.platform = line;
                            break;
                        case 9:
                            device.download = line.replace("\">File</a>", "").replace("<a href=\"", "");
                            break;
                        default:
                            break;
                    }
                    tdEntry++;
                }
                //add device to device list
                deviceList.add(device);
            }
        }
    }

}

/**
 * Device class is an object representing a table entry of a device on
 * http://autoroot.chainfire.eu
 *
 */
class Device {

    String oem;
    String model;
    String name;
    String device;
    String board;
    String platform;
    String download;
}
