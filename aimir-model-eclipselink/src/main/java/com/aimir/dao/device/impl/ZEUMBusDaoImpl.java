package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ZEUMBusDao;
import com.aimir.model.device.ZEUMBus;
import com.aimir.util.Condition;

@Repository(value = "zeumbusDao")
public class ZEUMBusDaoImpl extends AbstractJpaDao<ZEUMBus, Integer> implements ZEUMBusDao {

    Log log = LogFactory.getLog(ZEUMBusDaoImpl.class);
    
    public ZEUMBusDaoImpl() {
		super(ZEUMBus.class);
	}

    // ZEUMBus Modem 정보 가져오기 : ID 기준
	public ZEUMBus getModem(Integer id) {
	    return get(id);
	}
	
	public ZEUMBus get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZEUMBus Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<ZEUMBus> getModem() {
	    return getAll();
	}

	// ZEUMBus Modem 정보 저장
	public Serializable setModem(ZEUMBus modem) {
	    return add(modem);
	}

    @Override
    public Class<ZEUMBus> getPersistentClass() {
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