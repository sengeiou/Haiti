package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.EndDeviceLogDao;
import com.aimir.model.device.EndDeviceLog;
import com.aimir.util.Condition;

@Repository(value = "enddeviceLogDao")
public class EndDeviceLogDaoImpl extends AbstractJpaDao<EndDeviceLog, Long> implements EndDeviceLogDao {

	public EndDeviceLogDaoImpl() {
		super(EndDeviceLog.class);
	}

    @Override
    public long getTotalSize(List<Integer> location) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<EndDeviceLog> getEndDeviceLogs(int start, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDeviceLog> getEndDeviceLogByEndDeviceId(
            List<Integer> endDeviceId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDeviceLog> getEndDeviceLogByLocationId(
            List<Integer> location, int start, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<EndDeviceLog> getPersistentClass() {
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