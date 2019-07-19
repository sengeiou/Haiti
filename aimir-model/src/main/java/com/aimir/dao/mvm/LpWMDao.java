package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.LpWM;
import com.aimir.util.Condition;

public interface LpWMDao extends GenericDao<LpWM, Integer>{
	public List<LpWM> getLpWMsByListCondition(Set<Condition> list);
    public List<Object> getLpWMsCountByListCondition(Set<Condition> set);
    public List<Object> getLpWMsMaxMinSumAvg(Set<Condition> conditions, String div) ;
    
    /*
     * 2010.09.07 양철민
     */
    public List<Object> getLpWMsByNoSended();
    public List<Object> getLpWMsByNoSended(String mdevType);
    public void updateSendedResult(LpWM lpwm);
    public List<Object> getConsumptionWmCo2LpValuesParentId(Map<String, Object> condition);
    
    public int getLpInterval( String mdevId );
}
