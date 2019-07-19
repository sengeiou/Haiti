package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.PowerQualityStatusDao;
import com.aimir.model.mvm.PowerQualityStatus;
import com.aimir.model.mvm.PowerQualityStatusPk;
import com.aimir.util.Condition;

@Repository(value = "powerqualitystatusDao")
public class PowerQualityStatusDaoImpl extends AbstractJpaDao<PowerQualityStatus, PowerQualityStatusPk> implements PowerQualityStatusDao {

	private static Log logger = LogFactory.getLog(PowerQualityStatusDaoImpl.class);
    
	public PowerQualityStatusDaoImpl() {
		super(PowerQualityStatus.class);
	}

    @Override
    public Class<PowerQualityStatus> getPersistentClass() {
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