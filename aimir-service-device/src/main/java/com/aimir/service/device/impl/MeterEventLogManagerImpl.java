package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterEventDao;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.ObisCodeDao;
import com.aimir.dao.system.ProfileDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.MeterEvent;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Profile;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MeterEventLogManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value = "MeterEventLogManager")
public class MeterEventLogManagerImpl implements MeterEventLogManager {

	@Autowired LocationDao locationDao;	
	
	@Autowired SupplierDao supplierDao;
	
	@Autowired MeterEventLogDao meterEventLogDao;
	@Autowired MeterEventDao meterEventDao;
	@Autowired ProfileDao profileDao;
	
	@Autowired ObisCodeDao obisCodeDao;
	
    // 해당 지역의 가장 하위 지역들을 구한다.
    private String getLeafLocationId(String locationId, String supplierId) {
        
        if("".equals(locationId)) return "";
        
        StringBuilder rtnVal = new StringBuilder();
        Set<Integer> locations = new HashSet<Integer>();
        List<Integer> tempList = null;
        Iterator<Integer> it = null;
        int cnt = 0 ;
        int tot = 0;
        
        tempList = locationDao.getLeafLocationId(Integer.parseInt(locationId), Integer.parseInt(supplierId));
        
        for(Integer integer : tempList) {
            locations.add(integer);
        }
        
        tot = locations.size();
        it = locations.iterator();
        
        while(it.hasNext()) {
            rtnVal.append(it.next());
            //if(it.hasNext())
            if(cnt != (tot - 1)) {
                rtnVal.append(',');
            }
            cnt++;
        }

        return rtnVal.toString();
    }

    private String getMeterEventByUser(Long userId) {
        
        if(userId == null) return null;
        
        List<String> profList = null;
        StringBuilder rtnVal = new StringBuilder();
        int cnt = 0 ;
        
        profList = profileDao.getMeterEventProfileByUser(userId.intValue());
        if (profList.size() == 0) {
            return rtnVal.toString();
        }
        for(String profile : profList) {
            if(cnt != 0) {
                rtnVal.append(',');
            }
            rtnVal.append("'"+profile+"'");
            cnt++;
        }
        
        return rtnVal.toString();
    }
    
    public List<Map<String, Object>> getMeterEventLogMiniChartData(Map<String, Object> conditionMap) {

        List<Map<String, Object>> result = null;
        
        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));
        conditionMap.put("userEvents", getMeterEventByUser((Long)conditionMap.get("userId")));
        result = meterEventLogDao.getMeterEventLogMiniChartData(conditionMap);

        return result;
    }

    public List<Map<String, Object>> getMeterEventLogProfileData(Map<String, Object> conditionMap) {

        List<Map<String, Object>> result = null;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;

        result = meterEventLogDao.getMeterEventLogProfileData(conditionMap);

        for(Map<String, Object> data: result) {
            map = new HashMap<String, Object>();
            map.put("meterEventName", data.get("METEREVENTNAME"));
            map.put("hasProfile",     data.get("HASPROFILE"));

//            map.put("meterEventName", data.get(0));
//            map.put("hasProfile",     data.get(1));

            list.add(map);
        }
        return list;
    }

    @Transactional
    public void updateMeterEventLogProfileData(Map<String, Object> conditionMap) {

        int userId = 0;
        String[] meterEventNames = null;
        String allRemove = null;
        Profile profile = null;
        Operator operator = null;
        MeterEvent meterEvent = null;
        List<Map<String, Object>> list = null;

        allRemove = (String)conditionMap.get("allRemove");
        userId = ((Long)conditionMap.get("userId")).intValue();
        // 사용자ID에 해당하는 데이터 삭제
        profileDao.deleteMeterEventProfileByUser(userId);
        
        if (allRemove.equals("yes")) {
            return;
        }

        meterEventNames = (String[])conditionMap.get("meterEventNames");
        
        list = meterEventDao.getEventIdsByNames(meterEventNames);
        
        for (Map<String, Object> data : list) {
            profile = new Profile();
            operator = new Operator();
            meterEvent = new MeterEvent();

            operator.setId(userId);
            profile.setOperator(operator);
            meterEvent.setId((String)data.get("meterEventId"));
            profile.setMeterEvent(meterEvent);
            // 선택한 데이터 저장
            profileDao.add(profile);
//            meterEventIds.add((String)data.get("meterEventId"));
        }
    }

    // Event Name 리스트를 조회한다.
    public List<Map<String, Object>> getEventNames() {
        
        List<Map<String, Object>> list = null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> cbmap = new HashMap<String, Object>();
        
        list = meterEventDao.getEventNames();

        for (Map<String, Object> map : list) {
            cbmap = new HashMap<String, Object>();
            if (StringUtil.nullToBlank(map.get("eventName")).length() == 0) {
                continue;
            }
            cbmap.put("id", map.get("eventName"));
            cbmap.put("name", map.get("eventName"));
            result.add(cbmap);
        }

        return result;
    }
    
    @Transactional
    public void add(MeterEvent meterEvent) throws Exception {
    	meterEventDao.add(meterEvent );
    }

    @Transactional(readOnly=false)
    public void update(MeterEvent meterEvent) throws Exception {
    	meterEventDao.update(meterEvent);
    }
    
    @Transactional
    public void delete(MeterEvent meterEvent) throws Exception {
    	meterEventDao.delete(meterEvent);
    }

    public List<Map<String, Object>> getMeterEventLogMaxChartData(Map<String, Object> conditionMap) {

        List<Map<String, Object>> result = null;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;

        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));
        conditionMap.put("userEvents", getMeterEventByUser((Long)conditionMap.get("userId")));
        result = meterEventLogDao.getMeterEventLogMaxChartData(conditionMap);
        
        for(Map<String, Object> data: result) {
            map = new HashMap<String, Object>();
            map.put("eventName", data.get("EVENTNAME"));
            map.put("eventCount", data.get("EVENTCOUNT"));
            list.add(map);
        }
        
        return list;
    }
    
    public String getMeterEventLogConditionEvent(Map<String, Object> conditionMap) {
        StringBuilder eventNames = new StringBuilder();
        
        List<Map<String, Object>> list = getMeterEventLogMaxChartData(conditionMap);
        int cnt = 0 ; 
        for (Map<String, Object> data : list) {
            if (cnt != 0) {
                eventNames.append(',');
            }
            eventNames.append("'");
            eventNames.append(data.get("eventName"));
            eventNames.append("'");
            
            cnt++;
        }
        
        return eventNames.toString();
    }

    public Integer getMeterEventLogMeterByEventGridDataCount(Map<String, Object> conditionMap) {

        List<Map<String, Object>> result = null;
//        String eventCondition = getMeterEventLogConditionEvent(conditionMap);
//        
//        if (eventCondition.length() == 0) {
//            return 0;
//        }

        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));
//        conditionMap.put("eventCondition", eventCondition);
        conditionMap.put("userEvents", getMeterEventByUser((Long)conditionMap.get("userId")));
        result = meterEventLogDao.getMeterEventLogMeterByEventGridData(conditionMap, true);

//        return result.size();
        if(result != null) {
        	Map<String, Object> obj = result.get(0);
            System.out.println(" result count : " + obj.get("total"));            
            return (Integer)obj.get("total");
        } else {
        	return 0;
        }
    }

    public List<Map<String, Object>> getMeterEventLogMeterByEventGridData(Map<String, Object> conditionMap) {

    	String supplierId = (String)conditionMap.get("supplierId");
        List<Map<String, Object>> result = null;
        
        
//        String eventCondition = getMeterEventLogConditionEvent(conditionMap);
//        
//        if (eventCondition.length() == 0) {
//            return new ArrayList<Map<String, Object>>();
//        }

        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));
//        conditionMap.put("eventCondition", eventCondition);
        conditionMap.put("userEvents", getMeterEventByUser((Long)conditionMap.get("userId")));
        result = meterEventLogDao.getMeterEventLogMeterByEventGridData(conditionMap, false);
        
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        
        for (Map<String, Object> map : result) {
			map.put("METERCOUNT",map.get("METERCOUNT") == null ? null : dfMd.format(map.get("METERCOUNT")));
		}

        return result;
    }

    public Integer getMeterEventLogEventByMeterGridDataCount(Map<String, Object> conditionMap) {

        List<Map<String, Object>> result = null;
//        String eventCondition = getMeterEventLogConditionEvent(conditionMap);
//        
//        if (eventCondition.length() == 0) {
//            return 0;
//        }

        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));
//        conditionMap.put("eventCondition", eventCondition);
        conditionMap.put("userEvents", getMeterEventByUser((Long)conditionMap.get("userId")));
        result = meterEventLogDao.getMeterEventLogEventByMeterGridData(conditionMap, true);
        
        if(result != null) {
        	Map<String, Object> obj = result.get(0);
            System.out.println(" result count : " + obj.get("total"));
            
            return (Integer)obj.get("total");
        } else {
        	return 0;
        }
        
    }

    public List<Map<String, Object>> getMeterEventLogEventByMeterGridData(Map<String, Object> conditionMap) {

        List<Map<String, Object>> result = null;
//        String eventCondition = getMeterEventLogConditionEvent(conditionMap);
//        
//        if (eventCondition.length() == 0) {
//            return new ArrayList<Map<String, Object>>();
//        }

        conditionMap.put("locationCondition", getLeafLocationId((String)conditionMap.get("locationId"), (String)conditionMap.get("supplierId")));
//        conditionMap.put("eventCondition", eventCondition);
        conditionMap.put("userEvents", getMeterEventByUser((Long)conditionMap.get("userId")));
        result = meterEventLogDao.getMeterEventLogEventByMeterGridData(conditionMap, false);
        
        Supplier supplier = supplierDao.get(Integer.parseInt((String)conditionMap.get("supplierId")));

        for(Map<String, Object> data: result) {
            data.put("OPENTIME", TimeLocaleUtil.getLocaleDate((String)data.get("OPENTIME") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            data.put("WRITETIME", TimeLocaleUtil.getLocaleDate((String)data.get("WRITETIME") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
        }
        
        return result;
    }
    
    public MeterEvent getMeterEventByCondition(Map<String,Object> conditionMap) {
    	return meterEventDao.getMeterEventByCondition(conditionMap);
    }
}