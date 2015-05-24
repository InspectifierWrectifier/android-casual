/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adamoutler
 */
public class TranslationsTest {
    
    //mixed valid and invalid translation strings early, middle, late values.
    final static String line = "@permissionsElevationRequired @interactionOfflineNotification @ppermissionsElevationRequiredermissionsElevationRequired @permissionsElevatisfdasf  test test test   @heimdallWasSucessful test test  ";
    //early in translation file
    final static String line2 = "@interactionOfflineNotification";
    //late in translation file
    final static String line3 = "@NotForYourDevice";
    //middle of translation file
    final static String line4 = "@md5sVerified";

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void translation1() {
        Long time=System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            PrintStream out= Log.out;  
            //run this 8000 translations quietly
            Log.out=new PrintStream(new ByteArrayOutputStream());
            Translations.get(line);
            Translations.get(line2);
            Translations.get(line3);
            Translations.get(line4);
            Log.out=out;
        }
        System.out.println(Translations.get(line));
        System.out.println(Translations.get(line2));
        System.out.println(Translations.get(line3));
        assert(Translations.get(line4).contains("MD5s verified!"));
        double diff=(System.currentTimeMillis()-time)/1000.00000;
        double tps=8000/diff;
        System.out.println("performed 8000 translations in "+ diff+ "seconds");
        System.out.println("at a rate of "+tps+" translations per second");
    }
}
