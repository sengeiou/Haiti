package com.aimir.service.system.homeDeviceMgmt;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.MCU;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.HomeDeviceDrLevel;
import com.aimir.model.system.HomeGroup;

/**
 * HomeDeviceMgmtManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 29.   v1.0       eunmiae         
 *
 */
public interface HomeDeviceMgmtManager {
	public HomeGroup getHomeGroupByMember(String member);
	public HomeGroup getHomeGroupById(int id);
	public List<Map<String, Object>> getHomeDeviceInfo(String groupId, String homeDeviceGroupName, String homeDeviceCategory);
	public List<Object> getHomeDeviceGroupSelected(String groupId);
	public List<Object> getHomeDeviceCategorySelected(String groupId);
	public void updateMappingInfo(int id, int modemId);
	public int updateGroupMember(int id, String member) throws Exception;
	public void delete(int memberId);
	public void updateEndDeviceInfo(int id, String homeDeviceGroupName, String friendlyName);
	public void resetMappingInfo(int modemId, int categoryId);
	public List<Map<String, Object>> getHomeDeviceGroupCnt(String groupId);
	public List<Map<String, Object>> getHomeDeviceDrLevelByCondition(int categoryId, String drLevel);
	public HomeDeviceDrLevel getHomeDeviceDrLevelByDrLevel(String drLevel);
	public List<HomeDeviceDrLevel> getHomeDeviceDrLevel();
	public void resetMappingInfo(int endDeviceId);
	public MCU getHomeGroupMcuByGroupId(Integer groupId);
}
