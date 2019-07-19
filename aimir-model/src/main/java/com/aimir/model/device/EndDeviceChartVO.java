package com.aimir.model.device;


import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

public class EndDeviceChartVO implements JSONString {

	
	
	String facilityType="";
	String old="";
	String current="";
	

	public EndDeviceChartVO(String facilityType) {
      this.facilityType = facilityType;
	}
	
	public void setOld(String old){
		this.old = old;		
	}
	
	public void setCurrent(String current){
		this.current = current;
		
	}

	

	public String toJSONString() {
		JSONStringer js = null;

		try {
			js = new JSONStringer();
			js.object().key("facilityType").value(facilityType)
					   .key("old").value(old)
					   .key("current").value(current).endObject();

		} catch (Exception e) {
			System.out.println(e);
		}
		return js.toString();
	}

}
