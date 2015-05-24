/*
 * Copyright (C) 2013 Jeremy
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
package CASUAL.communicationstools.heimdall.drivers;

import CASUAL.CASUALSessionData;
import CASUAL.Log;
import CASUAL.OSTools;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;

/**
 *
 * @author Jeremy
 */
public class DriverInstallTest {

    public DriverInstallTest() {
        assumeTrue(!System.getProperty("user.name").equals("jenkins"));
        assumeTrue(!java.awt.GraphicsEnvironment.isHeadless());
        assumeTrue(OSTools.isWindows());
        CASUALSessionData.setGUI(new GUI.testing.automatic());
    }

    /**
     * Test of installKnownDrivers method, of class DriverInstall.
     */
    @Test
    public void testInstallKnownDrivers() {
        Log.level4Debug("Testing DriverInstall.installKnownDrivers()");
        DriverInstall instance = new DriverInstall(1);
        assert (!instance.installKnownDrivers());
    }
}
