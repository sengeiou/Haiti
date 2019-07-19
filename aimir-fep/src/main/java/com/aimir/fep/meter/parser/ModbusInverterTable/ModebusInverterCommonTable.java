/**
 * (@)# ModebusInverterCommonTable.java
 *
 * 2015. 6. 13.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.meter.parser.ModbusInverterTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.parser.ModbusEMnVLS;

/**
 * @author simhanger
 *
 */
public class ModebusInverterCommonTable {
	public final static String UNDEFINED = "undefined";
	private static Logger log = LoggerFactory.getLogger(ModebusInverterCommonTable.class);

	
	public String resetLpTime(Calendar nowTime) {
		log.debug("nowTime = {}", getDatetimeString(nowTime.getTime(), "yyyyMMddHHmmss"));
		
		Calendar setTime = null;
		
		Calendar lPart1 = (Calendar) nowTime.clone();
		lPart1.set(Calendar.MINUTE, 0);
		lPart1.set(Calendar.SECOND, 0);
		log.debug("lPart1 = {}", getDatetimeString(lPart1.getTime(), "yyyyMMddHHmmss"));
		
		Calendar rPart1 = (Calendar) nowTime.clone();
		rPart1.set(Calendar.MINUTE, 15);
		rPart1.set(Calendar.SECOND, 00);
		log.debug("rPart1 = {}", getDatetimeString(rPart1.getTime(), "yyyyMMddHHmmss"));
		
		if((nowTime.after(lPart1) || nowTime.equals(lPart1)) && nowTime.before(rPart1)){
			setTime = lPart1;
		}
		
		Calendar lPart2 = (Calendar) nowTime.clone();
		lPart2.set(Calendar.MINUTE, 15);
		lPart2.set(Calendar.SECOND, 0);
		log.debug("lPart2 = {}", getDatetimeString(lPart2.getTime(), "yyyyMMddHHmmss"));
		
		Calendar rPart2 = (Calendar) nowTime.clone();
		rPart2.set(Calendar.MINUTE, 30);
		rPart2.set(Calendar.SECOND, 00);
		log.debug("rPart2 = {}", getDatetimeString(rPart2.getTime(), "yyyyMMddHHmmss"));
		
		if((nowTime.after(lPart2) || nowTime.equals(lPart2)) && nowTime.before(rPart2)){
			setTime = lPart2;
		}
		
		Calendar lPart3 = (Calendar) nowTime.clone();
		lPart3.set(Calendar.MINUTE, 30);
		lPart3.set(Calendar.SECOND, 0);
		log.debug("lPart3 = {}", getDatetimeString(lPart3.getTime(), "yyyyMMddHHmmss"));
		
		Calendar rPart3 = (Calendar) nowTime.clone();
		rPart3.set(Calendar.MINUTE, 44);
		rPart3.set(Calendar.SECOND, 59);
		log.debug("rPart3 = {}", getDatetimeString(rPart3.getTime(), "yyyyMMddHHmmss"));
		
		if((nowTime.after(lPart3) || nowTime.equals(lPart3)) && nowTime.before(rPart3)){
			setTime = lPart3;
		}
		
		Calendar lPart4 = (Calendar) nowTime.clone();
		lPart4.set(Calendar.MINUTE, 45);
		lPart4.set(Calendar.SECOND, 0);
		log.debug("lPart4 = {}", getDatetimeString(lPart4.getTime(), "yyyyMMddHHmmss"));
		
		Calendar rPart4 = (Calendar) nowTime.clone();		
		rPart4.set(Calendar.MINUTE, 59);
		rPart4.set(Calendar.SECOND, 59);
		log.debug("rPart4 = {}", getDatetimeString(rPart4.getTime(), "yyyyMMddHHmmss"));
		
		if((nowTime.after(lPart4) || nowTime.equals(lPart4)) && nowTime.before(rPart4)){
			setTime = lPart4;
		}
		
		log.debug("setTime = {}", getDatetimeString(setTime.getTime(), "yyyyMMddHHmmss"));
		
		return getDatetimeString(setTime.getTime(), "yyyyMMddHHmmss");
		
	}
	
	public static String getDatetimeString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
}
