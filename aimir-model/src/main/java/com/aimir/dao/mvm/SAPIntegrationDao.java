package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.SAPIntegrationLog;

public interface SAPIntegrationDao extends GenericDao<SAPIntegrationLog, Integer>{

	/**
	 * 수행할 명령 목록을 리턴
	 * @return List<SAPIntegrationLog>
	 */
	List<SAPIntegrationLog> getOrgerList(Map<String, Object> condition);
	
	/**
	 * OutBound File에 대한 값을 결과값을 검색 
	 * 
	 * @param condition
	 * @return
	 */
	public List<Object> getOutBoundGridData(Map<String, Object> condition);
	
	/**
	 * InBound File에 대한 결과 값을 겸색 (InBound FileName is not null)
	 * 
	 * @param condition
	 * @return
	 */
	public List<Object> getInBoundGridData(Map<String, Object> condition);
	
	/**
	 * error가 발생한 미터와 그 로그를 검색 (InBound FileName is null)
	 * 
	 * @param condition
	 * @return
	 */
	public List<Object> getErrorLogGridData(Map<String, Object> condition);

}
