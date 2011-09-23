package com.ickik.ladder.values;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This enum represents the different files supported
 * by the loader. It is files which could be executed
 * by the test framework.
 * @author Patrick Allgeyer
 * @version 0.1.000, 06/04/11
 */
public enum TypeFile {

	/*XML("frame.radio.testfile.xml") {
		@Override
		public String getExtension() {
			return ".xml";
		}

		@Override
		public boolean isValidFile(File file) {
			try {
				System.out.println(file.getAbsolutePath());
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				LadderDefaultHandler handler = new LadderDefaultHandler();
				parser.parse(file, handler);
				return handler.isValide();
				
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
			return false;
		}
	},*/
	JAR("frame.radio.testfile.jar") {
		@Override
		public String getExtension() {
			return ".jar";
		}

		@Override
		public boolean isValidFile(File file) {
			if (file.isFile()) {
				try {
					JarFile jarFile = new JarFile(file.getAbsolutePath());
					Enumeration<JarEntry> jarFileEnum = jarFile.entries();
					while (jarFileEnum.hasMoreElements()) {
			    		String fileName = jarFileEnum.nextElement().getName();
			    		if(fileName.equals("testng.xml")) {
			    			jarFile.close();
			    			return true;
			    		}
			    	}
					jarFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}
	},
	/*CLASS("frame.radio.testfile.class") {
		@Override
		public String getExtension() {
			return ".class";
		}

		@Override
		public boolean isValidFile(File file) {
			LadderClassLoader lcl = LadderClassLoader.getInstance();
			try {
				Class<?> classe = lcl.loadClass(file.getAbsolutePath());
				if (classe != null) {
					if (classe.getAnnotation(org.testng.annotations.Test.class) != null) {
						return true;
					}
					if (classe.getAnnotation(org.junit.Test.class) != null) {
						return true;
					}
					for (Method m : classe.getDeclaredMethods()) {
						if (m.isAnnotationPresent(org.testng.annotations.Test.class)) {
							return true;
						} else if (m.isAnnotationPresent(org.junit.Test.class)) {
							return true;
						}
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	}*/;
	
	private String type;
	
	private TypeFile(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return type;
	}
	
	public abstract String getExtension();
	public abstract boolean isValidFile(File file);
	
	private class LadderDefaultHandler extends DefaultHandler {
		
		private boolean isTestNG = false;
		private final List<String> beaconList = new ArrayList<String>();
		
		public LadderDefaultHandler() {
			beaconList.add("suite");
			beaconList.add("test");
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			//System.out.println("Start Element :" + qName);
			String beacon = qName.toLowerCase();
			if (beaconList.contains(beacon) && !isTestNG) {
				isTestNG = true;
			}
		}
		
		public boolean isValide() {
			return isTestNG;
		}
	}
}
