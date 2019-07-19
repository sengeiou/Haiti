package com.aimir.schedule.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.Enumeration;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class BemsProperty {
    private static Log log = LogFactory.getLog(BemsProperty.class);
    private static final Properties properties;

    static {
        String it = "/config/bems.properties";

        Properties result = new Properties();
        try {
            InputStream is = BemsProperty.class.getResourceAsStream(it);
            result.load(is);
            is.close();

        } catch (Exception e) {
            log.error(e);
        }

        properties = result;
    }

    private BemsProperty() {
        super();
    }

    /**
     * get PropertyURL
     * 
     * @param key <code>String</code>
     * @return url <code>URL</code>
     */
    public static URL getPropertyURL(String key) {
        String val = properties.getProperty(key);

        URL url = null;
        try {
            url = new URL(val);
        } catch (Exception ex) {
        }

        if (url == null) {
            url = BemsProperty.class.getResource(val);
        }

        return url;

    }

    /**
     * get property
     * 
     * @param key <code>String</code> key
     * @return value <code>String</code>
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * get property
     * 
     * @param key <code>String</code> key
     * @param key <code>String</code> value
     * @return value <code>String</code>
     */
    public static String getProperty(String key, String value) {
        return properties.getProperty(key, value);
    }

    /**
     * get property names
     * 
     * @return enumeration <code>Enumeration</code>
     */
    public static Enumeration<?> propertyNames() {
        return properties.propertyNames();
    }
}
