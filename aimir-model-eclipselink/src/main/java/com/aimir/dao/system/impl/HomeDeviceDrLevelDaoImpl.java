package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.HomeDeviceDrLevelDao;
import com.aimir.model.system.HomeDeviceDrLevel;
import com.aimir.util.Condition;

@Repository(value = "homedevicedrlevelDao")
public class HomeDeviceDrLevelDaoImpl extends AbstractJpaDao<HomeDeviceDrLevel, Integer>
implements HomeDeviceDrLevelDao  {
	Log logger = LogFactory.getLog(HomeDeviceDrLevelDaoImpl.class);

	public HomeDeviceDrLevelDaoImpl() {
		super(HomeDeviceDrLevel.class);
	}

    @Override
    public List<Map<String, Object>> getHomeDeviceDrLevelByCondition(
            int categoryId, String drLevel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<HomeDeviceDrLevel> getPersistentClass() {
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
