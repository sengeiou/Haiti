package com.aimir.dao.mvm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.DisplayChannelDao;
import com.aimir.model.mvm.DisplayChannel;
import com.aimir.util.Condition;

@Repository(value = "displaychannelDao")
public class DisplayChannelDaoImpl extends AbstractJpaDao<DisplayChannel, Integer> implements DisplayChannelDao {

	private static Log logger = LogFactory.getLog(DisplayChannelDaoImpl.class);
    
	public DisplayChannelDaoImpl() {
		super(DisplayChannel.class);
	}

    @Override
    public List<DisplayChannel> getFromTableNameByList(
            HashMap<String, Object> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DisplayChannel> getPersistentClass() {
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