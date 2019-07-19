/**
 * EnergyConsumptionSearchManager.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.energyConsumptionSearch;

import java.util.List;
import java.util.Map;

import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.BillingDayGM;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.OperatorContract;

/**
 * EnergyConsumptionSearchManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 26.   v1.0       김상연         전기 사용량 조회
 * 2011. 5. 17.   v1.1       김상연         가스,수도 사용량 조회
 * 2011. 6. 24.   v1.2       김상연         사용량 -> 비용, 목표 비용 설정
 *
 */

public interface EnergyConsumptionSearchManager {

	/**
	 * method name : getMaxDay
	 * method Desc : 해당 계약 최종 일자 조회
	 *
	 * @param contract
	 * @return
	 */
	public String getMaxDay(Contract contract);

	/**
	 * method name : getBillingDayEm
	 * method Desc : 해당 계약의 특정 일자 별 조회
	 *
	 * @param contract
	 * @param someDay
	 * @return
	 */
	public BillingDayEM getBillingDayEm(Contract contract, String someDay);
	
	/**
	 * method name : getMonthUsage
	 * method Desc : 해당 계약의 특정 월 별 사용량 조회
	 *
	 * @param contractId
	 * @param someDay
	 * @return
	 */
	public BillingMonthEM getMonthUsage(Contract contract, String someDay);

	/**
	 * method name : getPeriodUsage
	 * method Desc : 해당 계약의 특정 기간 사용량 조회
	 * @param basicDay 
	 * @param lastDay 
	 * @param contract 
	 *
	 * @return
	 */
	public Double getPeriodUsage(Contract contract, String lastDay, String basicDay);

	/**
	 * method name : getAverageUsage
	 * method Desc : 해당 계약 관련 공급사 해당 월 평균 사용량 조회
	 *
	 * @param contract
	 * @param basicDay
	 * @return
	 */
	public Double getAverageUsage(Contract contract, String basicDay);

	/**
	 * method name : getPeriodTimeUsage
	 * method Desc : 기간 시간별 사용량 조회
	 *
	 * @param contract
	 * @param basicDay
	 * @param channel 
	 * @param mdevType 
	 */
	public List<MeteringDay> getPeriodTimeUsage(Contract contract, String basicDay, int channel, String mdevType);

	/**
	 * method name : getPeriodDayUsageEm
	 * method Desc : 전기 기간 일별 사용량 조회
	 *
	 * @param contractId
	 * @param basicDay
	 * @return
	 */
	public List<BillingDayEM> getPeriodDayUsageEm(Contract contract, String someMonth);

	/**
	 * method name : getPeriodMonthUsageEm
	 * method Desc : 전기 기간 월별 사용량 조회
	 *
	 * @param contractId
	 * @param basicDay
	 * @return
	 */
	public List<BillingMonthEM> getPeriodMonthUsageEm(Contract contract, String someYear);

	/**
	 * method name : getBillingMonthEm
	 * method Desc : 해당 계약의 특정 월 별 조회
	 *
	 * @param contract
	 * @param someMonth
	 * @return
	 */
	public BillingMonthEM getBillingMonthEm(Contract contract, String someMonth);

	/**
	 * method name : getBillingYear
	 * method Desc : 해당 계약의 특정 년 별 조회
	 *
	 * @param contract
	 * @param lastYear
	 * @return
	 */
	public Map<String, Object> getBillingYear(Contract contract, String someYear);

	/**
	 * method name : getDeviceSpecificTimeChart1
	 * method Desc : DayEM 조회 - 조건(DayEM)
	 *
	 * @param contractId
	 * @param basicDay
	 * @param channel
	 * @param mdevType
	 * @return
	 */
	public List<DayEM> getDayEMs(DayEM dayEM);

	/**
	 * method name : getMonthEMs
	 * method Desc : MonthEM 조회 - 조건(MonthEM)
	 *
	 * @param monthEM
	 * @return
	 */
	public List<MonthEM> getMonthEMs(MonthEM monthEM);

	/**
	 * method name : getSumMonthEMs
	 * method Desc : MonthEM 합계 조회 - 조건(MonthEM)
	 *
	 * @param monthEM
	 * @return
	 */
	public List<Map<String, Object>> getSumMonthEMs(MonthEM monthEM);

	/**
	 * method name : getCompareBill
	 * method Desc : 전년도 동일/월 요금, 지난 사용 요금, 기준 사용 요금
	 *
	 * @param contractId
	 * @param someDay
	 * @param lastDay
	 * @param lastYearDay
	 * @return
	 */
	public List<Object> getCompareBill(int contractId, String someDay, String lastDay, String lastYearDay);

	/**
	 * method name : getDeviceSpecificGrid
	 * method Desc : 기기별 그리드 조회
	 *
	 * @param basicDay
	 * @param contractId
	 * @return
	 */
	public List<Map<String, Object>> getDeviceSpecificGrid(String basicDay, int contractId);

	/**
	 * method name : getBillingDayGm
	 * method Desc : 가스 해당 계약의 특정 일 조회
	 *
	 * @param contract
	 * @param maxDay
	 * @return
	 */
	public BillingDayGM getBillingDayGm(Contract contract, String someDay);

	/**
	 * method name : getBillingDayWm
	 * method Desc : 수도 해당 계약의 특정 일 조회
	 *
	 * @param contract
	 * @param someDay
	 * @return
	 */
	public BillingDayWM getBillingDayWm(Contract contract, String someDay);

	/**
	 * method name : getBillingMonthGm
	 * method Desc : 가스 해당 계약의 특정 월 조회
	 *
	 * @param contract
	 * @param basicDay
	 * @return
	 */
	public BillingMonthGM getBillingMonthGm(Contract contract, String basicDay);

	/**
	 * method name : getBillingMonthWm
	 * method Desc : 수도 해당 계약의 특정 월 조회
	 *
	 * @param contract
	 * @param basicDay
	 * @return
	 */
	public BillingMonthWM getBillingMonthWm(Contract contract, String basicDay);

	/**
	 * method name : getPeriodDayUsageGm
	 * method Desc : 가스 기간 일별 사용량 조회
	 *
	 * @param contract
	 * @param lastMonth
	 * @return
	 */
	public List<BillingDayGM> getPeriodDayUsageGm(Contract contract, String someMonth);

	/**
	 * method name : getPeriodDayUsageWm
	 * method Desc : 수도 기간 일별 사용량 조회
	 *
	 * @param contract
	 * @param lastMonth
	 * @return
	 */
	public List<BillingDayWM> getPeriodDayUsageWm(Contract contract, String someMonth);

	/**
	 * method name : getPeriodMonthUsageGm
	 * method Desc : 가스 기간 월별 사용량 조회
	 *
	 * @param contract
	 * @param someYear
	 * @return
	 */
	public List<BillingMonthGM> getPeriodMonthUsageGm(Contract contract, String someYear);

	/**
	 * method name : getPeriodMonthUsageWm
	 * method Desc : 수도 기간 월별 사용량 조회
	 *
	 * @param contract
	 * @param someYear
	 * @return
	 */
	public List<BillingMonthWM> getPeriodMonthUsageWm(Contract contract, String someYear);

	/**
	 * method name : getMonthBill
	 * method Desc : 평균 사용비용, 지난 사용비용, 기준 사용비용 조회
	 *
	 * @param contract
	 * @param startDay
	 * @param finishDay
	 * @return
	 */
	public List<Object> getMonthBill(Contract contract, String basicDay);

	/**
	 * method name : getSavingTarget
	 * method Desc : 목표 비용 조회
	 *
	 * @param operatorContract
	 * @param basicDay
	 * @return
	 */
	public Double getSavingTarget(OperatorContract operatorContract, String fromDay, String toDay);

//	public List<Object> getPeriodTimeAvgUsage(Contract contract, String basicDay, int channel, String mdevType);
	
	public Double getPeriodTimeAvgUsage(Contract contract, String basicDay, int channel, String mdevType);
	public List<Object> getPeriodDayAvgUsageEm(Contract contract, String someMonth);
	public List<Object> getPeriodDayAvgUsageGm(Contract contract, String someMonth);
	public List<Object> getPeriodDayAvgUsageWm(Contract contract, String someMonth);

	public List<Object> getPeriodMonthAvgUsageGm(Contract contract, String someYear);
	public List<Object> getPeriodMonthAvgUsageWm(Contract contract, String someYear);
	public List<Object> getPeriodMonthAvgUsageEm(Contract contract, String someYear);

	public Double getPeriodDayAvgBillEm(Contract contract, String someMonth);
	public Double getPeriodDayAvgBillGm(Contract contract, String someMonth);
	public Double getPeriodDayAvgBillWm(Contract contract, String someMonth);
	
	public Double getPeriodMonthAvgBillEm(Contract contract, String someMonth);
	public Double getPeriodMonthAvgBillGm(Contract contract, String someMonth);
	public Double getPeriodMonthAvgBillWm(Contract contract, String someMonth);
}
