package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.EnergySavingGoal2;
import com.aimir.model.system.EnergySavingGoalPk2;

public interface EnergySavingGoal2Dao extends GenericDao<EnergySavingGoal2, EnergySavingGoalPk2> {
	
	/**
     * method name : getEnergySavingGoal2ListByStartDate
     * method Desc : startDate와 같거나  createDate 를 기준으로 가장 최근의 등록순으로 정렬된 리스트를 반환함.
     * 
	 * @param searchDateType CommonConstants.DateType.getCode()
	 * @param energyType supplyType
	 * @param startDate yyyymmdd
	 * @param supplierId Supplier.id
	 * 
	 * @return List of EnergySavingGoal2 @see com.aimir.model.system.EnergySavingGoal2
	 */
	public List<EnergySavingGoal2> getEnergySavingGoal2ListByStartDate( String searchDateType , String energyType ,  String startDate , Integer supplierId );

	/**
     * method name : getEnergySavingGoal2ListByAverageUsage
     * method Desc : startDate와 같거나  createDate 를 기준으로 가장 최근의 등록순으로 정렬된 리스트를 반환함.
     * 
	 * @param searchDateType CommonConstants.DateType.getCode()
	 * @param energyType supplyType
	 * @param startDate  yyyymmdd
	 * @param supplierId  Supplier.id
	 * @param averageUsageId AverageUsage.id
	 * 
	 * @return  List of EnergySavingGoal2 @see com.aimir.model.system.EnergySavingGoal2
	 */
	public List<EnergySavingGoal2> getEnergySavingGoal2ListByAverageUsage( String searchDateType , String energyType ,  String startDate , Integer supplierId , Integer averageUsageId );
	
	/**
     * method name : getEnergySavingGoal2ListByAvg
     * method Desc : 목표에 사용된 평균관리 정보 키를 적용하고있는 목표 내역 리스트 반환
	 * 
	 * @param supplierId Supplier.id
	 * @param energyType supplyType
	 * @param avgInfoId AverageUsage.id
	 * @param allView - no use
	 * @return List of EnergySavingGoal2 @see com.aimir.model.system.EnergySavingGoal2
	 */
	public List<EnergySavingGoal2> getEnergySavingGoal2ListByAvg(
			String supplierId, String energyType, String avgInfoId,
			String allView);
}
