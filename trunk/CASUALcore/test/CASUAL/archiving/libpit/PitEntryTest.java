/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.archiving.libpit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class PitEntryTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    PitData pitFile;

    public PitEntryTest() {
        try {
            pitFile = new PitData(new File("../test/CASUAL/archiving/resources/ekgc100part.pit"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitEntryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        pitFile.addEntry(pitFile.getEntry(0));
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of matches method, of class PitEntry.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        resetPit();
        PitEntry otherPitEntry = pitFile.getEntry(7);
        pitFile.addEntry(otherPitEntry);
        PitEntry instance = pitFile.getEntry(27);
        boolean expResult = true;
        boolean result = instance.matches(otherPitEntry);
        assertEquals(expResult, result);
        resetPit();
    }

    private void resetPit() {
        try {
            pitFile = new PitData(new File("../test/CASUAL/network/CASUALDevIntegration/resources/sch-i535-32gb.pit"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PitEntryTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Test of getBinType method, of class PitEntry.
     */
    @Test
    public void testGetBinType() {
        System.out.println("getBinType");
        PitEntry instance = new PitEntry();
        int expResult = 91;
        instance.setBinType(expResult);
        int result = instance.getBinType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDevType method, of class PitEntry.
     */
    @Test
    public void testGetDevType() {
        System.out.println("getDevType");
        PitEntry instance = new PitEntry();
        int expResult = 89;
        instance.setDeviceType(expResult);
        int result = instance.getDevType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPartID method, of class PitEntry.
     */
    @Test
    public void testGetPartID() {
        System.out.println("getPartID");
        PitEntry instance = new PitEntry();
        int expResult = 92;
        instance.setPartID(expResult);
        int result = instance.getPartID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPartitionType method, of class PitEntry.
     */
    @Test
    public void testGetAttributes() {
        System.out.println("getAttributes");
        PitEntry instance = new PitEntry();
        int expResult = 93;
        instance.setPartitionType(expResult);
        int result = instance.getPartitionType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFilesystem method, of class PitEntry.
     */
    @Test
    public void testGetFilesystem() {
        System.out.println("getFilesystem");
        PitEntry instance = new PitEntry();
        int expResult = 88;
        instance.setFilesystem(expResult);
        int result = instance.getFilesystem();
        assertEquals(expResult, result);
    }

    /**
     * Test of getBlockStart method, of class PitEntry.
     */
    @Test
    public void testGetBlockStart() {
        System.out.println("getBlockStart");
        PitEntry instance = new PitEntry();
        int expResult = 87;
        instance.setBlockStart(expResult);
        int result = instance.getBlockStart();
        assertEquals(expResult, result);
    }

    /**
     * Test of setBlockStart method, of class PitEntry.
     */
    @Test
    public void testSetBlockStart() {
        testGetBlockStart();
    }

    /**
     * Test of getBlockCount method, of class PitEntry.
     */
    @Test
    public void testGetBlockCount() {
        System.out.println("getBlockCount");
        PitEntry instance = new PitEntry();
        int expResult = 86;
        instance.setBlockCount(expResult);
        int result = instance.getBlockCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of setBlockCount method, of class PitEntry.
     */
    @Test
    public void testSetBlockCount() {
        System.out.println("setBlockCount");
        testGetBlockCount();
    }

    /**
     * Test of getFileOffset method, of class PitEntry.
     */
    @Test
    public void testGetFileOffset() {
        System.out.println("getFileOffset");
        PitEntry instance = new PitEntry();
        int expResult = 0;
        instance.setFileOffset(expResult);
        int result = instance.getFileOffset();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFileOffset method, of class PitEntry.
     */
    @Test
    public void testSetFileOffset() {
        System.out.println("setFileOffset");
        testGetFileOffset();
    }

    /**
     * Test of getFileSize method, of class PitEntry.
     */
    @Test
    public void testGetFileSize_0args() {
        System.out.println("getFileSize");
        PitEntry instance = new PitEntry();
        int expResult = 0;
        instance.setFileSize(expResult);
        int result = instance.getFileSize();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPartitionNameBytes method, of class PitEntry.
     */
    @Test
    public void testGetPartitionNameBytes() {
        System.out.println("getPartitionNameBytes");
        PitEntry instance = new PitEntry();
        byte[] expResult = new byte[]{'w', 'o', 'o', 't'};
        instance.setPartitionName(expResult);
        byte[] result = instance.getPartitionNameBytes();
        assertArrayEquals(expResult, result);

    }

    /**
     * Test of getPartitionName method, of class PitEntry.
     */
    @Test
    public void testGetPartitionName() {
        System.out.println("getPartitionName");
        PitEntry instance = new PitEntry();
        String expResult = "name";
        instance.setPartitionName(expResult);
        String result = instance.getPartitionName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPartitionName method, of class PitEntry.
     */
    @Test
    public void testSetPartitionName_byteArr() {
        System.out.println("setPartitionName");
        testGetPartitionNameBytes();

    }

    /**
     * Test of setPartitionName method, of class PitEntry.
     */
    @Test
    public void testSetPartitionName_String() {
        System.out.println("setPartitionName");
        testGetPartitionName();
    }

    /**
     * Test of getFileNameBytes method, of class PitEntry.
     */
    @Test
    public void testGetFileNameBytes() {
        System.out.println("getFileNameBytes");
        PitEntry instance = new PitEntry();
        byte[] expResult = Arrays.copyOf(new byte[]{'w', 'o', 'o', 't', 't', 't'}, 32);
        instance.setFilename(expResult);
        byte[] result = Arrays.copyOf(instance.getFileNameBytes(), 32);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getFilenameString method, of class PitEntry.
     */
    @Test
    public void testGetFilename() {
        System.out.println("getFilename");
        PitEntry instance = new PitEntry();
        String expResult = "testatest";
        instance.setFilename(expResult);
        String result = instance.getFilenameString();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFilename method, of class PitEntry.
     */
    @Test
    public void testSetFilename_byteArr() {
        System.out.println("setFilename");
        this.testGetFileNameBytes();
    }

    /**
     * Test of setFilename method, of class PitEntry.
     */
    @Test
    public void testSetFilename_String() {
        System.out.println("setFilename");
        this.testGetFilename();
    }

    /**
     * Test of getFotaNameBytes method, of class PitEntry.
     */
    @Test
    public void testGetFotaNameBytes() {
        System.out.println("getFotaNameBytes");
        PitEntry instance = new PitEntry();
        byte[] expResult = new byte[]{'w', 'o', 'o', 't', 't', 't', 'e', 's', 't'};
        instance.setFotaName(expResult);
        byte[] result = instance.getFotaNameBytes();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getFotaName method, of class PitEntry.
     */
    @Test
    public void testGetFotaName() {
        System.out.println("getFotaName");
        PitEntry instance = new PitEntry();
        String expResult = "testatestatest";
        instance.setFotaName(expResult);
        String result = instance.getFotaName();
        assertEquals(expResult, result);

    }

    /**
     * Test of setFotaName method, of class PitEntry.
     */
    @Test
    public void testSetFotaName_byteArr() {
        System.out.println("setFotaName");
        this.testGetFotaNameBytes();
    }

    /**
     * Test of setFotaName method, of class PitEntry.
     */
    @Test
    public void testSetFotaName_String() {
        System.out.println("setFotaName");
        this.testGetFotaName();
    }

    /**
     * Test of toString method, of class PitEntry.
     */
    @Test
    public void testToString() {
        //nothing to do here
    }

    /**
     * Test of convertByteArrayToCharArray method, of class PitEntry.
     */
    @Test
    public void testConvertByteArrayToCharArray() {
        //this is tested several times
    }

    /**
     * Test of convertCharArrayToByteArray method, of class PitEntry.
     */
    @Test
    public void testConvertCharArrayToByteArray() {
        //this is tested several times
    }


}
