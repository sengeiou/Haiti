package com.aimir.schedule.util;

import java.util.Date;

/**
 * 데이터 베이스 조회 조건등을 담는 vo객체.
 * @author kskim
 *
 */
public class SAPFileOutputCondition {
	/**
	 * 미터 시리얼 정보
	 */
	private String[] meterSerials = null;
	
	/**
	 * 날짜 영역
	 */
	private Date[] dateRange = null;
	
	public String[] getMeterSerials() {
		return meterSerials;
	}

	public void setMeterSerials(String[] meterSerials) {
		this.meterSerials = meterSerials;
	}

	public Date[] getDateRange() {
		return dateRange;
	}

	public void setDateRange(Date[] dateRange) {
		this.dateRange = dateRange;
	}
}
