package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.PLCIUDao;
import com.aimir.model.device.PLCIU;
import com.aimir.util.Condition;

@Repository(value = "plciuDao")
public class PLCIUDaoImpl extends AbstractJpaDao<PLCIU, Integer> implements PLCIUDao {

	private static Log log = LogFactory.getLog(PLCIUDaoImpl.class);
    
	public PLCIUDaoImpl() {
		super(PLCIU.class);
	}

    // PLCIU Modem 정보 가져오기 : ID 기준
	public PLCIU getModem(Integer id) {
	    return get(id);
	}

    // PLCIU Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<PLCIU> getModem() {
	    return getAll();
	}

	// PLCIU Modem 정보 저장
	public Serializable setModem(PLCIU modem, ModemType modemType) {
		modem.setModemType(modemType.name());
	    return add(modem);
	}

    @Override
    public Class<PLCIU> getPersistentClass() {
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