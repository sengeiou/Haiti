package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.CommLog;
import com.aimir.model.device.CommLogChartVO;
import com.aimir.model.system.Code;


public interface CommLogDao extends GenericDao<CommLog, Long> {

	public List<CommLogChartVO> getReceivePieChartData(Map<String, String> conditionMap);
	
	public List<Map<String, Object>> getReceivePieChart(Map<String, String> conditionMap);

	public List<CommLogChartVO> getBarChartData(Map<String, String> conditionMap);
	
	public List<Map<String, Object>> getBarChart(Map<String, String> conditionMap);

	public List<CommLog> getCommLogGridData(Map<String, String> conditionMap);
	
	/**
	 * 패킷타입을 가지고 온다.
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getPacketType();
	

	/**
	 * comm log grid data fetch
	 * @param conditionMap
	 * @return
	 */
	public List<CommLog> getCommLogGridData2(Map<String, String> conditionMap);

	public List<Map<String, String>> getLocationLineChartData(Map<String, String> conditionMap);

	public List<Map<String, String>> getMcuLineChartData(Map<String, String> conditionMap);

	public List<CommLogChartVO> getLocationPieChartData(Map<String, String> conditionMap);

	public List<CommLogChartVO> getMcuPieChartData(Map<String, String> conditionMap);

	public List<CommLogChartVO> getPieChartData(Map<String, String> conditionMap);

	public List<CommLogChartVO> getSendReceiveChartData(String suppliedId);

	public List<CommLogChartVO> getSVCTypeChartData(String suppliedId);

	public List<Map<String, String>> getLocationChartData();
	
	public String getCommLogGridDataCount(Map<String, String> conditionMap);

    @Deprecated
    public Map<String, String> getCommLogData(Map<String, String> conditionMap);

	public Map<String, String> getCommLogStatisticsData(Map<String, String> conditionMap);		
	
	public List<CommLog> getCommLogGridDataForExcel(Map<String, String> conditionMap);

    /**
     * method name : getMcuCommLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getMcuCommLogList(Map<String, Object> conditionMap, boolean isCount);

    /**
     * method name : getMcuCommLogData<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMcuCommLogData(Map<String, Object> conditionMap);

    /**
	 * @desc 센더타입을 가지고 온다.
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Code> getSenderType();
}
