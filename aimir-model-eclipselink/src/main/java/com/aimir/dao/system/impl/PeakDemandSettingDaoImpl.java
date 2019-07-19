package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.PeakDemandSettingDao;
import com.aimir.model.system.PeakDemandSetting;
import com.aimir.util.Condition;

@Repository(value = "peakDemandSettingDao")
public class PeakDemandSettingDaoImpl
	extends AbstractJpaDao<PeakDemandSetting, Integer> implements PeakDemandSettingDao {

	public PeakDemandSettingDaoImpl() {
		super(PeakDemandSetting.class);
	}

	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}

    @Override
    public Class<PeakDemandSetting> getPersistentClass() {
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
