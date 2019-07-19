package com.aimir.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

@SuppressWarnings("unused")
public class CommonUtils2
{
    private static Log log = LogFactory.getLog(CommonUtils2.class);
    
    /**
     * @desc 쿼리에 페이징 기능을 추가하는 method
     * @param query : 결과값 가져오기 전에 쿼리 
     * @param conditionMap :page, pageSize 값이 들어있는 conditionMap
     * @return Query
     */
    public static Query  addPagingForQuery(Query query, Map<String, String> conditionMap)
    {
        String strPage = conditionMap.get("page");
        String strPageSize = conditionMap.get("pageSize");

        int pageCommaIndex = strPage.indexOf(".");
        int pageSizeCommaIndex = strPageSize.indexOf(".");
        int page = -1;
        int pageSize = -1;
        
        if(pageCommaIndex > -1)
            page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
        else
            page = Integer.parseInt(strPage);
        
        if(pageSizeCommaIndex > -1)
            pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
        else
            pageSize = Integer.parseInt(strPageSize);

        int firstResult = page * pageSize;
        
        query.setFirstResult(firstResult);      
        query.setMaxResults(pageSize);
        
        
        return query;
        
    }
    
    public static Query  addPagingForQuery2(Query query, Map<String, Object> conditionMap)
    {
        String strPage = (String) conditionMap.get("page");
        String strPageSize = (String) conditionMap.get("pageSize");

        int pageCommaIndex = strPage.indexOf(".");
        int pageSizeCommaIndex = strPageSize.indexOf(".");
        int page = -1;
        int pageSize = -1;
        
        if(pageCommaIndex > -1)
            page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
        else
            page = Integer.parseInt(strPage);
        
        if(pageSizeCommaIndex > -1)
            pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
        else
            pageSize = Integer.parseInt(strPageSize);

        int firstResult = page * pageSize;
        
        query.setFirstResult(firstResult);      
        query.setMaxResults(pageSize);
        
        
        return query;
        
    }
    
    public static SQLQuery  addPagingForQuery2(SQLQuery query, Map<String, Object> conditionMap)
    {
        String strPage = (String) conditionMap.get("page");
        String strPageSize = (String) conditionMap.get("pageSize");

        int pageCommaIndex = strPage.indexOf(".");
        int pageSizeCommaIndex = strPageSize.indexOf(".");
        int page = -1;
        int pageSize = -1;
        
        if(pageCommaIndex > -1)
            page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
        else
            page = Integer.parseInt(strPage);
        
        if(pageSizeCommaIndex > -1)
            pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
        else
            pageSize = Integer.parseInt(strPageSize);

        int firstResult = page * pageSize;
        
        query.setFirstResult(firstResult);      
        query.setMaxResults(pageSize);
        
        
        return query;
        
    }
    
    public static SQLQuery  addPagingForQuery4(SQLQuery query, Map<String, Object> conditionMap)
    {
        
        int intPage = (Integer) conditionMap.get("page");
        int intPageSize = (Integer) conditionMap.get("pageSize");
        
        String strPage =  Integer.toString(intPage);
        String strPageSize = Integer.toString(intPageSize);

        int pageCommaIndex = strPage.indexOf(".");
        int pageSizeCommaIndex = strPageSize.indexOf(".");
        int page = -1;
        int pageSize = -1;
        
        if(pageCommaIndex > -1)
            page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
        else
            page = Integer.parseInt(strPage);
        
        if(pageSizeCommaIndex > -1)
            pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
        else
            pageSize = Integer.parseInt(strPageSize);

        int firstResult = page * pageSize;
        
        query.setFirstResult(firstResult);      
        query.setMaxResults(pageSize);
        
        
        return query;
        
    }
    
    
    
    


    
    public static Query  addPagingForQuery3(Query query, Map<String, Object> conditionMap)
    {
        String strPage = (String) conditionMap.get("page");
        String strPageSize = (String) conditionMap.get("pageSize2");

        int pageCommaIndex = strPage.indexOf(".");
        int pageSizeCommaIndex = strPageSize.indexOf(".");
        int page = -1;
        int pageSize = -1;
        
        if(pageCommaIndex > -1)
            page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
        else
            page = Integer.parseInt(strPage);
        
        if(pageSizeCommaIndex > -1)
            pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
        else
            pageSize = Integer.parseInt(strPageSize);

        int firstResult = page * pageSize;
        
        query.setFirstResult(firstResult);      
        query.setMaxResults(pageSize);
        
        
        return query;
        
    }
    
    
    
    
    
    
    /**
     * @desc : 페이지 따른 idx 만들어 주는 메소드
     * @param curPage
     * @param pageSize
     * @param idx1
     * @return
     */
    public static int makeIdxPerPage(String curPage, String pageSize, int idx1)
    {
        
        return (Integer.parseInt(curPage) * Integer.parseInt(pageSize)) + idx1;
         
    }
    
    
    
    
     /**
     * @desc 
     * @param supplierId
     * @return
     */
    public static int checkSupplierId(String supplierId)
    {
        int supplierId2=0;
        
        
         if ( supplierId =="" || supplierId.toString().length() <=0 || supplierId== null ) 
         {
             //supplierId2= Integer.parseInt("22");//누리텔레콤
            supplierId2= Integer.parseInt("1");//
         }
        else
            supplierId2=  Integer.parseInt(supplierId);
         
         return supplierId2;
        
    }
    
    /**
     * @desc extjs 
     * @param conditionMap
     * @return
     */
    public static Map<String, String> getFirstPageForExtjsGrid(Map<String, String> conditionMap)
    {
        // 페이징 처리를 위한 부분 추가.extjs 는 0부터 시작
        int temppage = Integer.parseInt(conditionMap.get("page"));

        temppage = temppage - 1;

        conditionMap.put("page", Integer.toString(temppage));
        
        return conditionMap;
    }
    
    public static Map<String, Object> getFirstPageForExtjsGrid2(Map<String, Object> conditionMap)
    {
        // 페이징 처리를 위한 부분 추가.extjs 는 0부터 시작
        int temppage = (java.lang.Integer) conditionMap.get("page");

        temppage = temppage - 1;

        conditionMap.put("page", Integer.toString(temppage));
        
        return conditionMap;
    }
    
    public static Map<String, Object> getFirstPageForExtjsGrid3(Map<String, Object> conditionMap)
    {
        // 페이징 처리를 위한 부분 추가.extjs 는 0부터 시작
        String temppage = (String) conditionMap.get("page");

        int intTempPage = Integer.parseInt(temppage) - 1;

        conditionMap.put("page", intTempPage);
        
        return conditionMap;
    }
    
    /*public static String makeDisplayCommState(String lastCommDate)
    {
        
        String commState="";
        
                
        if (lastCommDateMS >= oneDayMS)
        {
            commState = this.fmtMessage10;
        } else if (lastCommDateMS < oneDayMS && lastCommDateMS >= twoDayMS)
        {
            commState = this.fmtMessage11;
        } else if (lastCommDateMS < twoDayMS)
        {
            commState = this.fmtMessage12;
        }
        
    }
    */

    public static void main (String[] args)
    {
        //String testDate= "12.9.11";
        String testDate= "20121106125600";
        
        //20121105150025/
        String testDate2= "20121105130138";
    
        String today= CommonUtils2.getToday();
        String yest= CommonUtils2.getYesterday();
        String ago2day = CommonUtils2.getDayAgoYesterday();
        
        System.out.println(today);
        
        System.out.println(yest);
        
        System.out.println(ago2day);
        
        //1일전 시간을 계산
        Long tempDate= Long.parseLong(today) - Long.parseLong(testDate2) ;
        
        //2일전 시간을 계산
        Long tempDate2= Long.parseLong(today) - Long.parseLong(ago2day) ;
        
        if ( tempDate > 1000000 && tempDate < 2000000)
        {
            System.out.println("No activity over 24 hours");
        }
        else if ( tempDate > 2000000)
        {
            System.out.println("//No activity over 48 hours");
        }
        
        System.out.println(tempDate);
        
        System.out.println(tempDate2);
        
        
        
    }
    
    
    /**
     * @desc 
     * @param lastcommdate
     * @return
     */
    public static String getDisplayCommState(String  lastcommdate, String[] fmtMessage)
    {
        String displaycommstate="";
        
        String today = CommonUtils2.getToday();
        
        if ( lastcommdate== "" || lastcommdate.trim().equals("") )
        {
            displaycommstate= "";
        }
        else
        {
            //Long tempDate= Long.parseLong(lastcommdate) ;
            
            Long tempDate= Long.parseLong(today) - Long.parseLong(lastcommdate) ;
            
            
            if ( tempDate <= 1000000 )
            {
                System.out.println("24시간 이내");
                
                //displaycommstate= "normal";
                displaycommstate= fmtMessage[10];
                
            }
            else if ( tempDate > 1000000 && tempDate <= 2000000)
            {
                System.out.println("No activity over 24 hours");
                
                //displaycommstate= "No activity over 24 hours";
                displaycommstate= fmtMessage[11];
            }
            else if ( tempDate > 2000000)
            {
                System.out.println("//No activity over 48 hours");
                
                //displaycommstate= "No activity over 48 hours";
                displaycommstate= fmtMessage[12];
            }
        }
            
        
        
        return displaycommstate;
        
    }
    
    
    

    static int supplierId2 = 0;

    public static int getSupplierId(int supplierId)
    {

        Map<String, Object> conditionMap = new HashMap<String, Object>();

        if (conditionMap.get("supplierId") == ""
                || conditionMap.get("supplierId").toString().length() <= 0)
            supplierId2 = Integer.parseInt("22");// 누리텔레콤
        else
            supplierId2 = supplierId;

        return supplierId2;
    }

    /**
     * @desc db query형식으로 날짜 변환
     * 
     * @param strDate
     * @return
     */
    public static String changeDbFormatDate(String strDate)
    {

        String[] dates= strDate.split("\\. ");
        
        String year = dates[0];
        String month =dates[1];
        String day = dates[2];
        

        // alert( day.toString().length );
        if (day.length() == 1)
        {
            day = "0" + day.toString();
        }

        if (month.length() == 1)
        {
            month = "0" + month.toString();
        }

        String strFormattedDate = "20"+ year.toString() + month.toString() + day.toString();

        return strFormattedDate;

    }
    
    public static String changeDbFormatDate2(String strDate)
    {

        String[] dates= strDate.split("/");
        
        String day = dates[0];
        String month =dates[1];
        String year = dates[2];
        

    

        String strFormattedDate = "20"+ year.toString() + month.toString() + day.toString();

        return strFormattedDate;

    }
    
    
    /**
     * @desc Returns true if the string contains of "."
     * @param str1
     * @return
     */
    public static boolean containString(String str1, String matchString)
    {
        boolean result =false;
        
        
        try
        {
            
            
            if (str1.matches(".*"+matchString +".*"))
            {
                log.debug("매치되었습니다.%n");
                result= true;
            }
                
            else
            {
                log.debug("그런 문자열이 없습니다.%n");
                result= false;
            }
    
        } catch (PatternSyntaxException e)
        { // 정규식에 에러가 있다면
            log.error(e);
            // System.exit(1);
        }
        
        return result;
    }
    
    /**
     * @desc : datepicker ( View ) {01/01/12혹은 12.01.01형태} 에서 전달되온 날짜형태를 
     *         db date query 형태로 변환 (20121010 형태) 
     * @param date
     * @return
     */
    public static String convertToDbDate (String date)
    {
        boolean boolContainString1= containString(date, "\\.");
        boolean boolContainString2= containString(date, "/");
        
        
        
        String dbDate ="";
        
        //.을 포함하고 있는 날짜 형식이면
        if ( boolContainString1 == true )
        {
            dbDate= changeDbFormatDate(date);
        }
        // slash를 포함하고 있는 날짜 형식일 경우.
        else if ( boolContainString2 == true)
        {
            dbDate = changeDbFormatDate2(date);
        }
        
        return dbDate;
    }
    
    
    /**
     * @desc :현재 시간을 구한다 ( yyyyMMddHHmmss형태)
     * @return
     */
    public static String getCurrentTime()
    {
        Date now = new Date();

        // Print the result of toString()
        String dateString = now.toString();
        /*System.out.println(" 1. " + dateString);*/


        // short timezone, a final space, and a long year.
        SimpleDateFormat format = new SimpleDateFormat( "yyyyMMddHHmmss");

        

        // Print the result of formatting the now Date to see if the result
        // is the same as the output of toString()
        System.out.println();
        
        return  format.format(now);
    }
    
    
    /**
     * 
     * @return 현재 날짜 시간 
     */
    public static String getToday()
    {
         // (1) get today's date
        Date today = Calendar.getInstance().getTime();
        
        Calendar cal = Calendar.getInstance();
        
        cal.add(Calendar.HOUR, -24);
        
        Date yesterday= cal.getTime();
        
        cal.add(Calendar.HOUR, -24);
        
        Date dayAgoYesterday= cal.getTime();

        // (2) create our date "formatter" (the date format we want)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        // (3) create a new String using the date format we want
        String today2 = formatter.format(today);
        
        String yesterday2 = formatter.format(yesterday);
        
        String dayAgoYesterday2 = formatter.format(dayAgoYesterday);
        
        return today2;
        
        
    }
    
    
    /**
     * @desc :  
     * @return:24시간 전 날짜 date
     */
    public static String getYesterday()
    {
         // (1) get today's date
        Date today = Calendar.getInstance().getTime();
        
        Calendar cal = Calendar.getInstance();
        
        cal.add(Calendar.HOUR, -24);
        
        Date yesterday= cal.getTime();
        
        cal.add(Calendar.HOUR, -24);
        
        Date dayAgoYesterday= cal.getTime();

        // (2) create our date "formatter" (the date format we want)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        // (3) create a new String using the date format we want
        //String today2 = formatter.format(today);
        
        String yesterday2 = formatter.format(yesterday);
        
       // String dayAgoYesterday2 = formatter.format(dayAgoYesterday);
        
        return yesterday2;
    }
    
    
    /**
     * 
     * @return 48시간 전 날짜 
     */
    public static String getDayAgoYesterday()
    {
         // (1) get today's date
        Date today = Calendar.getInstance().getTime();
        
        Calendar cal = Calendar.getInstance();
        
        cal.add(Calendar.HOUR, -24);
        
        Date yesterday= cal.getTime();
        
        cal.add(Calendar.HOUR, -24);
        
        Date dayAgoYesterday= cal.getTime();

        // (2) create our date "formatter" (the date format we want)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        // (3) create a new String using the date format we want
        String today2 = formatter.format(today);
        
        String yesterday2 = formatter.format(yesterday);
        
        String dayAgoYesterday2 = formatter.format(dayAgoYesterday);
        
        return dayAgoYesterday2;
    }

}
