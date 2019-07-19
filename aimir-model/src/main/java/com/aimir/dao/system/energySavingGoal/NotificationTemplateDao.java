/**
 * NotificationTemplateDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.energySavingGoal;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.NotificationTemplate;

/**
 * NotificationTemplateDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 21.   v1.0       김상연         에너지 절감 목표
 *
 */

public interface NotificationTemplateDao extends GenericDao<NotificationTemplate, Integer> {

	/**
	 * method name : getNotificationTemplateList
	 * method Desc : NotificationTemplate 리스트 조회 (조건 : NotificationTemplate)
	 *
	 * @param notificationTemplate
	 * @return List of NotificationTemplate @see com.aimir.model.system.NotificationTemplate
	 */
	List<NotificationTemplate> getNotificationTemplateList(NotificationTemplate notificationTemplate);

}
