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
import com.aimir.dao.device.MMIUDao;
import com.aimir.model.device.MMIU;

@Repository(value = "mmiuDao")
public class MMIUDaoImpl extends AbstractHibernateGenericDao<MMIU, Integer> implements MMIUDao {

    Log log = LogFactory.getLog(MMIUDaoImpl.class);
    
	@Autowired
	protected MMIUDaoImpl(SessionFactory sessionFactory) {
		super(MMIU.class);
		super.setSessionFactory(sessionFactory);
	}

    // MMIU Modem 정보 가져오기 : ID 기준
	public MMIU getModem(Integer id) {
		return (MMIU) getSession().get(MMIU.class, id);
	}

    // MMIU Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<MMIU> getModem() {
		Criteria criteria = getSession().createCriteria(MMIU.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// MMIU Modem 정보 저장
	public Serializable setModem(MMIU modem) {
        modem.setModemType(ModemType.MMIU.name());
		return getSession().save(modem);
	}
}