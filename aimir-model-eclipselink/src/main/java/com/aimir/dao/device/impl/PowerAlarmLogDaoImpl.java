package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.LineType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.PowerAlarmLogDao;
import com.aimir.model.device.PowerAlarmLog;
import com.aimir.util.Condition;

@Repository(value = "poweralarmlogDao")
public class PowerAlarmLogDaoImpl extends AbstractJpaDao<PowerAlarmLog, Long> implements PowerAlarmLogDao {

    private static Log log = LogFactory.getLog(PowerAlarmLogDaoImpl.class);
    
    public PowerAlarmLogDaoImpl() {
		super(PowerAlarmLog.class);
	}

    @Override
    public Class<PowerAlarmLog> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getPowerAlarmLogColumnChartData(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getPowerAlarmLogPieData(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getPowerAlarmLogListData(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getPowerAlarmLogListData(Map<String, Object> condition,
            Boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PowerAlarmLog> getOpenPowerAlarmLog(Integer id, String openTime,
            LineType lineType) {
        // TODO Auto-generated method stub
        return null;
    }
	
}