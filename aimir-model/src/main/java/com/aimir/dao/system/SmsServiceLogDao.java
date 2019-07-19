package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.SmsServiceLog;

public interface SmsServiceLogDao extends GenericDao<SmsServiceLog, Integer> {
	
	/**
	 * method name : count
	 * method Desc : 전체 데이터 카운트를 리턴
	 * 
	 * @return
	 */
	public Integer count();
}
