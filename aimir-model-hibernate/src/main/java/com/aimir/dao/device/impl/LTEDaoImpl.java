package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.LTEDao;
import com.aimir.model.device.LTE;

@Repository(value = "LTEDao")
public class LTEDaoImpl extends AbstractHibernateGenericDao<LTE, Integer> implements LTEDao {

	private static Log log = LogFactory.getLog(LTEDaoImpl.class);

	@Autowired
	protected LTEDaoImpl(SessionFactory sessionFactory) {
		super(LTE.class);
		super.setSessionFactory(sessionFactory);
	}

	// LTE Modem 정보 가져오기 : ID 기준
	public LTE getModem(Integer id) {
		return (LTE) getSession().get(LTE.class, id);
	}

	public LTE get(Integer id) {
		return (LTE) getSession().get(LTE.class, id);
	}

	public LTE get(String deviceSerial) {
		return findByCondition("deviceSerial", deviceSerial);
	}

	// LTE Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<LTE> getModem() {
		Criteria criteria = getSession().createCriteria(LTE.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// LTE Modem 정보 저장
	public Serializable setModem(LTE modem) {
		modem.setModemType(ModemType.LTE.name());
		return getSession().save(modem);
	}
}