package com.aimir.dao.mvm;

import java.util.HashMap;
import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.DisplayChannel;


public interface DisplayChannelDao extends GenericDao<DisplayChannel, Integer>{
	public List<DisplayChannel> getFromTableNameByList(HashMap<String, Object> conditions);
	//public List<DisplayChannel> getFromTableNameByListCondition(Set<Condition> set);
}
