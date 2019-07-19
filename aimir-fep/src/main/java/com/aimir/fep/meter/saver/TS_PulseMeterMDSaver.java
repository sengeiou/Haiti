package com.aimir.fep.meter.saver;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.ModemLPData;
import com.aimir.fep.meter.parser.TS_PulseMeter;
import com.aimir.fep.util.DataUtil;

@Service
public class TS_PulseMeterMDSaver extends AbstractMDSaver {
	
    @Override
    protected boolean save(IMeasurementData md) throws Exception 
    {
        TS_PulseMeter parser = (TS_PulseMeter)md.getMeterDataParser();
        
        // period를 가져와서 주기를 검사한다.
        int interval = 60 / (parser.getPeriod() != 0? parser.getPeriod():1);
        if (interval != parser.getMeter().getLpInterval())
            parser.getMeter().setLpInterval(interval);
        
        String meteringTime = parser.getMeteringTime();
        // byte[] year = DataUtil.get2ByteToInt(meteringTime.substring(0, meteringTime.length() - 10));
        // DataUtil.convertEndian(year);
        // meteringTime = DataUtil.getIntTo2Byte(year) + meteringTime.substring(meteringTime.length() - 10);
        
        // 올라온 검침데이타의 검침값을 저장한다.
        saveMeteringData(MeteringType.Manual, meteringTime.substring(0, 8),
                meteringTime.substring(8), parser.getMeteringValue(),
                parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                parser.getMDevType(), parser.getMDevId(), null);
        
        // LP 데이타 저장
        int[] flaglist = null;
        ModemLPData lpdata = null;
        
        for (int i = 0; i < parser.getLpData().length; i++) {
            lpdata = parser.getLpData()[i];
            
            if (lpdata == null || lpdata.getLp() == null)
                continue;
            
            flaglist = new int[lpdata.getLp()[0].length];
            
            for (int j = 0; j < flaglist.length; j++) {
                if (lpdata.getLp()[0][j] == -1) {
                    flaglist[j] = MeteringFlag.Missing.getFlag();
                    lpdata.getLp()[0][j] = 0;
                }
                else
                    flaglist[j] = MeteringFlag.Correct.getFlag();
            }
            
            saveLPData(MeteringType.Manual, lpdata.getLpDate(), "0000",
                    lpdata.getLp(), flaglist, lpdata.getBasePulse(), parser.getMeter(),
                    parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }

        // 설정 저장
        com.aimir.model.device.ZEUPLS zeupls = (com.aimir.model.device.ZEUPLS)parser.getMeter().getModem();
        zeupls.setBatteryVolt(parser.getBatteryVolt());
        zeupls.setLpPeriod(parser.getLpPeriod());
        
        saveBatteryLog(parser.getMeter().getModem(), parser.getBatteryLog());
        
        return true;
    }

}
