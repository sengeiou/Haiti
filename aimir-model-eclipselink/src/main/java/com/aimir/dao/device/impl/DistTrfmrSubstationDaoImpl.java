package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.DistTrfmrSubstationDao;
import com.aimir.model.device.DistTrfmrSubstation;
import com.aimir.util.Condition;


/**
 * DistTrfmrSubstationDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 3. 13.  v1.0        문동규   Distribution Transformer Substation 조회
 * </pre>
 */
@Repository(value = "distTrfmrSubstationDao")
public class DistTrfmrSubstationDaoImpl extends AbstractJpaDao<DistTrfmrSubstation, Integer> implements DistTrfmrSubstationDao {

    Log logger = LogFactory.getLog(DistTrfmrSubstationDaoImpl.class);
    
	public DistTrfmrSubstationDaoImpl() {
		super(DistTrfmrSubstation.class);
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
    public List<Map<String, Object>> getEbsDtsList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getEbsDtsDupCount(Map<String, Object> conditionMap) {
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
    public Class<DistTrfmrSubstation> getPersistentClass() {
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