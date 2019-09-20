package com.aimir.util;

import org.springframework.context.ApplicationContext;

public class ContextUtil {

    private static ApplicationContext appCtx = null;
        // new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"});
    
    /**
     * constructor
     */
    public ContextUtil()
    {
    }


    public static void setApplicationContext(ApplicationContext ctx) {
        appCtx = ctx;
    }
    
    public static <T> T getBean(Class<T> clazz) {
        if (appCtx != null) return appCtx.getBean(clazz);
        
        return null;
    }
    
    public static Object getBean(String beanName) {
        if (appCtx != null) return appCtx.getBean(beanName);
        
        return null;
    }
    
    public static Object getBean(String beanName, Class clazz) {
        if (appCtx != null) return appCtx.getBean(beanName, clazz);
        
        return null;
    }
}
