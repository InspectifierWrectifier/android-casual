/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.crypto;

import java.io.File;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class AES128HandlerTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public AES128HandlerTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of encrypt method, of class CipherHandler.
     */
    @Test
    public void testEncryptDecryptForConsistancy() {
        String input = "../../CASPAC/testpak.zip";
        String output = "../../CASPAC/testpak.enc.zip";
        String finalOutput = "../../CASPAC/testpak.zip";
        File inputFile = new File(input);
        AES128Handler instance = new AES128Handler(inputFile);
        for (int i = 0; i < 3; i++) {
            System.out.println("encrypt");
            String key = "testatesttestatestatestatest" + i;
            MD5sum md5 = new MD5sum();
            String originalMD5 = md5.md5sum(inputFile);
            //encrypt file
            assertEquals(true, instance.encrypt(output, key.toCharArray()));
            //decrypt file
            File encryptedFile = new File(output);
            AES128Handler instance2 = new AES128Handler(encryptedFile);
            System.out.println("Encrypted MD5:" + md5.getLinuxMD5Sum(encryptedFile));
            String result2 = "";
            try {
                result2 = instance2.decrypt(finalOutput, key.toCharArray());
            } catch (Exception ex) {
                Logger.getLogger(AES128HandlerTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(result2);
            String newMD5 = md5.md5sum(new File(finalOutput));
            assertEquals(originalMD5, newMD5);
        }
    }

    @Test
    public void testVectors() {
        System.out.println("Testing AES128 bit encryption mode with vectors from http://www.inconteam.com/software-development/41-encryption/55-aes-test-vectors#aes-cbc-128");
        
        
        try {
            byte[] key = hexStringToByteArray("2b7e151628aed2a6abf7158809cf4f3c");
            byte[] iv = hexStringToByteArray("000102030405060708090A0B0C0D0E0F");
            String testVector = "6bc1bee22e409f96e93d7e117393172a";
            String expectedResult = "7649abac8119b246cee98e9b12e9197d";
            System.out.println("initializing dummy file");
            AES128Handler ch = new AES128Handler(new File("../../CASPAC/testpak.zip"));
            System.out.println("getting cipher");

            Cipher c = ch.getCipher(key, iv, Cipher.ENCRYPT_MODE);
            System.out.println("encrypting");
            byte[] tresult = c.doFinal(hexStringToByteArray(testVector));
            System.out.println("got encrytped bytes, converting to hex");
            BigInteger bigInt = new BigInteger(1, tresult);
            String result = bigInt.toString(16).substring(0, 32);//remove padding
            assertEquals(expectedResult, result);
            
            
            
            iv = hexStringToByteArray("7649ABAC8119B246CEE98E9B12E9197D");
            testVector = "ae2d8a571e03ac9c9eb76fac45af8e51";
            expectedResult = "5086cb9b507219ee95db113a917678b2";
            System.out.println("getting cipher");

            c = ch.getCipher(key, iv, Cipher.ENCRYPT_MODE);
            System.out.println("encrypting");
            tresult = c.doFinal(hexStringToByteArray(testVector));
            System.out.println("got encrytped bytes, converting to hex");
            bigInt = new BigInteger(1, tresult);
            result = bigInt.toString(16).substring(0, 32);//remove padding
            assertEquals(expectedResult, result);

            iv = hexStringToByteArray("5086CB9B507219EE95DB113A917678B2");
            testVector = "30c81c46a35ce411e5fbc1191a0a52ef";
            expectedResult = "73bed6b8e3c1743b7116e69e22229516";
            System.out.println("getting cipher");

            c = ch.getCipher(key, iv, Cipher.ENCRYPT_MODE);
            System.out.println("encrypting");
            tresult = c.doFinal(hexStringToByteArray(testVector));
            System.out.println("got encrytped bytes, converting to hex");
            bigInt = new BigInteger(1, tresult);
            result = bigInt.toString(16).substring(0, 32);//remove padding
            assertEquals(expectedResult, result);

            iv = hexStringToByteArray("73BED6B8E3C1743B7116E69E22229516");
            testVector = "f69f2445df4f9b17ad2b417be66c3710";
            expectedResult = "3ff1caa1681fac09120eca307586e1a7";
            System.out.println("getting cipher");

            c = ch.getCipher(key, iv, Cipher.ENCRYPT_MODE);
            System.out.println("encrypting");
            tresult = c.doFinal(hexStringToByteArray(testVector));
            System.out.println("got encrytped bytes, converting to hex");
            bigInt = new BigInteger(1, tresult);
            result = bigInt.toString(16).substring(0, 32);//remove padding
            assertEquals(expectedResult, result);
            
            
            
            
            
            
            
            
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AES128HandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AES128HandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AES128HandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AES128HandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AES128HandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(AES128HandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
    }

    /**
     * Test of encrypt method, of class AES128Handler.
     */
    @Test
    public void testEncrypt() {
        testEncryptDecryptForConsistancy();
    }

    /**
     * Test of decrypt method, of class AES128Handler.
     * @throws java.lang.Exception
     */
    @Test
    public void testDecrypt() throws Exception {
        testEncryptDecryptForConsistancy();
    }

    /**
     * Test of oneWayHash method, of class AES128Handler.
     */
    @Test
    public void testOneWayHash() {
        System.out.println("oneWayHash");
        char[] input = "test".toCharArray();
        PBKDF2_128  instance = new PBKDF2_128();
        byte[] expResult = new byte[]{-39,7,54,-119,-22,57,-32,-92,-99,-122,69,-64,61,47,-42,-103};
        byte[] result = instance.oneWayHash(input,null);
        assertArrayEquals(expResult, result);

    }

    /**
     * Test of getCipher method, of class AES128Handler.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCipher() throws Exception {
        System.out.println("getCipher");
             byte[] key = hexStringToByteArray("2b7e151628aed2a6abf7158809cf4f3c");
            byte[] iv = hexStringToByteArray("000102030405060708090A0B0C0D0E0F");
            AES128Handler ch = new AES128Handler(new File("../../CASPAC/testpak.zip"));
            Cipher c = ch.getCipher(key, iv, Cipher.ENCRYPT_MODE);
            assertArrayEquals(iv,c.getIV());
    }

    /**
     * Test of getCASPACHeaderLength method, of class AES128Handler.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCASPACHeaderLength() throws Exception {
        System.out.println("getCASPACHeaderLength");
        int expResult = AES128Handler.header.length();
        int result=AES128Handler.getCASPACHeaderLength(new File("../../CASPAC/testpak.enc.zip"));
        assertEquals(expResult, result);

    }
}
