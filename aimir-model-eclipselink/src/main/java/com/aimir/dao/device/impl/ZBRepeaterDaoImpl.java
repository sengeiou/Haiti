package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ZBRepeaterDao;
import com.aimir.model.device.ZBRepeater;
import com.aimir.util.Condition;

@Repository(value = "zbrepeaterDao")
public class ZBRepeaterDaoImpl extends AbstractJpaDao<ZBRepeater, Integer> implements ZBRepeaterDao {

    Log log = LogFactory.getLog(ZBRepeaterDaoImpl.class);
    
    public ZBRepeaterDaoImpl() {
		super(ZBRepeater.class);
	}
	
	public ZBRepeater get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZBRepeater Modem 정보 가져오기 : ID 기준
	public ZBRepeater getModem(Integer id) {
	    return get(id);
	}

    // ZBRepeater Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<ZBRepeater> getModem() {
	    return getAll();
	}

	// ZBRepeater Modem 정보 저장
	public Serializable setModem(ZBRepeater modem) {
	    return add(modem);
	}

    @Override
    public Class<ZBRepeater> getPersistentClass() {
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