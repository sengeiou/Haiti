package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MCUInstallImgDao;
import com.aimir.model.device.MCUInstallImg;

@Repository(value = "mcuinstallimgDao")
public class MCUInstallImgDaoImpl  extends AbstractHibernateGenericDao<MCUInstallImg, Long> implements MCUInstallImgDao {

    Log logger = LogFactory.getLog(MCUInstallImgDaoImpl.class);
    
	@Autowired
	protected MCUInstallImgDaoImpl(SessionFactory sessionFactory) {
		super(MCUInstallImg.class);
		super.setSessionFactory(sessionFactory);
	}
}
