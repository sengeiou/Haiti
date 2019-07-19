package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ConverterDao;
import com.aimir.model.device.Converter;
import com.aimir.util.Condition;

@Repository(value = "converterDao")
public class ConverterDaoImpl extends AbstractJpaDao<Converter, Integer> implements ConverterDao {

    Log log = LogFactory.getLog(ConverterDaoImpl.class);
    
	public ConverterDaoImpl() {
		super(Converter.class);
	}
	
	public Converter get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    @Override
    public Converter getModem(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Converter> getModem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Serializable setModem(Converter modem) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Converter> getPersistentClass() {
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