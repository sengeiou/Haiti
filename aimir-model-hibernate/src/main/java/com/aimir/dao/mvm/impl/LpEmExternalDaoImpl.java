package com.aimir.dao.mvm.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.LpEmExternalDao;
import com.aimir.model.mvm.LpEmExternal;
import com.aimir.model.mvm.LpExPk;

@Repository(value="lpemexternalDao")
public class LpEmExternalDaoImpl extends AbstractHibernateGenericDao<LpEmExternal, LpExPk> implements LpEmExternalDao{
	
	@Autowired
    protected LpEmExternalDaoImpl(SessionFactory sessionFactory) {
        super(LpEmExternal.class);
        super.setSessionFactory(sessionFactory);
    }

}
