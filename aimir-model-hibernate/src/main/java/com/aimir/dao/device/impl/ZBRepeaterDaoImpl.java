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
import com.aimir.dao.device.ZBRepeaterDao;
import com.aimir.model.device.ZBRepeater;

@Repository(value = "zbrepeaterDao")
public class ZBRepeaterDaoImpl extends AbstractHibernateGenericDao<ZBRepeater, Integer> implements ZBRepeaterDao {

    Log log = LogFactory.getLog(ZBRepeaterDaoImpl.class);
    
	@Autowired
	protected ZBRepeaterDaoImpl(SessionFactory sessionFactory) {
		super(ZBRepeater.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public ZBRepeater get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // ZBRepeater Modem 정보 가져오기 : ID 기준
	public ZBRepeater getModem(Integer id) {
		return (ZBRepeater) getSession().get(ZBRepeater.class, id);
	}

    // ZBRepeater Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<ZBRepeater> getModem() {
		Criteria criteria = getSession().createCriteria(ZBRepeater.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// ZBRepeater Modem 정보 저장
	public Serializable setModem(ZBRepeater modem) {
        modem.setModemType(ModemType.Repeater.name());
		return getSession().save(modem);
	}
}