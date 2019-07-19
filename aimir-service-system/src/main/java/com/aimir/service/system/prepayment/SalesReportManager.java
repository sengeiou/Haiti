package com.aimir.service.system.prepayment;

import java.util.Map;

import com.aimir.model.system.Supplier;

public interface SalesReportManager {
	/**
	 * @MethodName getAddBalanceList
	 * @Date 2014. 4. 23.
	 * @param supplier
	 * @param page
	 * @param limit
	 * @param searchDate
	 * @return
	 * @Modified
	 * @Description 특정 날짜에 대한 전체 선불 구매 내역 조회(단위 포맷화)
	 */
	public Map<String, Object> getAddBalanceList(Supplier supplier, Integer page, Integer limit, String searchDate, String vendorId);
	
	/**
	 * @MethodName getMonthlyGridDataList
	 * @Date 2014. 4. 28.
	 * @param supplier
	 * @param page
	 * @param limit
	 * @param startDate
	 * @param endDate
	 * @return
	 * @Modified
	 * @Description 특정 날짜에 대한 고객별 전기 사용내역 관련 데이터 조회
	 */
	public Map<String, Object> getMonthlyGridDataList(Supplier supplier, Integer page, Integer limit, String startDate, String endDate, String vendorId);
}
