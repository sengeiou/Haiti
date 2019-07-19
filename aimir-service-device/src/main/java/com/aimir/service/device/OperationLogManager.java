package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.OperationList;
import com.aimir.model.device.OperationLog;
import com.aimir.model.device.OperationLogChartData;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;

public interface OperationLogManager {

	List<OperationLogChartData> getOperationLogMiniChartData(Integer supplier);

	List<OperationLogChartData> getAdvanceGridData(Map<String, String> conditioMap);
	
	List<OperationLogChartData> getColumnChartData(Map<String, String> conditioMap);
	
	List<OperationLog> getGridData(Map<String, String> conditioMap);
	
	List<Map<String, Object>> getGridData(Map<String, String> conditioMap, String supplierId);

	List<OperationList> getOperationListByConstraintId(int constraintId);

	void updateOperation(String updateStr);
	
	void saveOperationLog(Supplier supplier, Code targetTypeCode, String targetName, String userId, Code operationCode, Integer status, String errorReason);

	List<OperationList> getOperationGridData(int operationCodeId);

	public List<Object> getOpeartionLogListExcel(Map<String, String> condition);
	String getOperationLogMaxGridDataCount(Map<String, String> conditionMap);
	public void saveOperationLogByCustomer(Supplier supplier, Code targetTypeCode, String targetName, String userId, Code operationCode, Integer status, String errorReason, String description, String contractNumber);
	public void deleteOperationLog(Long id);

    /**
     * method name : saveOperationLogByMeterCmd<b/>
     * method Desc : Meter Command 실행 후 Operation Log 를 저장한다.
     * 
     * @param supplier
     * @param targetTypeCode
     * @param targetName
     * @param writeDate
     * @param userId
     * @param operationCode
     * @param description
     * @param contractNumber
     */
    public void saveOperationLogByMeterCmd(Supplier supplier, Code targetTypeCode, String targetName, String writeDate,
            String userId, Code operationCode, String description, String contractNumber);
}