/**
 * BillingDayEMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.system.Contract;

/**
 * BillingDayEMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 26.   v1.0       김상연         해당 계약 최종 일자 조회
 * 2011. 4. 28.   v1.1       김상연         BillingDayEM 조회 조건 (BillingDayEM, 시작일, 종료일) 
 * 2011. 4. 28.   v1.2       김상연        관련 범위  내 평균 사용량 조회
 * 2011. 4. 28.   v1.3       김상연        기간  내 사용량 조회
 * 2011. 5. 13.   v1.4       김상연        동일 공급사 평균 사용량 조회
 * 2011. 5. 31.   v1.5       김상연        최근 데이터 조회
 * 2011. 5. 31.   v1.6       김상연        최근 전체 데이터 조회
 *
 */
public interface BillingDayEMDao extends GenericDao<BillingDayEM, Integer>{

    /**
     * 에너지 사용량 리포트 일간 TOU 전체건수
     * 
     * @param conditionMap
     * @return
     */
//    public Long getBillingDataDailyCount(Map<String, Object> conditionMap);

    /**
     * 에너지 사용량 리포트 일간 TOU 리스트
     * 
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getBillingDataDaily(Map<String, Object> conditionMap, boolean isCount);

    /**
     * 에너지 사용량 리포트 일간 TOU 리포트 데이터
     * 
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getBillingDataReportDaily(Map<String, Object> conditionMap);

    /**
     * Billing Data 일별 TOU 리포트 상세데이터 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    public List<Map<String, Object>> getBillingDetailDataDaily(Map<String, Object> conditionMap);

	/**
	 * method name : getMaxDay
	 * method Desc : 해당 계약 최종 일자 조회
	 *
	 * @param contract
	 */
	public String getMaxDay(Contract contract);

	/**
	 * method name : getBillingDayEMs
	 * method Desc : BillingDayEM 조회 조건 (BillingDayEM, 시작일, 종료일) 
	 *
	 * @param billingDayEM
	 * @param startDay
	 * @param finishDay
	 * @return
	 */
	public List<BillingDayEM> getBillingDayEMs (BillingDayEM billingDayEM, String startDay, String finishDay);
	
	/**
	 * method name : getAverageUsage
	 * method Desc : 관련 범위  내 평균 사용량 조회
	 *
	 * @param contract
	 * @param startDay
	 * @param finishDay
	 * @return
	 */
	public Double getAverageUsage(Contract contract, String startDay, String finishDay);

	/**
	 * method name : getPeriodUsage
	 * method Desc : 기간  내 사용량 조회
	 *
	 * @param contract
	 * @param startDay
	 * @param finishDay
	 * @return
	 */
	public Double getPeriodUsage(Contract contract, String startDay, String finishDay);

	/**
	 * method name : getAverageUsage
	 * method Desc : 동일 공급사 평균 사용량 조회
	 *
	 * @param billingDayEM
	 * @return
	 */
	public Double getAverageUsage(BillingDayEM billingDayEM);

	/**
	 * method name : getLast
	 * method Desc : 최근 데이터 조회
	 *
	 * @param id
	 */
	public Map<String, Object> getLast(Integer id);
	
	/**
	 * method name : getFirst
	 * method Desc : 처음 데이터 조회
	 *
	 * @param id
	 * @return
	 */
	public Map<String, Object> getFirst(Integer id);

	/**
	 * method name : getTotal
	 * method Desc : 최근 전체 데이터 조회
	 *
	 * @param id
	 * @param lastBillDay
	 * @param periodDay
	 */
	public Map<String, Object> getTotal(Integer id, String lastBillDay, String periodDay);
	
	/**
	 * method name : getBillingDayEMsAvg
	 * method Desc : 같은 지역의 평균 요금을 취득한다.
	 *
	 * @param billingDayEM
	 * @return
	 */
	public List<Object> getBillingDayEMsAvg (BillingDayEM billingDayEM);

	/**
	 * method name : getSelDate
	 * method Desc : 지정한 일자에 해당하는 빌링 정보를 취득한다.
	 *
	 * @param id
	 * @param selDate
	 * @return
	 */
	public Map<String, Object> getSelDate(Integer id, String selDate);
	
	/**
	 * method name : getMaxBill
	 * method Desc : 지정한 일자의 MAX요금을 취득한다.
	 *
	 * @param contract
	 * @param yyyymmdd
	 * @return
	 */
	public Double getMaxBill(Contract contract, String yyyymmdd);

    /**
     * method name : getPrepaymentBillingDayList
     * method Desc : 잔액모니터링 스케줄러에서 조회하는 선불계약별 일별빌링 리스트
     *
     * @param contractId
     * @return
     */
    public List<BillingDayEM> getPrepaymentBillingDayList(Integer contractId);
    
    public Double getAverageBill(Contract contract, String yyyymmdd);

    /**
     * method name : getChargeHistoryBillingList
     * method Desc : 고객 선불관리 화면의 충전 이력 사용전력량을 조회한다.(계산용)
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryBillingList(Map<String, Object> conditionMap);
    public List<Map<String, Object>> getDayBillingInfoData();
    public List<Map<String, Object>> getNotDeliveryDayBillingInfoData();
    public List<Map<String, Object>> getReDeliveryDayBillingInfoData(int contractId, String yyyymmdd);
    public boolean hasActiveTotalEnergyByBillDate(int contractId, String yyyymmdd);
    public List<Map<String, Object>> getDayBillingInfoDataBySupplierBillDate(int serviceTypeId, int creditType);
    public List<Map<String, Object>> getNotDeliveryDayBillingInfoDataBySupplierBillDate(int serviceType, int creditType);
    public void updateBillingSendResultFlag(boolean sendResultFlag, int contractId, String yyyymmdd);
    
    /**
     * method name : getLastAccumulateBill
     * method Desc : 선불로직 구현시 마지막 누적액을 구하는데 쓰인다.
     * 
     * @param mdevId
     * @return
     */
    public List<Map<String, Object>> getLastAccumulateBill(String mdevId);
    
    /**
     * @MethodName getBillingDayEM
     * @Date 2014. 2. 6.
     * @param condition (String) mdsId, (String) yyyymmdd, (Integer) mdevTypeCode
     * @return
     * @Modified
     * @Description 기본 id 조건에 맞는 billingEM을 반환한다.
     */
    public BillingDayEM getBillingDayEM(Map<String, Object> condition);
}
