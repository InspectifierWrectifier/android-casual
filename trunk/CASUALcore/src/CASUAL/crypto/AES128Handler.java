/*AES128Handler provides a way to encrypt and decrypt given a password
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
package CASUAL.crypto;

import CASUAL.FileOperations;
import CASUAL.Log;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * provides a way to encrypt and decrypt given a password
 *
 * @author Adam Outler adamoutler@gmail.com
 * http://stackoverflow.com/questions/8674018/pbkdf2-with-bouncycastle-in-java
 * inspired by
 * https://www.cigital.com/justice-league-blog/2009/08/14/proper-use-of-javas-securerandom/
 * inspired by
 * http://stackoverflow.com/questions/1220751/how-to-choose-an-aes-encryption-mode-cbc-ecb-ctr-ocb-cfb
 * severely beaten several times by Pulser
 */
public class AES128Handler {



    /*these variables are used for generating a header 
     *"EncryptedCASPAC-CASUAL-Revision3999" where
     * 3 represents then number of digits in the revision
     */
    /**
     * Magic String for CASPAC.
     */
    final static private String casualID = "EncryptedCASPAC-CASUAL-Revision";
    /**
     * version for the CASPAC
     */
    final static private String revision = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.revision");
    /**
     * Header for the CASPAC
     */
    final static public String header = casualID + revision.length() + revision;

    /**
     * will return the length of the CASPAC Header
     *
     * @param f file to check
     * @return 0 if failed, will be between &gt;18 if valid.
     * @throws FileNotFoundException if file is not present or deleted
     * @throws IOException if permission problem.
     */
    public static int getCASPACHeaderLength(File f) throws FileNotFoundException, IOException {
        AES128Handler c = new AES128Handler(f);
        FileInputStream fis = new FileInputStream(f);
        byte[] chartest = new byte[casualID.length()];
        byte[] headert = casualID.getBytes();
        fis.read(chartest);
        //read length of revision
        if (Arrays.equals(chartest, headert)) {
            char charRevisionLength = (char) fis.read();
            int revisionLength = Integer.parseInt(String.valueOf(charRevisionLength));
            return chartest.length + 1 + revisionLength;
        }
        return 0;
    }
    final File targetFile;

    /**
     * loads a file for use in AES128Handler.
     *
     * @param targetFile File to be encrypted
     */
    public AES128Handler(File targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * encrypts a file to the output file. Appends CASPAC Header
     *
     * @param output string location of file output
     * @param key password
     * @return true if encryption was sucessful
     */
    public boolean encrypt(String output, char[] key) {
        Log.level2Information("Encrypting " + targetFile.getName());

        try {
            //key is infalated by 16 random characters A-Z,a-z,0-9
            //16 digits are used for ivSpec
            byte[] randomness = secureRandomCharGen(key, 16);
            Log.level2Information("Key parsed.  Encrypting...");
            InputStream fis = new FileInputStream(targetFile);
            List<InputStream> streams = Arrays.asList(
                    new ByteArrayInputStream(randomness),
                    fis);
            Log.level2Information("obtaining key...");
            InputStream is = new SequenceInputStream(Collections.enumeration(streams));
            writeCipherFile(is, randomness, output, key, Cipher.ENCRYPT_MODE);
        } catch (NoSuchAlgorithmException ex) {
            return false;
        } catch (FileNotFoundException ex) {
            Log.errorHandler(ex);
        } catch (NoSuchPaddingException ex) {
            Log.errorHandler(ex);
        } catch (InvalidKeyException ex) {
            Log.errorHandler(ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Log.errorHandler(ex);
        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
        //key is returned.
        return true;
    }

    /**
     * decrypts a file
     *
     * @param output string name of file to output
     * @param key password issued by encrytper
     * @return name of file written, null if error
     * @throws java.io.FileNotFoundException if file isn't able to be found
     * @throws java.lang.Exception if crypto error
     */
    public String decrypt(String output, char[] key) throws Exception {
        try {
            FileInputStream fis = new FileInputStream(targetFile);
            int headersize = getCASPACHeaderLength(targetFile);
            if (headersize < 10) {
                throw new Exception("Invalid CASPAC Format");
            }

            fis.read(new byte[headersize]);
            byte[] IV = new byte[16];
            fis.read(IV);
            return writeCipherFile(fis, IV, output, key, Cipher.DECRYPT_MODE);
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }

    private InputStream appendStream(InputStream appendToFront, InputStream is) {
        List<InputStream> streams = Arrays.asList(
                appendToFront, is);
        InputStream newis = new SequenceInputStream(Collections.enumeration(streams));
        return newis;
    }

    private String writeCipherFile(InputStream fis, byte[] iv, String output, char[] key, int mode) throws NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, FileNotFoundException, IOException {
        byte[] bkey = new PBKDF2_128().oneWayHash(key,null);
        Cipher c = getCipher(bkey, iv, mode);
        CipherInputStream cis = new CipherInputStream(fis, c);

        if (mode == Cipher.ENCRYPT_MODE) {
            InputStream headerbytes = new ByteArrayInputStream(header.getBytes());
            InputStream doOutput = appendStream(headerbytes, (InputStream) cis);
            new FileOperations().writeStreamToFile(new BufferedInputStream(doOutput), output);
        } else {
            new FileOperations().writeStreamToFile(new BufferedInputStream(cis), output);
        }
        return output;
    }

    private byte[] secureRandomCharGen(char[] key, int numberOfChars) throws NoSuchAlgorithmException {

        Log.level4Debug("Generating randomness");
        SecureRandom random = new SecureRandom(SecureRandom.getSeed(key.length));
        byte bytes[] = new byte[numberOfChars];
        random.nextBytes(bytes);  //burn some bits
        byte[] temp = new byte[1];
        for (int i = 0; i < numberOfChars - 1; i++) {
            random.nextBytes(temp);
            bytes[i] = temp[0];
            //generate a new random generator
            random = new SecureRandom();
            random.nextBytes(new byte[key.length]);//burn bits
        }
        return bytes;
    }

    /**
     * gets a cypher for encryption
     *
     * @param key secret key
     * @param iv initialization vector which is pulled from or appended to the
     * file
     * @param mode encryption or decryption key
     * @return cypher to be used for encryption or decryption.
     * @throws NoSuchPaddingException If Java implementation is incomplete
     * @throws NoSuchAlgorithmException If Java implementation is incomplete
     * @throws InvalidKeyException If Java implementation is incomplete
     * @throws InvalidAlgorithmParameterException If Java implementation is
     * incomplete
     */
    public Cipher getCipher(byte[] key, byte[] iv, int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        SecretKeySpec skey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(mode, skey, ivspec);
        return c;
    }
}
