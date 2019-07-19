package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.SNRLog;

public interface SNRLogDao extends GenericDao<SNRLog, Integer> {

    /**
     * Method Desc : 기간 조건 없이 각 모뎀의 마지막 SNR값을 조회
     * 마지막 값과 이에 해당하는 통계값을 조합하여 Controller로 반환해야한다
     */
    public List<Object> getSnrWithoutPeriod(Map<String,Object> conditionMap, List deviceList);
    
    /**
     * Method Desc : 기간 조건 없이 각 모뎀의 마지막 SNR값의 통계값을 조회
     * getSnrWithPeriod와 함께 사용.
     */
    public List<Object> getSnrStatisticWithoutPeriod(Map<String,Object> conditionMap, List deviceList);
    
    /**
     * Method Desc : 주어진 기간(startdate, enddate)에서 각 모뎀의 마지막 SNR값을 조회
     * 마지막 값과 이에 해당하는 통계값을 조합하여 Controller로 반환해야한다
     */
    public List<Object> getSnrWithPeriod(Map<String,Object> conditionMap, List deviceList);
    
    /**
     * Method Desc : 주어진 startDate, endDate 범위에서 각 모뎀의 SNR값의 통계값을 조회
     * getSnrWithoutPeriod와 함께 사용.
     */
    public List<Object> getSnrStatisticWithPeriod(Map<String,Object> conditionMap, List deviceList);
    
    /**
     * Method Name : getLastSnrByMcu
     * Method Desc : 주어진 기간내에서 모뎀별로 가장 마지막에 올린 데이터를 조회
     * @param conditionMap
     */
    public List<Object> getLastSnrByMcu(Map<String,Object> conditionMap);
    
    /**
     * Method Desc : getLastSnrByMcu에서 날짜 조건을 무시하고 마지막 데이터를 조회
     * @param conditionMap
     */
    public List<Object> getFinalSnrByMcu(Map<String,Object> conditionMap);
    
    /**
     * Method Name : getStatisticsByMcu
     * Method Desc : 주어진 기간내에서 각 모뎀의 SNR 통계값을 조회
     * @param conditionMap
     */
    public List<Object> getStatisticsByMcu(Map<String,Object> conditionMap);
        
    /**
     * getFinalStatisticsByMcu 개선 - 각 모뎀의 마지막 일자를 대상으로 통계를 구함.
     * 모뎀별 마지막 일자를 구하고, 모뎀별로 통계구하여 묶음.
     * 집중기아이디, Poor신호, 모뎀시리얼, 최근날짜.
     */
    public List<Object> getFinalDayByMcu(String _sysId, Boolean _isPoor);
    public List<Object> getStatisticsByModem(String _sysId, Boolean _isPoor, String _modemId, String _maxDate);
    
    /**
     * Method Name : getSnrChartByModem
     * Method Desc : 주어진 기간내에서 선택된 모뎀의 SNR 데이터 조회. 차트 생성.
     * @param conditionMap
     */
    public List<Object> getSnrChartByModem(Map<String,Object> conditionMap);
    
    /**
     * Method Name : getFinalSnrChartByModem
     * Method Desc : 주어진 기간과 상관없이 선택된 모뎀의 마지막 데이터를 조회 
     * @param conditionMap
     */
    public List<Object> getFinalSnrChartByModem(Map<String,Object> conditionMap);
}
