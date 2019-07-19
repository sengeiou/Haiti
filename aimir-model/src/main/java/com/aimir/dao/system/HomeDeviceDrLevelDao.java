/**
 * NotificationTemplateDao.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.HomeDeviceDrLevel;

/**
 * HomeDeviceDrLevelDao.java Description 
 * 홈 디바이스의 DR레벨을 관리한다.
 * 
 * Date          Version     Author   Description
 * 2011. 6. 14.   v1.0       eunmiae  초판 생성       
 *
 */
public interface HomeDeviceDrLevelDao extends GenericDao<HomeDeviceDrLevel, Integer> {
	
	/**
     * method name : getHomeDeviceDrLevelByCondition
     * method Desc : category와 drLevel에 일치하는 HomeDeviceDrLevel 목록을 리턴한다.
     * 
	 * @param categoryId HomeDeviceDrLevel.category.id
	 * @param drLevel HomeDeviceDrLevel.drLevel
	 * @return List Of Map {  	HomeDeviceDrLevel.drname AS drname,
	 * 							HomeDeviceDrLevel.drlevel AS drlevel,
	 * 							HomeDeviceDrLevel.category_id AS categoryId}
	 */
	public List<Map<String, Object>> getHomeDeviceDrLevelByCondition(int categoryId, String drLevel);
}
