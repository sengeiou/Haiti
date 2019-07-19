/**
 * NotificationTemplateDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl.energySavingGoal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.energySavingGoal.NotificationTemplateDao;
import com.aimir.model.system.NotificationTemplate;

/**
 * NotificationTemplateDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 21.   v1.0       김상연         에너지 절감 목표
 *
 */

@Repository(value = "notificationTemplateDao")
public class NotificationTemplateDaoImpl 
				extends AbstractHibernateGenericDao<NotificationTemplate, Integer> 
				implements NotificationTemplateDao {

	@Autowired
	protected NotificationTemplateDaoImpl(SessionFactory sessionFactory) {
		
		super(NotificationTemplate.class);
		super.setSessionFactory(sessionFactory);
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.system.energySavingGoal.NotificationTemplateDao#getNotificationTemplateList(com.aimir.model.system.NotificationTemplate)
	 */
	@SuppressWarnings("unchecked")
	public List<NotificationTemplate> getNotificationTemplateList(NotificationTemplate notificationTemplate) {

		Criteria criteria = getSession().createCriteria(NotificationTemplate.class);
		
		if (notificationTemplate != null) {
			
			if (notificationTemplate.getId() != null) {
				
				criteria.add(Restrictions.eq("id", notificationTemplate.getId()));
			}
			
			if (notificationTemplate.getName() != null) {
				
				criteria.add(Restrictions.eq("name", notificationTemplate.getName()));
				criteria.addOrder(Order.desc("name"));
			}
		}
		
		return criteria.list();
	}

}
