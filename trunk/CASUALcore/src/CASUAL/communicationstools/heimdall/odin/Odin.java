/*Odin provides a set of tools to make CASUAL operate using Odin parameters. 
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
package CASUAL.communicationstools.heimdall.odin;


import CASUAL.archiving.libpit.PitData;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;
import org.apache.commons.compress.archivers.ArchiveException;

/**
 * provides a set of tools to make CASUAL operate using Odin parameters.
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class Odin {
    final private PitData pit;
    public Odin(File pit) throws FileNotFoundException {
        this.pit=new PitData(pit);
    }
    
    public String[] getHeimdallFileParametersFromOdinFile(String tempFolder,File[] files) throws CorruptOdinFileException, FileNotFoundException {
        //Sorted set allows for only one instance of each file to exist
        SortedSet<File> set = new java.util.TreeSet<File>(); 
        
        //decompress Odin files into regular files and track them
        for (File file:files){
            OdinFile o;
            try {
                o = new OdinFile(file);
            o.extractOdinContents(tempFolder);
            set.addAll(Arrays.asList(o.extractOdinContents(tempFolder)));
            } catch (IOException ex) {
                throw new FileNotFoundException();
            } catch (NoSuchAlgorithmException ex) {
                throw new CorruptOdinFileException("This computer cannot handle the file");
            } catch (CorruptOdinFileException ex) {
                throw new CorruptOdinFileException("The Archive is corrupt");
            } catch (ArchiveException ex) {
                throw new CorruptOdinFileException("The Archive is corrupt");
            }
        }
        
        //create list of --PARTITION filename for Heimdall
        ArrayList<String> list = new ArrayList<String>();
        for (File f:set){
            String partname=pit.findEntryByFilename(f.getName()).getPartitionName();
            list.add("--"+partname);
            list.add(f.getAbsolutePath());
        }
        
        //return that list. 
        return list.toArray(new String[list.size()]);
    }
}
