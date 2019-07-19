package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.CommLog;
import com.aimir.model.device.CommLogChartVO;




public interface CommLogManager {

	public List<CommLogChartVO> getReceivePieChartData(Map<String, String> conditionMap);
	
	public List<Map<String, Object>> getReceivePieChart(Map<String, String> conditionMap);

	public List<CommLogChartVO> getBarChartData(Map<String, String> conditionMap);

    public List<Map<String, Object>> getBarChart(Map<String, String> conditionMap);
    
    //flex
    @Deprecated
	public List<CommLog> getCommLogGridData(Map<String, String> conditionMap);
	
	//for extjs grid 
	@SuppressWarnings("rawtypes")
    public List getCommLogGridData2(Map<String, String> conditionMap);
	
	

	public List<Map<String, String>> getLocationLineChartData(Map<String, String> conditionMap);

	public List<Map<String, String>> getMcuLineChartData(Map<String, String> conditionMap);

	public List<CommLogChartVO> getLocationPieChartData(Map<String, String> conditionMap);

	public List<CommLogChartVO> getMcuPieChartData(Map<String, String> conditionMap);

	public List<CommLogChartVO> getPieChartData(Map<String, String> conditionMap);

	//public List<CommLogChartVO> getSendRevceiveChartData();

    @Deprecated
    public List<Map<String,Object>> getSendRevceiveChartData(String supplierId);

	public List<Map<String,Object>> getSendReceiveChartData(String supplierId);

	//public List<CommLogChartVO> getSVCTypeChartData();
	
	public List<Map<String,Object>> getSVCTypeChartData(String supplierId);

	public List<Map<String, String>> getLocationChartData();

	public Map<String, String> getCommLogStatisticsData(Map<String, String> conditionMap);

	public String getCommLogGridDataCount(Map<String, String> conditionMap);
	
	public List<CommLog> getCommLogGridDataForExcel(Map<String, String> conditionMap);
	
	/**
	 * @desc 패킷타입을 가지고 온다.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getPacketType();
	
	/**
	 * @desc 센더타입을 가지고 온다.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getSenderType();
	
}
