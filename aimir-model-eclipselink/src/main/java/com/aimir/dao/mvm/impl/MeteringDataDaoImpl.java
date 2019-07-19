package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringDataDao;
import com.aimir.model.mvm.MeteringData;
import com.aimir.util.Condition;

@Repository(value = "meteringdataDao")
public class MeteringDataDaoImpl extends
		AbstractJpaDao<MeteringData, Integer> implements
		MeteringDataDao {

	@SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);

	public MeteringDataDaoImpl() {
		super(MeteringData.class);
	}

    @Override
    public Map<String, Object> getTotalCountByLocation(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSuccessCountByLocation(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSuccessCountByLocationJeju(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public String getFailureCountByCause(Map<String, Object> condition,
            int cause) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public Map<String, String> getFailureCountByCauses(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public String getFailureCountByEtc(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getCommPermitMeterCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getPermitMeterCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getTotalGatheredMeterCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLastRegisterMeteringData(
            Map<String, Object> preCondition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MeteringData> getPersistentClass() {
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