package com.aimir.service.system.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.util.StringUtil;

@WebService(endpointInterface = "com.aimir.service.system.DeviceModelManager")
@Service(value = "deviceModelManager")
@Transactional
public class DeviceModelManagerImpl implements DeviceModelManager {

	@Autowired
	DeviceModelDao dao;
	
	@Autowired
	CodeDao codeDao;	
	
	public DeviceModel getDeviceModel(int modelId) {
		return dao.get(modelId);
	}

	public DeviceModel addDeviceModel(DeviceModel deviceModel) {
		return dao.add(deviceModel);
	}

	public DeviceModel updateDeviceModel(DeviceModel deviceModel) {
		return dao.update(deviceModel);
	}

//	public List<Object[]> getDeviceTree(int supplierId) {
//		return dao.getDeviceTree(supplierId);
//	}

	public void deleteDeviceModel(DeviceModel deviceModel) {
		dao.delete(deviceModel);
	}
//	
//	public int deleteDeviceModel(int deviceModelId) {
//		return dao.deleteById(deviceModelId);
//	}
	
	public List<DeviceModel> getDeviceModels(Integer vendorId) {
		return dao.getDeviceModels(vendorId);
	}
	
	public List<DeviceModel> getDeviceModels(String vendorName) {
		return dao.getDeviceModels(vendorName);
	}

	public List<DeviceModel> getDeviceModels(int vendorId, int deviceTypeId) {
		return dao.getDeviceModels(vendorId, deviceTypeId);
	}
	
	public List<DeviceModel> getDeviceModels(Map<String, Object> condition){
		
		int vendorId 		   	 = Integer.parseInt(StringUtil.nullToBlank(condition.get("vendorId")));
		String deviceType    	 = StringUtil.nullToBlank(condition.get("deviceType"));
		String subDeviceType	 = StringUtil.nullToBlank(condition.get("subDeviceType"));
		
		if("".equals(subDeviceType)){
			
			List<Code> typeList = new ArrayList<Code>();
			List<DeviceModel> deviceModelList = new ArrayList<DeviceModel>();
			
			// MCU
			if(DeviceType.MCU.toString().equals(deviceType))
				typeList = codeDao.getChildCodes(Code.MCU_TYPE);
							
			// MODEM
			if(DeviceType.Modem.toString().equals(deviceType))
				typeList = codeDao.getChildCodes(Code.MODEM_TYPE);

			// METER
			if(DeviceType.Meter.toString().equals(deviceType))
				typeList = codeDao.getChildCodes(Code.METER_TYPE);
			
			int typeListlen = 0;
			
			if(typeList != null)
				typeListlen = typeList.size();
			
			for(int i = 0 ; i < typeListlen ; i++){
				deviceModelList.addAll(dao.getDeviceModels(vendorId , typeList.get(i).getId()));
			}
				return deviceModelList;
			
		} else {
			return dao.getDeviceModels(condition);
		}
	}
	
	public List<DeviceModel> getDeviceModelByName(Integer supplierId, String name) {
		return dao.getDeviceModelByName(supplierId, name);
	}

	public DeviceModel getDeviceModelByCode(Integer supplierId, Integer code, Integer deviceTypeCodeId) {
		return dao.getDeviceModelByCode(supplierId, code, deviceTypeCodeId);
	}

	public List<DeviceModel> getDeviceModelByTypeId(Integer supplierId,
			Integer typeId) {
		return dao.getDeviceModelByTypeId(supplierId, typeId);
	}

	public List<DeviceModel> getDeviceModelBySupplierId(Integer supplierId) {
		return dao.getDeviceModelBySupplierId(supplierId);
	}
	
	public List<DeviceModel> getDeviceModelByTypeIdUnknown(Integer supplierId) {
		return dao.getDeviceModelByTypeIdUnknown(supplierId);
	}

	public List<Map<String, String>> getMCUDeviceModel() {
		
		List<Code> codeList = codeDao.getChildCodes(Code.MCU_TYPE);
		
		Code code = null;		
		StringBuffer inCondition = new StringBuffer("(");
		
		for(int i = 0, size = codeList.size() ; i < size ; i++) {
		
			code = codeList.get(i);
			
			if(i != 0)		
				inCondition.append(", " + code.getId());
			else
				inCondition.append(code.getId());			
		}
		
		inCondition.append(")");
		
		return dao.getMCUDeviceModel(inCondition.toString());
	}

	

}
