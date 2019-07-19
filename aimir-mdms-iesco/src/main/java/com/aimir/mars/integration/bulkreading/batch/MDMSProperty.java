package com.aimir.mars.integration.bulkreading.batch;

import java.io.InputStream;
import java.util.Properties;
import java.util.Enumeration;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MDMSProperty
{
    private static Log log = LogFactory.getLog(MDMSProperty.class);
    private static final Properties properties;

    static {
    	
        String it = "/config/mdms.config.properties";

        Properties result = new Properties();
        try {
            InputStream is = 
                MDMSProperty.class.getResourceAsStream(it);
            result.load(is);
            is.close();
        } catch(Exception e) {
        	e.printStackTrace();
            log.error(e, e);
        }
        properties = result;
    }

    private MDMSProperty() {
        super();
    }

    /**
     * get PropertyURL
     *
     * @param key <code>String</code>
     * @return url <code>URL</code>
     */
    public static URL getPropertyURL(String key)
    {
        String val = properties.getProperty(key);

        URL url = null;
        try { url = new URL(val); }catch(Exception ex) {}

        if(url == null)
        {
            url = MDMSProperty.class.getResource(val);
        }

        return url;

    }

    /**
     * get property
     *
     * @param key <code>String</code> key
     * @return value <code>String</code>
     */
    public static String getProperty(String key)
    {
        return properties.getProperty(key);
    }

    /**
     * get property
     *
     * @param key <code>String</code> key
     * @param key <code>String</code> value
     * @return value <code>String</code>
     */
    public static String getProperty(String key, String value)
    {
        return properties.getProperty(key,value);
    }

    /**
     * get property names
     *
     * @return enumeration <code>Enumeration</code>
     */
    public static Enumeration<?> propertyNames()
    {
        return properties.propertyNames();
    }
}
