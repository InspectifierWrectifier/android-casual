/*
 * Copyright (C) 2013 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/ .
 */
package CASUAL;

import CASUAL.CASUALSettings.CASUALMode;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class CASUALSettingsTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    public CASUALSettingsTest() {
        assumeTrue(!java.awt.GraphicsEnvironment.isHeadless());
        
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getCASPACType method, of class CASUALSettings.
     */
    @Test
    public void testGetCASPACType() {
        System.out.println("getCASPACType");
        CASUALSettings instance = new CASUALSettings();
         instance.checkArguments(new String[]{"--Caspac", "../../CASPAC/testpak.zip"});
        CASUALSettings.CASUALMode expResult = CASUALMode.CASPAC;
        CASUALSettings.CASUALMode result = instance.getCASPACType();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{});
        result = instance.getCASPACType();
        expResult = CASUALMode.CASUAL;
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--caspac", "asdfalskfas"});
        result = instance.getCASPACType();
        expResult = CASUALMode.EXIT;
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"asdfalskfas"});
        result = instance.getCASPACType();
        expResult = CASUALMode.EXIT;
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--execute", "testomfg"});
        result = instance.getCASPACType();
        expResult = CASUALMode.EXECUTE;
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--execute"});
        result = instance.getCASPACType();
        expResult = CASUALMode.EXIT;
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--caspac"});
        result = instance.getCASPACType();
        expResult = CASUALMode.EXIT;
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--asfdaf"});
        result = instance.getCASPACType();
        expResult = CASUALMode.EXIT;
        assertEquals(expResult, result);
    }

    /**
     * Test of setCASPACType method, of class CASUALSettings.
     */
    @Test
    public void testSetCASPACType() {
        System.out.println("setCASPACType");
        CASUALSettings instance = new CASUALSettings();
        CASUALSettings.CASUALMode expResult = CASUALMode.CASPAC;
        instance.setCASPACType(expResult);
        CASUALMode result = instance.getCASPACType();
        assertEquals(result, expResult);
        expResult = CASUALMode.CASUAL;
        instance.setCASPACType(expResult);
        result = instance.getCASPACType();
        assertEquals(result, expResult);
        expResult = CASUALMode.EXECUTE;
        instance.setCASPACType(expResult);
        result = instance.getCASPACType();
        assertEquals(result, expResult);
        expResult = CASUALMode.EXIT;
        instance.setCASPACType(expResult);
        result = instance.getCASPACType();
        assertEquals(result, expResult);
    }

    /**
     * Test of getCaspacLocation method, of class CASUALSettings.
     */
    @Test
    public void testGetCaspacLocation() {
        System.out.println("getCaspacLocation");
        CASUALSettings instance = new CASUALSettings();
        File expResult = new File("../../CASPAC/testpak.zip");
        File result = instance.getCaspacLocation();
        assertEquals(null, result);
        instance.checkArguments(new String[]{"--caspac", "../../CASPAC/testpak.zip"});
        result = instance.getCaspacLocation();
        assertEquals(expResult, result);

    }

    /**
     * Test of setCaspacLocation method, of class CASUALSettings.
     */
    @Test
    public void testSetCaspacLocation() {
        System.out.println("setCaspacLocation");
        testGetCaspacLocation();
    }

    /**
     * Test of isUseGUI method, of class CASUALSettings.
     */
    @Test
    public void testIsUseGUI() {
        System.out.println("isUseGUI");
        CASUALSettings instance = new CASUALSettings();
        instance.checkArguments(new String[]{"--Caspac", "../../CASPAC/testpak.zip"});
        boolean expResult = false;
        boolean result = instance.isUseGUI();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--Caspac", "../../CASPAC/testpak.zip", "--GUI"});
        expResult = true;
        result = instance.isUseGUI();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--GUI", "--Caspac", "../../CASPAC/testpak.zip", "--GUI"});
        expResult = true;
        result = instance.isUseGUI();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--GUI", "--Caspac", "../../CASPAC/testpak.zip"});
        expResult = true;
        result = instance.isUseGUI();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{});
        expResult = true;
        result = instance.isUseGUI();
        assertEquals(expResult, result);

    }

    /**
     * Test of setUseGUI method, of class CASUALSettings.
     */
    @Test
    public void testSetUseGUI() {
        System.out.println("setUseGUI");
        boolean expResult = true;
        CASUALSettings instance = new CASUALSettings();
        instance.setUseGUI(expResult);
        boolean result = instance.isUseGUI();
        assertEquals(expResult, result);
        expResult = false;
        instance.setUseGUI(expResult);
        result = instance.isUseGUI();
        assertEquals(expResult, result);
        expResult = true;
        instance.setUseGUI(expResult);
        result = instance.isUseGUI();
        assertEquals(expResult, result);
        testIsUseGUI();
    }

    /**
     * Test of isExecute method, of class CASUALSettings.
     */
    @Test
    public void testIsExecute() {
        System.out.println("isExecute");
        CASUALSettings instance = new CASUALSettings();
        boolean expResult = false;
        boolean result = instance.isExecute();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--execute", "testomfg"});
        expResult = true;
        result = instance.isExecute();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--EXECUTE", "testomfg"});
        expResult = true;
        result = instance.isExecute();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"--e", "testomfg"});
        expResult = true;
        result = instance.isExecute();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{"testomfg"});
        expResult = false;
        result = instance.isExecute();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{});
        expResult = false;
        result = instance.isExecute();
        assertEquals(expResult, result);

    }

    /**
     * Test of setExecute method, of class CASUALSettings.
     */
    @Test
    public void testSetExecute() {
        System.out.println("setExecute");
        boolean execute = false;
        CASUALSettings instance = new CASUALSettings();
        instance.setExecute(execute);
        boolean result = instance.isExecute();
        assertEquals(execute, result);

    }

    /**
     * Test of getExecuteCommand method, of class CASUALSettings.
     */
    @Test
    public void testGetExecuteCommand() {
        System.out.println("getExecuteCommand");
        CASUALSettings instance = new CASUALSettings();
        String expResult = "testomfg";
        instance.checkArguments(new String[]{"--execute", expResult});
        String result = instance.getExecuteCommand();
        assertEquals(expResult, result);
        expResult = "";
        instance.checkArguments(new String[]{"--eXeCuTe", expResult});
        result = instance.getExecuteCommand();
        assertEquals(expResult, result);
        instance.checkArguments(new String[]{});
        result = instance.getExecuteCommand();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of setExecuteCommand method, of class CASUALSettings.
     */
    @Test
    public void testSetExecuteCommand() {
        System.out.println("setExecuteCommand");
        String executeCommand = "aalsjdflasdf";
        CASUALSettings instance = new CASUALSettings();
        instance.setExecuteCommand(executeCommand);
        String result=instance.getExecuteCommand();
        assertEquals(executeCommand,result);
    }

    /**
     * Test of getPassword method, of class CASUALSettings.
     */
    @Test
    public void testGetPassword() {
        System.out.println("getPassword");
        CASUALSettings instance = new CASUALSettings();
        String expResult = "reallyLongPassword";
        instance.checkArguments(new String[]{"-p", expResult});
        String result = instance.getPassword();
        assertEquals(expResult, result);
        expResult = "adfa'asfa'a023";
        instance.checkArguments(new String[]{"--PasswORd", expResult});
        result = instance.getPassword();
        assertEquals(expResult, result);
        expResult = "";
        instance.checkArguments(new String[]{});
        result = instance.getPassword();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPassword method, of class CASUALSettings.
     */
    @Test
    public void testSetPassword() {
        System.out.println("setPassword");
        String expResult = "password";
        CASUALSettings instance = new CASUALSettings();
        instance.setPassword(expResult);
        String result=instance.getPassword();
        assertEquals(expResult,result);
    }

    /**
     * Test of checkArguments method, of class CASUALSettings.
     */
    @Test
    public void testCheckArguments() {
        System.out.println("checkArguments");
        String[] args = null;
        CASUALSettings instance = new CASUALSettings();
        instance.checkArguments(args);
        args = new String[]{};
        instance.checkArguments(args);
        args = new String[]{"",""};
        instance.checkArguments(args);
        
        
    }

}
