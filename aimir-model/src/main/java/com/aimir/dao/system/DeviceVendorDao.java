package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.DeviceVendor;

public interface DeviceVendorDao extends GenericDao<DeviceVendor, Integer>{

	/**
	 * 
	 * method name : getDeviceVendorsOrderByName
	 * method Desc : 공급사 아이디 정보로 해당하는 장비 벤더 정보 목록을 리턴한다.
	 * 
	 * @return @see com.aimir.model.system.DeviceVendor
	 */
	public List<DeviceVendor> getDeviceVendorsOrderByName();

	/**
	 * method name : getDeviceVendorsForTree
	 * method Desc : 공급사 아이디 정보로 해당하는 장비 벤더 및 하위 트리 정보(DeviceModel) 정보까지 추출한다.
	 * 
	 * @param supplierId Supplier.id
	 * @return DeviceType.name
	 *         DeviceType.id
	 *         DeviceVendor.name
	 *         DeviceVendor.id
	 *         DeviceModel.name
	 *         DeviceModel.id
	 *         Supplier.id
	 */
	public List<Object[]> getDeviceVendorsForTree(Integer supplierId);

			
	/**
	 * 
	 * method name : getDeviceVendorByName
	 * method Desc : 공급사 아이디 정보와, 제조사 이름으로 해당하는 장비 벤더 리스트 정보를 추출한다.
	 * 
	 * @param supplierId Supplier.id
	 * @param name DeviceVendor.name
	 * @return @see com.aimir.model.system.DeviceVendor
	 */
	public List<DeviceVendor> getDeviceVendorByName(Integer supplierId, String name);
	
	/**
	 * 
	 * method name : getDeviceVendorByCode
	 * method Desc : 공급사 아이디 정보와, 제조사 고유코드로 해당하는 장비 벤더 리스트 정보를 추출한다.
	 * 
	 * @param supplierId Supplier.id
	 * @param code DeviceVendor.code
	 * @return @see com.aimir.model.system.DeviceVendor
	 */
	public List<DeviceVendor> getDeviceVendorByCode(Integer supplierId, Integer code);
}
