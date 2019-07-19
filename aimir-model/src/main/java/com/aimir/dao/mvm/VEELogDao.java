package com.aimir.dao.mvm;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.VEELog;
import com.aimir.util.Condition;

public interface VEELogDao extends GenericDao<VEELog, Integer>{
	public List<VEELog> getVEELogByListCondition(Set<Condition> set);
	public List<VEELog> getVEELogByListCondition(Set<Condition> set, int startRow, int pageSize);
	public List<Object> getVeeLogByDataList(HashMap<String, Object> condition);
	public List<Object> getVeeLogByCountList(HashMap<String, Object> condition);
	
}
