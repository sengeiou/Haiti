package com.aimir.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TimeZoneDao;
import com.aimir.model.system.Supplier;
/**
 * 국가별로 시간을 표현하는 방법에 차이가 있다. 예를 들어 한국에서는 2010.6.15 로 표현하지만, 미국에서는 15/6/2010 으로 표현한다.
 * TimeFormatUtil 클래스는 DB에 저장된 일정한 형식의 날짜를 UI에 맞게 변환하는 기능을 한다. 
 * 
 * 
 * @author yuky
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class TimeLocaleUtil {

    public static Log log = LogFactory.getLog(TimeLocaleUtil.class);
    
	static DecimalFormatSymbols dfs;
	static String numberFormat;
	
	static String df0_format;
	static String df1_format;
	static String df2_format;
	static String df3_format;
	static String df4_format;
    
	static DecimalFormat df0;
	static DecimalFormat df1;
	static DecimalFormat df2;
	static DecimalFormat df3;	
    
	static DecimalFormat standard_df0;
	static DecimalFormat standard_df1;
	static DecimalFormat standard_df2;
	static DecimalFormat standard_df3;
	
	static String groupingSeparator;
	static String decimalSeparator;
    

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    TimeZoneDao timezoneDao;

    static Supplier supplier;
    static TimeZone timezone;

    static Locale locale = null;

    /**
     * TimeFormatUtil의 생성자. 생성시 Locale 객체와 해당 로케일에서 사용하는 grouping separator,
     * decimal separator를 인자로 넘긴다. 대부분의 국가에서 groupingSeparator 로는 ',' decimal
     * separator 로는 '.' 을 사용하지만 이태리나 스페인 같은 일부 국가에서는 정 반대로 사용하기도 한다.
     * 
     * DecimalFormat 객체인 df0 ~ df3 도 생성자에서 생성해준다. 이 멤버 객체들은 실수 값에서 특정 길이의 소수점 이하
     * 자리수를 String 형식으로 포현할 때 사용된다. df1 은 소수점 이하 한자리, df3 은 소수점 이하 세자리로 변환할때
     * 사용된다.
     * 
     * @param locale
     * @param groupingSeparator
     *            자리수를 구분하는 구분자. 일반적으로 ',' 를 사용하지만 '.' 를 구분자로 사용하는 국가도 있다.
     * @param decimalSeparator
     *            정수와 소수를 구분하는 구분다. 일반적으로 '.' 를 사용하지만 ',' 를 사용하는 국가도 있다.
     */

    public TimeLocaleUtil(Locale locale, String groupingSeparator, String decimalSeparator) {
        this.locale = locale;
        this.dfs = new DecimalFormatSymbols(locale);
        this.dfs.setGroupingSeparator(groupingSeparator.charAt(0));
        this.dfs.setDecimalSeparator(decimalSeparator.charAt(0));
        this.numberFormat = "#" + dfs.getGroupingSeparator() + "###" + dfs.getDecimalSeparator() + "#";

        TimeLocaleUtil.groupingSeparator = groupingSeparator;
        this.decimalSeparator = decimalSeparator;

        df0_format = "###,###,###,###,###.########";
        df1_format = "###,###,###,###,##0.0";
        df2_format = "###,###,###,###,##0.00";
        df3_format = "###,###,###,###,##0.000";
        df4_format = "###,###,###,###,##0.0000";

        df0 = new DecimalFormat(df0_format, TimeLocaleUtil.dfs);
        df1 = new DecimalFormat(df1_format, TimeLocaleUtil.dfs);
        df2 = new DecimalFormat(df2_format, TimeLocaleUtil.dfs);
        df3 = new DecimalFormat(df3_format, TimeLocaleUtil.dfs);

        standard_df0 = new DecimalFormat(df0_format.replaceAll(",", ""));
        standard_df1 = new DecimalFormat(df1_format.replaceAll(",", ""));
        standard_df2 = new DecimalFormat(df2_format.replaceAll(",", ""));
        standard_df3 = new DecimalFormat(df3_format.replaceAll(",", ""));
    }

    /**
     * supplierId 를 넘기면 공급사 정보에서 locale 정보를 얻어오는 생성자.
     * 
     * @param supplierId
     */
    public TimeLocaleUtil(Integer supplierId) {
        this.supplier = supplierDao.get(supplierId);

        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        this.locale = new Locale(lang, country);
        this.dfs = new DecimalFormatSymbols(locale);
        this.numberFormat = "#" + dfs.getGroupingSeparator() + "###" + dfs.getDecimalSeparator() + "#";

        df0_format = "###,###,###,###,###.########";
        df1_format = "###,###,###,###,##0.0";
        df2_format = "###,###,###,###,##0.00";
        df3_format = "###,###,###,###,##0.000";
        df4_format = "###,###,###,###,##0.0000";

        df0 = new DecimalFormat(df0_format, this.dfs);
        df1 = new DecimalFormat(df1_format, TimeLocaleUtil.dfs);
        df2 = new DecimalFormat(df2_format, TimeLocaleUtil.dfs);
        df3 = new DecimalFormat(df3_format, TimeLocaleUtil.dfs);

        standard_df0 = new DecimalFormat(df0_format.replaceAll(",", ""));
        standard_df1 = new DecimalFormat(df1_format.replaceAll(",", ""));
        standard_df2 = new DecimalFormat(df2_format.replaceAll(",", ""));
        standard_df3 = new DecimalFormat(df3_format.replaceAll(",", ""));
    }

    /*if( AIMIRProperty.getProperty("Locale.GroupingSeparator")!=null &&
       AIMIRProperty.getProperty("Locale.GroupingSeparator").length()>0)
        dfs.setGroupingSeparator(AIMIRProperty.getProperty("Locale.GroupingSeparator").charAt(0));
    
    if( AIMIRProperty.getProperty("Locale.DecimalSeparator")!=null &&
       AIMIRProperty.getProperty("Locale.DecimalSeparator").length()>0)
        dfs.setDecimalSeparator(AIMIRProperty.getProperty("Locale.DecimalSeparator").charAt(0));

    String ` = "#" + dfs.getGroupingSeparator()
                        + "###" + dfs.getDecimalSeparator()
                        + "#";
     */

    /**
     * 
     * @param strFormat
     * @return
     */
    public static String getYearMonthFormat(String strFormat) {
        String yearMonthFormat = "";
        String etemp = "";
        String regex = ".*[^a-zA-Z0-9]";
        int epos = strFormat.lastIndexOf("d");

        if (epos + 1 < strFormat.length()) {
            etemp = strFormat.substring(epos + 1, epos + 2);
        }

        yearMonthFormat = strFormat.replaceAll("d", "");
        yearMonthFormat = yearMonthFormat.replaceAll("m", "M");

        if (etemp.length() > 0)
            yearMonthFormat = yearMonthFormat.replaceFirst(etemp, "");

        if (yearMonthFormat.matches(regex)) {
            yearMonthFormat = yearMonthFormat.substring(0, yearMonthFormat.length() - 1);
            if (yearMonthFormat.matches(regex)) {
                yearMonthFormat = yearMonthFormat.substring(0, yearMonthFormat.length() - 1);
            }
        }
        return yearMonthFormat;
    }

    /**
     * @return the supplier
     */
    public static Supplier getSupplier() {
        return supplier;
    }

    /**
     * @param supplier
     *            the supplier to set
     */
    public static void setSupplier(Supplier supplier) {
        TimeLocaleUtil.supplier = supplier;
    }

    /**
     * @return the timezone
     */
    public static TimeZone getTimezone() {
        return timezone;
    }

    /**
     * @param timezone
     *            the timezone to set
     */
    public static void setTimezone(TimeZone timezone) {
        TimeLocaleUtil.timezone = timezone;
    }

    /**
     * @return the locale
     */
    public static Locale getLocale() {
        return locale;
    }

    /**
     * @param locale
     *            the locale to set
     */
    public static void setLocale(Locale locale) {
        TimeLocaleUtil.locale = locale;
    }

    /**
     * 날짜 형식 패턴을 구하는 메소드. 첫번째 인자로 넘긴 길이(6, 8, 12, 14)에 따라 날짜 형식을 반환한다. 지정된 길이가
     * 아닐 경우 기본 패턴으로 yyyy-MM-dd 을 반환한다. 로케일이 한국일 경우 각 길이별 날짜 형식 패턴은 다음과 같다.
     * length 6 : a h:mm:ss length 8 : yy. m. d length 12 : yy. M. d a h:mm
     * length 14 : yy. M. d a h:mm:ss length 16 : yyyy. m. d
     * 
     * @param length
     *            날짜 형식 패턴의 길이
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 날짜 형식 패턴
     */
    // Detail View에서 사용하는 Locale 영역 - 부모
    public static String getDateFormat(int length, String lang, String country) {
        String sysDatePattern = "";
        
        if(supplier == null || supplier.getSysDatePattern() == null){
            sysDatePattern = "No Option";
        } else {
            sysDatePattern = supplier.getSysDatePattern();
        }
        
        Locale locale = new Locale(lang, country);
        SimpleDateFormat locdf6 = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, locale);
        SimpleDateFormat locdf16 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        if (!sysDatePattern.equals("No Option")) {
            Locale localeValue = locale;

            if (sysDatePattern.equals("DD.MM.YY HH:MM:SS")) { // DD.MM.YY HH:MM:SS
                localeValue = locale.GERMANY;
            }
            if (sysDatePattern.equals("DD/MM/YY HH:MM:SS")) { // DD/MM/YY HH:MM:SS 
                localeValue = locale.UK;
            }
            if (sysDatePattern.equals("DD/MM/YY HH.MM.SS")) { // DD/MM/YY HH.MM.SS 
                localeValue = locale.ITALY;
            }
            if (sysDatePattern.equals("DD/MM/YY HH:MM:SS AM/PM")) { // DD/MM/YY HH:MM:SS AM/PM
                localeValue = locale.CANADA;
            }
            if (sysDatePattern.equals("MM/DD/YY HH:MM:SS AM/PM")) { // MM/DD/YY HH:MM:SS AM/PM
                localeValue = locale.US;
            }
            if (sysDatePattern.equals("YY/MM/DD HH:MM:SS")) { // YY/MM/DD HH:MM:SS 
                localeValue = locale.JAPAN;
            }
            if (sysDatePattern.equals("YY-MM-DD HH:MM:SS")) { // YY-MM-DD HH:MM:SS
                localeValue = locale.CHINA;
            }

            locdf6 = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.MEDIUM, localeValue);
            locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, localeValue);
            locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, localeValue);
        }

        String pattern = "";

        switch (length) {
        case 6:
            pattern = locdf6.toPattern();
            break;
        case 8:
            pattern = locdf8.toPattern();
            break;
        case 12:
            pattern = locdf14.toPattern().substring(0, locdf14.toPattern().length() - 3);
            break;
        case 14:
            pattern = locdf14.toPattern();
            break;
        case 16:
            pattern = locdf16.toPattern();
            break;
        default:
            pattern = "yyyy-MM-dd";
            break;
        }
        return pattern;
    }

    public static String getDecimalFomrat(String type) {
        String rtnType = "";

        if (type.equals("group"))
            rtnType = groupingSeparator;
        else if (type.equals("decimal"))
            rtnType = decimalSeparator;

        return rtnType;

    }

    public static DecimalFormat getDecimalFormat(Supplier supplier) {
        Locale locale = new Locale(supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());

        if (locale == null) {
            locale = Locale.getDefault();
        }

        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        dfs.setGroupingSeparator(supplier.getMd().getGroupingSeperator().toCharArray()[0]);
        dfs.setDecimalSeparator(supplier.getMd().getDecimalSeperator().toCharArray()[0]);
        DecimalFormat df = new DecimalFormat(supplier.getMd().getPattern(), dfs);
        return df;
    }

    /**
     * 로케일에 맞는 형식의 연월(YearMonth) 날짜 형식을 반환
     * 
     * @param strDate
     *            스트링 형식의 날짜.
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 yyyyMM 패턴의 날짜
     */
    public static String getLocaleYearMonth(String strDate, String lang, String country) {
        Locale locale = new Locale(lang, country);
        String locDate = "";
        SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMM", locale);

        try {
            //SimpleDateFormat yearMonthFormat = new SimpleDateFormat(getYearMonthFormat(getDateFormat(8, lang, country)), new Locale(lang, country));;
            SimpleDateFormat yearMonthFormat = new SimpleDateFormat(getYearMonthFormat(getDateFormat(16, lang, country)), locale);
            if (!strDate.equals("000000")) {
                locDate = yearMonthFormat.format(sdf6.parse(strDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locDate;
    }

    /**
     * 로케일에 맞는 형식의 연월(YearMonth) 날짜 형식을 반환
     * 
     * @param strDate
     *            스트링 형식의 날짜.
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 yyyyMM 패턴의 날짜
     */
    public static String getLocaleYearMonthBy(String strDate, String lang, String country) {
        String locDate = "";
        SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMM");

        try {
            //SimpleDateFormat yearMonthFormat = new SimpleDateFormat(getYearMonthFormat(getDateFormat(8, lang, country)), new Locale(lang, country));;
            SimpleDateFormat yearMonthFormat = new SimpleDateFormat(getYearMonthFormat(getDateFormat(16, lang, country)), new Locale(lang, country));
            ;
            if (!strDate.equals("000000")) {
                locDate = yearMonthFormat.format(sdf6.parse(strDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locDate;
    }

    /**
     * YearMonth 형식의 날짜를 DB에 저장하는 형식으로 변환(YYYYMM). 로케일이 적용된 날짜를 이용하여 데이터베이스에
     * YYYYMM 형식으로 저장된 날짜를 조회하거나 DB에 저장시 사용
     * 
     * @param localeDate
     *            현재 로케일 형식의 날짜
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return YYYYMM 형식의 날짜 스트링
     */
    public String getDBYearMonth(String localeDate, String lang, String country) {
        SimpleDateFormat yearsdf = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthsdf = new SimpleDateFormat("MM");
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat(getYearMonthFormat(getDateFormat(8, lang, country)), new Locale(lang, country));
        ;

        Date date = null;
        String dbDate = "";

        try {
            date = yearMonthFormat.parse(localeDate);
            dbDate = yearsdf.format(date) + monthsdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbDate;
    }

    /**
     * 로케일에 맞는 형식의 연월일시(YearMonthDateHour) 날짜 형식을 반환 2012-11-20 추가 : 태국 locale
     * 에서 날짜오류가 발생하여 SimpleDateFormat 파라메터에 locale 추가
     * 
     * @param strDate
     *            스트링 형식의 날짜.
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 yyyyMMddHH 패턴의 날짜
     */
    public static String getLocaleDateHour(String strDate, String lang, String country) {
        Locale locale = new Locale(lang, country);
        String locDate = null;
        SimpleDateFormat sdf10 = new SimpleDateFormat("yyyyMMddHH", locale);

        try {
            String dateFormat = getDateFormat(14, lang, country);
            // TODO - 적용조건 : 시간 pattern 의 시,분,초 사이 delimiter 가 1byte 특수문자일 경우만 적용가능. 그 외 케이스가 있을 경우 수정요망.
            SimpleDateFormat dateHourFormat = new SimpleDateFormat(dateFormat.replaceAll(".mm.ss", ""), locale);

            if (!strDate.equals("0000000000")) {
                locDate = dateHourFormat.format(sdf10.parse(strDate));
            } else {
                locDate = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return locDate;
    }

    /**
     * 로케일에 맞는 형식의 연월일요일(YearMonthDateDay) 날짜 형식을 반환
     * 
     * @param strDate
     *            스트링 형식의 날짜. (yyyyMMdd)
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 yyyyMMddEEE 패턴의 날짜 (ex. 6/16/11 Thu)
     */
    public static String getLocaleWeekDay(String strDate, String lang, String country) {
        Locale locale = new Locale(lang, country);
        String locDate = null;
        SimpleDateFormat sdf8 = new SimpleDateFormat("yyyyMMdd", locale);

        try {
            StringBuilder pattern = new StringBuilder();
            pattern.append(getDateFormat(8, lang, country).replace('m', 'M')).append(" E");
            //
            SimpleDateFormat weekDayFormat = new SimpleDateFormat(pattern.toString(), locale);

            if (!strDate.equals("00000000")) {
                locDate = weekDayFormat.format(sdf8.parse(strDate));
            } else {
                locDate = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locDate;
    }

    /**
     * 로케일에 맞는 형식의 요일(Day) 날짜 형식을 반환
     * 
     * @param strDate
     *            스트링 형식의 날짜. (yyyyMMdd)
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 EEE 패턴의 날짜 (ex. Mon)
     */
    public static String getLocaleWeekDayOnly(String strDate, String lang, String country) {
        String weekDay = null;
        Locale locale = new Locale(lang, country);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", locale);
        SimpleDateFormat rsdf = new SimpleDateFormat("E", locale);

        try {
            if (!strDate.equals("00000000")) {
                weekDay = rsdf.format(sdf.parse(strDate));
            } else {
                weekDay = "";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weekDay;
    }

    /**
     * 로케일에 맞는 형식의 전체 요일(Day) 리스트를 반환
     * 
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 EEE 패턴의 전체요일 리스트 (ex. [Sun, ..., Sat])
     */
    public static List<String> getLocaleWeekDayList(String lang, String country) {
        Locale loc = new Locale(lang, country);
        Calendar cal = Calendar.getInstance();
        List<String> dayList = new ArrayList<String>();

        for (int i = 0; i < 7; i++) {
            cal.set(Calendar.DAY_OF_WEEK, (i + 1));
            dayList.add(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, loc));
        }
        return dayList;
    }

    /**
     * 로케일에 맞는 형식의 시(Hour) 시간 타입을 반환
     * 
     * @param strHour
     *            HH 포멧의 시각.
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 HH 패턴의 시간
     */
    public static String getLocaleHour(String strHour, String lang, String country) {
        Locale locale = new Locale(lang, country);
        String locHour = null;
        SimpleDateFormat sdf02 = new SimpleDateFormat("HH", locale);

        try {
            String timePattern = getDateFormat(6, lang, country);
            // TODO - 적용조건 : 시간 pattern 의 시,분,초 사이 delimiter 가 1byte 특수문자일 경우만 적용가능. 그 외 케이스가 있을 경우 수정요망.
            SimpleDateFormat hourFormat = new SimpleDateFormat(timePattern.replaceAll(".mm.ss", ""), locale);
            locHour = hourFormat.format(sdf02.parse(strHour));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locHour;
    }

    /**
     * 로케일에 맞는 형식의 시(Hour)분(Minute) 시간 타입을 반환
     * 
     * @param strTime
     *            HHMM 포멧의 시각.
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 HHMM 패턴의 시간
     */
    public static String getLocaleHourMinute(String strTime, String lang, String country) {
        Locale locale = new Locale(lang, country);
        String locHour = null;
        SimpleDateFormat sdf02 = new SimpleDateFormat("HHMM", locale);

        try {
            String timePattern = getDateFormat(6, lang, country);
            // TODO - 적용조건 : 시간 pattern 의 시,분,초 사이 delimiter 가 1byte 특수문자일 경우만 적용가능. 그 외 케이스가 있을 경우 수정요망.
            SimpleDateFormat hourFormat = new SimpleDateFormat(timePattern.replaceAll(".ss", ""), locale);
            locHour = hourFormat.format(sdf02.parse(strTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locHour;
    }
    
    /**
     * return time-string(hours, minutes, seconds) which is matching with locale
     * @param strTime YYYYMMDDhhmmss  or hhmmss
     * @param lang
     * @param country
     * @return
     */
    public static String getLocaleTime(String strTime, String lang, String country) {
        Locale locale = new Locale(lang, country);
        String locTime = null;
        SimpleDateFormat sdf02 = null;
        if ( strTime.length() == 14 ) 
            sdf02 = new SimpleDateFormat("yyyyMMddHHmmss", locale);
        else
            sdf02 = new SimpleDateFormat("HHmmss", locale);
       
        try {
            String timePattern = getDateFormat(6, lang, country);
            // TODO - 적용조건 : 시간 pattern 의 시,분,초 사이 delimiter 가 1byte 특수문자일 경우만 적용가능. 그 외 케이스가 있을 경우 수정요망.
            SimpleDateFormat hourFormat = new SimpleDateFormat(timePattern, locale);
            locTime = hourFormat.format(sdf02.parse(strTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locTime;
    }
    /**
     * 로케일이 적용된 날짜를 전달받아 연도(Year)만 반환한다.
     * 
     * @param localeDate
     *            UI에서 사용하는 로케일이 적용된 날짜 형식
     * @param length
     *            localeDate 의 길이(8, 12, 14, default). 각 길이별 날짜 형식 예제(한국의 경우)
     *            length 8 : 2010. 06. 15 length 12 : 2010. 06. 15 오전 12:30
     *            length 14 : 2010. 06. 15 오전 12:30:05
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return YYYY 형식의 스트링. length 인자의 값이 8, 12, 14가 아닐경우 "" 가 반환된다
     */
    public static String getLocaleYear(String localeDate, int length, String lang, String country) {
        Locale locale = new Locale(lang, country);
        SimpleDateFormat yearsdf = new SimpleDateFormat("yyyy", locale);

        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, locale);

        Date date = null;
        try {
            switch (length) {
            case 8:
                date = locdf8.parse(localeDate);
                localeDate = yearsdf.format(date);
                break;
            case 12:
                date = locdf14.parse(localeDate + ":00");
                localeDate = yearsdf.format(date);
                break;
            case 14:
                date = locdf14.parse(localeDate);
                localeDate = yearsdf.format(date);
                break;
            default:
                localeDate = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localeDate;
    }

    /**
     * 로케일이 적용된 날짜를 전달받아 월(Month) 만 반환
     * 
     * @param localeDate
     *            로케일이 적용된 날짜 스트링
     * @param length
     *            locDate 의 길이(8, 12, 14, default). 각 길이별 날짜 형식 예제(한국의 경우)
     *            length 8 : 2010. 06. 15 length 12 : 2010. 06. 15 오전 12:30
     *            length 14 : 2010. 06. 15 오전 12:30:05
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return localeDate 에서 월(Month)에 해당하는 부분만 반환. length의 길이가 8,12,14가 아닐 경우
     *         ""를 반환
     */
    public static String getLocaleMonth(String localeDate, int length, String lang, String country) {
        Locale locale = new Locale(lang, country);
        SimpleDateFormat monthsdf = new SimpleDateFormat("MM", locale);

        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, locale);

        Date date = null;
        try {
            switch (length) {
            case 8:
                date = locdf8.parse(localeDate);
                localeDate = monthsdf.format(date);
                break;
            case 12:
                date = locdf14.parse(localeDate + ":00");
                localeDate = monthsdf.format(date);
                break;
            case 14:
                date = locdf14.parse(localeDate);
                localeDate = monthsdf.format(date);
                break;
            default:
                localeDate = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localeDate;
    }

    /**
     * 로케일이 적용된 날짜를 전달받아 일(Day)만 반환
     * 
     * @param localeDate
     *            로케일이 적용된 날짜 스트링
     * @param length
     *            localeDate 의 길이(8, 12, 14, default). 각 길이별 날짜 형식 예제(한국의 경우)
     *            length 8 : 2010. 06. 15 length 12 : 2010. 06. 15 오전 12:30
     *            length 14 : 2010. 06. 15 오전 12:30:05
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return localeDate 에서 일(Day)에 해당하는 부분만 반환. length의 길이가 8,12,14가 아닐 경우 ""를
     *         반환
     */
    public static String getLocaleDay(String localeDate, int length, String lang, String country) {
        Locale locale = new Locale(lang, country);
        SimpleDateFormat daysdf = new SimpleDateFormat("dd", locale);

        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, locale);

        Date date = null;
        try {
            switch (length) {
            case 8:
                date = locdf8.parse(localeDate);
                localeDate = daysdf.format(date);
                break;
            case 12:
                date = locdf14.parse(localeDate + ":00");
                localeDate = daysdf.format(date);
                break;
            case 14:
                date = locdf14.parse(localeDate);
                localeDate = daysdf.format(date);
                break;
            default:
                localeDate = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localeDate;
    }

    /**
     * Calendar 날짜를 읽어와 Locale형식에 맞게 날짜포맷을 변경하여 리턴한다. 날짜 입력 포멧은 yyyyMMdd이다.
     * 
     * @param supplier
     * @param cal
     * @return Locale형식에 따른 날짜 포멧으로 변형된 날짜
     */
    public static String getLocaleDate(Supplier supplier, Calendar cal) {
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        Locale locale = new Locale(lang, country);
        Date date = cal.getTime();

        SimpleDateFormat smp = new SimpleDateFormat("yyyyMMdd", locale);

        return getLocaleDate(smp.format(date), lang, country);
    }

    /**
     * 현재 로케일의 날짜를 반환. 파라미터의 길이에 따라 반환 되는 날짜 형식도 다르다. 다음은 로케일이 한국일 경우 파라미터 형식 별
     * 결과에 대한 예제이다.
     * 
     * 파라미터 - 결과 201006 - 오후 8:10:06 ( hh:mm:ss ) 20100615 - 10. 6. 15
     * 201006151525 - 10. 6. 15 오후 3:25:00 20100615152530 - 10. 6. 15 오후 3:25:30
     * 
     * @param strDate
     *            DB에 저장된 날짜 형식의 파라미터. 상단의 설명 참조
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 SimDateFormat 형식의 날짜 스트링
     */
    public static String getLocaleDate(String strDate) {
        if (strDate == null || "".equals(strDate))
            return "";
        else
            strDate = strDate.trim();

        Locale locale = Locale.getDefault();

        SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMM", locale);
        SimpleDateFormat sdf8 = new SimpleDateFormat("yyyyMMdd", locale);
        SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss", locale);

        //SimpleDateFormat locdf6 = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, locale);

        //SimpleDateFormat locdf6 = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        // 6자리인 YYYYMM의 경우 날짜 표현 방법으로 변경 필요
        SimpleDateFormat locdf6 = new SimpleDateFormat("yyyy. MM.", locale);
        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, locale);
        String localeDate = "";

        try {
            switch (strDate.length()) {
            case 6:
                if (!strDate.equals("000000"))
                    localeDate = locdf6.format(sdf6.parse(strDate));
                break;
            case 8:
                if (!strDate.equals("00000000"))
                    localeDate = locdf8.format(sdf8.parse(strDate));
                break;
            case 10:
                if (!strDate.equals("0000000000")) {
                    strDate = strDate + "0000";
                    if (!strDate.equals("00000000000000"))
                        localeDate = locdf14.format(sdf14.parse(strDate));
                }
                break;
            case 12:
                if (!strDate.equals("000000000000")) {
                    strDate = strDate + "00";
                    if (!strDate.equals("00000000000000"))
                        localeDate = locdf14.format(sdf14.parse(strDate));
                }
                break;
            case 14:
                if (!strDate.equals("00000000000000"))
                    localeDate = locdf14.format(sdf14.parse(strDate));
                break;
            default:
                localeDate = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localeDate;
    }

    /**
     * 현재 로케일의 날짜를 반환. 파라미터의 길이에 따라 반환 되는 날짜 형식도 다르다. 다음은 로케일이 한국일 경우 파라미터 형식 별
     * 결과에 대한 예제이다.
     * 
     * 파라미터 - 결과 201006 - 오후 8:10:06 ( hh:mm:ss ) 20100615 - 10. 6. 15
     * 201006151525 - 10. 6. 15 오후 3:25:00 20100615152530 - 10. 6. 15 오후 3:25:30
     * 
     * @param strDate
     *            DB에 저장된 날짜 형식의 파라미터. 상단의 설명 참조
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 SimDateFormat 형식의 날짜 스트링
     */
    
    // 공통적으로 사용하는  Locale 영역
    public static String getLocaleDate(String strDate, String lang, String country) {
        String sysDatePattern = "";
        
        if (strDate == null || "".equals(strDate))
            return "";
        else
            strDate = strDate.trim();
        
        if(supplier == null || supplier.getSysDatePattern() == null){
            sysDatePattern = "No Option";
        } else {
            sysDatePattern = supplier.getSysDatePattern();
        }
        
        Locale locale = new Locale(lang, country);
        SimpleDateFormat sdf6 = new SimpleDateFormat("HHmmss", locale);
        SimpleDateFormat sdf8 = new SimpleDateFormat("yyyyMMdd", locale);
        SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss", locale);

        SimpleDateFormat locdf6 = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, locale);

        if (!sysDatePattern.equals("No Option")) {
            Locale localeValue = locale;

            if (sysDatePattern.equals("DD.MM.YY HH:MM:SS")) { // DD.MM.YY HH:MM:SS
                localeValue = locale.GERMANY;
            }else if (sysDatePattern.equals("DD/MM/YY HH:MM:SS")) { // DD/MM/YY HH:MM:SS 
                localeValue = locale.UK;
            }else if (sysDatePattern.equals("DD/MM/YY HH.MM.SS")) { // DD/MM/YY HH.MM.SS 
                localeValue = locale.ITALY;
            }else if (sysDatePattern.equals("DD/MM/YY HH:MM:SS AM/PM")) { // DD/MM/YY HH:MM:SS AM/PM
                localeValue = locale.CANADA;
            }else if (sysDatePattern.equals("MM/DD/YY HH:MM:SS AM/PM")) { // MM/DD/YY HH:MM:SS AM/PM
                localeValue = locale.US;
            }else if (sysDatePattern.equals("YY/MM/DD HH:MM:SS")) { // YY/MM/DD HH:MM:SS 
                localeValue = locale.JAPAN;
            }else if (sysDatePattern.equals("YY-MM-DD HH:MM:SS")) { // YY-MM-DD HH:MM:SS
                localeValue = locale.CHINA;
            }

            locdf6 = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.MEDIUM, localeValue);
            locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, localeValue);
            locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, localeValue);
        }

        String localeDate = "";

        try {
            switch (strDate.length()) {
            case 6:
                if (!strDate.equals("000000"))
                    localeDate = locdf6.format(sdf6.parse(strDate));
                break;
            case 8:
                if (!strDate.equals("00000000"))
                    localeDate = locdf8.format(sdf8.parse(strDate));
                break;
            case 10:
                if (!strDate.equals("0000000000")) {
                    strDate = strDate + "0000";
                    if (!strDate.equals("00000000000000"))
                        localeDate = locdf14.format(sdf14.parse(strDate));
                }
                break;
            case 12:
                if (!strDate.equals("000000000000")) {
                    strDate = strDate + "00";
                    if (!strDate.equals("00000000000000"))
                        localeDate = locdf14.format(sdf14.parse(strDate));
                }
                break;
            case 14:
                if (!strDate.equals("00000000000000"))
                    localeDate = locdf14.format(sdf14.parse(strDate));
                break;
            default:
                localeDate = "";
            }
        } catch (Exception e) { 
            log.error("Get Local Date Error - " + e);
        }

        return localeDate;
    }

    /**
     * 밀리세컨드 형식의 시간을 파라미터로 전달 받아 로케일이 적용된 날짜 형태로 반환 length 길이에 따라 다른 형태의 날짜 패턴을
     * 반환한다. 다음은 로케일이 한글/한국일때의 예제이다. longDate 의 값이 1277362822639 일 경우 length 6 -
     * 오후 4:00:22 length 8 - 10. 6. 24 length 14 - 10. 6. 24 오후 4:00:22
     * 
     * @param longDate
     *            1970년 1월 1일, GMT 부터의 시간을 밀리세컨드 단위로 표현하는 표준 날짜 형식
     * @param length
     *            리턴할 날짜 형태의 길이. 6, 8, 14 를 사용할 수 있다. 상단의 예제 참조
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 날짜 스트링. length 에 잘못된 인자가 넘어올 경우 "" 를 리턴
     */
    public static String getLocaleDate(long longDate, int length, String lang, String country) {
        Locale locale = new Locale(lang, country);

        SimpleDateFormat locdf6 = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, locale);
        String localeDate = "";

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(longDate));

        switch (length) {
        case 6:
            localeDate = locdf6.format(cal.getTime());
            break;
        case 8:
            localeDate = locdf8.format(cal.getTime());
            break;
        case 14:
            localeDate = locdf14.format(cal.getTime());
            break;
        default:
            localeDate = "";
            break;
        }

        if (longDate < 1) {
            localeDate = "";
        }
        return localeDate;
    }

    /**
     * java.util.Date 객체 형식의 날짜를 전달 받아 이를 로케일에 맞게 반환 length 파라미터는 반환할 날짜의 형식을
     * 지정한다. 로케일이 한글/한국일 경우 '2010. 6. 24 오후 5:20:03' 라는 값을 가진 Date 객체의 대한 length
     * 별 반환값은 다음과 같다.
     * 
     * length 6 - 오후 5:20:03 length 8 - 10. 6. 24 length 14 - 10. 6. 24 오후
     * 5:20:03
     * 
     * @param calDate
     *            java.util.Date 객체 형식의 날짜
     * @param length
     *            반환할 날짜 형식 지정. 인자는 6, 8, 14 중 지정. 다른 값이 인자로 넘어올 경우 "" 반환
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return
     * 
     * @see java.util.Date
     */
    public static String getLocaleDate(Date calDate, int length, String lang, String country) {
        Locale locale = new Locale(lang, country);

        SimpleDateFormat locdf6 = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, locale);
        String localeDate = "";

        switch (length) {
        case 6:
            localeDate = locdf6.format(calDate);
            break;
        case 8:
            localeDate = locdf8.format(calDate);
            break;
        case 14:
            localeDate = locdf14.format(calDate);
            break;
        default:
            localeDate = "";
            break;
        }
        return localeDate;
    }

    /**
     * java.util.Date 객체 형식의 날짜를 전달 받아 이를 로케일에 맞게 반환. 연-월-일 형식의 날짜를 반환한다.
     * 
     * @param calDate
     *            변환하려는 날짜가 담긴 Date 객체
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 연-월-일 형식의 날짜 패턴 형식
     */
    public String getLocaleDate(Date calDate, String lang, String country) {
        DateFormat locdf8 = DateFormat.getDateInstance(DateFormat.SHORT, new Locale(lang, country));
        return locdf8.format(calDate);
    }

    /**
     * 로케일이 적용된 날짜 형식을 전달 받아 DB에 저장되는 날짜 형식으로 변환. 다음은 각 length 별 변환된 날짜의 예제이다.
     * length가 6 - hhmmss 형식으로 시분초에 대한 값을 반환한다. 이때 localeDate 파라미터도 시간값을 넘겨야 한다.
     * localDate가 '오후 5:31:43'일 경우 반환되는 값은 '173143' 이다.
     * 
     * localeDate 값이 '10. 6. 24' 일 경우 length가 8 - 20100624
     * 
     * localeDate 값이 '10. 6. 24 오후 5:31:43' 일 경우 length가 12 - 201006241731
     * length가 14 - 20100624173143
     * 
     * - 2012-11-20 추가 : 태국 locale 에서 날짜오류가 생겨서 SimpleDateFormat 에 locale 파라메터
     * 추가
     * 
     * @param localeDate
     *            로케일에 따른 패턴이 적용된 날짜
     * @param length
     *            반환할 날짜 형식 지정. 6, 8, 12, 14 가능
     * @param lang
     * @param country
     * @return DB 저장 형식으로 변환된 날짜 값. length 가 6,8,12,14 가 아닐 경우 "" 리턴
     */
    // Detail View에서 사용하는 Locale 영역 - 자식
    public static String getDBDate(String localeDate, int length, String lang, String country) {
        String sysDatePattern = "";
        
        if(supplier == null || supplier.getSysDatePattern() == null){
            sysDatePattern = "No Option";
        } else {
            sysDatePattern = supplier.getSysDatePattern();
        }
        
        Locale locale = new Locale(lang, country);
        SimpleDateFormat yearsdf = new SimpleDateFormat("yyyy", locale);
        SimpleDateFormat monthsdf = new SimpleDateFormat("MM", locale);
        SimpleDateFormat daysdf = new SimpleDateFormat("dd", locale);
        SimpleDateFormat hoursdf = new SimpleDateFormat("HH", locale);
        SimpleDateFormat minutesdf = new SimpleDateFormat("mm", locale);
        SimpleDateFormat secondsdf = new SimpleDateFormat("ss", locale);

        SimpleDateFormat locdf6 = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        SimpleDateFormat locdf16 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        if (!sysDatePattern.equals("No Option")) {
            Locale localeValue = locale;

            if (sysDatePattern.equals("DD.MM.YY HH:MM:SS")) { // DD.MM.YY HH:MM:SS
                localeValue = locale.GERMANY;
            }
            if (sysDatePattern.equals("DD/MM/YY HH:MM:SS")) { // DD/MM/YY HH:MM:SS 
                localeValue = locale.UK;
            }
            if (sysDatePattern.equals("DD/MM/YY HH.MM.SS")) { // DD/MM/YY HH.MM.SS 
                localeValue = locale.ITALY;
            }
            if (sysDatePattern.equals("DD/MM/YY HH:MM:SS AM/PM")) { // DD/MM/YY HH:MM:SS AM/PM
                localeValue = locale.CANADA;
            }
            if (sysDatePattern.equals("MM/DD/YY HH:MM:SS AM/PM")) { // MM/DD/YY HH:MM:SS AM/PM
                localeValue = locale.US;
            }
            if (sysDatePattern.equals("YY/MM/DD HH:MM:SS")) { // YY/MM/DD HH:MM:SS 
                localeValue = locale.JAPAN;
            }
            if (sysDatePattern.equals("YY-MM-DD HH:MM:SS")) { // YY-MM-DD HH:MM:SS
                localeValue = locale.CHINA;
            }

            yearsdf = new SimpleDateFormat("yyyy", localeValue);
            monthsdf = new SimpleDateFormat("MM", localeValue);
            daysdf = new SimpleDateFormat("dd", localeValue);
            hoursdf = new SimpleDateFormat("HH", localeValue);
            minutesdf = new SimpleDateFormat("mm", localeValue);
            secondsdf = new SimpleDateFormat("ss", localeValue);

            locdf6 = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.MEDIUM, localeValue);
            locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, localeValue);
            locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, localeValue);
        }

        Date date = null;
        String dbDate = "";
        try {
            switch (length) {
            case 6:
                date = locdf6.parse(localeDate);
                dbDate = hoursdf.format(date) + minutesdf.format(date) + secondsdf.format(date);
                break;
            case 8:
                date = locdf8.parse(localeDate);
                dbDate = yearsdf.format(date) + monthsdf.format(date) + daysdf.format(date);
                break;
            case 12:
                date = locdf14.parse(localeDate + ":00");
                dbDate = yearsdf.format(date) + monthsdf.format(date) + daysdf.format(date) + hoursdf.format(date) + minutesdf.format(date);
                break;
            case 14:
                date = locdf14.parse(localeDate);
                dbDate = yearsdf.format(date) + monthsdf.format(date) + daysdf.format(date) + hoursdf.format(date) + minutesdf.format(date) + secondsdf.format(date);
                break;
            case 16:
                date = locdf16.parse(localeDate);
                dbDate = yearsdf.format(date) + monthsdf.format(date) + daysdf.format(date);
                break;
            default:
                dbDate = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbDate;
    }

    /**
     * double 형 실수의 DeciamlFormat을 변환해 주는 메소드. 숫자값을 표현할 때 소수점 자리수와
     * groupingSeparator, decimalSeparator를 고려하여 변경한다.
     * 
     * pointLength - 소수점 이하 자리수 df - 사용할 DecimalFormat. 소수점 이하 자리 0~3일 경우
     * TimeUtilFormat의 멤버 변수 df0 ~ df3 를 사용하면 된다.
     * 
     * 만약 로케일이 한국이고 12.3456 double형 값을 소수점 이하 첫째짜리~셋째자리로 변환하고 싶다면 다음과 같이 사용한다.
     * 
     * TimeFormatUtil tfu = new TimeFormatUtil(new Locale("ko"), ",", ".");
     * double num = 12.3456;
     * 
     * String result1 = tfu.dfformat(1, tfu.df1, num); // 결과 : 12.3 String
     * result2 = tfu.dfformat(2, tfu.df2, num); // 결과 : 12.34 String result3 =
     * tfu.dfformat(3, tfu.df3, num); // 결과 : 12.345
     * 
     * @param pointLength
     *            변환할 소수점 이하 자리수.
     * @param df
     *            변환에 사용할 DecimalFormat. 멤버 변수 df0 ~ df3 중 자리수에 맞게 사용
     * @param a
     *            변환할 값
     * @return 소수점 이하 pointLength 길이 만큼의 변환된 값. pointLength 보다 자리수가 많을 경우
     *         pointLength+1 자리에서 반올림한다.
     */
    public String dfformat(int pointLength, DecimalFormat df, double a) {
        switch (pointLength) {
        case 3:
            return df.format(Math.round(a * 1000) / 1000d);
        case 2:
            return df.format(Math.round(a * 100) / 100d);
        case 1:
            return df.format(Math.round(a * 10) / 10d);
        case 0:
            return df.format(Math.round(a));
        default:
            return df.format(a);
        }
    }

    /**
     * Double 형 실수의 DeciamlFormat을 변환해 주는 메소드. 숫자값을 표현할 때 소수점 자리수와
     * groupingSeparator, decimalSeparator를 고려하여 변경한다.
     * 
     * pointLength - 표현할 소수점 이하 자리수 df - 사용할 DecimalFormat. 소수점 이하 자리 0~3일 경우
     * TimeUtilFormat의 멤버 변수 df0 ~ df3 를 사용하면 된다.
     * 
     * 만약 로케일이 한국이고 12.3456 Double형 값을 소수점 이하 첫째짜리~셋째자리로 변환하고 싶다면
     * 
     * TimeFormatUtil tfu = new TimeFormatUtil(new Locale("ko"), ",", ".");
     * Double num = new Double(12.3456);
     * 
     * String result1 = tfu.dfformat(1, tfu.df1, num); // 결과 : 12.3 String
     * result2 = tfu.dfformat(2, tfu.df2, num); // 결과 : 12.34 String result3 =
     * tfu.dfformat(3, tfu.df3, num); // 결과 : 12.345
     * 
     * @param pointLength
     *            변환할 소수점 이하 자리수.
     * @param df
     *            변환에 사용할 DecimalFormat. 멤버 변수 df0 ~ df3 중 자리수에 맞게 사용
     * @param a
     *            변환할 값
     * @return 소수점 이하 pointLength 길이 만큼의 변환된 값. pointLength 보다 자리수가 많을 경우
     *         pointLength+1 자리에서 반올림한다.
     */
    public String dfformat(int pointLength, DecimalFormat df, Double a) {
        switch (pointLength) {
        case 3:
            return df.format(Math.round(a.doubleValue() * 1000) / 1000d);
        case 2:
            return df.format(Math.round(a.doubleValue() * 100) / 100d);
        case 1:
            return df.format(Math.round(a.doubleValue() * 10) / 10d);
        case 0:
            return df.format(Math.round(a.doubleValue()));
        default:
            return df.format(a.doubleValue());
        }
    }

    /**
     * Long형 실수의 DeciamlFormat을 변환해 주는 메소드. 숫자값을 표현할 때 소수점 자리수와
     * groupingSeparator, decimalSeparator를 고려하여 변환한다.
     * 
     * pointLength - 소수점 이하 자리수 df - 사용할 DecimalFormat. 소수점 이하 자리 0~3일 경우
     * TimeUtilFormat의 멤버 변수 df0 ~ df3 를 사용하면 된다.
     * 
     * 만약 로케일이 한국이고 12.3456 Long형 값을 소수점 이하 첫째짜리~셋째자리로 변환하고 싶다면
     * 
     * TimeFormatUtil tfu = new TimeFormatUtil(new Locale("ko"), ",", "."); Long
     * num = new Double(12.3456);
     * 
     * String result1 = tfu.dfformat(1, tfu.df1, num.longValue()); // 결과 : 12.3
     * String result2 = tfu.dfformat(2, tfu.df2, num.longValue()); // 결과 : 12.34
     * String result3 = tfu.dfformat(3, tfu.df3, num.longValue()); // 결과 :
     * 12.345
     * 
     * @param pointLength
     *            변환할 소수점 이하 자리수.
     * @param df
     *            변환에 사용할 DecimalFormat. 멤버 변수 df0 ~ df3 중 자리수에 맞게 사용
     * @param a
     *            변환할 Long 형 값
     * @return 소수점 이하 pointLength 길이 만큼의 변환된 값. pointLength 보다 자리수가 많을 경우
     *         pointLength+1 자리에서 반올림한다.
     */
    public String dfformat(int pointLength, DecimalFormat df, Long a) {
        switch (pointLength) {
        case 3:
            return df.format(Math.round(a.doubleValue() * 1000) / 1000d);
        case 2:
            return df.format(Math.round(a.doubleValue() * 100) / 100d);
        case 1:
            return df.format(Math.round(a.doubleValue() * 10) / 10d);
        case 0:
            return df.format(Math.round(a.doubleValue()));
        default:
            return df.format(a.doubleValue());
        }
    }

    /**
     * 인자로 넘어온 Double형 값보다 작거나 같은 가장 가까운 정수로 pointLength 길이 만큼 변환한다.
     * groupingSeparator, decimalSeparator는 생성자에 사용한 인자를 사용한다.
     * 
     * pointLength - 소수점 이하 자리수 df - 사용할 DecimalFormat. 소수점 이하 자리 0~3일 경우
     * TimeUtilFormat의 멤버 변수 df0 ~ df3 를 사용하면 된다.
     * 
     * 만약 로케일이 한국이고 12.3456 Long형 값을 소수점 이하 첫째짜리~셋째자리로 변환하고 싶다면
     * 
     * TimeFormatUtil tfu = new TimeFormatUtil(new Locale("ko"), ",", ".");
     * Double num = new Double(12.6789);
     * 
     * String result1 = tfu.dfformat(1, tfu.df1, num.longValue()); // 결과 : 12.6
     * String result2 = tfu.dfformat(2, tfu.df2, num.longValue()); // 결과 : 12.67
     * String result3 = tfu.dfformat(3, tfu.df3, num.longValue()); // 결과 :
     * 12.678
     * 
     * Long num2= new Double(-12.6789); String result1 = tfu.dfformat(1,
     * tfu.df1, num2.longValue()); // 결과 : -12.7 String result1 =
     * tfu.dfformat(2, tfu.df2, num2.longValue()); // 결과 : -12.68 String result1
     * = tfu.dfformat(3, tfu.df3, num2.longValue()); // 결과 : -12.679
     * 
     * @param pointLength
     *            변환할 소수점 이하 자리수.
     * @param df
     *            변환에 사용할 DecimalFormat. 멤버 변수 df0 ~ df3 중 자리수에 맞게 사용
     * @param a
     *            변환할 값
     * @return 소수점 이하 pointLength 길이 만큼의 변환된 값. pointLength 보다 자리수가 많을 경우
     *         pointLength+1 자리에서 반올림한다.
     */
    public String dfformatFloor(int pointLength, DecimalFormat df, Double a) {
        switch (pointLength) {
        case 3:
            return df.format(Math.floor(a.doubleValue() * 1000) / 1000d);
        case 2:
            return df.format(Math.floor(a.doubleValue() * 100) / 100d);
        case 1:
            return df.format(Math.floor(a.doubleValue() * 10) / 10d);
        case 0:
            return df.format(Math.floor(a.doubleValue()));
        default:
            return df.format(a.doubleValue());
        }
    }

    /**
     * 인자로 넘어온 double형 값보다 작거나 같은 가장 가까운 정수로 pointLength 길이 만큼 변환한다.
     * groupingSeparator, decimalSeparator는 생성자에 사용한 인자를 사용한다.
     * 
     * pointLength - 소수점 이하 자리수 df - 사용할 DecimalFormat. 소수점 이하 자리 0~3일 경우
     * TimeUtilFormat의 멤버 변수 df0 ~ df3 를 사용하면 된다.
     * 
     * 만약 로케일이 한국이고 12.3456 Long형 값을 소수점 이하 첫째짜리~셋째자리로 변환하고 싶다면
     * 
     * TimeFormatUtil tfu = new TimeFormatUtil(new Locale("ko"), ",", ".");
     * double num = 12.6789;
     * 
     * String result1 = tfu.dfformat(1, tfu.df1, num); // 결과 : 12.6 String
     * result2 = tfu.dfformat(2, tfu.df2, num); // 결과 : 12.67 String result3 =
     * tfu.dfformat(3, tfu.df3, num); // 결과 : 12.678
     * 
     * double num2 = -12.6789; String result1 = tfu.dfformat(1, tfu.df1, num2);
     * // 결과 : -12.7 String result1 = tfu.dfformat(2, tfu.df2, num2); // 결과 :
     * -12.68 String result1 = tfu.dfformat(3, tfu.df3, num2); // 결과 : -12.679
     * 
     * @param pointLength
     *            변환할 소수점 이하 자리수.
     * @param df
     *            변환에 사용할 DecimalFormat. 멤버 변수 df0 ~ df3 중 자리수에 맞게 사용
     * @param a
     *            변환할 값
     * @return 소수점 이하 pointLength 길이 만큼의 변환된 값. pointLength 보다 자리수가 많을 경우
     *         pointLength+1 자리에서 반올림한다.
     */
    public String dfformatFloor(int pointLength, DecimalFormat df, double a) {
        switch (pointLength) {
        case 3:
            return df.format(Math.floor(a * 1000) / 1000d);
        case 2:
            return df.format(Math.floor(a * 100) / 100d);
        case 1:
            return df.format(Math.floor(a * 10) / 10d);
        case 0:
            return df.format(Math.floor(a));
        default:
            return df.format(a);
        }
    }

    /**
     * 스트링 형식의 인자를 받아 yyyyMMdd 패턴의 날짜를 반환
     * 
     * @param text
     * @return 로케일이 적용된 yyyyMMdd 날짜 패턴을 반환. 파라미터의 길이가 짧을 경우 null 리턴
     * @throws ParseException
     */
    public String getYYYYMMDD(String yyyyMMdd) throws ParseException {
        // 넘어온 파라미터를 길이에 맞게 자른다.
        int length = yyyyMMdd.length();

        if (length < 8) {
            return null;
        } else if (length > 8) {
            yyyyMMdd = yyyyMMdd.substring(0, 9);
        }

        // TimeZoneDST의 getYYYYMMDD 이용 필요, Supplier의 로케일이 적용된 형태로 변경 
        String tzId = this.supplier.getTimezone().getName();
        TimeZoneDST tzDST = new TimeZoneDST(this.locale, false, false, false, tzId); // static으로..

        /*
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, this.locale);
        df.format(date);
        */

        return tzDST.getYYYYMMDD(yyyyMMdd);
    }

    /**
     * 스트링 형식의 인자를 받아 yyyyMM 패턴의 날짜를 반환
     * 
     * @param text
     * @return 로케일이 적용된 yyyyMM 날짜 패턴을 반환. 파라미터의 길이가 짧을 경우 null 리턴
     * @throws ParseException
     */
    public String getYYYYMM(String yyyyMMddHHmmss) throws ParseException {
        int length = yyyyMMddHHmmss.length();
        if (length != 14) {
            return null;
        }

        // TimeZoneDST의 getYYYYMMDD 이용 필요, Supplier의 로케일이 적용된 형태로 변경 
        String tzId = this.supplier.getTimezone().getName();
        TimeZoneDST tzDST = new TimeZoneDST(this.locale, false, false, false, tzId); // static으로..

        return tzDST.getYYYYMM(yyyyMMddHHmmss, tzId, DateFormat.SHORT);
    }

    /**
     * 스트링 형식의 인자를 받아 yyyy 패턴의 날짜를 반환
     * 
     * @param text
     * @return 로케일이 적용된 yyyy 날짜 패턴을 반환. 파라미터의 길이가 짧을 경우 null 리턴
     * @throws ParseException
     */
    public String getYYYY(String yyyyMMddHHmmss) throws ParseException {
        int length = yyyyMMddHHmmss.length();

        if (length != 14) {
            return null;
        }
        // TimeZoneDST의 getYYYYMMDD 이용 필요, Supplier의 로케일이 적용된 형태로 변경 
        String tzId = this.supplier.getTimezone().getName();
        TimeZoneDST tzDST = new TimeZoneDST(this.locale, false, false, false, tzId); // static으로..

        return tzDST.getYYYY(yyyyMMddHHmmss, tzId);
    }

    /**
     * 현재 로케일의 날짜를 반환. 파라미터의 길이에 따라 반환 되는 날짜 형식도 다르다. 다음은 로케일이 한국일 경우 파라미터 형식 별
     * 결과에 대한 예제이다.
     * 
     * 파라미터 - 결과 201006 - 2010. 6 20100615 - 2010. 6. 15
     * 
     * @param strDate
     *            DB에 저장된 날짜 형식의 파라미터. 상단의 설명 참조
     * @param lang
     *            언어 코드
     * @param country
     *            국가 코드
     * @return 로케일이 적용된 SimDateFormat 형식의 날짜 스트링
     */
    public static String getLocaleDateByMediumFormat(String strDate, String lang, String country) {
        if (strDate == null || "".equals(strDate))
            return "";
        else
            strDate = strDate.trim();

        Locale locale = new Locale(lang, country);

        SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMM", locale);
        SimpleDateFormat sdf8 = new SimpleDateFormat("yyyyMMdd", locale);
        SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss", locale);

        SimpleDateFormat locdf6 = new SimpleDateFormat(getYearMonthFormat(getDateFormat(16, lang, country)), locale);
        SimpleDateFormat locdf8 = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        SimpleDateFormat locdf14 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);

        String localeDate = "";

        try {
            switch (strDate.length()) {
            case 6:
                if (!strDate.equals("000000"))
                    localeDate = locdf6.format(sdf6.parse(strDate));
                break;
            case 8:
                if (!strDate.equals("00000000"))
                    localeDate = locdf8.format(sdf8.parse(strDate));
                break;
            case 14:
                if (!strDate.equals("00000000000000"))
                    localeDate = locdf14.format(sdf14.parse(strDate));
                break;
            default:
                localeDate = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localeDate;
    }

    /**
     * @desc 8/12/12 형식 ( 일/월/년) date를 20120812 형식의 date type으로 변환
     * 
     * @param formattedDate
     * @return
     */
    public static String convertToDbDate(String formattedDate) {
        String[] tempDate = formattedDate.split("/");

        String month = tempDate[0];
        String day = tempDate[1];
        String year = tempDate[2];

        year = "20" + year;

        // alert( day.toString().length );
        if (day.length() == 1) {
            day = "0" + day.toString();
        }

        if (month.length() == 1) {
            month = "0" + month.toString();
        }

        String tempDate2 = year + month + day;

        return tempDate2;
    }
    
	public static boolean isThisDateValid(String dateToValidate, String dateFromat){

		if(dateToValidate == null){
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);

		try {

			//if not valid, it will throw ParseException
			Date date = sdf.parse(dateToValidate);
			System.out.println(date);

		} catch (ParseException e) {

			e.printStackTrace();
			return false;
		}

		return true;
	}
}
