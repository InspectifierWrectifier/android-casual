/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASPACcreator;

import CASUAL.FileOperations;
import java.io.File;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class CASPACcreatorTest {
    
    public CASPACcreatorTest() {
    }
    static String outfile;
     @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
        new CASUAL.FileOperations().deleteFile(outfile);
    }

    @Test
    public void CASPACcreatorTest() {
        try {
            outfile = "testtmptest";
            String input1 = new File("../../repo/driver.properties").getCanonicalPath();
            String input2 = new File("../../repo/heimdall.properties").getCanonicalPath();
            String checking = "[TEST]TESTING ";
            String verified = "[TEST]VERIFIED output file ";


            FileOperations fo = new FileOperations();
            String[] args = new String[]{"--output", outfile, input1};
            System.out.println("[TEST]Verifying inputs");
            assertEquals(true, new FileOperations().verifyExists(input1));
            assertEquals(true, new FileOperations().verifyExists(input2));
            fo.deleteFile(outfile);
            System.out.println("[TEST]Beginning test");
            CASPACcreator.main(args);
            String out = new File(outfile).getCanonicalPath();
            System.out.println(checking + out);
            System.out.println(verified + out + " exists");

            args = new String[]{"--force", "--output", outfile, input1, input2};
            CASPACcreator.main(args);
            System.out.println(checking + out);
            assertEquals(true, fo.verifyExists(out));
            assert (new File(out).length()>100);
            System.out.println(verified + out + " exists");
            fo.deleteFile(out);
            fo.deleteFile(outfile);
            /* expected output
             [TEST]Verifying inputs
             [DEBUG]Deleted testtmptest
             [TEST]Beginning test
             [VERBOSE]Added testtmptest
             [INFO]Successfully created zip file at: testtmptest
             [TEST]TESTING /home/adam/code/android-casual/trunk/CASCADE/CASPACcreator/testtmptest
             [TEST]VERIFIED output file /home/adam/code/android-casual/trunk/CASCADE/CASPACcreator/testtmptest exists
             [VERBOSE]force overwrite of output file.
             [VERBOSE]Added testtmptest
             [INFO]Successfully created zip file at: testtmptest
             [TEST]TESTING /home/adam/code/android-casual/trunk/CASCADE/CASPACcreator/testtmptest
             [TEST]VERIFIED output file /home/adam/code/android-casual/trunk/CASCADE/CASPACcreator/testtmptest exists
             */
        } catch (IOException ex) {

        }
    }
}