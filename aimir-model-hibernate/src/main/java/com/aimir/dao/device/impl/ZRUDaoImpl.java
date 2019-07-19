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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.ZRUDao;
import com.aimir.model.device.ZRU;

@Repository(value = "zruDao")
public class ZRUDaoImpl extends AbstractHibernateGenericDao<ZRU, Integer> implements ZRUDao {

    Log log = LogFactory.getLog(ZRUDaoImpl.class);
    
	@Autowired
	protected ZRUDaoImpl(SessionFactory sessionFactory) {
		super(ZRU.class);
		super.setSessionFactory(sessionFactory);
	}

    // ZRU Modem 정보 가져오기 : ID 기준
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public ZRU getModem(Integer id) {
		return (ZRU) getSession().get(ZRU.class, id);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public ZRU get(Integer id) {
		return (ZRU) getSession().get(ZRU.class, id);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public ZRU get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZRU Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<ZRU> getModem() {
		Criteria criteria = getSession().createCriteria(ZRU.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// ZRU Modem 정보 저장
	public Serializable setModem(ZRU modem) {
	    modem.setModemType(ModemType.ZRU.name());
		return getSession().save(modem);
	}
}