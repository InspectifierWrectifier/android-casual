/*
 * Copyright (C) 2013 adamoutler
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
package CASUAL;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class ResourceDeployer {

    /**
     * Deploys a resource from the pacakge to the disk.
     * @param resourceLocation location of internal resource. 
     * @param tempFolder disk location.
     * @return fully qualified path to deployed file.
     */
    public String deployResourceTo(String resourceLocation, String tempFolder) {
        return deployResourceTo(new String[]{resourceLocation}, tempFolder)[0];
    }

    /**
     * deploys multiple resources to disk.
     * @param resourceLocation array of files in jar.
     * @param tempFolder deployment location.
     * @return fully qualified paths to new files. 
     */
    public String[] deployResourceTo(String[] resourceLocation, String tempFolder) {
        ArrayList<String> deployed = new ArrayList<String>();
        for (String res : resourceLocation) {
            String name = tempFolder + new File(res).getName();
            if (!copyFromResourceToFile(res, name)) {
                return new String[]{};
            }
            deployed.add(name);
        }
        return deployed.toArray(new String[deployed.size()]);
    }

    /**
     * copies a resource to a file
     *
     * @param Resource resource to be deployed
     * @param toFile file to deploy resource to
     * @return true if complete
     */
    public boolean copyFromResourceToFile(String Resource, String toFile) {
        
        boolean retval = false;

        try {
            File destination = new File(toFile);
            makeParentFolder(destination);
            InputStream resourceAsStream = getClass().getResourceAsStream(Resource);
            if (resourceAsStream.available() >= 1) {
                writeInputStreamToFile(resourceAsStream, destination);
            } else {
                resourceAsStream.close();
                Log.level0Error("@criticalErrorWhileCopying " + Resource);
                return false;
            }
            if (destination.length() < 1) {
                resourceAsStream.close();
                Log.level0Error("@failedToWriteFile");
                retval = false;
            } else {
                resourceAsStream.close();
                retval = true;
            }
        } catch (NullPointerException e) {
            return false;

        } catch (IOException ex) {
            Log.errorHandler(ex);
            Log.level0Error("@criticalErrorWhileCopying " + Resource);
            return false;
        }
        return retval;

    }

    private void makeParentFolder(File destination) {
        //check if destination directory exists and make it if needed.
        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }
    }

    private boolean writeInputStreamToFile(InputStream is, File file) {
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[4096];
            if (is.available() > 0) {
                // while stream does not return -1, fill data buffer and write.
                while ((is.read(data, 0, data.length)) != -1) {
                    out.write(data, 0, data.length);
                }
            } else {
                return false;
            }
            is.close();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
