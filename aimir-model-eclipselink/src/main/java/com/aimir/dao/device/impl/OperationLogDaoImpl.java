package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.OperationLog;
import com.aimir.model.device.OperationLogChartData;
import com.aimir.util.Condition;

@Repository(value = "operationlogDao")
public class OperationLogDaoImpl extends AbstractJpaDao<OperationLog, Long> implements OperationLogDao {

    private static Log logger = LogFactory.getLog(OperationLogDaoImpl.class);

	@Autowired
	SupplierDao supplierDao;
	
	public OperationLogDaoImpl() {
		super(OperationLog.class);
	}

    @Override
    public Class<OperationLog> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<OperationLogChartData> getOperationLogMiniChartData(
            Integer supplier) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<OperationLogChartData> getAdvanceGridData(
            Map<String, String> conditioMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<OperationLogChartData> getColumnChartData(
            Map<String, String> conditioMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<OperationLog> getGridData(Map<String, String> conditioMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOperationLogMaxGridDataCount(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getGridDataCount(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMcuOperationLogList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }
}