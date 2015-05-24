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
package CASUAL.language.commands;

import CASUAL.language.CASUALLanguageException;
import CASUAL.language.Command;
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
public class VariablesTest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    public VariablesTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of parseVarialbesInString method, of class Variables.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseVariablesInString() throws Exception {
        System.out.println("parseVariablesInString");
        Command c = new Command("var=y");
        Variables.parseVariablesInCommandString(c);
        Variables.reset();
        c=new Command("var=x");
        Variables.parseVariablesInCommandString(c);
        assertEquals("x",c.get());
        // TODO review the generated test code and remove the default call to fail.
    }
    @Test
     public void variousVariableUseCases() throws Exception {
        System.out.println("various use");
        Command c = new Command("x=3");
        Variables.parseVariablesInCommandString(c);
        c = new Command("y=4");
        Variables.parseVariablesInCommandString(c);
        c = new Command("z=$MATHx+y") ;
        Variables.parseVariablesInCommandString(c);
        System.out.print(c.toString());
        assertEquals("7",c.get());
    }
    

    /**
     * Test of reset method, of class Variables.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        Variables.reset();
        Variables v=new Variables();
        try {
            Variables.parseVariablesInCommandString(new Command("x=$MATH3+4"));
            Command cmd=new Command("x");
            Variables.parseVariablesInCommandString(cmd);
            assert (cmd.get().equals("7"));
            cmd=new Command("x");
            Variables.reset();
            Variables.parseVariablesInCommandString(cmd);
            assert (cmd.get().equals("x"));
            System.out.println(cmd);
            // TODO review the generated test code and remove the default call to fail.
        } catch (CASUALLanguageException ex) {
            Logger.getLogger(VariablesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
