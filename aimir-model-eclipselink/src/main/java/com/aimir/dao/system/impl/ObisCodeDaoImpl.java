package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ObisCodeDao;
import com.aimir.model.system.OBISCode;
import com.aimir.util.Condition;

@Repository(value="ObisCodeDao")
public class ObisCodeDaoImpl extends AbstractJpaDao<OBISCode, Long> implements ObisCodeDao {

	Log logger = LogFactory.getLog(ObisCodeDaoImpl.class);
	
	protected ObisCodeDaoImpl() {
		super(OBISCode.class);
	}

	@Override
	public Class<OBISCode> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String,Object>> getObisCodeInfo(Map<String,Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public List<Map<String,Object>> getObisCodeInfoByName(Map<String,Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public List<Map<String,Object>> getObisCodeGroup(Map<String,Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String,Object>> getObisCodeWithEvent(Map<String,Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String,Object>> getEventObisCode(String obisFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateDescr(Map<String, Object> condition) throws Exception {
		// TODO Auto-generated method stub
		
	}



}
