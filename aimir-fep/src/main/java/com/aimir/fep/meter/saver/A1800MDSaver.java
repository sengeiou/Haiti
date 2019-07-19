package com.aimir.fep.meter.saver;

import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.data.PowerQualityMonitor;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.A1800;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.mvm.PowerQuality;
import com.aimir.util.DateTimeUtil;

@Service
public class A1800MDSaver extends AbstractMDSaver {
	
	private static boolean cbBool = false;
	private static boolean pbBool = false; 
	private static boolean isBool = false;
	private static boolean lpBool = false;
	private static boolean evBool = false;
	private static boolean pqBool = false; 
	
	@Override
	protected boolean save(IMeasurementData md) throws Exception {


    	A1800 parser = (A1800)md.getMeterDataParser();  
    	
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
            parser.getMeter().setTimeDiff(diffTime);
        }
        
        //Meter Status(Warning, Error)정보를 이벤트 화 하여 미터 이벤트 로그로 저장
        String meterStatusLog = parser.getMeterLog();
        if( meterStatusLog != null && !"".equals(meterStatusLog)){
        	parser.getMeter().setMeterCaution(meterStatusLog);
        }
        
        // 미터링 데이터 저장
        if (parser.getMeteringValue() != null) {
            saveMeteringData(MeteringType.Normal, md.getTimeStamp().substring(0,8),
                            md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                            parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                            parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        }

        if(isBool == false){  
	        Instrument[] instrument = parser.getInstrument();
	        if (instrument != null && instrument.length > 0) {
	            savePowerQuality(parser.getMeter(), instrument, parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
	//            parser.setInstrument(instrument =null);
	            isBool=true;
	        }	        
        }
        
        // Meter Event Log 저장
        if(evBool == false){
	        EventLogData[] meterEventLog = parser.getMeterEventLog();
	
	        if (meterEventLog != null) {
	          	saveMeterEventLog(parser.getMeter(), meterEventLog);
	          	evBool=true;
	        }
        }
        
        // 현재 시점 검침 사용량 및 Demand Power
        if(cbBool == false){
	        BillingData cb = parser.getCurrentBillingData();
	        if(cb != null){
	        	saveCurrentBilling(cb, parser.getMeter(),
	            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
	        	
	        	//day billing data
	        	TOU_BLOCK[] touBlk = null;
	        	saveDayBilling( touBlk, 
	        			parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),  parser.getMDevType(), parser.getMDevId());
	        	cbBool = true;
	        }
        }

        // Previous Billing ,월별 TOU DATA저장
        if(pbBool == false){
	        BillingData pb = parser.getPreviousBillingData();
	        if(pb != null){
	            saveMonthlyBilling(pb, parser.getMeter(),
	            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
	            pbBool = true;
	        }
        }
        
        if(lpBool==false){
	        LPData[] lplist = parser.getLpData();	
	        
	        if(lplist == null){
	            log.debug("LPSIZE => 0");
	        }
	        else
	        {
	            String yyyymmdd = lplist[0].getDatetime().substring(0,8);
	
	            String hhmm = lplist[0].getDatetime().substring(8,12);
	            double basePulse = 0;//parser.getLpValue();	//구현안되어있음
	            
	            
	            double[][] lpValues = new double[lplist[0].getCh().length][lplist.length];
	            int[] flaglist = new int[lplist.length];
	            
	            for (int ch = 0; ch < lpValues.length; ch++) {
	                for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
	                    lpValues[ch][lpcnt] = lplist[lpcnt].getCh()[ch];
	                }
	            }
	            
	            for (int i = 0; i < flaglist.length; i++) {
	                flaglist[i] = lplist[i].getFlag();
	            }
	            // TODO Flag, PF 처리해야 함.
	            saveLPData(MeteringType.Normal, yyyymmdd, hhmm, lpValues, flaglist, basePulse,
	                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
	                    parser.getMDevType(), parser.getMDevId());
	            
	            lpBool=true;
	        }
        }

        if(pqBool == false){
	        PowerQualityMonitor pqm = parser.getPowerQualityData();
	        if(pqm != null){
	            savePowerQualityStatus(parser.getMeter(), parser.getMeterTime(), pqm, 
	            		parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
	            pqBool = true;
	        }
        }
        
        // Power Event Log 저장
    	Vector<PowerAlarmLogData> powerAlarmLogData = parser.getPowerAlarmLog();
    	if(powerAlarmLogData.size() > 0){
			savePowerAlarmLog(parser.getMeter(),powerAlarmLogData.toArray(new PowerAlarmLogData[0]));
		}
                
		return true;
	}

	
	protected void savePowerQuality(Meter meter, Instrument[] instrument,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        PowerQuality pw = null;
//        for (Instrument ins : instrument) {
//        	System.out.println(ins.getDatetime().substring(0, 12));
//        }
		if(instrument == null || instrument.length == 0)
		{
			return;
		}


        for (Instrument ins : instrument) {

			String _time = DateTimeUtil.getDST(null, ins.getDatetime());
            int dst = DateTimeUtil.inDST(null, _time);
            pw = new PowerQuality();
            BeanUtils.copyProperties(pw, ins);

            pw.setDeviceId(deviceId);
            pw.setDeviceType(deviceType);
            pw.setDst(dst);
            pw.setLine_frequency(ins.getLINE_FREQUENCY());
            pw.setMDevId(mdevId);
            pw.setMDevType(mdevType.name());
            pw.setYyyymmdd(ins.getDatetime().substring(0, 8));
            pw.setHhmm(ins.getDatetime().substring(8,12));
            pw.setYyyymmddhhmm(ins.getDatetime().substring(0, 12));
            pw.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

            switch (mdevType) {
            case Meter :
                pw.setMeter(meter);
                pw.setSupplier(meter.getSupplier());
                break;
            case Modem :
                Modem modem = modemDao.get(mdevId);
                pw.setModem(modem);
                if(modem!=null && modem.getSupplier() != null){
                    pw.setSupplier(modem.getSupplier());
                }
                break;
            case EndDevice :
                // pw.setEnvdevice(enddevice);
            }
            
            if (meter != null && meter.getContract() != null)
                pw.setContract(meter.getContract());

            pw.setCurr_1st_harmonic_mag_a(ins.getCURR_1ST_HARMONIC_MAG_A());
            pw.setCurr_1st_harmonic_mag_b(ins.getCURR_1ST_HARMONIC_MAG_B());
            pw.setCurr_1st_harmonic_mag_c(ins.getCURR_1ST_HARMONIC_MAG_C());
            pw.setCurr_2nd_harmonic_mag_a(ins.getCURR_2ND_HARMONIC_MAG_A());
            pw.setCurr_2nd_harmonic_mag_b(ins.getCURR_2ND_HARMONIC_MAG_B());
            pw.setCurr_2nd_harmonic_mag_c(ins.getCURR_2ND_HARMONIC_MAG_C());
            pw.setCurr_a(ins.getCURR_A());
            pw.setCurr_angle_a(ins.getCURR_ANGLE_A());
            pw.setCurr_angle_b(ins.getCURR_ANGLE_B());
            pw.setCurr_angle_c(ins.getCURR_ANGLE_C());
            pw.setCurr_b(ins.getCURR_B());
            pw.setCurr_c(ins.getCURR_C());
            pw.setCurr_harmonic_a(ins.getCURR_HARMONIC_A());
            pw.setCurr_harmonic_b(ins.getCURR_HARMONIC_B());
            pw.setCurr_harmonic_c(ins.getCURR_HARMONIC_C());
            pw.setCurr_seq_n(ins.getCURR_SEQ_N());
            pw.setCurr_seq_p(ins.getCURR_SEQ_P());
            pw.setCurr_seq_z(ins.getCURR_SEQ_Z());
            pw.setCurr_thd_a(ins.getCURR_THD_A());
            pw.setCurr_thd_b(ins.getCURR_THD_B());
            pw.setCurr_thd_c(ins.getCURR_THD_C());
            pw.setDistortion_kva_a(ins.getDISTORTION_KVA_A());
            pw.setDistortion_kva_b(ins.getDISTORTION_KVA_B());
            pw.setDistortion_kva_c(ins.getDISTORTION_KVA_C());
            pw.setDistortion_pf_a(ins.getDISTORTION_PF_A());
            pw.setDistortion_pf_b(ins.getDISTORTION_PF_B());
            pw.setDistortion_pf_c(ins.getDISTORTION_PF_C());
            pw.setDistortion_pf_total(ins.getDISTORTION_PF_TOTAL());
            pw.setKva_a(ins.getKVA_A());
            pw.setKva_b(ins.getKVA_B());
            pw.setKva_c(ins.getKVA_C());
            pw.setKvar_a(ins.getKVAR_A());
            pw.setKvar_b(ins.getKVAR_B());
            pw.setKvar_c(ins.getKVAR_C());
            pw.setKw_a(ins.getKW_A());
            pw.setKw_b(ins.getKW_B());
            pw.setKw_c(ins.getKW_C());
            pw.setPf_a(ins.getPF_A());
            pw.setPf_b(ins.getPF_B());
            pw.setPf_c(ins.getPF_C());
            pw.setPf_total(ins.getPF_TOTAL());
            pw.setPh_curr_pqm_a(ins.getPH_CURR_PQM_A());
            pw.setPh_curr_pqm_b(ins.getPH_CURR_PQM_B());
            pw.setPh_curr_pqm_c(ins.getPH_CURR_PQM_C());
            pw.setPh_fund_curr_a(ins.getPH_FUND_CURR_A());
            pw.setPh_fund_curr_b(ins.getPH_FUND_CURR_B());
            pw.setPh_fund_curr_c(ins.getPH_CURR_PQM_C());
            pw.setPh_fund_vol_a(ins.getPH_FUND_VOL_A());
            pw.setPh_fund_vol_b(ins.getPH_FUND_VOL_B());
            pw.setPh_fund_vol_c(ins.getPH_FUND_VOL_C());
            pw.setPh_vol_pqm_a(ins.getPH_VOL_PQM_A());
            pw.setPh_vol_pqm_b(ins.getPH_VOL_PQM_B());
            pw.setPh_vol_pqm_c(ins.getPH_VOL_PQM_C());
            pw.setSystem_pf_angle(ins.getSYSTEM_PF_ANGLE());
            pw.setTdd_a(ins.getTDD_A());
            pw.setTdd_b(ins.getTDD_B());
            pw.setTdd_c(ins.getTDD_C());
            pw.setVol_1st_harmonic_mag_a(ins.getVOL_1ST_HARMONIC_MAG_A());
            pw.setVol_1st_harmonic_mag_b(ins.getVOL_1ST_HARMONIC_MAG_B());
            pw.setVol_1st_harmonic_mag_c(ins.getVOL_1ST_HARMONIC_MAG_C());
            pw.setVol_2nd_harmonic_a(ins.getVOL_2ND_HARMONIC_A());
            pw.setVol_2nd_harmonic_b(ins.getVOL_2ND_HARMONIC_B());
            pw.setVol_2nd_harmonic_c(ins.getVOL_2ND_HARMONIC_C());
            pw.setVol_2nd_harmonic_mag_a(ins.getVOL_2ND_HARMONIC_MAG_A());
            pw.setVol_2nd_harmonic_mag_b(ins.getVOL_2ND_HARMONIC_MAG_B());
            pw.setVol_2nd_harmonic_mag_c(ins.getVOL_2ND_HARMONIC_MAG_C());
            pw.setVol_a(ins.getVOL_A());
            pw.setVol_b(ins.getVOL_B());
            pw.setVol_c(ins.getVOL_C());
            pw.setVol_angle_a(ins.getVOL_ANGLE_A());
            pw.setVol_angle_b(ins.getVOL_ANGLE_B());
            pw.setVol_angle_c(ins.getVOL_ANGLE_C());
            pw.setVol_seq_n(ins.getVOL_SEQ_N());
            pw.setVol_seq_p(ins.getVOL_SEQ_P());
            pw.setVol_seq_z(ins.getVOL_SEQ_Z());
            pw.setVol_thd_a(ins.getVOL_THD_A());
            pw.setVol_thd_b(ins.getVOL_THD_B());
            pw.setVol_thd_c(ins.getVOL_THD_C());

            powerQualityDao.saveOrUpdate(pw);
        }
    }
}
