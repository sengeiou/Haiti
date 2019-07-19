package com.aimir.dao.device.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.ChangeLogSettingDao;
import com.aimir.model.device.ChangeLogSetting;

@Repository(value = "changelogsettingDao")
public class ChangeLogSettingDaoImpl extends AbstractHibernateGenericDao<ChangeLogSetting, Long> implements ChangeLogSettingDao {
	@Autowired
	protected ChangeLogSettingDaoImpl(SessionFactory sessionFactory) {
		super(ChangeLogSetting.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<ChangeLogSetting> getChangeLogSettings(String[] array) {

		//currentDevice[0], isLoggingCode[1], levelCode[2]
		@SuppressWarnings("unused")
        String currentDevice = array[0];
		@SuppressWarnings("unused")
        String isLoggingCode = array[1];
        @SuppressWarnings("unused")
		String levelCode = array[2];
						
		Criteria criteria = getSession().createCriteria(ChangeLogSetting.class);		
		
//		if(!"".equals(currentDevice))
//			criteria.add(Restrictions.eq("id", currentDevice));	
		
		return criteria.list();		
	}	
}
