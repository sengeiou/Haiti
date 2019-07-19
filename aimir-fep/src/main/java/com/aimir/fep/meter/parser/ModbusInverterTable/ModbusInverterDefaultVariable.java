/**
 * (@)# ModbusInverterDefaultVariable.java
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author simhanger
 *
 */
public class ModbusInverterDefaultVariable {
	Map<String, Object> obisMap = new LinkedHashMap<String, Object>();

	public enum MODBUS_DEFAULT_CODE {
		DATE,                   //날짜
		OUTPUT_FREQUENCY,     //주파수
		OUTPUT_VOLTAGE,       //전압
		OUTPUT_CURRENT;       //전류 
	}


	public enum LOAD_PROFILE {
		Structure(0, "Structure"),                        
		ImportActive(1, "Import Active Energy QI+QIV"),                   // 순방향 유효전력량  
		ImportLaggingReactive(2, "Import Lagging Reactive Energy QI"),  // 순방향 지상 무효전력량
		ImportLeadingReactive(3, "Import Leading Reactive Energy QIV"), // 순방향 진상 무효전력량
		ImportApparentEnergy(4, "Import Apparent Energy QI+QIV"),       // 순방향 피상 전력량 
		Date(5, "Date"),                                                  // 일자/시간
		Status(6, "Status");                                              // 상태정보
//		ExportActive(7, "Export Active Energy QII+QIII"),                // 역방향 유효 전력량
//		ExportLaggingReactive(8,	"Export Lagging Reactive Energy QII"), // 역방향 진상 무효전력량
//		ExportLeadingReactive(9, "Export Leading Reactive Energy QIII"), // 역방향 지상 무효전력량
//		ExportApparentEnergy(10, "Export Apparent Energy QII+QIII");     // 역방향 피상전력량

		private int code;
		private String name;

		LOAD_PROFILE(int code, String name) {
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return this.code;
		}

		public String getName() {
			return this.name;
		}
	}
}
