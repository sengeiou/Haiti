/**
 * NotificationDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl.energySavingGoal;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.energySavingGoal.NotificationDao;
import com.aimir.model.system.Notification;

/**
 * NotificationDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 21.   v1.0       김상연         에너지 절감 목표
 *
 */

@Repository(value = "notificationDao")
public class NotificationDaoImpl
				extends AbstractHibernateGenericDao<Notification, Integer> 
				implements NotificationDao {

	@Autowired
	protected NotificationDaoImpl(SessionFactory sessionFactory) {
		
		super(Notification.class);
		super.setSessionFactory(sessionFactory);
	}

}
