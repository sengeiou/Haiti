package com.aimir.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

public class PropertyUtil {

    public String getCountry() {
        return System.getProperty("user.country");
    }

    public void setCountry(String country) {
        System.setProperty("user.country", country);
    }

    public String getLanguage() {
        return System.getProperty("user.language");
    }

    public void setLanguage(String language) {
        System.setProperty("user.language", language);
    }

    public String getTimeZone() {
        return System.getProperty("user.timezone");
    }

    public void setTimeZone(String timeZone) {
        System.setProperty("user.timezone", timeZone);
    }
    
    public void setLocaleWithSession(HttpServletRequest request, String locale) {
        setLanguage(locale);
        WebUtils.setSessionAttribute(request,"org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE", locale);
    }
}
