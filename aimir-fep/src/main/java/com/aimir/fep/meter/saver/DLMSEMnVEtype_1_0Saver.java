/**
 * (@)# DLMSEMnVEtype10Saver.java
 *
 * 2015. 6. 30.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.meter.saver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSEMnVEType_1_0;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * @author nuri
 *
 */
@Service
public class DLMSEMnVEtype_1_0Saver extends AbstractMDSaver {
	private static Logger log = LoggerFactory.getLogger(DLMSEMnVEtype_1_0Saver.class);

	@Autowired
	LpEMDao lpEMDao;

	@Autowired
	MonthEMDao monthEMDao;

	@Autowired
	TariffEMDao tariffDao;

	@Autowired
	RealTimeBillingEMDao billingEmDao;

	@Override
    public boolean save(IMeasurementData md) throws Exception {
        try {
            DLMSEMnVEType_1_0 parser = (DLMSEMnVEType_1_0) md.getMeterDataParser();
            parser.setMeteringValue();
            LPData[] lplist = parser.getLPData();
            
            if (lplist == null || lplist.length <= 1) {
            	log.debug("LP SIZE => {}", lplist == null? 0 : lplist.length);
            } else {
            	int startLpListParam = 1;  // 시작 lp
                log.info("lplist[0]:"+lplist[startLpListParam]);
                log.info("lplist[0].getDatetime():"+lplist[startLpListParam].getDatetime());
    			
                String startlpdate = lplist[startLpListParam].getDatetime();
                String lpdatetime = startlpdate;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                cal.setTime(sdf.parse(startlpdate));
                List<Double>[] chValue = new ArrayList[lplist[startLpListParam].getCh().length];
                List<Integer> flag = new ArrayList<Integer>();
                //double baseValue = lplist[startLpListParam].getCh()[1];
                double baseValue = lplist[startLpListParam].getCh()[0];
    	        
                for (int i = startLpListParam; i < lplist.length; i++) {
                //for (int i = 0; i < lplist.length; i++) {
                    if (!lpdatetime.equals(lplist[i].getDatetime())) {
                        saveLPData(chValue, flag, startlpdate, baseValue, parser);
    	        		
                        startlpdate = lplist[i].getDatetime();
                        lpdatetime = startlpdate;
                        baseValue = lplist[i].getLpValue();
                        flag = new ArrayList<Integer>();
                        chValue = new ArrayList[lplist[i].getCh().length];
                    }
                    flag.add(lplist[i].getFlag());
            		
                    for (int ch = 0; ch < chValue.length; ch++) {
                        if (chValue[ch] == null) chValue[ch] = new ArrayList<Double>();
            			
                        if (ch+1 <= lplist[i].getCh().length)
                            chValue[ch].add(lplist[i].getCh()[ch]);
                        else
                            chValue[ch].add(0.0);
                    }
                    cal.add(Calendar.MINUTE, parser.getMeter().getLpInterval());
                    lpdatetime = sdf.format(cal.getTime());
                }
                
                saveLPData(chValue, flag, startlpdate, baseValue, parser);
                log.debug("### 1 saveLPData 완료 ####");
                log.debug("### 2 saveBill   없음 ####");
                log.debug("### 3 saveMeterEventLog 없음 ####");
    	        
                //if (billData != null && parser.getMeter().getContract() != null){
                if (parser.getMeter().getContract() != null){
                    saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                            md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                            parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                            parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
                    
                    log.debug("### 4 saveMeteringData 완료 ####");
                }

                log.debug("### 5 savePowerQuality 없음 ####");
                
                try {
                    Meter meter = parser.getMeter();
                    String dsttime = DateTimeUtil.getDST(null, md.getTimeStamp());
                    if (meter.getLastReadDate() == null || dsttime.compareTo(meter.getLastReadDate()) > 0) {
                        meter.setLastReadDate(dsttime);
                        
                        String notTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim();
                        log.debug("##### [E-SAVER] 임시 통신시간 저장 체크 [미터={}] [dsttime-{} / 현재시간-{}", new Object[]{meter.getMdsId(), dsttime, notTime});
                        log.debug("##### [E-SAVER] 임시 통신시간 저장 체크 [미터={}] [dsttime-{} / 현재시간-{}", new Object[]{meter.getMdsId(), dsttime, notTime});
                        log.debug("##### [E-SAVER] 임시 통신시간 저장 체크 [미터={}] [dsttime-{} / 현재시간-{}", new Object[]{meter.getMdsId(), dsttime, notTime});
                        
                        
                        Code meterStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
                        log.debug("METER_STATUS[" + (meterStatus==null? "NULL":meterStatus.getName()) + "]");
                        meter.setMeterStatus(meterStatus);
                        meter.setLastMeteringValue(parser.getMeteringValue());
                        
                        //String meterTime = parser.getMeterTime();
                        //meterTime = meterTime.length() != 14 ? meterTime + "00" : meterTime;
                        if (md.getTimeStamp() != null && !"".equals(md.getTimeStamp())) {
                        	
                            long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(notTime).getTime() - DateTimeUtil.getDateFromYYYYMMDDHHMMSS(md.getTimeStamp()).getTime();
                            meter.setTimeDiff(Long.parseLong(String.valueOf(Math.round(diff / 1000 / 60))));
                            
                            if(0 < diff){
                                log.debug("### [{}] TIME_DIFF 발생!! timestamp={}, meterTime={}, diff={}, set_diff={}분", 
                                		new Object[]{meter.getMdsId(), md.getTimeStamp(), notTime, diff, meter.getTimeDiff()});                            	
                            }
                        }
                        
                     // 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
                        if (meter.getModem() != null) {
                            meter.getModem().setLastLinkTime(dsttime);
                            
                            String tt = meter.getModem().getDeviceSerial();
                            log.debug("##### [E-SAVER]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [dsttime-{} / 현재시간-{}", new Object[]{tt, dsttime, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim()});
                            log.debug("##### [E-SAVER]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [dsttime-{} / 현재시간-{}", new Object[]{tt, dsttime, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim()});
                            log.debug("##### [E-SAVER]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [dsttime-{} / 현재시간-{}", new Object[]{tt, dsttime, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim()});
                        }
                    }
                }
                catch (Exception ignore) {}
            }
	    log.info("{} Metering END......!!!!", parser.getMDevId());	
        }
        catch (Exception e) {
            log.error("Error - ", e);
            throw e;
        }
        return true;
    }
	
    public void saveLPData(List<Double>[] chValue, List<Integer> flag, String startlpdate, double baseValue, DLMSEMnVEType_1_0 parser)
            throws Exception {
        double[][] _lplist = new double[chValue.length][chValue[0].size()];
        for (int ch = 0; ch < _lplist.length; ch++) {
            for (int j = 0; j < _lplist[ch].length; j++) {
                if (chValue[ch].get(j) != null)
                    _lplist[ch][j] = chValue[ch].get(j);
                else
                    _lplist[ch][j] = 0.0;
            }
        }
        int[] _flag = new int[chValue[0].size()];
        for (int j = 0; j < _flag.length; j++) {
            _flag[j] = flag.get(j);
        }
        
//        super.saveLPData(MeteringType.Normal, startlpdate.substring(0, 8), startlpdate.substring(8)+"00",
//                _lplist, _flag, baseValue, parser.getMeter(),
//                DeviceType.Modem, parser.getMeter().getModem().getDeviceSerial(),    
//                DeviceType.Meter, parser.getMeterID());
        super.saveLPData(MeteringType.OnDemand, startlpdate.substring(0, 8), startlpdate.substring(8)+"00",
                _lplist, _flag, baseValue, parser.getMeter(),
                DeviceType.Modem, parser.getMeter().getModem().getDeviceSerial(),    
                DeviceType.Meter, parser.getMeterID());
    }
		
    @Override
    public String relayValveOn(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYON.getCode());
            
            
            if (resultMap != null && resultMap.get("LoadControlStatus") != null) {
                LOAD_CONTROL_STATUS ctrlStatus =  (LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus");
                
                if (ctrlStatus == LOAD_CONTROL_STATUS.CLOSE) {
                    updateMeterStatusNormal(meter);
                }
            }
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

    @Override
    public String relayValveOff(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYOFF.getCode());
            
            
            if (resultMap != null && resultMap.get("LoadControlStatus") != null) {
                LOAD_CONTROL_STATUS ctrlStatus =  (LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus");
                
                if (ctrlStatus == LOAD_CONTROL_STATUS.OPEN) {
                    updateMeterStatusCutOff(meter);
                }
            }
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            int nOption = OnDemandOption.READ_OPTION_RELAY.getCode(); //read table
            resultMap = commandGw.cmdOnDemandMeter( mcuId, meterId, nOption);
            
            if (resultMap != null) {
                if ((LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus") == LOAD_CONTROL_STATUS.OPEN) {
                    updateMeterStatusCutOff(meter);
                }
                else if ((LOAD_CONTROL_STATUS)resultMap.get("LoadControlStatus") == LOAD_CONTROL_STATUS.CLOSE) {
                    updateMeterStatusNormal(meter);
                }
            }
        }
        catch (Exception e) {
            log.error("ERROR - ", e);
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

    @Override
    public String syncTime(String mcuId, String meterId) {
        Map<String, String> resultMap = new HashMap<String, String>();
        int result = 0;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            resultMap = commandGw.cmdMeterTimeSyncByGtype(mcuId,meter.getMdsId());
            if(resultMap != null) {
                String before = (String) resultMap.get("beforeTime");
                String after = (String) resultMap.get("afterTime");
                String diff = String.valueOf((TimeUtil.getLongTime(after) - TimeUtil.getLongTime(before))/1000);
                resultMap.put("diff", diff);
                
                saveMeterTimeSyncLog(meter, before, after, result);
            }
        }
        catch (Exception e) {
            result = 1;
            resultMap.put("failReason", e.getMessage());
        }
        return MapToJSON(resultMap);
    }

}
