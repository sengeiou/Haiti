package com.aimir.dao.device;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.AsyncCommandResultPk;

public interface AsyncCommandResultDao extends GenericDao<AsyncCommandResult, AsyncCommandResultPk> {
    public Integer getMaxNum(String mcuId, Long trId);

    public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String paramName);
    public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String tr_type, String paramName);
	public String getCmdResults(String deviceSerial, long trId);
}

