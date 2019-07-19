package com.aimir.dao.device;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Headend;

public interface HeadendDao extends GenericDao<Headend, Integer>{
	
	public List<Headend> getLastData();
}
