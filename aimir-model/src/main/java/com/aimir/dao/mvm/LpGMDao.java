package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.LpGM;
import com.aimir.util.Condition;

public interface LpGMDao extends GenericDao<LpGM, Integer>{
	public List<LpGM> getLpGMsByListCondition(Set<Condition> list);
    public List<Object> getLpGMsCountByListCondition(Set<Condition> set);
    public List<Object> getLpGMsMaxMinSumAvg(Set<Condition> conditions, String div) ;
    
    /*
     * 2010.09.07 양철민
     */
    public List<Object> getLpGMsByNoSended();
    public List<Object> getLpGMsByNoSended(String mdevType);
    public void updateSendedResult(LpGM lpgm);
    public List<Object> getConsumptionGmCo2LpValuesParentId(Map<String, Object> condition);
    
    public int getLpInterval( String mdevId );
}
