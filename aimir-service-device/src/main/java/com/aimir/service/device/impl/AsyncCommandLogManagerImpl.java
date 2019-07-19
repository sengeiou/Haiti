package com.aimir.service.device.impl;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.service.device.AsyncCommandLogManager;

@WebService(endpointInterface = "com.aimir.service.device.AsyncCommandLogManager")
@Service(value = "AsyncCommandLogService")
@Transactional(readOnly=false)
public class AsyncCommandLogManagerImpl implements AsyncCommandLogManager {
	
    @Autowired
    AsyncCommandLogDao dao;
	
    @Autowired
    AsyncCommandParamDao paramDao;
    
    @Autowired
    AsyncCommandResultDao resultDao;
    
    public void add(AsyncCommandLog asyncCommandLog) {
    	dao.add(asyncCommandLog);
    }
    
    public void addParam(AsyncCommandParam AsyncCommandParam) {
    	paramDao.add(AsyncCommandParam);
    }
    
	public Integer getCmdStatus(String deviceId, String cmd) {
		return dao.getCmdStatus(deviceId, cmd);
	}
	
	public Integer getCmdStatusByTrId(String deviceId, long trId) throws Exception{
		return dao.getCmdStatusByTrId(deviceId, trId);
	}
    
	public Long getMaxTrId(String deviceId) {
		return dao.getMaxTrId(deviceId);
	}

	public Long getMaxTrId(String deviceId, String cmd) {
		return dao.getMaxTrId(deviceId, cmd);
	}
	
	public Integer getParamMaxNum(String deviceId, long trId) {
		return paramDao.getMaxNum(deviceId, trId);
	}
	
	public List<AsyncCommandParam> getCmdParams(String deviceSerial, long trId, String paramName) {
		return paramDao.getCmdParams(deviceSerial, trId, paramName);
	}
	
	@Override
	public List<AsyncCommandParam> getCmdParamsByTrnxId(String deviceSerial, String paramName) {
		return paramDao.getCmdParamsByTrnxId(deviceSerial, paramName);
	}

	@Override
	public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String paramName) {
		return resultDao.getCmdResults(deviceSerial, trId, paramName);
	}
	
	@Override
	public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String tr_type, String paramName) {
		return resultDao.getCmdResults(deviceSerial, trId, tr_type, paramName);
	}
	@Override
	public String getCmdResults(String deviceSerial, long trId) {
		return resultDao.getCmdResults(deviceSerial, trId);
	}
	
	@Override
	public List<Object> getCommandLogList(Map<String, Object> condition) throws Exception {
		return dao.getCommandLogList(condition);
	}

	@Override
	public Integer getCommandLogListTotalCount(Map<String, Object> condition) throws Exception {
		List<Map<String, Object>> result = dao.getCommandLogListTotalCount(condition);
        return (Integer)(result.get(0).get("total"));
	}
	
}