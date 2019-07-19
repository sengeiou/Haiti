package com.aimir.fep.meter.saver;

import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.ActarisSCE8711;
import com.aimir.fep.util.FMPProperty;
import com.aimir.util.DateTimeUtil;

@Service
public class ActarisSCE8711MDSaver extends AbstractMDSaver {

    @SuppressWarnings("unchecked")
	@Override
	protected boolean save(IMeasurementData md) throws Exception {
        ActarisSCE8711 parser = (ActarisSCE8711)md.getMeterDataParser();
        LPData[] lplist = parser.getLPData();
        
        long diffTime = 0;
        String meterTime = md.getTimeStamp();
        long systemTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
        long limitTime = Long.parseLong(FMPProperty.getProperty("metertime.diff.limit.forcertain")) * 1000;
        boolean isCorrectTime = true;
        
        if(parser.getMeterTime() != null){
            diffTime = systemTime - DateTimeUtil.getDateFromYYYYMMDDHHMMSS((parser).getMeterTime()).getTime();
            if (diffTime < 0) {
                diffTime *= -1;
            }
            if (limitTime < diffTime) {
                isCorrectTime = false;
            }
        }
        
        if(lplist == null){
            log.debug("LPSIZE => 0");
        }
        else
        {
            log.debug("LPSIZE => "+lplist.length);
            String yyyymmdd = lplist[0].getDatetime().substring(0,8);
            String hhmm = lplist[0].getDatetime().substring(8,12);
            String mdevId=parser.getMDevId();
            int hh=new Integer(lplist[0].getDatetime().substring(8,10));
            int mm=new Integer(lplist[0].getDatetime().substring(10,12));
            int interval = parser.getResolution() != 0? parser.getResolution():15;
            if (interval != parser.getMeter().getLpInterval())
                parser.getMeter().setLpInterval(interval);
            
			double basePulse = lplist[0].getBasePulse();				        	        
	                    
            log.info("mdevId["+mdevId+"] yyyymmdd["+yyyymmdd+"] hh["+hh+"] mm["+mm+"] basePulse["+basePulse+"]");
            double[][] lpValues = new double[lplist[0].getCh().length][lplist.length];
            int[] flaglist = new int[lplist.length];
            double[] pflist = new double[lplist.length];
            
            for (int ch = 0; ch < lpValues.length; ch++) {
                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
                    lpValues[ch][lpcnt] = lplist[lpcnt].getCh()[ch];
                }
            }
            
            for (int i = 0; i < flaglist.length; i++) {
                flaglist[i] = lplist[i].getFlag();
                pflist[i] = lplist[i].getPF();
            }
            
            // TODO Flag, PF 처리해야 함.
            saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist, basePulse,
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(), 
                    parser.getMDevType(), parser.getMDevId());
        }
        
        
        
        if( parser.getMeteringValue()!= null){
            saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                    md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        }

        
        // 순시값 (Voltage,Current) 데이터 저장
        Instrument[] instrument = parser.getInstrument();
        if(instrument != null){
            savePowerQuality(parser.getMeter(), parser.getMeterTime(), instrument,
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        
        /*
        // 현재 시점 검침 사용량 및 Demand Power
        TOU_BLOCK[] tou_curr = parser.getCurrBilling(); 
        if(tou_curr != null){ 
        	saveCurrentBilling(tou_curr, parser.getMeter(), 
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        
        
        // 월별 TOU DATA저장
        TOU_BLOCK[] tou_month = parser.getPrevBilling();
        if(tou_month != null){ 
            saveMonthlyBilling(tou_month, parser.getMeter(), 
            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        }
        */
        
        /*
        // 셀프 리드 테이블 저장 (일별 정기 검침 누적 사용량)
        ArrayList<TOU_BLOCK[]> list = parser.getSelfReads();
        for(int i = 0; list!= null && i < list.size(); i++) {
        	TOU_BLOCK[] tou_day = list.get(i);
            if(tou_day != null){ 
                saveDayBilling(tou_day, parser.getMeter(), 
                		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
            }
        } 
        */                               
        
        //Meter Event Log저장
        EventLogData[] meterEventLog = parser.getEventLog();
        if(meterEventLog != null){
        	saveMeterEventLog(parser.getMeter(), meterEventLog);
        }

        /*
        //Meter Status(Warning, Error)정보를 이벤트 화 하R여 미터 이벤트 로그로 저장
        EventLogData[] meterStatusLog = parser.getMeterStatusLog();
        if( meterStatusLog != null){
        	saveMeterEventLog(parser.getMeter(),  meterStatusLog);
        }
        */
        //mcu id 필드에 올라오는 아이디로 임시로 모뎀을 생성한 후 파싱을 마치고 나면 실제 전화 번호를 모뎀에 셋팅해줌
        if(parser !=null && parser.getMeter()!=null && parser.getMeter().getModem()!=null && parser.getMdmData() != null) {
//        	parser.getMeter().getModem().setDeviceSerial(parser.getMdmData().get("sysPhoneNumber"));
        }
        return true;
    }

}
