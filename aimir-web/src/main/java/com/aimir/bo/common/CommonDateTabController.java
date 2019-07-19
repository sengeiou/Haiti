package com.aimir.bo.common;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.Season;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.SeasonManager;
import com.aimir.service.mvm.bean.SeasonData;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Controller
public class CommonDateTabController {

    @Autowired
    SeasonManager seasonManager;

    @Autowired
    SupplierManager supplierManager;

    /**
     * 날짜와 증감값을 입력받아 날짜 계산
     * @param dateVal
     * @param addVal
     * @return
     */
    @RequestMapping(value="/common/getDate")
    public ModelAndView getDate(
            @RequestParam("searchDate") String dateVal,
            @RequestParam("addVal") String addVal,
            @RequestParam("supplierId") String supplierId,
            @RequestParam(value="basicDate", required=false) String basicDate,
            @RequestParam(value="monthCalc", required=false) Boolean monthCalc) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        if (basicDate == null || basicDate.isEmpty()) {
            basicDate = CalendarUtil.getCurrentDate();
        }

        if (dateVal == null || dateVal.trim().isEmpty()) {
            dateVal = basicDate;
        } else {
            dateVal = TimeLocaleUtil.getDBDate(dateVal, 8,  lang,  country);
        }

        if (addVal == null || addVal.trim().isEmpty()) {
            addVal = "0";
        }

        Integer iaddVal = Integer.parseInt(addVal);
        String resultDate = null;

        // monthCalc = true 이면 일자가 아닌 월로 계산한다.
        if (monthCalc != null && monthCalc == true) {
            iaddVal = -1;
            resultDate = CalendarUtil.getDate(dateVal, Calendar.MONTH, iaddVal);
        } else {
            resultDate = CalendarUtil.getDate(dateVal, Calendar.DAY_OF_MONTH, iaddVal);
        }
        //        resultDate = CalendarUtil.getDate(dateVal, Calendar.DAY_OF_MONTH, Integer.parseInt(addVal));

        if (Integer.parseInt(basicDate) < Integer.parseInt(resultDate)) {
            resultDate = basicDate;
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("searchDate", TimeLocaleUtil.getLocaleDate(resultDate, lang, country));
        mav.addObject("dbDate", resultDate);
        mav.addObject("currDate"  , TimeLocaleUtil.getLocaleDate(basicDate, lang, country));

        return mav;
    }

    /**
     * 날짜와 증감값을 입력받아 날짜 계산
     * @param dateVal
     * @param addVal
     * @return
     */
    @RequestMapping(value="/common/getYearMonth")
    public ModelAndView getYearMonth(@RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("addVal") String addVal,
            @RequestParam(value="basicDate", required=false) String basicDate) {
        StringBuffer sb = new StringBuffer();
        sb.append(year);
        sb.append(Integer.parseInt(month)<10?"0"+month:month);
        sb.append("01");

        String resultDate = CalendarUtil.getDateUsingFormat(sb.toString(), Calendar.MONTH, Integer.parseInt(addVal));

        resultDate = resultDate.replaceAll("/", "");

        if (basicDate == null || basicDate.isEmpty()) {
            basicDate = CalendarUtil.getCurrentDate();
        }

        if(Integer.parseInt(basicDate)<Integer.parseInt(resultDate)){
            resultDate = basicDate;
        }

        String resYear = resultDate.substring(0,4);
        String resMonth = resultDate.substring(4,6);

        resMonth = Integer.toString(Integer.parseInt(resMonth));

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("year",resYear);
        mav.addObject("month",resMonth);
        return mav;
    }

    /**
     * 최근10년을 계산
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getYear")
    public ModelAndView getYear(@RequestParam("supplierId") String supplierId,
            @RequestParam(value="basicDate", required=false) String basicDate) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        if (basicDate == null || basicDate.isEmpty()) {
            basicDate = CalendarUtil.getCurrentDate();
        }

        String year = CalendarUtil.getDateUsingFormat(basicDate, Calendar.YEAR, -9);
        year = year.substring(0, 4);

        String currDate = TimeLocaleUtil.getLocaleDate(basicDate, lang, country);
        String currYear = Integer.toString(Integer.parseInt(year)+9);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("year",year);
        mav.addObject("currYear",currYear);
        mav.addObject("currDate",currDate);
        return mav;
    }

//    /**
//     * method name : getYearBasicDate<b/>
//     * method Desc : 기준일을 기준으로 최근10년을 계산한다. 기준일이 null 이면 오늘일자를 기준일로 지정한다.
//     *
//     * @param supplierId
//     * @param basicDate 기준일
//     * @return
//     */
//    @RequestMapping(value="/common/getYearBasicDate")
//    public ModelAndView getYearBasicDate(@RequestParam("supplierId") String supplierId,
//            @RequestParam(value="basicDate", required=false) String basicDate) {
//
//        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));
//
//        String country = supplier.getCountry().getCode_2letter();
//        String lang    = supplier.getLang().getCode_2letter();
//
//        if (basicDate == null || basicDate.isEmpty()) {
//            basicDate = CalendarUtil.getCurrentDate();
//        }
//        String year = CalendarUtil.getDateUsingFormat(basicDate, Calendar.YEAR, -9);
//        year = year.substring(0, 4);
//
//        String currDate = TimeLocaleUtil.getLocaleDate(basicDate, lang, country);
//        String currYear = Integer.toString(Integer.parseInt(year)+9);
//
//        ModelAndView mav = new ModelAndView("jsonView");
//        mav.addObject("year",year);
//        mav.addObject("currYear",currYear);
//        mav.addObject("currDate",currDate);
//        return mav;
//    }

    /**
     * 년을  날짜와 증감값을 입력받아 날짜 계산
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getYearAddVal")
    public ModelAndView getYearAddVal(@RequestParam("year") String year,
            @RequestParam("addVal") String addVal) {
        StringBuffer sb = new StringBuffer();
        sb.append(year);
        sb.append("01");
        sb.append("01");

        String resultDate = CalendarUtil.getDate(sb.toString(), Calendar.YEAR, Integer.parseInt(addVal));

        resultDate = resultDate.replaceAll("/", "");

        if(Integer.parseInt(CalendarUtil.getCurrentDate())<Integer.parseInt(resultDate)){
            resultDate = CalendarUtil.getCurrentDate();
        }

        String targetYear = resultDate.substring(0,4);
        String currYearRange = CalendarUtil.getDate(CalendarUtil.getCurrentDate(), Calendar.YEAR, -9).substring(0,4);
        String currYear = CalendarUtil.getDate(CalendarUtil.getCurrentDate(), Calendar.YEAR, 0).substring(0,4);
        String currDate = CalendarUtil.getCurrentDateUsingFormat();


        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("targetYear",targetYear);
        mav.addObject("year",currYearRange);
        mav.addObject("currYear",currYear);
        mav.addObject("currDate",currDate);
        return mav;
    }

    /**
     * 연도의 시작일,종료일계산
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getYearPeriod")
    public ModelAndView getYearPeriod(@RequestParam("year") String year, 
            @RequestParam("supplierId") String supplierId,
            @RequestParam(value="basicDate", required=false) String basicDate) {
        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        if (basicDate == null || basicDate.isEmpty()) {
            basicDate = CalendarUtil.getCurrentDate();
        }

        String currYear = basicDate.substring(0, 4);
        String startDate = year + "0101";
        String endDate = year + "1231";

        if(year.equals(currYear)){
            endDate = basicDate;
        }

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("startDate", TimeLocaleUtil.getLocaleDate(startDate, lang, country));
        mav.addObject("endDate"  , TimeLocaleUtil.getLocaleDate(endDate, lang, country));
        return mav;
    }

    /**
     * 년도를 입력받아 월수 계산
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getMonth")
    public ModelAndView getMonth(@RequestParam("year") String year,
            @RequestParam(value="basicDate", required=false) String basicDate) {
        String monthCount = null;
//        String monthCount = CalendarUtil.getMonthCountOfYear(year);

        if (!StringUtil.nullToBlank(basicDate).isEmpty() && basicDate.substring(0, 4).equals(year)) {
            monthCount = Integer.valueOf(basicDate.substring(4, 6)).toString();
        } else {
            monthCount = CalendarUtil.getMonthCountOfYear(year);
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("monthCount",monthCount);
        return mav;
    }

    /**
     * 년도,월을 입력받아 주차수 계산
     * @param year
     * @param month
     * @return
     */
    @RequestMapping(value="/common/getWeek")
    public ModelAndView getWeek(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam(value="basicDate", required=false) String basicDate) {

        String weekCount = null;

        if (!StringUtil.nullToBlank(basicDate).isEmpty() && year.equals(basicDate.substring(0, 4))
                && Integer.valueOf(month).equals(Integer.valueOf(basicDate.substring(4, 6)))) {
            weekCount = Integer.toString(CalendarUtil.getWeekOfMonth(basicDate));
        } else {
            weekCount = CalendarUtil.getWeekCountOfMonth(year, month);
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("weekCount",weekCount);
        return mav;
    }

    /**
     * 년도,월,주차를 입력받아 시작요일,종료요일 계산
     * @param year
     * @param month
     * @param week
     * @return
     */
    @RequestMapping(value="/common/getWeekDay")
    public ModelAndView getWeekDay(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("week") String week,
            @RequestParam(value="basicDate", required=false) String basicDate) {

        Map<String,String> map = CalendarUtil.getWeekDayOfWeek(year, month, week);

        String startWeek = (String)map.get("startWeek");
        String endWeek = null;

        if (!StringUtil.nullToBlank(basicDate).isEmpty() && year.equals(basicDate.substring(0, 4))
                && Integer.valueOf(month).equals(Integer.valueOf(basicDate.substring(4, 6)))
                && week.equals(Integer.toString(CalendarUtil.getWeekOfMonth(basicDate)))) {
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(DateTimeUtil.getDateFromYYYYMMDD(basicDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            endWeek = Integer.toString(cal.get(Calendar.DAY_OF_WEEK));
        } else {
            endWeek = (String)map.get("endWeek");
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("startWeek", startWeek);
        mav.addObject("endWeek", endWeek);
        return mav;
    }

    /**
     * 년도,월,주차를 입력받아 해당주의 시작일자,종료일자 계산
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getWeekPeriod")
    public ModelAndView getWeekPeriod(
            @RequestParam("year")  String year,
            @RequestParam("month") String month,
            @RequestParam("week")  String week,
            @RequestParam("supplierId") String supplierId,
            @RequestParam(value="basicDate", required=false) String basicDate) {
        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        Map<String,String> map = CalendarUtil.getDateWeekOfMonth(year, month, week);
        ModelAndView mav = new ModelAndView("jsonView");

        String startDate = (String)map.get("startDate");
        String endDate = null;

        if (!StringUtil.nullToBlank(basicDate).isEmpty() && year.equals(basicDate.substring(0, 4))
                && Integer.valueOf(month).equals(Integer.valueOf(basicDate.substring(4, 6)))
                && week.equals(Integer.toString(CalendarUtil.getWeekOfMonth(basicDate)))) {
            //weekCount = Integer.toString(CalendarUtil.getWeekOfMonth(basicDate));
            endDate = basicDate;
        } else {
            endDate = (String)map.get("endDate");
        }

        mav.addObject("startDate", TimeLocaleUtil.getLocaleDate(startDate, lang, country));
        mav.addObject("endDate"  , TimeLocaleUtil.getLocaleDate(endDate, lang, country));

        return mav;
    }

    /**
     * 년도,월,주차를 입력받아 해당주의 시작일자,종료일자 계산
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getWeekPeriodFromTo")
    public ModelAndView getWeekPeriodFromTo (
            @RequestParam("startYear")  String startYear,
            @RequestParam("startMonth") String startMonth,
            @RequestParam("startWeek")  String startWeek,
            @RequestParam("endYear")  String endYear,
            @RequestParam("endMonth") String endMonth,
            @RequestParam("endWeek")  String endWeek,
            @RequestParam("supplierId") String supplierId,
            @RequestParam(value="basicDate", required=false) String basicDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        Map<String,String> startMap = CalendarUtil.getDateWeekOfMonth(startYear, startMonth, startWeek);
        Map<String,String> endMap = CalendarUtil.getDateWeekOfMonth(endYear, endMonth, endWeek);

        String startDate = (String)startMap.get("startDate");
        String endDate = null;

        if (!StringUtil.nullToBlank(basicDate).isEmpty() && endYear.equals(basicDate.substring(0, 4))
                && Integer.valueOf(endMonth).equals(Integer.valueOf(basicDate.substring(4, 6)))
                && endWeek.equals(Integer.toString(CalendarUtil.getWeekOfMonth(basicDate)))) {
            //weekCount = Integer.toString(CalendarUtil.getWeekOfMonth(basicDate));
            endDate = basicDate;
        } else {
            endDate = (String)endMap.get("endDate");
        }

        mav.addObject("startDate", TimeLocaleUtil.getLocaleDate(startDate, lang, country));
        mav.addObject("endDate"  , TimeLocaleUtil.getLocaleDate(endDate, lang, country));

        return mav;
    }

    /**
     * 년도,월을 입력받아 해당월의 시작일자,종료일자 계산
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getMonthPeriod")
    public ModelAndView getMonthPeriod(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("supplierId") String supplierId,
            @RequestParam(value="basicDate", required=false) String basicDate) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        Map<String,String> map = CalendarUtil.getDateMonth(year, month);
        String startDate = (String)map.get("startDate");
        String endDate = null;

        if (!StringUtil.nullToBlank(basicDate).isEmpty() && year.equals(basicDate.substring(0, 4))
                && Integer.valueOf(month).equals(Integer.valueOf(basicDate.substring(4, 6)))) {
            endDate = basicDate;
        } else {
            endDate = (String)map.get("endDate");
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("startDate", TimeLocaleUtil.getLocaleDate(startDate, lang, country));
        mav.addObject("endDate"  , TimeLocaleUtil.getLocaleDate(endDate, lang, country));

        return mav;
    }
    
    @RequestMapping(value="/common/getFullMonthlyPeriod")
    public ModelAndView getFullMonthlyPeriod(
    		@RequestParam("year") String year,
    		@RequestParam("month") String month,
    		@RequestParam("supplierId") Integer supplierId,
    		String basicDate) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	Supplier supplier = supplierManager.getSupplier(supplierId);
    	String country = supplier.getCountry().getCode_2letter();
    	String lang    = supplier.getLang().getCode_2letter();
    	
    	Map<String, String> map = CalendarUtil.getDateFullMonth(year, month);
    	String startDate = (String) map.get("startDate");
    	String endDate = (String) map.get("endDate");
    	
    	mav.addObject("startDate", TimeLocaleUtil.getLocaleDate(startDate, lang, country));
    	mav.addObject("endDate", TimeLocaleUtil.getLocaleDate(endDate, lang, country));
    	return mav;
    }

    /**
     * 년도,월,일를 입력받아 해당주의 시작일자,종료일자 계산 (주의 시작 요일은 일요일.)
     * @param year
     * @param month
     * @param day
     * @param week
     * @return
     */
    @RequestMapping(value="/common/getWeekThisDayPeriod")
    public ModelAndView getWeekThisDayPeriod(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("day") String day,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        int thisWeek = CalendarUtil.getWeekOfMonth( year + month + day );
        Map<String,String> map = CalendarUtil.getDateWeekOfMonth(year, String.valueOf( Integer.parseInt( month ) ), String.valueOf(thisWeek) );
        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("startDate", TimeLocaleUtil.getLocaleDate((String)map.get("startDate"), lang, country));
        mav.addObject("endDate"  , TimeLocaleUtil.getLocaleDate((String)map.get("endDate"), lang, country));
        return mav;
    }

    /**
     * 년도,월,주차,요일을 입력받아 해당주의 시작일자,종료일자 계산
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getWeekDayPeriod")
    public ModelAndView getWeekDayPeriod(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("week") String week,
            @RequestParam("weekDay") String weekDay,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        String date = CalendarUtil.getDateWeekDayOfWeek(year, month, week,weekDay);
        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("startDate", TimeLocaleUtil.getLocaleDate(date.replaceAll("/", ""), lang, country));
        mav.addObject("endDate"  , TimeLocaleUtil.getLocaleDate(date.replaceAll("/", ""), lang, country));

        return mav;
    }

    /**
     * 년도,월,주차,요일을 입력받아 해당주의 시작일자,종료일자 계산
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getSeason")
    public ModelAndView getSeason(@RequestParam("year") String year,
            @RequestParam(value="basicDate", required=false) String basicDate) {

        Map<String,SeasonData> seasonDataMap = null;

        if (!StringUtil.nullToBlank(basicDate).isEmpty()) {
            seasonDataMap = seasonManager.getSeasonData(year, basicDate);
        } else {
            seasonDataMap = seasonManager.getSeasonData(year);
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject(Season.SPRING.getSeason(),seasonDataMap.get(Season.SPRING.getSeason())==null?"":seasonDataMap.get(Season.SPRING.getSeason()));
        mav.addObject(Season.SUMMER.getSeason(),seasonDataMap.get(Season.SUMMER.getSeason())==null?"":seasonDataMap.get(Season.SUMMER.getSeason()));
        mav.addObject(Season.AUTUMN.getSeason(),seasonDataMap.get(Season.AUTUMN.getSeason())==null?"":seasonDataMap.get(Season.AUTUMN.getSeason()));
        mav.addObject(Season.WINTER.getSeason(),seasonDataMap.get(Season.WINTER.getSeason())==null?"":seasonDataMap.get(Season.WINTER.getSeason()));
        return mav;
    }

    /**
     * 년도,월,일를 입력받아 해당주의 시작일자,종료일자 계산 (주의 시작 요일은 일요일.)
     * @param year
     * @param month
     * @param day
     * @param week
     * @return
     */
    @RequestMapping(value="/common/getMonthQuarter")
    public ModelAndView getMonthQuarter(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("quarter") String quarter,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        double toQuarter = 3.0;
        if( "4".equals( quarter ) ) toQuarter = 3.0;
        else if( "3".equals( quarter ) ) toQuarter = 4.0;
        else if( "2".equals( quarter ) ) toQuarter = 6.0;

        Map<String,String> map = CalendarUtil.getMonthQuarter(year,month, toQuarter );
        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("startDate", TimeLocaleUtil.getLocaleDate((String)map.get("startDate").replaceAll("/", ""), lang, country));
        mav.addObject("endDate"  , TimeLocaleUtil.getLocaleDate((String)map.get("endDate").replaceAll("/", ""), lang, country));

        return mav;
    }


    @RequestMapping(value="/common/convertSearchDate")
    public ModelAndView convertSearchDate(
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("supplierId") String supplierId,
            @RequestParam(value="basicDate", required=false) String basicDate) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        if (basicDate == null || basicDate.isEmpty()) {
            basicDate = CalendarUtil.getCurrentDate();
        }

        try {
            if(searchStartDate != null && !"".equals(searchStartDate.trim()))
                searchStartDate = TimeLocaleUtil.getDBDate(searchStartDate, 8, lang, country);
        } catch (Exception e) {
            searchStartDate = basicDate;
        }

        try {
            if(searchEndDate != null && !"".equals(searchEndDate.trim()))
                searchEndDate = TimeLocaleUtil.getDBDate(searchEndDate, 8, lang, country);
        } catch (Exception e) {
            searchEndDate = basicDate;
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("searchStartDate", searchStartDate);
        mav.addObject("searchEndDate"  , searchEndDate);

        return mav;
    }

    @RequestMapping(value="/common/convertLocalDate")
    public ModelAndView convertLocalDate(
            @RequestParam("dbDate") String dbDate,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("localDate", TimeLocaleUtil.getLocaleDate(dbDate, lang, country));

        return mav;
    }

    @RequestMapping(value="/common/convertDbDate")
    public ModelAndView convertDbDate(
            @RequestParam("dbDate") String dbDate,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        ModelAndView mav = new ModelAndView("jsonView");
        
        //String localDate = TimeLocaleUtil.getLocaleDate(dbDate, lang, country);
        
        String localDate = TimeLocaleUtil.convertToDbDate(dbDate);
        
        
        
        mav.addObject("localDate", localDate);

        return mav;
    }

    @RequestMapping(value="/common/convertDBDate")
    public ModelAndView convertDBDate(
            @RequestParam("localDate") String localDate,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();


        try {
            if(localDate != null && !"".equals(localDate.trim()))
                localDate = TimeLocaleUtil.getDBDate(localDate, 8, lang, country);
        } catch (Exception e) {
            localDate = CalendarUtil.getCurrentDate();
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("dbDate", localDate);


        return mav;
    }

    @RequestMapping(value="/common/convertDBDateYYYYMMDDHHMMSS")
    public ModelAndView convertDBDateYYYYMMDDHHMMSS(
            @RequestParam("localDate") String localDate,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();


        try {
            if(localDate != null && !"".equals(localDate.trim()))
                localDate = TimeLocaleUtil.getDBDate(localDate, 14, lang, country);
        } catch (Exception e) {
            localDate = CalendarUtil.getCurrentDate();
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("dbDate", localDate);


        return mav;
    }

    // 20110809 -> 2011. 8. 9로 변형
    // 20110811 -> Aug 11. 2011
    @RequestMapping(value="/common/convertLocalDateByMediumFormat")
    public ModelAndView convertLocalDateByMediumFormat(
            @RequestParam("dbDate") String dbDate,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("localDate", TimeLocaleUtil.getLocaleDateByMediumFormat(dbDate, lang, country));

        return mav;
    }

    // 20110809 -> 2011. 8. 9로 변형
    // 20110811 -> Aug 11. 2011
    @RequestMapping(value="/common/convertSearchDateByMediumFormat")
    public ModelAndView convertSearchDateByMediumFormat(
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();


        try {
            if(searchStartDate != null && !"".equals(searchStartDate.trim()))
                searchStartDate = TimeLocaleUtil.getDBDate(searchStartDate, 16, lang, country);
        } catch (Exception e) {
            searchStartDate = CalendarUtil.getCurrentDate();
        }

        try {
            if(searchEndDate != null && !"".equals(searchEndDate.trim()))
                searchEndDate = TimeLocaleUtil.getDBDate(searchEndDate, 16, lang, country);
        } catch (Exception e) {
            searchEndDate = CalendarUtil.getCurrentDate();
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("searchStartDate", searchStartDate);
        mav.addObject("searchEndDate"  , searchEndDate);

        return mav;
    }

    /**
     * 년도,월을 입력받아 해당월의 시작일자,종료일자 계산
     * 20110809 -> 2011. 8. 9로 변형
     * 20110811 -> Aug 11. 2011
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getMonthPeriodByMediumFormat")
    public ModelAndView getMonthPeriodByMediumFormat(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        Map<String,String> map = CalendarUtil.getDateMonth(year, month);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("startDate", TimeLocaleUtil.getLocaleDateByMediumFormat((String)map.get("startDate"), lang, country));
        mav.addObject("endDate"  , TimeLocaleUtil.getLocaleDateByMediumFormat((String)map.get("endDate"), lang, country));

        return mav;
    }

    /**
     * 연도의 시작일,종료일계산
     * 20110809 -> 2011. 8. 9로 변형
     * 20110811 -> Aug 11. 2011
     * @param year
     * @return
     */
    @RequestMapping(value="/common/getYearPeriodByMediumFormat")
    public ModelAndView getYearPeriodByMediumFormat(@RequestParam("year") String year,
            @RequestParam("supplierId") String supplierId) {

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        String currYear = CalendarUtil.getCurrentDate();
        currYear = currYear.substring(0, 4);

        String startDate = year+"0101";
        String endDate = year+"1231";
        if(year.equals(currYear)){
            endDate = CalendarUtil.getCurrentDate();
        }

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("startDate", TimeLocaleUtil.getLocaleDateByMediumFormat(startDate, lang, country));
        mav.addObject("endDate"  , TimeLocaleUtil.getLocaleDateByMediumFormat(endDate, lang, country));
        return mav;
    }

    /**
     * method name : getWeeksCount<b/>
     * method Desc : 시작년도, 시작월, 시작주차, 종료년도, 종료월, 종료주차를 입력받아 주차기간을 계산
     *
     * @param startYear
     * @param startMonth
     * @param startWeek
     * @param endYear
     * @param endMonth
     * @param endWeek
     * @return
     */
    @RequestMapping(value="/common/getWeeksCount")
    public ModelAndView getWeeksCount (@RequestParam("startYear") String startYear,
            @RequestParam("startMonth") String startMonth,
            @RequestParam("startWeek") String startWeek,
            @RequestParam("endYear") String endYear,
            @RequestParam("endMonth") String endMonth,
            @RequestParam("endWeek") String endWeek) {
        ModelAndView mav = new ModelAndView("jsonView");

        int weekCount = 0;
        startMonth = StringUtil.frontAppendNStr('0', startMonth, 2);
        endMonth = StringUtil.frontAppendNStr('0', endMonth, 2);
        String curYear = startYear;
        String curMonth = startMonth;
        String curYearMonth = curYear + curMonth;
        boolean isComplete = false;
        int totalWeeksCount = 0;
        int j = 0;

        for (int i = 0; i < 10; i++) {      // 무한 loop 를 방지하기 위해 loop 회수를 10으로 제한
            if (i == 0) {
                j = Integer.parseInt(startMonth) -1;
            } else {
                j = 0;
            }

            for (; j < 12; j++) {
                if (curYear.equals(startYear) && curMonth.equals(startMonth)) {     // 시작일자일 경우
                    weekCount = Integer.parseInt(CalendarUtil.getWeekCountOfMonth(curYear, curMonth));
                    totalWeeksCount = weekCount - Integer.parseInt(startWeek) + 1;
                } else if (curYear.equals(endYear) && curMonth.equals(endMonth)) {  // 종료일자일 경우
                    totalWeeksCount = totalWeeksCount + Integer.parseInt(endWeek);
                } else {
                    weekCount = Integer.parseInt(CalendarUtil.getWeekCountOfMonth(curYear, curMonth));
                    totalWeeksCount = totalWeeksCount + weekCount;
                }

                if (curYear.equals(endYear) && curMonth.equals(endMonth)) {     // 종료일자일 경우 종료
                    isComplete = true;
                    break;
                }

                // 월을 +1 더함
                curYearMonth = (CalendarUtil.getDate(curYearMonth + "01", Calendar.MONTH, 1)).substring(0, 6);
                curYear = curYearMonth.substring(0, 4);
                curMonth = curYearMonth.substring(4, 6);
            }

            if (isComplete) break;
        }

        mav.addObject("weekCount", totalWeeksCount);
        return mav;
    }

    /**
     * method name : getSeasonsCount<b/>
     * method Desc : 시작년도, 시작계절, 종료년도, 종료계절을 입력받아 계절 횟수를 계산
     *
     * @param startYear
     * @param startSeason
     * @param endYear
     * @param endSeason
     * @return
     */
    @RequestMapping(value="/common/getSeasonsCount")
    public ModelAndView getSeasonsCount (@RequestParam("startYear") String startYear,
            @RequestParam("startSeason") String startSeason,
            @RequestParam("endYear") String endYear,
            @RequestParam("endSeason") String endSeason) {
        ModelAndView mav = new ModelAndView("jsonView");
        String curYear = startYear;
        int totalSeasonsCount = 0;

        if (curYear.equals(startYear) && curYear.equals(endYear)) { // 시작년도와 종료년도가 동일할 경우
            totalSeasonsCount = Integer.parseInt(endSeason) - Integer.parseInt(startSeason) + 1;
        } else {        // 시작년도와 종료년도가 다를 경우
            for (int i = 0; i < 10; i++) { // 무한 loop 를 방지하기 위해 loop 회수를 10으로 제한
                if (curYear.equals(startYear)) { // 시작년도일 경우
                    totalSeasonsCount = 4 - Integer.parseInt(startSeason) + 1;
                } else if (curYear.equals(endYear)) { // 종료년도일 경우
                    totalSeasonsCount = totalSeasonsCount + Integer.parseInt(endSeason);
                    break;
                } else {
                    totalSeasonsCount = totalSeasonsCount + 4;
                }

                curYear = (CalendarUtil.getDate(curYear + "0101", Calendar.YEAR, 1)).substring(0, 4);
            }
        }

        mav.addObject("seasonsCount", totalSeasonsCount);
        return mav;
    }

    /**
     * method name : getHoursCount<b/>
     * method Desc : 시작일자, 시작시간, 종료일자, 종료시간을 입력받아 시간 범위를 계산
     *
     * @param startDate
     * @param startHour
     * @param endDate
     * @param endHour
     * @return
     */
    @RequestMapping(value="/common/getHoursCount")
    public ModelAndView getHoursCount(@RequestParam("startDate") String startDate,
            @RequestParam("startHour") String startHour,
            @RequestParam("endDate") String endDate,
            @RequestParam("endHour") String endHour) {
        ModelAndView mav = new ModelAndView("jsonView");
        String curDateHour = startDate + startHour;
        String lastDateHour = endDate + endHour;
        int totalHoursCount = 0;

        int year = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;

        for (int i = 0; i < 100; i++) {
            if (curDateHour.compareTo(lastDateHour) <= 0) {
                totalHoursCount++;
            } else {
                break;
            }

            year = Integer.parseInt(curDateHour.substring(0,4));
            month = Integer.parseInt(curDateHour.substring(4,6))-1;
            day = Integer.parseInt(curDateHour.substring(6,8));
            hour = Integer.parseInt(curDateHour.substring(8,10));
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, hour, minute);
            cal.add(Calendar.HOUR_OF_DAY, 1);

            StringBuilder sb = new StringBuilder();
            sb.append(cal.get(Calendar.YEAR));
            sb.append(CalendarUtil.to2Digit(cal.get(Calendar.MONTH)+1));
            sb.append(CalendarUtil.to2Digit(cal.get(Calendar.DAY_OF_MONTH)));
            sb.append(CalendarUtil.to2Digit(cal.get(Calendar.HOUR_OF_DAY)));

            curDateHour = sb.toString();
        }
        mav.addObject("hoursCount", totalHoursCount);
        return mav;
    }

    /**
     * method name : getHoursCount<b/>
     * method Desc : 시작일자, 종료일자를 입력받아 일자 범위를 계산
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value="/common/getDaysCount")
    public ModelAndView getDaysCount(@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        
        int daysCount = 0;
        try {
            daysCount = TimeUtil.getDayDuration(startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.addObject("daysCount", daysCount + 1);
        return mav;
    }

    @RequestMapping(value = "/common/i18nDateLocaleMessage.do")
    public ModelAndView fmtMessage() {
        return new ModelAndView("/js/framework/Config/I18NDateLocaleMessage");
    }
}