/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import CASUAL.caspac.Script;
import CASUAL.misc.JarClassLoader;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adam
 */
public class CASUALJUnitTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    public CASUALJUnitTest() {
    }
    /*
     -do-compile-test:
     [javac] Compiling 13 source files to /var/lib/jenkins/workspace/ProjectCASUAL/trunk/CASUALcore/build/test/classes
     [javac] /var/lib/jenkins/workspace/ProjectCASUAL/trunk/CASUALcore/test/CASUAL/CASUALJUnitTest.java:36: warning: [rawtypes] found raw type: Class
     [javac]         Class c = jarLoader.loadClass("CASUAL.CASUALApp",true);
     [javac]         ^
     [javac]   missing type arguments for generic class Class<T>
     [javac]   where T is a type-variable:
     [javac]     T extends Object declared in class Class
     [javac] /var/lib/jenkins/workspace/ProjectCASUAL/trunk/CASUALcore/test/CASUAL/CASUALJUnitTest.java:42: warning: [static] static method should be qualified by type name, CASUALApp, instead of by an expression
     [javac]             ca.main(new String[]{});
     [javac]               ^
     [javac] /var/lib/jenkins/workspace/ProjectCASUAL/trunk/CASUALcore/test/CASUAL/CASUALLanguageTest.java:241: warning: [static] static method should be qualified by type name, SHA256sum, instead of by an expression
     [javac]             String sha256sum=new CASUAL.crypto.SHA256sum(new File(result)).getLinuxSum(new File(result));
     [javac]                                                                           ^
     [javac] 3 warnings
     */
    @Test
    @SuppressWarnings({"unchecked", "rawtypes", "static"})
    public void testCASUAL() throws Exception {
        System.out.println(new File("../dist/CASUALcore.jar").getCanonicalPath());
        JarClassLoader jarLoader = new JarClassLoader("../dist/CASUALstatic.jar");
        Class c = jarLoader.loadClass("CASUAL.CASUALMain", true);
        Object cmain = c.newInstance();
        CASUAL.CASUALMain ca = (CASUAL.CASUALMain) cmain;
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        CASUALMain.main(new String[]{});
        Thread.sleep(7000);
        Script s=CASUALMain.getSession().CASPAC.getActiveScript();
        assert s.getName().equals("TestScript");
        System.out.println(CASUALMain.getSession().getTempFolder());
    }
}
