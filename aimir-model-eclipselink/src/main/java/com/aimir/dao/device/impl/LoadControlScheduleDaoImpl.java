package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.LoadControlScheduleDao;
import com.aimir.model.device.LoadControlSchedule;
import com.aimir.util.Condition;

@Repository(value = "loadcontrolscheduleDao")
public class LoadControlScheduleDaoImpl extends AbstractJpaDao<LoadControlSchedule, Integer> implements LoadControlScheduleDao {

	private static Log log = LogFactory.getLog(LoadControlScheduleDaoImpl.class);
	
	public LoadControlScheduleDaoImpl() {
		super(LoadControlSchedule.class);
	}

    @Override
    public List<LoadControlSchedule> getLoadControlSchedule(String targetType,
            String targetId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadControlSchedule> getLoadControlSchedule(String targetId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadControlSchedule> getLoadControlSchedule(String targetId,
            ScheduleType type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<LoadControlSchedule> getPersistentClass() {
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
