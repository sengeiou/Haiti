package com.aimir.mars.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MarsProperty {

    private static Log log = LogFactory.getLog(MarsProperty.class);
    private static final Properties properties;

    static {
        String it = "/config/mars.properties";

        Properties result = new Properties();
        try {
            InputStream is = MarsProperty.class.getResourceAsStream(it);
            result.load(is);
            is.close();

        } catch (Exception e) {
            log.error("Cannot find mars.properties");
        }

        properties = result;
    }

    private MarsProperty() {
        super();
    }

    /**
     * get PropertyURL
     *
     * @param key
     *            <code>String</code>
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
            url = MarsProperty.class.getResource(val);
        }

        return url;

    }

    /**
     * get property
     *
     * @param key
     *            <code>String</code> key
     * @return value <code>String</code>
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * get property
     *
     * @param key
     *            <code>String</code> key
     * @param key
     *            <code>String</code> value
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
