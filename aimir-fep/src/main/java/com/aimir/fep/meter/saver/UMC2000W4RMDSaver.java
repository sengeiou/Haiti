package com.aimir.fep.meter.saver;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.UMC2000W4R;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;

@Service
public class UMC2000W4RMDSaver extends ZEUPLSMDSaver {
    private static Log log = LogFactory.getLog(UMC2000W4RMDSaver.class);
	
    @Override
    protected boolean save(IMeasurementData md) throws Exception 
    {
        super.save(md);
        
        // ZEUPLS2 검침값 외에 온디맨드시 미터 정보가 더 있다.
        UMC2000W4R parser = (UMC2000W4R)md.getMeterDataParser();
        WaterMeter meter = null;
        
        if (parser.isOnDemand()) {
            Double currentPulse = parser.getCurrentPulse();
            String serialNumber = parser.getSerialNumber();
            byte alarmStatus = parser.getAlarmStatus();
            byte meterStatus = parser.getMeterStatus();
            // meter version
            String functionTestResult = parser.getFunctionTestResult();
            String meterHwVerison = parser.getHwVersion();
            String meterSwVersion = parser.getSwVersion();
            
            meter = (WaterMeter)parser.getMeter();
            meter.setAlarmStatus(2000+(int)alarmStatus);
            meter.setMeterStatus(CommonConstants.getWaterMeterStatus((2000+(int)meterStatus)+""));
            meter.setHwVersion(meterHwVerison);
            meter.setSwVersion(meterSwVersion);
            meter.setMeterCaution(functionTestResult);
            meter.setLastMeteringValue(currentPulse);
        }
        else {
            byte alarmStatus = parser.getAlarmStatus();
            byte meterStatus = parser.getMeterStatus();
            
            meter = (WaterMeter)parser.getMeter();
            meter.setAlarmStatus(2000+(int)alarmStatus);
            meter.setMeterStatus(CommonConstants.getWaterMeterStatus((2000+(int)meterStatus)+""));
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
        
            WaterMeter wmeter = (WaterMeter)meter;
            
            // valveSerial 정보를 확인한다.
            //if(map.containsKey("serialNumber") && map.get("serialNumber")!=null){
            //    String valveSerial = (String) map.get("serialNumber");
            if(resultMap.containsKey("meterSerial") && resultMap.get("meterSerial")!=null){
                String valveSerial = (String) resultMap.get("meterSerial");
                
                // valveSerial 정보가 없을경우 기존 설정된 값을 보존한다.
                if(valveSerial.length() != 0)
                    wmeter.setValveSerial(valveSerial);
            }
            
            // alarmStatus 정보를 읽어와 code값에 해당하는 Name을 읽어온다.
            if(resultMap.containsKey("alarmStatus") && resultMap.get("alarmStatus")!=null){
                Integer alarmStatus = (Integer)resultMap.get("alarmStatus");
                wmeter.setAlarmStatus(alarmStatus);

                // bit정보를 상태 정보로 변환하여 출력 용도로 사용한다.
                String alarmStbuf = CommonConstants.getWaterMeterAlarmStatusCodesNames(alarmStatus);

                //기존 bit값을 삭제하고 출력용 String으로 대체한다.
                resultMap.remove("alarmStatus");
                resultMap.put("alarmStatus", alarmStbuf.toString());
            }
            
            // meterStatus 정보를 읽어와 code값에 해당하는 Name을 읽어온다.
            if(resultMap.containsKey("meterStatus") && resultMap.get("meterStatus")!=null){
                Integer meterStatus = (Integer)resultMap.get("meterStatus");
                //code와 업로드 값이 다름. 다만, 기초데이터 변경하기엔 그 영향을 확인하기 어려워서 여기서 임시 수정.
                Integer meterStatusCode = meterStatus + 2000;
                Code code = CommonConstants.getWaterMeterStatus(meterStatusCode.toString());
                wmeter.setMeterStatus(code);
                resultMap.remove("meterStatus");
                resultMap.put("meterStatus", code.getName());
            }
            
            // cp값 저장
            if(resultMap.containsKey("cp") && resultMap.get("cp")!=null){
                Integer cp = Integer.parseInt(resultMap.get("cp").toString());
                wmeter.setCurrentPulse(cp);
                resultMap.remove("cp");
                resultMap.put("cp", cp.toString());
            }
            
            //읽어온 정보를 저장한다.
            meterDao.update(wmeter);
        }
        catch(Exception e) {
        	log.error(e,e);
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
            //6:CenterRemoteBlock
            Code code = CommonConstants.getGasMeterStatus("6");
            meter.setMeterStatus(code);
            meterDao.update(meter);
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }
    
    @Override
    public String relayValveOn(String mcuId, String meterId) {
        String rtnStr = null;
        Map<String,Object> resultMap = new HashMap<String, Object>();
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            commandGw.cmdKDValveControl(meter.getModem().getDeviceSerial(), CommonConstants.ValveStatus.VALVE_ON.getValue());
            rtnStr = "Success"; //ErrorCode가 없으면, NoError임.
            updateMeterStatusNormal(meter);
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        resultMap.put("Result", rtnStr);
        //return MapToJSON(new String[]{rtnStr});
        return MapToJSON(resultMap);
    }
}
