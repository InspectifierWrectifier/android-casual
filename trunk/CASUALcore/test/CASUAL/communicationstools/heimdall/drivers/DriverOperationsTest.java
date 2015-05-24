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
public class DriverOperationsTest {
    
    public DriverOperationsTest() {
        assumeTrue(!System.getProperty("user.name").equals("jenkins"));
        assumeTrue(!java.awt.GraphicsEnvironment.isHeadless());
        assumeTrue(OSTools.isWindows());
        CASUALSessionData.setGUI(new GUI.testing.automatic());
    }

    /**
     * Test of getDeviceList method, of class DriverOperations.
     */
    @Test
    public void testGetDeviceList_String() {
        Log.level4Debug("Testing DriverOperations.getDeviceList(String)");
        DriverOperations instance = new DriverOperations();
        assert(instance.getDeviceList("XXXX") == null);
    }

    /**
     * Test of getDeviceList method, of class DriverOperations.
     */
    @Test
    public void testGetDeviceList_boolean_boolean() {
        Log.level4Debug("Testing DriverOperations.getDeviceList(boolean, boolean)");
        DriverOperations instance = new DriverOperations();
        assert(instance.getDeviceList(true, true)!= null);
    }

    /**
     * Test of regexPattern method, of class DriverOperations.
     */
    @Test
    public void testRegexPattern() {
        Log.level4Debug("Testing DriverOperations.regexPattern()");
        DriverOperations.PatternChoice whatPattern = null;
        DriverOperations instance = new DriverOperations();
        assert(instance.regexPattern(DriverOperations.PatternChoice.ALLDEVICES) != null);
    }

    /**
     * Test of getCASUALDriverCount method, of class DriverOperations.
     */
    @Test
    public void testGetCASUALDriverCount() {
        Log.level4Debug("Testing DriverOperations.getCASUALDriverCount()");
        DriverOperations instance = new DriverOperations();
        assert(instance.getCASUALDriverCount() == 0);
    }

    /**
     * Test of update method, of class DriverOperations.
     */
    @Test
    public void testUpdate() {
        Log.level4Debug("Testing DriverOperations.update()");
        DriverOperations instance = new DriverOperations();
        assert(instance.update("USB\\VID_XXXX&PID_XXXX").contains(""));
    }

    /**
     * Test of remove method, of class DriverOperations.
     */
    @Test
    public void testRemove() {
        Log.level4Debug("Testing DriverOperations.remove()");
        DriverOperations instance = new DriverOperations();
        assert(instance.remove("USB\\VID_XXXX&PID_XXXX").contains(""));
    }

    /**
     * Test of delete method, of class DriverOperations.
     */
    @Test
    public void testDelete() {
        Log.level4Debug("Testing DriverOperations.delete()");
        DriverOperations instance = new DriverOperations();
        assert(instance.delete("9388f0455c210dc4c6c32f51425b62ca.inf").contains(""));
    }

    /**
     * Test of find method, of class DriverOperations.
     */
    @Test
    public void testFind() {
        Log.level4Debug("Testing DriverOperations.find()");
        DriverOperations instance = new DriverOperations();
        assert(instance.find("=USB*").contains(""));
    }

    /**
     * Test of findall method, of class DriverOperations.
     */
    @Test
    public void testFindall() {
        Log.level4Debug("Testing DriverOperations.findall()");
        DriverOperations instance = new DriverOperations();
        assert(instance.findall("=USB*").contains(""));
    }

    /**
     * Test of enumerate method, of class DriverOperations.
     */
    @Test
    public void testEnumerate() {
        Log.level4Debug("Testing DriverOperations.enumerate()");
        DriverOperations instance = new DriverOperations();
        assert(instance.enumerate().contains(""));
    }

    /**
     * Test of rescan method, of class DriverOperations.
     */
    @Test
    public void testRescan() {
        Log.level4Debug("Testing DriverOperations.rescan()");
        DriverOperations instance = new DriverOperations();
        assert(instance.rescan());
    }
    
}
