package CASUAL.archiving.libpit;
/*Pitdata provides a way to work with the header information of the PIT file
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Pitdata provides a way to work with the header information of the PIT file
 * Original Files may be found here:
 * https://github.com/Benjamin-Dobell/libpit--Java-/tree/master/libpit/src/au/com/glassechidna/libpit
 * modified by: Adam Outler
 *
 * @author Benjamin Dobell
 */
public class PitData {

    /**
     * Magic Number to identify an Odin File.
     */
    public static final int FILE_IDENTIFIER = 0x12349876;
    /**
     * number of entries available to be read.
     */
    public int entryCount;
    /**
     * PIT Type in Char array. eg. new char[]{C,O,M,_,T,A,R,2}
     */
    char[] fileType = new char[8];
    /**
     * Pit name in char array. eg new char[]{M,S,M,8,9,2,0,\0,\0,\0,\0,\0} where
     * \0 is a 0-byte
     */
    char[] pitName = new char[12];

    // Entries start at 0x1C
    private final ArrayList<PitEntry> entries = new ArrayList<PitEntry>();
    ByteArrayOutputStream signature = new ByteArrayOutputStream();

    /**
     * Constructor for new PIT file.
     */
    public PitData() {
    }

    /**
     * Constructor to grab PIT file from an InputStream
     *
     * @param PitStream inputStream containing a PIT file only
     */
    public PitData(PitInputStream PitStream) {
        unpack(PitStream);
    }

    /**
     * Constructor to grab PIT file from a File
     *
     * @param pit PIT file
     * @throws FileNotFoundException {@inheritDoc}
     */
    public PitData(File pit) throws FileNotFoundException {
        this(new PitInputStream(new FileInputStream(pit)));
    }

    /**
     * unpacks a PIT into the PitData and its PitEntry classes
     *
     * @param pitInputStream InputStream containing only a Pit FIle
     * @return true if unpack was performed
     */
    public final boolean unpack(PitInputStream pitInputStream) {
        try {
            int pitID = pitInputStream.readInt();
            if (pitID != FILE_IDENTIFIER) {
                return (false);
            }

            entries.clear();

            entryCount = pitInputStream.readInt();

            entries.ensureCapacity(entryCount);

            //read 8 bytes of filetype
            for (int i = 0; i < 8; i++) {
                fileType[i] = (char) pitInputStream.read();
            }

            //read 12 bytes of phone name
            for (int i = 0; i < 12; i++) {
                pitName[i] = (char) pitInputStream.read();
            }

            byte[] buffer = new byte[PitEntry.FILENAME_MAX_LENGTH];

            for (int i = 0; i < entryCount; i++) {
                PitEntry entry = new PitEntry();
                entries.add(entry);

                entry.setBinType(pitInputStream.readInt());
                entry.setDeviceType(pitInputStream.readInt());
                entry.setPartID(pitInputStream.readInt());
                entry.setPartitionType(pitInputStream.readInt());
                entry.setFilesystem(pitInputStream.readInt());
                entry.setBlockStart(pitInputStream.readInt());
                entry.setBlockCount(pitInputStream.readInt());
                entry.setFileOffset(pitInputStream.readInt());
                entry.setFileSize(pitInputStream.readInt());

                //read partition name
                pitInputStream.read(buffer, 0, PitEntry.PARTITION_NAME_MAX_LENGTH);
                entry.setPartitionName(buffer);

                //read filename
                pitInputStream.read(buffer, 0, PitEntry.FILENAME_MAX_LENGTH);
                entry.setFilename(buffer);

                //read fota name
                pitInputStream.read(buffer, 0, PitEntry.FOTA_NAME_MAX_LENGTH);
                entry.setFotaName(buffer);

            }

            int byteRead;
            while ((byteRead = pitInputStream.read()) != -1) {
                signature.write(byteRead);
            }
            return (true);
        } catch (IOException e) {
            return (false);
        }
    }

    /**
     * Packs current object into a PIT file
     *
     * @param dataOutputStream dataoutputstream to write to
     * @return true if sucessful
     */
    public boolean pack(DataOutputStream dataOutputStream) {
        try {

            dataOutputStream.writeInt(Integer.reverseBytes(FILE_IDENTIFIER));

            dataOutputStream.writeInt(Integer.reverseBytes(entryCount));
            for (int i = 0; i < fileType.length; i++) {
                dataOutputStream.write( fileType[i]);
            }
            for (int i = 0; i < pitName.length; i++) {
                dataOutputStream.write(pitName[i]);
            }

            for (int i = 0; i < entryCount; i++) {
                PitEntry entry = entries.get(i);
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getBinType()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getDevType()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getPartID()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getPartitionType()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getFilesystem()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getBlockStart()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getBlockCount()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getFileOffset()));
                dataOutputStream.writeInt(Integer.reverseBytes(entry.getFileSize()));
                dataOutputStream.write(entry.getPartitionNameBytes());
                dataOutputStream.write(entry.getFileNameBytes());
                dataOutputStream.write(entry.getFotaNameBytes());
            }
            dataOutputStream.write(signature.toByteArray());

            return (true);
        } catch (IOException e) {
            return (false);
        }
    }

    /**
     * tests for a match on a PitData
     *
     * @param otherPitData second pit data to be ested
     * @return true if match
     */
    public boolean matches(PitData otherPitData) {
        return this.toString().equals(otherPitData.toString());
    }

    /**
     * clears all data in the pit file.
     */
    public void clear() {
        entryCount = 0;
        fileType = new char[8];
        pitName = new char[12];
        entries.clear();
    }

    /**
     * Gets a PitEntry by index
     *
     * @param index index of entry
     * @return PitEntry at index
     */
    public PitEntry getEntry(int index) {
        return (entries.get(index));
    }

    /**
     * gets a PitEntry by Partition name
     *
     * @param partitionName partition name to be matched
     * @return PitEntry matched by name
     */
    public PitEntry findEntry(String partitionName) {
        for (int i = 0; i < entries.size(); i++) {
            PitEntry entry = entries.get(i);
            String s = entry.getPartitionName().trim();

            if (entry.getPartitionName().equals(partitionName)) {
                return (entry);
            }
        }

        return (null);
    }

    /**
     * gets a PitEntry by filename
     *
     * @param filename filename in pit entry
     * @return PitEntry matched on filename
     */
    public PitEntry findEntryByFilename(String filename) {
        for (int i = 0; i < entries.size(); i++) {
            PitEntry entry = entries.get(i);
            String nameCheck = "";
            for (char c : entry.file_name) {
                if (c == 0) {  //character signifying the end of the name and the beginning of modifier "md5"
                    break;
                } else {
                    nameCheck += c;
                }
            }

            if (filename.equals(nameCheck)) {
                return (entry);
            }
        }
        return (null);
    }

    /**
     * Gets a PitEntry based on partition ID
     *
     * @param partitionIdentifier identifier to match
     * @return PitEntry matched on PartitionIdentifier
     */
    public PitEntry findEntry(int partitionIdentifier) {
        for (int i = 0; i < entries.size(); i++) {
            PitEntry entry = entries.get(i);

            if (entry.getPartID() == partitionIdentifier) {
                return (entry);
            }
        }

        return (null);
    }

    /**
     * Removes a PitEntry from the list of PitEntries
     *
     * @param entry entry to be removed
     */
    public void removeEntry(PitEntry entry) {
        entries.remove(entry);
    }

    /**
     * Adds a PitEntry to the list of entries
     *
     * @param entry entry to be added
     */
    public void addEntry(PitEntry entry) {
        entries.add(entryCount++, entry);
    }

    /**
     * gets the number of entries
     *
     * @return entry count
     */
    public int getEntryCount() {
        return (entryCount);
    }

    /**
     * returns the file type
     *
     * @return file type
     */
    public char[] getFileType() {
        return fileType;
    }

    /**
     * gets the name of the intended platform
     *
     * @return platform name
     */
    public char[] getPhone() {
        return pitName;
    }

    /**
     * returns pit name with parameters
     *
     * @return PIT friendly name with parameters
     */
    public String getPITFriendlyName() {
        String pitFriendlyName = "";
        for (int i = 0; i < pitName.length; i++) {
            //first part of file will be filename
            if (pitName[i] != 0) {
                pitFriendlyName += pitName[i];
            } else { //anything after first 0 byte will be a parameter
                while (pitName[i] == 0 && i < pitName.length - 1) {
                    i++;
                    if (pitName[i] != 0) {
                        pitFriendlyName = pitFriendlyName + "\nPIT Parameter: " + pitName[i];
                        break;
                    }
                }
            }
        }
        return pitFriendlyName;
    }

    /**
     * returns filetype friendly name with parameters
     *
     * @return File Type friendly name with parameters
     */
    public String getFileTypeFriendlyName() {
        String filetypeFriendlyName = "";
        for (int i = 0; i < fileType.length; i++) {
            //first part of file will be filename
            if (fileType[i] != 0) {
                filetypeFriendlyName += fileType[i];
            } else { //anything after first 0 byte will be a parameter
                while (fileType[i] == 0 && i < fileType.length - 1) {
                    i++;
                    if (fileType[i] != 0) {
                        filetypeFriendlyName = filetypeFriendlyName + "\nPIT Parameter: " + fileType[i];
                        break;
                    }
                }
            }
        }
        return filetypeFriendlyName;
    }

    @Override
    public String toString() {
        String n = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append(n);
        sb.append("PIT Name: ").append(getPITFriendlyName()).append(n);
        sb.append("Entry Count: ").append(this.entryCount).append(n);
        sb.append("File Type: ").append(getFileTypeFriendlyName()).append(n);
        sb.append(n);
        sb.append(n);
        for (int i = 0; i < this.entries.size(); i++) {
            sb.append("--- Entry #").append(i).append(" ---").append(n);
            sb.append(entries.get(i).toString());
        }
        sb.append(n).append(n);
        return sb.toString();
    }

    /**
     * Resizes a pitentry and adjusts starting blocks of all successive
     * pitentries.
     *
     * @param partName name of partition to be resized
     * @param changeToSize the amount to change the entry This can be positive
     * or negative and will change the size relatively.
     * @throws java.lang.ClassNotFoundException {@inheritDoc}
     * @see #resizePartition(CASUAL.archiving.libpit.PitEntry, int) 
     */
    public void resizePartition(String partName, int changeToSize) throws ClassNotFoundException {
        resizePartition(this.findEntry(partName), changeToSize);
    }

    /**
     * Resizes a pitentry and adjusts starting blocks of all successive
     * pitentries.
     *
     * @param entry The entry to be resized
     * @param changeToSize the amount to change the entry This can be positive
     * or negative and will change the size relatively.
     * @throws java.lang.ClassNotFoundException {@inheritDoc}
     */
    public void resizePartition(PitEntry entry, int changeToSize) throws ClassNotFoundException {
        PitEntry[] sorted = sortEntriesByBlockLocation();
        //get entry location
        int entryLocation = -1;
        String type = entry.getPartitionTypeFriendlyName();
        for (int i = 0; i < sorted.length; i++) {
            if (entry == sorted[i]) {
                entryLocation = i;
                break;
            }
        }
        //halt if not found
        if (entryLocation == -1) {
            throw new ClassNotFoundException("The PitEntry Specified was not found:" + entry);
        }
        //resize the partition
        sorted[entryLocation].setBlockCount(sorted[entryLocation].getBlockCount() + changeToSize);
        //adjust the offset of the remainders
        for (int i = entryLocation + 1; i < sorted.length; i++) {
            //ignore partitions on a different device
            if (sorted[i].getPartitionTypeFriendlyName().equals(type)) {
                sorted[i].setBlockStart(sorted[i].getBlockStart() + changeToSize);
            }
        }
        //put each entry back into the original array
        for (PitEntry finalEntry : sorted) {
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getPartID() == finalEntry.getPartID()) {
                    entries.set(i, finalEntry);
                }
            }
        }

    }

    /**
     * returns an array of the PitEntries sorted by block location
     *
     * @return sorted PitEntries
     */
    public PitEntry[] sortEntriesByBlockLocation() {
        boolean enumerated = false;
        LinkedList<PitEntry> ll = new LinkedList<PitEntry>();
        ll.addAll(entries);
        //while not every entry is enumerated
        while (!enumerated) {
            enumerated = true;
            int lastBlock = 0;
            //for each entry
            for (int i = 0; i < ll.size(); i++) {
                //check
                if (lastBlock < ll.get(i).getBlockStart()) {
                    lastBlock = ll.get(i).getBlockStart();
                } else {

                    if (i != 0) {
                        enumerated = false;
                        // this entry needs to be bumped up
                        ll.add(i - 2, ll.get(i));
                        ll.remove(i + 1);
                    }

                }
            }

        }

        PitEntry[] retval = ll.toArray(new PitEntry[ll.size()]);
        return retval;
    }

}
