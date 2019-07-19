package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.EnergySavingGoal;
import com.aimir.model.system.EnergySavingGoalPk;

public interface EnergySavingGoalDao extends GenericDao<EnergySavingGoal, EnergySavingGoalPk> {

	/**
     * method name : getEnergySavingGoalListBystartDate
     * method Desc : startDate와 같거나  createDate 를 기준으로 가장 최근의 등록순으로 정렬된 리스트를 반환함.
     * 
	 * @param startDate yyyymmdd
	 * @param supplierId Supplier.id
	 * @return List of EnergySavingGoal @see com.aimir.model.system.EnergySavingGoal
	 */
	public List<EnergySavingGoal> getEnergySavingGoalListBystartDate( String startDate , Integer supplierId );
}
