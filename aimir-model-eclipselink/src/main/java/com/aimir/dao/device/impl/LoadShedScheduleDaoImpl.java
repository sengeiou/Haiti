package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.LoadShedScheduleDao;
import com.aimir.model.device.LoadShedSchedule;
import com.aimir.model.device.LoadShedScheduleVO;
import com.aimir.util.Condition;

@Repository(value="loadshedscheduleDao")
public class LoadShedScheduleDaoImpl extends AbstractJpaDao<LoadShedSchedule, Integer>
		implements LoadShedScheduleDao {

	private static Log log = LogFactory.getLog(LoadShedScheduleDaoImpl.class);
	
	public LoadShedScheduleDaoImpl(){
		super(LoadShedSchedule.class);
	}

    @Override
    public List<LoadShedSchedule> getLoadShedSchedule(Integer groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadShedSchedule> getLoadShedSchedule(Integer targetId,
            ScheduleType scheduleType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadShedScheduleVO> getLoadShedSchedule(String groupType,
            String groupName, String scheduleType, String startDate,
            String endDate, String dayOfWeek) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadShedSchedule> getLoadShedSchedule(String groupType,
            String scheduleType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadShedSchedule> searchLoadShedSchedule(Integer operatorId,
            String groupType, String startDate, String endDate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<LoadShedSchedule> getPersistentClass() {
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
