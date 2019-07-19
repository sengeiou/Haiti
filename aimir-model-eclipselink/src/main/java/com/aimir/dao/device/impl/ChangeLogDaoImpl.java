package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.model.device.ChangeLog;
import com.aimir.model.device.ChangeLogVO;
import com.aimir.util.Condition;

@Repository(value = "changelogDao")
public class ChangeLogDaoImpl extends AbstractJpaDao<ChangeLog, Long> implements ChangeLogDao {

	public ChangeLogDaoImpl() {
		super(ChangeLog.class);
	}

    @Override
    public List<ChangeLogVO> getChanageLogMiniChartData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ChangeLog> getChangeLogs(String[] strArray) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getChangeLogCount(String[] array) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ChangeLog> getPersistentClass() {
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
