package com.ickik.ladder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.ickik.ladder.exception.LadderException;

/**
 * Manage the properties for this soft. Load and save them
 * into a properties file.
 * @author Patrick Allgeyer, p.allgeyer@gmail.com
 * @version 0.1.000, 08/03/11
 */
public class TestLadderProperties {

	private static TestLadderProperties testLadderProperties;
	private Properties properties;
	private static final String directory = ".TestLadder";
	private static final String userDirectory = System.getProperty("user.home");
	private boolean propertiesFileExists = false;
	
	private TestLadderProperties() throws LadderException {
		File f = new File(userDirectory + "/" + directory);
		if (!f.exists()) {
			f.mkdir();
		}
		properties = propertiesFileLoader(new File(userDirectory + "/" + directory + "/user.properties"));
	}
	
	/**
	 * Load the property file given by argument and return a
	 * Properties Object.
	 * @param propertiesFile the property file.
	 * @return an instance of Properties.
	 * @throws LadderException if the file could not be read.
	 */
	private Properties propertiesFileLoader(File propertiesFile) throws LadderException {
		if (!propertiesFile.exists()) {
			try {
				return initDefaultProperties(propertiesFile);
			} catch (IOException e) {
				e.printStackTrace();
				throw new LadderException("");
			}
		}
		propertiesFileExists = true;
		return loadProperties(propertiesFile);
	}
	
	/**
	 * Return if the properties exists before starting the
	 * application.
	 * @return true if it exists before, false otherwise.
	 */
	public boolean propertiesExists() {
		return propertiesFileExists;
	}
	
	/**
	 * Define a default property file which all default properties.
	 * @param propertiesFile the property file to create and to
	 * initialize.
	 * @return an instance of Properties Object.
	 * @throws IOException if the save of the default properties in the
	 * file was not possible.
	 */
	private Properties initDefaultProperties(File propertiesFile) throws IOException {
		
		propertiesFile.createNewFile();
		Properties properties = new Properties();
		for (LadderProperties p : LadderProperties.values()) {
			properties.put(p.name(), p.getDefaultValue());
		}
		FileOutputStream out = new FileOutputStream(propertiesFile);
		try {
			properties.store(out, "Virtual ICTouch Computing file properties");
		} finally {
			out.close();
		}
		return properties;
	}
	
	private Properties loadProperties(File propertiesFile) throws LadderException {
		Properties properties = new Properties();
		try {
			InputStream in = new FileInputStream(propertiesFile);
			try {
				properties.load(in);
			} finally {
				in.close();
			}
		} catch (FileNotFoundException e) {
			throw new LadderException("");
		} catch (IOException e) {
			throw new LadderException("");
		}
		checkProperties(properties);
		return properties;
	}
	
	private void checkProperties(Properties properties) {
		List<String> propertiesList = new ArrayList<String>();
		for (LadderProperties p : LadderProperties.values()) {
			propertiesList.add(p.name());
		}
		checkPropertiesKeys(properties, propertiesList);
		checkPropertiesValues(properties);
	}
	
	private void checkPropertiesKeys(Properties properties, List<String> propertiesList) {
		BitSet propertiesFound = new BitSet(propertiesList.size());
		List<String> failedPropertiesList = new ArrayList<String>();
		for (Object s : properties.keySet()) {
			if (!propertiesList.contains(s)) {
				failedPropertiesList.add((String) s);
			} else {
				propertiesFound.set(propertiesList.indexOf(s), true);
			}
		}
		for(String s : failedPropertiesList) {
			properties.remove(s);
		}
		int i = propertiesFound.nextClearBit(0);
		while (i < propertiesList.size()) {
			propertiesFileExists = false;
			properties.put(LadderProperties.values()[i].name(), LadderProperties.values()[i].getDefaultValue());
			i = propertiesFound.nextClearBit(i + 1);
		}
	}
	
	private void checkPropertiesValues(Properties properties) {
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String value = entry.getValue().toString();
			if (value.isEmpty()) {
				entry.setValue(LadderProperties.valueOf(entry.getKey().toString()).getDefaultValue());
			}
		}
	}
	
	/**
	 * Return the loaded properties.
	 * @return the loaded properties.
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * Save the properties in the file which contains all of them.
	 * @throws LadderException is throw when the writing was not
	 * possible.
	 */
	public void saveProperties() throws LadderException {
		File propertiesFile = new File(userDirectory + "/" + directory + "/user.properties");
		//logger.debug("saveProperties - save the properties under {}", new Object[]{propertiesFile.getAbsolutePath()});
		try {
			FileOutputStream out = new FileOutputStream(propertiesFile);
			try {
				properties.store(out, "Virtual ICTouch Computing file properties");
			} finally {
				out.close();
			}
		} catch (FileNotFoundException e) {
			//logger.debug("saveProperties - File not found {} cause by : {}", new Object[]{ e.getMessage(), e.getCause()});
			throw new LadderException("");
		} catch (IOException e) {
			//logger.debug("saveProperties - cannot write in the file {}, caused by: {}", new Object[]{e.getMessage(), e.getCause()});
			throw new LadderException("");
		}
	}
	
	/**
	 * Return an instance of the {@link TestLadderProperties}. The instance is
	 * unique, so it returns every time the same object.
	 * @return an unique instance of {@link TestLadderProperties}.
	 * @throws LadderException is throw when it was not possible to load or
	 * create the default properties.
	 */
	public static TestLadderProperties getInstance() throws LadderException {
		if (testLadderProperties == null) {
			testLadderProperties = new TestLadderProperties();
		}
		return testLadderProperties;
	}
}
