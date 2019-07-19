package com.aimir.fep.meter.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.command.conf.DLMSMeta.CONTROL_STATE;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSClou3PTable.DLMSTable;
import com.aimir.fep.meter.parser.DLMSClou3PTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSClou3PTable.DLMSVARIABLE.ENERGY_LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSClou3PTable.DLMSVARIABLE.METER_DEVICE_MODEL;
import com.aimir.fep.meter.parser.DLMSClou3PTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSClou3PTable.DLMSVARIABLE.RELAY_STATUS_KAIFA;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EnergyMeter;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

public class DLMSClou3P extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 5099915362766709642L;

	private static Log log = LogFactory.getLog(DLMSClou.class);
	
	LPData[] lpData = null;
	
	Double[] MeteringDataChannelData = null;
	
	LinkedHashMap<String, Map<String, Object>> result = 
            new LinkedHashMap<String, Map<String, Object>>();
	
	String 	meterID = "";
    String 	modemID = "";
    String 	fwVersion = "";
    String 	meterModel = "";
    String 	logicalNumber = "";
    String 	manufactureSerial = "";
    String 	servicePointSerial = "";
    String 	meterVendor = "";
    Long 	ct_ratio = 0L;
    Long 	vt_ratio = 0L;
    Long 	ct_den = 0L;
    Long 	vt_den = 0L;
    Long 	trans_num = 0L;
    byte[] 	phaseType = null;
    Long	meterStatus = 0L;
    RELAY_STATUS_KAIFA	relayStatus;
    CONTROL_STATE		loadCtrlState;
    int 	loadCtrlMode;
    Double	limiterInfo = 0d;
    Double	limiterInfoMin = 0d;
    int 	modemPort = 0;
    
    int lpInterval = 60;
    boolean existLpInterval = false;
    
    double activePulseConstant = 1;
    
    Double	meteringValue= null;
    Double	ct = 1d;
        
    public enum CHANNEL_IDX {
    	CUMULATIVE_ACTIVEENERGY_IMPORT(1),
    	CUMULATIVE_ACTIVEENERGY_EXPORT(2),
    	CUMULATIVE_REACTIVEENERGY_IMPORT(3),
    	CUMULATIVE_REACTIVEENERGY_EXPORT(4),
    	AVERAGE_DEMAND_VALUE_TIME_OF_ACTIVE_POWER_IMPORT(5),
    	AVERAGE_DEMAND_VALUE_TIME_OF_ACTIVE_POWER_EXPORT(6),
    	AVERAGE_DEMAND_VALUE_TIME_OF_REACTIVE_POWER_IMPORT(7),
    	AVERAGE_DEMAND_VALUE_TIME_OF_REACTIVE_POWER_EXPORT(8),
    	INSTANTANEOUS_VOLTAGE_L1(9),
    	INSTANTANEOUS_VOLTAGE_L2(10),
    	INSTANTANEOUS_VOLTAGE_L3(11),
    	INSTANTANEOUS_CURRENT_L1(12),
    	INSTANTANEOUS_CURRENT_L2(13),
    	INSTANTANEOUS_CURRENT_L3(14);
    	
        private int index;
        
    	CHANNEL_IDX(int index) {
            this.index = index;
        }
        public int getIndex() {
        	return this.index;
        }
        public void setIndex(int index){
        	this.index = index;
        }
    }
	    
	@Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public void parse(byte[] data) throws Exception {
		System.out.println("Meter:"+meter.getMdsId()+", DLMS parse:"+Hex.decode(data));
		
		String obisCode = "";
        int clazz = 0;
        int attr = 0;

        int pos = 0;
        int len = 0;
        // DLMS Header OBIS(6), CLASS(2), ATTR(1), LENGTH(2)
        // DLMS Tag Tag(1), DATA or LEN/DATA (*)
        byte[] OBIS = new byte[6];
        byte[] CLAZZ = new byte[2];
        byte[] ATTR = new byte[1];
        byte[] LEN = new byte[2];
        byte[] TAGDATA = null;
        
        DLMSTable dlms = null;
        while (pos < data.length) {
        	dlms = new DLMSTable();
            System.arraycopy(data, pos, OBIS, 0, OBIS.length);
            pos += OBIS.length;
            obisCode = Hex.decode(OBIS);
            System.out.println("OBIS["+obisCode+"]");
            dlms.setObis(obisCode);
            
            System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
            pos += CLAZZ.length;
            clazz = DataUtil.getIntTo2Byte(CLAZZ);
            System.out.println("CLASS["+clazz+"]");
            dlms.setClazz(clazz);
            
            if (dlms.getDlmsHeader().getClazz() == null) break;
            
            System.arraycopy(data, pos, ATTR, 0, ATTR.length);
            pos += ATTR.length;
            attr = DataUtil.getIntToBytes(ATTR);
            System.out.println("ATTR["+attr+"]");
            dlms.setAttr(attr);

            System.arraycopy(data, pos, LEN, 0, LEN.length);
            pos += LEN.length;
            len = DataUtil.getIntTo2Byte(LEN);
            System.out.println("LENGTH["+len+"]");
            dlms.setLength(len);
            
            if (len == 0) continue;
            
            TAGDATA = new byte[len];
            if (pos + TAGDATA.length <= data.length) {
            	System.arraycopy(data, pos, TAGDATA, 0, TAGDATA.length);
            	pos += TAGDATA.length;
            }
            else {
            	System.arraycopy(data, pos, TAGDATA, 0, data.length-pos);
            	pos += data.length-pos;
            }
            
            System.out.println("TAGDATA=["+Hex.decode(TAGDATA)+"]");
            
            dlms.setMeter(meter);
            dlms.parseDlmsTag(TAGDATA);
            if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.ENERGY_LOAD_PROFILE) {
            	Map tempMap = dlms.getData();
            	if(tempMap.containsKey("LpInterval")){
    	        	Object obj = tempMap.get("LpInterval");
    	        	if (obj != null) {
    	        		lpInterval = ((Long)obj).intValue()/60; //sec -> min
    	        		existLpInterval = true;
    	        	}
    	        	System.out.println("LP_INTERVAL[" + lpInterval + "]");
            	}
            	
            	 for (int cnt = 0; ;cnt++) {
                     obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
                     if (!result.containsKey(obisCode)) {
                         result.put(obisCode, tempMap);
                         break;
                     }
                 }
            }
        }
        
        MeterType meterType = MeterType.valueOf(this.getMeter().getMeterType().getName());
        switch (meterType) {
	        case EnergyMeter :
	            EnergyMeter meter = (EnergyMeter)this.getMeter();
	            this.ct = 1.0;
	            if (meter != null && meter.getCt() != null && meter.getCt() > 0) {
	                ct = meter.getCt();
	            }
	            
	            setCt(ct);
	            break;
	        case GasMeter :
	            break;
	        case WaterMeter :
	            break;
        }                
        
        setMeterInfo();
        setLPData();
        setLPChannelData();
        setMeteringValue();
	}

	public void setMeterInfo() {
		
	}
	
	public void setLPData() {
		try {
			List<LPData> lpDataList = new ArrayList<LPData>();
	        METER_DEVICE_MODEL meterMo = DLMSVARIABLE.getMeterDeviceModel(meter.getModel().getName()); 
			
            Double lp = 0.0;
            Object value = null;
            Map<String, Object> lpMap = null;
            int cnt = 0;
            LPData _lpData = null;
	            
            double activeEnergyImport = 0.0;
            double activeEnergyExport = 0.0;
            double reactiveEnergyImport = 0.0;
            double reactiveEnergyExport = 0.0;
            double averageDemandValueActiveImport = 0.0;
            double averageDemandValueActiveExport = 0.0;
            double averageDemandValueRectiveImport = 0.0;
            double averageDemandValueRectiveExport = 0.0;
            double L1InstantVoltage = 0.0;
            double L2InstantVoltage = 0.0;
            double L3InstantVoltage = 0.0;
            double L1InstantCurrent = 0.0;
            double L2InstantCurrent = 0.0;
            double L3InstantCurrent = 0.0;
            
            for (int i = 0; i < result.size(); i++) {
            	 if (!result.containsKey(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i))
                     break;
            	 
            	 lpMap = (Map<String, Object>) result.get(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i);
            	 cnt = 0;
            	 
            	 while(true) {
            		 // Cumulative active energy +
            		 value = lpMap.get(ENERGY_LOAD_PROFILE.ActiveEnergyImport.name()+"-"+cnt);
            		 if(value != null) {
                      	if (value instanceof OCTET) lp = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long) lp = ((Long)value).doubleValue();
                      	
                      	activeEnergyImport = ( lp / activePulseConstant ) * 0.01;
            		 }
            		 
                     value = lpMap.get(ENERGY_LOAD_PROFILE.ActiveEnergyExport.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) activeEnergyExport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) activeEnergyExport = ((Long)value).doubleValue();
                     	
                     	activeEnergyExport /= activePulseConstant;
                     	activeEnergyExport *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.ReactiveEnergyImport.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) reactiveEnergyImport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) reactiveEnergyImport = ((Long)value).doubleValue();
                     	
                     	reactiveEnergyImport *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.ReactiveEnergyExport.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) reactiveEnergyExport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) reactiveEnergyExport = ((Long)value).doubleValue();
                     	
                     	reactiveEnergyExport *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.AverageDemandValueActiveImport.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) averageDemandValueActiveImport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) averageDemandValueActiveImport = ((Long)value).doubleValue();

                     	averageDemandValueActiveImport /= activePulseConstant;
                     	averageDemandValueActiveImport *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.AverageDemandValueActiveExport.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) averageDemandValueActiveExport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) averageDemandValueActiveExport = ((Long)value).doubleValue();

                     	averageDemandValueActiveExport /= activePulseConstant;
                     	averageDemandValueActiveExport *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.AverageDemandValueReactiveImport.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) averageDemandValueRectiveImport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) averageDemandValueRectiveImport = ((Long)value).doubleValue();

                     	averageDemandValueRectiveImport *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.AverageDemandValueReactiveExport.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) averageDemandValueRectiveExport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) averageDemandValueRectiveExport = ((Long)value).doubleValue();

                     	averageDemandValueRectiveExport *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.L1InstantVoltage.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) L1InstantVoltage = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) L1InstantVoltage = ((Long)value).doubleValue();

                     	L1InstantVoltage *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.L2InstantVoltage.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) L2InstantVoltage = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) L2InstantVoltage = ((Long)value).doubleValue();

                     	L2InstantVoltage *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.L3InstantVoltage.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) L3InstantVoltage = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) L3InstantVoltage = ((Long)value).doubleValue();

                     	L3InstantVoltage *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.L1InstantCurrent.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) L1InstantCurrent = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) L1InstantCurrent = ((Long)value).doubleValue();

                     	L1InstantCurrent *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.L2InstantCurrent.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) L2InstantCurrent = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) L2InstantCurrent = ((Long)value).doubleValue();

                     	L2InstantCurrent *= 0.01;
                     }

                     value = lpMap.get(ENERGY_LOAD_PROFILE.L3InstantCurrent.name()+"-"+cnt);
                     if (value != null) {
                     	if (value instanceof OCTET) L3InstantCurrent = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                     	else if (value instanceof Long) L3InstantCurrent = ((Long)value).doubleValue();

                     	L3InstantCurrent *= 0.01;
                     }
                     //Get Meter Time & Operation Time
                     Long lmeteringTime = meteringTime != null ? 
                     		DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringTime).getTime() : new Date().getTime(); ;
                     Long lmeterTime = meterTime != null ?
                        		DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime() : lmeteringTime;  
                        		
                     _lpData = new LPData((String) lpMap.get(ENERGY_LOAD_PROFILE.Date.name() + "-" + cnt), lp, activeEnergyImport);
                     _lpData.setPF(1d);
                     switch(meterMo) {
                     case CL710K22:
                    	 break;
                     case CL730S22:
                    	 _lpData.setCh(new Double[]{
                    			 activeEnergyImport, 
                    			 activeEnergyExport, 
                    			 reactiveEnergyImport, 
                    			 reactiveEnergyExport,
                    			 averageDemandValueActiveImport,
                    			 averageDemandValueActiveExport,
                    			 averageDemandValueRectiveImport,
                    			 averageDemandValueRectiveExport,
                    			 L1InstantVoltage,
                    			 L2InstantVoltage,
                    			 L3InstantVoltage,
                    			 L1InstantCurrent,
                    			 L2InstantCurrent,
                    			 L3InstantCurrent,
                    			 lmeterTime.doubleValue(), 
                    			 lmeteringTime.doubleValue() });
                    	 break;
                     case CL730D22H:
                    	 break;
                     case CL730D22L:
                    	 break;
                     }
                     
                     if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
                     	lpDataList.add(_lpData);
                     	System.out.println(_lpData.toString());
                     }else{
                         try {
                             EventUtil.sendEvent("Meter Value Alarm",
                                     TargetClass.valueOf(meter.getMeterType().getName()),
                                     meter.getMdsId(),
                                     new String[][] {{"message", "Wrong Date LP, DateTime[" + _lpData.getDatetime() + "]"}}
                                     );
                         }
                         catch (Exception ignore) {
                         }
                     }
                     
                     Collections.sort(lpDataList,LPComparator.TIMESTAMP_ORDER); 
                     lpDataList = checkDupLPAndWrongLPTime(lpDataList);
                     lpData = lpDataList.toArray(new LPData[0]);	// INSERT SP-501 (Uncomment)
                     log.debug("########################lpData.length:"+lpData.length);
                     
            		 cnt++;
            	 }
            	 
            }
	            
		}catch(Exception e) {
			log.error(e,e);
		}
	}
	
	public void setLPChannelData() {
		
	}
	
	public void setMeteringValue() {
		try {
			if(lpData != null && lpData.length > 0) {
				meteringValue = lpData[lpData.length - 1].getLpValue();
			}
			System.out.println("METERING_VALUE[" + meteringValue + "]");
		}catch(Exception e) {
			log.error(e,e);
		}
	}
	
	@Override
	public Double getMeteringValue() {
		return meteringValue;
	}
	
	@Override
	public LinkedHashMap<?, ?> getData() {
		return null;
	}
	
	@Override
	public String toString() {
		return null;
	}
	
	@Override
	public int getFlag() {
		return 0;
	}

	@Override
	public void setFlag(int flag) { }

	public void setCt(Double ct) {
		this.ct = ct;
	}
	
    /**
     * Time consistency check
     * @param chkDate
     * @return
     */
    private boolean checkLpDataTime(String chkDate) {
    	boolean ret = true;
    	String cd = chkDate.substring(8, 12); // YYYYMMDD[hhii]SS
    	if ("5255".equals(cd)){
    		ret = false;
    	}else{
        	// Time check
        	String hh = cd.substring(0, 2);
        	String mm = cd.substring(2, 4);
        	if (Integer.parseInt(hh) > 23){
        		ret = false;
        	}else if (Integer.parseInt(mm) > 59){
        		ret = false;
        	}
    	}

    	return ret;
    }

	private List<LPData> checkDupLPAndWrongLPTime(List<LPData> list) throws Exception {
        List<LPData> totalList = list;
        List<LPData> removeList = new ArrayList<LPData>();
        LPData prevLPData = null;
    	
        for(int i = 0; i < list.size(); i++){
        	// SP-783
        	// Time consistency check
        	removeList.add(list.get(i));
        	if (!checkLpDataTime(list.get(i).getDatetime())) {
                try {
                    EventUtil.sendEvent("Meter Value Alarm",
                            TargetClass.valueOf(meter.getMeterType().getName()),
                            meter.getMdsId(),
                            new String[][] {{"message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime() + "]"}}
                            );
                } catch (Exception ignore) { }
        	} else {
	        	if(prevLPData != null && prevLPData.getDatetime() != null && !prevLPData.getDatetime().equals("")){
	        		if(list.get(i).getDatetime().equals(prevLPData.getDatetime()) && list.get(i).getCh()[0].equals(prevLPData.getCh()[0])){
	        			removeList.add(list.get(i));
	                    try {
	                        EventUtil.sendEvent("Meter Value Alarm",
	                                TargetClass.valueOf(meter.getMeterType().getName()),
	                                meter.getMdsId(),
	                                new String[][] {{"message", "Duplicate LP, DateTime[" + list.get(i).getDatetime() + "] LP Val[" + list.get(i).getCh()[0] + "]"}}
	                                );
	                    } catch (Exception ignore) { }
	
	        		}else if(list.get(i).getDatetime().equals(prevLPData.getDatetime()) && list.get(i).getCh()[0] > prevLPData.getCh()[0]){
	        			System.out.println("time equls:" + list.get(i).getDatetime()); 
	        			removeList.add(list.get(i-1));
	                    try {
	                        EventUtil.sendEvent("Meter Value Alarm",
	                                TargetClass.valueOf(meter.getMeterType().getName()),
	                                meter.getMdsId(),
	                                new String[][] {{"message", "Duplicate LP and Diff Value DateTime[" + list.get(i).getDatetime() + "] LP Val[" + list.get(i).getCh()[0]+"/"+prevLPData.getCh()[0] + "]"}}
	                                );
	                    } catch (Exception ignore) { }
	        	    }else if(list.get(i).getDatetime().equals(prevLPData.getDatetime()) && list.get(i).getCh()[0] < prevLPData.getCh()[0]){
	        	    	System.out.println("time equls:" +list.get(i).getDatetime()); 
	        			removeList.add(list.get(i));
	                    try {
	                        EventUtil.sendEvent("Meter Value Alarm",
	                                TargetClass.valueOf(meter.getMeterType().getName()),
	                                meter.getMdsId(),
	                                new String[][] {{"message", "Duplicate LP and Diff Value DateTime[" + list.get(i).getDatetime() + "] LP Val[" + list.get(i).getCh()[0]+"/"+prevLPData.getCh()[0] + "]"}}
	                                );
	                    } catch (Exception ignore) { }
	        	    }
	        		
	        	}
	        	prevLPData = list.get(i);
	        	
	        	if(list.get(i).getDatetime().startsWith("1994") 
	        			|| list.get(i).getDatetime().startsWith("2000")
	        			|| (list.get(i).getDatetime().startsWith("2057") && !TimeUtil.getCurrentTime().startsWith("205"))){
	        		removeList.add(list.get(i));
	                try {
	                    EventUtil.sendEvent("Meter Value Alarm",
	                            TargetClass.valueOf(meter.getMeterType().getName()),
	                            meter.getMdsId(),
	                            new String[][] {{"message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime() + "]"}}
	                            );
	                } catch (Exception ignore) { }
	        	}
	        	if(meterTime != null && !"".equals(meterTime) 
	        			&& meterTime.length() == 14 
	        			&& list.get(i).getDatetime().compareTo(meterTime.substring(0, 12)) > 0){
	        		removeList.add(list.get(i));
	                try {
	                    EventUtil.sendEvent("Meter Value Alarm",
	                            TargetClass.valueOf(meter.getMeterType().getName()),
	                            meter.getMdsId(),
	                            new String[][] {{"message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime() + "] Meter Time[" + meterTime + "]"}}
	                            );
	                } catch (Exception ignore) { }
	        	}        	
	
	            Long lpTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(list.get(i).getDatetime()+"00").getTime();
	            Long serverTime = new Date().getTime(); ;
	 
	        	if(lpTime > serverTime){
	                try {
	                    EventUtil.sendEvent("Meter Value Alarm",
	                            TargetClass.valueOf(meter.getMeterType().getName()),
	                            meter.getMdsId(),
	                            new String[][] {{"message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime() + "] Current Time[" + TimeUtil.getCurrentTime() + "]"}}
	                            );
	                } catch (Exception ignore) { }
	        	}
        	}
        }
      
        totalList.removeAll(removeList);
        return totalList;
    }
}
