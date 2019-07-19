package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ChangeLogSettingDao;
import com.aimir.model.device.ChangeLogSetting;
import com.aimir.util.Condition;

@Repository(value = "changelogsettingDao")
public class ChangeLogSettingDaoImpl extends AbstractJpaDao<ChangeLogSetting, Long> implements ChangeLogSettingDao {
    
	public ChangeLogSettingDaoImpl() {
		super(ChangeLogSetting.class);
	}

    @Override
    public List<ChangeLogSetting> getChangeLogSettings(String[] array) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ChangeLogSetting> getPersistentClass() {
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
