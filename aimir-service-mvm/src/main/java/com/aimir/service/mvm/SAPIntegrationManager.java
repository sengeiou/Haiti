package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;

public interface SAPIntegrationManager {

	/**
	 * OutBound File에 대한 값을 결과값을 검색 
	 * 
	 * @param condition
	 * @return
	 */
	@WebMethod(operationName="getOutBoundGridData")
	public List<Object> getOutBoundGridData(Map<String, Object> condition);
	
	/**
	 *  InBound File에 대한 결과 값과 그 결과 값의 Total Count, Meter Count의 Total 값을 return
	 *  
	 * @param condition
	 * @return
	 */
	@WebMethod(operationName="getInBoundGridData")
	public List<Object> getInBoundGridData(Map<String, Object> condition);
	
	/**
	 * error가 발생한 미터에 대한 로그와 검색해온 결과 값을 개수를 return
	 * 
	 * @param condition
	 * @return
	 */
	@WebMethod(operationName="getErrorLogGridData")
	public List<Object> getErrorLogGridData(Map<String, Object> condition);
}
