/**
 * EndDeviceDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.model.device.EndDevice;
import com.aimir.util.Condition;

/**
 * EndDeviceDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 12.   v1.0       김상연         EndDevice 조회 - 조건(EndDevice)
 *
 */
@Repository(value = "enddeviceDao")
public class EndDeviceDaoImpl extends AbstractJpaDao<EndDevice, Integer> implements EndDeviceDao {

	public EndDeviceDaoImpl() {
		super(EndDevice.class);
	}

    @Override
    public List<EndDevice> getEndDevices(int page, int count) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDevice> getEndDevicesByLocationId(int locationId, int page,
            int count) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDevice> getEndDevicesByLocationId(int locationId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDevice> getEndDevicesByLocationIds(
            List<Integer> locationIds, int start, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getTotalSize(List<Integer> locationIds) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void updateZoneOfEndDevice(EndDevice endDevice) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<EndDevice> getEndDevicesByCategories(List<Integer> categories) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDevice> getEndDevicesByzones(List<Integer> zones) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDevice> getEndDevicesByLocations(List<Integer> locationIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDevice> getEndDevicesByParentLocations(
            List<Integer> locationIds, List<Integer> categories) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getEndDeviceTypeAndStatusCountByZones(
            List<Integer> zones) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getGroupMember(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDevice> getEndDevices(EndDevice endDevice) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getHomeDeviceInfo(String groupId,
            String homeDeviceGroupName, String homeDeviceCategory) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDevice> getMappingHomeDevice(int modeId, int codeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getHomeDeviceGroupSelected(String groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getHomeDeviceCategorySelected(String groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateMappingInfo(int id, int modemId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateEndDeviceInfo(int id, String homeDeviceGroupName,
            String friendlyName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resetMappingInfo(int modemId, int categoryId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Map<String, Object>> getHomeDeviceGroupCnt(String groupId,
            int smartConcent, int generalAppliance) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateEndDeviceInstallStatus(int installStatusCode,
            String serialNumber, int categoryCode) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Map<String, Object>> getHomeDeviceInfoForDrMgmt(String groupId,
            String homeDeviceGroupName, String homeDeviceCategory) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EndDevice> getMappingHomeDeviceForDrMgmt(int modeId, int codeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateDrProgramMandatoryInfo(int id, String drProgramMandatory) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resetMappingInfo(int endDeviceId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateEndDeviceDrLevel(int endDeviceId, int categoryCode,
            int drLevel) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<EndDevice> getEndDeviceByFriendlyName(String friendlyName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<EndDevice> getPersistentClass() {
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
