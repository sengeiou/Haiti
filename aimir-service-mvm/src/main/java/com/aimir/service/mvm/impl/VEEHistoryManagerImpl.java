package com.aimir.service.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.mvm.VEELogDao;
import com.aimir.model.mvm.VEELog;
import com.aimir.util.Condition;

@Service(value = "VEEHistoryManagerImpl")
public class VEEHistoryManagerImpl {
	
	
	@Autowired
	VEELogDao veeLogDao;
	
	//일별
	public List<Object> getDayVEEHistoryList(HashMap<String, Object> hm) {
		List<Object> result = new ArrayList<Object>();
		
		Set<Condition> set = (Set<Condition>)hm.get("condition");
		
		List<VEELog> dataList = veeLogDao.getVEELogByListCondition(set);

		return result;
	}
	//요일별
	public List<Object> getDayWeekVEEHistoryList(HashMap<String, Object> hm) {
		List<Object> result = new ArrayList<Object>();
		
		return result;
	}
	
	//주별
	public List<Object> getWeekVEEHistoryList(HashMap<String, Object> hm) {
		List<Object> result = new ArrayList<Object>();
		
		return result;
	}
	
	//월별
	public List<Object> getMonthVEEHistoryList(HashMap<String, Object> hm) {
		List<Object> result = new ArrayList<Object>();
		
		return result;
	}
}
