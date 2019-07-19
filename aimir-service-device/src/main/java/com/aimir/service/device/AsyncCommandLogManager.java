package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandResult;

@WebService(name="AsyncCommandLogService", targetNamespace="http://aimir.com/services")
public interface AsyncCommandLogManager {
	
    @WebMethod
    public void add(@WebParam(name="asyncCommandLog") AsyncCommandLog asyncCommandLog);
    
	public Integer getCmdStatus(String deviceId, String cmd);
	public Integer getCmdStatusByTrId(String deviceId, long trId) throws Exception;
	
    @WebMethod
	public Long getMaxTrId(@WebParam(name="deviceId") String deviceId);
    
    @WebMethod
    public Long getMaxTrId(@WebParam(name="deviceId") String deviceId, @WebParam(name="command") String cmd);
    
    @WebMethod
	public Integer getParamMaxNum(@WebParam(name="deviceId") String deviceId,
			@WebParam(name="trId") long trId);
    
    @WebMethod
	public void addParam(@WebParam(name="asyncCommandParam") AsyncCommandParam asyncCommandParam);

    @WebMethod
	public List<AsyncCommandParam> getCmdParams(String deviceSerial, long trId, String paramName);
    
    @WebMethod
    public List<AsyncCommandParam> getCmdParamsByTrnxId(String deviceSerial, String paramName);

    public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String paramName);
    public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String tr_type, String paramName);
    public String getCmdResults(String deviceSerial, long trId);
    public List<Object> getCommandLogList(Map<String, Object> condition) throws Exception;
    public Integer getCommandLogListTotalCount(Map<String, Object> condition) throws Exception;
}
