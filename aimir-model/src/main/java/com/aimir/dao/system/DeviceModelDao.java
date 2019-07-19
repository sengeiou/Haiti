package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.DeviceModel;

public interface DeviceModelDao extends GenericDao<DeviceModel, Integer>{
	
	/**
	 * method name : getDeviceModels
	 * method Desc : 제조사에 해당하는 장비 보델 리스트 객체를 리턴한다.
	 * 
	 * @param vendorId 제조사 정보의 아이디 DeviceVendor.id
	 * @return @see com.aimir.model.system.DeviceModel
	 */
	public List<DeviceModel> getDeviceModels(Integer vendorId);
	
	/**
	 * method name : getDeviceModels
	 * method Desc : 제조사에 해당하는 장비 보델 리스트 객체를 리턴한다.
	 * 
	 * @param vendorId 제조사 정보의 이름 DeviceVendor.name
	 * @return @see com.aimir.model.system.DeviceModel
	 */
	public List<DeviceModel> getDeviceModels(String vendorName);
	
	/**
	 * method name : getDeviceModels
	 * method Desc : 제조사, 장비 타입에 해당하는 장비 보델 리스트 객체를 리턴한다.
	 * 
	 * @param vendorId 제조사 정보의 아이디 DeviceVendor.id
	 * @param deviceTypeId 장비 타입 아이디 DeviceType.id
	 * @return @see com.aimir.model.system.DeviceModel
	 */
	public List<DeviceModel> getDeviceModels(int vendorId, int deviceTypeId);
	
	/**
	 * method name : getDeviceModels
	 * method Desc : 조건에 해당하는 장비 보델 리스트 객체를 리턴한다.
	 * 
	 * @param condition 제조사 정보의 아이디, 장비 타입 아이디 DeviceType.id가 된다. 컨디션 파라미터가 여러개 올수 있으나 실제로 두가지 타입에 대해서만 처리한다.
	 * 			
	 * {@code}	int vendorId	         = Integer.parseInt(condition.get("vendorId").toString());
	 *          String subDeviceType	 = StringUtil.nullToBlank(condition.get("subDeviceType"));
			
	 * @return @see com.aimir.model.system.DeviceModel
	 */
	public List<DeviceModel> getDeviceModels(Map<String, Object> condition);
	
	/**
	 * 
	 * method name : getDeviceModelByName
	 * method Desc : 공급사 정보와 , 장비 모델 이름으로 해당되는 장비 모델 리스트 객체를 리턴한다.
	 * 
	 * @param supplierId Supplier.id
	 * @param name DeviceModel.name
	 * @return @see com.aimir.model.system.DeviceModel
	 */
	public List<DeviceModel> getDeviceModelByName(Integer supplierId, String name);
	
	/**
	 * 
	 * method name : getDeviceModelByCode
	 * method Desc : 공급사 정보와 , 장비코드, 장비 타입으로 해당되는 장비 모델 리스트 객체를 리턴한다.
	 * 
	 * @param supplierId  Supplier.id
	 * @param code DeviceModel.code
	 * @param deviceTypeCodeId 장비 타입에 해당하는 코드의 아이디 Code.id
	 * @return  @see com.aimir.model.system.DeviceModel
	 */
	public DeviceModel getDeviceModelByCode(Integer supplierId, Integer code, Integer deviceTypeCodeId);
	
	/**
	 * method name : getDeviceModelByTypeId
	 * method Desc : 공급사 정보와 , 장비 타입으로 해당되는 장비 모델 리스트 객체를 리턴한다.
	 * 
	 * @param supplierId  Supplier.id
	 * @param typeId 장비 타입에 해당하는 코드의 아이디 Code.id
	 * @return  @see com.aimir.model.system.DeviceModel
	 */
	public List<DeviceModel> getDeviceModelByTypeId(Integer supplierId, Integer typeId);
	
	/**
	 * method name : getDeviceModelBySupplierId
	 * method Desc : 공급사 정보로  해당되는 장비 모델 리스트 객체를 리턴한다.
	 * 
	 * @param supplier_id  Supplier.id
	 * @return  @see com.aimir.model.system.DeviceModel
	 */
	public List<DeviceModel> getDeviceModelBySupplierId(Integer supplier_id);
	
	/**
	 * method name : getDeviceModelByTypeIdUnknown
	 * method Desc : 공급사 정보로 Unknown 타입인 모델 리스트를 리턴한다.
	 * 
	 * @param supplier_id Supplier.id
	 * @return  @see com.aimir.model.system.DeviceModel
	 */
	public List<DeviceModel> getDeviceModelByTypeIdUnknown(Integer supplier_id);

	
	/**
	 * method name : getMCUDeviceModel
	 * method Desc : 조회조건으로 집중기에 해당하는 장비 모델 정보를 가져온다.조
	 * 
	 * @param inCondition 집정기 타입에 대한 데이터가 조회된다.//TODO 파라미터 컨디션 세분화 필요
	 * @return
	 */
	public List<Map<String, String>> getMCUDeviceModel(String inCondition);

}
