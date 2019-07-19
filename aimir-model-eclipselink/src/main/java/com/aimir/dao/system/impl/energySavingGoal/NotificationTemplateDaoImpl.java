/**
 * NotificationTemplateDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl.energySavingGoal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.energySavingGoal.NotificationTemplateDao;
import com.aimir.model.system.NotificationTemplate;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

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
				extends AbstractJpaDao<NotificationTemplate, Integer> 
				implements NotificationTemplateDao {

	public NotificationTemplateDaoImpl() {
		super(NotificationTemplate.class);
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.system.energySavingGoal.NotificationTemplateDao#getNotificationTemplateList(com.aimir.model.system.NotificationTemplate)
	 */
	public List<NotificationTemplate> getNotificationTemplateList(NotificationTemplate notificationTemplate) {
	    Set<Condition> conditions = new HashSet<Condition>();
	    
		if (notificationTemplate != null) {
			
			if (notificationTemplate.getId() != null) {
			    conditions.add(new Condition("id", new Object[]{notificationTemplate.getId()}, null, Restriction.EQ));
			}
			
			if (notificationTemplate.getName() != null) {
			    conditions.add(new Condition("name", 
			            new Object[]{notificationTemplate.getName()}, null, Restriction.EQ));
			    conditions.add(new Condition("name", 
			            new Object[]{notificationTemplate.getName()}, null, Restriction.ORDERBYDESC));
			}
		}
		
		return findByConditions(conditions);
	}

    @Override
    public Class<NotificationTemplate> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}
