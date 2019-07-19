package com.aimir.dao.system.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.PrepaymentAuthDeviceDao;
import com.aimir.model.system.PrepaymentAuthDevice;

@Repository(value = "prepaymentAuthDeviceDao")
public class PrepaymentAuthDeviceDaoImpl extends AbstractHibernateGenericDao<PrepaymentAuthDevice, Integer> implements PrepaymentAuthDeviceDao {

    Log logger = LogFactory.getLog(PrepaymentAuthDeviceDaoImpl.class);
        
    @Autowired
    protected PrepaymentAuthDeviceDaoImpl(SessionFactory sessionFactory) {
        super(PrepaymentAuthDevice.class);
        super.setSessionFactory(sessionFactory);
    }
}
