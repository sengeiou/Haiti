package com.aimir.util;

import java.util.regex.Pattern;

import com.googlecode.ipv6.IPv6Address;

public class IPUtil
{
    public static String formatTrim(String ip) {
        return format(trim(ip));
    }
    
    public static String format(String ip) {
        if (ip != null && checkIPv6(ip))
            return IPv6Address.fromString(ip).toString().toUpperCase();
        else
            return ip;
    }
    
    public static boolean checkIPv4(String ip) {
        String regexIPv4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
        // IPv4
        Pattern pattern = Pattern.compile(regexIPv4);
        return pattern.matcher(ip).matches();
    }
    
    public static boolean checkIPv6(String ip) {
        String regexIPv6 = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
        // IPv6
        Pattern pattern = Pattern.compile(regexIPv6);
        return pattern.matcher(ip).matches();
    }
    
    public static String trim(String ip) {
        if (ip == null) return null;
        ip = ip.replace("/", "");
        
        /*
        String result = "";
        String[] array = ip.split(":", -1);
        int len = array.length;
        boolean portFlag = false;
        
        if (len <=1) return ip;
        if ((array[len-1].length()>=5) && (Integer.parseInt(array[len-1]) >= ephemeralPort)) {
            portFlag = true;
        }
        
        if (portFlag) {
            for(int i=0; i<len-2; i++) {
                result = result + array[i] + ":";
            }
            result = result + array[len-2];
        } else {
            for(int i=0; i<len-1; i++) {
                result = result + array[i] + ":";
            }
            result = result + array[len-1];
        }
        return result;
        */
        if (ip != null && !"".equals(ip) && ip.lastIndexOf(":") != -1) {
            ip = ip.substring(0, ip.lastIndexOf(":"));
        }
        
        return ip;
    }
}
