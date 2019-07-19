package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.PrepaymentAuthDeviceDao;
import com.aimir.model.system.PrepaymentAuthDevice;
import com.aimir.util.Condition;

@Repository(value = "prepaymentAuthDeviceDao")
public class PrepaymentAuthDeviceDaoImpl extends AbstractJpaDao<PrepaymentAuthDevice, Integer> implements PrepaymentAuthDeviceDao {

    Log logger = LogFactory.getLog(PrepaymentAuthDeviceDaoImpl.class);
        
    public PrepaymentAuthDeviceDaoImpl() {
        super(PrepaymentAuthDevice.class);
    }

    @Override
    public Class<PrepaymentAuthDevice> getPersistentClass() {
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
