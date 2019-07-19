package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.ZEUMBusDao;
import com.aimir.model.device.ZEUMBus;

@Repository(value = "zeumbusDao")
public class ZEUMBusDaoImpl extends AbstractHibernateGenericDao<ZEUMBus, Integer> implements ZEUMBusDao {

    Log log = LogFactory.getLog(ZEUMBusDaoImpl.class);
    
	@Autowired
	protected ZEUMBusDaoImpl(SessionFactory sessionFactory) {
		super(ZEUMBus.class);
		super.setSessionFactory(sessionFactory);
	}

    // ZEUMBus Modem 정보 가져오기 : ID 기준
	public ZEUMBus getModem(Integer id) {
		return (ZEUMBus) getSession().get(ZEUMBus.class, id);
	}
	
	public ZEUMBus get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZEUMBus Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<ZEUMBus> getModem() {
		Criteria criteria = getSession().createCriteria(ZEUMBus.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// ZEUMBus Modem 정보 저장
	public Serializable setModem(ZEUMBus modem) {
        modem.setModemType(ModemType.ZEU_MBus.name());
		return getSession().save(modem);
	}
}