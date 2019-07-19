package com.aimir.schedule.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.util.DataUtil;
import com.aimir.util.DateTimeUtil;

public class CommonUtil {
	@SuppressWarnings("unchecked")
	public static String MapToJSON(Map map) throws Exception {
		StringBuffer rStr = new StringBuffer();
		Iterator<String> keys = map.keySet().iterator();
		String keyVal = null;
		rStr.append("[");
		while (keys.hasNext()) {
			keyVal = (String) keys.next();
			rStr.append("{\"name\":\"");
			rStr.append(keyVal);
			rStr.append("\",\"value\":\"");
			rStr.append(map.get(keyVal));
			rStr.append("\"}");
			if (keys.hasNext()) {
				rStr.append(",");
			}
		}
		rStr.append("]");
		return rStr.toString();
	}

	@SuppressWarnings("unchecked")
	public static String MapToJSON(String[] array) throws Exception {
		StringBuffer rStr = new StringBuffer();
		rStr.append("[");
		for (int i = 0; array != null && i < array.length; i++) {
			rStr.append("{\"name\":\"");
			rStr.append("key[" + i + "]");
			rStr.append("\",\"value\":\"");
			rStr.append(array[i]);
			rStr.append("\"}");
		}
		rStr.append("]");
		return rStr.toString();
	}
	
    /**
     * Create 2016/12/12 SP-414
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public static Map<String,String> getParamValueByRange(String fromDate, String toDate) throws Exception {
    	Map<String,String> valueMap = new HashMap<String,String>();

    	String clockObis = DataUtil.convertObis(OBIS.CLOCK.getCode());
    	String option="1";	//option 0 is offset, option 1 is range_descriptor(date). but not yet implement offset.

    	valueMap.put("clockObis", clockObis);
    	valueMap.put("option", option);
    	Calendar fromCal = null;
    	if (fromDate != null && !fromDate.equals("")) {
    		fromCal = DateTimeUtil.getCalendar(fromDate);
    	} else {
    		fromCal = Calendar.getInstance();
    	}
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    	fromDate = formatter.format(fromCal.getTime());
    	valueMap.put("fYear", fromDate.substring(0,4));
    	valueMap.put("fMonth", fromDate.substring(4,6));
    	valueMap.put("fDayOfMonth", fromDate.substring(6,8));
    	valueMap.put("fDayOfWeek", String.valueOf(fromCal.get(Calendar.DAY_OF_WEEK)));
    	valueMap.put("fHh", fromDate.substring(8,10));
    	valueMap.put("fMm", fromDate.substring(10,12));
    	valueMap.put("fSs", fromDate.substring(12,14));

    	Calendar toCal = null;
    	if (toDate != null && !toDate.equals("")) {
    		toCal = DateTimeUtil.getCalendar(toDate);
    	} else {
    		toCal = Calendar.getInstance();
    	}
    	toDate = formatter.format(toCal.getTime());

    	valueMap.put("tYear", toDate.substring(0,4));
    	valueMap.put("tMonth", toDate.substring(4,6));
    	valueMap.put("tDayOfMonth", toDate.substring(6,8));
    	valueMap.put("tDayOfWeek", String.valueOf(toCal.get(Calendar.DAY_OF_WEEK)));
    	valueMap.put("tHh", toDate.substring(8,10));
    	valueMap.put("tMm", toDate.substring(10,12));
    	valueMap.put("tSs", toDate.substring(12,14));

    	return valueMap;
    }
    
	/**
	 * Create 2016/12/12 SP-414
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public static String meterParamMapToJSON(Map map) {
        StringBuffer rStr = new StringBuffer();
        Iterator<String> keys = map.keySet().iterator();
        String keyVal = null;
        rStr.append("[{");
        while (keys.hasNext()) {
            keyVal = (String) keys.next();
            rStr.append("\""+keyVal+"\":");
            rStr.append("\""+map.get(keyVal)+"\"");
            if (keys.hasNext()) {
                rStr.append(",");
            }
        }
        rStr.append("}]");
        return rStr.toString();
    }
}
