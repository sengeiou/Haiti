/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmWareManagerImpl
 * 작성일자/작성자 : 2010.12.06 최창희
 * @see 
 *
 * 펌웨어 관리자 페이지 Manager
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.service.device.impl;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.FirmwareCodiDao;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.FirmwareHistoryDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.FirmwareMCUDao;
import com.aimir.dao.device.FirmwareModemDao;
import com.aimir.dao.device.FirmwareTriggerDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareCodi;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.device.FirmwareMCU;
import com.aimir.model.device.FirmwareModem;
import com.aimir.model.device.FirmwareTrigger;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.FirmWareManager;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;
import com.aimir.util.Condition.Restriction;

@Service(value="firmWareManager")
@Transactional(readOnly=false)
public class FirmWareManagerImpl implements FirmWareManager{
	
	@Autowired
	DeviceVendorDao deviceVendorDao;
	
	@Autowired
	DeviceModelDao deviceModelDao;
	
	@Autowired
	FirmwareDao firmwareDao;
	
	@Autowired
	FirmwareMCUDao firmwareMCUDao;
	
	@Autowired
	FirmwareModemDao firmwareModemDao;
	
	@Autowired
	FirmwareCodiDao firmwareCodiDao;
	
	@Autowired
	SupplierDao supplierDao;	

	@Autowired
	CodeDao codeDao;
	
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    FirmwareTriggerDao firmwareTriggerDao;
    
    @Autowired
    FirmwareHistoryDao firmwareHistoryDao;
    
    @Autowired
    FirmwareIssueDao firmwareIssueDao;
    
    @Autowired
    FirmwareIssueHistoryDao firmwareIssueHistoryDao;

    public List<Object> getFirmwareFileList(Map<String, Object> condition) throws Exception {
    	return firmwareDao.getFirmwareFileList(condition);
	}
    
    public List<Object> getFirmwareIssueList(Map<String, Object> condition) throws Exception {
    	return firmwareIssueDao.getFirmwareIssueList(condition);
	}
    
    public List<Object> getFirmwareIssueHistoryList(Map<String, Object> condition) throws Exception {
    	return firmwareIssueHistoryDao.getFirmwareIssueHistoryList(condition);
	}
    
	/**
	 * MCU 배포 리스트 조회 
	 * */
	public List<Object> getFirmwareList(String equip_kind ,String devicemodel_id,String firstResults,String maxResults, String supplierId, String equip_type) {
	    HashMap<String, Object> condition = new HashMap<String, Object>();
	    
		Code code = codeDao.getCodeByName(equip_type);	

		condition.put("equip_typeCD", code.getId());
	    condition.put("equip_kind", equip_kind);
	    condition.put("devicemodel_id", devicemodel_id);
	    condition.put("firstResults", firstResults);
	    condition.put("maxResults", maxResults);	    
	    condition.put("supplierId", supplierId);	
	    condition.put("equip_type", equip_type);

		List<Object> returnList = null;
		
		
		
		if(condition.get("equip_kind").equals("MCU")){
			returnList = firmwareMCUDao.getMcuFirmwareList(condition);			
		}else if(condition.get("equip_kind").equals("Codi")){
			returnList =  firmwareCodiDao.getMcuCodiFirmwareList(condition);
		}else if(condition.get("equip_kind").equals("Modem")){
			returnList = firmwareModemDao.getModemFirmwareList(condition);
		}
		
		return returnList;
	}	
	
	/**
	 * MCU 배포 리스트 조회 전체 count Paging Bar 기능 구현 하기 위해 필요.
	 * */
	public String getFirmwareListCNT(String equip_kind ,String devicemodel_id, String supplierId, String equip_type) {
		Code code = codeDao.getCodeByName(equip_type);	

	     HashMap<String, Object> condition = new HashMap<String, Object>();
	     condition.put("equip_typeCD", code.getId());
	     condition.put("equip_kind", equip_kind);
	     condition.put("devicemodel_id", devicemodel_id);
	     condition.put("supplierId", supplierId);
	     condition.put("equip_type", equip_type);
	     
		String returnString = "";
		if(condition.get("equip_kind").equals("MCU")){
			returnString = firmwareMCUDao.getFirmwareMcuListCNT(condition);
		}else if(condition.get("equip_kind").equals("Codi")){
			returnString =  firmwareCodiDao.getFirmwareMcuCodiListCNT(condition); 
		}else if(condition.get("equip_kind").equals("Modem")){
			returnString = firmwareModemDao.getFirmwareModemListCNT(condition); 
		}
		return returnString ;
	}	
	
	/**
	 * 배포 파일관리 리스트 조회 전체 count Paging Bar 기능 구현 하기 위해 필요.
	 * */
	public String getFirmwareFileMgmListCNT(String equip_kind ,String devicemodel_id, String supplierId, String equip_type) {
		Code code = codeDao.getCodeByName(equip_type);	

	     HashMap<String, Object> condition = new HashMap<String, Object>();
	     condition.put("equip_typeCD", code.getId());
	     condition.put("equip_kind", equip_kind);
	     condition.put("devicemodel_id", devicemodel_id);
	     condition.put("supplierId", supplierId);
	     condition.put("equip_type", equip_type);
	     
		String returnString = firmwareDao.getFirmwareFileMgmListCNT(condition);

		return returnString ;
	}	
	
	/**
	 * 배포 파일관리 리스트 조회 .
	 * */
	public List<Object> getFirmwareFileMgmList(String equip_kind ,String devicemodel_id,String firstResults,String maxResults, String supplierId, String equip_type) {
		Code code = codeDao.getCodeByName(equip_type);	

	     HashMap<String, Object> condition = new HashMap<String, Object>();
	     condition.put("equip_typeCD", code.getId());
	     condition.put("equip_kind", equip_kind);
	     condition.put("devicemodel_id", devicemodel_id);
	     condition.put("supplierId", supplierId);
	     condition.put("equip_type", equip_type);
	     condition.put("firstResults", firstResults);
		 condition.put("maxResults", maxResults);	
	     
	     List<Object> returnList = firmwareDao.getFirmwareFileMgmList(condition);

		return returnList ;
	}	
	
	/**
	 * 배포 리스트 조회에서 Equip Total 을 따로 조회 하는 쿼리 
	 * */
	public String getEquipCnt(Map<String, Object> param){
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());
		
		String returnString = "";
		if(param.get("equip_kind").equals("MCU")){
			returnString = firmwareMCUDao.getMcuEquipCnt(param);
		}else if(param.get("equip_kind").equals("Codi")){
			returnString =  firmwareCodiDao.getMcuCodiEquipCnt(param); 
		}else if(param.get("equip_kind").equals("Modem")){
			returnString = firmwareModemDao.getModemEquipCnt(param); 
		}
		return returnString ;
	}
	
	/**
	 * 해당지역  ID와 MCU별 장비 수 
	 **/
	public String getDistriButeMcuIdCnt(Map<String, Object> param, String location_id,String location_name){
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());
		param.put("location_id", location_id);

		String returnString = "";
		if(param.get("equip_kind").equals("MCU")){
			returnString = firmwareMCUDao.getDistriButeMcuIdCnt(param, location_id, location_name);			
		}else if(String.valueOf(param.get("equip_kind")).indexOf("Codi")>-1){
			returnString =  firmwareCodiDao.getDistriButeCodiIdCnt(param, location_id, location_name);
		}else if(param.get("equip_kind").equals("Modem")){
			returnString = firmwareModemDao.getDistriButeModemIdCnt(param, location_id, location_name);
		}
		return returnString;
	}
	
	/**
	 * 해당지역 MCU ID와 MCU별 장비 리스트 (modem, codi는 id가 트리 형태로 다시 표시 된다.) 
	 **/
	public List<Object> getdistributeMcuIdDivList(Map<String, Object> param, String location_id,String location_name){
		String equip_type = String.valueOf(param.get("equip_type"));
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());
		param.put("gubun", "");

		List<Object> returnList = null;
		
		if(param.get("equip_kind").equals("MCU")){
			returnList = firmwareMCUDao.getdistributeMcuIdDivList(param, location_id, location_name);			
		}else if(param.get("equip_kind").equals("Codi")||String.valueOf(param.get("equip_type")).equals("Codi")){
			returnList =  firmwareCodiDao.getdistributeCodiIdDivList(param, location_id, location_name);
		}else if(param.get("equip_kind").equals("Modem")){
			returnList = firmwareModemDao.getdistributeModemIdDivList(param, location_id, location_name);
		}
		
		return returnList;
	}
	
/*	*//**
	 * 배포시 최하단 device_serial또는mcu_id를 선택 하지 않고
	 * 상위 지역만 선택하였을 경우. mcu_id, device_serial을 재 검색해서 배포 하기 위한 리스트 조회 
	 **//*
	public List<Object> getdistributeMcuIdList(Map<String, Object> param, String location_id,String location_name){
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());
		param.put("gubun", "total");

		List<Object> returnList = null;
		
		if(param.get("equip_kind").equals("MCU")){
			returnList = firmwareMCUDao.getdistributeMcuIdDivList(param, location_id, location_name);			
		}else if(param.get("equip_kind").equals("Codi")){
			returnList =  firmwareCodiDao.getdistributeCodiIdDivList(param, location_id, location_name);
		}else if(param.get("equip_kind").equals("Modem")){
			returnList = firmwareModemDao.getdistributeModemIdDivList(param, location_id, location_name);
		}
		
		return returnList;
	}*/
	

	/**
	 * codi, modem만 실행 됨
	 * mcu별 장비 수를 클릭 하면 id가 Tree형태로 다시 표시 됨. (id count를 미리 출력_보여주기 위함)
	 **/
	public String getDistriButeModemListCnt(Map<String, Object> param, String mcuId){
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());

		String returnString = "";
		if(param.get("equip_kind").equals("Codi")){
			returnString =  firmwareCodiDao.getDistriButeMcuCodiModelListCnt(param, mcuId); 
		}else if(param.get("equip_kind").equals("Modem")){
			returnString = firmwareModemDao.getDistriButeModemModelListCnt(param, mcuId); 
		}
		return returnString ;
	}
	
	/**
	 * codi, modem만 실행 됨
	 * mcu별 장비 수를 클릭 하면 id가 Tree형태로 다시 표시 됨. 
	 **/
	public List<Object> getDistriButeModemList(Map<String, Object> param, String mcuId){
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());

		List<Object> returnList = null;
		
		if(param.get("equip_kind").equals("Codi")){
			returnList =  firmwareCodiDao.getDistriButeMcuCodiModelList(param, mcuId);
		}else if(param.get("equip_kind").equals("Modem")){
			returnList = firmwareModemDao.getDistriButeModemModelList(param, mcuId);
		}
		
		return returnList;
	}
	
	/**
	 * 배포>배포상태정보(작성자 정보)
	 **/
	public List<Object> distributeWriterStatus(Map<String, Object> param){
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());

		List<Object> returnList = null;
		
		//if(param.get("equip_kind").equals("MCU")){
			returnList =  firmwareMCUDao.distributeWriterStatus(param);
		/*}else if(param.get("equip_kind").equals("Modem")){
			//returnList = firmwareModemDao.distributeStatus(param);
		}else if(param.get("equip_kind").equals("Codi")){
			//returnList = firmwareCodiDao.distributeStatus(param);
		}*/
		
		return returnList;
	}
	
	/**
	 * 배포>배포상태정보(LOCATION/TRIGGERID 정보)
	 **/
	public List<Object> distributeStatus(Map<String, Object> param){
		String gubun = String.valueOf(param.get("gubun"));
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());

		List<Object> returnList = null;
		
		if(gubun.equals("B")){
			if(param.get("equip_kind").equals("MCU")){
				returnList =  firmwareMCUDao.distributeLocationStatus(param);
			}else if(param.get("equip_kind").equals("Modem")){
				returnList = firmwareModemDao.distributeModemLocationStatus(param);
			}else if(param.get("equip_kind").equals("Codi")){
				returnList = firmwareCodiDao.distributeCodiLocationStatus(param);
			}
		}else if(gubun.equals("A")){
			if(param.get("equip_kind").equals("MCU")){
				returnList =  firmwareMCUDao.distributeTriggerIdStatus(param);
			}else if(param.get("equip_kind").equals("Modem")){
				returnList = firmwareModemDao.distributeModemTriggerIdStatus(param);
			}else if(param.get("equip_kind").equals("Codi")){
				returnList = firmwareCodiDao.distributeCodiTriggerIdStatus(param);
			}
		}
		
		return returnList;
	}
	
	
	/**
	 * 배포>배포상태정보>MCUID세부정보(배포상태LOCATION정보에서  리스트 클릭하면 나오는 세부항목)
	 **/
	public List<Object> distributeStatusDetail(Map<String, Object> param){
		String gubun = String.valueOf(param.get("gubun"));
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());

		List<Object> returnList = null;
		
		if(param.get("equip_kind").equals("MCU")){
			returnList =  firmwareMCUDao.distributeMCULocStatusDetail(param);
		}else if(param.get("equip_kind").equals("Modem")){
			returnList = firmwareModemDao.distributeModemLocStatusDetail(param);
		}else if(param.get("equip_kind").equals("Codi")){
			returnList = firmwareCodiDao.distributeCodiLocStatusDetail(param);
		}
		
		return returnList;
	}
	
	/**
	 * 배포>배포상태정보>MCUID세부정보(배포상태LOCATION/TRIGGER정보에서  리스트 클릭하면 나오는 세부항목)
	 **/
	public List<Object> distributeStatusEquipDetail(Map<String, Object> param){
		String gubun = String.valueOf(param.get("gubun"));
		Code code = codeDao.getCodeByName(String.valueOf(param.get("equip_type")));	
		param.put("equip_typeCD", code.getId());

		List<Object> returnList = null;
		
		returnList =  firmwareDao.distributeFmStatusEqDetail(param);
		return returnList;
	}
	
	/**
	 * Firmware추가 시 파일이 존재하는지 여부 체크 
	 * */
	public String checkExistFirmware(String equip_kind,String equip_type,String hwVersion,String fwVersion,String build,String arm,String vendor, String modelId){
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("equip_kind", equip_kind);
        condition.put("equip_type", equip_type);
        condition.put("hw_version", hwVersion);
        condition.put("fw_version", fwVersion);
        condition.put("arm", arm);
        condition.put("vendor", vendor);
        condition.put("devicemodel_id", modelId);
        condition.put("build", build); 
        
		return firmwareDao.checkExistFirmware(condition);
	}
	
	// 펌웨어 파일 추가
    public void addFirmWareFile(Map<String, Object> condition) throws Exception {
		int vendorId = (int) condition.get("vendorId");
		String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
		String parameter = StringUtil.nullToBlank(condition.get("parameter"));
		String deviceType = StringUtil.nullToBlank(condition.get("deviceType"));
		String fileName = StringUtil.nullToBlank(condition.get("fileName"));
		String fileNameWithFwVersion = StringUtil.nullToBlank(condition.get("fileNameWithFwVersion"));
		String creationDate = StringUtil.nullToBlank(condition.get("creationDate"));
		String checkSum = StringUtil.nullToBlank(condition.get("checkSum"));
		Object crc =  condition.get("fw_crc");
		String finalFilePath = StringUtil.nullToBlank(condition.get("finalFilePath"));
		String fwDownURL = StringUtil.nullToBlank(condition.get("fwDownURL"));
		
		int modelId = (int) condition.get("modelId"); 
		int supplierId = (int) condition.get("supplierId");
			
		Supplier supplier = supplierDao.getSupplierById(supplierId);
		
		// 제조사명 정보를 가져온다.
		DeviceVendor deviceVendor = deviceVendorDao.get(vendorId);
		String vendorName = deviceVendor.getName();
		
		// 모델명 정보를 가져온다.
		DeviceModel deviceModel =  deviceModelDao.get(modelId);
		String modelName = deviceModel.getName();
		
		// 사용하지 않는 Firmware Table 컬럼 값 (S)
		String build = " ";
//		String hwVersion = " ";
		boolean arm = false;
		// 사용하지 않는 Firmware Table 컬럼 값 (E)
		
		Firmware entity = new Firmware();
		
		entity.setBuild(build);
		entity.setArm(arm);
		entity.setEquipVendor(vendorName);
		entity.setFwVersion(fwVersion);
		entity.setHwVersion(fileName);
		entity.setImageKey(parameter);
		entity.setEquipKind(deviceType);
		entity.setBinaryFileName(fileName);
		entity.setFileName(fileNameWithFwVersion);
		entity.setReleasedDate(creationDate);
		entity.setCheckSum(checkSum);
		entity.setCrc((String) crc);
		entity.setFilePath(finalFilePath);
		entity.setFileUrlPath(fwDownURL);
		entity.setDevicemodel_id(modelId);
		entity.setEquipModel(modelName); 
		entity.setSupplier(supplier);
		
		firmwareDao.add(entity);
	}
    public Firmware findByFileName(String fileName) {
		Set<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("build", new Object[] { "Deleted" }, null, Restriction.NEQ));
		condition.add(new Condition("fileName", new Object[] { fileName }, null, Restriction.EQ));
		List<Firmware> list = firmwareDao.findByConditions(condition);
    	return (list == null || list.size() == 0) ? null : list.get(0);
    }

    public Firmware findByFileUrlPath(String fileUrlPath) {
		Set<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("build", new Object[] { "Deleted" }, null, Restriction.NEQ));
		condition.add(new Condition("fileUrlPath", new Object[] { fileUrlPath }, null, Restriction.EQ));
		List<Firmware> list = firmwareDao.findByConditions(condition);
    	return (list == null || list.size() == 0) ? null : list.get(0);
    }
 	// 펌웨어 파일 수정
	public void updateFirmWareFile(Map<String, Object> condition) throws Exception {
		// 현재 선택된 Firmware 정보를 받아온다.
		String firmwareId = StringUtil.nullToBlank(condition.get("firmwareId"));
		String command = StringUtil.nullToBlank(condition.get("command"));
		Firmware entity = firmwareDao.getByFirmwareId(firmwareId);

		if (command.equals("updateFileStatus")) {
			entity.setBuild(condition.get("fileStatus") == null ? " " : condition.get("fileStatus").toString());
			entity.setFilePath(condition.get("finalFilePath") == null ? entity.getFilePath() : condition.get("finalFilePath").toString());
		} else {
			int vendorId = (int) condition.get("vendorId");
			int modelId = (int) condition.get("modelId");
			String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));

			// 제조사명 정보를 가져온다.
			DeviceVendor deviceVendor = deviceVendorDao.get(vendorId);
			String vendorName = deviceVendor.getName();

			// 모델명 정보를 가져온다.
			DeviceModel deviceModel = deviceModelDao.get(modelId);
			String modelName = deviceModel.getName();
			
			entity.setFilePath(condition.get("finalFilePath") == null ? entity.getFilePath() : condition.get("finalFilePath").toString());
			entity.setEquipVendor(vendorName); // EQUIP_VENDOR
			entity.setFwVersion(fwVersion); // FW_VERSION
			entity.setDevicemodel_id(modelId); // DEVICEMODEL_ID
			entity.setEquipModel(modelName); // EQUIP_MODEL
		}

		firmwareDao.update(entity);
	}
    
    // 펌웨어 파일 삭제
    public void deleteFirmware(String firmwareId) throws Exception {
		Firmware entity = firmwareDao.getByFirmwareId(firmwareId);
		int firmware_index = entity.getId();
		
		firmwareDao.deleteById(firmware_index);
    }
    
    // 펌웨어 파일 논리적 삭제 SP-967
    public void deleteFirmwareLogical(String firmwareId) throws Exception {
		String sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		Firmware entity = firmwareDao.getByFirmwareId(firmwareId);
		entity.setBuild("Deleted");
		entity.setFirmwareId(entity.getFirmwareId()+"_deleted_"+sdf);
		firmwareDao.update(entity);
    }
	/**
	 * Firmwaremcu추가
	 * */
	public void addFirmWareMCU(FirmwareMCU firmware, FirmwareBoard firmwareBoard) throws Exception {
		firmwareMCUDao.addFirmWareMCU(firmware, firmwareBoard);
	}
	
	/**
	 * Firmwaremcu update
	 * */
	public void updateFirmWareMCU(FirmwareMCU firmware, FirmwareBoard firmwareBoard) throws Exception {
		firmwareMCUDao.updateFirmWareMCU(firmware, firmwareBoard);
	}
	
	/**
	 * Firmwaremodem추가
	 * */	
	public void addFirmWareModem(FirmwareModem firmware, FirmwareBoard firmwareBoard) throws Exception {
		firmwareModemDao.addFirmWareModem(firmware, firmwareBoard);
	}
	
	/**
	 * Firmwaremodem update
	 * */	
	public void updateFirmWareModem(FirmwareModem firmware, FirmwareBoard firmwareBoard) throws Exception {
		firmwareModemDao.updateFirmWareModem(firmware, firmwareBoard);
	}
	
	/**
	 * FirmwareCodi추가
	 * */
	public void addFirmWareCodi(FirmwareCodi firmware, FirmwareBoard firmwareBoard) throws Exception {
		firmwareCodiDao.addFirmWareCodi(firmware, firmwareBoard);
	}

	/**
	 * FirmwareCodi update
	 * */
	public void updateFirmWareCodi(FirmwareCodi firmware, FirmwareBoard firmwareBoard) throws Exception {
		firmwareCodiDao. updateFirmWareCodi(firmware, firmwareBoard);
	}
	
	public Supplier getSupplierId(int frm_supplier) {
		return supplierDao.getSupplierById(frm_supplier);
	}
	
	public List<Object> getStatisticsStr(Map<String, Object> condition){
		return firmwareDao.getStatisticsStr(condition);
	}
	
	public List<Location> getLocationAllList(){
		return locationDao.getParents();
	}	
	
/*	public boolean checkExistFirmware(Map<String, Object> param){
		return firmwareDao.checkExistFirmware(param);
	}
	*/
	/**
	 * trigger 테이블에 인서트(cmd 호출후 작업)  
	 **/
	public void createTrigger(FirmwareTrigger firmwaretrigger)throws Exception{
		firmwareTriggerDao.createTrigger(firmwaretrigger);
	}
	
	int ch =0;
	
	/**
	 * history 테이블에 인서트 (modem cmd 호출 후 작업)
	 **/
	public void insertModemFirmHistory(FirmwareHistory firmwareHistory, Map<String, Object> param, ArrayList  sendArrayEquipList)throws Exception{
		
		for(int i=0 ; i< sendArrayEquipList.size(); i++){
			
	        String format = "yyyyMMddHHmmss";
	        SimpleDateFormat sdf = new SimpleDateFormat(format);
	        Calendar cal = Calendar.getInstance();
	        String dateStr = sdf.format(cal.getTime());
	        
	       
	        
	       /* 
			20110307142210
			201103080948423
*/
        	ch++;
        	//System.out.println("ch======"+ch);
        	String chStr = String.valueOf(ch);
        	//dateStr = dateStr.substring(0,dateStr.length()-chStr.length()) + "" + ch;	

	        String equipId = String.valueOf(sendArrayEquipList.get(i));
	        if(equipId.indexOf("_")>0){
	        	equipId = equipId.substring(0,equipId.indexOf("_"));	
	        }
	        
		    
//		    System.out.println("firmwareHistory.getIssueDate()===sss==================================="+firmwareHistory.getIssueDate());		    
//		    System.out.println(equipId+"dateStr======================================"+dateStr);
	        
			firmwareHistory.setEquipId(equipId);
			firmwareHistory.setIssueDate(dateStr);
			firmwareHistory.setInSeq(String.valueOf(ch));
			param.put("equip_id", equipId);//equip_id를 재 세팅.. device_serial로 ...
	        String triggerHistory = firmwareHistoryDao.getTriggerHistory(param);
	        
		    if(triggerHistory==null || triggerHistory.length()==0){
		        triggerHistory= String.valueOf(firmwareHistory.getTrId());
		    }
		    
		    firmwareHistory.setTriggerHistory(triggerHistory);
		    firmwareHistory.setTriggerCnt(triggerHistory.split(",").length);
		    
		    firmwareHistoryDao.insertFirmHistory(firmwareHistory);
		    
		    //System.out.println("======================Exist======="+Integer.parseInt(firmwareHistoryDao.historyCheckExistEquip(param)));
		    
		    //기존 history가 있으면 나머지도 update?
		    if(Integer.parseInt(firmwareHistoryDao.historyCheckExistEquip(param))>1){
	        	firmwareHistoryDao.updateFirmHistory(firmwareHistory,param);
	        }
		}
	}
	
	public void insertFirmHistory(FirmwareHistory firmwareHistory, Map<String, Object> param)throws Exception{
		 
        String triggerHistory = firmwareHistoryDao.getTriggerHistory(param);
        
	    if(triggerHistory==null || triggerHistory.length()==0){
	        triggerHistory= String.valueOf(firmwareHistory.getTrId());
	    }
	    
        String format = "yyyyMMddHHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        String dateStr = sdf.format(cal.getTime());
		firmwareHistory.setIssueDate(dateStr);
		firmwareHistory.setInSeq("1");
	    firmwareHistory.setTriggerHistory(triggerHistory);
//	    firmwareHistory.setTriggerCnt(triggerHistory.split(",").length-1);
//	    System.out.println("triggerHistory========"+triggerHistory);
//	    System.out.println("triggerHistory.split().length========"+triggerHistory.split(",").length);
	    firmwareHistory.setTriggerCnt(triggerHistory.split(",").length);
	    
	    firmwareHistoryDao.insertFirmHistory(firmwareHistory);
	    
	    if(Integer.parseInt(firmwareHistoryDao.historyCheckExistEquip(param))>1){
        	firmwareHistoryDao.updateFirmHistory(firmwareHistory,param);
        }
	}	

	/**
	 * 배포이력 TriggerList 조회 CNT
	 * */
	public String getTriggerListStep1CNT(Map<String, Object> condition)throws Exception{
		
		int locationId = -1;
		if(String.valueOf(condition.get("locationId")) != null && !"".equals(String.valueOf(condition.get("locationId")))){
			locationId = Integer.parseInt(String.valueOf(condition.get("locationId")));
		}

		String locationStr = "";
		List<Integer> locationIdList = null;
		if(locationId != -1){
			locationIdList = locationDao.getChildLocationId(locationId);
			locationStr = "( ";
			
			if(locationIdList.size() > 0){
				for(int i=0; i<locationIdList.size(); i++){
					if(i==locationIdList.size()-1 && i==0){
						locationStr = "("+locationIdList.get(i)+")";
					}else if(i!=locationIdList.size()-1 && i==0){
						locationStr += ""+locationIdList.get(i);
					}else if(i==locationIdList.size()-1){
						locationStr += ","+locationIdList.get(i) +")";
					}else {
						locationStr += ","+locationIdList.get(i) ;
					}
				}
				
			}else {
				locationStr = "("+locationId+");";
			}
		}
	
		return firmwareDao.getTriggerListStep1CNT(condition,locationStr);
	}
	/**
	 * 배포이력 TriggerList 조회 
	 * */
	public List<Object> getTriggerListStep1(Map<String, Object> condition)throws Exception{

		int locationId = -1;
		if(String.valueOf(condition.get("locationId")) != null && !"".equals(String.valueOf(condition.get("locationId")))){
			locationId = Integer.parseInt(String.valueOf(condition.get("locationId")));
		}
		
		String locationStr = "";
		List<Integer> locationIdList = null;
		if(locationId != -1){
			locationIdList = locationDao.getChildLocationId(locationId);
			locationStr = "( ";
			
			if(locationIdList.size() > 0){
				for(int i=0; i<locationIdList.size(); i++){
					if(i==locationIdList.size()-1 && i==0){
						locationStr = "("+locationIdList.get(i)+")";
					}else if(i!=locationIdList.size()-1 && i==0){
						locationStr += ""+locationIdList.get(i);
					}else if(i==locationIdList.size()-1){
						locationStr += ","+locationIdList.get(i) +")";
					}else {
						locationStr += ","+locationIdList.get(i) ;
					}
				}
				
			}else {
				locationStr = "("+locationId+");";
			}
		}
		
		return firmwareDao.getTriggerListStep1(condition,locationStr);
	}
	/**
	 * 배포이력 TriggerList>TriggerInfo 조회 
	 * */
	public List<Object> getTriggerListStep2(Map<String, Object> condition)throws Exception {
		String equip_type =firmwareHistoryDao.equipTypeBytrID(Integer.parseInt(String.valueOf(condition.get("tr_Id"))));
		condition.put("equip_type", equip_type);
		return firmwareDao.getTriggerListStep2(condition);
	}
	/**
	 * 배포이력 TriggerList>TriggerInfo step2,3 상세정보에서 재배포를 위한 리스트 조회. 
	 * */
	public List<Object> getReDistList(HashMap<String, Object> condition){
		List<Object> returnList = null;
		String equip_kind = String.valueOf(condition.get("equip_kind"));
		if(equip_kind.equals("MCU")){
			returnList = firmwareMCUDao.getReDistMcuList(condition);			
		}else if(equip_kind.equals("Modem")){
			returnList = firmwareModemDao.getReDistModemList(condition);
		}else if(equip_kind.equals("Codi")){
			returnList = firmwareCodiDao.getReDistCodiList(condition);
		}
		return returnList ;
	}
	
	public String getMcuBuildByFirmware(Map<String, Object> param){
		String equip_kind = String.valueOf(param.get("equip_kind"));
		String returnStr = "";
		
		if(equip_kind.equals("Modem")){
			returnStr = firmwareModemDao.getMcuBuildByModemFirmware(param);
		}else if(equip_kind.equals("Codi")){
			returnStr = firmwareCodiDao.getMcuBuildByCodiFirmware(param);
		}
		return returnStr;
	}
	
	public FirmwareTrigger getFirmwareTrigger(String tr_id)throws Exception {
		return firmwareTriggerDao.getFirmwareTrigger(tr_id);
	}
	
	public void updateFirmwareHistory(FirmwareHistory firmwareHistory, ArrayList updateFirmwareHistory)throws Exception{
		firmwareHistoryDao.updateFirmwareHistory(firmwareHistory, updateFirmwareHistory);
	}
	
	
	public List<Integer> getChildren(int locationid){
		return locationDao.getChildLocationId(locationid);
	}

	public String getIDbyMcuSysId(String sys_id){
		return firmwareMCUDao.getIDbyMcuSysId(sys_id);
	}

	@Override
	public void addFirmWare(Firmware firmware) {
		firmwareDao.saveOrUpdate(firmware);
	}

	@Override
	public void addFirmWareTrigger(FirmwareTrigger trigger) {
		firmwareTriggerDao.saveOrUpdate(trigger);
	}
	
/*	public String getMcuIdbyModemModelID(String devicemodel_id,String supplierId,String equip_type){
		return firmwareMCUDao.getMcuIdbyModemModelID(devicemodel_id, supplierId, equip_type);
	}*/
	
	public void addFirmWareModem(FirmwareModem firmware) {
		firmwareModemDao.saveOrUpdate(firmware);
		firmwareModemDao.flushAndClear();
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public void addFirmwareIssue(FirmwareIssue firmwareIssue) {
		firmwareIssueDao.saveOrUpdate(firmwareIssue);
		firmwareIssueDao.flushAndClear();
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public void addFirmwareIssueHistory(FirmwareIssueHistory firmwareIssueHistory) {
		firmwareIssueHistoryDao.saveOrUpdate(firmwareIssueHistory);
		firmwareIssueHistoryDao.flushAndClear();
	}

	@Override
	public Firmware getById(int firmwareId) {
		return firmwareDao.get(firmwareId);
	}
	
}
