package com.ickik.ladder.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import org.testng.annotations.Test;

import com.ickik.ladder.listener.TestFramework;

public class LadderClassLoader extends ClassLoader {
        
	private JarFile jar;
    private final Set<Class<?>> classes = new HashSet<Class<?>>();
    private static final LadderClassLoader instance;
    
    static {
        instance = new LadderClassLoader();
    }

    /**
     * Private default in order to make it Singleton
     */
    private LadderClassLoader() {}

    /**
     * Get the classes loaded by this classloader
     * @return The classes loaded by this classloader
     */
    Set<Class<?>> getClasses() {
            return classes;
    }

    /**
     * Get the unique instance of DiceLoader
     * @return The unique instance of DiceLoader
     */
    public static LadderClassLoader getInstance() {
            return instance;
    }

    /**
     * Load all Dice classes from a Jar file.
     * Only Dices that match the method checkLoadClass are loaded
     * @param jarName The name of the Jar archive   
     * @return A Set that contains the list of the Dice Class<?>
     * @throws IOException if an I/O error has occurred
     */
    public Set<Class<?>> loadJar(String jarName) throws IOException  {
    	jar = new JarFile(jarName);
    	Enumeration<? extends ZipEntry> entries = jar.entries();
    	while (entries.hasMoreElements()) {
    		String className = entries.nextElement().getName();
    		if(className.endsWith(".class")) {
    			try {                                   
    				Class<?> clazz = this.loadClass(className.replace(File.separatorChar, '.'));
    				
    				if(clazz!=null){
    					if (checkLoadClass(clazz)) {
    						classes.add(clazz);
        				}
    				}
    			} catch (ClassNotFoundException e) {
    				System.err.println(e.getMessage());                                     
    			}
    		}
    	}
    	jar.close();

    	return classes;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
    	class InnerMethod {
    		private LadderClassLoader loader = LadderClassLoader.getInstance();

    		/**
    		 * Get the class for the findClass method
    		 * @param className The name of the class
    		 * @param binaryName The binary name of the class
    		 * @return The Class if it is found
    		 * @throws IOException if an I/O error has occurred
    		 * @throws ClassNotFoundException 
    		 */
    		private Class<?> getClass(String className,String binaryName) throws IOException {
    			Class<?> clazz;
    			File file = new File(className);
    			DataInputStream dis = new DataInputStream(new FileInputStream(className));
    			byte[] b = new byte[(int)file.length()];
    			dis.readFully(b,0,b.length);
    			dis.close();
    			clazz = loader.defineClass(binaryName, b, 0, b.length).asSubclass(Test.class);
    			loader.resolveClass(clazz);

    			classes.add(clazz);
    			return clazz;
    		}

    		/**
    		 * Get the class from a jar file for the findClass method
    		 * @param className The name of the class
    		 * @param binaryName The binary name of the class
    		 * @return The Class if it is found 
    		 * @throws IOException if an I/O error has occurred
    		 */
    		private Class<?> getClassFromJar(String className,String binaryName) throws IOException {
    			try {
    				Class<?> c = Class.forName(binaryName);
    				if(c.getClassLoader()==loader.getParent()) {
    					return c;
    				}
    			} catch (ClassNotFoundException e) {
    				/*We just want to test if the bin name is already loaded by the
    				 * system classLoader. If not, just follow the next step of the method*/
    			}                               

    			ZipEntry ze = jar.getEntry(className);
    			if(ze==null) return null;
    			Class<?> clazz=null;
    			DataInputStream dis;
    			try {
    				InputStream in = jar.getInputStream(ze);

    				dis = new DataInputStream(in);
    				byte[] b = new byte[(int)ze.getSize()];
    				dis.readFully(b,0,b.length);
    				dis.close();
    				clazz = loader.defineClass(binaryName, b, 0, b.length);//.asSubclass(Test.class);
    				loader.resolveClass(clazz);
    			} catch(ClassCastException e) {
    				System.err.println(e.getMessage()+" is not a valid Dice cce");
    				e.printStackTrace();
    			} catch(ZipException e) {
    				System.err.println(e.getMessage()+" is not a valid Dice zip");
    			} catch(SecurityException e) {
    				System.err.println(e.getMessage()+" is not a valid Dice se");
    			} catch(IllegalStateException e) {
    				System.err.println(e.getMessage()+" is not a valid Dice ise");
    			} catch(IOException e) {
    				System.err.println(e.getMessage()+" is not a valid Dice ioe");
    			} catch(NoClassDefFoundError e) {
    				System.err.println(e.getMessage()+" is not a valid Dice ncdf");
    				e.printStackTrace();
    			}

    			return clazz;
    		}
    	}

    	InnerMethod inner = new InnerMethod();

    	String binaryName = className.replace(File.separatorChar, '.').substring(0, className.length()-6);

    	Class<?> alreadyLoaded = this.findLoadedClass(binaryName);              
    	if(alreadyLoaded!=null) {
    		return alreadyLoaded;
    	}

    	Class<?> clazz=null;
    	try {
    		clazz = jar==null?inner.getClass(className,binaryName):inner.getClassFromJar(className, binaryName);

    	} catch (IOException e) {
    		System.err.println(e.getMessage()+" Reading "+className+" error");
    	} 
    	
    	return clazz;
    }

    /**
     * Check if the class match the following : 
     *  * - implements Dice Interface
     * - has just one constructor
     * - this contructor must have parameter with type : int, float or String
     * - this contructor must be annoted by DiceDescription & DiceDescription must return String[] type 
     * - The length of the return String[] DiceDescription must be equals to the 
     * number of constructor's arguments +1 
     * @param nameClass The name of the class loaded by the class loader
     * @return The Dice class if exists;null otherwise
     * @throws ClassNotFoundException 
     */
    @SuppressWarnings("unchecked")
	private boolean checkLoadClass(Class<?> nameClass) throws ClassNotFoundException {
    	if(nameClass==null) {
    		return false;
    	}
    	for (TestFramework tf : TestFramework.values()) {
    		if (nameClass.getAnnotation(tf.getTestAnnotation()) != null) {
        		return true;
        	}
    	}
    	
    	for (Method m : nameClass.getDeclaredMethods()) {
    		for (TestFramework tf : TestFramework.values()) {
        		if (m.getAnnotation(tf.getTestAnnotation()) != null) {
            		return true;
            	}
        	}
    	}
    	return false;
    }
}
