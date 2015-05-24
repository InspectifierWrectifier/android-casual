package CASUAL.misc;

/*
 * http://www.javaworld.com/javatips/jw-javatip70.html


/**
 *
 * @author adam
 */
public class JarClassLoader extends MultiClassLoader
    {
    private final JarResources    jarResources;
    public JarClassLoader (String jarName)
    {
    // Create the JarResource and suck in the jar file.
    jarResources = new JarResources (jarName);
    }
    @Override
    protected byte[] loadClassBytes (String className)
    {
    // Support the MultiClassLoader's class name munging facility.
    className = formatClassName (className);
    // Attempt to get the class data from the JarResource.
    return (jarResources.getResource (className));
    }
    }