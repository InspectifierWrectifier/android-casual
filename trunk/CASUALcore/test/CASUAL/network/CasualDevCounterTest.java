/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.network;

import CASUAL.network.CASUALDevIntegration.CasualDevCounter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class CasualDevCounterTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }


    /**
     * Test of ping method, of class CasualDevCounter.
     */
    int x=0;

    String testvalue="";
    public CasualDevCounterTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPing() {
        try {
            x++;
            System.out.println("ping "+x);
            
            String testCounter="TimesISignERDBadge";
            Thread.sleep(300);
            getCounter(testCounter);
            int baseValue=Integer.parseInt(testvalue);
            testvalue="";
            CasualDevCounter instance = new CasualDevCounter();
            instance.incrementCounter(testCounter);
            Thread.sleep(300);
            instance.waitFor();
            getCounter(testCounter);
            Thread.sleep(300);
            int newvalue=Integer.parseInt(testvalue);
            assert newvalue>=baseValue+2;
            testvalue="";

        } catch (InterruptedException ex) {
            Logger.getLogger(CasualDevCounterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getCounter(String testvar) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        try {
            url = new URL("http://counter.casual-dev.com/?"+testvar);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                testvalue += line;
            }
        } catch (MalformedURLException mue) {
        } catch (IOException ioe) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
            }
        }
    }

}
