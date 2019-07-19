package com.aimir.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.aimir.model.system.Contract;

/**
 * BillDateUtil.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 8. 16.   v1.0       eunmiae  과금일 관련 일자 취득 유틸       
 *
 */
public class BillDateUtil {

    public static String getBillDate(Contract contract, String someDay, int idx) {

        int billDate = contract.getBillDate() == null ? 1 : contract.getBillDate();
        int year = Integer.parseInt(someDay.substring(0, 4));
        int month = Integer.parseInt(someDay.substring(4, 6));
        int date = Integer.parseInt(someDay.substring(6, 8));

        Calendar calendar =  Calendar.getInstance();

        calendar.set(year, billDate > date ? month - 1 : month, billDate);
        calendar.add(Calendar.MONTH, idx); // 이번달일경우 : 0, 전월 : -1, 다음달 : 1
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        return formatter.format(calendar.getTime());
    }
	
	 /**
     * method name : getLastBillDay
     * method Desc : 일자 기준 전 달 과금일 조회
     *
     * 예) billDate : 15일
     *     오늘           : 8월 10일
     *     결과값        : 7월 15일
     * @param contract
     * @param someDay
     * @return
     */
    public static String getLastBillDay(Contract contract, String someDay, int idx) {

        int billDate = contract.getBillDate() == null ? 1 : contract.getBillDate();
        int year = Integer.parseInt(someDay.substring(0, 4));
        int month = Integer.parseInt(someDay.substring(4, 6)) - 1;
        int date = Integer.parseInt(someDay.substring(6, 8));

        Calendar calendar =  Calendar.getInstance();

        calendar.set(year, billDate > date ? month - 1 : month, billDate);
        calendar.add(Calendar.MONTH, idx); // 두 달 전일경우, -1
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        return formatter.format(calendar.getTime());
    }
  
    /**
     * method name : getNextBillDay
     * method Desc :
     * 
     * 예) billDate : 15일
     *     오늘           : 8월 10일
     *     결과값        : 8월 15일
     *     
     * @param contract
     * @param someDay
     * @param idx
     * @return
     */
    public static String getNextBillDay(Contract contract, String someDay, int idx) {
        int billDate = contract.getBillDate() == null ? 1 : contract.getBillDate();
        int year = Integer.parseInt(someDay.substring(0, 4));
        int month = Integer.parseInt(someDay.substring(4, 6)) - 1;
        int date = Integer.parseInt(someDay.substring(6, 8));

        Calendar calendar =  Calendar.getInstance();

        calendar.set(year, billDate > date ? month - 1 : month, billDate);
        //calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MONTH, idx); // 두달전 일경우, 0
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        return formatter.format(calendar.getTime());   	
    }
    
    /**
     * method name : getMonthToDate
     * method Desc :
     *
     * 예) billDate : 15일
     *     오늘           : 8월 10일
     *     결과값        : 8월 14일
     *     
     * @param contract
     * @param someDay
     * @param idx
     * @return
     */
    public static String getMonthToDate(Contract contract, String someDay, int idx) {
        int billDate = contract.getBillDate() == null ? 1 : contract.getBillDate();
        int year = Integer.parseInt(someDay.substring(0, 4));
        int month = Integer.parseInt(someDay.substring(4, 6)) - 1;
        int date = Integer.parseInt(someDay.substring(6, 8));

        Calendar calendar =  Calendar.getInstance();

        calendar.set(year, billDate > date ? month - 1 : month, billDate);
//        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MONTH, idx); // 두달전 일경우, 0
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        return formatter.format(calendar.getTime());   	
    }

}
