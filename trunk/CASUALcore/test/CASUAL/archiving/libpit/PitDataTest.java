/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.archiving.libpit;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class PitDataTest {
    static File pitFile;

    @BeforeClass
    public static void setUpClass() throws Exception {
        pitFile = new File("../test/CASUAL/archiving/resources/ekgc100part.pit");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    public PitDataTest() {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of pack method, of class PitData.
     */
    @Test
    public void testPack() {
        try {
            System.out.println("pack");
            String testFile = CASUAL.CASUALSessionData.newInstance().getTempFolder() + "test.pit";
            PitData instance = new PitData(pitFile);
            instance.pack(new DataOutputStream(new FileOutputStream(testFile)));
            System.out.println("packed " + testFile);
            PitData test = new PitData(new File(testFile));
            assert (test.matches(instance));
            String s = instance.getEntry(0).getFilenameString();
            assert (s.equals("sboot.binmd5"));
            String origSHA256sum = new CASUAL.crypto.SHA256sum(new File(testFile)).getSha256();
            String newSHA256sum = new CASUAL.crypto.SHA256sum(pitFile).getSha256();
            assert newSHA256sum.equals(origSHA256sum);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of matches method, of class PitData.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        testPack();
    }

    /**
     * Test of clear method, of class PitData.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        PitData instance = new PitData();
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        instance.clear();
        assert (instance.getEntryCount() == 0);
    }

    /**
     * Test of getEntry method, of class PitData.
     */
    @Test
    public void testGetEntry() {
        System.out.println("getEntry");
        PitData instance = new PitData();
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        String test = instance.getEntry(0).getFilenameString();
        assert (test.equals("sboot.binmd5"));
        test = instance.getEntry(1).getFilenameString();
        assert (test.equals("tz.imgmd5"));
        test = instance.getEntry(2).getFilenameString();
        assert (test.equals("camera.pit"));
        test = instance.getEntry(3).getFilenameString();
        assert (test.equals("md5.imgin.md5"));
        test = instance.getEntry(4).getFilenameString();
        assert (test.equals("-"));
        test = instance.getEntry(5).getFilenameString();
        assert (test.equals("-"));
        test = instance.getEntry(6).getFilenameString();
        assert (test.equals("efs.imgmd5"));
        test = instance.getEntry(7).getFilenameString();
        assert (test.equals("param.binmd5"));
        test = instance.getEntry(8).getFilenameString();
        assert (test.equals("boot.imgmd5"));
        test = instance.getEntry(9).getFilenameString();
        assert (test.equals("recovery.imgmd5"));
        test = instance.getEntry(10).getFilenameString();
        assert (test.equals("modem.binmd5"));
        test = instance.getEntry(11).getFilenameString();
        assert (test.equals("cache.imgmd5"));
        test = instance.getEntry(12).getFilenameString();
        assert (test.equals("system.imgmd5"));
        test = instance.getEntry(13).getFilenameString();
        assert (test.equals("hidden.imgmd5"));
        test = instance.getEntry(14).getFilenameString();
        assert (test.equals("-"));
        test = instance.getEntry(15).getFilenameString();
        assert (test.equals("-erdata.imgmd5"));
        test = instance.getEntry(16).getFilenameString();
        assert (test.equals("userdata.img"));
    }

    /**
     * Test of findEntry method, of class PitData.
     */
    @Test
    public void testFindEntry_String() {
        System.out.println("findEntry");
        String partitionName = "BOOTLOADER";
        PitData instance = new PitData();
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        String expResult = "sboot.binmd5";
        PitEntry result = instance.findEntry(partitionName);
        assertEquals(expResult, result.getFilenameString());
    }

    /**
     * Test of findEntry method, of class PitData.
     */
    @Test
    public void testFindEntry_int() {
        try {
            System.out.println("findEntry");
            int partitionIdentifier = 4;
            PitData instance = new PitData(pitFile);
            String expResult = "PARAM";
            PitEntry result = instance.findEntry(partitionIdentifier);
            assertEquals(expResult, result.getPartitionName());
            result = instance.findEntry(6);
            assertEquals("RECOVERY", result.getPartitionName());
            result = instance.findEntry(2);
            assertEquals("BOTA1", result.getPartitionName());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of addEntry method, of class PitData.
     */
    @Test
    public void testAddEntry() {
        try {
            System.out.println("addEntry");
            PitEntry entry = null;
            PitData instance = new PitData(pitFile);
            int size = instance.getEntryCount();
            PitEntry expresult = instance.findEntry(10);
            instance.addEntry(expresult);
            PitEntry result = instance.getEntry(size);
            assert (result.getPartitionName().equals(expresult.getPartitionName()));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getEntryCount method, of class PitData.
     */
    @Test
    public void testGetEntryCount() {
        try {
            System.out.println("getEntryCount");
            PitData instance = new PitData(pitFile);
            int expResult = 17;
            int result = instance.getEntryCount();
            assertEquals(expResult, result);
            instance.addEntry(new PitEntry());
            assertEquals(expResult + 1, instance.getEntryCount());
            instance.addEntry(new PitEntry());
            assertEquals(expResult + 2, instance.getEntryCount());
            instance.addEntry(new PitEntry());
            assertEquals(expResult + 3, instance.getEntryCount());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getFileType method, of class PitData.
     */
    @Test
    public void testGetFileType() {
        try {
            System.out.println("getFileType");
            PitData instance = new PitData(pitFile);
            char[] expResult = new char[]{'C', 'O', 'M', '_', 'T', 'A', 'R', '2'};
            char[] result = instance.getFileType();
            assertArrayEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getPhone method, of class PitData.
     */
    @Test
    public void testGetPhone() {
        try {
            System.out.println("getPhone");
            PitData instance = new PitData(pitFile);
            char[] expResult = new char[]{'M', 'x', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
            char[] result = instance.getPhone();
            assertArrayEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of unpack method, of class PitData.
     */
    @Test
    public void testUnpack() {
        testPack();
    }

    /**
     * Test of findEntryByFilename method, of class PitData.
     */
    @Test
    public void testFindEntryByFilename() {
        System.out.println("findEntryByFilename");
        String filename = "tz.img";
        PitData instance ;
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            fail();
            return;
        }
        int expResult = 81;
        PitEntry result = instance.findEntryByFilename(filename);
        assertEquals(expResult, result.getPartID());

    }

    /**
     * Test of removeEntry method, of class PitData.
     */
    @Test
    public void testRemoveEntry() {
        System.out.println("removeEntry");
        PitData instance;
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("could not find entry");
            return;
        }
        PitEntry entry = instance.findEntry(81);
        instance.removeEntry(entry);
        PitEntry test = instance.findEntry(81);
        assert (test == null);

    }

    /**
     * Test of getPITFriendlyName method, of class PitData.
     */
    @Test
    public void testGetPITFriendlyName() {
        System.out.println("getPITFriendlyName");
        PitData instance;
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            fail("could not find entry");
            return;
        }
        String expResult = "Mx";
        String result = instance.getPITFriendlyName();
        assertEquals(expResult, result);

    }

    /**
     * Test of getFileTypeFriendlyName method, of class PitData.
     */
    @Test
    public void testGetFileTypeFriendlyName() {
        System.out.println("getFileTypeFriendlyName");
        PitData instance;
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            fail("could not find entry");
            return;
        }
        String expResult = "COM_TAR2";
        String result = instance.getFileTypeFriendlyName();
        assertEquals(expResult, result);

    }

    /**
     * Test of toString method, of class PitData.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        PitData instance;
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            fail("could not find entry");
            return;
        }
        String result = instance.toString();

    }

    /**
     * Test of sortEntriesByBlockLocation method, of class PitData.
     */
    @Test
    public void testSortEntriesByBlockLocation() {
        System.out.println("sortEntriesByBlockLocation");
        PitData instance;
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            fail("could not find entry");
            return;
        }

        PitEntry[] result = instance.sortEntriesByBlockLocation();
        int[] partitionIDOrder = new int[]{80, 70, 71, 81, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        for (int i = 0; i < instance.entryCount; i++) {
            assertEquals(partitionIDOrder[i], result[i].getPartID());
        }

    }

    /**
     * Test of resizePartition method, of class PitData.
     */
    @Test
    public void testResizePartition() {
        System.out.println("resizePartition");
        String partitionToResize = "CACHE";
        PitData instance = new PitData();
        try {
            instance = new PitData(pitFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        //check code
        int[] originalStart = new int[instance.entryCount];
        PitEntry[] entryList = instance.sortEntriesByBlockLocation();
        for (int i = 0; i < entryList.length; i++) {
            originalStart[i] = entryList[i].getBlockStart();
        }
        int partitionNumber = 11;
        PitEntry entry = instance.getEntry(partitionNumber);
        int originalSize = entry.getBlockCount();
        //break here--- writing demonstration code.
        assert entry.getPartitionName().equals("CACHE");

        //example code
        String partName = "CACHE"; //partition name to change
        int changeToSize = -2000; //size to change partition (1 megabyte smaller)
        try {
            instance.resizePartition(partName, changeToSize);
        } catch (ClassNotFoundException ex) {
            //this occurs if the partition specified is not found
            Logger.getLogger(PitDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        entryList = instance.sortEntriesByBlockLocation();
        for (int i = partitionNumber + 1; i < originalStart.length; i++) {
            assertEquals(originalStart[i] + changeToSize, entryList[i].getBlockStart());
        }
        assertEquals(originalSize + changeToSize, instance.findEntry(partName).getBlockCount());

    }

}
