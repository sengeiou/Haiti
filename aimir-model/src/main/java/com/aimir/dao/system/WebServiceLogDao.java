package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.WebServiceLog;

public interface WebServiceLogDao extends GenericDao<WebServiceLog, Integer> {
	
	/**
	 * method name : count
	 * method Desc : 전체 데이터 카운트를 리턴
	 * 
	 * @return
	 */
	public Integer count();
}
