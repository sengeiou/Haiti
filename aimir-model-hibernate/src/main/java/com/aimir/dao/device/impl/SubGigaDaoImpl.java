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
import com.aimir.dao.device.SubGigaDao;
import com.aimir.model.device.SubGiga;

@Repository(value = "subgigaDao")
public class SubGigaDaoImpl extends AbstractHibernateGenericDao<SubGiga, Integer> implements SubGigaDao {

    private static Log log = LogFactory.getLog(SubGiga.class);
    
	@Autowired
	protected SubGigaDaoImpl(SessionFactory sessionFactory) {
		super(SubGiga.class);
		super.setSessionFactory(sessionFactory);
	}

    // SubGiga Modem 정보 가져오기 : ID 기준
	public SubGiga getModem(Integer id) {
		return (SubGiga) getSession().get(SubGiga.class, id);
	}

	
	public SubGiga get(Integer id) {
		return (SubGiga) getSession().get(SubGiga.class, id);
	}
	
	public SubGiga get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // SubGiga Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<SubGiga> getModem() {
		Criteria criteria = getSession().createCriteria(SubGiga.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// SubGiga Modem 정보 저장
	public Serializable setModem(SubGiga modem) {
        modem.setModemType(ModemType.SubGiga.name());
		return getSession().save(modem);
	}
}