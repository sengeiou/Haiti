package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ModemConfigDao;
import com.aimir.model.system.ModemConfig;
import com.aimir.util.Condition;

@Repository(value="modemconfigDao")
public class ModemConfigDaoImpl extends AbstractJpaDao<ModemConfig, Integer> implements ModemConfigDao{

	public ModemConfigDaoImpl() {
		super(ModemConfig.class);
	}

    @Override
    public List<ModemConfig> getDeviceConfigs(Integer configId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModemConfig getDeviceConfig(String swVersion, String swRevision,
            Integer connectedDeviceModel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ModemConfig getDeviceConfig(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<?, ?> getParsers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ModemConfig> getPersistentClass() {
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
