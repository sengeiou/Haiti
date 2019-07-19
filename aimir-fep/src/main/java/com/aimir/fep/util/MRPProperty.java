package com.aimir.fep.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.Enumeration;
import java.net.URL;

/**
 * MRP properties manager
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class MRPProperty
{
    private static final Properties properties;

    static {
        String it = "/properties/fmp.properties";

        Properties result = new Properties();
        try
        {
            InputStream is = 
                MRPProperty.class.getResourceAsStream(it);
            result.load(is);
            is.close();

        } catch(Exception ignored) {}

        properties = result;
    }

    private MRPProperty() {
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
            url = MRPProperty.class.getResource(val);
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
}
