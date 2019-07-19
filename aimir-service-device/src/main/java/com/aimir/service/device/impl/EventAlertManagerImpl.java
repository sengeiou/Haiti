package com.aimir.service.device.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ThresholdName;	// INSERT SP-193
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.ThresholdDao;		// INSERT SP-193
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.Threshold;		// INSERT SP-193
import com.aimir.service.device.EventAlertManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@Service(value="eventAlertManager")
public class EventAlertManagerImpl implements EventAlertManager {

	@Autowired
	EventAlertDao dao;

	@Autowired
	ThresholdDao thresholdDao;			// INSERT SP-193
	
	/**
     * @desc Paging 처리를 위한 extjs 인덱스 조정 
     * @param conditionMap
     * @return conditionMap
     */
    public static Map<String, String> getFirstPageForExtjsGrid(Map<String, String> conditionMap)
    {
        // 페이징 처리를 위한 부분 추가.extjs 는 0부터 시작
        int temppage = Integer.parseInt(conditionMap.get("page"));

        temppage = temppage - 1;

        conditionMap.put("page", Integer.toString(temppage));
        
        return conditionMap;
    }
    
	public List<EventAlert> getEventAlerts() {
		Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("name",null,null,Restriction.ORDERBY));
    	return dao.findByConditions(set);
	}

	public EventAlert getEventAlert(Integer eventAlertId) {
		return dao.get(eventAlertId);
	}

	public List<EventAlert> getEventAlertsByType(String eventAlertType) {
		return dao.getEventAlertsByType(eventAlertType);
	}

	// EventAlert 테이블 리스트 조회
    public List<EventAlert> getEventAlertListWithPaging(Map<String, String> conditionMap) {
        Map<String, String> conditionMap2 = this.getFirstPageForExtjsGrid(conditionMap);
        List<EventAlert> evList = dao.getEventAlertList(conditionMap2);
                
        return evList;
    }
    
    // EventAlert 테이블 리스트 개수 조회
    public Integer getEventAlertListCount(Map<String, String> conditionMap) {       
        Integer count = dao.getRowCountByQuery();
        return count;
    }
    
    // 입력된 매개변수로 table update
    @Transactional(readOnly=false)
    public Map<String,String> updateEventAlertConfig(Map<String,String> conditionMap){
        Map<String,String> result = new HashMap<String,String>();
        Integer eId = Integer.parseInt(conditionMap.get("eId").trim());
        EventAlert oriEvent = dao.get(eId);
        if(oriEvent == null){
            result.put("result", "fail");
            result.put("info", "Can't find valid event object at database. ");
            return result;
        }else{
            //항목 수정
            oriEvent.setDescr(conditionMap.get("eDescription").trim());
            oriEvent.setSeverity(conditionMap.get("eSeverity").trim());
            oriEvent.setMonitor(conditionMap.get("eMonitorType").trim());
            oriEvent.setTroubleAdvice(conditionMap.get("eAdvice").trim());
            oriEvent.setEventAlertType(conditionMap.get("eCategory").trim());
            
            try{
                //상태업데이트
                dao.update(oriEvent);
            }catch(Exception e){
                result.put("result", "fail");
                result.put("info", "Error at database. ");
                return result;
            }
            
        }
        
        result.put("result", "success");
        result.put("info", "Event Configuration is updated. ");
        return result;
    }
    // INSERT START SP-193
    @Transactional(readOnly=false)
    public Map<String,String> updateAllThreshold(List<Map<String,String>> conditionMapList){
        Map<String,String> result = new HashMap<String,String>();
        for(Map<String,String> conditionMap : conditionMapList) {
	        Threshold threshold = thresholdDao.getThresholdByname(conditionMap.get("name").trim());
	        if(threshold == null){
	        	Threshold insthreshold = new Threshold();
	        	insthreshold.setThresholdName(conditionMap.get("name"));
	        	insthreshold.setLimit(Integer.parseInt(conditionMap.get("threshold").trim()));
	        	insthreshold.setDuration(conditionMap.get("schedule"));
	            try{
	            	thresholdDao.saveOrUpdate(insthreshold);
	            }catch(Exception e){
	                result.put("result", "fail");
	                result.put("info", "Error at database. ");
	                return result;
	            }	        	
	        }else{
	        	threshold.setLimit(Integer.parseInt(conditionMap.get("threshold").trim()));
	        	threshold.setDuration(conditionMap.get("schedule"));
            
	            try{
	            	thresholdDao.update(threshold);
	            }catch(Exception e){
	                result.put("result", "fail");
	                result.put("info", "Error at database. ");
	                return result;
	            }
	            
	        }
        }
        
        result.put("result", "success");
        result.put("info", "Threshold is updated. ");
        return result;
    }
    
	public List<Threshold> getAllThreshold() {
		
		List<Threshold> res = new ArrayList<Threshold>();
		ThresholdName[] names = ThresholdName.values();
		List<Threshold> dbList = thresholdDao.getAll();
		
		if (dbList.size() == 0) {
			for (int i = 0; i < names.length; i++) {
				Threshold threshold = new Threshold();
				threshold.setThresholdName(names[i].name());
				threshold.setThresholdNameValue(names[i].getThresholdNameValue());
				threshold.setDuration("");
				threshold.setLimit(0);
				threshold.setMore(0);
				res.add(threshold);
			}
		}
		else {
			for (int i = 0; i < names.length; i++) {
				int exist = 0;
				Threshold threshold = new Threshold();
				for(Threshold data : dbList) {
					if (data.getThresholdName() == names[i]) {
						threshold.setThresholdName(data.getThresholdName().name());
						threshold.setThresholdNameValue(data.getThresholdName().getThresholdNameValue());
						threshold.setDuration(data.getDuration());
						threshold.setLimit(data.getLimit());
						threshold.setMore(data.getMore());
						exist=1;
					}					
				}
				if (exist != 1) {
					threshold.setThresholdName(names[i].name());
					threshold.setDuration("");
					threshold.setLimit(0);
					threshold.setMore(0);
				}
				exist = 0;
				res.add(threshold);
			}
		}
		
		return res;

	}
		
    // INSERT END SP-193    
}
