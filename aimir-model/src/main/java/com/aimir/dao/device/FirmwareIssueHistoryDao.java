package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.device.FirmwareIssueHistoryPk;

public interface FirmwareIssueHistoryDao extends GenericDao<FirmwareIssueHistory, FirmwareIssueHistoryPk> {
	public List<Object> getFirmwareIssueHistoryList(Map<String, Object> condition) throws Exception;

	public Map<String, Integer> getHistoryStepCount(FirmwareIssueHistory firmwareIssueHistory);

	public void updateOTAHistory(String eventMessage, String deviceId, DeviceType deviceType, String openTime, String resultStatus, String requestId);

	public void updateOTAHistory(String eventMessage, String deviceId, DeviceType deviceType, String openTime, String resultStatus);

	public void updateOTAHistoryFor63_59_31(String eVENT_MESSAGE, String openTime, String string, String requestId);

	
	public void updateOTAHistoryIssue(String eVENT_MESSAGE, String deviceId, DeviceType deviceType, String requestId);

	public void updateOTAHistoryIssue(String eVENT_MESSAGE, String deviceId, DeviceType deviceType);

	public void updateOTAHistoryIssueFor63_59_31(String eVENT_MESSAGE, String requestId);

	
	public List<FirmwareIssueHistory> getRetryTargetList(String issueDate);

	public List<FirmwareIssueHistory> getTargetList(Map<String, String> targetParams);

	

}
