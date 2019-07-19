package com.aimir.dao.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.util.Condition;

/**
 * @author MieUn
 *
 */
public interface PrepaymentLogDao extends GenericDao<PrepaymentLog, Long> {

	/**
     * method name : getPrepaymentLogCountByListCondition
     * method Desc : 조회조건을 만족하는 PrepaymentLog 데이터의 카운트를 리턴
     * 
	 * @param set
	 * @return count data
	 */
	public List<Object> getPrepaymentLogCountByListCondition(Set<Condition> set);
	
	/**
     * method name : getPrepaymentLogByListCondition
     * method Desc : 조회조건을 만족하는 PrepaymentLog 목록을 리턴
     * 
	 * @param set
	 * @return List of PrepaymentLog @see com.aimir.model.system.PrepaymentLog
	 */
	public List<PrepaymentLog> getPrepaymentLogByListCondition(Set<Condition> set);

    /**
     * method name : getPrepaymentContractBalanceInfo
     * method Desc : 선불 웹서비스에서 잔액 충전 내역을 조회한다.
     * 
	 * @param conditionMap
	 * <ul>
	 * <li> contractNumber : Contract.contractNumber
	 * </ul>
     * @return List of Map { supplierName,
     *                       contractNumber,
     *                       mdsId,
     *                       currentCredit,
     *                       emergencyYn,
     *                       creditStatus,
     *                       switchStatus
     *                       lastTokenDate,
     *                       lastTokenId,
     *                       chargedCredit,
     *                       powerLimit,
     *                       keyType}
     *                       
     */
    public List<Map<String, Object>> getPrepaymentChargeHistoryList(Map<String, Object> conditionMap);

    /**
     * method name : getChargeInfo
     * method Desc : 고객 선불관리 화면의 충전 정보를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> contractNumber : Contract.contractNumber
	 * <li> serviceType : Prepayment.contract.serviceTypeCode.code
	 * </ul> 
	 * 
     * @return List of Map { lastTokenDate,
     *                       balance,
     *                       chargedCredit,
     *                       currentCredit,
     *                       emergencyCreditAutoChange,
     *                       creditType,
     *                       p.contract.emergencyCreditStartTime AS emergencyCreditStartTime,
     *                       p.contract.emergencyCreditMaxDuration AS emergencyCreditMaxDuration}
     *                       
     */
    public List<Map<String, Object>> getChargeInfo(Map<String, Object> conditionMap);

    /**
     * method name : getChargeHistory
     * method Desc : 고객 선불관리 화면의 충전 이력 리스트를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> contractNumber : Contract.contractNumber
	 * <li> searchStartMonth : start month yyyymm
	 * <li> searchEndMonth : end month yyyymm
	 * <li> page : page number
	 * <li> limit : page max count
	 * </ul> 
     * @param isTotal total count 여부
     * 
     * @return List of Map if isCount is true then return {total,cnt}
     *                     else return 
     *                     { lastTokenDate,
     *                       balance,
     *                       chargedCredit,
     *                       currentCredit,
     *                       keyNum,
     *                       payment,
     *                       lastTokenId,
     *                       authCode,
     *                       municipalityCode}
     */
    public List<Map<String, Object>> getChargeHistory(Map<String, Object> conditionMap, boolean isCount);

    /**
     * method name : getChargeHistoryByMaxUnderDate
     * method Desc : 고객 선불관리 화면의 충전 이력 리스트 이전 데이터를 조회한다.(계산용)
     *
     * @param conditionMap
	 * <ul>
	 * <li> contractNumber : Contract.contractNumber
	 * <li> searchStartMonth : start month yyyymm
	 * </ul>
	 * 
     * @return List of Map { lastTokenDate,
     *                       balance,
     *                       chargedCredit,
     *                       currentCredit,
     *                       contractId}
     */
    public List<Map<String, Object>> getChargeHistoryByMaxUnderDate(Map<String, Object> conditionMap);
    
    /**
     * method name : getChargeHistoryForCustomer
     * method Desc : 고객관리 가젯에서 선불고객의 잔액 충전 이력 정보 취득
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryForCustomer(Map<String, Object> conditionMap, boolean isCount);
    
    /**
     * method name : getChargeHistoryByLastTokenDate
     * method Desc : IHD Data관련, 계약번호와 마지막 충전 날짜로 선불데이터 조회
     *
     * @param conditionMap(contractNumber, lastTokenDate)
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryByLastTokenDate(Map<String, Object> conditionMap);

    /**
     * method name : getChargeHistoryList
     * method Desc : Prepayment Charge 가젯에서 충전 이력 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryList(Map<String, Object> conditionMap, boolean isCount);

    /**
     * @MethodName getMonthlyPaidAmount
     * @Date 2013. 10. 28.
     * @param contract
     * @param yyyymm
     * @return
     * @Modified
     * @Description 월간 사용료로 결제된 결제된 금액 
     */
    public Double getMonthlyPaidAmount(Contract contract, String yyyymm);    
    
	/**
	 * @MethodName getMonthlyConsumptionLog
	 * @Date 2013. 10. 31.
	 * @param yyyyMM
	 * @param tariffName
	 * @return
	 * @Modified
	 * @Description Contract 중 특정 tarrif에 대한 월간 정산 로그를 출력한다.
	 */
	public List<PrepaymentLog> getMonthlyConsumptionLog(String yyyyMM, String tariffName);
	
	public List<PrepaymentLog> getMonthlyConsumptionLogByGeocode(String yyyyMM, String tariffName, String geocode);
		

	/**
	 * @MethodName getMonthlyConsumptionLog
	 * @Date 2013. 11. 20.
	 * @param yyyyMM
	 * @param tariffName
	 * @param locationIds
	 * @return
	 * @Modified
	 * @Description Contract 중 특정 tarrif와 location에 대한 월간 정산 로그를 출력한다.
	 */
	public List<PrepaymentLog> getMonthlyConsumptionLog(String yyyyMM, String tariffName, List<Integer> locationIds);
	
	/**
	 * @MethodName getMonthlyReceiptLog
	 * @Date 2013. 11. 21.
	 * @param yyyyMM
	 * @return
	 * @Modified
	 * @Description ECGMonthlyBillingJob에서 발생하는 월간 정산 로그를 반환한다.
	 */
	public List<PrepaymentLog> getMonthlyReceiptLog(String yyyyMM);
	
	/**
	 * @MethodName getRecentPrepaymentLogId
	 * @Date 2014. 2. 5.
	 * @param contractNumber
	 * @return
	 * @Modified
	 * @Description 특정 계약에서 가장 최신 선불 결제 로그 id를 조회한다. 
	 */
	public Long getRecentPrepaymentLogId(String contractNumber);
	
	/**
	 * @MethodName getAddBalanceList
	 * @Date 2014. 4. 22.
	 * @param page
	 * @param limit
	 * @param searchDate
	 * @return
	 * @Modified
	 * @Description 특정 날짜에 대한 전체 선불 구매 내역 조회
	 */
	public Map<String, Object> getAddBalanceList(Integer page, Integer limit, String searchDate, String vendorId);
	
	/**
	 * @MethodName getPrepaymentLogList
	 * @Date 2014. 4. 29.
	 * @param contractId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @Modified
	 * @Description
	 */
	public List<Map<String, Object>> getPrepaymentLogList(Integer contractId, String startDate, String endDate, String vendorId);

	 /**
     * @MethodName getMonthlyCredit
     * @Description 현재 달의 사용금액을 조회
     */
	public List<PrepaymentLog> getMonthlyCredit(Map<String, Object> condition);
	
	/**
	 * @MethodName getMonthlyUsageByContract
	 * @Date 2014. 5. 16.
	 * @param contract
	 * @param yyyymm
	 * @return
	 * @Modified
	 * @Description 계약에 따른 월간 사용량 조회
	 */
	public Double getMonthlyUsageByContract(Contract contract, String yyyymm);

    /**
     * method name : checkMonthlyFirstReceipt<b/>
     * method Desc : 영수증 출력 시 해당 월의 첫 번째 선불결제인지 체크.
     *
     * @param conditionMap
     * @return
     */
    public Boolean checkMonthlyFirstReceipt(Map<String, Object> conditionMap);

    /**
     * method name : getMonthlyPaidData<b/>
     * method Desc : 월정산 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public PrepaymentLog getMonthlyPaidData(Map<String, Object> conditionMap);
    
    /**
     * method name : getMonthlyPaidDataCount<b/>
     * method Desc : 월정산 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public  List<PrepaymentLog> getMonthlyPaidDataCount(Map<String, Object> conditionMap);
    
    public List<PrepaymentLog> getMonthlyNotCalculationReceiptLog(String yyyyMM, String[] modelName);
    
    
    /**
     * @return next value for sequence
     */
    public Long getNextVal();
    
    /**
     * method name : getDoubleSalesList
     * method Desc : Vendor 충전 가젯에서 고객의 잔액을 충전했는데 같은 금액으로 두번 로그가 남는 리스트를 삭제하기 위해 두번이상 충전한 고객목록 검색.
     */
    public List<Map<String,Object>> getDoubleSalesList(String yyyymmdd);
}