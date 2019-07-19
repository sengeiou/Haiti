/*
 * Created on 2006. 11. 22
 */
package com.aimir.schedule.util;

import java.util.Properties;
import java.util.Enumeration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ScheduleProperty Utility Class 
 * 
 * @author 
 * @version 1.0
 */
public class ScheduleProperty {
    private static Log log = LogFactory.getLog(ScheduleProperty.class);
    
	private static Properties properties = null;
    private static File pfile = new File("config/schedule.properties");
    private static long lastModified = 0;
    private static String filename = null;
    static {
       
        
        filename = "quartz.properties";
        Properties prop = new Properties();
        try
        {
        	/*
            InputStream is = 
            	ScheduleProperty.class.getResourceAsStream(filename);
            result.load(is);
            is.close();
            */

			//prop.load(ScheduleProperty.class.getResourceAsStream(filename));
			
			prop.load(ScheduleProperty.class.getClassLoader().getResourceAsStream(filename));

        } catch(Exception e) {
            log.error(e,e);
        }

        properties = prop;
    }

    public static synchronized void loadProperties()
    {
        if(lastModified < pfile.lastModified())
        {
            lastModified = pfile.lastModified();
            try
            {
                Properties result = new Properties();
                InputStream is = new FileInputStream(pfile);
                result.load(is);
                is.close();
                properties = result;
            } catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * get PropertyURL
     *
     * @param key <code>String</code>
     * @return url <code>URL</code>
     */
    public static URL getPropertyURL(String key)
    {
        loadProperties();
        String val = properties.getProperty(key);

        URL url = null;
        try { url = new URL(val); }catch(Exception ex) {}

        if(url == null)
        {
            url = ScheduleProperty.class.getResource(val);
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
        loadProperties();
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
        loadProperties();
        return properties.getProperty(key,value);
    }

    /**
     * get property names
     *
     * @return enumeration <code>Enumeration</code>
     */
    public static Enumeration propertyNames()
    {
        loadProperties();
        return properties.propertyNames();
    }
};
