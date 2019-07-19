package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.ChannelConfig;


public interface ChannelConfigDao extends GenericDao<ChannelConfig, Integer>{
	public List<Object> getByList(Map<String, Object> conditions);

    /**
     * method name : getChannelCalcMethodList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChannelCalcMethodList(Map<String, Object> conditionMap);
}