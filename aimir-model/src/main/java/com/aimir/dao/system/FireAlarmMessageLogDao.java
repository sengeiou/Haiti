package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.FireAlarmMessageLog;

public interface FireAlarmMessageLogDao extends GenericDao<FireAlarmMessageLog, Number> {
	
	/**
	 * 
     * method name : listNotSended
     * method Desc : 타 시스템에 전송되지 않은 FireAlarmMessageLog 정보 를 리턴
     * 
	 * @return Array Of FireAlarmMessageLog @see com.aimir.model.system.FireAlarmMessageLog
	 */
	public FireAlarmMessageLog[] listNotSended();
}
