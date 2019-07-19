package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.PowerQualityDao;
import com.aimir.model.mvm.PowerQuality;
import com.aimir.util.Condition;

@Repository(value = "powerqualityDao")
@SuppressWarnings("unchecked")
public class PowerQualityDaoImpl extends AbstractJpaDao<PowerQuality, Integer> implements PowerQualityDao {

	private static Log logger = LogFactory.getLog(PowerQualityDaoImpl.class);

	// 표준편차율(%) 구하는 식. diviation = 100 * (avg - minimum value) / avg
	private static final String QUERY_WHERE_DEVIATION = 
		"CASE WHEN ((p.vol_a + p.vol_b + p.vol_c) / 3) > 0 THEN " +
		"(100 * (" +
		"((p.vol_a + p.vol_b + p.vol_c) / 3) - " +
		"CASE WHEN p.vol_a <= p.vol_b AND "+
				  "p.vol_a <= p.vol_c THEN " +
				  "p.vol_a " +
			 "WHEN p.vol_c < p.vol_b THEN "+
			 	  "p.vol_c " +
	         "ELSE p.vol_b END ) /" +
	    "((p.vol_a + p.vol_b + p.vol_c) / 3) )" +
		" ELSE 0 END";
    
	public PowerQualityDaoImpl() {
		super(PowerQuality.class);
	}

    @Override
    public Class<PowerQuality> getPersistentClass() {
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
    public List<Object> getPowerQuality(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getCount(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getCountForPQMini(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getVoltageLevels(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }
    
    // INSERT START SP-204
    @Override
    public List<Object> getVoltageLevelsForSoria(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }
    // INSERT END SP-204

    @Override
    public Integer getVoltageLevelsCount(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getPowerInstrumentList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getPowerInstrumentListCount(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getPowerDetailList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getPowerDetailListCount(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterDetailInfoPqData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getPowerQualityData(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getPowerQualityChartData(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String meterId, String yyyymmdd) {
        // TODO Auto-generated method stub
        
    }

}