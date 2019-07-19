package com.aimir.fep.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.Enumeration;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FMP properties manager
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPProperty
{
    private static Log log = LogFactory.getLog(FMPProperty.class);
    private static final Properties properties;

    static {
        String it = "/config/fmp.properties";

        Properties result = new Properties();
        try
        {
            InputStream is = 
                FMPProperty.class.getResourceAsStream(it);
            result.load(is);
            is.close();

        } catch(Exception e) {
        	log.error("Cannot find fmp.properties");
        	it = "/config/schedule.properties";
        	try {
        		InputStream is = FMPProperty.class.getResourceAsStream(it);
        		result.load(is);
        		is.close();
        	} catch (Exception e1) {
            	log.error("Cannot find schedule.properties");
            	it = "/command.properties";
        		try {
            		InputStream is = FMPProperty.class.getResourceAsStream(it);
            		result.load(is);
            		is.close();
				} catch (Exception e2) {
					log.error("Cannot find command.properties");
					log.error(e2,e2);
				}
        	}
        }

        properties = result;
    }

    private FMPProperty() {
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
            url = FMPProperty.class.getResource(val);
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
    public static String getProperty(String key,String value)
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
    
    /**
     * set property
     * @param key
     * @param value
     */
    public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
    }
}
