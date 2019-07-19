package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.LTEDao;
import com.aimir.model.device.LTE;
import com.aimir.util.Condition;

@Repository(value = "LTEDao")
public class LTEDaoImpl extends AbstractJpaDao<LTE, Integer> implements LTEDao {

	private static Log log = LogFactory.getLog(LTEDaoImpl.class);

	public LTEDaoImpl() {
	    super(LTE.class);
	}

	// LTE Modem 정보 가져오기 : ID 기준
	public LTE getModem(Integer id) {
		return (LTE) get(id);
	}

	public LTE get(String deviceSerial) {
		return findByCondition("deviceSerial", deviceSerial);
	}

	// LTE Modem 정보 가져오기 : 전체 List
	public List<LTE> getModem() {
	    return getAll();
	}

	// LTE Modem 정보 저장
	public Serializable setModem(LTE modem) {
	    modem.setModemType(ModemType.LTE.name());
	    add(modem);
		return modem;
	}

    @Override
    public Class<LTE> getPersistentClass() {
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