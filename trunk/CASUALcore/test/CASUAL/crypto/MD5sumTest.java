/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.crypto;

import CASUAL.misc.StringOperations;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class MD5sumTest {
    
    public MD5sumTest() {
    }
    
    @Before
    public void setUp() {
    }

   @Test
    public void testMd5sum_InputStream() {
       
        System.out.println("md5sum");
        System.out.println("Test Vectors Implemented from http://www.nsrl.nist.gov/testdata/");
        String vector="abc";
        MD5sum instance = new MD5sum();
        String expResult = "900150983CD24FB0D6963F7D28E17F72".toLowerCase();
        String result = instance.md5sum(StringOperations.convertStringToStream(vector));
        assertEquals(expResult, result);
        System.out.println(result);
        vector="abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        instance = new MD5sum();
        expResult = "8215EF0796A20BCAAAE116D3876C664A".toLowerCase();
        result = instance.md5sum(StringOperations.convertStringToStream(vector));
        assertEquals(expResult, result);
        System.out.println(result);
        vector="";
        System.out.println("Generating test of 1,000,000 a's");
        for (int i=0;i<1000;i++){
            //takes 30seconds to generate one-at-a-time, 1 second to generate 1,000 at a time.
            vector += "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        }
        System.out.println("MD5'ing A's");
        instance = new MD5sum();
        expResult = "7707D6AE4E027C70EEA2A935C2296F21".toLowerCase();
        result = instance.md5sum(StringOperations.convertStringToStream(vector));
        assertEquals(expResult, result);
        System.out.println(result);
        
    }

}