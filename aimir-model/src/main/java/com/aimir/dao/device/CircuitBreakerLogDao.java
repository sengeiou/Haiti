package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.CircuitBreakerLog;

public interface CircuitBreakerLogDao extends GenericDao<CircuitBreakerLog, Long> {

	List<CircuitBreakerLog> getCircuitBreakerLogGridData(Map<String, String> paramMap);

	Long getCircuitBreakerLogGridDataCount(Map<String, String> paramMap);

	List<Map<String, String>> getCircuitBreakerLogChartData(Map<String, String> paramMap);

}
