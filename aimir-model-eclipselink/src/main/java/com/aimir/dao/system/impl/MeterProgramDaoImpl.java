package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.MeterProgramDao;
import com.aimir.model.system.MeterProgram;
import com.aimir.util.Condition;

@Repository(value = "meterProgramDao")
public class MeterProgramDaoImpl extends
		AbstractJpaDao<MeterProgram, Integer> implements
		MeterProgramDao {

	public MeterProgramDaoImpl() {
		super(MeterProgram.class);
	}

    @Override
    public MeterProgram getMeterConfigId(int meterconfig_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MeterProgram getMeterConfigId(int meterconfig_id,
            MeterProgramKind kind) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMeterProgramSettingsData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MeterProgram> getPersistentClass() {
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