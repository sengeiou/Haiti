package com.aimir.dao.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.model.device.FirmwareHistoryPk;
import com.aimir.model.device.FirmwareIssueHistory;

public interface FirmwareHistoryDao extends GenericDao<FirmwareHistory, FirmwareHistoryPk> {
	public String historyCheckExistEquip(Map<String, Object> param)throws Exception ;
	public String getTriggerHistory(Map<String, Object> param)throws Exception;
	public void insertFirmHistory(FirmwareHistory firmwareHistory)throws Exception;
	public void updateFirmHistory(FirmwareHistory firmwarehistory,Map<String, Object> param)throws Exception;
	public void updateFirmwareHistory(FirmwareHistory firmwareHistory,ArrayList<String> updateFirmwareHistory)throws Exception;
	public  List<Object> getScheduleCheckOTAState(String equip_kind)throws Exception;
	public void updateFirmHistoryBySchedule(String sql)throws Exception;
	public String equipTypeBytrID(Integer tr_id)throws Exception;
}
