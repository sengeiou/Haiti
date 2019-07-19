package com.aimir.fep.meter.saver;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.UMC2000G4R;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;

public class UMC2000G4RMDSaver extends ZEUPLSMDSaver {
    private static Log log = LogFactory.getLog(UMC2000G4RMDSaver.class);
	
    @Override
    protected boolean save(IMeasurementData md) throws Exception 
    {
        super.save(md);
        
        // ZEUPLS2 검침값 외에 온디맨드시 미터 정보가 더 있다.
        UMC2000G4R parser = (UMC2000G4R)md.getMeterDataParser();
        GasMeter meter = null;
        
        if (parser.isOnDemand()) {
            Double currentPulse = parser.getCurrentPulse();
            String serialNumber = parser.getSerialNumber();
            byte alarmStatus = parser.getAlarmStatus();
            byte meterStatus = parser.getMeterStatus();
            // meter version
            String functionTestResult = parser.getFunctionTestResult();
            String meterHwVerison = parser.getHwVersion();
            String meterSwVersion = parser.getSwVersion();
            
            meter = (GasMeter)parser.getMeter();
            meter.setAlarmStatus(3000+(int)alarmStatus);
            meter.setMeterStatus(CommonConstants.getGasMeterStatus((3000+(int)meterStatus)+""));
            meter.setHwVersion(meterHwVerison);
            meter.setSwVersion(meterSwVersion);
            meter.setMeterCaution(functionTestResult);
            meter.setLastMeteringValue(currentPulse);
        }
        else {
            byte alarmStatus = parser.getAlarmStatus();
            byte meterStatus = parser.getMeterStatus();
            
            meter = (GasMeter)parser.getMeter();
            meter.setAlarmStatus(3000+(int)alarmStatus);
            meter.setMeterStatus(CommonConstants.getGasMeterStatus((3000+(int)meterStatus)+""));
        }
        log.debug(meter.toString());
        
        return true;
    }

    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        Map<String,Object> resultMap = new HashMap<String, Object>();
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            resultMap = commandGw.cmdKDGetMeterStatus(meter.getModem().getDeviceSerial());
        
            GasMeter gmeter = (GasMeter)meter;
            
            // alarmStatus 정보를 읽어와 code값에 해당하는 Name을 읽어온다.
            if(resultMap.containsKey("alarmStatus") && resultMap.get("alarmStatus")!=null){
                Integer alarmStatus = (Integer)resultMap.get("alarmStatus");
                gmeter.setAlarmStatus(alarmStatus);
    
                // bit정보를 상태 정보로 변환하여 출력 용도로 사용한다.
                String alarmStbuf = CommonConstants.getWaterMeterAlarmStatusCodesNames(alarmStatus);
    
                //기존 bit값을 삭제하고 출력용 String으로 대체한다.
                resultMap.remove("alarmStatus");
                resultMap.put("alarmStatus", alarmStbuf.toString());
            }
            
            // meterStatus 정보를 읽어와 code값에 해당하는 Name을 읽어온다.
            if(resultMap.containsKey("meterStatus") && resultMap.get("meterStatus")!=null){
                Integer meterStatus = (Integer)resultMap.get("meterStatus");
                Code code = CommonConstants.getWaterMeterStatus(meterStatus.toString());
                gmeter.setMeterStatus(code);
                resultMap.remove("meterStatus");
                resultMap.put("meterStatus", code.getName());
            }
            
            //읽어온 정보를 저장한다.
            meterDao.update(gmeter);
        }
        catch(Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }
    
    @Override
    public String relayValveOff(String mcuId, String meterId) {
        String rtnStr = null;
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            commandGw.cmdKDValveControl(meter.getModem().getDeviceSerial(), CommonConstants.ValveStatus.VALVE_OFF.getValue());
            rtnStr = "Success";
            
            Contract contract = meter.getContract();
            if(contract != null) {
                Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
                contractDao.updateStatus(contract.getId(), pauseCode);
            }
            GasMeter gmeter = (GasMeter)meter;
            
            //6:CenterRemoteBlock
            Code code = CommonConstants.getGasMeterStatus("6");
            gmeter.setMeterStatus(code);
            meterDao.update(gmeter);
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }
    
    @Override
    public String relayValveOn(String mcuId, String meterId) {
        String rtnStr = null;
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            commandGw.cmdKDValveControl(meter.getModem().getDeviceSerial(), CommonConstants.ValveStatus.VALVE_ON.getValue());
            updateMeterStatusNormal(meter);
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }
}
