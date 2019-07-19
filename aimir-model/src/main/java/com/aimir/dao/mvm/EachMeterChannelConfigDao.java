package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.EachMeterChannelConfig;
import com.aimir.model.mvm.EachMeterChannelConfigPk;


public interface EachMeterChannelConfigDao extends GenericDao<EachMeterChannelConfig, EachMeterChannelConfigPk>{
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