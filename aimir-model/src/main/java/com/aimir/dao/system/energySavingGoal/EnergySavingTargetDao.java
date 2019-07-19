/**
 * EnergySavingTargetDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.energySavingGoal;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.EnergySavingTarget;

/**
 * EnergySavingTargetDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 9.   v1.0       김상연         에너지 절감 목표
 *
 */

public interface EnergySavingTargetDao extends GenericDao<EnergySavingTarget, Integer> {

	/**
	 * method name : getEnergySavingTarget
	 * method Desc : EnergySavingTarget 조회 (조건 : EnergySavingTarget, fromDay, toDay)
	 *
	 * @param energySavingTarget
	 * @param toDay 
	 * @param fromDay 
	 * @return
	 */
	List<EnergySavingTarget> getEnergySavingTarget(EnergySavingTarget energySavingTarget, String fromDay, String toDay);
	String getEnergySavingStartDate(int id);
	
	/**
	 * method name : deleteByOperatorContractId
	 * method Desc : OperatorContract Id에 해당하는 정보 삭제
	 *
	 * @param operatorContractId
	 */
	public void deleteByOperatorContractId(int operatorContractId);

}
