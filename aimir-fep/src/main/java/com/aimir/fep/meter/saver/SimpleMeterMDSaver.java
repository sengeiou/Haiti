package com.aimir.fep.meter.saver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.SimpleMeter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.DeviceModel;
import com.aimir.util.DateTimeUtil;

@Service
public class SimpleMeterMDSaver extends AbstractMDSaver {
	
	@Autowired DeviceModelDao deviceModelDao;
	
    @Override
    protected boolean save(IMeasurementData md) throws Exception 
    {
    	SimpleMeter parser = (SimpleMeter)md.getMeterDataParser();
        
        // 날짜포맷이 맞지 않거나 시스템시간의 일시가 다르면 
        if (parser.getCurrentTime() != null && 
                (parser.getCurrentTime().length() != 14)) {
            log.error("TIME[" + parser.getCurrentTime() + "] IS WRONG SO THEN RETURN NOT BACKUP");
            return false;
        }
        
        // period를 가져와서 주기를 검사한다.
        int interval = parser.getLpInterval() != 0? parser.getLpInterval():60;
        if (interval != parser.getMeter().getLpInterval())
            parser.getMeter().setLpInterval(interval);
        
        // 올라온 검침데이타의 검침값을 저장한다.
        saveMeteringData(MeteringType.Normal, parser.getCurrentTime().substring(0, 8),
                parser.getCurrentTime().substring(8), parser.getMeteringValue(),
                parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        

        
        LPData[] lplist = parser.getLplist();
        double[][] lpValues = new double[lplist[0].getCh().length][lplist.length];
        int[] flaglist = new int[lplist.length];
        double basePulse = lplist[0].getBasePulse();
		String yyyymmdd = lplist[0].getDatetime().substring(0, 8);
        String hhmm     = lplist[0].getDatetime().substring(8, 12);
			
        for (LPData data : lplist) {
            if (data == null || data.getLp() == null || data.getCh().length == 0) {
                log.warn("LP size is 0 then skip");
                continue;
            }
            
            for (int ch = 0; ch < lpValues.length; ch++) {
                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
                    lpValues[ch][lpcnt] = lplist[lpcnt].getCh()[ch];
                }
            }

            for (int i = 0; i < flaglist.length; i++) {
                flaglist[i] = lplist[i].getFlag();
            }

        }
        saveLPData(MeteringType.Normal, yyyymmdd, 
        	hhmm,
        	lpValues, flaglist, basePulse,
            parser.getMeter(),  parser.getDeviceType(), parser.getDeviceId(),
            parser.getMDevType(), parser.getMDevId());
        
        
        Modem modem = parser.getMeter().getModem();
        
        if(parser.getEdgeRouterId() != null){
        	Modem edgemodem = modemDao.get(parser.getEdgeRouterId());
        	if(edgemodem == null){
        		SubGiga edgeRouter = new SubGiga();
        		edgeRouter.setDeviceSerial(parser.getEdgeRouterId() );
        		edgeRouter.setCommState(1);
        		edgeRouter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(null));
        		//edgeRouter.setIpv6Address(parser.getIp6addr());
        		edgeRouter.setProtocolType(Protocol.UDP.name());
        		edgeRouter.setModemType(ModemType.SubGiga.name());
        		edgeRouter.setModem(modem);
        		edgeRouter.setSupplier(parser.getMeter().getSupplier());
        		edgeRouter.setMcu(modem.getMcu());
        		
        		List<DeviceModel> model = deviceModelDao.getDeviceModelByName(parser.getMeter().getSupplier().getId(), "edgeRouter");
        		
        		if(modem != null && model.size() > 0){
            		edgeRouter.setModel(model.get(0));
        		}

        		modemDao.add(edgeRouter);
        	}
        }
        
        return true;
    }

}
