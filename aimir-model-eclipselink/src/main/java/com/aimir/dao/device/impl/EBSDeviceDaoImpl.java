package com.aimir.dao.device.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.EBSDeviceDao;
import com.aimir.model.device.EBS_DEVICE;
import com.aimir.util.Condition;

/**
 * EBSDeviceDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 3. 13.  v1.0        문동규   Distribution Transformer Substation 조회
 * </pre>
 */
@Repository(value = "ebsDeviceDao")
public class EBSDeviceDaoImpl extends AbstractJpaDao<EBS_DEVICE, Integer> implements EBSDeviceDao {

    Log logger = LogFactory.getLog(EBSDeviceDaoImpl.class);
    
    public EBSDeviceDaoImpl() {
        super(EBS_DEVICE.class);
    }

    @Override
    public Class<EBS_DEVICE> getPersistentClass() {
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
    public List<Map<String, Object>> getEbsDeviceList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsDailyMonitoringList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsMonthlyMonitoringList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsMeterList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getTopParentMID(String meterId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int deleteValify(Integer id) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Integer getParentId(String meterId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMonitoringTree(String meterId,
            String yyyymmdd, Integer channel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getOrder(String meterId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getParentOrder(String typeCd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getEbsDtsDupCount(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsSuspectedDtsList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsDtsStateChartData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsDtsTreeLocationNodeData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsDtsTreeDtsNodeData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsDtsChartImportData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsExportExcelData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMonitoringTreeMonthly(String meterId,
            String yyyymmdd, Integer channel) {
        // TODO Auto-generated method stub
        return null;
    }

}