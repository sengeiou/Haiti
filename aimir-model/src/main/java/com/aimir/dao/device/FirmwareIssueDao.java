package com.aimir.dao.device;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssuePk;
import com.aimir.util.Condition;

public interface FirmwareIssueDao extends GenericDao<FirmwareIssue, FirmwareIssuePk> {
	public List<Object> getFirmwareIssueList(Map<String, Object> condition) throws Exception;

	public List<FirmwareIssue> getFirmwareIssue(Set<Condition> condition2);
}
