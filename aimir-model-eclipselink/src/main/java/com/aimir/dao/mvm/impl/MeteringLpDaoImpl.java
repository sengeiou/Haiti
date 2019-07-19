package com.aimir.dao.mvm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.util.Condition;

@Repository(value = "meteringlpDao")
public class MeteringLpDaoImpl extends
		AbstractJpaDao<MeteringLP, Integer> implements
		MeteringLpDao {

    @Autowired
    SupplierDao supplierDao;

    private static Log logger = LogFactory.getLog(MeteringLpDaoImpl.class);

    public MeteringLpDaoImpl() {
		super(MeteringLP.class);
	}

    @Override
    public Map<String, Object> getMissingCountByDay(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getMissingCountByHour(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDetailHourSearchData(
            HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDetailLpMaxMinAvgSumData(
            HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDetailHourlyLPData(
            Map<String, Object> condition, boolean isSum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDetailHourlyLPIntervalData(
            Map<String, Object> condition, String searchDate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpByMeter(String lpTableName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLpByModem(String lpTableName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTimeLpByMeter(String lpTableName, String yyyymmddhh) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTimeLpByModem(String lpTableName, String yyyymmddhh) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTimeLpValue(String lpTableName, String mdevId,
            String yyyymmddhh) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void insertAzbilLog(String createDate, String time, String name,
            Integer value, Integer status) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Map<String, Object>> getDetailHourData4fc(
            Map<String, Object> condition, boolean isSum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataHourlyData(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataHourlyData(
            Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataDetailHourlyData(
            Map<String, Object> conditionMap, boolean isSum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataDetailLpData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSgdgXam1Data(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSgdgXam1LPData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getMeteringLpPreData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MeteringLP> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}