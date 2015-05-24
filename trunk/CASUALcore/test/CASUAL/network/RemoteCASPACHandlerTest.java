/*
 * Copyright (C) 2015 adamoutler
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
package CASUAL.network;

import CASUAL.CASUALSessionData;
import CASUAL.communicationstools.adb.ADBTools;
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
public class RemoteCASPACHandlerTest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    public RemoteCASPACHandlerTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of downloadFromRemoteRepository method, of class RemoteCASPACHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testDownloadFromRemoteRepository() throws Exception {
        System.out.println("downloadFromRemoteRepository");
        String tempFolder = CASUALSessionData.newInstance().getTempFolder();
        String urlPath = "all/testpak.zip";
        RemoteCASPACHandler instance = new RemoteCASPACHandler();
        String expResult = "testpak.zip";
        File result = instance.downloadFromRemoteRepository(tempFolder, urlPath);
        assertEquals(expResult,result.getName());
    }

    /**
     * Test of executeCaspac method, of class RemoteCASPACHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecuteCaspac() throws Exception {
        System.out.println("executeCaspac");
        assumeTrue(new ADBTools().isConnected());
        CASUALSessionData sd = CASUALSessionData.newInstance();
        CASUALSessionData.setGUI(new GUI.testing.automatic());
        String urlPath =  "all/testpak.zip";
        RemoteCASPACHandler instance = new RemoteCASPACHandler();
        String expResult = "";
        String result = instance.executeCaspac(sd, urlPath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    
}
