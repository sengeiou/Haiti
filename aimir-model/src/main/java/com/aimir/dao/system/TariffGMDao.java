package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffGM;
import com.aimir.model.vo.TariffGMVO;

public interface TariffGMDao extends GenericDao<TariffGM, Integer> {

	/**
     * method name : getYyyymmddList
     * method Desc : TariffGM 정보에서 날짜를 순서대로 order by 해서 날짜 목록을 리턴
     * 
	 * @return List Of Object {yyyymmdd}
	 */
	public List<Object> getYyyymmddList(Integer supplierId);
	
	/**
     * method name : getChargeMgmtList
     * method Desc : 해당 날짜에 해당하는 TariffGM 목록을 리턴한다.
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
	 * @return List of TariffGMVO @see  com.aimir.model.vo.TariffGMVO
	 */
	public List<TariffGMVO> getCustomerChargeMgmtList(Map<String, Object> condition);
	
	
	/**
     * method name : updateData
     * method Desc : 과금 정보를 업데이트한다.
     * 
	 * @param tariffGM
	 * @return
	 * @throws Exception
	 */
	public int updateData(TariffGM tariffGM) throws Exception;

	/**
	 * @deprecated
	 * method name : getUsageCharge
	 * method Desc : 
	 * 
	 * @param condition
	 * 
	 * Parameters : 
	 * Contract contract (tariffType, contractId)
	 * Meter meter(mdsId)
	 * Double usage
	 * yyyymmdd
	 * season
	 * @return
	 * @throws Exception
	 */
	public Double getUsageCharge(Map<String, Object> condition) throws Exception;

	/**
	 * @deprecated
	 * method name : getUsageCharges
	 * method Desc : 
	 * 
	 * @param condition
	 * Parameters : 
	 * Contract contract (tariffType, contractId)
	 * Meter meter(mdsId)
	 * Double usage
	 * yyyymmdd
	 * season
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Object> getUsageCharges(Map<String, Object> condition) throws Exception;
	
	/**
	 * method name : getUsageChargeByContract
	 * method Desc : 
	 * 
	 * 계약애 해당하는 사용량에 따른 사용요금을 계산한다.
	 * @param condition
	 * <ul>
	 * <li> Contract : 계약
	 * <li> dateType : 조회기간구분(일/월) ,CommonConstants.DateType
	 * <li> startDate : yyyyMMdd or yyyyMM
	 * <li> endDate : yyyyMMdd or yyyyMM
	 * </ul>
	 * @return
	 */
	public Double getUsageChargeByContract(Map<String, Object> params);
	
    /**
     * method name : getApplyedTariff
     * method Desc : 해당 날짜 구간과 계약종별에 속하는 TariffGM 리턴
     * 
     * @param condition
	 * <ul>
	 * <li> searchDate : yyyyMMdd
	 * <li> tariffIndex : TariffType
	 * </ul>
	 * 
     * @return  @see com.aimir.model.system.TariffGM
     */
	public TariffGM getApplyedTariff(Map<String, Object> params);
	
    /**
     * method name : saveGmBillingDayWithTariffGM
     * method Desc : 일별 가스 사용량과 요금표를 가지고 빌링테이블의 요금 정보를 갱신한다.
     *
     * @param contract 계약 정보
     * @return 과금
     */
	/*
	 *  선불계산로직은 스케줄러로 분리
	public Double saveGmBillingDayWithTariffGM(Contract contract);
	*/
    /**
     * method name : saveGmBillingMonthWithTariffGM
     * method Desc : 월별 가스 사용량과 요금표를 가지고 빌링테이블의 요금 정보를 갱신한다.
     * 
     * @param contract  계약 정보
     * @return 과금
     */
	/*
	 *  선불계산로직은 스케줄러로 분리
	public Double saveGmBillingMonthWithTariffGM(Contract contract);
	*/
}