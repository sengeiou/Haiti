package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.HeadendCtrl;

public interface HeadendCtrlDao extends GenericDao<HeadendCtrl, Integer> {
	
	public List<HeadendCtrl> getHeadendCtrlLastData(Map<String, Object> conditionMap);
	
	public void insert(HeadendCtrl headendCtrl);
	
}
