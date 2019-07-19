package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffWM;
import com.aimir.model.vo.TariffWMVO;

public interface TariffWMDao extends GenericDao<TariffWM, Integer> {

    /**
     * method name : getYyyymmddList
     * method Desc : TariffGM 정보에서 날짜를 순서대로 order by 해서 날짜 목록을 리턴
     * 
     * @return List Of Object {yyyymmdd}
     */
    public List<Object> getYyyymmddList(Integer supplierId);
    
    /**
     * method name : getChargeMgmtList
     * method Desc : 해당 날짜에 해당하는 TariffWM 목록을 리턴한다.
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
     * @return List of TariffWMVO @see  com.aimir.model.vo.TariffWMVO
     */
    public List<TariffWMVO> getCustomerChargeMgmtList(Map<String, Object> condition);
    
    /**
     * method name : updateData
     * method Desc : 과금 정보를 업데이트한다.
     * 
     * @param tariffWM
     * @return
     * @throws Exception
     */
    public int updateData(TariffWM tariffWM) throws Exception;
    
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
     * method Desc : 해당 날짜 구간과 계약종별에 속하는 TariffWM 리턴
     * 
     * @param condition
     * <ul>
     * <li> searchDate : yyyyMMdd
     * <li> tariffIndex : TariffType
     * </ul>
     * 
     * @return  @see com.aimir.model.system.TariffWM
     */
    public List<TariffWM> getApplyedTariff(Map<String, Object> params);
    
    /**
     * method name : saveWMChargeUsingDailyUsage
     * method Desc : 일별 수도 사용량과 요금표를 가지고 빌링테이블의 요금 정보를 갱신한다.
     *
     * @param contract 계약 정보
     * @return 과금
     */
    /*
     *  선불계산로직은 스케줄러로 분리
    public Double saveWMChargeUsingDailyUsage(Contract contract);
    */
    /**
     * method name : saveWMChargeUsingMonthlyUsage
     * method Desc : 월별 수도 사용량과 요금표를 가지고 빌링테이블의 요금 정보를 갱신한다.
     * 
     * @param contract  계약 정보
     * @return 과금
     */
    /*
     *  선불계산로직은 스케줄러로 분리
    public Double saveWMChargeUsingMonthlyUsage(Contract contract);
    */
    /**
     * method name : getTariffIndexId 
     * method Desc : 계약정보로 최신의 TariffWM 리스트를 구한다.
     * 
     * @param contract
     * @return
     */
    public List<TariffWM> getNewestTariff(Contract contract);
    
    /**
     * 2016. 02. 주어진 applied date에 해당하는 tariff_em이 있는지 여부 조회
     * @param yyyymmdd : 확인할 적용날짜
     */
    public Boolean isNewDate(String yyyymmdd);
    
    /**
     * 2016. 02. 적용날짜가 yyyymmdd인 tariff_wm 항목을 모두 삭제한다.
     * @param yyyymmdd : 삭제할 적용날짜
     */
    public Boolean deleteYyyymmddTariff(String yyyymmdd);
    
    /**
     * 조건을 받아서 tariff_wm 테이블의 정보를 삭제한다. 
     * @param condition : tariff_wm.id, tarifftype.id 
     * @return
     */
    public int tariffDeleteByCondition(Map<String,Object> condition);
}