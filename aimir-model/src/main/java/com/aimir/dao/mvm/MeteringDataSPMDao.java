package com.aimir.dao.mvm;

import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MeteringDataSPM;
import com.aimir.util.Condition;

public interface MeteringDataSPMDao extends GenericDao<MeteringDataSPM, Integer> {

	public long totalByConditions(Set<Condition> condition);
}
