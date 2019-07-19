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
import com.aimir.dao.device.IEIUDao;
import com.aimir.model.device.IEIU;

@Repository(value = "ieiuDao")
public class IEIUDaoImpl extends AbstractHibernateGenericDao<IEIU, Integer> implements IEIUDao {

    Log log = LogFactory.getLog(IEIUDaoImpl.class);
    
	@Autowired
	protected IEIUDaoImpl(SessionFactory sessionFactory) {
		super(IEIU.class);
		super.setSessionFactory(sessionFactory);
	}

    // IEIU Modem 정보 가져오기 : ID 기준
	public IEIU getModem(Integer id) {
		return (IEIU) getSession().get(IEIU.class, id);
	}

    // IEIU Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<IEIU> getModem() {
		Criteria criteria = getSession().createCriteria(IEIU.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// IEIU Modem 정보 저장
	public Serializable setModem(IEIU modem) {
        modem.setModemType(ModemType.IEIU.name());
		return getSession().save(modem);
	}
}