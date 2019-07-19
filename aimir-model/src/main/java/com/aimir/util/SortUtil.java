package com.aimir.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Collections;

public class SortUtil {
	
	public static Map<String, Object> getCompareMap(Map<String, Object> params) {

		List<Map.Entry<String, Object>> entries = new ArrayList<Map.Entry<String, Object>>(
				params.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Object>>() {
			public int compare(Map.Entry<String, Object> a,
					Map.Entry<String, Object> b) {
				return a.getKey().compareTo(b.getKey());
			}
		});

		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

		for (int i = 0; i < entries.size(); i++) {
			Entry<String, Object> temp = (Map.Entry<String, Object>) entries
					.get(i);

			String key = temp.getKey();
			Object value = temp.getValue();
			resultMap.put(key, value);
		}
		return resultMap;
	}
	
}
