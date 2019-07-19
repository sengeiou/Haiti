package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.IEIUDao;
import com.aimir.model.device.IEIU;
import com.aimir.util.Condition;

@Repository(value = "ieiuDao")
public class IEIUDaoImpl extends AbstractJpaDao<IEIU, Integer> implements IEIUDao {

    Log log = LogFactory.getLog(IEIUDaoImpl.class);
    
    public IEIUDaoImpl() {
		super(IEIU.class);
	}

    // IEIU Modem 정보 가져오기 : ID 기준
	public IEIU getModem(Integer id) {
		return (IEIU) get(id);
	}

    // IEIU Modem 정보 가져오기 : 전체 List
	public List<IEIU> getModem() {
	    return getAll();
	}

	// IEIU Modem 정보 저장
	public Serializable setModem(IEIU modem) {
	    add(modem);
	    return modem;
	}

    @Override
    public Class<IEIU> getPersistentClass() {
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