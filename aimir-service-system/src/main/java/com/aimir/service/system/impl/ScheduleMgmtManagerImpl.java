package com.aimir.service.system.impl;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.service.system.ScheduleMgmtManager;

@WebService(endpointInterface = "com.aimir.service.system.ScheduleMgmtManager")
@Service(value="scheduleMgmtManager")
@Transactional
public class ScheduleMgmtManagerImpl implements ScheduleMgmtManager {

    @Autowired
    GroupDao groupDao;

    @Autowired
    SupplierDao supplierDao;

	/**
	 * method name : getGroupComboDataByType<b/>
	 * method Desc : Task Management 맥스가젯에서 선택한 GroupType 의 Group Combo Data 를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> getGroupComboDataByType(Map<String, Object> conditionMap) {
		return groupDao.getGroupComboDataByType(conditionMap);
	}

    /**
     * method name : getGroupTypeByGroup<b/>
     * method Desc : Task Management 맥스가젯에서 선택한 Job 의 Group Type 을 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    public String getGroupTypeByGroup(Map<String, Object> conditionMap) {
        return groupDao.getGroupTypeByGroup(conditionMap);
    }
}