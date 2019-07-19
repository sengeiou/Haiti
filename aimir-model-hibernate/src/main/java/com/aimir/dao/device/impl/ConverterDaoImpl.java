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
import com.aimir.dao.device.ConverterDao;
import com.aimir.model.device.Converter;

@Repository(value = "converterDao")
public class ConverterDaoImpl extends AbstractHibernateGenericDao<Converter, Integer> implements ConverterDao {

    Log log = LogFactory.getLog(ConverterDaoImpl.class);
    
	@Autowired
	protected ConverterDaoImpl(SessionFactory sessionFactory) {
		super(Converter.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public Converter get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}

    // Converter Modem 정보 가져오기 : ID 기준
	public Converter getModem(Integer id) {
		return (Converter) getSession().get(Converter.class, id);
	}

    // Converter Modem 정보 가져오기 : 전체 List
	@SuppressWarnings("unchecked")
	public List<Converter> getModem() {
		Criteria criteria = getSession().createCriteria(Converter.class);
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	// ZBRepeater Modem 정보 저장
	public Serializable setModem(Converter modem) {
        modem.setModemType(ModemType.Converter_Ethernet.name());
		return getSession().save(modem);
	}
}