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

package GUI.CommandLine;

import CASUAL.CASUALMain;
import CASUAL.communicationstools.adb.ADBTools;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class CommandLineUITest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    public CommandLineUITest() {
        assumeTrue(!java.awt.GraphicsEnvironment.isHeadless());
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testMain() {
        assumeTrue(new ADBTools().isConnected());
        System.out.println("main");
        String[] args = new String[]{"--caspac","../../CASPAC/testpak.zip"};
        CASUAL.CASUALSessionData.setGUI(new GUI.testing.automatic());
        CASUALMain.beginCASUAL(args,CASUALMain.getSession());

    }
    
}
