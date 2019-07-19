package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.PowerQuality;

 
public interface PowerQualityDao extends GenericDao<PowerQuality, Integer>{
	public List<Object> getPowerQuality(Map<String, Object> condition);
	public Integer getCount(Map<String, Object> condition);
	public List<Map<String, Object>> getCountForPQMini(Map<String, Object> condition);
    public List<Object> getVoltageLevels(Map<String, Object> condition);
    public List<Object> getVoltageLevelsForSoria(Map<String, Object> condition);	 // INSERT SP-204
    public Integer getVoltageLevelsCount(Map<String, Object> condition);
    public List<Object> getPowerInstrumentList(Map<String, Object> condition);
    public Integer getPowerInstrumentListCount(Map<String, Object> condition);
    public List<Object> getPowerDetailList(Map<String, Object> condition); 
    public Integer getPowerDetailListCount(Map<String, Object> condition); 

    /**
     * method name : getMeterDetailInfoPqData<b/>
     * method Desc : MDIS - Meter Management 맥스가젯의 Detail Information 탭에서 PowerQuality 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterDetailInfoPqData(Map<String, Object> conditionMap);
    
    /**
     * @MethodName getPowerQualityData
     * @Date 2014. 1. 13.
     * @param condition (String) date, (double) seg, (double) swell, (double) vol
     * @return List<PowerQuality>
     * @Modified
     * @Description PowerQualityDate를 반환한다. 
     */
    public Map<String, Object> getPowerQualityData(Map<String, Object> condition);
    
    /**
     * @MethodName getPowerQualityChartData
     * @Date 2014. 1. 15.
     * @param condition (String) date, (Double) seg, (Double) swell
     * @return
     * @Modified
     * @Description
     */
    public List<Map<String, Object>> getPowerQualityChartData(Map<String, Object> condition);
    
    /**
     * 전력 정보를 삭제한다.
     * @param meterId
     * @param yyyymmdd
     */
    public void delete(String meterId, String yyyymmdd);
}