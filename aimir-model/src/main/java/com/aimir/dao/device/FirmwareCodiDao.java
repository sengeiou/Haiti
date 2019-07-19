package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareCodi;

public interface FirmwareCodiDao extends GenericDao<FirmwareCodi, Integer> {

	public FirmwareCodi get(String firmwareId);
	public String getMcuCodiEquipCnt(Map<String, Object> param);
	public String getDistriButeCodiIdCnt(Map<String, Object> param, String location_id,String location_name) ;
	public List<Object> getdistributeCodiIdDivList(Map<String, Object> param, String location_id,String location_name);
	public List<Object> getMcuCodiFirmwareList(Map<String, Object> condition);
	public String getFirmwareMcuCodiListCNT(Map<String, Object> condition);
	public String getDistriButeMcuCodiModelListCnt(Map<String, Object> param, String mcuId);
	public List<Object>  getDistriButeMcuCodiModelList(Map<String, Object> param, String mcuId);
	public List<Object> distributeCodiLocationStatus(Map<String, Object> param);
	public List<Object> distributeCodiTriggerIdStatus(Map<String, Object> param);
	public List<Object> distributeCodiLocStatusDetail(Map<String, Object> param);
	public void addFirmWareCodi(FirmwareCodi firmware,FirmwareBoard firmwareBoard)throws Exception ;
	public void updateFirmWareCodi(FirmwareCodi firmware,FirmwareBoard firmwareBoard)throws Exception ;
	public List<Object> getReDistCodiList(Map<String, Object> param);
	public String getMcuBuildByCodiFirmware(Map<String, Object> param);

}
