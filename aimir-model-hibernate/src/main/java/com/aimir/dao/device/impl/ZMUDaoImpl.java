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
import com.aimir.dao.device.ZMUDao;
import com.aimir.model.device.ZMU;

@Repository(value = "zmuDao")
public class ZMUDaoImpl extends AbstractHibernateGenericDao<ZMU, Integer> implements ZMUDao {

    Log log = LogFactory.getLog(ZMUDaoImpl.class);
    
	@Autowired
	protected ZMUDaoImpl(SessionFactory sessionFactory) {
		super(ZMU.class);
		super.setSessionFactory(sessionFactory);
	}

    // ZMU Modem 정보 가져오기 : ID 기준
	public ZMU getModem(Integer id) {
		return (ZMU) getSession().get(ZMU.class, id);
	}	

	public ZMU get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZMU Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<ZMU> getModem() {
		Criteria criteria = getSession().createCriteria(ZMU.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// ZMU Modem 정보 저장
	public Serializable setModem(ZMU modem) {
	    modem.setModemType(ModemType.ZMU.name());
		return getSession().save(modem);
	}
}