package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffEM;
import com.aimir.model.vo.TariffEMVO;

public interface TariffEMDao extends GenericDao<TariffEM, Integer> {
	
	/**
     * method name : getYyyymmddList
     * method Desc : TariffEM 정보에서 날짜를 순서대로 order by 해서 날짜 목록을 리턴
     * 
	 * @return List Of Object {yyyymmdd}
	 */
	public List<Object> getYyyymmddList(Integer supplierId);
	
	/**
     * method name : getChargeMgmtList
     * method Desc : 해당 날짜에 해당하는 TariffEM 목록을 리턴한다.
     * 
	 * @param condition
	 * <ul>
	 * <li> yyyymmdd : yyyyMMdd
	 * </ul>
	 * @return List Of Map<String Object>
	 */
	public List<Map<String, Object>> getChargeMgmtList(Map<String, Object> condition);
	
	/**
     * method name : getCustomerChargeMgmtList
     * method Desc : 고객별 과금 정보 목록을 조회한다.
     * 
	 * @param condition
	 * <ul>
	 * <li> yyyymmdd : yyyyMMdd
	 * <li> sUserId : Operator.id
	 * </ul>
	 * @return List of TariffEMVO @see  com.aimir.model.vo.TariffEMVO
	 */
	public List<TariffEMVO> getCustomerChargeMgmtList(Map<String, Object> condition);
	
	/**
     * method name : updateData
     * method Desc : 과금 정보를 업데이트한다.
     * 
	 * @param tariffEM
	 * @return
	 * @throws Exception
	 */
	public int updateData(TariffEM tariffEM) throws Exception;
	
	/**
	 * method name : tariffDeleteByCondition
	 * method Desc : 조건을 받아서 tariffEM 테이블의 정보를 삭제한다.
	 * @return
	 */
	public int tariffDeleteByCondition(Map<String, Object> condition);


	/**
     * method name : getUsageChargeByContract
     * method Desc : 계약애 해당하는 사용량에 따른 사용요금을 계산한다.
	 * 
	 * @param condition
	 * <ul>
	 * <li> contractId : 계약ID
	 * <li> dateType : 조회기간구분(일/월) ,CommonConstants.DateType
	 * <li> startDate : yyyyMMdd or yyyyMM
	 * <li> endDate : yyyyMMdd or yyyyMM
	 * </ul>
	 * @return 요금합계
	 */
	public Double getUsageChargeByContract(Map<String, Object> params);

    /**
     * method name : getPrepaymentTariff
     * method Desc : 고객 선불관리 화면에서 요금단가를 조회한다.(SPASA에서만 적용)
     *
     * @param conditionMap 조회조건 사용 안함
     * @return List Of Map {	
     * 					TariffEM.supplySizeMin AS supplySizeMin,
     * 					TariffEM.supplySizeMax AS supplySizeMax,
     * 					TariffEM.condition1 AS condition1,
     * 					TariffEM.condition2 AS condition2,
     * 					TariffEM.serviceCharge AS serviceCharge,
     * 					TariffEM.transmissionNetworkCharge AS transmissionNetworkCharge,
     * 					TariffEM.energyDemandCharge AS energyDemandCharge,
     * 					TariffEM.rateRebalancingLevy AS rateRebalancingLevy
     * 				}
     * 
     */
    public List<Map<String, Object>> getPrepaymentTariff(Map<String, Object> conditionMap);
 
    /**
     * method name : getApplyedTariff
     * method Desc : 해당 날짜 구간과 계약종별에 속하는 TariffEM 목록 리턴
     * 
     * @param condition
	 * <ul>
	 * <li> searchDate : yyyyMMdd
	 * <li> tariffIndex : TariffType
	 * </ul>
	 * 
     * @return  List Of TariffEM @see com.aimir.model.system.TariffEM
     */
    public List<TariffEM> getApplyedTariff(Map<String, Object> condition);
    
    /**
     * method name : saveEmBillingDayWithTariffEM
     * method Desc : 일별 전기 사용량과 요금표를 가지고 빌링테이블의 요금 정보를 갱신한다.
     *
     * @param contract 계약 정보
     * @return 과금
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    public Double saveEmBillingDailyWithTariffEM(Contract contract);
    */
    /**
     * method name : saveEmBillingMonthWithTariffEM
     * method Desc : 월별 전기 사용량과 요금표를 가지고 빌링테이블의 요금 정보를 갱신한다.
     * 
     * @param contract  계약 정보
     * @return 과금
     */
    /*
     *  선불계산로직은 스케줄러로 분리
    public Double saveEmBillingMonthWithTariffEM(Contract contract);
    */
    /**
     * method name : getTariffIndexId
     * method Desc : 계약정보로 최신의 TariffEM 리스트를 구한다.
     * 
     * @param contract
     * @return
     */
    public List<TariffEM> getNewestTariff(Contract contract);
    
    /**
     * @MethodName getAppliedTariffDate
     * @Date 2013. 12. 27.
     * @param date(yyyymmdd)
     * @return
     * @Modified
     * @Description 입력한 날짜 값을 기준으로 적용할 tariff 날짜를 반환한다.
     */
    public String getAppliedTariffDate(String date, Integer supplierId);
    
    /**
     * @MethodName isNewDate
     * @Date 2013. 12. 30.
     * @param yyyymmdd
     * @return
     * @Modified
     * @Description 입력된 날짜가 applied date인 tariff_em이 있는지 반환한다.
     */
    public Boolean isNewDate(String yyyymmdd);
    
    /**
     * @MethodName getNewestTariff
     * @Date 2014. 1. 3.
     * @param contract
     * @param yyyymmdd
     * @return
     * @Modified
     * @Description
     */
    public List<TariffEM> getNewestTariff(Contract contract, String yyyymmdd);
    
    /**
     * @MethodName deleteYyyymmddTariff
     * @Date 2014. 1. 3.
     * @param yyyymmdd
     * @return
     * @Modified
     * @Description 도입일이 yyyymmdd인 tariff_em인 항목 삭제
     */
    public Boolean deleteYyyymmddTariff(String yyyymmdd);

    /**
     * method name : getTariffSupplySizeComboData<b/>
     * method Desc : Consumption Ranking 가젯에서 선택한 TariffType 에 해당하는 SupplySize ComboData 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getTariffSupplySizeComboData(Map<String, Object> conditionMap);
}