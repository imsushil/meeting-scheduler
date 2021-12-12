package com.systemdesign.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private static final Properties properties = new Properties();
    private static final String FILE_NAME = "app.properties";

    static {
        init();
    }

    private static void init() {
        try {
            logger.debug("Reading " + FILE_NAME + " file...");
            InputStream istream = Configuration.class.getResourceAsStream("/" + FILE_NAME);
            properties.load(istream);
        } catch (FileNotFoundException e) {
            logger.error("Please add config file " + FILE_NAME + " to your classpath.");
        } catch (IOException e) {
            logger.error("Unable to read configurations from " + FILE_NAME);
        }
    }

    public static String get(String propertyName) {
        if (!properties.containsKey(propertyName)) return null;
        return (String) properties.get(propertyName);
    }

}
