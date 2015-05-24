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

package CASUAL.network;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class NetworkPropertiesTest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    

    
    //this url should contain minSVN value
    final String networkPropertiesURL="https://builds.casual-dev.com/files/CASPAC/all/testpak.properties";
    final String minSVN="730";

    public NetworkPropertiesTest() {
    }

    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getProperties method, of class NetworkProperties.
     */
    @Test
    public void testGetProperties() {
        System.out.println("getProperties");
        NetworkProperties instance = null;
        try {
            instance = new NetworkProperties(networkPropertiesURL);
        } catch (IOException ex) {
            Logger.getLogger(NetworkPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        Properties result = instance.getProperties();
        // TODO review the generated test code and remove the default call to fail.
        System.out.println(result);
        assertEquals (result.get("CASUAL.minSVN"),minSVN);
    }


    
}
