package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

	public static void main(String[] args) {

		Map<String, Object> condition1 = new HashMap<String, Object>();
		Map<String, Object> condition2 = new HashMap<String, Object>();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> result2 = new ArrayList<Map<String, Object>>();
        
        condition1.put("SYS_ID", "781");
        condition1.put("RELATED_METER", "22");
        condition1.put("NAME", "SSYS");
        condition1.put("METERING_COUNT", "1");
        result.add(condition1);
        
        condition2.put("SYS_ID", "782");
        condition2.put("RELATED_METER", "1");
        condition2.put("NAME", "BKK");
        condition2.put("METERING_COUNT", "0");
        
        result2.add(condition2);
        
		System.out.println("result : " + result);
		System.out.println("result2 : " + result2);
		System.out.println("====");
        
        for (Map<String, Object> map : result2) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
       	        String key = entry.getKey();
       	        Object value = entry.getValue();
       	        map.put(key,value);
       	    }
             result.add(map);
        }
        
		System.out.println("result : " + result);
        
        
        
		// 임승한 DSO별 통계 TODO
		ArrayList<String> dsoList = new ArrayList<String>();
		Map<String, Object> dsoCalcMap = new HashMap<String, Object>();
		
		for (Map<String, Object> map : result) {
			if (!dsoList.contains(map.get("NAME").toString())) {
				dsoList.add(map.get("NAME").toString());
				dsoCalcMap.put(map.get("NAME").toString(), 0);
			}
		}

		System.out.println("===> dsoList : " + dsoList);
		System.out.println("===> dsoCalcMap : " + dsoCalcMap);
		
		
		
		
	}
	
}
