/*PackagerMain packages CASPACs
 ***************************************************************************
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
 **************************************************************************/
package CASPACkager;

import CASUAL.Log;
import CASUAL.CASUALSessionData;
import CASUAL.misc.StringOperations;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * *************************************************************************
 * PackagerMain packages a CASPAC into a CASUAL and provides a method of
 * modifying a CASPAC on the fly as a part of packaging.
 *
 * @author Jeremy Loper jrloper@gmail.com
 * @author Logan Ludington loglud@logatech.org
 ************************************************************************
 */
public class PackagerMain {

    /**
     * instantiates Packager
     */
    public PackagerMain() {
    }
    final private static boolean useOverrideArgs = false; //overrides the main arguments for testing
    final private static String[] overrideArgs = {"--fullauto", "../CASPAC/", "--type", "nightly"};
    /**
     * output directory for package
     */
    protected  String userOutputDir = "";//the output folder 
    final private  String defaultOutputDir = CASUALSessionData.CASUALHome + "PACKAGES" + CASUALSessionData.slash;
    private  String caspacWithPath = ""; //path to CASPAC
    String appendToName = ""; //string after name and before file extension
    String processFolder = "";//folder to be processed
    static boolean hasProcessedFolder = false;//once folder is complete this is true
    static ArrayList<String[]> replaceText;//used to replace text in script
    static ArrayList<String[]> replaceFile;//used to replace files in zip
    private final String slash = CASUALSessionData.slash;
     File outputFile=null;
     File returnFile=null;
    /**
     * Packages a CASPAC into a CASUAL.
     *
     * @param args input from command line where args can be the following:
     * --CASPAC || -c "/path_to/CASPAC.*" specifies the path to the CASPAC eg
     * "--CASPAC C:/mycaspac.CASPAC". --output || -o "/path_to/OutputCASUAL.jar"
     * specifies an output file name. --type || -t "words to be appended before
     * jar" appends words to the end of the filename eg "--type nightly"
     * results: CASPAC-CASUALr327-nightly.jar.
     *
     * --fullauto || -f "/path_to_CASPAC_Folder/" Creates a folder in the
     * specified fullauto path called "CASUAL". Generates new CASUAL.jar files
     * based upon each CASPAC name in the full auto folder eg "--fullauto
     * /path/to/folder" will process all CASPACs in the folder and output to
     * /path/to/folder/CASUAL/. May be used with --type. cannot be used with
     * --output or --CASPAC arguments.
     *
     */
    public static void main(final String[] args) {
        doPackaging(args);
    }

    public static File doPackaging(String[] args){
        CASUALSessionData.setGUI(new GUI.testing.automatic());
        PackagerMain pm=new PackagerMain();
        pm.processCommandline(args);
        Log.level2Information("[CASPACkager] Command line utility started");
        if (hasProcessedFolder) {
            pm.processFolder();
        } else {
            pm.mergeCaspacCasual();
        }
         return pm.returnFile;
    }
    /**
     * Merges the specified Caspac with the included CASUAL.
     */
    private  void mergeCaspacCasual() {
        mergeCaspacCasual(caspacWithPath, userOutputDir);
    }

    /**
     * Merges the specified Caspac with the included CASUAL.
     *
     * @param caspacLoc location to caspac
     */
    private void mergeCaspacCasual(String caspacLoc) {
        mergeCaspacCasual(caspacLoc, userOutputDir);
    }

    /**
     * Merges the specified Caspac with the included CASUAL.
     *
     * @param caspacLoc location to caspac
     * @param outputDir folder to output the CASUAL
     */
    private  void mergeCaspacCasual(String caspacLoc, String outputDir) {
        try {
            int lastSlash = caspacLoc.lastIndexOf("/");
            if (lastSlash == -1) {
                lastSlash = caspacLoc.lastIndexOf("\\");
            }
            int lastDot = caspacLoc.lastIndexOf(".");
            String outputFileName = caspacLoc.substring(lastSlash, lastDot) + "-CASUAL-R" + Integer.toString(CASUAL.CASUALTools.getSVNVersion()) + "b";
            if (!appendToName.isEmpty()) {
                outputFileName = outputFileName + "-" + appendToName;
            }
            outputFileName = outputFileName + ".jar";
            if (outputDir.equals("")) {
                outputFile = new File(defaultOutputDir + outputFileName);
            } else {
                outputFile = new File(outputDir + outputFileName);
            }
            if (!outputFile.exists()) {
                File parentDir = outputFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                outputFile.createNewFile();
            }
            final byte[] BUFFER = new byte[1024];
            ZipInputStream zin = new ZipInputStream(getClass().getResource("/CASPACkager/resources/CASUAL.jar").openStream());
            if (replaceText != null) {
            }
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));
            ZipEntry entry = zin.getNextEntry();
            while (entry != null) {
                out.putNextEntry(entry);
                int len;
                while ((len = zin.read(BUFFER)) > 0) {
                    out.write(BUFFER, 0, len);
                }
                entry = zin.getNextEntry();
            }
            zin.close();
            if (caspacLoc.equals("")) {
                Log.level0Error("The path for the CASPAC location is not set");
            }
            if (!new File(caspacLoc).exists()) {
                Log.level0Error("The file " + caspacLoc + " doesn't exist please make sure it is the right location.");
                return;
            }

            //start CASPAC merge
            zin = new ZipInputStream(new FileInputStream(new File(caspacLoc)));
            entry = zin.getNextEntry();
            while (entry != null) {
                InputStream entryStream = zin;
                String name;

                //if there are files to be replaced, then replace the files
                if (replaceFile != null && (entry.getName().endsWith(".zip")||entry.getName().endsWith(".CASPAC"))) {

                    entryStream = replaceFileIfNeeded(entryStream, entry);

                }

                //if there are entries to be replaced, then replace the entries
                if (replaceText != null) {
                    //loop through all text to be replaced
                    for (String[] replace : replaceText) {
                        //dont modify zip files
                        if (!entry.getName().endsWith(".zip")&&!entry.getName().endsWith(".CASPAC")) {
                            //Modify Script/Properties/Meta/txt contents
                            if (entry.getName().endsWith(".txt") || entry.getName().endsWith(".properties") || entry.getName().endsWith(".meta") || entry.getName().endsWith(".scr")) {
                                String check = CASUAL.misc.StringOperations.convertStreamToString(entryStream);
                                check = check.replace(replace[0], replace[1]);
                                entryStream = CASUAL.misc.StringOperations.convertStringToStream(check);
                            }

                        }
                        //Modify all filenames
                        name = entry.getName();
                        if (name.contains(replace[0]) && (name.endsWith(".scr") || name.endsWith(".zip") || name.endsWith(".meta") || name.endsWith(".txt") || name.endsWith(".properties"))) {
                            entry = new ZipEntry(entry.getName().replaceAll(replace[0], replace[1]));
                        }
                    }
                }
                name = entry.getName();
                out.putNextEntry(new ZipEntry("SCRIPTS" + slash + name));
                int bufferSize;
                while ((bufferSize = entryStream.read(BUFFER)) > 0) {
                    out.write(BUFFER, 0, bufferSize);
                }
                entry = zin.getNextEntry();
            }
            out.close();
            Log.level4Debug("created " + outputFile);
            outputFile.setExecutable(true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PackagerMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PackagerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        returnFile=outputFile;
    }

    private  void processCommandline(String[] args) {

        if (useOverrideArgs) {
            args = overrideArgs;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("--type") || args[i].contains("-t")) {
                appendToName = args[++i];
            } else if (args[i].contains("--CASPAC") || args[i].contains("-c")) {
                caspacWithPath = args[++i];
            } else if (args[i].contains("--output") || args[i].contains("-o")) {
                userOutputDir = StringOperations.removeLeadingAndTrailingSpaces(args[++i]);
            } else if (args[i].contains("--fullauto") || args[i].contains("-f")) {
                processFolder = StringOperations.removeLeadingAndTrailingSpaces(args[++i]);
            } else if (args[i].contains("--replaceReference")) {
                if (replaceText == null) {
                    replaceText = new ArrayList<String[]>();
                }
                String replaceThis = args[++i];
                String withThis = args[++i];
                replaceText.add(new String[]{replaceThis, withThis});
            } else if (args[i].contains("--replaceFile")) {
                if (replaceFile == null) {
                    replaceFile = new ArrayList<String[]>();
                }
                String replaceThis = args[++i];
                String withThis = args[++i];
                if (!new File(withThis).exists()) {
                    Log.level0Error("File Does Not Exist:" + withThis);
                    showMessageAndExit();
                }
                replaceFile.add(new String[]{replaceThis, withThis});

            } else {
                Log.level0Error(args[i] + " is an unrecognized command.");
                showMessageAndExit();
            }
        }

        //exit if nothing to process        
        if (caspacWithPath.equals("") && processFolder.equals("")) {
            Log.level0Error("You failed to specify any processing items.");
            showMessageAndExit();
        }

        if (!caspacWithPath.equals("") && !processFolder.equals("")) {
            Log.level0Error("Please only specify either a folder to process or "
                    + "a CASPAC to work.");
            showMessageAndExit();
        }

        if ((!caspacWithPath.endsWith(".zip")&& !caspacWithPath.endsWith(".CASPAC") )&& !caspacWithPath.equals("")) {
            Log.level0Error(caspacWithPath + " is not a valid CASPAC file.");
            showMessageAndExit();
        }

        if (!processFolder.equals("") && userOutputDir.equals("")) {
            Log.level2Information("No output directory supplied will place "
                    + processFolder + "CASUAL");
            userOutputDir = processFolder + "CASUAL" + CASUALSessionData.slash;
            if (!(new File(userOutputDir).exists())) {
                File outdir = new File(userOutputDir);
                outdir.mkdirs();
                for (File f : outdir.listFiles()) {
                    f.delete();
                }
            }

        }

        if (!new File(userOutputDir).isDirectory() && !userOutputDir.equals("") && new File(userOutputDir).exists()) {
            Log.level0Error(userOutputDir + " is a file not a directory. Please make sure to "
                    + "specify a folder not a file. The file will be named for you.");
            showMessageAndExit();
        }

        //if we are using userOutputDir
        if (!userOutputDir.equals("")) {
            // verify there is a slash at the end of userOutputDir
            if (!userOutputDir.endsWith(CASUALSessionData.slash)) {
                userOutputDir = userOutputDir + CASUALSessionData.slash;
            }
            // set output dir to the same as the file    
        }

        if (!processFolder.equals("")) {
            hasProcessedFolder = true;
        }

        if (hasProcessedFolder && !(new File(processFolder).isDirectory())) {
            Log.level0Error(processFolder + " is not a valid processing directory.");
            showMessageAndExit();
        }

        if (appendToName.contains(CASUALSessionData.slash)) {
            Log.level0Error("Append to name contains illegal characters");
            showMessageAndExit();
        }

    }

    /**
     * gets the list of files in the CASPAC.
     *
     * @return path to files
     */
    private String[] getCaspacFileList() {
        if (caspacWithPath.equals("")) {
            Log.level0Error("Caspac file not set.");
            return null;
        }
        String[] files = getCaspacFileList(caspacWithPath);
        return files;
    }

    /**
     * gets the list of files in the CASPAC.
     *
     * @param caspacLoc path toCASPAC
     * @return path to files
     */
    private String[] getCaspacFileList(String caspacLoc) {
        ArrayList<String> fileList = new ArrayList<String>();
        if (!new File(caspacLoc).exists()) {
            Log.level0Error("CASPAC requested not a valid file.");
            return null;
        }
        if (!caspacLoc.endsWith(".zip")&& !caspacLoc.endsWith(".CASPAC")) {
            Log.level0Error(caspacLoc + "is not a valid CASPAC file.");
            return null;
        }
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(new File(caspacLoc)));

            ZipEntry entry = zin.getNextEntry();

            while (entry != null) {
                fileList.add(entry.getName());
                entry = zin.getNextEntry();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PackagerMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PackagerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileList.toArray(new String[fileList.size()]);
    }

    /**
     * gets the list of files in the CASPAC. caspac file object representing the
     * CASPAC
     *
     * @param caspac to be examined
     * @return path to files
     */
    private String[] getCaspacFileList(File caspac) {
        if (!caspac.exists()) {
            Log.level0Error("CASPAC requested not a valid file.");
            return null;
        }
        if (!caspac.getName().endsWith("zip")) {
            try {
                Log.level0Error(caspac.getCanonicalPath() + "is not a valid CASPAC file.");
            } catch (IOException ex) {
                Logger.getLogger(PackagerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
        String[] fileList = getCaspacFileList(caspac.getAbsolutePath());
        return fileList;
    }

    private static void showMessageAndExit() {
        Log.level2Information(
                "CASUAL's CASPAC deployment PACKAGER              ");
        Log.level2Information(
                "                                                 ");
        Log.level2Information(
                "java -jar PACKAGER.jar [OPTIONS]                 ");
        Log.level2Information(
                "                                                 ");
        Log.level2Information(
                "--fullauto  /path_to/CASPACs                     ");
        Log.level2Information(
                "java -jar --fullauto /home/caspacs/              ");
        Log.level2Information(
                "  - or -                                         ");
        Log.level2Information(
                "java -jar --CASPAC /home/caspacs/myCASPAC.CASPAC    ");
        Log.level2Information(
                "                                                 ");
        Log.level2Information(
                "--output /directory/ (optional)                  ");
        Log.level2Information(
                "default (user.home)/.CASUAL/PACKAGES             ");
        Log.level2Information(
                "--type  \"stringToAppendToFileName\"  (optional) ");
        Log.level2Information(
                "--replaceReference \"Any Words In CASPAC\" \"Different Words\" (optional)");
        Log.level2Information(
                "default nothing                                  ");
        System.exit(1);
    }

    /**
     * processes the folder and merges the CASPAC
     */
    private void processFolder() {
        for (File f : new File(processFolder).listFiles()) {
            if (f.toString().contains(".zip")||f.toString().contains(".CASPAC")) {
                Log.level4Debug("processing " + f.getAbsolutePath());
                mergeCaspacCasual(f.toString(), userOutputDir);
            }
        }
    }

    private InputStream replaceFileIfNeeded(InputStream zin, ZipEntry entry) {
        CASUALSessionData sd=CASUALSessionData.newInstance();
        String working = sd.getTempFolder() + "extractionof" + entry + CASUALSessionData.slash;
        new File(working).mkdirs();
        try {
            CASUAL.archiving.Unzip.unZipInputStream(sd,zin, working);
            String[] extractList = new File(working).list();
            for (String[] fReplacement : replaceFile) {
                for (String fileCheck : extractList) {
                    if (fileCheck.equals(fReplacement[0])) {
                        new CASUAL.FileOperations().copyFile(fReplacement[1], working + fReplacement[0]);
                    }
                }
            }
            return repackEntry(sd,entry, working);
        } catch (ZipException ex) {
            return repackEntry(sd,entry, working);
        } catch (IOException ex) {
            return repackEntry(sd,entry, working);
        }

    }

    /**
     * adds a folder to the zip stream
     *
     * @param entry location to begin with
     * @param working folder to compress
     * @return inputstream representing the zip entry
     */
    private InputStream repackEntry(CASUALSessionData sd,ZipEntry entry, String working) {
        //TODO examine this to see if this is the cleanest way to handle this
        try {
            CASUAL.archiving.Zip zip = new CASUAL.archiving.Zip(sd,new File(sd.getTempFolder() + entry.getName()));
            zip.compressZipDir(working);
            return new FileInputStream(sd.getTempFolder() + entry.getName());
        } catch (IOException ex) {
            return null;
        }
    }
}
