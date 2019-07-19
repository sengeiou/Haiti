package com.aimir.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeZoneDST
{
    private Locale locale = null;
    private boolean dstServerEnable = false;
    private boolean dstMeterEnable = false;
    private boolean timezoneEnable = false;
    private String defaultTimezoneId = null;
    
    /**
     * 
     * @param locale 공급자의 locale 정보
     * @param dstServerEnable 운영 서버의 DST 적용 여부
     * @param dstMeterEnable 계량기의 DST 적용 여부(몇몇 계량기는 자체적으로 DST 기능을 가지고 있다)
     * @param timezoneEnable 타임존 시간을 더할지 여부. 보통 시스템 시간은 타임존을 설정하기 때문에 대부분 false
     *        이 변수가 true 일 경우 현재 시간을 GMT+0 으로 간주하여 시간을 구할 때 '현재 시간+시간대' 를 한다.
     *     예) 한국의 경우 GMT+9 이므로 현재 시간+9 를 정확한 시간으로 간주하여 사용.
     * @param defaultTimezoneId 기본 타임존 ID
     */
    public TimeZoneDST(Locale locale, 
    					boolean dstServerEnable, 
    					boolean dstMeterEnable,
    					boolean timezoneEnable, 
    					String defaultTimezoneId){
    	
        this.locale = locale;
        this.dstServerEnable = dstServerEnable;
        this.dstMeterEnable = dstMeterEnable;
        this.timezoneEnable = timezoneEnable;
        this.defaultTimezoneId = defaultTimezoneId;
    }
    
    /**
     * Gets the date, modified in case of daylight savings.
     * If timezoneId parameter is null, it uses default time zone ID
     * 
     * 
     * @param yyyyMMddHHmmss
     * @param timezoneId 
     * @return 파라미터로 받은 시간에 DST 를 적용한 yyyyMMddHHmmss 형식의 문자열 반환.
     * 
     * @throws ParseException
     */
    public  String getYYYYMMDDHHMMSS(String yyyyMMddHHmmss, String timezoneId)
    throws ParseException
    {
        return getYYYYMMDDHHMMSS(yyyyMMddHHmmss, timezoneId, DateFormat.SHORT);
    }
    
    /**
     * apply +timezone and +dst
     * if style is -1, date format is yyyyMMddHHmmss
     * @param yyyyMMddHHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDDHHMMSS(String yyyyMMddHHmmss, String timezoneId, int style)
    throws ParseException
    {
        if (yyyyMMddHHmmss == null || "".equals(yyyyMMddHHmmss))
            return "";
        
        if (timezoneId == null || "".equals(timezoneId))
            timezoneId = defaultTimezoneId;
        
        TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        long time = sdf.parse(yyyyMMddHHmmss).getTime();
        
        if (timezoneEnable)
            time += timezone.getRawOffset();
        
        if (dstServerEnable) {
            // 00:00:00 before dst time
            // 01:00:00 after dst time - timezone.getDSTSavings()
            if (timezone.inDaylightTime(new Date(time)))
                time += timezone.getDSTSavings();
        }
        
        if (style != -1) {
            sdf = (SimpleDateFormat)DateFormat.getDateTimeInstance(style, DateFormat.MEDIUM, locale);
            // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        }
        return sdf.format(new Date(time));
    }

    /**
     * apply +timezone and +dst
     * if style is -1, date format is yyyyMMddHHmmss
     * @param yyyyMMddHHmmss
     * @param timezoneId
     * @param format
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDDHHMMSSFORMAT(String yyyyMMddHHmmss, String timezoneId,String format)
    throws ParseException
    {
        if (yyyyMMddHHmmss == null || "".equals(yyyyMMddHHmmss))
            return "";
        
        if (timezoneId == null || "".equals(timezoneId))
            timezoneId = defaultTimezoneId;
        
        TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        long time = sdf.parse(yyyyMMddHHmmss).getTime();
        
        if (timezoneEnable)
            time += timezone.getRawOffset();
        
        if (dstServerEnable) {
            // 00:00:00 before dst time
            // 01:00:00 after dst time - timezone.getDSTSavings()
            if (timezone.inDaylightTime(new Date(time)))
                time += timezone.getDSTSavings();
        }
        
        if (format != null) {
            sdf = new SimpleDateFormat(format);
            // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        }
        return sdf.format(new Date(time));
    }

    /**
     * apply +timezone and +dst
     * @param yyyyMMddHHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDDHHMMSSDB(String yyyyMMddHHmmss, String timezoneId)
    throws ParseException
    {
        return getYYYYMMDDHHMMSS(yyyyMMddHHmmss, timezoneId, -1);
    }

    /**
     * apply +timezone and +dst
     * @param yyyyMMddHHmm
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDDHHMM(String yyyyMMddHHmm, String timezoneId, int style)
    throws ParseException
    {
        if (yyyyMMddHHmm == null || "".equals(yyyyMMddHHmm))
            return "";
        
        if (timezoneId == null || "".equals(timezoneId))
            timezoneId = defaultTimezoneId;
        
        TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        long time = sdf.parse(yyyyMMddHHmm).getTime();
        
        if (timezoneEnable)
            time += timezone.getRawOffset();
        
        if (dstServerEnable) {
            // 00:00:00 before dst time
            // 01:00:00 after dst time - timezone.getDSTSavings()
            if (timezone.inDaylightTime(new Date(time)))
                time += timezone.getDSTSavings();
        }
        
        if (style != -1) {
            sdf = (SimpleDateFormat)DateFormat.getDateTimeInstance(style, DateFormat.MEDIUM, locale);
            // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        }
        return sdf.format(new Date(time));
    }

    /**
     * apply +timezone and +dst
     * @param yyyyMMddHHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDDHHMMDB(String yyyyMMddHHmm, String timezoneId)
    throws ParseException
    {
        return getYYYYMMDDHHMM(yyyyMMddHHmm, timezoneId, -1);
    }

    
    public  String getYYYY(String yyyyMMddHHmmss, String timezoneId) 
    throws ParseException
    {
        String date = getYYYYMMDDHHMMSS(yyyyMMddHHmmss, timezoneId, -1);
        
        return date.substring(0, 4);
    }
    
    /**
     * apply +timezone and +dst
     * @param yyyyMMddHHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getYYYYMM(String yyyyMMddHHmmss, String timezoneId, int style)
    throws ParseException
    {
        String date = getYYYYMMDDHHMMSS(yyyyMMddHHmmss, timezoneId, -1);
        
        if (style == -1) {
            return date.substring(0, 6);
        }
        else  {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
            long time = sdf.parse(date).getTime();
            
            sdf = (SimpleDateFormat)DateFormat.getDateInstance(style, locale);
            // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
            String pattern = sdf.toPattern();
            
            int yyyy_length = 4;
            int mm_length = 2;
            
            int yyyy_offset = pattern.indexOf("yyyy");
            if (yyyy_offset == -1) {
                yyyy_offset = pattern.indexOf("yy");
                yyyy_length = 2;
            }
            int mm_offset = pattern.indexOf("MM");
            if (mm_offset == -1) {
                mm_offset = pattern.indexOf(" M");
            }
            if (mm_offset == -1) {
                mm_offset = pattern.indexOf("M");
                mm_length = 1;
            }
            
            String delim = null;
            String aPattern = null;
            if (yyyy_offset < mm_offset) {
                delim = pattern.substring(mm_offset-1, mm_offset);
                aPattern = pattern.substring(yyyy_offset, yyyy_offset + yyyy_length);
                aPattern += delim;
                aPattern += pattern.substring(mm_offset, mm_offset + mm_length);
            }
            else if (yyyy_offset > mm_offset){
                delim = pattern.substring(yyyy_offset-1, yyyy_offset);
                aPattern = pattern.substring(mm_offset, mm_offset + mm_length);
                aPattern += delim;
                aPattern += pattern.substring(yyyy_offset, yyyy_offset + yyyy_length);
            }
            sdf.applyPattern(aPattern);
            return sdf.format(new Date(time));
        }
    }
    
    /**
     * default date format is DateFormat.SHORT
     * @param yyyyMMddHHmmss
     * @param timezoneId
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDD(String yyyyMMddHHmmss, String timezoneId)
    throws ParseException
    {
        return getYYYYMMDD(yyyyMMddHHmmss, timezoneId, DateFormat.SHORT);
    }
    
    /**
     * apply +timezone and +dst
     * @param yyyyMMddHHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDD(String yyyyMMddHHmmss, String timezoneId, int style)
    throws ParseException
    {
        String date = getYYYYMMDDHHMMSS(yyyyMMddHHmmss, timezoneId, -1);
        
        if (style == -1) {
            return date.substring(0, 8);
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
            long time = sdf.parse(date).getTime();
            
            sdf = (SimpleDateFormat)DateFormat.getDateInstance(style, locale);
            // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
            
            return sdf.format(new Date(time));
        }
    }

    /**
     * apply +timezone and +dst
     * @param yyyyMMdd
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDDDB(String yyyyMMdd, String timezoneId)
    throws ParseException
    {
        if (timezoneId == null || "".equals(timezoneId))
            timezoneId = defaultTimezoneId;
        
        TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        
        long time = sdf.parse(yyyyMMdd).getTime();
        
        if (timezoneEnable)
            time += timezone.getRawOffset();
        
        if (dstServerEnable) {
            if (timezone.inDaylightTime(new Date(time)))
                time += timezone.getDSTSavings();
        }
        
        return sdf.format(new Date(time));
    }
   
    /**
     * apply +timezone and +dst
     * @param HHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getHHMMSS(String HHmmss, String timezoneId, int style)
    throws ParseException
    {
        if (timezoneId == null || "".equals(timezoneId))
            timezoneId = defaultTimezoneId;
        
        TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        long time = sdf.parse(HHmmss).getTime();
        
        if (timezoneEnable)
            time += timezone.getRawOffset();
        
        if (dstServerEnable) {
            if (timezone.inDaylightTime(new Date(time)))
                time += timezone.getDSTSavings();
        }
        
        sdf = (SimpleDateFormat)DateFormat.getTimeInstance(style, locale);
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        return sdf.format(new Date(time));
    }
    
    /**
     * apply +timezone and +dst
     * @param HHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getHHMMSSDB(String HHmmss, String timezoneId)
    throws ParseException
    {
        if (timezoneId == null || "".equals(timezoneId))
            timezoneId = defaultTimezoneId;
        
        TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        long time = sdf.parse(HHmmss).getTime();
        
        if (timezoneEnable)
            time += timezone.getRawOffset();
        
        if (dstServerEnable) {
            if (timezone.inDaylightTime(new Date(time)))
                time += timezone.getDSTSavings();
        }
        
        return sdf.format(new Date(time));
    }

    /**
     * apply -dst
     * @param yyyyMMddHHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDDHHMMSS(String yyyyMMddHHmmss)
    throws ParseException
    {
        TimeZone timezone = TimeZone.getTimeZone(defaultTimezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        long time = sdf.parse(yyyyMMddHHmmss).getTime();
        
        if (dstServerEnable) {
            // 00:00:00 before dst time
            // 01:00:00 after dst time - timezone.getDSTSavings()
            if (timezone.inDaylightTime(new Date(time)))
                time -= timezone.getDSTSavings();
        }
        
        return sdf.format(new Date(time));
    }
    
    /**
     * apply -dst
     * @param yyyyMMddHHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getYYYYMMDDHHMM(String yyyyMMddHHmm)
    throws ParseException
    {
        return getYYYYMMDDHHMMSS(yyyyMMddHHmm+"00").substring(0,12);
    }

    /**
     * apply -dst. Check dstMeterEnable
     * @param yyyyMMdd
     * @param timezoneId
     * @param style
     * @return yyyyMMdd formatted date string
     * @throws ParseException
     */
    public  String getYYYYMMDD(String yyyyMMdd)
    throws ParseException
    {
        TimeZone timezone = TimeZone.getTimeZone(defaultTimezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        long time = sdf.parse(yyyyMMdd).getTime();
        
        if (dstMeterEnable) {
            if (timezone.inDaylightTime(new Date(time)))
                time -= timezone.getDSTSavings();
        }
        
        return sdf.format(new Date(time));
    }
    
    public  String getAddGMTDST(String timezoneId)
    {
        TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        long addTime = 0;
        if (timezoneEnable)
            addTime += timezone.getRawOffset();
        if (dstServerEnable) {
            if (timezone.inDaylightTime(new Date()))
                addTime += timezone.getDSTSavings();
        }
        long hour = addTime / (1000*60*60);
        long min = addTime  % (1000*60*60);
        DecimalFormat df = new DecimalFormat("00");

        if (addTime >= 0)
            return (new StringBuffer()).append("+").append(df.format(hour))
                    .append(df.format(min)).toString();
        else
            return (new StringBuffer()).append(df.format(hour))
                    .append(df.format(min)).toString();
    }

    /**
     * apply -dst
     * problem method.,.. yyyymmdd -> 19700101
     * don't used..
     * @param HHmmss
     * @param timezoneId
     * @param style
     * @return
     * @throws ParseException
     */
    public  String getHHMMSS(String HHmmss)
    throws ParseException
    {
        TimeZone timezone = TimeZone.getTimeZone(defaultTimezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        long time = sdf.parse(HHmmss).getTime();
        
        if (dstMeterEnable) {
            if (timezone.inDaylightTime(new Date(time)))
                time -= timezone.getDSTSavings();
        }
        
        return sdf.format(new Date(time));
    }
    
    /**
     * @return GMT 
     */
    public  long getServerTimeMillis() {
    	
    	Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    	
        //TimeZone timezone = TimeZone.getTimeZone(defaultTimezoneId);
        
        /*
        if (dstServerEnable) {
            if (timezone.inDaylightTime(new Date(time)))
                time -= timezone.getDSTSavings();
        }
        */
    }
    
    public  String[] applyDST(String yyyymmddhhmmss)
    throws ParseException 
    {
        String[] result = new String[2];
        TimeZone timezone = TimeZone.getTimeZone(defaultTimezoneId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        long time = sdf.parse(yyyymmddhhmmss).getTime();
        
        if (dstServerEnable) {
            if (timezone.inDaylightTime(new Date(time))) {
                time += timezone.getDSTSavings();
                result[0] = "true";
            }
            else {
                result[0] = "false";
            }
        }
        else {
            result[0] = "false";
        }
        
        sdf = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        // sdf.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));
        result[1] = sdf.format(new Date(time));
        return result;
    }
    
    /**
     * 현재 시간을 반환하는 함수. TimeZoneId 와 시간 형식을 인자로 받는다
     * @param timezoneId 현재 시간을 구할 타임 존
     * @param style 타임 포맷 스타일
     * @return [0] - 현재 시간
     *         [1] - DateFormat Pattern
     *         [2] - DST 적용 여부 true/false
     * @throws ParseException
     */
    public String[] getCurrentTime(String timezoneId, int style)
    throws ParseException
    {
        Calendar calendar = Calendar.getInstance();
        TimeZone timezone = TimeZone.getTimeZone(timezoneId);
        long time = calendar.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String[] times = new String[3];
        
        times[0] = getYYYYMMDDHHMMSS(sdf.format(time), timezoneId, style);
        
        if (style == -1)
            times[1] = sdf.toPattern();
        else {
            times[1] = ((SimpleDateFormat)DateFormat.getDateTimeInstance(style, DateFormat.MEDIUM, locale)).toPattern();
        }
        if (dstServerEnable && timezone.inDaylightTime(new Date(time)))
            times[2] = "true";
        else
            times[2] = "false";
        return times;
    }
    
    public static void main(String[] args) throws ParseException {
    	TimeZoneDST tz = new TimeZoneDST(new Locale("ko", "KR"), true, true, false, "Asia/Seoul");
    	String str[] = tz.getCurrentTime("Asia/Seoul", 2);
	}
    
    public  String getDatePattern(int style) {
        return ((SimpleDateFormat)DateFormat.getDateInstance(style, locale)).toPattern();
    }
}
