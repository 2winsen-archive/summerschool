package demo.jdbc.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

/**
 * TODO could be useful, but not applicable to this project, 
 * since Spring config (applicationContext.xml) is being used. 
 * 
 * @author kap
 */
public class PropertyManager {
	
	private static PropertyManager instance;
	
	private static Properties properties;
	
	private static String propFile;
	
	private PropertyManager() {
		URL url =  ClassLoader.getSystemResource(propFile);
		properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(url.getFile())));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load PropertyManager", e);
		}

	}
	
	public static void load(String fileName) {
		if (!fileName.equals(propFile) || instance == null) {
			propFile = fileName; 
			instance = new PropertyManager();
		}
	}

	public static String getProperty(String key) {
		if (properties != null) {
			return properties.getProperty(key);
		}
		else {
			return null;
		}
	}
}
