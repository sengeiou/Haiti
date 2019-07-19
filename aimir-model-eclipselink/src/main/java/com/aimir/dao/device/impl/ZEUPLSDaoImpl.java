package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ZEUPLSDao;
import com.aimir.model.device.ZEUPLS;
import com.aimir.util.Condition;

@Repository(value = "zeuplsDao")
public class ZEUPLSDaoImpl extends AbstractJpaDao<ZEUPLS, Integer> implements ZEUPLSDao {

    Log log = LogFactory.getLog(ZEUPLSDaoImpl.class);
    
    public ZEUPLSDaoImpl() {
		super(ZEUPLS.class);
	}

    // ZEUPLS Modem 정보 가져오기 : ID 기준
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public ZEUPLS getModem(Integer id) {
	    return get(id);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public ZEUPLS get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZEUPLS Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<ZEUPLS> getModems() {
	    return getAll();
	}

	// ZEUPLS Modem 정보 저장
	public Serializable setModem(ZEUPLS modem) {
	    return add(modem);
	}

    @Override
    public Class<ZEUPLS> getPersistentClass() {
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