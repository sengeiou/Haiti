/**
 * MonthEMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.GenericDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;


/**
 * MonthEMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 11.   v1.0       김상연         MonthEM 조회 - 조건(MonthEM)
 * 2011. 5. 11.   v1.1       김상연         MonthEM 합계 조회 - 조건(MonthEM)
 * 2011. 5. 25.   v1.2       김상연         기기별 그리드 조회
 *
 */
public interface MonthEMDao extends GenericDao<MonthEM, Integer>{
	
    public List<MonthEM> getMonthEMsByListCondition(Set<Condition> list);
    public List<Object> getMonthEMsCountByListCondition(Set<Condition> set);
    /**
     * 
     * method name : getMonthEMsByCondition<b/>
     * method Desc :ECG에서 선불계산에 필요한 4가지 채널의 결과를 조회하기 위한 쿼리
     *
     * @param condition
     * @return
     */
    public List<MonthEM> getMonthEMsByCondition(Map<String, Object> condition);
    public List<Object> getMonthEMsMaxMinAvgSum(Set<Condition> conditions, String div);
    
	public List<Object[]> getMonthBillingChartData(Map<String, String> conditionMap);
	public List<Object[]> getMonthBillingGridData(Map<String, String> conditionMap);
	public List<MonthEM> getMonthCustomerBillingGridData(Map<String, Object> conditionMap);
	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap);	

	public List<Object> getConsumptionTmHmMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2MonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2MonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2MonthMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2MonthMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2MonthSearchDayTypeTotal(Map<String, Object> conditionYear);
	public List<Object> getConsumptionEmCo2MonthSearchDayTypeTotal2(Map<String, Object> conditionYear);
	public List<Object> getCompareFacilityMonthData(Map<String, Object> condition);
	public List<Map<String, String>> getContractBillingChartData(Map<String, String> conditionMap);
	public List<Object> getSearchChartData(Set<Condition> conditions,int locationId, int endDeviceId);
	public List<Object> getConsumptionEmValueSum(int supplierId ,String startDate, String endDate,int startValue, int endValue);
	
	public List<Object> getMonthToYears();
	
	public List<Object> getEnergySavingReportMonthlyData(String[] years, int channel, Integer[] meterIds);
	public List<Object> getEnergySavingReportYearlyData(String[] years, int channel, Integer[] meterIds);
	
	/**
	 * method name : getMonthEMbySupplierId
	 * method Desc : 해당 공급사의 고객에 대한 MonthEM정보만 가져오는 조건
	 * 
	 * @param params
	 * @return
	 */
	public List<MonthEM> getMonthEMbySupplierId(Map<String,Object> params);
	
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
	 * method name : getDeviceSpecificGrid
	 * method Desc : 기기별 그리드 조회
	 *
	 * @param basicDay
	 * @param contractId
	 * @return
	 */
	public List<Map<String, Object>> getDeviceSpecificGrid(String basicDay, int contractId);
	
	public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType dateType);
	
	/**
	 * method name : getYearlyUsageTotal
	 * method Desc : 해당 년에 월별 사용량 조회
	 * @param yyyymm : 해당 년월
	 * @param locationId : 해당 년월
	 * @return 
	 */
	public List<Map<String, Object>> getYearlyUsageTotal( List<String> yyyymm, int locationId );
	
	/**
	 * method name : getMonthByMinDate
	 * method Desc : 해당 미터의 최초 MonthEM 값
	 * 
	 * @param mdevId
	 * 
	 */
	public List<Map<String, Object>> getMonthByMinDate(String mdevId);
	
	/**
	 * @MethodName getMonthlyUsage
	 * @Date 2013. 10. 28.
	 * @param contract
	 * @param yyyymm
	 * @param channels
	 * @return
	 * @Modified
	 * @Description 특정 contract, 채널에 대하여 월간 사용이력을 구한다.
	 */	
	public List<MonthEM> getMonthlyUsageByContract(Contract contract, String yyyymm, String channels);
	
	/**
	 * @Methodname getMonthlyUsageByMeter
	 * @Date 2013. 11. 29.
	 * @Author scmitar1
	 * @ModifiedDate 
	 * @Description 특정 contract, 채널에 대하여 월간 사용이력을 구한다.
	 * @param Meter
	 * @param yyyymm
	 * @param channels
	 * @return
	 */
	public List<MonthEM> getMonthEMByMeter(Meter meter, String yyyymm, Integer... channels);
	
	/**
	 * @Methodname getMonthlyAccumulatedUsageByMeter
	 * @Date 2013. 12. 2.
	 * @Author scmitar1
	 * @ModifiedDate 
	 * @Description 특정 contract, 채널에 대하여 월간 누적 사용량을 구한다.(값이 무효인 경우)
	 * @param Meter
	 * @param yyyymm
	 * @param channels
	 * @return
	 */
	public Map<String, Object> getMonthlyAccumulatedUsageByMeter(Meter meter, String yyyymm, Integer... channels);
	
	/**
	 * @Methodname getMonthEMsCount
	 * @Description 조건에 해당하는 MonthEM테이블의 데이터 개수를 반환한다.
	 * 
	 * @param conditions
	 * @param div
	 * @return
	 */
    public List<Object> getMonthEMsCount(Set<Condition> conditions, String div);
    
    /**
     * 해당 미터의 오래된(bDate보다 오래된)LP 삭제
     * @param mdsId
     * @param bDate
     */
	public void oldLPDelete(String mdsId, String bDate);
}
