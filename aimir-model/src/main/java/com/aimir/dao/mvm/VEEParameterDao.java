package com.aimir.dao.mvm;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.VEEParameter;
import com.aimir.util.Condition;

public interface VEEParameterDao extends GenericDao<VEEParameter, Integer>{
	public List<String> getParameterNames();
	public List<VEEParameter> getVEEParameterByListCondition(Set<Condition> set);
	public List<Object> getParameterDataList(HashMap<String, Object> hm);
	public List<Object> getParameterDataList(HashMap<String, Object> hm, int startRow, int pageSize);
	public List<VEEParameter> getParameterList(String ruleType);
}
