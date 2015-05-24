/*FileOperations provides a group of tools which relate to files.
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
package CASUAL;

import CASUAL.misc.StringOperations;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * FileOperations provides a set of acellerators for working with files in Java.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class FileOperations {

    /**
     * performs file operations
     */
    public FileOperations() {
    }

    /**
     * recursively deletes a String path
     *
     * @param path path to delete recursively
     */
    public void recursiveDelete(String path) {
        recursiveDelete(new File(path));
    }

    /**
     * recursively deletes a file path
     *
     * @param path path to delete
     */
    public void recursiveDelete(File path) {

        File[] c = path.listFiles();
        if (path.exists()) {
            Log.level4Debug("Removing folder and contents:" + path.toString());
            if (c != null && c.length > 0) {
                for (File file : c) {
                    if (file.isDirectory()) {
                        recursiveDelete(file);
                        file.delete();
                    } else {
                        file.delete();
                    }
                }
            }
            path.delete();
        }
    }

    /**
     * verify ability to write to every file in a path
     *
     * @param path path to verify.
     * @return true if permission to write
     */
    public boolean verifyWritePermissionsRecursive(String path) {

        File Check = new File(path);
        File[] c = Check.listFiles();
        if (Check.exists()) {
            Log.level4Debug("Verifying permissions in folder:" + path);
            for (File file : c) {
                if (!file.canWrite()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * takes a path and a name returns qualified path to file
     *
     * @param PathToSearch  path to search
     * @param FileName filename to locate
     * @return absolute path to folder
     */
    public String findRecursive(String PathToSearch, String FileName) {

        File Check = new File(PathToSearch);
        File[] c = Check.listFiles();
        String s = "";
        if (Check.exists()) {
            Log.level3Verbose("Searching for file in folder:" + PathToSearch);
            for (File file : c) {
                if (file.isDirectory()) {
                    return findRecursive(file.getAbsolutePath(), FileName);
                } else if (file.getName().equals(FileName)) {
                    try {
                        return file.getCanonicalPath();
                    } catch (IOException ex) {
                        Log.errorHandler(ex);
                    }

                }
            }
        }
        return s;
    }

    /**
     * verifies file/folder exists returns a boolean value if the file exists
     *
     * @param file  file to check
     * @return true if exists
     */
    public boolean verifyExists(String file) {
        if (file != null && !file.isEmpty()) {
            File f = new File(file);
            if (!f.exists() && !f.isDirectory() && !f.isFile()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * makes a folder, works recursively
     *
     * @param Folder folder to create
     * @return true if folder was created
     */
    public boolean makeFolder(String Folder) {

        if (Folder == null) {
            return false;
        }
        File folder = new File(Folder);
        if (folder.exists()) {
            return true;
        } else {
            folder.mkdirs();
            if (folder.exists()) {
                return true;
            } else {
                Log.level0Error("@couldNotCreateFolder " + Folder);
                return false;
            }
        }
    }

    /**
     * writes a stream to a destination file
     *
     * @param stream Stream to be written
     * @param destination output file
     * @throws FileNotFoundException  when destination cannot be created
     * @throws IOException with permission problems
     */
    public void writeStreamToFile(BufferedInputStream stream, String destination) throws FileNotFoundException, IOException {
        int currentByte;
        int buffer = 4096;
        byte data[] = new byte[buffer];
        File f = new File(destination);
        if (!verifyExists(f.getParent())) {
            makeFolder(f.getParent());
        }
        FileOutputStream fos = new FileOutputStream(f);
        BufferedOutputStream dest;
        dest = new BufferedOutputStream(fos, buffer);
        while ((currentByte = stream.read(data, 0, buffer)) != -1) {
            dest.write(data, 0, currentByte);
        }
        dest.flush();
        dest.close();
    }

    /**
     * takes a string and a filename, writes to the file
     *
     * @param Text text to write
     * @param File file to write to
     * @throws IOException when permission problems exist
     */
    public void writeToFile(String Text, String File) throws IOException {

        BufferedWriter bw;
        FileWriter fw = new FileWriter(File, true);
        bw = new BufferedWriter(fw);
        bw.write(Text);
        bw.flush();
        fw.close();
        Log.level4Debug("Write Finished");
    }

    /**
     * takes a string and a filename, overwrites to the file
     *
     * @param Text text to write
     * @param File file to write to
     * @throws IOException when permission problems exist
     */
    public void overwriteFile(String Text, String File) throws IOException {

        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(File, false));
        bw.write(Text);
        bw.close();
        Log.level4Debug("File overwrite Finished");
    }

    private boolean writeInputStreamToFile(InputStream is, File file) {

        Log.level4Debug("Attempting to write " + file.getPath());
        try {
            BufferedOutputStream out;
            out = new BufferedOutputStream(new FileOutputStream(file));
            int currentByte;
            // establish buffer for writing file
            int BUFFER = 4096;
            byte data[] = new byte[BUFFER];
            if (is.available() > 0) {
                // while stream does not return -1, fill data buffer and write.
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, currentByte);
                }
            } else {
                return false;
            }
            is.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            return false;
        }
        if (file.exists() && file.length() >= 4) {
            Log.level4Debug("File verified.");
            return true;
        } else {
            Log.level0Error("@failedToWriteFile");
            return false;
        }
    }

    /**
     * takes a string filename returns a boolean if the file was deleted
     *
     * @param FileName filename to delete
     * @return true if file was deleted
     */
    public Boolean deleteFile(String FileName) {

        Boolean Deleted;
        File file = new File(FileName);
        if (file.exists()) {
            if (file.delete()) {
                Deleted = true;
                Log.level4Debug("Deleted " + FileName);
            } else {
                Deleted = false;
                Log.level0Error("@couldNotDeleteFile" + FileName);
            }
        } else {
            Deleted = true;
        }
        return Deleted;
    }

    /**
     * deletes files
     *
     * @param cleanUp files to be deleted
     * @return true if all files were deleted false and halts on error
     */
    public boolean deleteStringArrayOfFiles(String[] cleanUp) {
        for (String s : cleanUp) {
            if (s != null) {
                new File(s).delete();
            } else {
                continue;
            }
            if (this.verifyExists(s)) {
                return false;
            }
        } //all files were deleted
        return true;
    }

    /**
     * copies a file from a source to a destination
     *
     * @param sourceFile  file to copy
     * @param destFile destination to copy file (including filename)
     * @throws IOException when permission problem exists
     */
    public void copyFile(File sourceFile, File destFile) throws IOException {

        Log.level4Debug("Copying " + sourceFile.getCanonicalPath() + " to " + destFile.getCanonicalPath());
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
        }
        if (destination != null) {
            destination.close();

        }

    }

    /**
     * returns the name of the current folder
     *
     * @return current folder
     */
    public String currentDir() {

        String CurrentDir = new File(".").getAbsolutePath();
        Log.level4Debug("Detected current folder: " + CurrentDir);
        if (CurrentDir.endsWith(".")) {
            CurrentDir = CurrentDir.substring(0, CurrentDir.length() - 1);
        }
        return CurrentDir;
    }

    /**
     * copies a file from a string path to a string path returns a boolean if
     * completed
     *
     * @param FromFile  file to copy
     * @param ToFile destination to copy to
     * @return true if completed
     */
    public boolean copyFile(String FromFile, String ToFile) {
        File OriginalFile = new File(FromFile);
        File DestinationFile = new File(ToFile);
        try {
            copyFile(OriginalFile, DestinationFile);
            return true;
        } catch (IOException ex) {
            return false;
        }

    }

    /**
     * takes a filename sets executable returns result
     *
     * @param Executable file to be set executable on linux/mac/unix
     * @return true if executable bit was set
     */
    public boolean setExecutableBit(String Executable) {

        File Exe = new File(Executable);
        boolean Result = Exe.setExecutable(true);
        Log.level4Debug("Setting executable " + Exe + ". Result=" + Result);
        return Result;
    }

    /**
     * takes a string resource name returns result if it exists
     *
     * @param res resource to verify
     * @return true if resource exists
     */
    public boolean verifyResource(String res) {
        return getClass().getResource(res) != null;
    }

    /**
     * takes a resource name returns a string of file contents
     *
     * @param Resource path to resource in jar or filesystem
     * @return string contents of resource
     */
    public String readTextFromResource(String Resource) {

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(Resource);
        StringBuilder text = new StringBuilder();
        try {
            InputStreamReader in;
            in = new InputStreamReader(resourceAsStream, "UTF-8");
            int read;
            while ((read = in.read()) != -1) {
                char C = (char) read;
                text.append(C);
            }
            in.close();
        } catch (NullPointerException ex) {
            Log.level0Error("@resourceNotFound:" + Resource);
        } catch (IOException ex) {
            Log.level0Error("@resourceNotFound:" + Resource);
        }
        //Log.level3(text.toString());
        return text.toString();
    }

    /**
     * reads text from stream
     *
     * @param in stream to read
     * @return text output
     */
    public String readTextFromStream(BufferedInputStream in) {
        StringBuilder text = new StringBuilder();
        try {
            int read;
            while ((read = in.read()) != -1) {
                char C = (char) read;
                text.append(C);
            }
            in.close();
        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
        //Log.level3(text.toString());
        return text.toString();
    }

    /**
     * reads file contents returns string
     *
     * @param FileOnDisk file to read
     * @return string representation of file
     */
    public String readFile(String FileOnDisk) {

        String EntireFile = "";
        try {
            String Line;
            BufferedReader br = new BufferedReader(new FileReader(FileOnDisk));
            while ((Line = br.readLine()) != null) {
                //Log.level3(Line);  
                EntireFile = EntireFile + "\n" + Line;

            }
            br.close();
        } catch (IOException ex) {
            Log.level2Information("@fileNotFound " + FileOnDisk);
        }
        EntireFile = EntireFile.replaceFirst("\n", "");
        return EntireFile;
    }

    /**
     * lists files in a folder
     *
     * @param folder folder to list
     * @return array of filenames
     */
    public String[] listFolderFiles(String folder) {
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            Log.level0Error("@fileNotAFolder");
            return null;
        }
        ArrayList<String> files = new ArrayList<String>();
        File[] list = dir.listFiles();
        for (int x = 0; list.length > x; x++) {
            files.add(list[x].getName());
        }
        return StringOperations.convertArrayListToStringArray(files);
    }

    /**
     * lists files with full qualifiers
     *
     * @param folder folder to list
     * @return array of files
     */
    public String[] listFolderFilesCannonically(String folder) {
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            Log.level0Error("\"@fileNotAFolder");
            return null;
        }
        File[] list = dir.listFiles();
        String[] childOf = new String[list.length];
        for (int x = 0; list.length > x; x++) {
            try {
                childOf[x] = list[x].getCanonicalFile().toString();
            } catch (IOException ex) {
                Log.errorHandler(ex);
            }
        }
        return childOf;
    }

    public ArrayList<String> listRecursive(String folder){
        ArrayList<String> filesList=new ArrayList<String>();
        File[] files=new File(folder).listFiles();
        for (File file:files){
            if (file.isDirectory()){
                filesList.addAll(listRecursive(file.getAbsolutePath()));
            } else {
                filesList.add(file.getAbsolutePath());
            }
        }
        
        
        return filesList;
        
    }
    
    
    /**
     *
     * @param sourceFile from locaton
     * @param destFile to location
     * @return true if moved
     * @throws IOException when permission problem exists
     */
    public boolean moveFile(File sourceFile, File destFile) throws IOException {

        FileOperations fO = new FileOperations();
        if (!destFile.getParentFile().exists()) {
            File folder = destFile.getParentFile();
            folder.mkdirs();
        }
        if (destFile.exists()) {
            Log.level3Verbose("Cannot move file.  Destination file is in the way");
            return false;
        }
        Log.level4Debug("moving " + sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());
        return sourceFile.renameTo(destFile);
    }

    /**
     * moves a file
     *
     * @param sourceFile from location
     * @param destFile to location
     * @return true if moved
     * @throws IOException when permissions problems exist
     */
    public boolean moveFile(String sourceFile, String destFile) throws IOException {

        FileOperations fo = new FileOperations();
        if (!fo.verifyExists(sourceFile)) {
            Log.level4Debug("[moveFile()] Source doesn't exist");
            return false;
        }
        if (fo.verifyExists(destFile)) {
            fo.deleteFile(destFile);
        }
        if (fo.copyFile(sourceFile, destFile)) {
            if (fo.deleteFile(sourceFile)) {
                Log.level4Debug("[moveFile()]File moved successfully");
                return true;
            } else {
                Log.level4Debug("[moveFile()]File copied, unable to remove source");
                return false;
            }
        } else {
            Log.level4Debug("[moveFile()]Unable to copy source to destination");
            return false;
        }
    }
}
