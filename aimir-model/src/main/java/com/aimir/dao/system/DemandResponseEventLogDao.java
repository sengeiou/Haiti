package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.DemandResponseEventLog;

public interface DemandResponseEventLogDao extends GenericDao<DemandResponseEventLog, Integer>{

	/**
	 * 
	 * method name : getDemandResponseHistory
	 * method Desc : 조회 조건으로 계약고객의 Demand Response History를 조회한다.
	 * 
	 * @param userId
	 * @param contractNumber 계약번호  Contract.contractNumber
	 * @param page 페이지번호
	 * @param limit 최대 개수
	 * @param fromDate 조회시작날짜 yyyymmdd
	 * @param toDate 조회 마지막 날짜 yyyymmdd
	 * @return
	 */
	public List<Map<String, Object>> getDemandResponseHistory(String userId, 
																String contractNumber, 
																int page, 
																int limit, 
																String fromDate, 
																String toDate);
	
	/**
	 * method name : getDemandResponseHistoryTotalCount
	 * method Desc : 조회 조건으로 계약고객의 Demand Response History 전체 카운트를 조회한다.
	 *               페이징을 처리하기 위해 필요하다. 조회조건에 해당하는 카운트를 구해서 페이지별 쿼리를 수행하기 때문이다.
	 * 
	 * @param userId
	 * @param contractNumber 계약번호  Contract.contractNumber
	 * @param fromDate 조회시작날짜 yyyymmdd
	 * @param toDate 조회 마지막 날짜 yyyymmdd
	 * @return
	 */
	public String getDemandResponseHistoryTotalCount(String userId, 
														String contractNumber, 
														String fromDate, 
														String toDate);
	
	/**
	 * method name : getDemandResponseEventLogs
	 * method Desc : 조회 조건으로 계약고객의 Demand Response History를 조회한다.
	 *               
	 * @param drEventLog DemandResponseEventLog
	 * @return
	 */
	public List<DemandResponseEventLog> getDemandResponseEventLogs (DemandResponseEventLog drEventLog);
}
