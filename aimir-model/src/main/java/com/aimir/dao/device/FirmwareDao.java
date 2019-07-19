package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareCodi;
import com.aimir.model.device.FirmwareMCU;
import com.aimir.model.device.FirmwareModem;

public interface FirmwareDao extends GenericDao<Firmware, Integer> {
	public Firmware get(int id);
	public Firmware getByFirmwareId(String firmwareId);

	// public void addFirmWare(FirmwareMCU firmware,FirmwareBoard firmwareBoard) throws Exception;
	public List<FirmwareMCU> getMCUFirmwareList(Map<String, Object> condition);

	public List<FirmwareCodi> getCodiFirmwareList(Map<String, Object> condition);

	public List<FirmwareModem> getModemFirmwareList(Map<String, Object> condition);

	public List<Object> getStatisticsStr(Map<String, Object> condition);

	public List<Object> distributeFmStatusEqDetail(Map<String, Object> param);

	public String getFirmwareFileMgmListCNT(Map<String, Object> condition);

	public List<Object> getFirmwareFileMgmList(Map<String, Object> condition);

	public String checkExistFirmware(Map<String, Object> condition);

	public String getTriggerListStep1CNT(Map<String, Object> condition, String locationStr) throws Exception;

	public List<Object> getTriggerListStep1(Map<String, Object> condition, String locationStr) throws Exception;

	public List<Object> getTriggerListStep2(Map<String, Object> condition) throws Exception;
	
	public List<Object> getFirmwareFileList(Map<String, Object> condition) throws Exception;
	
}
