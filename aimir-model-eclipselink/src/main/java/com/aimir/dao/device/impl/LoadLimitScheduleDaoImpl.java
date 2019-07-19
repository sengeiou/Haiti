package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.LoadLimitScheduleDao;
import com.aimir.model.device.LoadLimitSchedule;
import com.aimir.util.Condition;

@Repository(value = "loadlimitscheduleDao")
public class LoadLimitScheduleDaoImpl extends AbstractJpaDao<LoadLimitSchedule, Integer> implements LoadLimitScheduleDao {

	private static Log log = LogFactory.getLog(LoadLimitScheduleDaoImpl.class);
	
	public LoadLimitScheduleDaoImpl() {
		super(LoadLimitSchedule.class);
	}

    @Override
    public List<LoadLimitSchedule> getLoadLimitSchedule(String targetType,
            String targetId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadLimitSchedule> getLoadLimitSchedule(String targetId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadLimitSchedule> getLoadLimitSchedule(String targetId,
            ScheduleType scheduleType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<LoadLimitSchedule> getPersistentClass() {
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
