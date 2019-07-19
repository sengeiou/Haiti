/**
 * NotificationDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl.energySavingGoal;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.energySavingGoal.NotificationDao;
import com.aimir.model.system.Notification;
import com.aimir.util.Condition;

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
				extends AbstractJpaDao<Notification, Integer> 
				implements NotificationDao {

	public NotificationDaoImpl() {
		super(Notification.class);
	}

    @Override
    public Class<Notification> getPersistentClass() {
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
