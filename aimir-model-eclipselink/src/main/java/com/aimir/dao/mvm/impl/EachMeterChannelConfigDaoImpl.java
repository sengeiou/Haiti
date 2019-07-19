package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.EachMeterChannelConfigDao;
import com.aimir.model.mvm.EachMeterChannelConfig;
import com.aimir.model.mvm.EachMeterChannelConfigPk;
import com.aimir.util.Condition;

@Repository(value = "eachmeterchannelconfigDao")
public class EachMeterChannelConfigDaoImpl extends AbstractJpaDao<EachMeterChannelConfig, EachMeterChannelConfigPk>
    implements EachMeterChannelConfigDao {

	private static Log logger = LogFactory.getLog(EachMeterChannelConfigDaoImpl.class);

	public EachMeterChannelConfigDaoImpl() {
		super(EachMeterChannelConfig.class);
	}

    @Override
    public List<Object> getByList(Map<String, Object> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getChannelCalcMethodList(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<EachMeterChannelConfig> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}