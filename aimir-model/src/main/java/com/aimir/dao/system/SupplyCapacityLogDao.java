package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.SupplyCapacityLog;

public interface SupplyCapacityLogDao extends GenericDao<SupplyCapacityLog, Long>{
	
	/**
	 * method name : supplyCapacityLogDelete
	 * method Desc : supplytypeid에 해당하는 로그를 삭제한다.
	 * 
	 * @param supplyTypeId SupplyType.id
	 */
	public void supplyCapacityLogDelete(int supplyTypeId);	
	
	/**
	 * method name : getSupplyCapacityLogs
	 * method Desc : 페이지 단위로 SupplyCapacityLog 목록을 리턴한다.
	 * 
	 * @param page pageNumber
	 * @param count maximum count
	 * @return List of SupplyCapacityLog @see com.aimir.model.system.SupplyCapacityLog
	 */
	public List<SupplyCapacityLog> getSupplyCapacityLogs(int page, int count);
	
}
