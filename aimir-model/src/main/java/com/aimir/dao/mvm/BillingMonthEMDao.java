/**
 * BillingMonthEMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.system.Contract;

/**
 * BillingMonthEMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 28.   v1.0       김상연         BillingMonthEM 조회 조건(BillingMonthEM)
 * 2011. 5. 04.   v1.1       김상연         해당 계약의 특정 년 별 조회
 * 2011. 5. 13.   v1.2       김상연        동일 공급사 평균 사용량 조회
 * 2011. 6. 09.   v1.3       김상연        해당 계약 최고 사용 금액 조회
 * 2011. 6. 27.   v1.4       김상연        사용 평균 비용 조회
 *
 */
public interface BillingMonthEMDao extends GenericDao<BillingMonthEM, Integer>{

    /**
     * 에너지 사용량 리포트 월간 TOU 전체건수
     * 
     * @param conditionMap
     * @return
     */
//    public Long getBillingDataMonthlyCount(Map<String, Object> conditionMap);

    /**
     * 에너지 사용량 리포트 월간 TOU 리스트
     * 
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getBillingDataMonthly(Map<String, Object> conditionMap, boolean isCount);

    /**
     * 에너지 사용량 리포트 월간 TOU 리포트 데이터
     * 
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getBillingDataReportMonthly(Map<String, Object> conditionMap);

    /**
     * method name : getBillingDataReportMonthlyWithLastMonth<b/>
     * method Desc : Billing Data 월별 TOU 리포트 데이터를 조회한다. (전월 데이터 조회 선택 시)<b/>
     *               전월 데이터와 같이 보여줄 경우 일자가 매칭이 되지 않으므로 월별로 합산해서 보여준다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getBillingDataReportMonthlyWithLastMonth(Map<String, Object> conditionMap);

    /**
     * Billing Data 월별 TOU 리포트 상세데이터 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    public List<Map<String, Object>> getBillingDetailDataMonthly(Map<String, Object> conditionMap);

    /**
     * method name : getBillingMonthEMs
     * method Desc : BillingMonthEM 조회 조건(BillingMonthEM)
     *
     * @param billingMonthEM
     * @param startDay
     * @param finishDay
     * @return
     */
    public List<BillingMonthEM> getBillingMonthEMs(BillingMonthEM billingMonthEM, String startDay, String finishDay);

	/**
	 * method name : getBillingYearEm
	 * method Desc : 해당 계약의 특정 년 별 조회
	 *
	 * @param billingMonthEM
	 * @return
	 */
	public Map<String, Object> getBillingYearEm(BillingMonthEM billingMonthEM);

	/**
	 * method name : getAverageUsage
	 * method Desc : 동일 공급사 평균 사용량 조회

	 *
	 * @param billingMonthEM
	 * @return
	 */
	public Double getAverageUsage(BillingMonthEM billingMonthEM);

	/**
	 * method name : getMaxBill
	 * method Desc : 해당 계약 최고 사용 금액 조회
	 *
	 * @param contract
	 * @param yyyymmdd
	 * @return
	 */
	public Double getMaxBill(Contract contract, String yyyymmdd);

	/**
	 * method name : getAverageBill
	 * method Desc : 사용 평균 비용 조회
	 *
	 * @param contract
	 * @param someDay
	 * @return
	 */
	public Double getAverageBill(Contract contract, String yyyymmdd);

	/**
	 * method name : getBillingMonthEMsAvg
	 * method Desc : 같은 지역의 평균 요금 정보 취득
	 *
	 * @param billingMonthEM
	 * @param fromDay
	 * @param toDay
	 * @return
	 */
	public List<Object> getBillingMonthEMsAvg(BillingMonthEM billingMonthEM, String fromDay, String toDay);
	public List<Object> getBillingMonthEMsComboBox(BillingMonthEM billingMonthEM, String fromDay, String toDay);
	
	/**
	 * 정해진 날짜의 PF, ACTIVEPWRDMDMAXIMPORTRATETOTAL, ATVPWRDMDMAXTIMEIMPRATETOT 값을 검색해온다.
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getCurrMonCummMaxDemandData(Map<String, Object> conditionMap);
	
	/**
	 * @Methodname getBillingMonthEM
	 * @Date 2014. 1. 6.
	 * @Author scmitar1
	 * @ModifiedDate 
	 * @Description 특정 기간에 특정 미터에 대한 BillingMonthEm을 반환한다.
	 * @param meter
	 * @param date
	 * @return
	 */
	public BillingMonthEM getBillingMonthEM(Meter meter, String date);
}
