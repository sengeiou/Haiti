package com.aimir.service.device;

import java.util.List;
import java.util.Map;

public interface PowerAlarmLogManager {
	
	/**
	 * MiniGadget ColumnChart 데이터 조회
	 * @param params
	 * @return
	 */
	public List<Object> getPowerAlarmLogColumnMiniChart(Map<String,Object> params);
	
	/**
	 * MaxGadget ColumnChart 데이터 조회
	 * @param params
	 * @return
	 */
	public List<Object> getPowerAlarmLogColumnChart(Map<String,Object> params);
	
	/**
	 * MaxGadget PieChart 데이터 조회
	 * @param params
	 * @return
	 */
	public List<Object> getPowerAlarmLogPieData(Map<String,Object> params);
	public List<Object> getPowerAlamLogMaxDataExcel(Map<String,Object> params);
	
	/**
	 * MaxGadget Grid 데이터 조회
	 * @param params
	 * @return
	 */
	public List<Object> getPowerAlarmLogGridData(Map<String,Object> params);

    /**
     * method name : getPowerAlarmLogGridDataTotalCount<b/>
     * method Desc : Power Outage  맥스가젯에서 Grid 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getPowerAlarmLogGridDataTotalCount(Map<String, Object> conditionMap);

	public List<Object> getPowerAlamLogMaxData(Map<String,Object> params);

}
