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
import com.aimir.dao.device.ZRUDao;
import com.aimir.model.device.ZRU;
import com.aimir.util.Condition;

@Repository(value = "zruDao")
public class ZRUDaoImpl extends AbstractJpaDao<ZRU, Integer> implements ZRUDao {

    Log log = LogFactory.getLog(ZRUDaoImpl.class);
    
    public ZRUDaoImpl() {
		super(ZRU.class);
	}

    // ZRU Modem 정보 가져오기 : ID 기준
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public ZRU getModem(Integer id) {
	    return get(id);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public ZRU get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZRU Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<ZRU> getModem() {
	    return getAll();
	}

	// ZRU Modem 정보 저장
	public Serializable setModem(ZRU modem) {
	    return add(modem);
	}

    @Override
    public Class<ZRU> getPersistentClass() {
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