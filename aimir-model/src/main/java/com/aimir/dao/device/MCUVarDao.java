package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MCUVar;

public interface MCUVarDao extends GenericDao<MCUVar, Long> {
	public MCUVar get(Long id);
	
}	
