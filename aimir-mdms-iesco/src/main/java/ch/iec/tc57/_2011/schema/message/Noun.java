package ch.iec.tc57._2011.schema.message;

import com.aimir.mars.integration.bulkreading.xml.*;
import com.aimir.mars.integration.bulkreading.xml.cim.MeterReadingsType;

public enum Noun {
	
	MeterReadings	(MeterReadingsType.class,	"MeterReadings",	"MeterReadings"),
	deviceList	(MeterReadingsType.class,	"deviceList",	"deviceList");
//	EndDeviceControls	(EndDeviceControls.class,	"EndDeviceControls",	"EndDeviceControls"),
//	EndDeviceEvents	(EndDeviceEvents.class,	"EndDeviceEvents",	"EndDeviceEvents");
		
	private String returnType;
	private Object returnObj;
	private String description;
	 
	Noun(Object obj,String returnType,String description) {
		this.returnObj = obj;
        this.returnType = returnType;
        this.description = description;
    }
	
    public String getDescription() {
        return this.description;
    }
    
    public String getReturnType() {
        return this.returnType;
    }
    
    public Object getReturnObj() {
		return returnObj;
	}
    
	public static Enum<Noun> findEnum(String str){
    	Noun.valueOf(str);
    	return Noun.valueOf(str);
    }
	
	public static Noun getNounByReturnType(String str){
		Noun val = null ;

		for(Noun re :  values()){
			if(re.getReturnType().equals(str)){
				val = re; 
			}
		}
		
		return val;
	}
	
}
