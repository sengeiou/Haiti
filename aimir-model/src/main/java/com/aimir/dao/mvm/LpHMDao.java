package com.aimir.dao.mvm;

import java.util.List;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.LpHM;
import com.aimir.util.Condition;

public interface LpHMDao extends GenericDao<LpHM, Integer>{
	public List<LpHM> getLpHMsByListCondition(Set<Condition> set);
    public List<Object> getLpHMsCountByListCondition(Set<Condition> set);
    public List<Object> getLpHMsMaxMinSumAvg(Set<Condition> conditions, String div) ;
    
    /*
     * 2010.09.07 양철민
     */
    public List<Object> getLpHMsByNoSended();
    public List<Object> getLpHMsByNoSended(String mdevType);
    public void updateSendedResult(LpHM lphm);
}
