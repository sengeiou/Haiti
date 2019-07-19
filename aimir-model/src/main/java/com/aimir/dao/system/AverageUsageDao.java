package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.AverageUsage;

public interface AverageUsageDao extends GenericDao<AverageUsage, Integer> {
	
	/**
     * method name : getAverageUsageByUsed
     * method Desc : 사용유무가 true 인것들중에 가장 최근의 등록순으로 정렬하여 하나만 가져온다.
	 * 
	 * @param startDate
	 * @param supplierId
	 * @return @see com.aimir.model.system.AverageUsage
	 */
	public AverageUsage getAverageUsageByUsed();

	/**
     * method name : usageInitSql
     * method Desc : usage 정보를 모두 flase로 초기화 한다. 
	 * 
	 * @param averageUsage
	 * @return
	 */
	public int usageInitSql( AverageUsage averageUsage );

	/**
     * method name : updateSql
     * method Desc : 직접 수정방식으로 update함.
	 * 
	 * @param averageUsage
	 * @return
	 */
	public int updateSql( AverageUsage averageUsage );	
}
