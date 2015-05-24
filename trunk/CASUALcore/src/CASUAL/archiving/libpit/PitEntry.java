package CASUAL.archiving.libpit;
/*PitEntry provides a method of organizing PIT entries and storing PIT data
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

import java.util.Arrays;

/**
 * PitEntry provides a method of organizing PIT entries and storing PIT data
 * Original Files may be found here:
 * https://github.com/Benjamin-Dobell/libpit--Java-/tree/master/libpit/src/au/com/glassechidna/libpit
 * modified by: Adam Outler
 *
 * @author Benjamin Dobell
 */
public class PitEntry {

    /**
     * maximum byte length of part_name
     */
    public static final int PARTITION_NAME_MAX_LENGTH = 32;

    /**
     * maximum byte length of file_name
     */
    public static final int FILENAME_MAX_LENGTH = 32;

    /**
     * maximum byte length of fota_name
     */
    public static final int FOTA_NAME_MAX_LENGTH = 32;

    private int bin_type;
    private int device_type;
    private int part_id;
    private int part_type;
    private int filesystem;
    private int block_start;
    private int block_count;
    private int file_offset;
    private int file_size;

    /**
     * Partition name.
     */
    public char[] part_name = new char[32];

    /**
     * File name.
     */
    public char[] file_name = new char[32];

    /**
     * Firmware Over The Air name.
     */
    public char[] fota_name = new char[32];

    /**
     * Constructor for PitEntry sets default values
     */
    public PitEntry() {

        device_type = 0;
        block_start = 0;
        part_id = 0;
        part_type = 0;
        file_offset = 0;
        file_size = 0;
        block_count = 0;
        filesystem = 0;
        bin_type = 0;
    }

    /**
     * matches this entries parameters against another to detect equivalence.
     *
     * @param otherPitEntry entry to match against
     * @return true if match
     */
    public boolean matches(PitEntry otherPitEntry) {
        return this.toString().equals(otherPitEntry.toString());
    }

    /**
     * The major hardware structure that the partition belongs to. Call
     * processor or App Processor
     *
     * @return type of binary
     */
    public int getBinType() {
        return bin_type;
    }

    /**
     * gets the friendly name of bin AP or CP
     *
     * @return AP or CP
     */
    public String getBinFriendlyType() {
        if (bin_type == 0) {
            return "AP";
        } else {
            return "CP";
        }
    }

    /**
     * binary type
     *
     * @param binType unsigned integer
     */
    public void setBinType(int binType) {
        this.bin_type = binType;
    }

    /**
     * Device Type differs per-device. generally 0=emmc.
     *
     * @return device type
     */
    public int getDevType() {
        return device_type;
    }

    /**
     * Device Type differs per-device. generally 0=emmc.
     *
     * @param devType unsigned integer
     */
    public void setDeviceType(int devType) {
        this.device_type = devType;
    }

    /**
     * Partition ID is a number which identifies the partition
     *
     * @return partition identifier
     */
    public int getPartID() {
        return (part_id);
    }

    /**
     * Partition ID is a number which identifies the partition
     *
     * @param partitionIdentifier unsigned integer
     */
    public void setPartID(int partitionIdentifier) {
        this.part_id = partitionIdentifier;
    }

    /**
     * Partition Type bootloader, data, bct... This is the type of partition.
     *
     * @return attributes field in PIT
     */
    public int getPartitionType() {
        return (part_type);
    }

    /**
     * Partition Attributes
     *
     * @param partitionFlags unsigned integer
     */
    public void setPartitionType(int partitionFlags) {
        this.part_type = partitionFlags;
    }

    /**
     * rfs=0 raw=1 ext4=2
     *
     * @return filesystem type
     */
    public int getFilesystem() {
        return filesystem;
    }

    /**
     * sets filesystem type rfs=0 raw=1 ext4=2
     *
     * @param filesystem unsigned integer
     */
    public void setFilesystem(int filesystem) {
        this.filesystem = filesystem;
    }

    /**
     * starting block on EMMC in 512b blocks
     *
     * @return starting block
     */
    public int getBlockStart() {
        return (block_start);
    }

    /**
     * starting block on EMMC in 512b blocks
     *
     * @param blockStart unsigned integer
     */
    public void setBlockStart(int blockStart) {
        this.block_start = blockStart;
    }

    /**
     * number of 512b blocks in partition
     *
     * @return block count
     */
    public int getBlockCount() {
        return (block_count);
    }

    /**
     * number of 512b blocks in partition
     *
     * @param partitionBlockCount unsigned integer
     */
    public void setBlockCount(int partitionBlockCount) {
        this.block_count = partitionBlockCount;
    }

    /**
     * number of blocks to offset in partition before beginning write
     *
     * @return block offset
     */
    public int getFileOffset() {
        return (file_offset);
    }

    /**
     * number of blocks to offset in partition before beginning write
     *
     * @param fileOffset unsigned integer
     */
    public void setFileOffset(int fileOffset) {
        this.file_offset = fileOffset;
    }

    /**
     * size of file in bytes
     *
     * @return partition size in bytes
     */
    public int getFileSize() {
        return (file_size);
    }

    /**
     * size of file in bytes
     *
     * @param partitionBlockSize unsigned integer
     */
    public void setFileSize(int partitionBlockSize) {
        this.file_size = partitionBlockSize;
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @return byte representation of partition name
     */
    public byte[] getPartitionNameBytes() {
        return convertCharArrayToByteArray(part_name);
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @return partition name
     */
    public String getPartitionName() {
        String partitionName = "";
        return new String(part_name).trim();
    }
    
    /**
     * Proper name of partition used to reference flash location
     *
     * @return partition name
     */
    public String getOdinFlashablePartitionName() {
        String partitionName = "";
        for (int i = 0; i < part_name.length; i++) {
            //get first part of filename
            if (part_name[i] == 0) { //break on first \0 byte.
                break;
            } else {
                partitionName += part_name[i];
            }
        }
        return partitionName;
    }

    /**
     * Proper Friendly name and parameters of partition used to reference flash
     * location
     *
     * @return partition name
     */
    public String getPartitionFriendlyName() {
        String filename = "";
        for (int i = 0; i < part_name.length; i++) {
            //first part of file will be filename
            if (part_name[i] != 0) {
                filename += part_name[i];
            } else { //anything after first 0 byte will be a parameter
                while (part_name[i] == 0 && i < part_name.length - 1) {
                    i++;
                    if (part_name[i] != 0) {
                        filename = filename + "   param: " + part_name[i];
                        break;
                    }
                }
            }

        }
        return (filename);
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @param partitionName unsigned integer
     */
    public void setPartitionName(byte[] partitionName) {
        part_name = convertByteArrayToCharArray(partitionName);
    }

    /**
     * Proper name of partition used to reference flash location
     *
     * @param partitionName unsigned integer
     */
    public void setPartitionName(String partitionName) {
        if (partitionName.length() < part_name.length) { // "Less than" due to null byte.
            part_name = Arrays.copyOf(partitionName.toCharArray(), part_name.length);
        } else {
            partitionName = partitionName.substring(0, part_name.length - 1);
            part_name = Arrays.copyOf(partitionName.toCharArray(), part_name.length);
        }
    }

    /**
     * Name of file when transferred from device
     *
     * @return byte representation of filename
     */
    public byte[] getFileNameBytes() {
        return convertCharArrayToByteArray(file_name);
    }

    /**
     * Name of file when transferred from device
     *
     * @return file name
     */
    public String getFilenameString() {
        String filename = "";
        for (int i = 0; i < file_name.length; i++) {
            if (file_name[i] != 0) {
                filename += file_name[i];
            }
        }
        return (filename);
    }

    /**
     * Name of file when transferred from device
     *
     * @return file name
     */
    public String getFriendlyFileName() {
        String filename = "";
        for (int i = 0; i < file_name.length; i++) {
            //first part of file will be filename
            if (file_name[i] != 0) {
                filename += file_name[i];
            } else { //anything after first 0 byte will be a parameter
                while (file_name[i] == 0 && i < file_name.length - 1) {
                    i++;
                    if (file_name[i] != 0) {
                        filename = filename + "   param: " + file_name[i];
                        break;
                    }
                }
            }

        }
        return (filename);
    }

    /**
     * Name of file when transferred from device
     *
     * @param filename filename in pit entry
     */
    public void setFilename(byte[] filename) {
        file_name = convertByteArrayToCharArray(filename);
    }

    /**
     * Name of file when transferred from device
     *
     * @param filename filename in pit entry
     */
    public void setFilename(String filename) {
        if (filename.length() < file_name.length) { // "Less than" due to null byte.
            file_name = Arrays.copyOf(filename.toCharArray(), file_name.length);
        } else {
            filename = filename.substring(0, file_name.length - 1);
            file_name = Arrays.copyOf(filename.toCharArray(), file_name.length);
        }
    }

    /**
     * Name of file when receiving an OTA update
     *
     * @return byte representation of FOTA name
     */
    public byte[] getFotaNameBytes() {
        return convertCharArrayToByteArray(fota_name);
    }

    /**
     * Name of file when receiving an OTA update
     *
     * @return FOTA name
     */
    public String getFotaName() {
        String fotaname = "";
        for (int i = 0; i < fota_name.length; i++) {
            if (fota_name[i] != 0) {
                fotaname += fota_name[i];
            }
        }
        return fotaname;
    }

    /**
     * Proper Friendly name and parameters of partition used to reference flash
     * location
     *
     * @return partition name
     */
    public String getFOTAFriendlyName() {
        String fotaname = "";
        for (int i = 0; i < fota_name.length; i++) {
            //first part of file will be filename
            if (fota_name[i] != 0) {
                fotaname += fota_name[i];
            } else { //anything after first 0 byte will be a parameter
                while (fota_name[i] == 0 && i < fota_name.length - 1) {
                    i++;
                    if (fota_name[i] != 0) {
                        fotaname = fotaname + "   param: " + fota_name[i];
                        break;
                    }
                }
            }

        }

        return (fotaname);
    }

    /**
     * Name of file when receiving an OTA update
     *
     * @param fotaName name to set for FOTA entry
     */
    public void setFotaName(byte[] fotaName) {
        fota_name = convertByteArrayToCharArray(fotaName);
    }

    /**
     * Name of file when receiving an OTA update
     *
     * @param fotaName name to set for FOTA entry
     */
    public void setFotaName(String fotaName) {
        if (fotaName.length() < file_name.length) { // "Less than" due to null byte.
            fota_name = Arrays.copyOf(fotaName.toCharArray(), fota_name.length);
        } else {
            fotaName = fotaName.substring(0, file_name.length - 1);
            fota_name = Arrays.copyOf(fotaName.toCharArray(), fota_name.length);
        }
    }

    /**
     * returns partition type friendly name bct, bootloader, data, mbr, ebr,
     * gp1, gpt, unknown
     *
     * @return friendly name of the partition type
     */
    public String getPartitionTypeFriendlyName() {
        switch (this.part_type) {
            case 0:
                return "Raw";
            case 1:
                return "Bct";
            case 2:
                return "Bootloader";
            case 4:
                return "Data";
            case 5:
                return "Data";
            case 6:
                return "MBR";
            case 7:
                return "EBR";
            case 8:
                return "GP1";
            case 9:
                return "GPT";
            default:
                return "undocumented";
        }
    }

    /**
     * gets the friendly name of the filesystem type raw, basic, enhanced, ext2,
     * yaffs2, unknown
     *
     * @return friendly filesystem type name
     */
    public String getFilesystemTypeFriendlyName() {
        switch (this.filesystem) {
            case 0:
                return "raw";
            case 1:
                return "Basic";
            case 2:
                return "Enhanced";
            case 3:
                return "EXT2";
            case 4:
                return "YAFFS2";
            case 5:
                return "EXT4";
            default:
                return "undocumented";
        }
    }

    /**
     * returns the friendly hardware type nand, emmc, spi, ide, nand_x16,
     * unknown
     *
     * @return the name of the hardware device
     */
    public String getDeviceTypeFriendlyName() {
        switch (this.device_type) {
            case 1:
                return "NAND";
            case 2:
                return "EMMC";
            case 3:
                return "SPI";
            case 4:
                return "IDE";
            case 5:
                return "NAND_X16";
            default:
                return "undocumented";

        }
    }

    //http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    /**
     * convert block count into human-readable form.
     *
     * @param si use SI units (KB=1000B) or binary (KiB=1024B)
     * @return human readable bytes from block count
     */
    public String getBlockCountFriendly(boolean si) {
        long bytes = (long) block_count * 512;
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + "B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f%sB", bytes / Math.pow(unit, exp), pre);

    }

    /**
     * gets the partition Description of the entry in human readable form.
     *
     * @return partition description.
     */
    public String getPartitionDescritpion() {
        String n = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("The ").append(this.getPartitionFriendlyName());
        sb.append(" partition, ");
        if (this.getPartID() >= 0) {
            sb.append("identified as partition number ").append(this.getPartID());
        } else {
            sb.append(" is invalid");
            return sb.toString();
        }
        sb.append(", is ").append(getBlockCountFriendly(true)).append(" in size and carries a ")
                .append(this.getFilesystemTypeFriendlyName())
                .append(" format. This partition resides on the ")
                .append(this.getPartitionTypeFriendlyName())
                .append(" section of the ")
                .append(this.getBinFriendlyType()).append(" ")
                .append(this.getDeviceTypeFriendlyName()).append(".");

        if (!this.getFriendlyFileName().isEmpty() && !this.getFriendlyFileName().startsWith("-")) {
            sb.append(" It identifies itself to Odin as ").append(this.getFriendlyFileName()).append(".");
        }
        if (this.file_offset != 0 && this.file_size != 0) {
            sb.append("The partition carries a filesize of ").append(this.file_size).append(" and an offset of ").append(this.file_offset).append(".");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        /*  original entry from Heimdall
         Binary Type: 0 (AP)
         Device Type: 2 (MMC)
         Identifier: 73
         Attributes: 5 (Read/Write)
         Update Attributes: 1 (FOTA)
         Partition Block Size/Offset: 30777311
         Partition Block Count: 33
         File Offset (Obsolete): 0
         File Size (Obsolete): 0
         Partition Name: SGPT
         Flash Filename: sgpt.img
         FOTA Filename: 
         */
        StringBuilder sb = new StringBuilder();
        String n = System.getProperty("line.separator");
        sb.append("ID: ").append(this.part_id);
        sb.append("   Partition Name: ").append(this.getPartitionFriendlyName()).append(n);
        sb.append("Filename: ").append(this.getFriendlyFileName()).append(n);
        sb.append("Block Size: ").append(this.block_count).append(" (").append(getBlockCountFriendly(true)).append(")").append(n);
        sb.append("Block range: ").append(this.block_start).append(" - ").append(getPartitionEndBlock());
        sb.append(" (hex 0x").append(Integer.toHexString(this.block_start)).append(" - 0x").append(Integer.toHexString(getPartitionEndBlock())).append(")").append(n);
        sb.append("FilesystemType: ").append(this.filesystem);
        sb.append("   PartType: ").append(this.part_type);
        sb.append("   DevType: ").append(this.device_type);
        sb.append("   BinType: ").append(this.bin_type).append(n);
        sb.append("Offset:").append(this.file_offset);
        sb.append("   Size: ").append(this.file_size);
        sb.append("   FOTA: ").append(this.getFOTAFriendlyName()).append(n);
        sb.append(getPartitionDescritpion());
        if (this.getFotaName().contains("remained")) {
            sb.append(" The partition will expand to fill the remainder of the ").append(this.getDeviceTypeFriendlyName()).append(".");
        }
        sb.append(n).append(n).append(n);
        return sb.toString();
    }

    /**
     * calculated value for partition start + partition size -1 to account for
     * first block's usage.
     *
     * @return last block used by partition
     */
    private int getPartitionEndBlock() {
        return this.block_start + this.block_count - 1;
    }

    /**
     * converts a byte array to an equivalent char array
     *
     * @param byteArray array of bytes to be converted into chars
     * @return byte representation of char array
     */
    public char[] convertByteArrayToCharArray(byte[] byteArray) {
        char[] retval = new char[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            retval[i] = (char) byteArray[i];
        }
        return retval;
    }

    /**
     * converts a char array to an equivalent byte array
     *
     * @param charArray array to be converted into bytes
     * @return byte representation of char array
     */
    public byte[] convertCharArrayToByteArray(char[] charArray) {
        byte[] retval = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            retval[i] = (byte) charArray[i];
        }
        return retval;
    }

}
