package com.aimir.dao.integration.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.integration.WSMeterConfigOBISDao;
import com.aimir.model.integration.WSMeterConfigOBIS;
import com.aimir.util.Condition;


@Repository(value = "wsmeterconfigobisDao")
public class WSMeterConfigOBISDaoImpl extends AbstractJpaDao<WSMeterConfigOBIS, Long> implements WSMeterConfigOBISDao {

    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(WSMeterConfigLogDaoImpl.class);

    public WSMeterConfigOBISDaoImpl() {
		super(WSMeterConfigOBIS.class);
	}
    
	@Override
	public Class<WSMeterConfigOBIS> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions,
			String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSMeterConfigOBIS get(String userId, String obisCode, String classId,
			String attributeNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WSMeterConfigOBIS> getMeterConfigOBISList(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
	

}