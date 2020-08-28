package com.aimir.fep.meter.saver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.aimir.model.device.Modem;
import org.springframework.stereotype.Service;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.fep.command.conf.SM110Meta;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.I210Plus;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MeteringDataEM;
import com.aimir.model.mvm.MeteringDataPk;
import com.aimir.util.DateTimeUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class I210PlusMDSaver extends AbstractMDSaver {

	public void savePublic(IMeasurementData md) throws Exception {
		save(md);
	}
	
	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		I210Plus parser = (I210Plus) md.getMeterDataParser();
		log.info(parser.toString());
		
		Meter meter = meterDao.get(parser.getMDevId());
		
		// Save LP Data(S)
		saveLPUsingLpNormalization(
				CommonConstants.MeteringType.getMeteringType(parser.getMeteringType()),
				md,
				parser.getLPData(),
				parser.getMDevId(),
				parser.getDeviceId(),
				parser.getMDevType(), 
				parser.getMeteringTime());
		// Save LP Data(E)
		
		// Save MeteringDataEM(S)		
		try {
			MeteringDataEM meteringDataEM = new MeteringDataEM();
			meteringDataEM.setCh1(parser.getTOTAL_DEL_KWH());
			meteringDataEM.setCh2(parser.getTOTAL_DEL_PLUS_RCVD_KWH());
			meteringDataEM.setCh3(parser.getTOTAL_DEL_MINUS_RCVD_KWH());
			meteringDataEM.setCh4(parser.getTOTAL_REC_KWH());
			meteringDataEM.setDeviceId(meter.getMcu().getSysID());
			meteringDataEM.setDeviceType(DeviceType.MCU.toString());
			meteringDataEM.setHhmmss(parser.getMeteringTime().substring(8));
			meteringDataEM.setMeteringType(CommonConstants.MeteringType.getMeteringType(parser.getMeteringType()).getType());
			meteringDataEM.setValue(parser.getTOTAL_DEL_KWH());
			meteringDataEM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			meteringDataEM.setYyyymmdd(parser.getMeteringTime().substring(0,8));
			// PK
			meteringDataEM.setMDevId(parser.getMDevId());
			meteringDataEM.setYyyymmddhhmmss(parser.getMeteringTime());
			meteringDataEM.setDst(parser.getDst());
			meteringDataEM.setMDevType(parser.getMDevType().toString());
			
			meteringDataEM.setLocation(meter.getLocation());	
			meteringDataEM.setMeter(meter);
			meteringDataEM.setModem(meter.getModem());
			meteringDataEM.setSupplier(meter.getSupplier());
			meteringDataDao.saveOrUpdate(meteringDataEM);
		} catch (Exception e) {
			log.error(e,e);
		}
		// Save MeteringDataEM(E)
		
		// Save switch status(S)
		try {
			boolean switchStatus = parser.getACTUAL_SWITCH_STATE(); // 0 – Open, 1 – Close 
			log.debug("meter.getMeterStatus() : " + meter.getMeterStatus());
			log.debug("switchStatus : " + switchStatus);
			if(switchStatus){
				String code = CommonConstants.getMeterStatusCode(MeterStatus.Normal);
				meter.setMeterStatus(CommonConstants.getMeterStatus(code));
			}else{
				String code = CommonConstants.getMeterStatusCode(MeterStatus.CutOff);
				meter.setMeterStatus(CommonConstants.getMeterStatus(code));
			}
			log.debug("meter.getMeterStatus() : " + meter.getMeterStatus());
			meterDao.update(meter);
		} catch (Exception e) {
			log.error(e,e);
		}
		// Save switch status(E)

		return true;
	}

	@Override
    public String relayValveOff(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            Meter meter = meterDao.get(meterId);
            Modem modem = meter.getModem();
            if(modem == null) {
                resultMap.put("failReason", "Meter has no modem.");
                return MapToJSON(resultMap);
            }
            String deviceSerial = modem.getDeviceSerial();

            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            commandGw.cmdSetEnergyLevel(mcuId, deviceSerial, "15"); //1=open, 15=close.

            Thread.sleep(10000);	// Relay control 10초 대기
            log.debug("Wait for 10 sec for relayValveOff.");

            byte energyLevel = commandGw.cmdGetEnergyLevel(mcuId, deviceSerial);
            Integer relayStatus = (int)energyLevel;
            log.debug("energyLevel of meter["+meterId+"]: " + energyLevel);
            if(relayStatus.equals(15)){
                //결과 값이 15(Close)이면, 미터 상태를 'CutOff'로 변경한다.
                updateMeterStatusCutOff(meter);

                JsonArray jsonArray = new JsonArray();
                jsonArray.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
                return jsonArray.toString();
            }else{
                resultMap.put("failReason", "RelayStatus not changed.");
                return MapToJSON(resultMap);
            }

        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }
    
    @Override
    public String relayValveOn(String mcuId, String meterId)  {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            Meter meter = meterDao.get(meterId);
            Modem modem = meter.getModem();
            if(modem == null) {
                resultMap.put("failReason", "Meter has no modem.");
                return MapToJSON(resultMap);
            }
            String deviceSerial = modem.getDeviceSerial();

            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            commandGw.cmdSetEnergyLevel(mcuId, deviceSerial, "1"); //1=open, 15=close.

            Thread.sleep(10000);	// Relay control 10초 대기
            log.debug("Wait for 10 sec for relayValveOn.");

            byte energyLevel = commandGw.cmdGetEnergyLevel(mcuId, deviceSerial);
            Integer relayStatus = (int)energyLevel;
            log.debug("energyLevel of meter["+meterId+"]: " + energyLevel);
            if(relayStatus.equals(1)){
                //결과 값이 1(Open)이면, 미터 상태를 'Normal'로 변경한다.
                updateMeterStatusNormal(meter);

                JsonArray jsonArray = new JsonArray();
                jsonArray.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
                return jsonArray.toString();
            }else{
                resultMap.put("failReason", "RelayStatus not changed.");
                return MapToJSON(resultMap);
            }

        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

    @Override
    public String relayValveActivate(String mcuId, String meterId)  {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_ACTON.getCode());

            String str = relayValveStatus(mcuId, meterId);
            JsonArray ja = StringToJsonArray(str).getAsJsonArray();
            
            JsonObject jo = null;
            for (int i = 0; i < ja.size(); i++) {
                jo = ja.get(i).getAsJsonObject();
                if (jo.get("name").getAsString().equals("activateStatus") && jo.get("value").getAsString().equals("Activation")) {
                    ja.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
                }
            }
            return ja.toString();
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }
    
    @Override
    public String relayValveDeactivate(String mcuId, String meterId)  {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_ACTOFF.getCode());

            String str = relayValveStatus(mcuId, meterId);
            JsonArray ja = StringToJsonArray(str).getAsJsonArray();
            
            JsonObject jo = null;
            for (int i = 0; i < ja.size(); i++) {
                jo = ja.get(i).getAsJsonObject();
                if (jo.get("name").getAsString().equals("activateStatus") && jo.get("value").getAsString().equals("Deactivation")) {
                    ja.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
                }
            }
            return ja.toString();
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }
    
    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        Meter meter = meterDao.get(meterId);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.READ_OPTION_RELAY.getCode());

            if(resultMap != null){

                if(meter.getModel() != null && meter.getModel().getName().contains("SM110")){
                    // SM110
                    resultMap.put( "switchStatus", SM110Meta.getSwitchStatus((String)resultMap.get("relay status")) );
                    resultMap.put( "activateStatus", SM110Meta.getActivateStatus((String)resultMap.get("relay activate status")) );
                }else{
                    //I210기준 true=Valve close, false=Valve open.
                    boolean switchStatus = (boolean)resultMap.get("ACTUAL_SWITCH_STATE");
                    if(switchStatus) resultMap.put("switchStatus", "Close");
                    else resultMap.put("switchStatus", "Open");
                }

            }
        
            if (resultMap != null && resultMap.get("switchStatus") != null) {
                if(resultMap.get("switchStatus").equals("Open")){
                    updateMeterStatusNormal(meter);
                }
                else if(resultMap.get("switchStatus").equals("Close")){
                    updateMeterStatusCutOff(meter);
                }
                else if(resultMap.get("activateStatus").equals("Activation")) {
                    meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Activation.name()));
                    meterDao.update(meter);
                }
            }
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }
    
    @Override
    public String syncTime(String mcuId, String meterId) {
        Meter meter = meterDao.get(meterId);
        String[] result = null;
        
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            byte[] bx = commandGw.cmdMeterTimeSync(mcuId,meter.getMdsId());
            
            String beforeTime = CmdUtil.getYymmddhhmmss(bx,58,6);
            String afterTime = CmdUtil.getYymmddhhmmss(bx,73,6);
            
            saveMeterTimeSyncLog(meter, beforeTime, afterTime, 1);
            
            result = new String[]{beforeTime, afterTime};
        }
        catch (Exception e) {
            result = new String[]{e.getMessage()};
        }
        
        return MapToJSON(result);
    }
}
