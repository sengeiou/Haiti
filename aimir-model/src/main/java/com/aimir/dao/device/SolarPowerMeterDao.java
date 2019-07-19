package com.aimir.dao.device;

import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.SolarPowerMeter;
import com.aimir.util.Condition;

public interface SolarPowerMeterDao extends GenericDao<SolarPowerMeter, Integer> {
	
	public long totalByConditions(Set<Condition> condition);	
}
