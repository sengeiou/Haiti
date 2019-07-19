package com.aimir.dao.mvm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MeteringLP;

public interface MeteringLpDao extends GenericDao<MeteringLP, Integer>{
	
	public Map<String,Object> getMissingCountByDay(Map<String,Object> params);
	public Map<String,Object> getMissingCountByHour(Map<String,Object> params);
	public List<Object> getDetailHourSearchData(HashMap<String, Object> condition);
	public List<Object> getDetailLpMaxMinAvgSumData(HashMap<String, Object> condition);

    public List<Map<String,Object>> getDetailHourlyLPData(Map<String, Object> condition, boolean isSum);
	public List<Map<String,Object>> getDetailHourlyLPIntervalData(Map<String, Object> condition, String searchDate);
	
	public List<Object> getLpByMeter(String lpTableName);
	public List<Object> getLpByModem(String lpTableName);
	public List<Object> getTimeLpByMeter(String lpTableName, String yyyymmddhh);
	public List<Object> getTimeLpByModem(String lpTableName, String yyyymmddhh);
	public List<Object> getTimeLpValue(String lpTableName,String mdevId, String yyyymmddhh);
	public void insertAzbilLog(String createDate,String time, String name,Integer value,Integer status);

    /*
     * 검침데이터 상세 시간별 데이터
     */
    public List<Map<String, Object>> getDetailHourData4fc(Map<String, Object> condition, boolean isSum);

    /**
     * method name : getMeteringDataHourlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataHourlyData(Map<String, Object> conditionMap, boolean isTotal);

    /**
     * method name : getMeteringDataHourlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringDataHourlyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev);

    /**
     * method name : getMeteringDataDetailHourlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 시간별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isSum
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDetailHourlyData(Map<String, Object> conditionMap, boolean isSum);

    /**
     * method name : getMeteringDataDetailLpData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 주기별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDetailLpData(Map<String, Object> conditionMap);
    
    /**
     * method name : getSgdgXam1Data<b/>
     * method Desc : SgdgXam1테이블에 넣을 데이터들을 조회해온다.
     * 
     * @return
     */
    public List<Map<String, Object>> getSgdgXam1Data(Map<String, Object> conditionMap);
    
    /**
     * method name : getSgdgXam1LPData<b/>
     * method Desc : SgdgXam1테이블에 넣을 LP 데이터들을 조회해온다.
     * 
     * @return
     */
    public List<Map<String, Object>> getSgdgXam1LPData(Map<String, Object> conditionMap);
    


    /**
     * method name : getMeteringLpPreData<b/>
     * method Desc : 특정 날짜의 데이터보다 이전 날짜중 데이터가 있는 가장 최신 날짜의 정보를 조회해온다.
     * 
     * @return
     */
    public Map<String, Object> getMeteringLpPreData(Map<String, Object> conditionMap);
}