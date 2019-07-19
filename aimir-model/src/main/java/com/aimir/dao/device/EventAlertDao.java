package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.EventAlert;

public interface EventAlertDao extends GenericDao<EventAlert, Integer> {
	public List<EventAlert> getEventAlertsByType(String eventAlertType);
	
	/**
     * 이벤트 리스트를 조회
     * @param conditionMap
     * @return List<EventAlert>
     */
    public List<EventAlert> getEventAlertList(Map<String, String> conditionMap);

    /**
     * 이벤트 리스트의 열 개수 조회
     * @return
     */
	public Integer getRowCountByQuery();
}
