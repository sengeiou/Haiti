package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.SubGigaDao;
import com.aimir.model.device.SubGiga;
import com.aimir.util.Condition;

@Repository(value = "subgigaDao")
public class SubGigaDaoImpl extends AbstractJpaDao<SubGiga, Integer> implements SubGigaDao {

    private static Log log = LogFactory.getLog(SubGiga.class);
    
	public SubGigaDaoImpl() {
		super(SubGiga.class);
	}

    @Override
    public SubGiga getModem(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SubGiga get(String deviceSerial) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SubGiga> getModem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Serializable setModem(SubGiga modem) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<SubGiga> getPersistentClass() {
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