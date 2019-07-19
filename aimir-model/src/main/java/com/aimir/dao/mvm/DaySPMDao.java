package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.DaySPM;
import com.aimir.util.Condition;

public interface DaySPMDao extends GenericDao<DaySPM, Integer> {
	
	/**
	 * 해당 Entity의 총 개수를 주어진 condition으로 검색하고 결과를 long 으로 반환한다.
	 * 
	 * @param condition 검색 조건
	 * @return 총 행수
	 */
	public long totalByConditions(final Set<Condition>condition);
	
	/**
	 * totalByConditions 과 같지만 반환형은 List<Object> 형식.
	 *  
	 * @param conditions 검색 조건
	 * @return 총 행수를 포함한 리스트 객체
	 */
	public List<Object> getDaySPMsCountByListCondition(Set<Condition> conditions);
	
	/**
	 * AstractHibernateGenericDao의 findByConditions를 호출한 결과를 반환.
	 * 
	 * @see AbstractHibernateGenericDao::findByConditions
	 * @param conditions 검색 조건
	 * @return DaySPM 리스트
	 */
	public List<DaySPM> getDaySPMsByListCondition(final Set<Condition> conditions);
	
	/**
	 * 발전량의 총 합을 구한다.
	 * 
	 * @param conditions 조건
	 * @return 발전량의 총 합
	 */
	public double getSumTotalUsageByCondition(Set<Condition> conditions);
	
	/**
	 * 시간별 발전량의 총 합을 구한다.
	 * 
	 * @param conditions 조건
	 * @return 시간별 발전량의 총 합의 리스트
	 */
	public Map<String, Double> getSumUsageByCondition(Set<Condition> conditions);
	
	public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> conditions, DateType weekly);
	
}
