package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ZMUDao;
import com.aimir.model.device.ZMU;
import com.aimir.util.Condition;

@Repository(value = "zmuDao")
public class ZMUDaoImpl extends AbstractJpaDao<ZMU, Integer> implements ZMUDao {

    Log log = LogFactory.getLog(ZMUDaoImpl.class);
    
    public ZMUDaoImpl() {
		super(ZMU.class);
	}

    // ZMU Modem 정보 가져오기 : ID 기준
	public ZMU getModem(Integer id) {
	    return get(id);
	}	

	public ZMU get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZMU Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<ZMU> getModem() {
	    return getAll();
	}

	// ZMU Modem 정보 저장
	public Serializable setModem(ZMU modem) {
	    return add(modem);
	}

    @Override
    public Class<ZMU> getPersistentClass() {
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