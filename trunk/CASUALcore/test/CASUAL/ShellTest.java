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

import CASUAL.communicationstools.adb.ADBTools;
import CASUAL.communicationstools.fastboot.FastbootTools;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
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
public class ShellTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    final String exp = "List of devices attached";

    public ShellTest() {
        CASUALSessionData.setGUI(new GUI.testing.automatic());
    }

    @Before
    public void setUp() {
        new ADBTools().startServer();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of elevateSimpleCommandWithMessage method, of class Shell.
     */
    @Test
    public void testElevateSimpleCommandWithMessage() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        System.out.println("elevateSimpleCommandWithMessage");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String message = "adb wait-for-device";
        String result = instance.elevateSimpleCommandWithMessage(cmd, message);
        assert (result.contains(exp));

    }

    /**
     * Test of elevateSimpleCommand method, of class Shell.
     */
@Test
public void testElevateSimpleCommand() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        return;
    }
    System.out.println("elevateSimpleCommand");
    String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
    Shell instance = new Shell();
    String result = instance.elevateSimpleCommand(cmd);
    assert result.contains(exp);

    }

    /**
     * Test of sendShellCommand method, of class Shell.
     */
    @Test
    public void testSendShellCommand() {
        System.out.println("sendShellCommand");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.sendShellCommand(cmd);
        assert (result.contains(exp));
    }

    /**
     * Test of sendShellCommandIgnoreError method, of class Shell.
     */
    @Test
    public void testSendShellCommandIgnoreError() {
        System.out.println("sendShellCommandIgnoreError");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.sendShellCommand(cmd);
        assert (result.contains(exp));
        cmd = new String[]{new ADBTools().getBinaryLocation(), "version"};
        String expResult = "\nAndroid Debug Bridge version";
        result = instance.sendShellCommand(cmd);
        assert (result.startsWith(expResult));

    }

    /**
     * Test of silentShellCommand method, of class Shell.
     */
    @Test
    public void testSilentShellCommand() {
        System.out.println("silentShellCommand");
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.sendShellCommand(cmd);
        assert (result.contains(exp));
    }

    /**
     * Test of liveShellCommand method, of class Shell.
     */
    @Test
    public void testLiveShellCommand() {
        System.out.println("liveShellCommand");
        String[] params = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        boolean display = false;
        Shell instance = new Shell();
        String result = instance.liveShellCommand(params, display);
        assert result.contains(exp);

    }

    /**
     * Test of timeoutShellCommand method, of class Shell.
     */
    @Test
    public void testTimeoutShellCommand() {
        System.out.println("timeoutShellCommand");
        int timeout = 4000;
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.timeoutShellCommand(cmd, timeout);
        assert result.contains(exp);
        timeout = 0;
        result = instance.timeoutShellCommand(cmd, timeout);
        String expResult = "Timeout!!! ";
        assert(result.contains(expResult));

    }

    /**
     * Test of silentTimeoutShellCommand method, of class Shell.
     */
    @Test
    public void testSilentTimeoutShellCommand() {
        System.out.println("silentTimeoutShellCommand");
        int timeout = 6000;
        String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "devices"};
        Shell instance = new Shell();
        String result = instance.silentTimeoutShellCommand(cmd, timeout);
        assert result.contains(exp);
        timeout = 0;
        result = instance.silentTimeoutShellCommand(cmd, timeout);
        String expResult = "Timeout!!! ";
        assertEquals(expResult, result);

    }

    /**
     * Test of timeoutShellCommandWithWatchdog method, of class Shell.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testTimeoutValueCheckingShellCommand() throws IOException {
        System.out.println("timeoutValueCheckingShellCommand");
        String[] cmd = new String[]{new FastbootTools().getBinaryLocation()};
        String[] startTimerOnThisInLine = new String[]{"devices", "attached"};
        String expectedResult = "usage: fastboot";
        int timeout = 4000;
        Shell instance = new Shell();
        String result = instance.timeoutShellCommandWithWatchdog(cmd, startTimerOnThisInLine, timeout,false);
        
        assert result.contains(expectedResult);

        //verify the command "fastboot flash" times out after 3 seconds
        cmd = new String[]{new FastbootTools().getBinaryLocation(), "flash", "2>&1"};
        //instantiate a final static variable for use in the timer
        class check {

            boolean timerElapsed = false;
        }
        final check c = new check();
        //instantiate a timer
        Timer t = new Timer();
        startTimerOnThisInLine = new String[]{"waiting"};
        expectedResult = "Timeout!!! < waiting for device >\n";
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("test time elapsed");
                c.timerElapsed = true;
            }
        }, timeout-1000);
        System.out.println(result);
        result = instance.timeoutShellCommandWithWatchdog(cmd, startTimerOnThisInLine, timeout,true);
        assert (c.timerElapsed);
        System.out.println("RESULT:"+result);

        //reset test timer for checking non-timeout
        c.timerElapsed = false;
        cmd = new String[]{new FastbootTools().getBinaryLocation(), "devices"};
        expectedResult = "";
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                c.timerElapsed = true;
            }
        }, timeout);

        result = instance.timeoutShellCommandWithWatchdog(cmd, startTimerOnThisInLine, timeout,true);
        assert (!c.timerElapsed);
        assertEquals(expectedResult, result);

    }


}
