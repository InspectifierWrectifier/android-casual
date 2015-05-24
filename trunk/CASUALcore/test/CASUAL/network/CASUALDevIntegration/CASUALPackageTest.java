/*
 * Copyright (C) 2014 adamoutler
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

package CASUAL.network.CASUALDevIntegration;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class CASUALPackageTest {
    CASUALPackage cp;
    public CASUALPackageTest() {
        cp=new CASUALPackage("https://builds.casual-dev.com/files/CASPAC/all/testpak.zip");
        System.out.println("Set CASPAC up");
    }
    
    @Before
    public void setUp() {
        
    }

    /**
     * Test of isValid method, of class CASUALPackage.
     */
    @Test
    public void testIsValid() {
        System.out.println("isvalid");
        assert cp.isValid();
    }

    /**
     * Test of getDescritpion method, of class CASUALPackage.
     */
    @Test
    public void testGetDescritpion() {
        System.out.println("getDescription");
        assert !cp.getDescription().isEmpty();
    }

    /**
     * Test of getDeveloper method, of class CASUALPackage.
     */
    @Test
    public void testGetDeveloper() {
        System.out.println("getDeveloper");
        assert !cp.getDeveloper().isEmpty();
    }

    /**
     * Test of getDonateTo method, of class CASUALPackage.
     */
    @Test
    public void testGetDonateTo() {
        System.out.println("getDonateTo");
        assert !cp.getDonateTo().isEmpty();
    }

    /**
     * Test of getDonateLink method, of class CASUALPackage.
     */
    @Test
    public void testGetDonateLink() {
        System.out.println("getDonateLink");
        assert !cp.getDonateLink().contains(".");
    }

    /**
     * Test of getWindowTitle method, of class CASUALPackage.
     */
    @Test
    public void testGetWindowTitle() {
        System.out.println("getWindowTitle");
        assert !cp.getWindowTitle().isEmpty();
    }

    /**
     * Test of getDescription method, of class CASUALPackage.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        assert !cp.getDescription().isEmpty();
    }

    /**
     * Test of getRevision method, of class CASUALPackage.
     */
    @Test
    public void testGetRevision() {
        System.out.println("getRevision");
        assert !cp.getRevision().isEmpty();
    }

    /**
     * Test of getSupportURL method, of class CASUALPackage.
     */
    @Test
    public void testGetSupportURL() {
        System.out.println("getSupportURL");
        assert !cp.getSupportURL().isEmpty();
    }

    /**
     * Test of getUniqueID method, of class CASUALPackage.
     */
    @Test
    public void testGetUniqueID() {
        System.out.println("getUniqueID");
        assert !cp.getUniqueID().isEmpty();
    }
    
}
