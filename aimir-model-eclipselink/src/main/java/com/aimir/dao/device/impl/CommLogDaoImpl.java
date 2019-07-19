package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.CommLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.CommLog;
import com.aimir.model.device.CommLogChartVO;
import com.aimir.model.system.Code;
import com.aimir.util.Condition;

@Repository(value = "commlogDao")
public class CommLogDaoImpl extends AbstractJpaDao<CommLog, Long> implements CommLogDao {

    protected static Log logger = LogFactory.getLog(CommLogDaoImpl.class);

	@Autowired
	SupplierDao supplierDao;
	
	public CommLogDaoImpl() {
		super(CommLog.class);
	}

    @Override
    public List<CommLogChartVO> getReceivePieChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getReceivePieChart(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommLogChartVO> getBarChartData(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBarChart(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommLog> getCommLogGridData(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List getPacketType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommLog> getCommLogGridData2(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getLocationLineChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getMcuLineChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommLogChartVO> getLocationPieChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommLogChartVO> getMcuPieChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommLogChartVO> getPieChartData(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommLogChartVO> getSendReceiveChartData(String suppliedId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommLogChartVO> getSVCTypeChartData(String suppliedId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getLocationChartData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCommLogGridDataCount(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public Map<String, String> getCommLogData(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getCommLogStatisticsData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommLog> getCommLogGridDataForExcel(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMcuCommLogList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMcuCommLogData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<CommLog> getPersistentClass() {
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
    public List<Code> getSenderType() {
        // TODO Auto-generated method stub
        return null;
    }
}