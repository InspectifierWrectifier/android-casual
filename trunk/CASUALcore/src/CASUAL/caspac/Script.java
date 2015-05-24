/*Script provides a way to read and write Script information for a Caspac
 *Copyright (C) 2015  Adam Outler & Logan Ludington
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
package CASUAL.caspac;

import CASUAL.CASUALMessageObject;
import CASUAL.CASUALSessionData;
import CASUAL.CASUALStartupTasks;
import CASUAL.CASUALTools;
import CASUAL.Log;
import CASUAL.archiving.Unzip;
import CASUAL.archiving.Zip;
import CASUAL.crypto.MD5sum;
import CASUAL.misc.StringOperations;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 * provides a way to read and write Script information for a Caspac
 *
 * @author Adam Outler adamoutler@gmail.com
 * @author loganludington
 */
public class Script {
    private static final String slash = System.getProperty("file.separator");

    /**
     * @return the slash
     */
    public static String getSlash() {
        return slash;
    }

    final CASUALSessionData sd;

    /**
     * extractionMethod = 0 for CASPAC (File, zipFile/zipFile) 1 for CASUAL
     * (Resource, /SCRIPTS/zipFile 2 for Filesystem (File, zipFile)
     */
    final int CASPAC = 0;
    final int CASUAL = 1;
    final int FILE = 2;
    AtomicBoolean isLoaded = new AtomicBoolean(false);
    /**
     * Specifies the extraction method for the script. final int CASPAC = 0
     * final int CASUAL = 1; final int FILE = 2;
     *
     */
    final public int extractionMethod;

    /**
     * zipFile Entry, Resource or File on disk.
     */
    public Object scriptZipFile;

    /**
     * CASPAC only. used to show zipfile location on disk. Used to determine
     * parent
     */
    private Unzip zipfile; //CASPAC only.

    /**
     * Name of the Script (script filename without extension).
     */
    private String name;

    /**
     * Contents of the Script which are to be executed by CASUAL. This is
     * populated by the Script SCR file.
     */
    private String scriptContents = "";

    /**
     * An array of resources after decompression from the Script's ZIP file.
     */
    private List<File> individualFiles = new ArrayList<File>();

    /**
     * Metadata from the script. This is populated from the Script META file.
     */
    private ScriptMeta metaData = new ScriptMeta(this);

    /**
     * The description of the script. This is populated from the Script TXT
     * file.
     */
    private String discription = "";

    /**
     * While scriptContinue is true, the script may continue. If scriptContinue
     * is false, the script will not execute further lines.
     */
    private boolean scriptContinue = false;

    /**
     * Device Arch. This is used by busybox to determine what dependency to use.
     */
    private String deviceArch = "";

    /**
     * MD5 array as read from files directly.
     */
    private List<String> actualMD5s = new ArrayList<String>();

    /**
     * Creates a duplicate script from an old one.
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param s script to use as base.
     */
    public Script(CASUALSessionData sd,Script s) {
        this.sd=sd;
        Log.level4Debug("Setting up script " + s.name + " from preexisting script");
        this.name = s.name;
        this.extractionMethod = 2;
        this.metaData = s.metaData;
        this.individualFiles = s.individualFiles;
        this.zipfile = s.zipfile;
        this.discription = s.discription;
        this.scriptContinue = s.scriptContinue;
        this.deviceArch = s.deviceArch;
    }

    /**
     * Creates a new script from a name and a temp folder.
     *
     * @param sd session data to be used for this script
     * @param name name of script.
     * @param tempDir temp folder to use.
     */
    public Script(CASUALSessionData sd, String name, String tempDir) {
        this.sd=sd;
        Log.level4Debug("Setting up script " + name + " with name and tempdir");
        this.name = name;
        this.extractionMethod = 0;
    }

    /**
     * Creates a new script from a name, tempdir and type.
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param name name of script
     * @param tempDir temp folder to use.
     * @param type this.CASPAC, this.CASUAL, this.FILE. final int CASPAC = 0
     * final int CASUAL = 1; final int FILE = 2;
     */
    public Script(CASUALSessionData sd,String name, String tempDir, int type) {
        this.sd=sd;
        Log.level4Debug("Setting up script " + name + " with name, tempdir and type");
        this.name = name;
        this.extractionMethod = type;
    }

    /**
     * creates a new script with several parameters
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param name name of script
     * @param script Script contents to use for script (scr file)
     * @param discription description of script (txt file)
     * @param includeFiles files to be used in script (zipfile)
     * @param tempDir temp folder to use.
     */
    public Script(CASUALSessionData sd,String name, String script, String discription, List<File> includeFiles, String tempDir) {
        this.sd=sd;
        Log.level4Debug("Setting up script " + name + " with name, script, description, included files and tempdir");
        this.discription = discription;
        this.name = name;
        this.scriptContents = script;
        this.individualFiles = includeFiles;
        extractionMethod = 0;
    }

    /**
     * creates a new script with several parameters
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param name name of script
     * @param script Script contents to use for script (scr file)
     * @param discription description of script (txt file)
     * @param includeFiles files to be used in script (zipfile)
     * @param prop properties file to be used in script (meta)
     * @param tempDir temp folder to use.
     * @param type type of script (this.CASUAL this.CASPAC this.FILE).
     */
    public Script(CASUALSessionData sd, String name, String script, String discription,
            List<File> includeFiles, Properties prop, String tempDir, int type) {
        this.sd=sd;
        Log.level4Debug("Setting up script " + name + " with name, script, description, included files, propeties, type and tempdir");
        this.discription = discription;
        this.name = name;
        this.scriptContents = script;
        this.individualFiles = includeFiles;
        this.metaData = new ScriptMeta(prop, this);
        this.extractionMethod = type;
    }

    /**
     * creates a new script with several parameters
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param name name of script
     * @param script Script contents to use for script (scr file)
     * @param discription description of script (txt file)
     * @param includeFiles files to be used in script (zipfile)
     * @param prop properties file to be used in script (meta)
     * @param tempDir temp folder to use.
     */
    public Script(CASUALSessionData sd,String name, String script, String discription,
            List<File> includeFiles, Properties prop, String tempDir) {
        this.sd=sd;
        Log.level4Debug("Setting up script " + name + " with name, script, description includedFiles, properties, and tempdir");
        this.discription = discription;
        this.name = name;
        this.scriptContents = script;
        this.individualFiles = includeFiles;
        this.metaData = new ScriptMeta(prop, this);
        extractionMethod = 0;
    }

    /**
     * creates a new script with several parameters
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param name name of script
     * @param script Script contents to use for script (scr file)
     * @param discription description of script (txt file)
     * @param tempDir temp folder to use.
     */
    public Script(CASUALSessionData sd,String name, String script, String discription, String tempDir) {
        this.sd=sd;
        Log.level4Debug("Setting up script " + name + " with name, script, description and tempdir");
        this.name = name;
        this.scriptContents = script;
        this.discription = discription;
        extractionMethod = 0;
    }

    /**
     * Returns a copy of the script with a new name and tempdir.
     *
     * @param newScriptName new script name
     * @param newTempDir new tempdir
     * @return new script with tempdir and name.
     */
    public Script copyOf(String newScriptName, String newTempDir) {
        Log.level4Debug("Setting up script " + newScriptName + " from preexisting script");
        Script s = new Script(sd,newScriptName, sd.getTempFolder());
        s.metaData = metaData;
        s.individualFiles = individualFiles;
        s.zipfile = zipfile;
        s.discription = discription;
        s.scriptContinue = scriptContinue;
        return s;
    }

    /**
     * verifies script contents to ensure script is a valid script and can be
     * used.
     *
     * @return true if valid script.
     */
    public boolean verifyScript() {
        if (getName().isEmpty()) {
            Log.level0Error("Missing Script Name! Cannot continue.");
            return false;
        }
        if (scriptContents.isEmpty()) {
            Log.level0Error(getName()+ " Script contents are empty! Cannot continue.");
            return false;
        }
        if (discription.isEmpty()) {
            Log.level0Error(getName()+ " Script discription is empty! Cannot continue.");
            return false;
        }
        if (!metaData.verifyMeta()) {
            Log.level0Error(getName()+ " Script Meta data is incomplete! Cannot continue.");
            return false;
        }
        return true;
    }

    /**
     * gets the script contents (SCR) file.
     *
     * @return contents of script.
     */
    public DataInputStream getScriptContents() {
        InputStream is = StringOperations.convertStringToStream(scriptContents);
        return new DataInputStream(is);
    }

    public String getScriptContentsString() {
        return scriptContents;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name:").append(this.name);
        sb.append("\nMonitoring: ").append(this.metaData.getMonitorMode());
        sb.append("temp dir:").append(this.getTempDir());
        return name;
    }

    private void addMD5ToMeta(String linuxMD5, int md5Position) {
        Log.level3Verbose("evaluated MD5 to " + linuxMD5);
        metaData.getMetaProp().setProperty("Script.MD5[" + md5Position + "]", linuxMD5);
    }

    private void addMD5ToMeta(MD5sum md5sum, String filePath, int md5Position) {
        String linuxMD5 = md5sum.getLinuxMD5Sum(new File(filePath));
        Log.level3Verbose("evaluated MD5 to " + linuxMD5);
        metaData.getMetaProp().setProperty("Script.MD5[" + md5Position + "]", linuxMD5);
    }

    /*
     * extracts includedFiles from zip
     */
    /**
     * gets a runnable object representing the entire extraction of the script
     * from the zip file.
     *
     * @return runnable extraction method.
     */
    public Runnable getExtractionRunnable() {
        if (this.extractionMethod == CASPAC) {  //This is a CASPAC
            final Unzip myCASPAC = this.zipfile;
            final Object entry = this.scriptZipFile;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    ArrayList<File> unzipped = new ArrayList<File>();
                    Log.level4Debug("Examining CASPAC mode package contents");
                    BufferedInputStream bis = null;
                    try {
                        Log.level4Debug("Unzipping CASPAC member " + getName());
                        bis = myCASPAC.streamFileFromZip(entry);
                        getActualMD5s().add(new MD5sum().getLinuxMD5Sum(bis, entry.toString()));
                        bis = myCASPAC.streamFileFromZip(entry);
                        unzipped = Unzip.unZipInputStream(sd, bis, getTempDir());
                        bis.close();
                        Log.level4Debug("Extracted entry " + myCASPAC.getEntryName(entry) + "to " + getTempDir());

                    } catch (ZipException ex) {
                        Log.errorHandler(ex);
                    } catch (IOException ex) {
                        Log.errorHandler(ex);
                    } finally {
                        try {
                            if (bis != null) {
                                bis.close();
                            }
                        } catch (IOException ex) {
                            Log.errorHandler(ex);
                        }
                    }

                    getIndividualFiles().clear();
                    getIndividualFiles().addAll(unzipped);
                    if (getIndividualFiles().size() > 0) {
                        for (String md5 : getMetaData().getMd5s()) {
                            if (!Arrays.asList(actualMD5s.toArray(new String[]{})).contains(md5)) {
                                Log.level4Debug("Could not find " + md5 + " in list " + StringOperations.arrayToString(getActualMD5s().toArray(new String[]{})));
                                new CASUALMessageObject("@interactionPackageCorrupt").showErrorDialog();
                                if (!Caspac.isDebug()) {
                                    setScriptContents("");
                                }
                            }
                        }
                    }
                    isLoaded.set(true);
                }
            };
            CASUALStartupTasks.caspacScriptPrepLock = false;
            return r;
        }
        if (this.extractionMethod == CASUAL) {  //This is a CASUAL
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (scriptZipFile != null && !scriptZipFile.toString().isEmpty()) {
                        if (CASUALTools.IDEMode) {
                            try {
                                Log.level4Debug("Examining IDE mode script contents" + scriptZipFile.toString());
                                getActualMD5s().add(new MD5sum().getLinuxMD5Sum(new File((String) scriptZipFile)));
                                File folder = new File(getTempDir());
                                if (!folder.isDirectory()) {
                                    folder.mkdirs();
                                }
                                Unzip unzip = new Unzip(new File((String) scriptZipFile));
                                unzip.unzipFile(getTempDir());
                            } catch (ZipException ex) {
                                Log.errorHandler(ex);
                            } catch (IOException ex) {
                                Log.errorHandler(ex);
                            }
                        } else {
                            try {
                                Log.level4Debug("Examining CASUAL mode script contents:" + scriptZipFile.toString());
                                getActualMD5s().add(new MD5sum().getLinuxMD5Sum(getClass().getResourceAsStream("/" + scriptZipFile.toString()), scriptZipFile.toString()));
                                Log.level4Debug("unzip of " + scriptZipFile.toString() + " is beginning.");
                                Unzip.unZipResource(sd,"/" + scriptZipFile.toString(), getTempDir());
                            } catch (FileNotFoundException ex) {
                                Log.errorHandler(ex);
                            } catch (IOException ex) {
                                Log.errorHandler(ex);
                            }
                            Log.level4Debug("unzip of " + getName() + " is complete.");
                        }
                    } else {
                        Log.level3Verbose("script Zipfile was null");
                    }
                    /*
                     * CASUAL do not receive MD5s
                     */
                    isLoaded.set(true);
                }
            };
            CASUALStartupTasks.caspacScriptPrepLock = false;
            return r;
        }
        if (this.extractionMethod == FILE) { //This is running on the filesystem
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Log.level4Debug("Examining updated script contents on filesystem");
                    getActualMD5s().add(new MD5sum().getLinuxMD5Sum(new File(scriptZipFile.toString())));
                    String ziplocation = scriptZipFile.toString();
                    try {
                        Unzip unzip = new Unzip(ziplocation);
                        Log.level4Debug("Unzipping from " + ziplocation + " to " + getTempDir());
                        unzip.unzipFile(getTempDir());
                    } catch (ZipException ex) {
                        Log.errorHandler(ex);
                    } catch (IOException ex) {
                        Log.errorHandler(ex);
                    }
                    Log.level4Debug("examining MD5s");
                    for (String md5 : getMetaData().getMd5s()) {
                        if (!(Arrays.asList(actualMD5s.toArray()).contains(md5))) {
                            Log.level4Debug("Md5 mismatch!!  Expected:" + md5);

                            if (!Caspac.isDebug()) {
                                setScriptContents("");
                            }
                        }
                    }
                    if (!scriptContents.isEmpty()) {
                        Log.level4Debug("Update sucessful.  MD5s matched server.");
                    } else {
                        new CASUALMessageObject("@interactionPackageCorrupt").showErrorDialog();
                    }
                    isLoaded.set(true);
                }

            };
            CASUALStartupTasks.caspacScriptPrepLock = false;
            return r;
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
            }
        };

        CASUALStartupTasks.caspacScriptPrepLock = false;
        return r;

    }

    Map<String, InputStream> getScriptAsMapForCASPAC() {
        CASUAL.Log log = new CASUAL.Log();
        CASUAL.crypto.MD5sum md5sum = new CASUAL.crypto.MD5sum();
        Map<String, InputStream> scriptEntries = new HashMap<String, InputStream>();
        ArrayList<String> tempMD5s = new ArrayList<String>();

        //get md5 and stream for script
        tempMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(scriptContents), name + ".scr"));
        scriptEntries.put(name + ".scr", StringOperations.convertStringToStream(scriptContents));

        //get md5 and stream for txt
        tempMD5s.add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(this.discription), name + ".txt"));
        scriptEntries.put(name + ".txt", StringOperations.convertStringToStream(this.discription));

        //get md5 and stream for zip
        //go to folder above and create stream
        File masterTempDir = new File(sd.getTempFolder()).getParentFile();
        File instanceZip = new File(masterTempDir + CASUALSessionData.slash + name + ".zip");
        try {
            instanceZip.delete();
            instanceZip.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
        }
        Log.level3Verbose("set script $ZIPFILE to " + instanceZip.getAbsolutePath());
        try {
            Zip zip;

            zip = new Zip(sd,instanceZip);
            zip.removeAllEntries();
            zip.addFilesToExistingZip(individualFiles.toArray(new File[individualFiles.size()]));

            Log.level3Verbose("Adding zip:" + instanceZip.getAbsolutePath());

            tempMD5s.add(new CASUAL.crypto.MD5sum().getLinuxMD5Sum(instanceZip));
            scriptEntries.put(name + ".zip", new FileInputStream(instanceZip.getAbsoluteFile()));

        } catch (IOException ex) {
            Log.errorHandler(ex);
        }

        //update MD5s and update meta
        for (int i = 0; i < tempMD5s.size(); i++) {
            this.addMD5ToMeta(tempMD5s.get(i), i);
        }
        this.actualMD5s = tempMD5s;
        //get meta
        scriptEntries.put(name + ".meta", this.metaData.getMetaInputStream());

        return scriptEntries;

    }

    /**
     * performs unzip and is to be run after script zipfile update, not during
     * script init.
     *
     * @throws ZipException when zip is corrupt
     * @throws IOException when permissions problem exists.
     */
    public void performUnzipAfterScriptZipfileUpdate() throws ZipException, IOException {
        this.getExtractionRunnable().run();
    }

    /**
     * @return the zipfile
     */
    public Unzip getZipfile() {
        return zipfile;
    }

    /**
     * @param zipfile the zipfile to set
     */
    public void setZipfile(Unzip zipfile) {
        this.zipfile = zipfile;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     * @return this Script
     */
    public Script setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return the tempDir
     */
    public String getTempDir() {
        return sd.getTempFolder()+this.name;
    }



    /**
     * @param scriptContents the scriptContents to set
     * @return this Script
     */
    public Script setScriptContents(String scriptContents) {
        this.scriptContents = scriptContents;
        return this;
    }

    /**
     * @return the individualFiles
     */
    public List<File> getIndividualFiles() {
        return individualFiles;
    }

    /**
     * @param individualFiles the individualFiles to set
     * @return this Script
     */
    public Script setIndividualFiles(List<File> individualFiles) {
        this.individualFiles = individualFiles;
        return this;
    }

    /**
     * @return the metaData
     */
    public ScriptMeta getMetaData() {
        return metaData;
    }

    /**
     * @param metaData the metaData to set
     * @return this Script
     */
    public Script setMetaData(ScriptMeta metaData) {
        this.metaData = metaData;
        return this;
    }

    /**
     * @return the discription
     */
    public String getDiscription() {
        return discription;
    }

    /**
     * @param discription the discription to set
     * @return this Script
     */
    public Script setDiscription(String discription) {
        this.discription = discription;
        return this;
    }

    /**
     * @return the scriptContinue
     */
    public boolean isScriptContinue() {
        return scriptContinue;
    }

    /**
     * @param scriptContinue the scriptContinue to set
     */
    public void setScriptContinue(boolean scriptContinue) {
        this.scriptContinue = scriptContinue;
    }

    /**
     * @return the deviceArch
     */
    public String getDeviceArch() {
        return deviceArch;
    }

    /**
     * @param deviceArch the deviceArch to set
     */
    public void setDeviceArch(String deviceArch) {
        this.deviceArch = deviceArch;
    }

    /**
     * @return the actualMD5s
     */
    public List<String> getActualMD5s() {
        return actualMD5s;
    }

    /**
     * @param actualMD5s the actualMD5s to set
     */
    public void setActualMD5s(List<String> actualMD5s) {
        this.actualMD5s = actualMD5s;
    }

}
