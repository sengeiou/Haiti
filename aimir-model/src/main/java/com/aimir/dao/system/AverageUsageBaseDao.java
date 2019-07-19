package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.AverageUsageBase;
import com.aimir.model.system.AverageUsageBasePk;

public interface AverageUsageBaseDao extends GenericDao<AverageUsageBase, AverageUsageBasePk> {
	
	/**
	 * startDate와 같거나  createDate 를 기준으로 가장 최근의 등록순으로 정렬된 리스트를 반환함.
	 * @param avgUsageId
	 * @param supplyType
	 * @param UsageYear
	 * @return
	 */
	public List<AverageUsageBase> getAverageUsageBaseListBystartDate( Integer avgUsageId  , Integer supplyType , String UsageYear );

	public int deleteAvgUsageId( AverageUsageBase averageUsageBase );
	
	/**
	 * 평균에 추출에 사용된 년도를 조회
	 * @param avgUsageId
	 * @return
	 */
	public List<AverageUsageBase> getSetYearsbyId(Integer avgUsageId);
}
