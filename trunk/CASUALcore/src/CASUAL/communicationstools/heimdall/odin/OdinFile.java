/*Opens Odin tar, tar.md5, tar.gz, and tar.md5.gz and performs consistancy checks 
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

/**
 * Opens odin files and performs consistancy checks
 *
 * @author Adam Outler adamoutler@gmail.com
 */
public class OdinFile {

    final private BufferedInputStream odinStream;
    final File odinFile;
    final String actualMd5;
    String expectedMd5 = "";
    ArrayList<File> files = null;
    TarArchiveInputStream tarStream;
    /**
     * type 0=tar 1=tar.md5 2=tar.md5.gz.
     */
    final int type;

    /**
     * Opens an Odin file and verifies MD5sum
     *
     * @param odinFile file to be opened and verified
     * @throws CorruptOdinFileException Odin checks did not pass
     * @throws FileNotFoundException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @throws NoSuchAlgorithmException {@inheritDoc}
     * @throws org.apache.commons.compress.archivers.ArchiveException
     * {@inheritDoc}
     */
    public OdinFile(File odinFile) throws FileNotFoundException, IOException, NoSuchAlgorithmException, CorruptOdinFileException, ArchiveException {
        this.odinFile = odinFile;
        this.odinStream = new BufferedInputStream(new FileInputStream(odinFile));

        String name = odinFile.getName();
        if (name.endsWith("tar")) {
            actualMd5 = "";
            type = 0;
        } else if (name.endsWith("tar.md5")) {
            actualMd5 = getActualAndExpectedOdinMd5();
            if (!expectedMd5.equals(actualMd5)) {
                throw new CorruptOdinFileException(odinFile.getCanonicalPath());
            }
            System.out.println("verified file " + odinFile.getCanonicalPath());
            type = 1;
        } else if (name.endsWith("tar.gz.md5")) {
            actualMd5 = getActualAndExpectedOdinMd5();
            if (!expectedMd5.equals(actualMd5)) {
                throw new CorruptOdinFileException(odinFile.getCanonicalPath());
            }
            System.out.println("verified file " + odinFile.getCanonicalPath());
            type = 2;
        } else {//(name.endsWith("tar.gz")) {
            actualMd5 = "";
            type = 3;
        }
        //open a tar.gz stream for tar.gz and tar.md5.gz
        if (type == 2 || type == 3) {
            GZIPInputStream gzis = new GZIPInputStream(odinStream);
            tarStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", gzis);
            //open a tar stream for .tar and tar.md5
        } else {
            tarStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", odinStream);
        }

    }

    /**
     * Opens an Odin file and verifies MD5sum
     *
     * @param odinFile file to be opened and verified
     * @throws CorruptOdinFileException Odin checks did not pass
     * @throws FileNotFoundException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @throws NoSuchAlgorithmException {@inheritDoc}
     * @throws org.apache.commons.compress.archivers.ArchiveException
     * {@inheritDoc}
     */
    public OdinFile(String odinFile) throws FileNotFoundException, IOException, NoSuchAlgorithmException, CorruptOdinFileException, ArchiveException {
        this(new File(odinFile));
    }

    /**
     * Extracts Odin contents to outputDir
     *
     * @param outputDir temp folder
     * @return an array of files extracted from Odin Package
     * @throws IOException {@inheritDoc}
     * @throws ArchiveException {@inheritDoc}
     * @throws NoSuchAlgorithmException {@inheritDoc}
     */
    public File[] extractOdinContents(String outputDir) throws IOException, ArchiveException, NoSuchAlgorithmException {
        if (files != null) {
            //for sucessive calls
            return files.toArray(new File[files.size()]);
        }
        files = new ArrayList<File>();
        TarArchiveEntry entry;
        //parse the entries
        while ((entry = (TarArchiveEntry) tarStream.getNextEntry()) != null) {
            final File outputFile = new File(outputDir, entry.getName());
            //make folders
            if (entry.isDirectory()) {
                if (!outputFile.exists()) {
                    System.out.println("creating dir:" + outputFile.getCanonicalFile());
                    if (!outputFile.mkdirs()) {
                        throw new IllegalStateException();
                    }
                }
                //create files
            } else {
                final OutputStream outputFileStream = new FileOutputStream(outputFile);
                System.out.println("decompressing file:" + outputFile.getCanonicalFile());
                byte[] buffer = new byte[1024 * 1024];
                int len;
                while ((len = tarStream.read(buffer)) >= 0) {
                    outputFileStream.write(buffer, 0, len);
                }
                outputFileStream.close();
            }
            //add files to output array
            files.add(outputFile);
        }
        return files.toArray(new File[files.size()]);
    }

    /**
     * runs through a file and builds an MD5 of the actual file. Stops when last
     * block size is not 512 bytes as a Tar will be 512.
     *
     * @return OdinFile's actual MD5
     * @throws IOException {@inheritDoc}
     * @throws FileNotFoundException {@inheritDoc}
     * @throws NoSuchAlgorithmException {@inheritDoc}
     */
    private String getActualAndExpectedOdinMd5() throws IOException, FileNotFoundException, NoSuchAlgorithmException {
        FileInputStream fis;
        fis = new FileInputStream(odinFile);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[512];
        //read MD5 from file, break on the incomplete block (tar is 512 byte blocks)
        while ((fis.read(buffer)) == 512) {
            if (buffer[511] == 0xff) {
                break;
            }
            digest.update(buffer);
        }
        //last block will be MD5sum in Odin tar.gz format
        for (byte b : buffer) {
            //only read until end of MD5
            if (b == 0xff) {
                break;
            }
            expectedMd5 += (char) b;
        }

        //Create actual MD5sum from messageDigest
        byte[] md5sum = digest.digest();
        BigInteger bigInt = new BigInteger(1, md5sum);
        String localactualMd5 = bigInt.toString(16);
        while (localactualMd5.length() != 32) {
            localactualMd5 = "0" + localactualMd5;
        }

        //split expectedMd5sum from filename and only check sum
        expectedMd5 = expectedMd5.split("  ")[0];
        return localactualMd5;
    }

}
