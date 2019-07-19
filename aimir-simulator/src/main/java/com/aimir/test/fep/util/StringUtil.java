package com.aimir.test.fep.util;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * String Util
 *
 * @author 2.x버전에서 가져옴
 */
public class StringUtil
{	
	private static String osCharSet = "8859_1";
    private static String dbCharSet = "UTF-8";
    private static boolean isConversion = true;
    
    public static String nullCheck(String p,String m)
    {
        if((p==null)||(p.length()==0))
        {
            return m;
        }else
        {
            /** added by D.J Park in 2006.05.25 
             * To solve the Hangul Encoding Problem
             * must be called this function in All JSP
            try{
             p = new String(p.getBytes("EUC_KR") , "8859_1");
            } catch(Exception ex) { ex.printStackTrace();}
             */
            return p;
        }

    }
    public static String countLength(String p,int count)
    {
        String result="";
        for (int i=0;i<count ;i++ ) {
            result=result+p;
        }//for
        return result;
    }
    public static String reverse(String source)
    {
        int i, len = source.length();
        StringBuffer dest = new StringBuffer(len);

        for (i = (len - 1); i >= 0; i--)
            dest.append(source.charAt(i));
        return dest.toString();
    }

    public static String frontAppendNStr(char append, String str, int length)
    {
        StringBuffer b = new StringBuffer("");

        if(str.length() < length)
        {
           for(int i = 0; i < length-str.length() ; i++)
               b.append(append);
           b.append(str);
        }
        else
        {
            b.append(str);
        }

        return b.toString();
    }

    public static String endAppendNStr(char append, String str, int length)
    {
        StringBuffer b = new StringBuffer("");

        if (str.length() < length)
        {
            b.append(str);
            for (int i = 0; i < length - str.length(); i++)
                b.append(append);
        }
        else
        {
            b.append(str);
        }

        return b.toString();
    }
    
    public static String getHexDump(byte[] b){
        StringBuffer buf = new StringBuffer();
        int idx = 0;
        try{
            for(int i = 0; i < b.length; i++) {
                int val = b[i];
                val = val & 0xff;
                buf.append(frontAppendNStr('0',Integer.toHexString(val),2)+" ");
                if(((i+1)%16) == 0) 
                {
                    buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+new String(b,idx,16) +"");
                    idx += 16;
                    buf.append("<br>");
                }
            }
        }catch(Exception e){
            
        }
        return buf.toString();
    }
    
    public static String removeNewLine(String src) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < src.length(); i++)
        {
            char c = src.charAt(i);
            if (c != '\n')
            {
                sb.append(c);
            } else
            {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    
    public static int getDigitOnly(String org) throws NumberFormatException
    {
        StringBuffer sb = new StringBuffer();
        
        if (org != null) {
            for(int i = 0; i < org.length(); i++){
                if(org.charAt(i) < '0' || org.charAt(i) > '9'){
                    break;
                }
                else
                {
                    sb.append(org.charAt(i));
                }
            }
        }else{
            throw new NumberFormatException();
        }
        
        if(sb != null && sb.length() > 0){
            return Integer.parseInt(sb.toString());
        }else{
            throw new NumberFormatException();
        }
    }
    
	/**
	 * 문자열의 null 여부를 check하여 null일 경우 ""를 리턴한다.
	 * null이 아닐 경우에는 문자열의 trim()을 호출한 후 리턴한다.
	 * @param comment
	 * @return 공백이나 trim() 결과
	 */
	public static String nullToBlank(Object comment) {
		if (comment == null) return "";
		return String.valueOf(comment).trim();
	}
	
	public static String nullToZero(Object comment) {
		if (comment == null) return "0";
		return comment.toString();
	}
	
	public static Double nullToDoubleZero(Double comment) {
		if (comment == null) return 0d;
		return comment;
	}
	
    public static String toDB(String str)
    throws UnsupportedEncodingException
	{   
	    if(str == null)
	        return null;
	    if(isConversion)
	        return new String(str.getBytes(osCharSet),dbCharSet);
	    return str;
	}       
            
    public static String fromDB(String str)
    throws UnsupportedEncodingException
	{           
	    if (str == null)
	        return null;
	    if(isConversion)
	        return new String(str.getBytes(dbCharSet), osCharSet);
	    return str;
	}

    public static boolean isDigit(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * @MethodName joinList
     * @Date 2013. 11. 4.
     * @param list
     * @return
     * @Modified
     * @Description
     */
    @SuppressWarnings("rawtypes")
	public static String joinList(List list, String sep) {
    	String separator = ",";
    	if ( list == null || list.size() < 1 ) {
    		return "";
    	}
    	if ( !nullToBlank( sep ).equals("") ) {
    		separator = sep;
    	}
    	String result = list.toString().replace("[", "")
    	.replace("]", "")
    	.replace(", ", separator);
    	return result;
    }
}//end
