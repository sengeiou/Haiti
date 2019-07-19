package com.aimir.dao.mvm;

import java.util.List;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.LpSPM;
import com.aimir.util.Condition;

public interface LpSPMDao extends GenericDao<LpSPM, Integer> {

	public long totalByConditions(final Set<Condition>condition);
	public List<LpSPM> getLpSPMsByListCondition(final Set<Condition> conditions);
	public List<Object> getLpSPMsCountByListCondition(Set<Condition> conditions);
	
}
