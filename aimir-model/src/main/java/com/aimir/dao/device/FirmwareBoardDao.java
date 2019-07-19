package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.FirmwareBoard;

public interface FirmwareBoardDao  extends GenericDao<FirmwareBoard, Integer>{
	public List<FirmwareBoard> getFirmwareBoardList(Map<String, Object> condition);
}
