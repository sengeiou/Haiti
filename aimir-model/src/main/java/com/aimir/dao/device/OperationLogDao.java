package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.OperationLog;
import com.aimir.model.device.OperationLogChartData;

public interface OperationLogDao extends GenericDao<OperationLog, Long> {

	List<OperationLogChartData> getOperationLogMiniChartData(Integer supplier);

	List<OperationLogChartData> getAdvanceGridData(Map<String, String> conditioMap);

	List<OperationLogChartData> getColumnChartData(Map<String, String> conditioMap);
	
	List<OperationLog> getGridData(Map<String, String> conditioMap);

	String getOperationLogMaxGridDataCount(Map<String, String> conditionMap);

	String getGridDataCount(Map<String, String> conditionMap);

    /**
     * method name : getMcuOperationLogList<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭에서 명령내역을 조회한다. 
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getMcuOperationLogList(Map<String, Object> conditionMap, boolean isCount);
}
