package com.aimir.service.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimir.model.device.EndDevice;
import com.aimir.model.device.EndDeviceVO;

/**
 * EndDeviceManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 26.   v1.0       -         
 * 2011. 5. 26    v1.1       eunmiae  EndDevice취득(키:serialNumber)
 * 2011. 6. 30    v1.2       김상연        EndDeviceList 조회 (조건 : EndDevice)
 */
public interface EndDeviceManager {
	
	public List<EndDeviceVO> getEndDevices();
	public List<EndDeviceVO> getEndDevices(int page, int count);

	// EndDevice정보 취득 ( 조건 : serialNumber )
	public EndDevice getEndDevice(String serialNumber);

	public EndDevice addEndDevice(EndDevice endDevice);
	
	public void delete(Integer endDeviceId);
	
	public EndDevice getEndDevice(Integer endDeviceId);
	
	public void updateEndDevice(EndDevice endDevice);
	
	public List<EndDevice> getEndDevicesByLocationId(int locationId,int page,int count);
	public List<EndDevice> getEndDevicesByLocationId(int locationId);
	public List<EndDevice> getEndDevicesByCodeId(Integer codeId);
	public List<EndDevice> getEndDevicesList();
	
	public List<EndDeviceVO> getEndDevicesVOByLocationId(int locationId,int endDeviceId,int page,int count,boolean metering);
	public List<EndDeviceVO> getEndDevicesVOByLocationIdExt(int locationId,int endDeviceId,int page,int count,boolean metering);
	
	public Map<String, Object> getMetaData(int supplierId);
	
	public HashMap<String, Object> getCompareFacilityDayData(Map<String, Object> condition);
	
	public HashMap<String, Object> getCompareFacilityMonthData(Map<String, Object> condition);

	/**
	 * 입력된 Zone 의 최하위 zone 에 할당된 EndDevice 목록을 조회한다.
	 * @param zoneId
	 * @return
	 */
	public List<Object> getEndDeviceByZone(Map<String, Object> condition);
	/**
	 * 입력된 Location 에 할당된 EndDevice 목록을 조회한다.
	 * @param zoneId
	 * @return
	 */
	public List<Object> getEndDeviceByLocation(Map<String,Object> params);
	
	/**
	 * EndDevice 의 Zone 을 null 로 업데이트 한다.
	 * @param endDeviceIdList
	 */
	public void removeEndDeviceFromZone(List<Object> endDeviceIdList);
	
	/**
	 * 입력받은 Zone 을 EndDevice 에 업데이트 한다.
	 * @param params
	 */
	public void addEndDeviceToZone(Map<String,Object> params);
	
	/**
	 * method name : getEndDeviceList
	 * method Desc : EndDeviceList 조회 (조건 : EndDevice)
	 *
	 * @param endDevice
	 */
	public List<EndDevice> getEndDeviceList(EndDevice endDevice);
	
	public long getTotalSize(int locationId);
}
