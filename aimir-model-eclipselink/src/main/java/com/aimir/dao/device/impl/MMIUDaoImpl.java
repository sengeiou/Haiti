package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.model.device.MMIU;
import com.aimir.util.Condition;

@Repository(value = "mmiuDao")
public class MMIUDaoImpl extends AbstractJpaDao<MMIU, Integer> implements MMIUDao {

    Log log = LogFactory.getLog(MMIUDaoImpl.class);
    
    public MMIUDaoImpl() {
		super(MMIU.class);
	}

    // MMIU Modem 정보 가져오기 : ID 기준
	public MMIU getModem(Integer id) {
	    return get(id);
	}

    // MMIU Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<MMIU> getModem() {
	    return getAll();
	}

	// MMIU Modem 정보 저장
	public Serializable setModem(MMIU modem) {
	    return add(modem);
	}

    @Override
    public Class<MMIU> getPersistentClass() {
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