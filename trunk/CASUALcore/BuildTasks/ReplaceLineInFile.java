/*ReplaceLineInFile provides a method of hunting down and replacing a line in a file
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class ReplaceLineInFile {

    /**
     * replaces entire lines in files
     *
     * @param args 1. file or folder to scan 2. old line contents to be replaced
     * 3. new line contents to be replaced
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                return;
            }
            File folder = new File(args[0]);
            System.out.println("Checking " + folder.getCanonicalPath() + " for matches");
            if (folder.isDirectory()) {
                File[] list=folder.listFiles();
                for (File f : list) {
                    scanFile(f, args);
                }
            } else if (folder.isFile()) {
                scanFile(folder, args);
            } else {
                System.out.println("File Not Found");
            }
        } catch (IOException ex) {
            Logger.getLogger(ReplaceLineInFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void scanFile(File file, String[] args) throws IOException {
        System.out.println("Scanning " + file.getCanonicalPath());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(file + "temporary"));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals(args[1])) {
                line = args[2];
            }
            writer.write(line+"\n");
        }
        writer.close();
        reader.close();
        file.delete();
        new File(file + "temporary").renameTo(file);

    }
}
