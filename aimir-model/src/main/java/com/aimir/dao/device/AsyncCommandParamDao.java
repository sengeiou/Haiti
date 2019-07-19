package com.aimir.dao.device;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandParamPk;

public interface AsyncCommandParamDao extends GenericDao<AsyncCommandParam, AsyncCommandParamPk> {
	
	public Integer getMaxNum(String mcuId, Long trId);

	public List<AsyncCommandParam> getCmdParams(String deviceSerial, long trId, String paramName);
	   
    public List<AsyncCommandParam> getCmdParamsByTrnxId(String deviceSerial, String paramName);
}
