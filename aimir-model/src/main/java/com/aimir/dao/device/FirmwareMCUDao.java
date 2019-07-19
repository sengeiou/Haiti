package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareMCU;

public interface FirmwareMCUDao extends GenericDao<FirmwareMCU, Integer> {

	public FirmwareMCU get(String firmwareId);
	
	public String getDistriButeMcuIdCnt(Map<String, Object> param, String location_id,String location_name) ;
	public String getMcuEquipCnt(Map<String, Object> param) ;
	public List<Object> getdistributeMcuIdDivList(Map<String, Object> param, String location_id,String location_name);
	public List<Object> getMcuFirmwareList(Map<String, Object> condition);
	public List<Object> distributeWriterStatus(Map<String, Object> param);
	public List<Object> distributeLocationStatus(Map<String, Object> param);
	public List<Object> distributeTriggerIdStatus(Map<String, Object> param);
	public String getFirmwareMcuListCNT(Map<String, Object> condition);
	public List<Object> distributeMCULocStatusDetail(Map<String, Object> param);
	public void addFirmWareMCU(FirmwareMCU firmware,FirmwareBoard firmwareBoard) throws Exception ;
	public void updateFirmWareMCU(FirmwareMCU firmware,FirmwareBoard firmwareBoard) throws Exception ;
	public List<Object> getReDistMcuList(Map<String, Object> param);
	public String getIDbyMcuSysId(String sys_id);
	/*public String getMcuIdbyModemModelID(String devicemodel_id,String supplierId,String equip_type);*/
}
