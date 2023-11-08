package com.igsl.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;

public class SQLConfig {
	private static final String CONFIG_FILE = "SQLConfig.properties";
	private static final Logger LOGGER = LogManager.getLogger(SQLConfig.class);
	private static SQLConfig instance;
	private Properties props;
	
	static {
		instance = new SQLConfig();
		try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(CONFIG_FILE)) {
			instance.props = new Properties();
			instance.props.load(in);
		} catch (IOException ex) {
			Log.error(LOGGER, "Failed to load SQLConfig", ex);
		}
	}
	
	public static SQLConfig getInstance() {
		return instance;
	}
	
	public String getSQL(Class<?> cls) {
		if (cls != null) {
			return props.getProperty(cls.getCanonicalName());
		} 
		return null;
	}
	
	public String getSQL(String name) {
		return props.getProperty(name);
	}
}
