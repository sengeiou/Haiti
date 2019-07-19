package com.aimir.fep.meter.parser.a1830rlnTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.LineType;
import com.aimir.constants.CommonConstants.MeterEventKind;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.parser.ElsterA1700;
import com.aimir.fep.meter.parser.elsterA1700Table.EVENT_ATTRIBUTE.EVENTATTRIBUTE;
import com.aimir.fep.util.DataFormat;

public class A1800_EV implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -8476318715075036355L;
	private Log log = LogFactory.getLog(A1800_EV.class);
	private byte[] rawData = null;
	List<PowerAlarmLogData> powerAlarmList = new ArrayList();
	
	private Vector<PowerAlarmLogData> powerAlarmLogData = new Vector<PowerAlarmLogData>();
	
	private ST76 st76 = null;
	public A1800_EV(byte[] rawData) {
        this.rawData = rawData;
		parse();
	}
	
	public void parse() {		
		this.st76 = new ST76(rawData);
	}
		
	public EventLogData[] getEventLogData(){
		return this.st76.getEvent();
	}
	
	public PowerAlarmLogData[] getPowerAlarmLogData(){
		return null;
	}
	
	public Vector<PowerAlarmLogData> getPowerAlarmLog() {
		EventLogData[] eventPowerLog = this.st76.getEvent();
		int size = eventPowerLog.length;

		boolean powerOn = false;
		String closeDate = "";
		String closeTime = "";
		
		for(int i = 0 ; i <size;i++){
			
			if(eventPowerLog[i].getFlag() == 2){			//on				
				// Power ON Log
				powerOn = true;
				closeDate = eventPowerLog[i].getDate();
				closeTime = eventPowerLog[i].getTime();
				
			}else if(eventPowerLog[i].getFlag() == 1){ 	//off
				// Power Off log
				if(powerOn){
					powerOn=false;
					
					PowerAlarmLogData paData = new PowerAlarmLogData();
					paData.setFlag(eventPowerLog[i].getFlag());
					paData.setMsg(eventPowerLog[i].getMsg());
					paData.setKind(eventPowerLog[i].getKind());
					paData.setDate(eventPowerLog[i].getDate());
					paData.setTime(eventPowerLog[i].getTime());
					paData.setCloseDate(closeDate);
					paData.setCloseTime(closeTime);

					this.powerAlarmLogData.add(paData);
					
				}				
			}
			
			
		}		
		return this.powerAlarmLogData;
	}
	
	private void parsePowerAlarmLog() throws Exception {

	}

}
