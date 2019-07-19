package com.aimir.test.fep.pattern.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.aimir.constants.CommonConstants.TargetClass;
//import com.aimir.fep.util.EventUtil;
//import com.aimir.model.device.EventAlertLog;

public class TestRegistration {

	private static Log log = LogFactory.getLog(TestRegistration.class);
	    
	//private TargetClass devType = null;
	private String devId = null;
	
	private boolean isRandomId = false;
	private long randomCount = 0L;
	
	private String from = null;
	private String to = null;
	
    public void test_EquipmentRegistration() {
    	
    	String devId2 = "";
    	
    	//if(devType.equals(TargetClass.ZRU)){
    	//	devId2 = "sensorID";
    	//}else if(devType.equals(TargetClass.EnergyMeter)){
    	//	devId2 = "meterId";
    	//}
    	
/*
        try {
        	
        	if(from != null && !"".equals(from) && to != null && !"".equals(to)){
                //DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
        		
        		int digitLength = from.length();
        		long start = Long.parseLong(from);
        		long end = Long.parseLong(to);
        		for(long i = start; i < end; i++){
        			devId = i+"";
        			
        			for(int k = devId.length(); k < digitLength; k++){
        				devId = "0"+devId;
        			}
                    EventUtil.sendEvent("Equipment Registration",
                            devType,
                            devId,
                            new String[][] {
                                            {"mcuID", "8001"},
                                            {devId2, "Id["+devId+"] Port[0]"}
                            },
                            new EventAlertLog());
        		}
        		

        	}
        	

        }
        catch (Exception e) {
            log.error(e);
        }    */
    }


	//public TargetClass getDevType() {
	//	return devType;
	//}

	//public void setDevType(TargetClass devType) {
	//	this.devType = devType;
	//}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public boolean isRandomId() {
		return isRandomId;
	}

	public void setRandomId(boolean isRandomId) {
		this.isRandomId = isRandomId;
	}

	public long getRandomCount() {
		return randomCount;
	}

	public void setRandomCount(long randomCount) {
		this.randomCount = randomCount;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
