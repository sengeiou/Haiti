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
import com.aimir.dao.device.ZEUPLSDao;
import com.aimir.model.device.ZEUPLS;

@Repository(value = "zeuplsDao")
public class ZEUPLSDaoImpl extends AbstractHibernateGenericDao<ZEUPLS, Integer> implements ZEUPLSDao {

    Log log = LogFactory.getLog(ZEUPLSDaoImpl.class);
    
	@Autowired
	protected ZEUPLSDaoImpl(SessionFactory sessionFactory) {
		super(ZEUPLS.class);
		super.setSessionFactory(sessionFactory);
	}

    // ZEUPLS Modem 정보 가져오기 : ID 기준
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public ZEUPLS getModem(Integer id) {
		return (ZEUPLS) getSession().get(ZEUPLS.class, id);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public ZEUPLS get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZEUPLS Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<ZEUPLS> getModems() {
		Criteria criteria = getSession().createCriteria(ZEUPLS.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// ZEUPLS Modem 정보 저장
	public Serializable setModem(ZEUPLS modem) {
	    modem.setModemType(ModemType.ZEUPLS.name());
		return getSession().save(modem);
	}
}