/*Caspac handles gathering, reading and writing of CASPACs in a unified manner
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

import CASUAL.CASUALSessionData;
import CASUAL.CASUALStartupTasks;
import CASUAL.CASUALTools;
import CASUAL.FileOperations;
import CASUAL.Log;
import CASUAL.archiving.Unzip;
import CASUAL.archiving.Zip;
import CASUAL.crypto.AES128Handler;
import CASUAL.crypto.MD5sum;
import CASUAL.misc.MandatoryThread;
import CASUAL.misc.StringOperations;
import CASUAL.network.CASUALDevIntegration.CasualDevCounter;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

/**
 * handles gathering, reading and writing of CASPACs in a unified manner
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public final class Caspac {

    /**
     * If we are debugging a script, we dont want to delete the script contents
     * to prevent further execution on error. This is used for debugging
     * purposes.
     */
    private static boolean debug = false;
    
    
    
    /**
     * @return the debug
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * @param aDebug the debug to set
     */
    public static void setDebug(boolean aDebug) {
        debug = aDebug;
    }

    /**
     * returns an empty CASPAC.
     *
     * @return empty CASPAC &gt;
     * @throws IOException when permission problem exists
     */
    public static final Caspac makeGenericCaspac() throws IOException {
        CASUALSessionData cd=CASUALSessionData.newInstance();
        File f = new File(cd.getTempFolder() + "newfile");
        Caspac c = new Caspac(cd, f, cd.getTempFolder(), 2);
        Script s = new Script(cd,"oneshot", cd.getTempFolder());
        
        return c;
    }
    private final CASUALSessionData sd;

    /**
     * Loads a CASPAC Type 0 CASPAC, Type 1 CASUAL, Type 2 Filesystem.
     */
    private int type;
    //public File logo;

    /**
     * BufferedImage for logo.png in CASPAC.
     */
    private BufferedImage logo;

    /**
     * CASPAC which is being used.
     */
    private File CASPAC;

    /**
     * CodeSource if available to CASPAC. Generally used in place of CASPAC
     * file.
     */
    private CodeSource CASPACsrc;

    /**
     * CASPAC -Overview.txt file contents.
     */
    private String overview = "";
    /**
     * CASPAC -Build.properties file.
     */
    private Build build;

    /**
     * ArrayList of scripts contained in CASPAC.
     */
    private List<Script> scripts = new ArrayList<Script>();

    /**
     * TempDir used for CASPAC.
     */
    private String TempFolder;

    private ArrayList<CASUAL.misc.MandatoryThread> unzipThreads = new ArrayList<CASUAL.misc.MandatoryThread>();
    //For CASUAL mode
    private Script activeScript;

    /**
     * deletes an unencrypted CASPAC from disk after extraction occurs.
     */
    private boolean caspacShouldBeDeletedAfterExtraction = false;

    private String tempbannerpic;
    private String[] controlFiles = {"-Overview.txt", "-build.properties", "-logo.png"};
    /**
     * Constructor for Caspac
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param caspac file containing CASPAC information.
     * @param tempDir temp folder to use
     * @throws IOException when permission problem exists
     */
    public Caspac(CASUALSessionData sd, File caspac, String tempDir) throws IOException {
        this.sd=sd;
        this.CASPAC = caspac;
        this.CASPACsrc = null;
        this.TempFolder = tempDir;
        this.type = 0;
        if (caspac.exists()) {
            loadCASPACcontrolFilesFromCASPAC();
        } else {
            Log.level4Debug("CASPAC Not Found, treating as a request to create new CASPAC");
        }
    }
    
       /**
     * Constructor for Caspac
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param caspac file containing CASPAC information.
     * @throws IOException when permission problem exists
     */
    public Caspac(CASUALSessionData sd, File caspac) throws IOException {
        this.sd=sd;
        this.CASPAC = caspac;
        this.CASPACsrc = null;
        this.TempFolder = sd.getTempFolder();
        this.type = 0;
        if (caspac.exists()) {
            loadCASPACcontrolFilesFromCASPAC();
        } else {
            Log.level4Debug("CASPAC Not Found, treating as a request to create new CASPAC");
        }
    }

    /**
     * Constructor for Caspac
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param caspac file containing CASPAC information.
     * @param tempDir temp folder to use
     * @param type Type of CASPAC CASPAC, Type 1 CASUAL, Type 2 Filesystem
     * @throws IOException when permission problem exists
     */
    public Caspac(CASUALSessionData sd, File caspac, String tempDir, int type) throws IOException {
        this.sd=sd;
        this.CASPAC = caspac;
        this.CASPACsrc = null;
        this.TempFolder = tempDir;
        this.type = type;
        if (caspac.exists()) {
            loadCASPACcontrolFilesFromCASPAC();
        } else {
            Log.level4Debug("CASPAC Not Found, treating as a request to create new CASPAC");
        }
    }

    /**
     * secure constructor for Caspac always call startAndWaitForUnzip in order
     * to delete file and maintain security
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param caspac file containing CASPAC information.
     * @param tempDir temp folder to use
     * @param type Type of CASPAC CASPAC, Type 1 CASUAL, Type 2 Filesystem
     * @param securityKey key to decrypt CASPAC.
     * @throws IOException when permission problem exists
     * @throws Exception when crypto problem exists
     */
    public Caspac(CASUALSessionData sd,File caspac, String tempDir, int type, char[] securityKey) throws IOException, Exception {
    this.sd=sd;
        AES128Handler ch = new AES128Handler(caspac);
        ch.decrypt(tempDir + caspac.getName(), securityKey);
        this.CASPAC = new File(tempDir + caspac.getName());
        this.CASPACsrc = null;
        this.TempFolder = tempDir;
        this.type = type;
        caspacShouldBeDeletedAfterExtraction = true;
        loadCASPACcontrolFilesFromCASPAC();
        if (tempbannerpic != null) {
            this.build.setBannerPic(tempbannerpic);
        }
    }

    /*
     * Constructor for CASUAL
     */
    /**
     * Constructor for CASUAL
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param src CodeSource reference, used to reference SCRIPTS folder.
     * @param tempDir Temporary folder to use
     * @param type Type of CASPAC CASPAC, Type 1 CASUAL, Type 2 Filesystem
     * (should be 1 generally)
     * @throws IOException when permission problem exists
     */
    public Caspac(CASUALSessionData sd,CodeSource src, String tempDir, int type) throws IOException {
        this.sd=sd;
        this.CASPACsrc = src;
        URL jar = src.getLocation();
        this.CASPAC = new File(tempDir + jar.getFile());
        this.TempFolder = tempDir;
        this.type = type;
        if (CASUALTools.IDEMode) {
            updateMD5s();
            //Statics.scriptLocations = new String[]{""};
            setupIDEModeScriptForCASUAL(CASUAL.CASUALMain.defaultPackage);
        } else {
            Log.level4Debug("Opening self as stream for scan");
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry ZEntry;
            Log.level4Debug("Picking Jar File:" + src.toString() + " ..scanning.");
            while ((ZEntry = zip.getNextEntry()) != null) {
                String entry = ZEntry.getName();
                if (entry.startsWith("SCRIPTS/") || entry.startsWith("SCRIPTS\\")) { //part of CASPAC
                    handleCASPACJarFiles(entry);

                }
            }

        }
    }

    /**
     * Sets the active script to an instace of a script.
     *
     * @param s script to make active.
     * @return Active Script
     */
    public synchronized Script setActiveScript(Script s) {
        CasualDevCounter.doIncrementCounter(s.getName() + s.getMetaData().getUniqueIdentifier());
        CASUALStartupTasks.caspacScriptPrepLock = true;
        if (type == 1) {  //CASUAL checks for updates
            try {
                Log.level3Verbose("Setting script " + s.getName() + " as active and loading");
                activeScript = s;
                if (!s.isLoaded.get()) {
                    loadActiveScript();
                }
                //update script

            } catch (MalformedURLException ex) {
            } catch (IOException ex) {
            }

        } else {
            activeScript = s;
        }
        return s;
    }

    /**
     * gets the active script.
     *
     * @return reference to active script.
     */
    public Script getActiveScript() {
        return this.activeScript;
    }

    /**
     * removes a script
     *
     * @param script Script reference
     * @return this CASPAC
     */
    public Caspac removeScript(Script script) {
        if (scripts.contains(script)) {
            scripts.remove(script);
            Log.level4Debug("Removing Script: " + script.getName());
        }
        return this;
    }

    public Caspac removeAllScripts() {
        this.scripts.clear();
        return this;
    }

    public Script getFirstScript() {
        if (scripts.size() > 0) {
            return scripts.get(0);
        }
        return null;
    }

    public Caspac addScript(Script script) {
        if (!scripts.contains(script)) {
            scripts.add(script);
            Log.level4Debug("Adding Script " + script.getName());

        }
        return this;
    }

    /**
     * writes a CASPAC
     *
     * @throws IOException when permission problem exists
     */
    public void write() throws IOException {
        Map<String, InputStream> nameStream = new HashMap<String, InputStream>();
        if (!CASPAC.exists()) {
            CASPAC.createNewFile();
        }
        Zip zip = new Zip(sd, CASPAC);
        //write Properties File
        nameStream.put("-build.properties", build.getBuildPropInputStream());
        nameStream.put("-Overview.txt", StringOperations.convertStringToStream(overview));

        if (logo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(logo, "png", baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            nameStream.put("-logo.png", is);
        }
        for (Script s : scripts) {
            //individualFiles.toArray();
            File[] list = s.getIndividualFiles().toArray(new File[s.getIndividualFiles().size()]);
            if (list != null) {
                for (File test : list) {

                    //todo: what does this do?
                    boolean delete = true;
                    for (File f : s.getIndividualFiles()) {
                        if (test.getCanonicalFile().equals(f.getCanonicalFile())) {
                            delete = false;
                        }
                    }
                    if (delete) {
                        if (test.toString().contains(s.getTempDir())) {
                            test.delete();
                        }

                    }
                }
            }
            nameStream.putAll(s.getScriptAsMapForCASPAC());
        }

        Log.level4Debug("Placeing the following files in the caspac Zip");
        String s = zip.streamEntryToExistingZip(nameStream).getAbsolutePath();
        System.out.println(s);
    }

    /**
     * sets build properties
     *
     * @param prop properties file
     * @return build.prop
     */
    public Build setBuild(Properties prop) {
        build = new Build(prop, this);
        build.loadPropsToVariables();
        return build;
    }

    /**
     * parses CASPAC and loads the first script seen identified by non-caspac
     * controller files.
     *
     * @return  this CASPAC
     * @throws ZipException when zip file is corrupt
     * @throws IOException when permission problem exists
     */
    public synchronized Caspac loadFirstScriptFromCASPAC() throws ZipException, IOException {
        Log.level4Debug("Starting loadFirstScriptFromCASPAC unzip on " + CASPAC.getAbsolutePath());
        String scriptName = "";
        Unzip unzip = new Unzip(CASPAC);
        while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            String filename = unzip.getEntryName(entry);

            //detect if file is Overview, Logo, or build.properties, otherwise its a script
            boolean isScript = !Arrays.asList(controlFiles).contains(entry.toString());

            //if it's a script, and we havent set a script, or if it's a script and it matches the script we set
            if (isScript && scriptName.isEmpty() || isScript && scriptName.equals(activeScript.getName())) {
                handleCASPACScriptFiles(filename, unzip, entry);
                //entry.toString().subst
                scriptName = entry.toString().substring(0, entry.toString().lastIndexOf("."));
                this.activeScript = this.getScriptByFilename(filename);
            }
        }
        Log.level4Debug("loading CASPAC script");
        performUnzipOnQueue();
        return this;
    }

    /**
     * Loads the active script after its been set.
     *
     * @return active script
     * @throws IOException when permission problem exists
     */
    public synchronized Script loadActiveScript() throws IOException {
        Log.level4Debug("Starting loadActiveScript CASPAC unzip.");
        String scriptName = activeScript.getName();
        if (activeScript.isLoaded.get()) {
            return this.getActiveScript();
        }
        if (type == 0) {
            Unzip unzip = new Unzip(CASPAC);
            while (unzip.zipFileEntries.hasMoreElements()) {
                Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
                String filename = unzip.getEntryName(entry);
                if (entry.toString().startsWith(scriptName)) {
                    handleCASPACScriptFiles(filename, unzip, entry);
                }
            }

        } else if (type == 1) {
            Log.level3Verbose("This is a CASUAL jar with resources");
            try {
                activeScript = updateIfRequired(activeScript);
            } catch (URISyntaxException ex) {
                Log.errorHandler(ex);
            }
            Log.level4Debug("returned from checking updates.");
            //no need to update again because it is being updated
            replaceScriptByName(activeScript);
            unzipThreads = new ArrayList<CASUAL.misc.MandatoryThread>();

            CASUAL.misc.MandatoryThread t = new CASUAL.misc.MandatoryThread(activeScript.getExtractionRunnable());
            t.setName("Active Script Preparation");
            this.unzipThreads.add(t);
            Log.level4Debug("Size of unzipThreads =" + unzipThreads.size());
            performUnzipOnQueue();
        }
        return this.getActiveScript();
    }

    /**
     * loads a CASPAC.zip file
     *
     * @return this CASPAC
     * @throws ZipException when zip file is corrupt
     * @throws IOException when permission problem exists
     */
    public Caspac load() throws ZipException, IOException {

        if (type == 1) {
            Script s = this.scripts.get(0);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(s.scriptZipFile.toString());
            Unzip.unZipInputStream(sd, in, s.getTempDir());
            in.close();
            this.activeScript = s;

            return this;
        }
        //Type 0
        Log.level4Debug("Starting commanded Load CASPAC unzip.");
        Unzip unzip = new Unzip(CASPAC);
        while (unzip.zipFileEntries.hasMoreElements()) {
            Object entry = unzip.zipFileEntries.nextElement(); //get the object and begin examination
            handleCASPACFiles(entry, unzip);
        }
        Log.level4Debug("Starting to unzip script zips");
        performUnzipOnQueue();
        Log.level4Debug("CASPAC load completed.");
        return this;
    }

    /**
     * waits for unzip to complete and executes a runnable.
     *
     * @param action runnable to execute.
     * @return this caspac
     */
    public Caspac waitForUnzipAndRun(Runnable action) {
        waitForUnzipAndRun(action, false, null);
        return this;
    }

    /**
     * waits for unzip to complete and executes a runnable.
     *
     * @param action runnable to execute.
     * @param onASeparateThread true if multi-threadded
     * @param ThreadName name for the alternate thread.
     */
    public void waitForUnzipAndRun(Runnable action, boolean onASeparateThread, String ThreadName) {
        startAndWaitForUnzip();
        if (onASeparateThread) {
            MandatoryThread t = new MandatoryThread(action);
            t.setName(ThreadName);
            t.start();
        } else {
            action.run();
        }
    }

    /**
     * causes the current thread to wait until all unzipThreads have completed.
     * this is the longest part and the last part of completion of the CASPAC
     * prep.
     */
    public synchronized void startAndWaitForUnzip() {
        boolean[] isUnzipping = new boolean[unzipThreads.size()];
        Log.level4Debug("Currently waiting for Threads:" + Integer.toString(isUnzipping.length));
        for (CASUAL.misc.MandatoryThread t : unzipThreads) {
            if (t != null && !t.isComplete() && !t.isAlive()) {
                t.start();
                t.waitFor();

            }
            Log.level4Debug("Unzip completed!");
        }

        if (this.caspacShouldBeDeletedAfterExtraction) {
            this.CASPAC.delete();
        }
        Log.level4Debug("Unzipping complete.");
    }

    /**
     * loops through active unzip threads and waits for all unzip to complete.
     *
     * @return this CASPAC
     */
    public Caspac waitForUnzip() {
        for (CASUAL.misc.MandatoryThread t : unzipThreads) {
            if (t == null) {
                continue;
            }
            if (!t.isAlive() && !t.isComplete()) {
                t.start();
            }
            if (!t.isComplete()) {
                t.waitFor();
            }
            Log.level4Debug("Unzip completed!");
        }
        return this;
    }

    /**
     * handles each CASPAC file appropriately
     *
     * @param entry entry from CASPAC
     * @param pack CASPAC file to be processed
     * @throws IOException when permission problem exists
     */
    private void handleCASPACFiles(Object entry, Unzip pack) throws IOException {

        //get the filename from the entry
        String filename = pack.getEntryName(entry);
        boolean isScript = !handleCASPACInformationFiles(filename, pack, entry);
        if (isScript) {
            handleCASPACScriptFiles(filename, pack, entry);
        }
    }

    /**
     * script instance which is being referenced. creates a new script if not
     * found
     *
     * @param fileName filename of script
     * @return script instance of script to be processed
     */
    private Script getScriptInstanceByFilename(String fileName) {
        for (Script s : scripts) {
            if (s.getName().equals(fileName.substring(0, fileName.lastIndexOf(".")))) {
                return s;
            }
        }
        Script script = new Script(sd,fileName.substring(0, fileName.lastIndexOf(".")), this.TempFolder + fileName + CASUALSessionData.slash, this.type);
        //Add script 
        scripts.add(script);
        return scripts.get(scripts.indexOf(script));
    }

    /**
     * script instance which is being referenced
     *
     * @param fileName filename of script
     * @return script instance of script to be processed null if not found
     */
    public Script getScriptByFilename(String fileName) {
        Log.level4Debug("Looking up " + fileName);
        String scriptName = "";
        try {
            scriptName = fileName.substring(0, fileName.lastIndexOf("."));
            for (Script s : scripts) {
                if (s.getName().equals(scriptName)) {
                    return s;
                }
            }

        } catch (Exception ex) {
        }
        if (!scriptName.isEmpty()) {
            Script s = new Script(sd,scriptName, this.TempFolder + scriptName + CASUALSessionData.slash, this.type);
            this.scripts.add(s);
            return this.scripts.get(scripts.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * returns all script names
     *
     * @return list of script names
     */
    public String[] getScriptNames() {
        List<String> scriptNames = new ArrayList<String>();
        for (Script s : scripts) {
            scriptNames.add(s.getName());
        }
        return StringOperations.convertArrayListToStringArray(scriptNames);
    }

    /**
     * gets script by name
     *
     * @param name name of script to be pulled
     * @return Script object or null
     */
    public Script getScriptByName(String name) {
        for (Script s : scripts) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        Script s = new Script(sd,name, this.TempFolder + CASUALSessionData.slash + name + CASUALSessionData.slash, this.type);
        this.scripts.add(s);
        return this.scripts.get(scripts.size() - 1);
    }

    private void performUnzipOnQueue() {
        Log.level3Verbose("Performing unzip of resources.");
        for (MandatoryThread t : this.unzipThreads) {
            if (!t.isComplete() && !t.isAlive()) {
                Log.level4Debug("Starting unzip of " + t.getName() + " from performUnzipOnQueue.");
                t.start();
            }
        }
    }

    private void setBuildPropInformation(Unzip pack, Object entry) throws IOException {
        Log.level4Debug("Found -build.properties adding information to "
                + "CASPAC");
        build = new Build(pack.streamFileFromZip(entry), this);
        build.loadPropsToVariables();
    }

    private void extractCASPACBanner(Unzip pack, Object entry, String filename) throws IOException {
        Log.level4Debug("Found logo adding information to "
                + "CASPAC");
        logo = ImageIO.read(ImageIO.createImageInputStream(pack.streamFileFromZip(entry)));
        if (filename.isEmpty()) {
            filename = this.TempFolder + "-logo.png";
        }
        if (build != null) {
            build.setBannerPic(filename);
        } else {
            tempbannerpic = filename;
        }

    }

    private boolean handleCASPACInformationFiles(String filename, Unzip pack, Object entry) throws IOException {
        boolean isAControlFile = false;
        if (filename.equals("-build.properties")) {
            setBuildPropInformation(pack, entry);
            isAControlFile = true;
        } else if (filename.endsWith(".png")) {
            if (filename.isEmpty()) {
                filename = this.TempFolder + "-logo.png";
            }
            extractCASPACBanner(pack, entry, filename);
            isAControlFile = true;
        } else if (filename.equals("-Overview.txt")) {
            overview = StringOperations.convertStreamToString(pack.streamFileFromZip(entry));
            isAControlFile = true;
        }
        return isAControlFile;
    }

    private void handleCASPACScriptFiles(String filename, Unzip pack, Object entry) throws IOException {
        FileOperations fo = new FileOperations();
        MD5sum md5sum = new MD5sum();
        if (filename.endsWith(".meta")) {

            Script script = getScriptInstanceByFilename(filename);
            Log.level4Debug("Found METADATA for " + script.getName() + ".");
            int i;
            if (!scripts.contains(script)) {
                Log.level4Debug(script.getName() + " not found in CASPAC adding"
                        + " script to CASPAC.");
                scripts.add(script);
            }
            i = scripts.indexOf(script);

            script.getMetaData().load(pack.streamFileFromZip(entry));
            Log.level4Debug("Added METADATA to " + script.getName() + ".");
            int md5ArrayPosition = 0;
            scripts.set(i, script);
        } else if (filename.endsWith(".scr")) {
            Script script = getScriptInstanceByFilename(filename);
            script.setScriptContents(fo.readTextFromStream(pack.streamFileFromZip(entry)));
            Log.level4Debug("Added Script for " + script.getName() + ".");
            script.getActualMD5s().add(md5sum.getLinuxMD5Sum(script.getScriptContents(), filename));

        } else if (filename.endsWith(".zip")) {
            Script script = getScriptInstanceByFilename(filename);
            script.scriptZipFile = entry;
            script.setZipfile(pack);
            CASUAL.misc.MandatoryThread t = new CASUAL.misc.MandatoryThread(script.getExtractionRunnable());
            t.setName("zip File Preparation " + unzipThreads.size());
            this.unzipThreads.add(t);

            Log.level4Debug("Added .zip to " + script.getName() + ". It will be unziped at end of unpacking.");

        } else if (filename.endsWith(".txt")) {
            Script script = getScriptInstanceByFilename(filename);
            String description = fo.readTextFromStream(pack.streamFileFromZip(entry));
            script.setDiscription(description);
            Log.level4Debug("Added Description to " + script.getName() + ".");
            script.getActualMD5s().add(md5sum.getLinuxMD5Sum(StringOperations.convertStringToStream(script.getDiscription()), filename));
        }
    }

    private void setBuild(InputStream in) throws IOException {
        Properties prop = new Properties();
        prop.load(in);
        this.setBuild(prop);
        Log.level4Debug(StringOperations.convertStreamToString(this.build.getBuildPropInputStream()));

    }

    private void handleCASPACJarFiles(String entry) throws IOException {
        FileOperations fo = new FileOperations();
        if (entry.startsWith("/")) {
            entry = entry.replaceFirst("/", "");
        }

        if (entry.equals("SCRIPTS/-Overview.txt") || entry.equals("SCRIPTS\\-Overview.txt")) {

            Log.level4Debug("processing:" + entry);
            this.overview = fo.readTextFromResource(entry);
            System.out.println("overview " + overview);
        } else if (entry.equals("SCRIPTS/-build.properties") || entry.equals("SCRIPTS\\-build.properties")) {
            Log.level4Debug("processing:" + entry);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            setBuild(in);
        } else if (entry.equals("SCRIPTS/-logo.png") || entry.equals("SCRIPTS\\-logo.png")) {
            Log.level4Debug("processing:" + entry);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            logo = ImageIO.read(ImageIO.createImageInputStream(in));
        } else if (entry.endsWith(".txt")) {
            Log.level4Debug("processing:" + entry);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            this.getScriptByFilename(entry).setDiscription(fo.readTextFromResource(entry));
        } else if (entry.endsWith(".scr")) {
            Log.level4Debug("processing:" + entry);
            System.out.println("SCRIPT CONTENTS:" + fo.readTextFromResource(entry));
            this.getScriptByFilename(entry).setScriptContents(fo.readTextFromResource(entry));

        } else if (entry.endsWith(".meta")) {
            Log.level4Debug("processing:" + entry);
            System.out.println("loading meta " + entry);
            InputStream in = getClass().getClassLoader()
                    .getResourceAsStream(entry);
            Properties prop = new Properties();
            prop.load(in);
            this.getScriptByFilename(entry).getMetaData().load(prop);
        } else if (entry.endsWith(".zip")) {
            Log.level4Debug("processing:" + entry);
            Log.level3Verbose("found zip at " + entry);
            this.getScriptByFilename(entry).scriptZipFile = entry;
        }
        Log.level4Debug("getting MD5 for:" + entry);
        new MD5sum().getLinuxMD5Sum(Caspac.class.getClassLoader().getResourceAsStream(entry), entry);
    }

    private void setupIDEModeScriptForCASUAL(String defaultPackage) {
        FileOperations fo = new FileOperations();
        Script script = this.getScriptByName(defaultPackage);

        String caspacPath = "SCRIPTS/";
        try {
            File f = new File(".");
            caspacPath = f.getCanonicalPath() + "/SCRIPTS/";
        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
        String scriptPath = caspacPath + defaultPackage;
        try {
            this.setBuild(new BufferedInputStream(new FileInputStream(caspacPath + "-build.properties")));
        } catch (FileNotFoundException ex) {
            Log.errorHandler(ex);
        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
        this.overview = fo.readFile(caspacPath + "-Overview.txt");
        String logof = caspacPath + "-logo.png";

        try {

            InputStream in = new FileInputStream(logof);
            logo = ImageIO.read(ImageIO.createImageInputStream(in));
        } catch (IOException ex) {
            //no logo for this CASPAC
        }
        build.setBannerPic(logof);

        Log.level4Debug("IDE MODE PATH=" + scriptPath);
        getScriptByName(defaultPackage).setScriptContents(fo.readFile(scriptPath + ".scr"));
        getScriptByName(defaultPackage).setDiscription(fo.readFile(scriptPath + ".txt"));
        try {
            getScriptByName(defaultPackage).getMetaData().load(new BufferedInputStream(new FileInputStream(new File(scriptPath + ".meta"))));
        } catch (FileNotFoundException ex) {
            //no meta, its not requried
        }
        getScriptByName(defaultPackage).scriptZipFile = (scriptPath + ".zip");
    }

    private void loadCASPACcontrolFilesFromCASPAC() throws IOException {
        FileOperations fo = new FileOperations();
        //if the CASPAC exists lets try to grab the non-script files 
        if (fo.verifyExists(CASPAC.getAbsolutePath()) && CASPAC.canRead()) {
            try {
                Unzip unzip = new Unzip(CASPAC);
                Enumeration<? extends ZipEntry> cpEnumeration = unzip.zipFileEntries;
                if (cpEnumeration.hasMoreElements()) {
                    while (cpEnumeration.hasMoreElements()) {
                        Object o = cpEnumeration.nextElement();
                        if (unzip.getEntryName(o).contains("-Overview.txt")) {
                            this.overview = fo.readTextFromStream(unzip.streamFileFromZip(o));
                        }
                        if (unzip.getEntryName(o).contains("-build.properties")) {
                            this.build = new Build(unzip.streamFileFromZip(o), this);
                        }
                        if (unzip.getEntryName(o).contains("-logo.png")) {
                            extractCASPACBanner(unzip, o, overview);
                        }
                    }
                }

            } catch (ZipException ex) {
                Log.errorHandler(ex);
            }

        }
    }

    private void updateMD5s() {
        MandatoryThread update;
        update = new MandatoryThread(CASUALTools.updateMD5s);
        update.setName("Updating MD5s");
        update.start(); //ugly  move this to somewhere else
        Log.level3Verbose("IDE Mode: Using " + CASUAL.CASUALMain.defaultPackage + ".scr ONLY!");
    }

    /**
     * checks for updates.
     *
     * @param s Script to be checked
     * @return true if script can continue. false if halt is recommended.
     * @throws java.net.MalformedURLException should never happen
     * @throws java.net.URISyntaxException should never happen.
     */
    public Script updateIfRequired(Script s) throws MalformedURLException, URISyntaxException, IOException {
        return s;
        //TODO reenable SCRIPT updates. 
    }

    /**
     * sets the CASPAC location
     *
     * @param f File to use for new CASPAC location
     * @return this CASPAC
     */
    public Caspac setCASPACLocation(File f) {
        this.CASPAC = f;
        return this;
    }

    /**
     * gets the CASPAC location
     *
     * @return path to CASPAC
     */
    public File getCASPACLocation() {
        return CASPAC;
    }

    /*
    if (s.metaData.minSVNversion.isEmpty()) {
    return s;
    }
    int mySVNVersion=Integer.parseInt(java.util.ResourceBundle
    .getBundle("CASUAL/resources/CASUALApp")
    .getString("Application.revision"));
    int myScriptVersion=Integer.parseInt(s.metaData.scriptRevision);
    String myScriptName=s.name;
    CASUALUpdates ci=new CASUALUpdates();
    Properties updatedprop=new Properties();
    Log.level3Verbose("creating new script instance to compare against online version");
    Script updatedScript=new Script(s);
    new File(s.tempDir).mkdirs();
    Log.level3Verbose("getting updated script version info");
    //TODO: downloadMetaFromRepoForScript hangs.  Script will not complte unzip because of this.  Updates are down
    updatedprop.load(ci.downloadMetaFromRepoForScript(s));
    Log.level3Verbose("updating meta");
    updatedScript.metaData.load(updatedprop);
    int updatedSVNVersion=Integer.parseInt(updatedScript.metaData.minSVNversion);
    int updatedScriptVersion=Integer.parseInt(updatedScript.metaData.scriptRevision);
    Log.level3Verbose("comparing script information");
    if (mySVNVersion<updatedSVNVersion){
    updatedScript.scriptContents="";
    Log.level2Information("\n"+updatedScript.metaData.killSwitchMessage);
    return updatedScript;
    } else if (myScriptVersion< updatedScriptVersion){
    Log.level2Information("@scriptIsOutOfDate");
    Log.level2Information("\n"+updatedScript.metaData.updateMessage);
    updatedScript=ci.updateScript(updatedScript,this.TempFolder);
    return updatedScript;
    } else {
    Log.level2Information("@noUpdateRequired");
    return s;
    }
    } */
    /**
     * replaces a script in list array.
     *
     * @param s script to replace
     * @return location of script in scripts array.
     */
    public int replaceScriptByName(Script s) {
        String name = s.getName();
        for (int i = 0; i < this.scripts.size(); i++) {
            if (scripts.get(i).getName().equals(name)) {
                scripts.set(i, s);
                return i;
            }
        }
        return -1;
    }

    /**
     * gets the type of CASPAC
     *
     * @return type of CASPAC
     */
    public int getType() {

        String s = Integer.toString(type);

        return type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String n = System.getProperty("line.separator");
        sb.append("Scripts:").append(this.scripts.size()).append(n);
        sb.append("Working Dir: ").append(this.TempFolder).append(n);
        sb.append(this.build.getBuildProp().toString());

        return sb.toString();
    }

    /**
     * @param type the type to set
     */
    private void setType(int type) {
        this.type = type;
    }

    /**
     * @return the logo
     */
    public BufferedImage getLogo() {
        return logo;
    }

    /**
     * @param logo the logo to set
     * @return this CASPAC
     */
    public Caspac setLogo(BufferedImage logo) {
        this.logo = logo;
        return this;
    }

    /**
     * @return the CASPAC
     */
    public File getCASPAC() {
        return CASPAC;
    }

    /**
     * @param CASPAC the CASPAC to set
     * @return this CASPAC
     */
    public Caspac setCASPAC(File CASPAC) {
        this.CASPAC = CASPAC;
        return this;
    }

    /**
     * @return the CASPACsrc
     */
    public CodeSource getCASPACsrc() {
        return CASPACsrc;
    }

    /**
     * @param CASPACsrc the CASPACsrc to set
     * @return this CASPAC
     */
    public Caspac setCASPACsrc(CodeSource CASPACsrc) {
        this.CASPACsrc = CASPACsrc;
        return this;
    }

    /**
     * @return the overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     * @param overview the overview to set
     * @return this CASPAC
     */
    public Caspac setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    /**
     * @return the build
     */
    public Build getBuild() {
        return build;
    }

    /**
     * @param build the build to set
     * @return this CASPAC
     */
    public Caspac setBuild(Build build) {
        this.build = build;
        return this;
    }

    /**
     * @return the scripts
     */
    public List<Script> getScripts() {
        return scripts;
    }

    /**
     * @param scripts the scripts to set
     * @return this CASPAC
     */
    public Caspac setScripts(ArrayList<Script> scripts) {
        this.scripts = scripts;
        return this;
    }

    /**
     * @return the TempFolder
     */
    public String getTempFolder() {
        return TempFolder;
    }

    /**
     * @param TempFolder the TempFolder to set
     * @return this CASPAC
     */
    public Caspac setTempFolder(String TempFolder) {
        this.TempFolder = TempFolder;
        return this;
    }

    /**
     * @return the unzipThreads
     */
    public ArrayList<CASUAL.misc.MandatoryThread> getUnzipThreads() {
        return unzipThreads;
    }

    /**
     * @param unzipThreads the unzipThreads to set
     * @return this CASPAC
     */
    public Caspac setUnzipThreads(ArrayList<CASUAL.misc.MandatoryThread> unzipThreads) {
        this.unzipThreads = unzipThreads;
        return this;
    }

    /**
     * @return the caspacShouldBeDeletedAfterExtraction
     */
    public boolean isCaspacShouldBeDeletedAfterExtraction() {
        return caspacShouldBeDeletedAfterExtraction;
    }

    /**
     * @param caspacShouldBeDeletedAfterExtraction the
     * caspacShouldBeDeletedAfterExtraction to set
     * @return this CASPAC
     */
    public Caspac setCaspacShouldBeDeletedAfterExtraction(boolean caspacShouldBeDeletedAfterExtraction) {
        this.caspacShouldBeDeletedAfterExtraction = caspacShouldBeDeletedAfterExtraction;
        return this;
    }

    /**
     * @return the tempbannerpic
     */
    public String getTempbannerpic() {
        return tempbannerpic;
    }

    /**
     * @param tempbannerpic the tempbannerpic to set
     * @return this CASPAC
     */
    public Caspac setTempbannerpic(String tempbannerpic) {
        this.tempbannerpic = tempbannerpic;
        return this;
    }

    /**
     * @return the controlFiles
     */
    public String[] getControlFiles() {
        return controlFiles;
    }

    /**
     * @param controlFiles the controlFiles to set
     * @return this CASPAC
     */
    public Caspac setControlFiles(String[] controlFiles) {
        this.controlFiles = controlFiles;
        return this;
    }

    /**
     * @return the sd
     */
    public CASUALSessionData getSd() {
        return sd;
    }


}
