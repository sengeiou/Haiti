package com.aimir.model.device;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

public class EndDeviceVO  implements JSONString{
	int id = -1;
	String location = "";
	String locationId = "";
	String endDeviceId = "";
	String type = "";
	String typeId = "";
	String manufacturerer = "";
	String model = "";
	String friendlyName = "";
	String installDate = "";
	String manufactureDate = "";
	String powerConsumption = "";
	String modemId = "0";
	String modemType = "";
	String modemTypeId = "";
	String modemSerial = "";
	String facilityType="";
	String status="";
	int running=0;
	int stop=0;
	int unknown=0;
	
	String dayEM="";
	String dayGM="";
	String dayHM="";
	String dayWM="";
	public EndDeviceVO(){
		
	}
	public EndDeviceVO(EndDevice endDevice) {
		
//    	SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
//        Calendar calendar =  Calendar.getInstance();
//        String someDay = endDevice.getInstallDate();
//        int year = Integer.parseInt(someDay.substring(0, 4));
//        int month = Integer.parseInt(someDay.substring(4, 6)) - 1;
//        int date = Integer.parseInt(someDay.substring(6, 8));
//
//        calendar.set(year, month, date);
        
		this.setId(endDevice.getId());
		this.setEndDeviceId(endDevice.getId() + "");
		this.setFriendlyName(endDevice.getFriendlyName());
		// this.setInstallDate(sf.format(calendar.getTime()));	
		this.setInstallDate(endDevice.getInstallDate());
		
		this.setManufacturerer(endDevice.getManufacturer());
		
//		someDay = endDevice.getManufactureDate();
//        year = Integer.parseInt(someDay.substring(0, 4));
//        month = Integer.parseInt(someDay.substring(4, 6)) - 1;
//        date = Integer.parseInt(someDay.substring(6, 8));
//        calendar.set(year, month, date);
        
		// this.setManufactureDate(sf.format(calendar.getTime()));
		this.setManufactureDate(endDevice.getManufactureDate());
		this.setModel(endDevice.getModelName());
		this.setPowerConsumption(endDevice.getPowerConsumption()+"");
		if (endDevice.getControllerCode() != null) {
    		this.setModemType(endDevice.getControllerCode().getDescr());
    		this.setModemTypeId(endDevice.getControllerCode().getId().toString());
		}
		this.setModemSerial(endDevice.getSerialNumber());
		this.setStatus(endDevice.getStatusCode().getDescr());
		if (endDevice.getCategoryCode() != null) {
			this.setType(endDevice.getCategoryCode().getDescr());
			this.setTypeId(endDevice.getCategoryCode().getId()+"");
		}
		if (endDevice.getLocation() != null) {
			this.setLocation(endDevice.getLocation().getName());
			this.setLocationId(endDevice.getLocation().getId() + "");
		}
		if(endDevice.getModem() != null){
			this.setModemId(endDevice.getModem().getId()+"");
						
			this.setModemSerial(endDevice.getModem().getDeviceSerial());
			
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getEndDeviceId() {
		return endDeviceId;
	}

	public void setEndDeviceId(String endDeviceId) {
		this.endDeviceId = endDeviceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getManufacturerer() {
		return manufacturerer;
	}

	public void setManufacturerer(String manufacturerer) {
		this.manufacturerer = manufacturerer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getInstallDate() {
		return installDate;
	}

	public void setInstallDate(String installDate) {
		this.installDate = installDate;
	}

	public String getManufactureDate() {
		return manufactureDate;
	}

	public void setManufactureDate(String manufactureDate) {
		this.manufactureDate = manufactureDate;
	}

	public String getPowerConsumption() {
		return powerConsumption;
	}

	public void setPowerConsumption(String powerConsumption) {
		this.powerConsumption = powerConsumption;
	}

	public String getModemId() {
		return modemId;
	}

	public void setModemId(String modemId) {
		this.modemId = modemId;
	}

	public String getModemType() {
		return modemType;
	}

	public void setModemType(String modemType) {
		this.modemType = modemType;
	}
	
	public String getModemTypeId() {
		return modemTypeId;
	}

	public void setModemTypeId(String modemTypeId) {
		this.modemTypeId = modemTypeId;
	}

	public String getModemSerial() {
		return modemSerial;
	}

	public void setModemSerial(String modemSerial) {
		this.modemSerial = modemSerial;
	}
	
	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getRunning() {
		return running;
	}

	public void setRunning(int running) {
		this.running = running;
	}
	public void addRunning(int running) {
		this.running = this.running+running;
	}
	
	public int getStop() {
		return stop;
	}

	public void setStop(int stop) {
		this.stop = stop;
	}
	public void addStop(int stop) {
		this.stop = this.stop+stop;
	}
	
	public int getUnknown() {
		return unknown;
	}

	public void setUnknown(int unknown) {
		this.unknown = unknown;
	}
	
	public void addUnknown(int unknown) {
		this.unknown = this.unknown+unknown;
	}
	
	public String getDayEM() {
		return dayEM;
	}

	public void setDayEM(String dayEM) {
		this.dayEM = dayEM;
	}
	
	public String getDayGM() {
		return dayGM;
	}

	public void setDayGM(String dayGM) {
		this.dayGM = dayGM;
	}
	
	public String getDayWM() {
		return dayWM;
	}

	public void setDayWM(String dayWM) {
		this.dayWM = dayWM;
	}
	
	public String getDayHM() {
		return dayHM;
	}

	public void setDayHM(String dayHM) {
		this.dayHM = dayHM;
	}
	public String toJSONString() {
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("facilityType").value((this.facilityType == null)? "":this.facilityType)
    				   .key("running").value(running)
    				   .key("stop").value(stop)
    				   .key("unknown").value(unknown).endObject();
    				  
    				  
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	return js.toString();
	}
	

}
