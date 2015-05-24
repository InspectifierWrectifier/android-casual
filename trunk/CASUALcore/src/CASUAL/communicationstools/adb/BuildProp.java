/*BuildProp handles obtaining a build.prop from an Android Device. 
 * Copyright (C) 2014 Adam Outler <adamoutler@gmail.com>
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
package CASUAL.communicationstools.adb;

import CASUAL.CASUALMessageObject;
import CASUAL.Log;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 *
 * @author adamoutler
 */
public class BuildProp {

    String buildProp = "";

    
    /**
     * gets the build prop.
     *
     * @return string representing build.prop
     */
    private synchronized String getBuildProp() {
        if (buildProp.isEmpty()) {
            getFreshBuildProp();
        }
        return buildProp;
    }

    /**
     * obtains a new BuildProp from device.
     */
    private void getFreshBuildProp() {
        ADBTools adb = new ADBTools();
        if (!adb.isConnected()) {
            new CASUALMessageObject("@interactionWaitingForAdb").showInformationMessage();
            adb.waitForDevice();
        }
        String buidProp = new ADBTools().run(new String[]{"cat /system/build.prop"}, 10000, true);
    }

    /**
     * gets a buildprop and returns a properties file.
     *
     * @return buildprop.
     */
    public Properties toProperties() {
        Properties p = new Properties();
        try {
            p.load(new StringReader(toString()));
        } catch (IOException ex) {
            Log.level4Debug("Could not obtain properties file");
        }
        return p;
    }

    @Override
    public String toString() {
        return getBuildProp();
    }
}
