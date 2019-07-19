package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.model.system.TOURate;
import com.aimir.util.Condition;

@Repository(value = "tourateDao")
public class TOURateDaoImpl extends AbstractJpaDao<TOURate, Integer> implements TOURateDao {
			
    Log logger = LogFactory.getLog(TOURateDaoImpl.class);
    
    public TOURateDaoImpl() {
        super(TOURate.class);
    }

    @Override
    public Map<Integer, Object> getPeakTimeZone(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TOURate> getTOURateByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTOURateWithSeasonsBySyearNull(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTOURateWithSeasonsBySyear(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TOURate getTOURate(Integer tariffTypeId, Integer seasonId,
            PeakType peakType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int touDeleteByCondition(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Class<TOURate> getPersistentClass() {
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
