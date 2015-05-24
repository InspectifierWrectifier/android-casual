/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASPACkager;

import CASPACkager.PackagerMain;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class PackagerMainTest {
    
    public PackagerMainTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = new String[]{"--CASPAC", "../CASPAC/fastbootRecoveryTest.zip", "--type", "CASPACkager Test Build", "--output", "../CASPAC/CASUAL", "--replaceReference", "--Recovery--", "TWRP Recovery", "--replaceReference", "--DeviceFriendlyName--", "Nexus 7 2012", "--replaceReference", "--ProductName--", "ro.product.name=nakasi", "--replaceFile", "recovery.img", "test/CASPACkager/openrecovery-twrp-2.6.3.0-grouper.img"};
        PackagerMain.main(args);
        args = new String[]{"--CASPAC", "../CASPAC/testpak.zip", "--type", "CASPACkager Test Build", "--output", "../CASPAC/CASUAL", "--replaceReference", "--Recovery--", "TWRP Recovery", "--replaceReference", "--DeviceFriendlyName--", "Nexus 7 2012", "--replaceReference", "--ProductName--", "ro.product.name=nakasi", "--replaceFile", "recovery.img", "test/CASPACkager/openrecovery-twrp-2.6.3.0-grouper.img"};
        PackagerMain.main(args);
        
    }


}