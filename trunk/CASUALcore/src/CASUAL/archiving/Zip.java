/*Zip zips files
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
 * 
 * parts of this file are from http://stackoverflow.com/questions/3048669/how-can-i-add-entries-to-an-existing-zip-file-in-java
 * 
 */
package CASUAL.archiving;

import CASUAL.CASUALSessionData;
import CASUAL.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *Provides methods of accessing zip tools. 
 * @author Adam Outler adamoutler@gmail.com
 */
public class Zip {

    private static void copy(File in, File out) throws FileNotFoundException, IOException {
        copy(in, new FileOutputStream(out));
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    private static void copy(File file, OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        copy(in, out);
    }

    final CASUALSessionData sd;
    final private File outputZip;
    private final Log log = new Log();
    private String TempFolder;
    byte[] BUFFER = new byte[4096];

    /**
     * Constructor for the Zip class.
     * <p>
     * The File set in this is not the folder where the files to be zipped are,
     * but instead the actual file that will be created by the zip.
     * <p>
     * Example: ./test.zip
     *
     * @param sd The CASUALSessionData instace to use for this.
     * @param zip output file to be worked with
     * @throws IOException  {@inheritDoc}
     */
    public Zip(CASUALSessionData sd,File zip) throws IOException {
        this.TempFolder = sd.getTempFolder();
    this.sd=sd;
    this.outputZip = zip;
    }

    /**
     * Getter for the TempFolder where the files are to be transfered into
     * before they get steamed into a zip file.
     *
     * @return the string for the location of the TempFolder
     */
    public String getTempFolder() {
        return TempFolder;
    }

    /**
     * Changes the depth of the tempfolder.
     * <p>
 This is used to specify a different temp folder then the tempfolder
 stated in CASUAL.CASUALSessionData.getInstance(). It will add a new folder within that tempfolder
 to be used too add all the files that must be zipped up into.
 <p>
     * If the folder does not exist it will be created.
     *
     * @param TempFolder string of name of folder to dive into
     */
    public void addToTempFolderLoc(String TempFolder) {
        this.TempFolder = this.TempFolder + CASUALSessionData.slash + TempFolder;
        if (!(new File(this.TempFolder).exists())) {
            new File(this.TempFolder).mkdirs();
        }
    }

    /**
     * Streams a file directly into a zipfile.
     * <p>
     * This bypasses the uses of temp folder to stream the selected file into a
     * zip folder by writing the ZipOutputStream from an existing zip file
     * directly into the ZipInputStream of another.
     *
     * @param fileToAdd file to be added
     * @throws IOException  {@inheritDoc}
     */
    public void addFilesToExistingZip(String fileToAdd) throws IOException {
        File file = new File(fileToAdd);
        addFilesToExistingZip(new File[]{file});
    }

    /**
     * Streams files directly into a zipfile.
     * <p>
     * This bypasses the uses of temp folder to stream the selected files into a
     * zip folder by writing the ZipOutputStream from an existing zip file
     * directly into the ZipInputStream of another.
     *
     * @param filesToBeZipped file to be added
     * @throws IOException {@inheritDoc}
     * @see ZipInputStream 
     * @see ZipOutputStream
     */
    public void addFilesToExistingZip(String[] filesToBeZipped) throws IOException {
        File[] fileList = new File[filesToBeZipped.length];
        int i = 0;
        for (String file : filesToBeZipped) {
            fileList[i] = new File(file);
        }

        addFilesToExistingZip(fileList);
    }

    /**
     * Streams a file directly into a zipfile.
     * <p>
     * This bypasses the uses of temp folder to stream the selected file into a
     * zip folder by writing the ZipOutputStream from an existing zip file
     * directly into the ZipInputStream of another.
     *
     * @param fileToAdd file to be added
     * @throws IOException  {@inheritDoc}
     */
    public void addFilesToExistingZip(File fileToAdd) throws IOException {
        addFilesToExistingZip(new File[]{fileToAdd});
    }

    /**
     * Streams files directly into a zipfile.
     * <p>
     * This bypasses the uses of temp folder to stream the selected files into a
     * zip folder by writing the ZipOutputStream from an existing zip file
     * directly into the ZipInputStream of another.
     * <p>
     * This method is used once the File[] has been created.
     *
     * @param files files to be zipped
     * @throws IOException {@inheritDoc}
     */
    public void addFilesToExistingZip(File[] files) throws IOException {
        if (!outputZip.exists()){
            outputZip.createNewFile();
        }
        // get a temp file
        File tempFile = File.createTempFile(outputZip.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
        getTemporaryOutputZip(tempFile, BUFFER);
        ZipOutputStream out;
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        out = prepareZipFileForMoreEntries(zin, files);
        // Compress the files into the zip
        for (File file : files) {
            InputStream in = new FileInputStream(file);
            writeEntryToZipFile(out, file.getName(), in);
        }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }

    /**
     * Streams an InputStream directly into a zipfile.
     * <p>
     * This takes an InputStream and streams it directly into a zipfile.
     *
     * @param in the InputStream to be injected into the zipfile
     * @param name the name of the File that the InputStream will create inside
     * the zip
     * @throws IOException {@inheritDoc}
     * @see InputStream
     */
    public void streamEntryToExistingZip(InputStream in, String name) throws IOException {
        File tempFile = File.createTempFile(outputZip.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
        getTemporaryOutputZip(tempFile, BUFFER);
        ZipOutputStream out;
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        out = prepareZipFileForMoreEntries(zin, name);
        // Compress the files into the zip
        writeEntryToZipFile(out, name, in);
        // Complete the ZIP file
        out.close();
        tempFile.delete();


    }

    /**
     * This merges two separate zipfiles into a single file.
     * <p>
     * Injects the zip into the root of the initial zip.
     *
     * @param injectionZip string of the zipfile
     *
     */
    public void injectZip(String injectionZip) {
        injectZip(new File(injectionZip), "");
    }

    /**
     * This merges two separate zipfiles into a single file.
     * <p>
     * Injects the zip into the root of the initial zip.
     *
     * @param injectionZip the zip that is to be injected
     *
     */
    public void injectZip(File injectionZip) {
        injectZip(injectionZip, "");
    }

    /**
     * This merges two separate zipfiles into a single file.
     * <p>
     * If the injection path is empty, the root of the injected files will be
     * the root of the initial zip. If it is not the location will injected at
     * the path relative to the root of the current zip
     *
     * @param injectionZip the zip that is to be injected
     * @param injectionPath the path relative to the root of the
     */
    public void injectZip(File injectionZip, String injectionPath) {
        try {
            if (!injectionPath.isEmpty()) {
                if (injectionPath.startsWith(CASUALSessionData.slash)) {
                    injectionPath = injectionPath.replaceFirst(CASUALSessionData.slash, "");
                }
                if (!injectionPath.endsWith(CASUALSessionData.slash)) {
                    injectionPath = injectionPath.concat(CASUALSessionData.slash);
                }
            }
            byte[] buf = new byte[1024];
            ZipInputStream zin = new ZipInputStream(new FileInputStream(injectionZip));
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputZip));
            ZipEntry entry = zin.getNextEntry();
            while (entry != null) {
                out.putNextEntry(entry);
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                entry = zin.getNextEntry();
            }
            zin.close();
            if (!injectionZip.exists()) {
                Log.level0Error("The file " + injectionZip.getAbsolutePath() + " doesn't exist please make sure it is the right location.");
                return;
            }
            zin = new ZipInputStream(new FileInputStream(injectionZip));
            entry = zin.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                if (injectionPath.isEmpty()) {
                    out.putNextEntry(entry);
                } else {
                    out.putNextEntry(new ZipEntry(injectionPath + name));
                }
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                entry = zin.getNextEntry();
            }
            out.close();

        } catch (FileNotFoundException ex) {
            Log.errorHandler(ex);
        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
    }

    /**
     * Streams multiple InputStream directly into a zipfile.
     * <p>
     * This takes an a keyed pair of String names, and InputStreams and streams
     * them directly into a zipfile.
     *
     * @param nameStream map that contains keys that are Strings, and values
     * that are InputStream
     * @return zip file
     * @throws IOException {@inheritDoc}
     * @see Map
     * @see InputStream
     */
    public File streamEntryToExistingZip(Map<String, InputStream> nameStream) throws IOException {
        File tempFile = File.createTempFile(outputZip.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        getTemporaryOutputZip(tempFile, BUFFER);
        ZipOutputStream out;
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        out = prepareZipFileForMoreEntries(zin, nameStream);

        for (Map.Entry<String, InputStream> entry : nameStream.entrySet()) {
            // Compress the files into the zip
            writeEntryToZipFile(out, entry.getKey(), entry.getValue());
            entry.getValue().close();
        }
        // Complete the ZIP file
        out.close();
        
       return tempFile;

    }

    /**
     * STATIC Creates a new zip from a folder.
     * <p>
     * This method creates a new zip file from the folder that is handed in the
     * second argument.
     *
     * @param newZip output .zip File
     * @param toBeZipped File or folder to be placed in the Zip File.
     * @throws Exception {@inheritDoc}
     */
    public void addFolderFilesToNewZip(String newZip, String toBeZipped) throws Exception {
        File directory = new File(toBeZipped);
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(newZip);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File kid : directory.listFiles()) {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        } finally {
            res.close();
        }
    }

    private void addFileToZipDir(File file) throws IOException {

        if (!file.exists()) {
            Log.level0Error("File: " + file.toString() + " not found while adding to zip");
            return;
        }

        //First we need to create the file (empty) in the temp directory if its
        //not there all ready
        File fileToAdd = new File(TempFolder + CASUALSessionData.slash + file.getName());
        if (!fileToAdd.exists()) {
            fileToAdd.createNewFile();
        }

        //Create two file channels (effectivly Filestreamers with pointers)
        //Well take the source and read it into the file.
        FileChannel source = null;
        FileChannel dest = null;
        
        
        //Now stream from one channel to the other, and close once files are
        //filled
        try {
            source = new FileInputStream(file).getChannel();
            dest = new FileOutputStream(fileToAdd).getChannel();
            dest.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (dest != null) {
                dest.close();
            }
        }

    }

    //SHOULD ONLY BE CALLED FROM addDirectory
    private void addFileToZipDir(File file, File destFolder) throws IOException {
        if (!file.exists()) {
            Log.level0Error("File: " + file.toString() + " not found while adding to zip.");
            return;
        }

        //First we need to create the file (empty) in the temp directory if its
        //not there all ready
        File fileToAdd = new File(destFolder.toString() + CASUALSessionData.slash + file.getName());
        if (!fileToAdd.exists()) {
            fileToAdd.createNewFile();
        }

        copy(fileToAdd, destFolder);

    }

    /**
     * adds a file to a zip directory.
     * @param file file to add
     * @throws IOException {@inheritDoc}
     */
    public void addFileToZipDIr(File file) throws IOException {
        if (!file.exists()) {
            Log.level0Error("File: " + file.toString() + " not found while adding to zip.");
            return;
        }
        if (file.isFile()) {
            addFileToZipDir(file);
        }

        if (file.isDirectory()) {
            addDirectoryToZipDir(file, null);
        }


    }

    private void addDirectoryToZipDir(File folder, File parent) throws IOException {
        File dirToAdd;
        if (parent == null) {
            dirToAdd = new File(TempFolder + CASUALSessionData.slash + folder.getName());
        } else {
            dirToAdd = new File(parent.toString() + CASUALSessionData.slash + folder.getName());
        }
        if (!dirToAdd.exists()) {
            dirToAdd.mkdir();
        }
        for (File c : folder.listFiles()) {
            if (c.isDirectory()) {
                addDirectoryToZipDir(c, dirToAdd);
            } else {
                addFileToZipDir(c, dirToAdd);
            }
        }

    }

    /**
     * STATIC Compresses a folder into a .zip file
     *
     * @param file folder to be compressed
     * @throws FileNotFoundException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */    public void compressZipDir(String file) throws FileNotFoundException, IOException {
        zipDir(file, "");
    }

    /**
     * Compresses the TempFolder into a .zip file
     *
     * @throws FileNotFoundException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
     public void compressZipDir() throws FileNotFoundException, IOException {
        zipDir(TempFolder, "");
    }

    /**
     * Zip up a directory
     *
     * @param directory
     * @param path
     * @throws IOException {@inheritDoc}
     */
     private void zipDir(String directory, String path) throws IOException {
         ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputZip));
        zipDir(directory, zos, path);

    }

    /**
     * Zip up a directory path
     * @param directory directory to zip
     * @param zos outputstream to output into
     * @param path path inside zip
     * @throws IOException {@inheritDoc}
     */
     public void zipDir(String directory, ZipOutputStream zos, String path) throws IOException {
         File zipDir = new File(directory);
         // get a listing of the directory content
         String[] dirList = zipDir.list();
         byte[] readBuffer = new byte[2156];
         int bytesIn;
         // loop through dirList, and zip the files
         for (String dirList1 : dirList) {
             File f = new File(zipDir, dirList1);
             if (f.isDirectory()) {
                 String filePath = f.getPath();
                 zipDir(filePath, zos, path + f.getName() + "/");
                 continue;
             }
             FileInputStream fis = new FileInputStream(f);
             ZipEntry anEntry = new ZipEntry(path + f.getName());
             zos.putNextEntry(anEntry);
             bytesIn = fis.read(readBuffer);
             while (bytesIn != -1) {
                 zos.write(readBuffer, 0, bytesIn);
                 bytesIn = fis.read(readBuffer);
            }
        }
    }

     private void getTemporaryOutputZip(File tempFile, byte[] buf) throws IOException, RuntimeException {
         //try rename
         boolean renameOk = outputZip.renameTo(tempFile);
         boolean copyOk = false;
         //if rename fails, make copy
         
         if (!renameOk) {
             if (tempFile.exists()){
                 tempFile.delete();
             }
             tempFile.createNewFile();
             FileOutputStream out;
             FileInputStream in = new FileInputStream(outputZip);
             out = new FileOutputStream(tempFile);
             int len;
             while ((len = in.read(buf)) > 0) {
                 out.write(buf, 0, len);
             }
             in.close();
             
             out.close();
             copyOk = true;
         }
         if (!renameOk && !copyOk) {
             throw new IOException("could not rename or copy the file " + outputZip.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
    }

     private ZipOutputStream prepareZipFileForMoreEntries(ZipInputStream zin, File[] files) throws FileNotFoundException, IOException {
         
         
         String[] namesToCheck = new String[files.length];
         for (int i = 0; i < files.length; i++) {
             namesToCheck[i] = files[i].getName();
        }
         return this.prepareZipFileForMoreEntries(zin, namesToCheck);
     }
     
     private ZipOutputStream prepareZipFileForMoreEntries(ZipInputStream zin, String name) throws FileNotFoundException, IOException {
        String[] namesToCheck = new String[]{name};
        return this.prepareZipFileForMoreEntries(zin, namesToCheck);
     }
     
     private ZipOutputStream prepareZipFileForMoreEntries(ZipInputStream zin, Map<String, InputStream> nameStream) throws FileNotFoundException, IOException {
         String[] namesToCheck = nameStream.keySet().toArray(new String[nameStream.size()]);
         return this.prepareZipFileForMoreEntries(zin, namesToCheck);
     }
     
     private ZipOutputStream prepareZipFileForMoreEntries(ZipInputStream zin, String[] namesToCheck) throws FileNotFoundException, IOException {
         ZipOutputStream out;
         out = new ZipOutputStream(new FileOutputStream(outputZip));
         //ZipEntry entry = zin.getNextEntry();
         ZipEntry entry;
         while ((entry = zin.getNextEntry()) != null) {
             boolean skipEntryInFavorOfNewEntry = false;
             for (String newEntryName : namesToCheck) {
                 if (newEntryName.equals(entry.getName())) {
                     skipEntryInFavorOfNewEntry = true;
                 }
             }
             if (!skipEntryInFavorOfNewEntry) {
                 String name = entry.getName();
                 // Add ZIP entry to output stream.
                 out.putNextEntry(new ZipEntry(name));
                 // Transfer bytes from the ZIP file to the output file
                 int len;
                while ((len = zin.read(BUFFER)) > 0) {
                    out.write(BUFFER, 0, len);
                }
             }
         }
         return out;
     }
     
     private void writeEntryToZipFile(ZipOutputStream out, String file, InputStream in) throws IOException {
         out.putNextEntry(new ZipEntry(file));
         // Transfer bytes from the file to the ZIP file
         int len;
         while ((len = in.read(BUFFER)) > 0) {
             out.write(BUFFER, 0, len);
         }
         // Complete the entry
         out.closeEntry();
     }
     
     public void removeAllEntries() {
         this.outputZip.delete();
         try {
             new ZipOutputStream(new FileOutputStream(outputZip)).closeEntry();
         } catch (FileNotFoundException ex) {
            Logger.getLogger(Zip.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Zip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
