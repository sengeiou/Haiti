/**
 * EndDeviceDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.EndDevice;

/**
 * EndDeviceDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 12.   v1.0       김상연         EndDevice 조회 - 조건(EndDevice)
 *
 */
public interface EndDeviceDao extends GenericDao<EndDevice, Integer> {
	public List<EndDevice> getEndDevices(int page, int count);
	public List<EndDevice> getEndDevicesByLocationId(int locationId,int page,int count);
	public List<EndDevice> getEndDevicesByLocationId(int locationId);
	public List<EndDevice> getEndDevicesByLocationIds(List<Integer> locationIds, int start, int limit);
	public long getTotalSize(List<Integer> locationIds);
	
	
	public void updateZoneOfEndDevice(EndDevice endDevice);
	
	/**
	 * EndDevice 분류목록에 해당하는 EndDevice 목록을 조회한다.
	 * @param categories
	 * @return List<EndDevice>
	 */
	public List<EndDevice> getEndDevicesByCategories(List<Integer> categories);
	
	/**
	 * Zone 에 해당하는 EndDevice 목록을 조회한다.
	 * @param zones
	 * @return
	 */
	public List<EndDevice> getEndDevicesByzones(List<Integer> zones);
	
	/**
	 * Location 에 해당하는 EndDevice 목록을 조회한다.
	 * @param locationIds
	 * @return
	 */
	public List<EndDevice> getEndDevicesByLocations(List<Integer> locationIds);
	
	public List<EndDevice> getEndDevicesByParentLocations(List<Integer> locationIds,List<Integer> categories);
	public List<Object> getEndDeviceTypeAndStatusCountByZones(List<Integer> zones);

	@Deprecated
	public List<Object> getGroupMember(Map<String, Object> condition);

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 End Device 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap);

	/**
	 * method name : getEndDevice
	 * method Desc : EndDevice 조회 - 조건(EndDevice)
	 *
	 * @param endDevice
	 * @return
	 */
	public List<EndDevice> getEndDevices(EndDevice endDevice);
	

	public List<Map<String, Object>> getHomeDeviceInfo(String groupId, String homeDeviceGroupName, String homeDeviceCategory);
	public List<EndDevice> getMappingHomeDevice(int modeId, int codeId);
	public List<Object> getHomeDeviceGroupSelected(String groupId);
	public List<Object> getHomeDeviceCategorySelected(String groupId);
	public void updateMappingInfo(int id, int modemId);
	public void updateEndDeviceInfo(int id, String homeDeviceGroupName, String friendlyName);
	public void resetMappingInfo(int modemId, int categoryId);
	public List<Map<String, Object>> getHomeDeviceGroupCnt(String groupId, int smartConcent, int generalAppliance);
	public void updateEndDeviceInstallStatus(int installStatusCode, String serialNumber, int categoryCode);
	public List<Map<String, Object>> getHomeDeviceInfoForDrMgmt(String groupId, String homeDeviceGroupName, String homeDeviceCategory);
	public List<EndDevice> getMappingHomeDeviceForDrMgmt(int modeId, int codeId);
	public void updateDrProgramMandatoryInfo(int id, String drProgramMandatory);
	public void resetMappingInfo(int endDeviceId);
	public void updateEndDeviceDrLevel(int endDeviceId, int categoryCode, int drLevel);
	
    /**
     * method name : getEndDeviceByFriendlyName<b/>
     * method Desc : HomeGroup Management 가젯에서 FriendlyName을 가진 EndDevice를 구한다.
     *
     * @param String friendlyName
     * @return
     */
    public List<EndDevice> getEndDeviceByFriendlyName(String friendlyName);
}
