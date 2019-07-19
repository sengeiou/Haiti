package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.constants.CommonConstants.LineType;
import com.aimir.dao.GenericDao;
import com.aimir.model.device.PowerAlarmLog;

public interface PowerAlarmLogDao extends GenericDao<PowerAlarmLog, Long> {
	
	/**
	 * Flex ColumnChart 데이터 조회
	 * @param Map<String, Object>
	 * @return List<Object>
	 */
	public List<Object> getPowerAlarmLogColumnChartData(Map<String, Object> condition);
	
	/**
	 * Flex PieChart 데이터 조회
	 * @param Map<String, Object>
	 * @return List<Object>
	 */
	public List<Object> getPowerAlarmLogPieData(Map<String, Object> condition);
	
	/**
	 * Flex GridData 데이터 조회
	 * @param Map<String, Object>
	 * @return List<Object>
	 */
	public List<Object> getPowerAlarmLogListData(Map<String, Object> condition);

	/**
	 * method name : getPowerAlarmLogListData<b/>
	 * method Desc :
	 *
	 * @param condition
	 * @param isCount
	 * @return
	 */
	public List<Object> getPowerAlarmLogListData(Map<String, Object> condition, Boolean isCount);

	/**
	 * close되지 않은 open 알람 목록 조회
	 * @param id (미터 아이디)
	 * @param openTime (시간 )
	 * @param lineType (결선 타입)
	 * @return
	 */
	public List<PowerAlarmLog> getOpenPowerAlarmLog(Integer id, String openTime, LineType lineType);

}
