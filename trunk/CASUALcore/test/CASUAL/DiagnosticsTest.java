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

package CASUAL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class DiagnosticsTest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    public DiagnosticsTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDiagnosticReportOneLine method, of class Diagnostics.
     */
    @Test
    public void testGetDiagnosticReportOneLine() {
        System.out.println("getDiagnosticReportOneLine");
        String expResult = "CASUAL is handling this system as";
        String result = Diagnostics.getDiagnosticReportOneLine(CASUALSessionData.newInstance());
        System.out.println(result);
        assert(result.contains(expResult));
        
    }

    /**
     * Test of diagnosticReport method, of class Diagnostics.
     */
    @Test
    public void testDiagnosticReport() {
        System.out.println("diagnosticReport");
        String expResult = "\nCASUAL is handling this system as";
        String result = Diagnostics.diagnosticReport(CASUALSessionData.newInstance());
        System.out.println(result);
        assert(result.contains(expResult));
        
    }
    
}
