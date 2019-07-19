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
import com.aimir.dao.device.PLCIUDao;
import com.aimir.model.device.PLCIU;

@Repository(value = "plciuDao")
public class PLCIUDaoImpl extends AbstractHibernateGenericDao<PLCIU, Integer> implements PLCIUDao {

	private static Log log = LogFactory.getLog(PLCIUDaoImpl.class);
    
	@Autowired
	protected PLCIUDaoImpl(SessionFactory sessionFactory) {
		super(PLCIU.class);
		super.setSessionFactory(sessionFactory);
	}

    // PLCIU Modem 정보 가져오기 : ID 기준
	public PLCIU getModem(Integer id) {
		return (PLCIU) getSession().get(PLCIU.class, id);
	}

    // PLCIU Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<PLCIU> getModem() {
		Criteria criteria = getSession().createCriteria(PLCIU.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// PLCIU Modem 정보 저장
	public Serializable setModem(PLCIU modem, ModemType modemType) {
        modem.setModemType(modemType.name());
		return getSession().save(modem);
	}
}