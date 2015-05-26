/*CASUALTools is a miscellanious helper class
 *Copyright (C) 2015  Adam Outler
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see https://www.gnu.org/licenses/ .
 */
package CASUAL;

import CASUAL.caspac.Caspac;
import CASUAL.communicationstools.adb.ADBTools;
import CASUAL.crypto.MD5sum;
import CASUAL.misc.LinkedProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a set of tools used in CASUAL 
 * @author Adam Outler adamoutler@gmail.com
 */
public class CASUALTools {
    //final public String defaultPackage="ATT GS3 Root";

    /**
     * true if this is running on the flat filesystem. False if in a jar.
     */
    final public static boolean IDEMode = new CASUALTools().getIDEMode();

    
    //CASUALZipPrep
    /**
     * thread used for preparing zip file. this should never be interrupted.
     */
    public static Thread zipPrep;


    /**
     * provides a runnable object for updating MD5s
     */
    public static Runnable updateMD5s = new Runnable() {
        @Override
        public void run() {
            new CASUALTools().md5sumTestScripts();
        }
    };

    //This is only used in IDE mode for development
    /**
     * rewrites MD5s in the provided CASPAC. note: This is only used in IDE mode
     * for development
     *
     * @param sd SessionData for this run
     * @param CASPAC file to be checked and have MD5s rewritten.
     */
    public static void rewriteMD5OnCASPAC(CASUALSessionData sd,File CASPAC) {

        Caspac caspac;
        try {
            caspac = new Caspac(sd,CASPAC, sd.getTempFolder(), 0);
            caspac.load();
            caspac.write();
            System.exit(0);

        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
    }

    /**
     * sleeps for 1000ms.
     */    public static void sleepForOneSecond() {
         try {
             Thread.sleep(1000);
         } catch (InterruptedException ex) {
             Log.errorHandler(ex);
         }
     }

    /**
     * sleeps for 100ms.
     */
     public static void sleepForOneTenthOfASecond() {
         try {
             Thread.sleep(100);
         } catch (InterruptedException ex) {
             Log.errorHandler(ex);
         }
    }



     private static void setiCASUALinteraction(Class<?> cls) throws InstantiationException, IllegalAccessException {  
         iCASUALUI clsInstance;
         if (!java.awt.GraphicsEnvironment.isHeadless()) {
             clsInstance = (CASUAL.iCASUALUI) cls.newInstance();
             CASUALSessionData.setGUI(clsInstance);
         }

    }

  

    /**
     * sets the GUI API based on property in CASUAL/resources/CASUALApp.
     * The GUI API can be specified by modification of Application.GUI. The API
    only requires that you specify a class which implements the
    iCASUALUI class.
     * @throws java.lang.ClassNotFoundException when UI cannot be found
     * @throws java.lang.InstantiationException when UI cannot be instantiated
     * @throws java.lang.IllegalAccessException when SecurityManager gets in the way
     */
     public static void setGUIAPI() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
         String messageAPI = java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.GUI");
         try {
             Class<?> cls = Class.forName(messageAPI);
             setiCASUALGUI(cls);
         } catch (ClassNotFoundException ex) {
             Class<?> cls = Class.forName("GUI.development.CASUALGUIMain");
             setiCASUALGUI(cls);
         } catch (InstantiationException ex) {
             Class<?> cls = Class.forName("GUI.development.CASUALGUIMain");
             setiCASUALGUI(cls);
         } catch (IllegalAccessException ex) {
             Class<?> cls = Class.forName("GUI.development.CASUALGUIMain");
             setiCASUALGUI(cls);
        }
    }

     public static void setiCASUALGUI(Class<?> cls) throws InstantiationException, IllegalAccessException {
         iCASUALUI clsInstance;
         clsInstance = (CASUAL.iCASUALUI) cls.newInstance();
         CASUALSessionData.setGUI(clsInstance);
    }

    /**
     * compares User ID from id -u on the device to the specified User ID.
     * @param expectedUIDs  User ID specified.
     * @return True if actua UID matches expected
     */
     public static boolean uidMatches(String[] expectedUIDs) {
         String[] cmd = new String[]{new ADBTools().getBinaryLocation(), "shell", "id -u"};
         String retval = new Shell().silentShellCommand(cmd);
         for (String expUID:expectedUIDs){
             if (retval.equals(expUID)){
                 return true;
             }
        }
        return false;
    }

    /**
     * Checks the device to get the command required for root access.  This
     * accounts for both adb root and rooted devices.
     * @return command used to get root, will be blank if unrooted. 
     */
     public static String rootAccessCommand() {
         String[] normalUser=new String[]{"\n2000"};
         String[] rootUser=new String[]{"\nuid=0","\n0"};
         
         if (uidMatches(rootUser)) {
             return "";
         } else  {
             String retval = new Shell().silentShellCommand(new String[]{new ADBTools().getBinaryLocation(), "shell", "su -c 'id -u'"});
             if (retval.contains("uid=0(")) {
                 return "su -c ";
             } else {
                 new CASUALMessageObject("@interactionCouldNotObtainRootOnDevice").showErrorDialog();
                 return "";
            }
         }
     }
     
     public static boolean rootAccessPossible() {
         return CASUALTools.uidMatches(new String[]{"root","0"})||!CASUALTools.rootAccessCommand().isEmpty();
    }

     public static int getBuildNumber(){
            return Integer.parseInt(java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.buildnumber"));    
     }
    public static String getBuildNumberString() {
              return java.util.ResourceBundle.getBundle("CASUAL/resources/CASUALApp").getString("Application.buildnumber");    
    }
    /**
     * Starts the GUI, should be done last and only if needed.
     */
     public Runnable GUI = new Runnable() {
         @Override
         public void run() {
             try {
                 setGUIAPI();
                 CASUALSessionData.getGUI().setVisible(true);
             } catch (ClassNotFoundException ex) {
                 Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
             } catch (InstantiationException ex) {
                 Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IllegalAccessException ex) {
                 Logger.getLogger(CASUALTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    /**
     * md5sumTestScript Refreshes the MD5s on the scripts in the /SCRIPTS folder
     */
     private void md5sumTestScripts(){
         Log.level4Debug("\nIDE Mode: Scanning and updating MD5s.\nWe are in " + System.getProperty("user.dir"));
         incrementBuildNumber();
         
         if (getIDEMode()) { //if we are in development mode
             //Set up scripts path
             String scriptsPath = System.getProperty("user.dir") + CASUALSessionData.slash + "SCRIPTS" + CASUALSessionData.slash;
             final File folder = new File(scriptsPath);
             if (folder.isDirectory()) {
                 for (final File fileEntry : folder.listFiles()) {
                     if (fileEntry.toString().endsWith(".meta")) {
                         InputStream in = null;
                         try {
                             //load each meta file into a properties file
                             Log.level3Verbose("Verifying meta: " + fileEntry.toString());
                             LinkedProperties prop = new LinkedProperties();
                             in = new FileInputStream(fileEntry);
                             prop.load(in);
                             in.close();
                             //Identify and store the new MD5s
                             String md5;
                             int pos = 0;
                             boolean md5Changed = false;
                             while ((md5 = prop.getProperty("Script.MD5[" + pos + "]")) != null) {
                                 String entry = "Script.MD5[" + pos + "]";
                                 String[] md5File = md5.split("  ");
                                 String newMD5 = new MD5sum().md5sum(scriptsPath + md5File[1]);
                                 if (!md5.contains(newMD5)) {
                                     md5Changed = true;
                                     Log.level4Debug("Old MD5: " + md5);
                                     Log.level4Debug("New MD5: " + prop.getProperty(entry));
                                 }
                                 prop.setProperty(entry, newMD5 + "  " + md5File[1]);
                                 pos++;
                             }
                             
                             if (md5Changed) {
                                 Log.level4Debug("MD5s for " + fileEntry + " changed. Updating...");
                                 FileOutputStream fos = new FileOutputStream(fileEntry);
                                 prop.store(fos, null);
                                 fos.close();
                                 
                             }
                         } catch (FileNotFoundException ex) {
                             Log.errorHandler(ex);
                         } catch (IOException ex) {
                             Log.errorHandler(ex);
                         } finally {
                             try {
                                 if (in != null) {
                                     in.close();
                                 }
                             } catch (IOException ex) {
                                 Log.errorHandler(ex);
                             }
                         }
                    }
                }
            }
        }
    }

    /**
     * prepares the script for execution by setting up environment
     *
     * @param scriptName
     */
     /**
      * tells if CASUAL is running in Development or Execution mode
      *
      * @return true if in IDE mode
      */
     private boolean getIDEMode() {
         String className = getClass().getName().replace('.', '/');
         String classJar = getClass().getResource("/" + className + ".class").toString();
        String path = new File(".").getAbsolutePath();
        boolean isSource = path.contains("src") && path.contains("CASUALcore");
        return classJar.startsWith("file:") && isSource;
     }
     
     //This is only used in IDE mode for development
     private void incrementBuildNumber() throws NumberFormatException {
         Properties prop = new Properties();
         try {
             if (new File(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties").exists()) {
                 prop.load(new FileInputStream(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties"));
                 int x = Integer.parseInt(prop.getProperty("Application.buildnumber").replace(",", ""));
                 x++;
                 prop.setProperty("Application.buildnumber", Integer.toString(x));
                 prop.setProperty("Application.buildnumber", Integer.toString(x));
                 
                 prop.store(new FileOutputStream(System.getProperty("user.dir") + "/CASUAL/resources/CASUALApp.properties"), "Application.buildnumber=" + x);
            }
        } catch (IOException ex) {
            Log.errorHandler(ex);
        }
    }

}
