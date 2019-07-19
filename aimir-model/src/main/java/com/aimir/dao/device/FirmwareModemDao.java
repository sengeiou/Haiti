package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareModem;

public interface FirmwareModemDao extends GenericDao<FirmwareModem, Integer> {

	public FirmwareModem get(String firmwareId);
	public String getModemEquipCnt(Map<String, Object> param);
	public String getDistriButeModemIdCnt(Map<String, Object> param, String location_id,String location_name);
	public List<Object> getdistributeModemIdDivList(Map<String, Object> param, String location_id,String location_name) ;
	public List<Object> getModemFirmwareList(Map<String, Object> condition) ;
	public String getFirmwareModemListCNT(Map<String, Object> condition) ;
	public String getDistriButeModemModelListCnt(Map<String, Object> param, String mcuId);
	public List<Object>  getDistriButeModemModelList(Map<String, Object> param, String mcuId);
	public List<Object> distributeModemLocationStatus(Map<String, Object> param);
	public List<Object> distributeModemTriggerIdStatus(Map<String, Object> param);
	public List<Object> distributeModemLocStatusDetail(Map<String, Object> param);
	public void addFirmWareModem(FirmwareModem firmware,FirmwareBoard firmwareBoard)throws Exception;
	public void updateFirmWareModem(FirmwareModem firmware,FirmwareBoard firmwareBoard)throws Exception;
	public List<Object> getReDistModemList(Map<String, Object> param);
	public String getMcuBuildByModemFirmware(Map<String, Object> param);
}
