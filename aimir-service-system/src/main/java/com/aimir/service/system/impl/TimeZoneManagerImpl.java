package com.aimir.service.system.impl;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.TimeZoneDao;
import com.aimir.model.system.TimeZone;
import com.aimir.service.system.TimeZoneManager;

@WebService(endpointInterface = "com.aimir.service.system.TimeZoneManager")
@Service(value = "timezoneManager")
@Transactional
@RemotingDestination
public class TimeZoneManagerImpl implements TimeZoneManager {

	Log logger = LogFactory.getLog(TimeZoneManagerImpl.class);

	@Autowired
	TimeZoneDao dao;
	
	public TimeZone get(Integer timezoneId) {
		return dao.get(timezoneId);
	}

	public List<TimeZone> getTimeZones() {
		return dao.getAll();
	}
	
}
