package com.aimir.service.system.impl;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.SupplyCapacityLogDao;
import com.aimir.model.system.SupplyCapacityLog;
import com.aimir.service.system.SupplyCapacityLogManager;

@WebService(endpointInterface = "com.aimir.service.system.SupplyCapacityLogManager")
@Service(value = "supplyCapacityLogManager")
@Transactional
@RemotingDestination
public class SupplyCapacityLogManagerImpl implements SupplyCapacityLogManager {
	@Autowired
	SupplyCapacityLogDao dao;	
   
	Log logger = LogFactory.getLog(SupplyCapacityLogManagerImpl.class);
    


	public void delete(int supplyCapacityLogId) {
		//dao.deleteById(supplyCapacityLogId);
		
	}

	public void add(SupplyCapacityLog supplyCapacityLog) {
		dao.add(supplyCapacityLog);		
	}

	public List<SupplyCapacityLog> getSupplyCapacityLogs() {
	   return dao.getAll();
	}

	public void supplyCapacityLogDelete(int supplyTypeId) {
		dao.supplyCapacityLogDelete(supplyTypeId);
		
	}

	public List<SupplyCapacityLog> getSupplyCapacityLogs(int page, int count) {
		return dao.getSupplyCapacityLogs(page, count);
	}

	
    
    
}
