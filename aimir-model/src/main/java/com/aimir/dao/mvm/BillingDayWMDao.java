/**
 * BillingDayWMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.system.Contract;

/**
 * BillingDayWMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 17.   v1.0       김상연        유효 데이터 최신 날짜 조회
 * 2011. 5. 17.   v1.1       김상연        BillingDayWM 조회 - 조건(BillingDayWM, 시작일, 종료일)
 * 2011. 5. 17.   v1.2       김상연        계약 회사 평균 조회
 * 2011. 5. 31.   v1.3       김상연        최근 데이터 조회
 * 2011. 5. 31.   v1.4       김상연        최근 전체 데이터 조회
 *
 */
public interface BillingDayWMDao extends GenericDao<BillingDayWM, Integer>{

	/**
	 * method name : getMaxDay
	 * method Desc : 유효 데이터 최신 날짜 조회
	 *
	 * @param contract
	 * @return
	 */
	String getMaxDay(Contract contract);

	/**
	 * method name : getBillingDayWMs
	 * method Desc : BillingDayWM 조회 - 조건(BillingDayWM, 시작일, 종료일)
	 *
	 * @param billingDayWM
	 * @param startDay
	 * @param finishDay
	 * @return
	 */
	List<BillingDayWM> getBillingDayWMs(BillingDayWM billingDayWM, String startDay, String finishDay);

	/**
	 * method name : getAverageUsage
	 * method Desc : 계약 회사 평균 조회
	 *
	 * @param billing
	 * @return
	 */
	Double getAverageUsage(BillingDayWM billingDayWM);

	/**
	 * method name : getLast
	 * method Desc : 최근 데이터 조회
	 *
	 * @param id
	 */
	Map<String, Object> getLast(Integer id);

	/**
	 * method name : getFirst
	 * method Desc : 처음 데이터 조회
	 *
	 * @param id
	 * @return
	 */
	Map<String, Object> getFirst(Integer id);
	/**
	 * method name : getTotal
	 * method Desc : 최근 전체 데이터 조회
	 *
	 * @param id
	 * @param lastBillDay
	 * @param periodDay
	 */
	Map<String, Object> getTotal(Integer id, String lastBillDay, String periodDay);

	/**
	 * method name : getBillingDayWMsAvg
	 * method Desc : 같은 집단의 평균 일 요금 취득
	 *
	 * @param billingDayWM
	 * @return
	 */
	public List<Object> getBillingDayWMsAvg(BillingDayWM billingDayWM);
	/**
	 * method name : getSelDate
	 * method Desc : 지정한 날짜의 빌링 정보 취득
	 *
	 * @param id
	 * @param selDate
	 * @return
	 */
	public Map<String, Object> getSelDate(Integer id, String selDate);
	
	/**
	 * method name : getMaxBill
	 * method Desc : 지정한 날짜의 최고 요금 취득
	 *
	 * @param contract
	 * @param yyyymmdd
	 * @return
	 */
	public Double getMaxBill(Contract contract, String yyyymmdd);

    public Double getAverageBill(Contract contract, String yyyymmdd);

    /**
     * method name : getChargeHistoryBillingList
     * method Desc : 고객 선불관리 화면의 충전 이력 사용전력량을 조회한다.(계산용)
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryBillingList(Map<String, Object> conditionMap);
    public List<Map<String, Object>> getDayBillingInfoDataBySupplierBillDate(int serviceTypeId, int creditType);
    public void updateBillingSendResultFlag(boolean sendResultFlag, int contractId, String yyyymmdd);
    public boolean hasActiveTotalEnergyByBillDate(int contractId, String yyyymmdd);
    public List<Map<String, Object>> getReDeliveryDayBillingInfoData(int contractId, String yyyymmdd);
    public List<Map<String, Object>> getNotDeliveryDayBillingInfoDataBySupplierBillDate(int serviceType, int creditType);
}