/**
 * EnergyConsumptionMonitoringManager.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.energyConsumptionMonitoring;

import java.util.Map;

import com.aimir.model.system.Contract;

/**
 * EnergyConsumptionMonitoringManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 30.   v1.0       김상연         
 *
 */

public interface EnergyConsumptionMonitoringManager {

	/**
	 * method name : getLast
	 * method Desc : 최근 데이터 조회
	 *
	 * @param contract
	 * @return
	 */
	Map<String, Object> getLast(Contract contract);
	
	/**
	 * method name : getFirst
	 * method Desc : 처음 데이터 조회
	 *
	 * @param contract
	 * @return
	 */
	Map<String, Object> getFirst(Contract contract);

	/**
	 * method name : getTotal
	 * method Desc : 최근 전체 데이터 조회
	 *
	 * @param contract
	 * @param lastBillDay
	 * @param periodDay
	 * @return
	 */
	Map<String, Object> getTotal(Contract contract, String lastBillDay, String periodDay);
	
	public  Map<String, Object>  getBeforeDayUsageInfo(Contract contract, String date);
	public  Map<String, Object>  getBeforeMonthUsageInfo(Contract contract, String date);
	public Map<String, Object> getSelDate(Contract contract, String selDate);
	public Double getMaxBill(Contract contract, String yyyymmdd);

}
